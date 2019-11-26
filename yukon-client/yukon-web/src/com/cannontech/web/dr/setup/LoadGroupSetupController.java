package com.cannontech.web.dr.setup;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LmSetupFilterType;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.yukon.IDatabaseCache;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/setup/loadGroup")
public class LoadGroupSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String bindingResultKey = "org.springframework.validation.BindingResult.loadGroup";
    private static final String setupRedirectLink = "/dr/setup/filter?filterByType=" + LmSetupFilterType.LOAD_GROUP;
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupController.class);
    private static final List<PaoType> switchTypes =
            PaoType.getAllLMGroupTypes()
            .stream().sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
            .collect(Collectors.toList());

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private LoadGroupSetupControllerHelper controllerHelper;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private ServerDatabaseCache cache;

    @GetMapping("/create")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model, YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = new LoadGroupBase();
        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
            if (loadGroup.getType() != null) {
                controllerHelper.buildModelMap(loadGroup.getType(), model, request, userContext, bindingResultKey);
            }
        }
        model.addAttribute("selectedSwitchType", loadGroup.getType());
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/loadGroupView.jsp";
    }
    
    @GetMapping("/create/{type}")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String create(ModelMap model, @PathVariable String type, @RequestParam String name,
            YukonUserContext userContext, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = LMModelFactory.createLoadGroup(PaoType.valueOf(type));
        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
        } else {
            loadGroup.setName(name);
            loadGroup.setType(PaoType.valueOf(type));
            controllerHelper.setDefaultValues(loadGroup, userContext.getYukonUser());
        }
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("switchTypes", switchTypes);
        model.addAttribute("selectedSwitchType", type);
        controllerHelper.buildModelMap(PaoType.valueOf(type), model, request, userContext, bindingResultKey);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
            if (loadGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "loadGroup.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            }
            model.addAttribute("selectedSwitchType", loadGroup.getType());
            model.addAttribute("loadGroup", loadGroup);
            controllerHelper.buildModelMap(loadGroup.getType(), model, request, userContext, bindingResultKey);
            model.addAttribute("isLoadGroupSupportRoute", loadGroup.getType().isLoadGroupSupportRoute());
            return "dr/setup/loadGroup/loadGroupView.jsp";
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
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
            if (loadGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "loadGroup.retrieve.error"));
                return "redirect:" + setupRedirectLink;
            } else if (model.containsAttribute("loadGroup")) {
                loadGroup = (LoadGroupBase) model.get("loadGroup");
                loadGroup.setId(id);
            }
            
            model.addAttribute("loadGroup", loadGroup);
            model.addAttribute("selectedSwitchType", loadGroup.getType());
            model.addAttribute("switchTypes", switchTypes);
            controllerHelper.buildModelMap(loadGroup.getType(), model, request, userContext, bindingResultKey);
            return "dr/setup/loadGroup/loadGroupView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        }

    }

    @PostMapping("/save")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.UPDATE)
    public String save(@ModelAttribute("loadGroup") LoadGroupBase loadGroup, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            String url;
            if (loadGroup.getId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupSaveUrl);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupUpdateUrl + loadGroup.getId());
            }
            ResponseEntity<? extends Object> response =
                    saveGroup(userContext, request, url, loadGroup, HttpMethod.POST);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(loadGroup, "loadGroup");
                result = helper.populateBindingError(result, error, response);
                if (loadGroup.getType() != null) {
                    controllerHelper.setValidationMessageInFlash(result, flash, loadGroup.getType());
                }
                return bindAndForward(loadGroup, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> groupIdMap = (HashMap<String, Integer>) response.getBody();
                int groupId = groupIdMap.get("groupId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "save.success", loadGroup.getName()));
                return "redirect:/dr/setup/loadGroup/" + groupId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error creating load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", loadGroup.getName()));
            return "redirect:" + setupRedirectLink;
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.OWNER)
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupDeleteUrl + id);
            ResponseEntity<? extends Object> response = deleteGroup(userContext, request, url, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", lmDelete.getName()));
                return "redirect:" + setupRedirectLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + setupRedirectLink;
        } catch (RestClientException ex) {
            log.error("Error deleting load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error.exception.message", ex.getMessage()));
            return "redirect:" + setupRedirectLink;
        }
        return "redirect:" + setupRedirectLink;
    }

    @PostMapping("/{id}/copy")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String copy(@ModelAttribute("lmCopy") LMCopy lmCopy, @PathVariable int id, BindingResult result,
            YukonUserContext userContext, FlashScope flash, ModelMap model, HttpServletRequest request,
            HttpServletResponse servletResponse) throws IOException {
        Map<String, String> json = new HashMap<>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupCopyUrl + id);
            ResponseEntity<? extends Object> response = copyGroup(userContext, request, url, lmCopy, HttpMethod.POST);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(lmCopy, "lmCopy");
                result = helper.populateBindingError(result, error, response);
                return bindAndForwardForCopy(lmCopy, result, model, servletResponse, id);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) response.getBody();
                int groupId = paoIdMap.get("groupId");
                json.put("groupId", Integer.toString(groupId));
                servletResponse.setContentType("application/json");
                JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "copy.success", lmCopy.getName()));
                return null;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            json.put("redirectUrl", setupRedirectLink);
            servletResponse.setContentType("application/json");
            JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
            return null;
        } catch (RestClientException ex) {
            log.error("Error while copying load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "copy.error", lmCopy.getName()));
            json.put("redirectUrl", setupRedirectLink);
            servletResponse.setContentType("application/json");
            JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
            return null;
        }
        return null;
    }

    /**
     * Load Group - Copy Popup functionality.
     */
    @GetMapping("/{id}/renderCopyLoadGroup")
    @CheckPermissionLevel(property = YukonRoleProperty.DR_SETUP_PERMISSION, level = HierarchyPermissionLevel.CREATE)
    public String renderCopyLoadGroup(@PathVariable int id, Integer routeId, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {

        PaoType loadGroupType = getPaoTypeForPaoId(id);
        LMCopy lmCopy = LMModelFactory.createLoadGroupCopy(loadGroupType);

        LiteYukonPAObject litePao = dbCache.getAllPaosMap().get(id);
        MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
        lmCopy.setName(messageSourceAccessor.getMessage("yukon.common.copyof", litePao.getPaoName()));
        model.addAttribute("lmCopy", lmCopy);
        if (loadGroupType.isLoadGroupSupportRoute()) {
            model.addAttribute("routes", cache.getAllRoutes());
            LiteYukonPAObject route = cache.getAllRoutes().stream()
                                                          .filter(pao -> pao.getLiteID() == routeId)
                                                          .findFirst()
                                                          .get();
            model.addAttribute("route", route);
        }

        model.addAttribute("loadGroupId", id);
        model.addAttribute("selectedSwitchType", loadGroupType);
        return "dr/setup/copyLoadGroup.jsp";
    }

    /**
     * Make a rest call for retrieving group 
     */
    private LoadGroupBase retrieveGroup(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        LoadGroupBase loadGroup = null;
        try {
            ResponseEntity<? extends Object> response =
                apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, LoadGroupBase.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                loadGroup = (LoadGroupBase) response.getBody();
                loadGroup.setId(id);
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving load group: " + ex.getMessage());
        }
        return loadGroup;
    }

    /**
     * Get the response for save
     */
    private ResponseEntity<? extends Object> saveGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LoadGroupBase loadGroup, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response =
            apiRequestHelper.callAPIForObject(userContext, request, webserverUrl, methodtype, Object.class, loadGroup);
        return response;
    }

    /**
     * Get the response for copy
     */
    private ResponseEntity<? extends Object> copyGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LMCopy lmCopy, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response =
            apiRequestHelper.callAPIForObject(userContext, request, webserverUrl, methodtype, Object.class, lmCopy);
        return response;
    }

    /**
     * Get the response for delete
     */
    private ResponseEntity<? extends Object> deleteGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LMDelete lmDelete) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
            webserverUrl, HttpMethod.DELETE, Object.class, lmDelete);
        return response;
    }

    private String bindAndForward(LoadGroupBase loadGroup, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("loadGroup", loadGroup);
        attrs.addFlashAttribute(bindingResultKey, result);
        if (loadGroup.getId() == null) {
            return "redirect:/dr/setup/loadGroup/create";
        }
        return "redirect:/dr/setup/loadGroup/" + loadGroup.getId() + "/edit";
    }

    private String bindAndForwardForCopy(LMCopy lmCopy, BindingResult result, ModelMap model,
            HttpServletResponse response, int id) {
        PaoType loadGroupType = getPaoTypeForPaoId(id);
        if (loadGroupType.isLoadGroupSupportRoute()) {
            model.addAttribute("routes", cache.getAllRoutes());
        }
        model.addAttribute("lmCopy", lmCopy);
        model.addAttribute("selectedSwitchType", loadGroupType);
        model.addAttribute("org.springframework.validation.BindingResult.loadGroup", result);
        model.addAttribute("loadGroupId", id);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "dr/setup/copyLoadGroup.jsp";
    }

    /**
     * Returns the PaoType based upon the Load Group Id
     */
    private PaoType getPaoTypeForPaoId(int loadGroupId) {
        LiteYukonPAObject litePao = dbCache.getAllPaosMap().get(loadGroupId);
        return litePao.getPaoType();
    }
}
