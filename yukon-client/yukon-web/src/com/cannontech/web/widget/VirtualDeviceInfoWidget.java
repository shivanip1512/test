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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.virtualDevice.VirtualDeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
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
import com.cannontech.web.stars.virtualDevice.VirtualDeviceValidator;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.cannontech.web.widget.support.SimpleWidgetInput;
import com.cannontech.web.widget.support.WidgetParameterHelper;
import com.google.common.collect.ImmutableSet;

@Controller
@RequestMapping("/virtualDeviceInfoWidget")
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
public class VirtualDeviceInfoWidget extends AdvancedWidgetControllerBase {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private VirtualDeviceValidator virtualDeviceValidator;
    private static final Logger log = YukonLogManager.getLogger(VirtualDeviceInfoWidget.class);

    @Autowired
    public VirtualDeviceInfoWidget(@Qualifier("widgetInput.deviceId") SimpleWidgetInput simpleWidgetInput) {
        addInput(simpleWidgetInput);
        setIdentityPath("common/deviceIdentity.jsp");
    }

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        int deviceId = 0;
        try {
            deviceId = WidgetParameterHelper.getRequiredIntParameter(request, "deviceId");
            model.addAttribute("mode", PageEditMode.VIEW);
            retrieveVirtualDevice(userContext, request, deviceId, model);
            prepareModel(model);
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
        prepareModel(model);
        return "virtualDeviceInfoWidget/render.jsp";
    }
    
    @GetMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        VirtualDeviceModel virtualDevice = new VirtualDeviceModel();
        virtualDevice.setEnable(true);
        virtualDevice.setDeviceType(PaoType.VIRTUAL_SYSTEM);
        model.addAttribute("virtualDevice", virtualDevice);
        prepareModel(model);
        return "virtualDeviceInfoWidget/render.jsp";
    }
    
    @GetMapping("/create/{type}")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model, @PathVariable PaoType type, @RequestParam String name) {
        model.addAttribute("mode", PageEditMode.CREATE);
        VirtualDeviceBaseModel virtualDevice = (VirtualDeviceBaseModel) PaoModelFactory.getModel(type);
        virtualDevice.setDeviceName(name);
        virtualDevice.setEnable(true);
        virtualDevice.setDeviceType(type);
        model.addAttribute("virtualDevice", virtualDevice);
        prepareModel(model);
        return "virtualDeviceInfoWidget/render.jsp";
    }

    private void retrieveVirtualDevice(YukonUserContext userContext, HttpServletRequest request, int id, ModelMap model) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, VirtualDeviceBaseModel.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                VirtualDeviceBaseModel virtualDevice = (VirtualDeviceBaseModel) response.getBody();
                virtualDevice.setDeviceId(id);
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
            String virtualDeviceLabel = accessor.getMessage("yukon.web.modules.operator.virtualDevice.detail.pageName");
            String errorMsg = accessor.getMessage("yukon.web.api.retrieve.error", 
                                                  virtualDeviceLabel, ex.getMessage());
            model.addAttribute("errorMsg", errorMsg);
        }
    }
    
    private void prepareModel(ModelMap model) {
        model.addAttribute("virtualMeterType", PaoType.VIRTUAL_METER);
        model.addAttribute("types", ImmutableSet.of(PaoType.VIRTUAL_SYSTEM, PaoType.VIRTUAL_METER));
    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String save(@ModelAttribute("virtualDevice") VirtualDeviceBaseModel virtualDevice, BindingResult result, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request, ModelMap model, HttpServletResponse resp) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        prepareModel(model);
        try {
            virtualDeviceValidator.setMessageAccessor(accessor);
            virtualDeviceValidator.validate(virtualDevice, result);
            if (result.hasErrors()) {
                resp.setStatus(HttpStatus.BAD_REQUEST.value());
                return "virtualDeviceInfoWidget/render.jsp";
            }
            ResponseEntity<? extends Object> response = null;
            if (virtualDevice.getDeviceId() != null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl + "/" + virtualDevice.getDeviceId());
                model.addAttribute("mode", PageEditMode.EDIT);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, VirtualDeviceBaseModel.class, virtualDevice);
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.virtualDeviceUrl);
                model.addAttribute("mode", PageEditMode.CREATE);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, VirtualDeviceBaseModel.class, virtualDevice);
            }

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(virtualDevice, "virtualDevice");
                result = helper.populateBindingErrorForApiErrorModel(result, error, response, "yukon.web.error.");
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    return "virtualDeviceInfoWidget/render.jsp";
                }
            }

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                model.clear();
                Map<String, Object> json = new HashMap<>();
                if (virtualDevice.getDeviceId() == null) {
                    //device was created so we need to get id from response
                    VirtualDeviceBaseModel savedVirtualDevice = (VirtualDeviceBaseModel) response.getBody();
                    json.put("id", savedVirtualDevice.getDeviceId());
                } else {
                    json.put("id", virtualDevice.getDeviceId());
                }
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", virtualDevice.getDeviceName()));
                return JsonUtils.writeResponse(resp, json);
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            return "virtualDeviceInfoWidget/render.jsp";
        } catch (RestClientException e) {
            log.error("Error updating virtual device: {}. Error: {}", virtualDevice.getDeviceName(), e.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorMsg", accessor.getMessage("yukon.web.api.save.error", virtualDevice.getDeviceName(), e.getMessage()));
            return "virtualDeviceInfoWidget/render.jsp";
        }
        return null;
    }

}
