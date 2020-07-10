package com.cannontech.web.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.PointType;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckRoleProperty(YukonRoleProperty.ADMIN_SUPER_USER)
public class AttributesController {
    
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private AttributeValidator attributeValidator;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static final String redirectLink = "redirect:/admin/config/attributes";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.adminSetup.config.attributes.";
    
    private static final Logger log = YukonLogManager.getLogger(AttributesController.class);

    @GetMapping("/config/attributes")
    public String attributes(@DefaultSort(dir=Direction.desc, sort="attributeName") SortingParameters sorting, 
                             ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
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
        model.addAttribute("deviceTypes", getDeviceTypesThatSupportAssignment());
        retrieveAssignments(sorting, null, null, model, userContext, request);
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
    
    private List<PaoType> getDeviceTypesThatSupportAssignment() {
        List<PaoType> deviceTypes = dbCache.getAllPaoTypes().stream()
                .filter(type -> paoDefinitionDao.isTagSupported(type, PaoTag.SUPPORTS_ATTRIBUTE_ASSIGNMENT))
                .sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
                .collect(Collectors.toList());
        return deviceTypes;
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
    
    @GetMapping("/config/attributeAssignments/filter")
    public String filterAssignments(@DefaultSort(dir=Direction.desc, sort="attributeName") SortingParameters sorting, Integer[] selectedAttributes, 
                                    PaoType[] selectedDeviceTypes, ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        retrieveAssignments(sorting, selectedAttributes, selectedDeviceTypes, model, userContext, request);
        return "config/attributeAssignmentsTable.jsp";
    }
    
    @GetMapping("/config/attributeAssignments/popup")
    public String assignmentPopup(Integer id, ModelMap model, YukonUserContext userContext, HttpServletRequest request, 
                                  FlashScope flashScope) {
        AttributeAssignment assignment = new AttributeAssignment();
        if (id != null) {
            try {
                String retrieveUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + id);
                ResponseEntity<? extends Object> retrieveResponse = 
                        apiRequestHelper.callAPIForObject(userContext, request, retrieveUrl, HttpMethod.GET, AttributeAssignment.class, Integer.class);

                if (retrieveResponse.getStatusCode() == HttpStatus.OK) {
                    assignment = (AttributeAssignment) retrieveResponse.getBody();
                }
            } catch (ApiCommunicationException e) {
                log.error(e);
                flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
                return "config/attributeAssignmentPopup.jsp";
            } catch (RestClientException ex) {
                log.error("Error retrieving custom attribute assignment. Error: {}", ex.getMessage());
                MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
                flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", attributeAssignmentLabel, ex.getMessage()));
                return "config/attributeAssignmentPopup.jsp";
            }
        }
        
        model.addAttribute("assignment", assignment);
        retrievePopupModel(model, userContext, request);

        return "config/attributeAssignmentPopup.jsp";
    }
    
    private void retrievePopupModel(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        retrieveCustomAttributes(model, userContext, request);
        model.addAttribute("deviceTypes", getDeviceTypesThatSupportAssignment());
        model.addAttribute("pointTypes", PointType.values());
    }
    
    @PostMapping("/config/attributeAssignments/save")
    public String saveAssignment(@ModelAttribute("assignment") AttributeAssignment assignment, BindingResult result, ModelMap model, 
                                    YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp, FlashScope flashScope) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
        //validate
        
        //call REST API
        //call this multiple times if multiple device types are selected
        try {
            ResponseEntity<? extends Object> response = null;
            if (assignment.getId() != null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + assignment.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, AttributeAssignment.class, assignment);
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + "create");
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, AttributeAssignment.class, assignment);
            }
            
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(assignment, "assignment");
                result = helper.populateBindingError(result, error, response);
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    retrievePopupModel(model, userContext, request);
                    return "config/attributeAssignmentPopup.jsp";
                }
            }
            
            if (response.getStatusCode() == HttpStatus.OK) {
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", attributeAssignmentLabel));
            }
        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
            retrievePopupModel(model, userContext, request);
            return "config/attributeAssignmentPopup.jsp";
        } catch (RestClientException e) {
            log.error("Error saving custom attribute assignment: {}. Error: {}", attributeAssignmentLabel, e.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", attributeAssignmentLabel, e.getMessage()));
            retrievePopupModel(model, userContext, request);
            return "config/attributeAssignmentPopup.jsp";
        }
        
        return "config/attributeAssignmentPopup.jsp";
    }
    
    @DeleteMapping("/config/attributeAssignments/{id}/delete")
    public String deleteAssignment(ModelMap model, @PathVariable int id, String name, HttpServletRequest request, 
                                  YukonUserContext userContext, FlashScope flash) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + id);
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
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
            log.error("Error deleting assignment for custom attribute: {}. Error: {}", name, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", attributeAssignmentLabel, ex.getMessage()));
            return redirectLink;
        }
        return redirectLink;
    }
    
    private void retrieveAssignments(SortingParameters sorting, Integer[] selectedAttributes, PaoType[] selectedDeviceTypes, 
                                     ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        
/*      String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl);
        List<AttributeAssignment> assignmentList = new ArrayList<>();

        ResponseEntity<? extends Object> response = 
                apiRequestHelper.callAPIForList(userContext, request, url, AttributeAssignment.class, HttpMethod.GET, AttributeAssignment.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            assignmentList = (List<AttributeAssignment>) response.getBody();
        }
        model.addAttribute("assignments", assignmentList);*/
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        AssignmentSortBy sortBy = AssignmentSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        
        List<SortableColumn> columns = new ArrayList<>();
        for (AssignmentSortBy column : AssignmentSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        
        //mock up data for now
        CustomAttribute att1 = new CustomAttribute();
        att1.setId(121);
        att1.setName("CustomAttribute-001");
        List<AttributeAssignment> assignments = new ArrayList<>();
        AttributeAssignment a1 = new AttributeAssignment();
        a1.setAttribute(att1);
        a1.setDeviceType(PaoType.RFN410CL);
        a1.setId(111);
        a1.setPointType(PointType.CalcAnalog);
        a1.setPointOffset(122);
        assignments.add(a1);
        AttributeAssignment a2 = new AttributeAssignment();
        a2.setAttribute(att1);
        a2.setDeviceType(PaoType.RFN410FX);
        a2.setId(115);
        a2.setPointType(PointType.CalcStatus);
        a2.setPointOffset(123);
        assignments.add(a2);
        AttributeAssignment a3 = new AttributeAssignment();
        a3.setAttribute(att1);
        a3.setDeviceType(PaoType.RFN410CL);
        a3.setId(117);
        a3.setPointType(PointType.CalcAnalog);
        a3.setPointOffset(126);
        assignments.add(a3);
        model.addAttribute("assignments", assignments);
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
        model.addAttribute("attributes", atts);
    }
    
    public enum AssignmentSortBy implements DisplayableEnum {

        attributeName,
        deviceType,
        pointType,
        pointOffset;

        @Override
        public String getFormatKey() {
            if (this == attributeName) {
                return "yukon.web.modules.adminSetup.config.attributes." + name();
            }
            return "yukon.common." + name();
        }
    }
    
}