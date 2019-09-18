package com.cannontech.web.dr.setup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMPaoDto;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.dr.setup.MacroLoadGroup;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
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
import com.cannontech.yukon.IDatabaseCache;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/setup/macroLoadGroup")
public class MacroLoadGroupSetupController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private IDatabaseCache dbCache;

    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String setupRedirectLink = "/dr/setup/filter?filterByType=" + LmSetupFilterType.MACRO_LOAD_GROUP;
    private static final Logger log = YukonLogManager.getLogger(MacroLoadGroupSetupController.class);

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drMacroLoadGroupRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            MacroLoadGroup macroLoadGroupBase = retrieveGroup(userContext, request, id, url);
            if (macroLoadGroupBase == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "macroLoadGroup.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }
            macroLoadGroupBase.setId(id);
            model.addAttribute("macroLoadGroup", macroLoadGroupBase);
        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }
        return "dr/setup/macroLoadGroup/view.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drMacroLoadGroupRetrieveUrl + id);
            MacroLoadGroup macroLoadGroup = retrieveGroup(userContext, request, id, url);
            if (macroLoadGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "macroLoadGroup.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            } else if (model.containsAttribute("macroLoadGroup")) {
                macroLoadGroup = (MacroLoadGroup) model.get("macroLoadGroup");
            }
            macroLoadGroup.setId(id);
            List<Integer> selectedLoadGroupIds = CollectionUtils.emptyIfNull(macroLoadGroup.getAssignedLoadGroups()).stream().map(item -> item.getId())
                                                                                                                             .collect(Collectors.toList());
            model.addAttribute("selectedLoadGroupIds", selectedLoadGroupIds);
            model.addAttribute("macroLoadGroup", macroLoadGroup);
            model.addAttribute("mode", PageEditMode.EDIT);
            return "dr/setup/macroLoadGroup/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }
    }

    @GetMapping("/create")
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request, FlashScope flash) {
        MacroLoadGroup macroLoadGroup = new MacroLoadGroup();
        macroLoadGroup.setType(PaoType.MACRO_GROUP);
        if (model.containsAttribute("macroLoadGroup")) {
            macroLoadGroup = (MacroLoadGroup) model.get("macroLoadGroup");
        }
        model.addAttribute("macroLoadGroup", macroLoadGroup);
        List<Integer> selectedLoadGroupIds = CollectionUtils.emptyIfNull(macroLoadGroup.getAssignedLoadGroups()).stream().map(item -> item.getId())
                                                                                                                         .collect(Collectors.toList());
        model.addAttribute("selectedLoadGroupIds", selectedLoadGroupIds);
        model.addAttribute("mode", PageEditMode.CREATE);
        return "dr/setup/macroLoadGroup/view.jsp";
    }

    @PostMapping("/{id}/copy")
    public String copy(@ModelAttribute("lmCopy") LMCopy lmCopy, @PathVariable int id, BindingResult result,
            YukonUserContext userContext, FlashScope flash, HttpServletRequest request, HttpServletResponse response,
            ModelMap model) throws JsonGenerationException, JsonMappingException, IOException {
        Map<String, String> json = new HashMap<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drMacroLoadGroupCopyUrl + id);

            // Call Api to copy
            ResponseEntity<? extends Object> responseEntity =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, lmCopy);

            if (responseEntity.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(lmCopy, "lmCopy");
                helper.populateBindingError(result, error, responseEntity);
                model.addAttribute("lmCopy", lmCopy);
                model.addAttribute("loadGroupId", id);
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return "dr/setup/macroLoadGroup/copyPopup.jsp";
            }

            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) responseEntity.getBody();
                int copiedLoadGroupId = paoIdMap.get("paoId");
                json.put("copiedLoadGroupId", String.valueOf(copiedLoadGroupId));
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "copy.success", lmCopy.getName()));
            }

        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            json.put("redirectUrl", setupRedirectLink);
        } catch (RestClientException ex) {
            log.error("Error while copying load group: ", ex);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "copy.error", lmCopy.getName()));
            json.put("redirectUrl", setupRedirectLink);
        }
        response.setContentType("application/json");
        JsonUtils.getWriter().writeValue(response.getOutputStream(), json);
        return null;

    }

    @PostMapping("/save")
    public String save(@ModelAttribute("macroLoadGroup") MacroLoadGroup macroLoadGroup, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes,
            HttpServletRequest request, @RequestParam("selectedLoadGroupIds") List<Integer> selectedLoadGroupIds) {
        List<LMPaoDto> assignedLoadGroups = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(selectedLoadGroupIds)) {
            assignedLoadGroups = Lists.transform(selectedLoadGroupIds, new Function<Integer, LMPaoDto>() {
                public LMPaoDto apply(Integer selectedLoadGroupId) {
                    LMPaoDto lmBasic = new LMPaoDto();
                    lmBasic.setId(selectedLoadGroupId);
                    return lmBasic;
                }
            });
            macroLoadGroup.setAssignLoadGroups(assignedLoadGroups);
        }

        try {
            ResponseEntity<? extends Object> response = null;
            if (macroLoadGroup.getId() == null) {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.drMacroLoadGroupCreateUrl);
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    macroLoadGroup);
            } else {
                String url = helper.findWebServerUrl(request, userContext,
                    ApiURL.drMacroLoadGroupUpdateUrl + macroLoadGroup.getId());
                response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class,
                    macroLoadGroup);
            }

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(macroLoadGroup, "macroLoadGroup");
                result = helper.populateBindingError(result, error, response);
                if (result.hasFieldErrors("assignedLoadGroups")) {
                    flash.setError(result.getFieldError("assignedLoadGroups"));
                }
                if (result.hasGlobalErrors()) {
                    List<ObjectError> objectErrorList = result.getGlobalErrors();
                    List<String> errors =objectErrorList.stream()
                                                        .map(obj -> obj.getCode())
                                                        .collect(Collectors.toList());
                    flash.setError(YukonMessageSourceResolvable.createDefaultWithoutCode(String.join(", ", errors)));
                }
                return bindAndForward(macroLoadGroup, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) response.getBody();
                int groupId = paoIdMap.get("paoId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", macroLoadGroup.getName()));
                return "redirect:/dr/setup/macroLoadGroup/" + groupId;
            }
        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error creating load group: ", ex);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", macroLoadGroup.getName()));
            return "redirect:" + setupRedirectLink;
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {
        try {
            // Api call to delete macro load group
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drMacroLoadGroupDeleteUrl + id);
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.DELETE, Object.class, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:" + setupRedirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e);
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting macro load group: ", ex);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", lmDelete.getName()));
            return "redirect:" + setupRedirectLink;
        }
        return "redirect:" + setupRedirectLink;
    }

    @GetMapping("/{id}/renderCopyPopup")
    public String renderCopyPopup(@PathVariable int id, ModelMap model, YukonUserContext userContext) {
        LMCopy lmCopy = null;
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        if (model.containsAttribute("lmCopy")) {
            lmCopy = (LMCopy) model.get("lmCopy");
        } else {
            Optional<LiteYukonPAObject> loadGroup =
                dbCache.getAllLMGroups().stream().filter(group -> group.getLiteID() == id).findFirst();
            LiteYukonPAObject pao = loadGroup.get();
            lmCopy = new LMCopy();
            lmCopy.setName(messageSourceAccessor.getMessage("yukon.common.copyof", pao.getPaoName()));
        }
        model.addAttribute("loadGroupId", id);
        model.addAttribute("lmCopy", lmCopy);
        return "dr/setup/macroLoadGroup/copyPopup.jsp";
    }

    private String bindAndForward(MacroLoadGroup macroLoadGroup, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("macroLoadGroup", macroLoadGroup);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.macroLoadGroup", result);
        if (macroLoadGroup.getId() == null) {
            return "redirect:/dr/setup/macroLoadGroup/create";
        }
        return "redirect:/dr/setup/macroLoadGroup/" + macroLoadGroup.getId() + "/edit";
    }

    /**
     * Make a rest call for retrieving group
     */
    private MacroLoadGroup retrieveGroup(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        MacroLoadGroup macroLoadGroup = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, MacroLoadGroup.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                macroLoadGroup = (MacroLoadGroup) response.getBody();
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving load group: ", ex);
        }
        return macroLoadGroup;
    }
}
