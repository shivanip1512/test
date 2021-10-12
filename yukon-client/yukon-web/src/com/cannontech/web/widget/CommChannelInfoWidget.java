package com.cannontech.web.widget;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.SharedPortType;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.stars.commChannel.CommChannelSetupHelper;
import com.cannontech.web.stars.commChannel.CommChannelValidator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/commChannelInfoWidget")
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class CommChannelInfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CommChannelValidator<? extends PortBase<?>> commChannelValidator;
    @Autowired private CommChannelSetupHelper commChanelSetupHelper;
    private static final Logger log = YukonLogManager.getLogger(CommChannelInfoWidget.class);

    @Autowired
    public CommChannelInfoWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            model.addAttribute("mode", PageEditMode.VIEW);
            retrieveCommChannel(userContext, request, deviceId, model);
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering Comm Channel Information widget", e);
        }
        return "commChannelInfoWidget/render.jsp";
    }

    @GetMapping("/{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        retrieveCommChannel(userContext, request, id, model);
        return "commChannelInfoWidget/render.jsp";
    }

    private void retrieveCommChannel(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, PortBase.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                PortBase commChannel = (PortBase) response.getBody();
                commChannel.setDeviceId(id);
                model.addAttribute("commChannel", commChannel);
                commChanelSetupHelper.setupCommChannelFields(commChannel, model);
                commChanelSetupHelper.setupPhysicalPort(commChannel, model);
                setupSharedType(commChannel, model);
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.apiCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        } catch (RestClientException ex) {
            log.error("Error retrieving comm Channel: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String commChannelDeviceLabel = accessor.getMessage("yukon.web.modules.operator.commChannel");
            String errorMsg = accessor.getMessage("yukon.web.api.retrieve.error", 
                    commChannelDeviceLabel, ex.getMessage());
            model.addAttribute("errorMsg", errorMsg);
        }
    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.CREATE)
    public String save(@ModelAttribute("commChannel") PortBase commChannel, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {

        try {
            commChannelValidator.validate(commChannel, result);
            if (result.hasErrors()) {
                setupErrorFields(resp, commChannel, model, result, userContext);
                return "commChannelInfoWidget/render.jsp";
            }
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUrl + "/" + commChannel.getDeviceId());
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.PATCH, Object.class, commChannel);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(commChannel, "commChannel");
                result = helper.populateBindingErrorForApiErrorModel(result, error, response, "yukon.web.error.");
                if (result.hasErrors()) {
                    setupErrorFields(resp, commChannel, model, result, userContext);
                    return "commChannelInfoWidget/render.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                model.clear();
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", commChannel.getDeviceName()));
                return null;
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            return "commChannelInfoWidget/render.jsp";
        } catch (RestClientException e) {
            log.error("Error updating comm Channel: {}. Error: {}", commChannel.getDeviceName(), e.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", commChannel.getDeviceName(), e.getMessage()));
            return "commChannelInfoWidget/render.jsp";
        }
        return null;
    }

    private void setupErrorFields(HttpServletResponse resp, PortBase commChannel, ModelMap model, BindingResult result,
            YukonUserContext userContext) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        commChanelSetupHelper.setupCommChannelFields(commChannel, model);
        commChanelSetupHelper.setupPhysicalPort(commChannel, model);
        commChanelSetupHelper.setupGlobalError(result, model, userContext, commChannel.getDeviceType());
        setupSharedType(commChannel, model);
    }
    
    private void setupSharedType(PortBase commChannel, ModelMap model) {
        if (commChannel instanceof TerminalServerPortDetailBase) {
            model.addAttribute("isSharedPortTypeNone",
                    ((TerminalServerPortDetailBase) commChannel).getSharing().getSharedPortType().equals(SharedPortType.NONE));
        }
        if (commChannel instanceof LocalSharedPortDetail) {
            model.addAttribute("isSharedPortTypeNone",
                    ((LocalSharedPortDetail) commChannel).getSharing().getSharedPortType().equals(SharedPortType.NONE));
        }
        model.addAttribute("sharedPortNone", SharedPortType.NONE);
    }
}
