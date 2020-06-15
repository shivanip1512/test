package com.cannontech.web.widget;

import java.util.HashMap;
import java.util.Map;

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
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.validator.YukonValidationHelper;
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
        VirtualDeviceModel virtualDevice = new VirtualDeviceModel();
        virtualDevice.setEnable(true);
        virtualDevice.setType(PaoType.VIRTUAL_SYSTEM);
        model.addAttribute("virtualDevice", virtualDevice);
        return "virtualDeviceInfoWidget/render.jsp";
    }

    private void retrieveVirtualDevice(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, VirtualDeviceModel.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                VirtualDeviceModel virtualDevice = (VirtualDeviceModel) response.getBody();
                virtualDevice.setId(id);
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
    public String save(@ModelAttribute("virtualDevice") VirtualDeviceModel virtualDevice, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        try {
            String paoId = null;
            if (virtualDevice.getId() != null) {
                paoId = Integer.toString(virtualDevice.getId());
            }
            String nameLabel = accessor.getMessage("yukon.common.name");
            yukonValidationHelper.validatePaoName(virtualDevice.getName(), virtualDevice.getType(), result, nameLabel, paoId);
            if (result.hasErrors()) {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                return "virtualDeviceInfoWidget/render.jsp";
            }
            ResponseEntity<? extends Object> response = null;
            if (virtualDevice.getId() != null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + virtualDevice.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, VirtualDeviceModel.class, virtualDevice);
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, VirtualDeviceModel.class, virtualDevice);
            }

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
                Map<String, Object> json = new HashMap<>();
                if (virtualDevice.getId() == null) {
                    //device was created so we need id to redirect user to view page
                    virtualDevice = (VirtualDeviceModel) response.getBody();
                    json.put("id", virtualDevice.getId());
                    flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", virtualDevice.getName()));
                }
                json.put("userMessage", accessor.getMessage("yukon.common.save.success", virtualDevice.getName()));
                return JsonUtils.writeResponse(resp, json);
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            return "virtualDeviceInfoWidget/render.jsp";
        } catch (RestClientException e) {
            log.error("Error updating virtual device: {}. Error: {}", virtualDevice.getName(), e.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorMsg", accessor.getMessage("yukon.web.api.save.error", virtualDevice.getName(), e.getMessage()));
            return "virtualDeviceInfoWidget/render.jsp";
        }
        return null;
    }

}
