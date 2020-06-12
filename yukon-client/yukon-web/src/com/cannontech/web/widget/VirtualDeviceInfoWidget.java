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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.YukonValidationHelper;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
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
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;

@Controller
@RequestMapping("/virtualDeviceInfoWidget")
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class VirtualDeviceInfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private YukonValidationHelper yukonValidationHelper;
    private static final Logger log = YukonLogManager.getLogger(VirtualDeviceInfoWidget.class);
    private static final String baseKey = "yukon.web.modules.operator.virtualDeviceInfoWidget.";

    @Autowired
    public VirtualDeviceInfoWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @RequestMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            model.addAttribute("mode", PageEditMode.VIEW);
            retrieveVirtualDevice(userContext, request, deviceId, model);
        } catch (ServletRequestBindingException e) {
            log.error("Error rendering Virtual Device Information widget", e);
        }
        return "virtualDeviceInfoWidget/render.jsp";
    }

    @GetMapping("/{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        retrieveVirtualDevice(userContext, request, id, model);
        return "virtualDeviceInfoWidget/render.jsp";
    }
    
    @GetMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        model.addAttribute("virtualDevice", new LiteYukonPAObject(0, null, PaoType.VIRTUAL_SYSTEM, null, "N"));
        return "virtualDeviceInfoWidget/render.jsp";
    }

    private void retrieveVirtualDevice(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            //mock up data for now
            //LiteYukonPAObject virtualDevice = new LiteYukonPAObject(123, "VirtualDevice001", PaoType.VIRTUAL_SYSTEM, null, "Y");
            //model.addAttribute("virtualDevice", virtualDevice);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, LiteYukonPAObject.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                LiteYukonPAObject virtualDevice = (LiteYukonPAObject) response.getBody();
                //virtualDevice.setLiteID(id);
                model.addAttribute("virtualDevice", virtualDevice);
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.exception.apiCommunicationException.communicationError");
            model.addAttribute("errorMsg", errorMsg);
        } catch (RestClientException ex) {
            log.error("Error retrieving virtual device: " + ex.getMessage());
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String errorMsg = accessor.getMessage("yukon.web.modules.operator.virtualDeviceInfoWidget.retrieveError",
                    ex.getMessage());
            model.addAttribute("errorMsg", errorMsg);
        }
    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String save(@ModelAttribute("virtualDevice") LiteYukonPAObject virtualDevice, int deviceId, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {

        try {
            yukonValidationHelper.validatePaoName(virtualDevice.getPaoName(), virtualDevice.getPaoType(), result, "Name", Integer.toString(deviceId), "paoName");
            if (result.hasErrors()) {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                return "virtualDeviceInfoWidget/render.jsp";
            }
            //distinguish between create and update here to change url
            String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + deviceId);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.POST, Object.class, virtualDevice);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(virtualDevice, "virtualDevice");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    return "virtualDeviceInfoWidget/render.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                model.clear();
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", virtualDevice.getPaoName()));
                return null;
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            return "virtualDeviceInfoWidget/render.jsp";
        } catch (RestClientException e) {
            log.error("Error updating virtual device: {}. Error: {}", virtualDevice.getPaoName(), e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "saveError", virtualDevice.getPaoName(), e.getMessage()));
            return "virtualDeviceInfoWidget/render.jsp";
        }
        return null;
    }

}
