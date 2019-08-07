package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.dr.setup.ControlAreaTriggerType;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Controller
@RequestMapping("/setup/controlArea")
public class ControlAreaSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final Logger log = YukonLogManager.getLogger(ControlAreaSetupController.class);
    private Cache<String, ControlAreaTrigger> controlAreaTriggerCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ControlAreaSetupControllerHelper controllerHelper;

    @GetMapping("/create")
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
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            ControlArea controlArea = retrieveControlArea(userContext, request, id, url);
            if (controlArea == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.retrieve.error"));
                return "redirect:/dr/setup/list";
            }
            model.addAttribute("controlArea", controlArea);
            controllerHelper.buildModelMap(model, controlArea);
            populateTriggerCache(controlArea, model);
            return "dr/setup/controlArea/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }
    
    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            ControlArea controlArea = retrieveControlArea(userContext, request, id, url);
            if (controlArea == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "controlArea.retrieve.error"));
                return "redirect:/dr/setup/list";
            }
            if (model.containsAttribute("controlArea")) {
                controlArea = (ControlArea) model.get("controlArea");
            }
            model.addAttribute("controlArea", controlArea);
            controllerHelper.buildModelMap(model, controlArea);
            populateTriggerCache(controlArea, model);
            return "dr/setup/controlArea/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }
    
    @PostMapping("/save")
    public String save(ModelMap model, @ModelAttribute ControlArea controlArea, @RequestParam List<String> triggerIds, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            String url;
            if (controlArea.getControlAreaId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaCreateUrl);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaUpdateUrl + controlArea.getControlAreaId());
            }
            List<ControlAreaTrigger> triggers = new ArrayList<>(2);
            triggerIds.stream()
                      .forEach(id -> {
                       triggers.add(controlAreaTriggerCache.asMap().get(id));
            });
            controlArea.setTriggers(triggers);
            ResponseEntity<? extends Object> response =
                    saveControlArea(userContext, request, url, controlArea, HttpMethod.POST);
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
                return bindAndForward(controlArea, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> controlAreaIdMap = (HashMap<String, Integer>) response.getBody();
                int controlAreaId = controlAreaIdMap.get("controlAreaId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", controlArea.getName()));
                return "redirect:/dr/setup/controlArea/" + controlAreaId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error creating control area: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", controlArea.getName()));
            return "redirect:/dr/setup/list";
        }
        return null;
    }
    
    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drControlAreaDeleteUrl + id);
            ResponseEntity<? extends Object> response = deleteControlArea(userContext, request, url, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:/dr/setup/list";
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error deleting control area: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", lmDelete.getName()));
            return "redirect:/dr/setup/list";
        }
        return "dr/setup/list.jsp";
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
    
    private String bindAndForward(ControlArea controlArea, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("controlArea", controlArea);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.controlArea", result);
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
    public String addTrigger(ModelMap model, @ModelAttribute ControlAreaTrigger controlAreaTrigger) {
        model.addAttribute("mode", PageEditMode.CREATE);
        controllerHelper.buildTriggerModelMap(model, controlAreaTrigger);
        return "dr/setup/controlArea/trigger/triggerPopup.jsp";
    }

    @GetMapping("/renderTrigger/{triggerId}")
    public String renderTrigger(ModelMap model, @PathVariable String triggerId, @RequestParam PageEditMode mode,
            YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", mode);
        ControlAreaTrigger controlAreaTrigger = controlAreaTriggerCache.asMap().get(triggerId);
        if (mode != PageEditMode.VIEW && controlAreaTrigger.getTriggerType() == ControlAreaTriggerType.STATUS) {
            model.addAttribute("normalStates", retrieveNormalState(controlAreaTrigger.getTriggerPointId(), userContext, request));
        }
        controllerHelper.buildTriggerModelMap(model, controlAreaTrigger);
        return "dr/setup/controlArea/trigger/triggerPopup.jsp";
    }

    @PostMapping("/trigger/save")
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
        return triggerInfo;
    }

    private String getTriggerName(ControlAreaTrigger controlAreaTrigger) {
        String name = controlAreaTrigger.getTriggerPointName().replace(":", " /");
        return controlAreaTrigger.getTriggerType().getTriggerTypeValue() + " (" + name + ")";
    }

    @GetMapping("/getNormalState/{pointId}")
    public @ResponseBody Map<String, List<LMDto>> getNormalState(@PathVariable int pointId,
            YukonUserContext userContext, HttpServletRequest request) {
        List<LMDto> normalStates = retrieveNormalState(pointId, userContext, request);
        return Collections.singletonMap("normalStates", normalStates);
    }

    private List<LMDto> retrieveNormalState(int pointId, YukonUserContext userContext, HttpServletRequest request) {
        List<LMDto> normalStates = new ArrayList<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drNormalStateUrl + pointId);
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

    @GetMapping("/trigger/remove/{triggerId}")
    public void removeTrigger(@PathVariable String triggerId) {
        controlAreaTriggerCache.asMap().remove(triggerId);
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
}
