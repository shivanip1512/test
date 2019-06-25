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
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.dr.setup.LMModelFactory;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/setup/loadGroup")
public class LoadGroupSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.loadGroup.";
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupController.class);
    private static final List<PaoType> switchTypes =
            PaoType.getAllLMGroupTypes()
            .stream().sorted((p1, p2) -> p1.getDbString().compareTo(p2.getDbString()))
            .collect(Collectors.toList());

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private LoadGroupSetupControllerHelper controllerHelper;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = new LoadGroupBase();
        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
            controllerHelper.buildModelMap(loadGroup.getType(), model);
        }
        model.addAttribute("selectedSwitchType", loadGroup.getType());
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/loadGroupView.jsp";
    }
    
    @GetMapping("/create/{type}")
    public String create(ModelMap model, @PathVariable String type, @RequestParam String name) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = LMModelFactory.createLoadGroup(PaoType.valueOf(type));
        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
        } else {
            loadGroup.setName(name);
            loadGroup.setType(PaoType.valueOf(type));
        }
        controllerHelper.buildModelMap(PaoType.valueOf(type), model);
        model.addAttribute("loadGroup", loadGroup);
        List<PaoType> switchTypes = PaoType.getAllLMGroupTypes();
        model.addAttribute("switchTypes", switchTypes);
        model.addAttribute("selectedSwitchType", type);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.VIEW);
            LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
            if (loadGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return "redirect:/dr/setup/list";
            }
            model.addAttribute("selectedSwitchType", loadGroup.getType());
            model.addAttribute("loadGroup", loadGroup);
            return "dr/setup/loadGroup/loadGroupView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
            if (loadGroup == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return "redirect:/dr/setup/list";
            } else if (model.containsAttribute("loadGroup")) {
                loadGroup = (LoadGroupBase) model.get("loadGroup");
            }
            controllerHelper.buildModelMap(loadGroup.getType(), model);
            model.addAttribute("loadGroup", loadGroup);
            model.addAttribute("selectedSwitchType", loadGroup.getType());
            model.addAttribute("switchTypes", switchTypes);
            return "dr/setup/loadGroup/loadGroupView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

    }

    @PostMapping("/save")
    public String save(@ModelAttribute("loadGroup") LoadGroupBase loadGroup, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupSaveUrl);
            ResponseEntity<? extends Object> response =
                saveOrCopyGroup(userContext, request, url, loadGroup, HttpMethod.POST);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(loadGroup, "loadGroup");
                result = helper.populateBindingError(result, error, response);
                return bindAndForward(loadGroup, result, redirectAttributes);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                HashMap<String, Integer> paoIdMap = (HashMap<String, Integer>) response.getBody();
                int groupId = paoIdMap.get("paoId");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.saved"));
                return "redirect:/dr/setup/loadGroup/" + groupId;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        }

        catch (RestClientException ex) {
            log.error("Error creating load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error", ex.getMessage()));
            return "redirect:/dr/setup/list";
        }
        return null;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, @ModelAttribute LMDelete lmDelete, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id + "/delete");
            ResponseEntity<? extends Object> response = deleteGroup(userContext, request, url, lmDelete);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.deleted"));
                return "redirect:/dr/setup/list";
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error deleting load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.error", ex.getMessage()));
            return "redirect:/dr/setup/list";
        }

        return "dr/setup/list.jsp";
    }

    @PostMapping("/copy")
    public String copy(@ModelAttribute LoadGroupBase loadGroup, BindingResult result, YukonUserContext userContext,
            FlashScope flash, ModelMap model, HttpServletRequest request, HttpServletResponse servletResponse)
            throws IOException {

        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupCopyUrl);
            ResponseEntity<? extends Object> response =
                saveOrCopyGroup(userContext, request, url, loadGroup, HttpMethod.POST);

            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(loadGroup, "loadGroupBase");
                result = helper.populateBindingError(result, error, response);
                return bindAndForwardForCopy(loadGroup, result, model, servletResponse);
            }

            if (response.getStatusCode() == HttpStatus.OK) {
                Integer groupId = (int) response.getBody();
                Map<String, String> json = new HashMap<>();
                json.put("groupId", groupId.toString());
                servletResponse.setContentType("application/json");
                JsonUtils.getWriter().writeValue(servletResponse.getOutputStream(), json);
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.copied"));
                return null;
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/dr/setup/list";
        } catch (RestClientException ex) {
            log.error("Error creating load group: " + ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable(baseKey + "copy.error", ex.getMessage()));
            return "redirect:/dr/setup/list";
        }
        return null;
    }

    /**
     * Load Group - Copy Popup functionality.
     */
    @GetMapping("/{id}/rendercopyloadGroup")
    public String renderCopyloadGroup(@PathVariable int id, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request, FlashScope flash) {
        LoadGroupBase loadGroup = null;

        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
        } else {
            try {
                String url = helper.findWebServerUrl(request, userContext, ApiURL.drLoadGroupRetrieveUrl + id);

                loadGroup = retrieveGroup(userContext, request, id, url);
                MessageSourceAccessor messageSourceAccessor = messageResolver.getMessageSourceAccessor(userContext);
                loadGroup.setName(messageSourceAccessor.getMessage("yukon.common.copyof", loadGroup.getName()));
                model.addAttribute("loadGroup", loadGroup);

            } catch (ApiCommunicationException e) {
                log.error(e.getMessage());
                flash.setError(new YukonMessageSourceResolvable(communicationKey));
                return "redirect:/dr/setup/list";
            }

        }
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
            }

        } catch (RestClientException ex) {
            log.error("Error retrieving load group: " + ex.getMessage());
        }
        return loadGroup;
    }

    /* Get the response for save or copy */
    private ResponseEntity<? extends Object> saveOrCopyGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LoadGroupBase loadGroup, HttpMethod methodtype) throws RestClientException {
        ResponseEntity<? extends Object> response =
            apiRequestHelper.callAPIForObject(userContext, request, webserverUrl, methodtype, Object.class, loadGroup);
        return response;
    }

    /* Get the response for delete */
    private ResponseEntity<? extends Object> deleteGroup(YukonUserContext userContext, HttpServletRequest request,
            String webserverUrl, LMDelete lmDelete) throws RestClientException {
        ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request,
            webserverUrl, HttpMethod.DELETE, Object.class, lmDelete);
        return response;
    }

    private String bindAndForward(LoadGroupBase loadGroup, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("loadGroup", loadGroup);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.loadGroup", result);
        if (loadGroup.getId() == null) {
            return "redirect:/dr/setup/loadGroup/create/";
        }
        return "redirect:/dr/setup/loadGroup/" + loadGroup.getId() + "/edit";
    }

    private String bindAndForwardForCopy(LoadGroupBase loadGroup, BindingResult result, ModelMap model,
            HttpServletResponse response) {
        if (loadGroup.getId() == null) {
            return "redirect:/dr/setup/loadGroup/list";
        }
        model.addAttribute("loadGroup", loadGroup);
        model.addAttribute("org.springframework.validation.BindingResult.loadGroup", result);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "dr/setup/copyLoadGroup.jsp";
    }
}
