package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.dr.setup.ControlAreaTriggerType;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.SortingParameters;
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
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/setup/controlArea")
public class ControlAreaSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String setupRedirectLink = "/dr/setup/filter?filterByType=" + LmSetupFilterType.CONTROL_AREA;
    private static final Logger log = YukonLogManager.getLogger(ControlAreaSetupController.class);
    private Cache<String, BindingResult> triggerErrorCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
    private Cache<String, ControlAreaTrigger> controlAreaTriggerCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ControlAreaSetupControllerHelper controllerHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @GetMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        ControlArea controlArea = new ControlArea();
        if (model.containsAttribute("controlArea")) {
            controlArea = (ControlArea) model.get("controlArea");
        }
        model.addAttribute("controlArea", controlArea);
        controllerHelper.buildModelMap(model, controlArea);
        return "dr/setup/controlArea/view.jsp";
    }
    
    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request, @DefaultSort(dir = Direction.asc, sort = "startPriority") SortingParameters sorting) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl + "/" + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            ControlArea controlArea = retrieveControlArea(userContext, request, id, url);
            if (controlArea == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }

            setSortingParamaters(model, userContext, sorting, controlArea);

            controllerHelper.buildModelMap(model, controlArea);
            populateTriggerCache(controlArea, model);
            return "dr/setup/controlArea/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }

    }

    @GetMapping("/{id}/edit")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl + "/" + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            ControlArea controlArea = retrieveControlArea(userContext, request, id, url);
            if (controlArea == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }
            if (model.containsAttribute("controlArea")) {
                controlArea = (ControlArea) model.get("controlArea");
            }
            model.addAttribute("controlArea", controlArea);
            controllerHelper.buildModelMap(model, controlArea);
            if (!model.containsAttribute("triggerIds")) {
                populateTriggerCache(controlArea, model);
            }
            return "dr/setup/controlArea/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }

    }
    
    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String save(ModelMap model, @ModelAttribute ControlArea controlArea, @RequestParam List<String> triggerIds, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            String url;
            ResponseEntity<? extends Object> response;
            List<ControlAreaTrigger> triggers = new ArrayList<>(2);
            CollectionUtils.emptyIfNull(triggerIds)
                           .forEach(id -> {
                               triggers.add(controlAreaTriggerCache.asMap().get(id));
            });
            controlArea.setTriggers(triggers);

            if (controlArea.getControlAreaId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl);
                response = saveControlArea(userContext, request, url, controlArea, HttpMethod.POST);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl + "/" + controlArea.getControlAreaId());
                response = saveControlArea(userContext, request, url, controlArea, HttpMethod.PUT);
            }

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(controlArea, "controlArea");
                result = helper.populateBindingError(result, error, response);
                redirectAttributes.addFlashAttribute("triggerIds", triggerIds);
                if (result.hasGlobalErrors()) {

                    List<ObjectError> objectErrorList = result.getGlobalErrors();
                    List<String> errors = objectErrorList.stream()
                                                         .map(obj -> obj.getCode())
                                                         .collect(Collectors.toList());
                    flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(String.join(",", errors)));
                }
                setTriggerErrors(result, controlArea, flash, triggerIds);
                return bindAndForward(controlArea, result, redirectAttributes, model);
            }

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                HashMap<String, Integer> controlAreaIdMap = (HashMap<String, Integer>) response.getBody();
                int controlAreaId = controlAreaIdMap.get("controlAreaId");
                controlAreaTriggerCache.invalidateAll(triggerIds);
                triggerErrorCache.invalidateAll(triggerIds);
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.save.success", controlArea.getName()));
                return "redirect:/dr/setup/controlArea/" + controlAreaId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error creating control area: {}. Error: {}", controlArea.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", controlArea.getName(), ex.getMessage()));
            return "redirect:" + setupRedirectLink;
        }
        return null;
    }
    
    @DeleteMapping("/{id}/delete")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl + "/" + id);
            ResponseEntity<? extends Object> response = deleteControlArea(userContext, request, url, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", lmDelete.getName()));
                return "redirect:" + setupRedirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting control area: {}. Error: {}", lmDelete.getName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", lmDelete.getName(), ex.getMessage()));
            return "redirect:" + setupRedirectLink;
        }
        return "redirect:" + setupRedirectLink;
    }
    
    /**
     * Get the response for save
     */
    private ResponseEntity<? extends Object> saveControlArea(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, ControlArea controlArea, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response =
            apiRequestHelper.callAPIForObject(userContext, request, webserverUrl, methodtype, Object.class, controlArea);
        return response;
    }
    
    /**
     * Get the response for delete
     */
    private ResponseEntity<? extends Object> deleteControlArea(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LMDelete lmDelete) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
            webserverUrl, HttpMethod.DELETE, Object.class, lmDelete);
        return response;
    }
    
    private String bindAndForward(ControlArea controlArea, BindingResult result, RedirectAttributes attrs, ModelMap model) {
        attrs.addFlashAttribute("controlArea", controlArea);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.controlArea", result);
        if (result.hasFieldErrors("dailyStartTimeInMinutes") && controlArea.getDailyStartTimeInMinutes() == null) {
            attrs.addFlashAttribute("startTimeError", true);
        }
        if (result.hasFieldErrors("dailyStopTimeInMinutes") && controlArea.getDailyStopTimeInMinutes() == null) {
            attrs.addFlashAttribute("stopTimeError", true);
        }
        if (controlArea.getControlAreaId() == null) {
            return "redirect:/dr/setup/controlArea/create";
        }
        return "redirect:/dr/setup/controlArea/" + controlArea.getControlAreaId() + "/edit";
    }
    
    /**
     * Make a rest call for retrieving control area
     */
    private ControlArea retrieveControlArea(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        ControlArea controlArea = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, ControlArea.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                controlArea = (ControlArea) response.getBody();
                controlArea.setControlAreaId(id);
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving control area: " + ex.getMessage());
        }
        return controlArea;
    }

    @GetMapping("/renderTrigger")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String addTrigger(ModelMap model, @ModelAttribute ControlAreaTrigger controlAreaTrigger) {
        model.addAttribute("mode", PageEditMode.CREATE);
        controllerHelper.buildTriggerModelMap(model, controlAreaTrigger);
        return "dr/setup/controlArea/trigger/triggerPopup.jsp";
    }

    @GetMapping("/renderTrigger/{triggerId}")
    public String renderTrigger(ModelMap model, @PathVariable String triggerId, @RequestParam PageEditMode mode,
            YukonUserContext userContext, HttpServletRequest request,ModelMap map, RedirectAttributes attributes) {
        PageEditMode editMode = mode == PageEditMode.CREATE ? PageEditMode.EDIT : mode;
        model.addAttribute("mode", editMode);
        ControlAreaTrigger controlAreaTrigger = controlAreaTriggerCache.asMap().get(triggerId);
        if (controlAreaTrigger.getTriggerType() == ControlAreaTriggerType.STATUS) {
            model.addAttribute("normalStates", retrieveNormalState(controlAreaTrigger.getTriggerPointId(), userContext, request));
        }
        controllerHelper.buildTriggerModelMap(model, controlAreaTrigger);
        
        BindingResult result = triggerErrorCache.asMap().get(triggerId);
        if (result != null && CollectionUtils.isNotEmpty(result.getFieldErrors())) {
            model.put("org.springframework.validation.BindingResult.controlAreaTrigger", result);
        }
        return "dr/setup/controlArea/trigger/triggerPopup.jsp";
    }

    @PostMapping("/trigger/save")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public @ResponseBody Map<String, String> saveTrigger(ModelMap model, @RequestParam String id,
            @ModelAttribute ControlAreaTrigger controlAreaTrigger) {
        Map<String, String> triggerInfo = new HashMap<>();
        if (StringUtils.isNotBlank(id)) {
            controlAreaTriggerCache.put(id, controlAreaTrigger);
            triggerInfo.put("triggerId", id);
        } else {
            final String key = UUID.randomUUID().toString().replace("-", "");
            triggerInfo.put("triggerId", key);
            controlAreaTriggerCache.put(key, controlAreaTrigger);
        }
        triggerInfo.put("triggerName", getTriggerName(controlAreaTrigger));
        triggerErrorCache.invalidate(id);
        return triggerInfo;
    }

    private String getTriggerName(ControlAreaTrigger controlAreaTrigger) {
        String name = controlAreaTrigger.getTriggerPointName().replace(":", " /");
        return controlAreaTrigger.getTriggerType().getTriggerTypeValue() + " (" + name + ")";
    }

    @GetMapping("/getNormalState/{pointId}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public @ResponseBody Map<String, List<LMDto>> getNormalState(@PathVariable int pointId,
            YukonUserContext userContext, HttpServletRequest request) {
        List<LMDto> normalStates = retrieveNormalState(pointId, userContext, request);
        return Collections.singletonMap("normalStates", normalStates);
    }

    private List<LMDto> retrieveNormalState(int pointId, YukonUserContext userContext, HttpServletRequest request) {
        List<LMDto> normalStates = new ArrayList<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.pointUrl + pointId + "/states");
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForList(userContext, request, url, LMDto.class, HttpMethod.GET, LMDto.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                normalStates = (List<LMDto>) response.getBody();
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
        }
        return normalStates;
    }

    @DeleteMapping("/trigger/remove/{triggerId}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public void removeTrigger(@PathVariable String triggerId) {
        controlAreaTriggerCache.invalidate(triggerId);
    }

    private void populateTriggerCache(ControlArea controlArea, ModelMap model) {
        List<String> triggerIds = new ArrayList<>();
        CollectionUtils.emptyIfNull(controlArea.getTriggers())
                       .forEach(trigger -> {
                           controlAreaTriggerCache.put(String.valueOf(trigger.getTriggerId()), trigger);
            triggerIds.add(String.valueOf(trigger.getTriggerId()));
        });
        model.addAttribute("triggerIds", triggerIds);
    }
    
    private void setTriggerErrors(BindingResult result, ControlArea controlArea, FlashScope flash,
            List<String> triggerIds) {

        List<FieldError> errorList = result.getFieldErrors();
        Set<Integer> triggerPositionIndexes = errorList.stream()
                                                       .filter(fieldError -> fieldError.getField().contains("triggers"))
                                                       .map(fieldError -> Integer.parseInt(fieldError.getField().replaceAll("[\\D]", "")))
                                                       .collect(Collectors.toSet());
        List<String> filteredNameList = IntStream.range(0, controlArea.getTriggers().size())
                                                 .filter(i -> triggerPositionIndexes.contains(i))
                                                 .mapToObj(controlArea.getTriggers()::get)
                                                 .map(controlAreaTrigger -> getTriggerName(controlAreaTrigger))
                                                 .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(filteredNameList)) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.trigger.error",
                String.join(", ", filteredNameList)));
        }

        CollectionUtils.emptyIfNull(triggerPositionIndexes).forEach(index -> {
            ControlAreaTrigger controlAreaTrigger = controlArea.getTriggers().get(index);
            String triggerId = triggerIds.get(index);
            BindException bindException = new BindException(controlAreaTrigger, "controlAreaTrigger");
            BindingResult bindingResult = new BindException(bindException.getTarget(), bindException.getObjectName());
            List<FieldError> filteredErrors = errorList.stream()
                                                       .filter(fieldError -> fieldError.getField().contains("triggers[" + index + "]"))
                                                       .collect(Collectors.toList());
            filteredErrors.stream().forEach(fieldError -> {
                String fieldName = StringUtils.substringAfter(fieldError.getField(), "triggers[" + index + "].");
                String errorMessage = fieldError.getDefaultMessage();
                bindingResult.rejectValue(fieldName, "", errorMessage);
            });
            triggerErrorCache.put(triggerId, bindingResult);
        });
    }

    @GetMapping("/sortProgram/{id}")
    public String sortAssignedProgram(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request, @DefaultSort(dir = Direction.asc, sort = "startPriority") SortingParameters sorting) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUrl + "/" + id);
            ControlArea controlArea = retrieveControlArea(userContext, request, id, url);
            if (controlArea == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }

            List<ControlAreaProgramAssignment> assignedProgramList = controlArea.getProgramAssignment();
            ControlAreaSortBy sortBy = ControlAreaSortBy.valueOf(sorting.getSort());

            if (assignedProgramList != null) {
                Comparator<ControlAreaProgramAssignment> comparator = controlArea.getNameComparator();

                if (sortBy == ControlAreaSortBy.startPriority) {
                    comparator = controlArea.getStartPriorityComparator();
                }

                if (sortBy == ControlAreaSortBy.stopPriority) {
                    comparator = controlArea.getStopPriorityComparator();
                }

                if (sorting.getDirection() == Direction.desc) {
                    comparator = Collections.reverseOrder(comparator);
                }
                Collections.sort(assignedProgramList, comparator);
                controlArea.setProgramAssignment(assignedProgramList);
            }

            setSortingParamaters(model, userContext, sorting, controlArea);

            return "dr/setup/controlArea/sortedAssignedPrograms.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }
    }

    private void setSortingParamaters(ModelMap model, YukonUserContext userContext, SortingParameters sorting, ControlArea controlArea) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        ControlAreaSortBy sortBy = ControlAreaSortBy.valueOf(sorting.getSort());
        Direction dir = sorting.getDirection();
        List<SortableColumn> columns = new ArrayList<>();
        for (ControlAreaSortBy column : ControlAreaSortBy.values()) {
            String text = accessor.getMessage(column);
            SortableColumn col = SortableColumn.of(dir, column == sortBy, text, column.name());
            columns.add(col);
            model.addAttribute(column.name(), col);
        }
        model.addAttribute("controlArea", controlArea);
    }

    private enum ControlAreaSortBy implements DisplayableEnum {

        name,
        startPriority,
        stopPriority;

        @Override
        public String getFormatKey() {
            return baseKey + "controlArea." + name();
        }
    }
    
}
