package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
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
    
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private AttributeValidator attributeValidator;
    
    private static final String redirectLink = "redirect:/admin/config/attributes";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    
    private static final Logger log = YukonLogManager.getLogger(AttributesController.class);

    @GetMapping("/config/attributes")
    public String attributes(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        retrieveCustomAttributes(model, userContext, request);
        CustomAttribute createAttribute = new CustomAttribute();
        if (model.containsKey("createAttribute")) {
            createAttribute = (CustomAttribute) model.get("createAttribute");
        }
        model.addAttribute("createAttribute", createAttribute);
        CustomAttribute editAttribute = new CustomAttribute();
        if (model.containsKey("editAttribute")) {
            editAttribute = (CustomAttribute) model.get("editAttribute");
        }
        model.addAttribute("editAttribute", editAttribute);
        return "config/attributes.jsp";
    }
    
    @PostMapping("/config/attribute/create")
    public String createttribute(@ModelAttribute("createAttribute") CustomAttribute attribute, BindingResult result,
                                             ModelMap model, YukonUserContext userContext, HttpServletRequest request, 
                                             HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {
        return saveAttribute(attribute, result, model, userContext, request, resp, flash, redirectAttributes, false);
    }
    
    @PostMapping("/config/attribute/edit")
    public String editAttribute(@ModelAttribute("editAttribute") CustomAttribute attribute, BindingResult result,
                                             ModelMap model, YukonUserContext userContext, HttpServletRequest request, 
                                             HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes) {
        return saveAttribute(attribute, result, model, userContext, request, resp, flash, redirectAttributes, true);
    }
    
    private String saveAttribute(CustomAttribute attribute, BindingResult result, ModelMap model, YukonUserContext userContext, HttpServletRequest request, 
                                 HttpServletResponse resp, FlashScope flash, RedirectAttributes redirectAttributes, Boolean isEditMode) {
        attributeValidator.validate(attribute, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupErrorModel(redirectAttributes, attribute, result, isEditMode);
            return redirectLink;
        }

        try {
            ResponseEntity<? extends Object> response = null;
            if (attribute.getId() != null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + attribute.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, CustomAttribute.class, attribute.getName());
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + "create");
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, CustomAttribute.class, attribute.getName());
            }

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(attribute, "editAttribute");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    setupErrorModel(redirectAttributes, attribute, result, isEditMode);
                    return redirectLink;
                }
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", attribute.getName()));
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
        } catch (RestClientException e) {
            log.error("Error saving custom attribute: {}. Error: {}", attribute.getName(), e.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", attribute.getName(), e.getMessage()));
        }
        return redirectLink;
    }
    
    private void setupErrorModel(RedirectAttributes redirectAttributes, CustomAttribute attribute, BindingResult result, Boolean isEditMode) {
        if (isEditMode) {
            redirectAttributes.addFlashAttribute("enableEditId", attribute.getId());
            redirectAttributes.addFlashAttribute("editAttribute", attribute);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editAttribute", result);
        } else {
            redirectAttributes.addFlashAttribute("createAttribute", attribute);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.createAttribute", result);
        }
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
        
/*      String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl);
        List<CustomAttribute> attributeList = new ArrayList<>();

        ResponseEntity<? extends Object> response = 
                apiRequestHelper.callAPIForList(userContext, request, url, CustomAttribute.class, HttpMethod.GET, CustomAttribute.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            attributeList = (List<CustomAttribute>) response.getBody();
        }
        model.addAttribute("attributes", attributeList);*/
        
        //mock up data for now
        List<CustomAttribute> atts = new ArrayList<>();
        CustomAttribute att1 = new CustomAttribute();
        att1.setId(121);
        att1.setName("CustomAttribute-001");
        atts.add(att1);
        CustomAttribute att2 = new CustomAttribute();
        att2.setId(122);
        att2.setName("CustomAttribute-002");
        atts.add(att2);
        CustomAttribute att3 = new CustomAttribute();
        att3.setId(123);
        att3.setName("CustomAttribute-003");
        atts.add(att3);
        
/*        att1 = new CustomAttribute();
        att1.setId(121);
        att1.setName("CustomAttribute-001");
        atts.add(att1);
        att2 = new CustomAttribute();
        att2.setId(122);
        att2.setName("CustomAttribute-002");
        atts.add(att2);
        att3 = new CustomAttribute();
        att3.setId(123);
        att3.setName("CustomAttribute-003");
        atts.add(att3);
        
        att1 = new CustomAttribute();
        att1.setId(121);
        att1.setName("CustomAttribute-001");
        atts.add(att1);
        att2 = new CustomAttribute();
        att2.setId(122);
        att2.setName("CustomAttribute-002");
        atts.add(att2);
        att3 = new CustomAttribute();
        att3.setId(123);
        att3.setName("CustomAttribute-003");
        atts.add(att3);
        
        att1 = new CustomAttribute();
        att1.setId(121);
        att1.setName("CustomAttribute-001");
        atts.add(att1);
        att2 = new CustomAttribute();
        att2.setId(122);
        att2.setName("CustomAttribute-002");
        atts.add(att2);
        att3 = new CustomAttribute();
        att3.setId(123);
        att3.setName("CustomAttribute-003");
        atts.add(att3);*/
        model.addAttribute("attributes", atts);
    }
    
}