package com.cannontech.web.admin;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.utils.URIBuilder;
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
import com.cannontech.common.pao.attribute.model.Assignment;
import com.cannontech.common.pao.attribute.model.AttributeAssignment;
import com.cannontech.common.pao.attribute.model.CustomAttribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.web.admin.dao.CustomAttributeDao.SortBy;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
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
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ADMIN_MANAGE_ATTRIBUTES, level = HierarchyPermissionLevel.OWNER)
public class AttributesController {
    
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private AttributeValidator attributeValidator;
    @Autowired private AssignmentValidator assignmentValidator;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    private static final String redirectLink = "redirect:/admin/config/attributes";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.adminSetup.config.attributes.";
    
    private static final Logger log = YukonLogManager.getLogger(AttributesController.class);

    @GetMapping("/config/attributes")
    public String attributes(@DefaultSort(dir=Direction.asc, sort="attributeName") SortingParameters sorting, 
                             ModelMap model, YukonUserContext userContext, HttpServletRequest request, 
                             HttpServletResponse resp, FlashScope flashScope) {
        retrieveCustomAttributes(model, userContext, request, flashScope);
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
        retrieveAssignments(sorting, null, null, model, userContext, request, resp);
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
                                 HttpServletResponse resp, FlashScope flashScope, RedirectAttributes redirectAttributes, Boolean isEditMode) {
        attributeValidator.validate(attribute, result);
        Boolean isCreate = attribute.getCustomAttributeId() == null;
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupErrorModel(redirectAttributes, attribute, result, isEditMode);
            return redirectLink;
        }

