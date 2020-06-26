package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class AttributesController {
    
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    
    private static final String redirectLink = "redirect:/admin/config/attributes";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    private static final Logger log = YukonLogManager.getLogger(AttributesController.class);

    @GetMapping("/config/attributes")
    public String attributes(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        retrieveCustomAttributes(model, userContext, request);
        return "config/attributes.jsp";
    }
    
    @PostMapping("/config/attribute/save")
    @ResponseBody
    public Map<String, Object> saveAttribute(ModelMap model, Integer id, String name, YukonUserContext userContext, 
                                  HttpServletRequest request, FlashScope flash) {
        Map<String, Object> jsonResponse = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        boolean validationErrors = checkForValidationErrors(name, jsonResponse, accessor);
        if (validationErrors) {
            return jsonResponse;
        }

        try {
            ResponseEntity<? extends Object> response = null;
            if (id != null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + id);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, DeviceBaseModel.class, name);
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + "create");
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, name);
            }

/*            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(virtualDevice, "virtualDevice");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    return "virtualDeviceInfoWidget/render.jsp";
                }
            }*/

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", name));
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
        } catch (RestClientException e) {
            log.error("Error saving custom attribute: {}. Error: {}", name, e.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", name, e.getMessage()));
        }
        return jsonResponse;
    }
    
    private boolean checkForValidationErrors(String name, Map<String, Object> jsonResponse, MessageSourceAccessor accessor) {
        if (StringUtils.isBlank(name)) {
            String nameI18nText = accessor.getMessage("yukon.common.name");
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.fieldrequired", nameI18nText));
            return true;
        } else if (name.length() > 60) {
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.exceedsMaximumLength", 60));
            return true;
        } else if (StringUtils.containsAny(name, PaoUtils.ILLEGAL_NAME_CHARS)) {
            jsonResponse.put("errorMessage", accessor.getMessage("yukon.web.error.paoName.containsIllegalChars"));
            return true;
        }
        
        //TODO: Add check for attributes assigned to same point??

        return false;
    }
    
    @DeleteMapping("/config/attribute/{id}/delete")
    public String deleteAttribute(ModelMap model, @PathVariable int id, String name, HttpServletRequest request, 
                                  YukonUserContext userContext, FlashScope flash) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + id);
            ResponseEntity<? extends Object> deleteResponse = 
                    apiRequestHelper.callAPIForObject(userContext, request, deleteUrl, HttpMethod.DELETE, Object.class, Integer.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", name));
                return redirectLink;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting custom attribute: {}. Error: {}", name, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", name, ex.getMessage()));
            return redirectLink;
        }
        return redirectLink;
    }
    
    private void retrieveCustomAttributes(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        
/*        String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl);
        //Model object may change once REST API is ready
        //TODO: Call list REST API
        List<DeviceBaseModel> attributeList = new ArrayList<>();

        ResponseEntity<? extends Object> response = 
                apiRequestHelper.callAPIForList(userContext, request, url, DeviceBaseModel.class, HttpMethod.GET, DeviceBaseModel.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            attributeList = (List<DeviceBaseModel>) response.getBody();
        }
        model.addAttribute("attributes", attributeList);*/
        
        //mock up data for now
        List<DeviceBaseModel> atts = new ArrayList<>();
        atts.add(new DeviceBaseModel(123, null, "CustomAttribute-001", true));
        atts.add(new DeviceBaseModel(124, null, "CustomAttribute-002", true));
        atts.add(new DeviceBaseModel(125, null, "CustomAttribute-003", true));
        atts.add(new DeviceBaseModel(126, null, "CustomAttribute-004", true));
        atts.add(new DeviceBaseModel(127, null, "CustomAttribute-005", true));
        atts.add(new DeviceBaseModel(128, null, "CustomAttribute-006", true));
        atts.add(new DeviceBaseModel(129, null, "CustomAttribute-007", true));
        atts.add(new DeviceBaseModel(130, null, "CustomAttribute-008", true));
        atts.add(new DeviceBaseModel(131, null, "CustomAttribute-009", true));
        atts.add(new DeviceBaseModel(132, null, "CustomAttribute-010", true));
        atts.add(new DeviceBaseModel(133, null, "CustomAttribute-011", true));
        atts.add(new DeviceBaseModel(134, null, "CustomAttribute-012", true));
        atts.add(new DeviceBaseModel(135, null, "CustomAttribute-013", true));
        atts.add(new DeviceBaseModel(136, null, "CustomAttribute-014", true));
        atts.add(new DeviceBaseModel(137, null, "CustomAttribute-015", true));
        atts.add(new DeviceBaseModel(138, null, "CustomAttribute-016", true));
        atts.add(new DeviceBaseModel(139, null, "CustomAttribute-017", true));
        atts.add(new DeviceBaseModel(140, null, "CustomAttribute-018", true));
        atts.add(new DeviceBaseModel(141, null, "CustomAttribute-019", true));
        atts.add(new DeviceBaseModel(142, null, "CustomAttribute-020", true));

        model.addAttribute("attributes", atts);
    }
    
}