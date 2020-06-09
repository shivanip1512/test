package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.device.port.LocalSharedPortDetail;
import com.cannontech.common.device.port.PhysicalPort;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;
import com.cannontech.common.device.port.UdpPortDetail;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.stars.commChannel.CommChannelValidator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/commChannelInfoWidget")
public class CommChannelInfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CommChannelValidator<? extends PortBase<?>> commChannelValidator;
    private static final Logger log = YukonLogManager.getLogger(CommChannelInfoWidget.class);
    private static final String baseKey = "yukon.web.modules.operator.commChannelInfoWidget.";

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
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        retrieveCommChannel(userContext, request, id, model);
        return "commChannelInfoWidget/render.jsp";
    }

    private void retrieveCommChannel(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelViewUrl + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, PortBase.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                PortBase commChannel = (PortBase) response.getBody();
                commChannel.setId(id);
                model.addAttribute("commChannel", commChannel);
                setupCommChannelFields(commChannel, model);
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.apiCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        } catch (RestClientException ex) {
            log.error("Error retrieving comm Channel: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.web.modules.operator.commChannelInfoWidget.retrieveError",
                    ex.getMessage());
            model.addAttribute("errorMsg", errorMsg);
        }
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("commChannel") PortBase commChannel, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {

        try {
            commChannelValidator.validate(commChannel, result);
            if (result.hasErrors()) {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                setupCommChannelFields(commChannel, model);
                setupGlobalError(result, model, userContext, commChannel.getType());
                return "commChannelInfoWidget/render.jsp";
            }
            String url = helper.findWebServerUrl(request, userContext, ApiURL.commChannelUpdateUrl + commChannel.getId());
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.POST, Object.class, commChannel);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(commChannel, "commChannel");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    setupCommChannelFields(commChannel, model);
                    setupGlobalError(result, model, userContext, commChannel.getType());
                    return "commChannelInfoWidget/render.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                model.clear();
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.web.save.success", commChannel.getName()));
                return null;
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            return "commChannelInfoWidget/render.jsp";
        } catch (RestClientException e) {
            log.error("Error updating comm Channel: {}. Error: {}", commChannel.getName(), e.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.save.error", commChannel.getName(), e.getMessage()));
            return "commChannelInfoWidget/render.jsp";
        }
        return null;
    }

    private void setupCommChannelFields(PortBase commChannel, ModelMap model) {
        model.addAttribute("baudRateList", BaudRate.values());
        if (commChannel instanceof TerminalServerPortDetailBase) {
            model.addAttribute("isAdditionalConfigSupported", true);
            model.addAttribute("isPortNumberSupported", true);
            if (commChannel instanceof UdpPortDetail) {
                model.addAttribute("isEncyptionSupported", true);
            }
            if (commChannel instanceof TcpSharedPortDetail) {
                model.addAttribute("isIpAddressSupported", true);
            }
        }
        if (commChannel instanceof LocalSharedPortDetail) {
            model.addAttribute("isAdditionalConfigSupported", true);
            model.addAttribute("isPhysicalPortSupported", true);
            List<String> physicalPortList = new ArrayList<>();
            for (PhysicalPort value : PhysicalPort.values()) {
                physicalPortList.add(value.getPhysicalPort());
            }
            model.addAttribute("physicalPortList", physicalPortList);
            model.addAttribute("otherPhysicalPort", PhysicalPort.OTHER.getPhysicalPort());
            if (PhysicalPort.getByDbString(((LocalSharedPortDetail) commChannel).getPhysicalPort()) == PhysicalPort.OTHER) {
                model.addAttribute("isPhysicalPortUserDefined", true);
            }
        }
    }

    private void setupGlobalError(BindingResult result, ModelMap model, YukonUserContext userContext, PaoType commChannelType) {
        if (result.hasGlobalErrors()) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            List<ObjectError> globalError = result.getGlobalErrors();
            List<String> uniqueErrorMsg = new ArrayList<>();
            for (ObjectError objectError : globalError) {
                uniqueErrorMsg.add(accessor.getMessage(objectError.getCode(), objectError.getArguments()));
            }
            if (PaoType.TSERVER_SHARED == commChannelType) {
                result.rejectValue("ipAddress", "yukon.common.blank");
            }
            result.rejectValue("portNumber", "yukon.common.blank");
            model.addAttribute("uniqueErrorMsg", uniqueErrorMsg);
        }
    }
}