        try {
            ResponseEntity<? extends Object> response = null;
            if (!isCreate) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + "/" + attribute.getCustomAttributeId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.PATCH, Object.class, attribute);
            } else {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, attribute);
            }

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(attribute, isCreate ? "createAttribute" : "editAttribute");
                result = helper.populateBindingErrorForApiErrorModel(result, error, response, "yukon.web.error.");
                if (result.hasErrors()) {
                    resp.setStatus(HttpStatus.BAD_REQUEST.value());
                    setupErrorModel(redirectAttributes, attribute, result, isEditMode);
                    return redirectLink;
                }
            }

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", attribute.getName()));
            }

        } catch (ApiCommunicationException ex) {
            log.error(ex.getMessage());
        } catch (RestClientException e) {
            log.error("Error saving custom attribute: {}. Error: {}", attribute.getName(), e.getMessage());
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", attribute.getName(), e.getMessage()));
        }
        return redirectLink;
    }
    
    private void setupErrorModel(RedirectAttributes redirectAttributes, CustomAttribute attribute, BindingResult result, Boolean isEditMode) {
        if (isEditMode) {
            redirectAttributes.addFlashAttribute("enableEditId", attribute.getCustomAttributeId());
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
        
        // check if the virtual meter or virtual system are not present in your system, then add them to the dropdown
        if (!deviceTypes.contains(PaoType.VIRTUAL_METER)) {
            deviceTypes.add(PaoType.VIRTUAL_METER);
        }
        if (!deviceTypes.contains(PaoType.VIRTUAL_SYSTEM)) {
            deviceTypes.add(PaoType.VIRTUAL_SYSTEM);
        }
        return deviceTypes;
    }
        
    @DeleteMapping("/config/attribute/{id}/delete")
    public String deleteAttribute(ModelMap model, @PathVariable int id, String name, HttpServletRequest request, 
                                  YukonUserContext userContext, FlashScope flashScope) {
        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl + "/" + id);
            ResponseEntity<? extends Object> deleteResponse = 
                    apiRequestHelper.callAPIForObject(userContext, request, deleteUrl, HttpMethod.DELETE, Object.class, Integer.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", name));
                return redirectLink;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting custom attribute: {}. Error: {}", name, ex.getMessage());
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", name, ex.getMessage()));
            return redirectLink;
        }
        return redirectLink;
    }
    
    @GetMapping("/config/attributeAssignments/filter")
    public String filterAssignments(@DefaultSort(dir=Direction.asc, sort="attributeName") SortingParameters sorting, Integer[] selectedAttributes, 
                                    PaoType[] selectedDeviceTypes, ModelMap model, YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) {
        retrieveAssignments(sorting, selectedAttributes, selectedDeviceTypes, model, userContext, request, resp);
        return "config/attributeAssignmentsTable.jsp";
    }
    
    @GetMapping("/config/attributeAssignments/popup")
    public String assignmentPopup(Integer id, ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope) {
        Assignment req = new Assignment();
        if (id != null) {
            model.addAttribute("isEditMode", true);
            try {
                String retrieveUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + "/" + id);
                ResponseEntity<? extends Object> retrieveResponse = 
                        apiRequestHelper.callAPIForObject(userContext, request, retrieveUrl, HttpMethod.GET, AttributeAssignment.class, Integer.class);

                if (retrieveResponse.getStatusCode() == HttpStatus.OK) {
                    AttributeAssignment assignment = (AttributeAssignment) retrieveResponse.getBody();
                    req.setAttributeAssignmentId(assignment.getAttributeAssignmentId());
                    req.setAttributeId(assignment.getCustomAttribute().getCustomAttributeId());
                    req.setPaoType(assignment.getPaoType());
                    req.setPointType(assignment.getPointType());
                    req.setOffset(assignment.getOffset());
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
        
        model.addAttribute("assignment", req);
        retrievePopupModel(model, userContext, request, flashScope);

        return "config/attributeAssignmentPopup.jsp";
    }
    
    private void retrievePopupModel(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash) {
        retrieveCustomAttributes(model, userContext, request, flash);
        model.addAttribute("deviceTypes", getDeviceTypesThatSupportAssignment());
        model.addAttribute("pointTypes", PointType.values());
    }
    
    @PostMapping("/config/attributeAssignments/save")
    public String saveAssignment(@ModelAttribute("assignment") Assignment assignment, BindingResult result, ModelMap model, 
                                    PaoType[] deviceTypes, String attributeName, YukonUserContext userContext, HttpServletRequest request, 
                                    HttpServletResponse resp, FlashScope flashScope) {
        if (assignment.getAttributeAssignmentId() != null) {
            model.addAttribute("isEditMode", true);
        }
        model.addAttribute("selectedDeviceTypes", deviceTypes);

        assignmentValidator.validate(assignment, result);
        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            retrievePopupModel(model, userContext, request, flashScope);
            return "config/attributeAssignmentPopup.jsp";
        }
        
        List<String> successDeviceTypes = new ArrayList<>();
        List<String> failedDeviceTypes = new ArrayList<>();
        if (assignment.getAttributeAssignmentId() != null) {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + "/" + assignment.getAttributeAssignmentId());
            callCreateAssignment(assignment, successDeviceTypes, failedDeviceTypes, attributeName, userContext, request, url, result);
        } else {
            if (deviceTypes == null) {
                //All Device Types was selected
                deviceTypes = (PaoType[]) getDeviceTypesThatSupportAssignment().toArray(PaoType[]::new);
            }

            String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl);
            for (PaoType type : deviceTypes) {
                assignment.setPaoType(type);
                callCreateAssignment(assignment, successDeviceTypes, failedDeviceTypes, attributeName, userContext, request, url,
                        result);
                if (result.hasErrors()) {
                    break;
                }
            }
        }
        
        if (!failedDeviceTypes.isEmpty() || !successDeviceTypes.isEmpty()) {
            Map<String, Object> json = new HashMap<>();
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            if (!failedDeviceTypes.isEmpty()) {
                json.put("errorMessage", accessor.getMessage(baseKey + "attributeAssignmentFailed", attributeName, String.join(", ", failedDeviceTypes)));
            }
            if (!successDeviceTypes.isEmpty()) {
                json.put("successMessage", accessor.getMessage(baseKey + "attributeAssignmentSuccess", attributeName, String.join(", ", successDeviceTypes)));
            }
            resp.setContentType("application/json");
            return JsonUtils.writeResponse(resp, json);
        }

        if (result.hasErrors()) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        
        retrievePopupModel(model, userContext, request, flashScope);
        return "config/attributeAssignmentPopup.jsp";
    }
    
    private void callCreateAssignment(Assignment assignment, List<String> successDeviceTypes, List<String> failedDeviceTypes, 
                                      String attributeName, YukonUserContext userContext, HttpServletRequest request, String url, BindingResult result) {
        try {
            HttpMethod method = assignment.getAttributeAssignmentId() != null ? HttpMethod.PATCH : HttpMethod.POST;
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, 
                                                                                          method, Object.class, assignment);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                failedDeviceTypes.add(assignment.getPaoType().getDbString());
            }
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                successDeviceTypes.add(assignment.getPaoType().getDbString());
            }
        } catch (ApiCommunicationException | RestClientException e) {
            log.error("Error saving custom attribute assignment for attribute: {} and device type: {}.", attributeName, assignment.getPaoType().getDbString(), e);
            failedDeviceTypes.add(assignment.getPaoType().getDbString());
        }
    }
    
    @DeleteMapping("/config/attributeAssignments/{id}/delete")
    public String deleteAssignment(ModelMap model, @PathVariable int id, String name, HttpServletRequest request, 
                                  YukonUserContext userContext, FlashScope flashScope, HttpServletResponse resp) {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl + "/" + id);
            ResponseEntity<? extends Object> deleteResponse = 
                    apiRequestHelper.callAPIForObject(userContext, request, deleteUrl, HttpMethod.DELETE, Object.class, Integer.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                json.put("successMessage", accessor.getMessage(baseKey + "attributeAssignmentDeleteSuccess", name));
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            json.put("errorMessage", accessor.getMessage(communicationKey));
        } catch (RestClientException ex) {
            String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
            log.error("Error deleting assignment for custom attribute: {}. Error: {}", name, ex.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            json.put("errorMessage", accessor.getMessage("yukon.web.api.delete.error", attributeAssignmentLabel, ex.getMessage()));
        }
        resp.setContentType("application/json");
        return JsonUtils.writeResponse(resp, json);
    }
    
    private void retrieveAssignments(SortingParameters sorting, Integer[] selectedAttributes, PaoType[] selectedDeviceTypes, 
                                     ModelMap model, YukonUserContext userContext, HttpServletRequest request, HttpServletResponse resp) {
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

        List<AttributeAssignment> assignmentList = new ArrayList<>();
        
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeAssignmentsUrl);
            URIBuilder ub = new URIBuilder(url);
            if (selectedAttributes != null) {
                for (Integer attr : selectedAttributes) {
                    ub.addParameter("attributeIds", Integer.toString(attr));
                }
            }
            if (selectedDeviceTypes != null) {
                for (PaoType type : selectedDeviceTypes) {
                    ub.addParameter("paoTypes", type.name());
                }
            }
            ub.addParameter("sort", sortBy.getValue().name());
            ub.addParameter("dir", dir.name());
            
            ResponseEntity<? extends Object> response = 
                    apiRequestHelper.callAPIForList(userContext, request, ub.toString(), AttributeAssignment.class, HttpMethod.GET, AttributeAssignment.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                assignmentList = (List<AttributeAssignment>) response.getBody();
            }
            model.addAttribute("assignments", assignmentList);
            
        } catch (ApiCommunicationException e) {
            log.error(e);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            model.addAttribute("errorMessage", accessor.getMessage(communicationKey));
        } catch (RestClientException ex) {
            log.error("Error retrieving custom attribute assignments. Error: {}", ex.getMessage());
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
            model.addAttribute("errorMessage", accessor.getMessage("yukon.web.api.retrieve.error", attributeAssignmentLabel, ex.getMessage()));
        } catch (URISyntaxException e) {
            log.error("URI syntax error while creating builder for retrieving custom attributes", e);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            String attributeAssignmentLabel = accessor.getMessage(baseKey + "attributeAssignment");
            model.addAttribute("errorMessage", accessor.getMessage("yukon.web.api.retrieve.error", attributeAssignmentLabel, e.getMessage()));
        }
    }
    
    private void retrieveCustomAttributes(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flashScope) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);

        String url = helper.findWebServerUrl(request, userContext, ApiURL.attributeUrl);
        List<CustomAttribute> attributeList = new ArrayList<>();

        try {
            ResponseEntity<? extends Object> response = 
                    apiRequestHelper.callAPIForList(userContext, request, url, CustomAttribute.class, HttpMethod.GET, CustomAttribute.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                attributeList = (List<CustomAttribute>) response.getBody();
            }
            model.addAttribute("attributes", attributeList);
        } catch (ApiCommunicationException e) {
            log.error(e);
            flashScope.setError(new YukonMessageSourceResolvable(communicationKey));
        } catch (RestClientException ex) {
            log.error("Error retrieving custom attributes. Error: {}", ex.getMessage());
            String customAttributesLabel = accessor.getMessage(baseKey + "customAttributes");
            flashScope.setError(new YukonMessageSourceResolvable("yukon.web.api.retrieve.error", customAttributesLabel, ex.getMessage()));
        }
    }
    
    public enum AssignmentSortBy implements DisplayableEnum {

        attributeName(SortBy.attributeName),
        deviceType(SortBy.paoType),
        pointType(SortBy.pointType),
        pointOffset(SortBy.pointOffset);
        
        private final SortBy value;
        
        private AssignmentSortBy(SortBy value) {
            this.value = value;
        }

        @Override
        public String getFormatKey() {
            if (this == attributeName) {
                return "yukon.web.modules.adminSetup.config.attributes." + name();
            }
            return "yukon.common." + name();
        }

        public SortBy getValue() {
            return value;
        }
    }
    
}