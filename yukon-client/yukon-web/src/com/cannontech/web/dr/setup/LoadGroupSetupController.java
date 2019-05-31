package com.cannontech.web.dr.setup;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.APIRequestHelper;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/setup/loadGroup")
public class LoadGroupSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.loadGroup.";
    private static final String drLoadGroupBaseUrl = "/setup/loadGroup/";
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupController.class);
    
    @Autowired private ApiControllerHelper helper;
    @Autowired private APIRequestHelper apiRequestHelper;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        LoadGroupBase loadGroup = new LoadGroupBase();
        if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
        }
        model.addAttribute("loadGroup", loadGroup);
        List<PaoType> switchTypes = Lists.newArrayList(PaoType.LM_GROUP_METER_DISCONNECT);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.VIEW);
        String url = helper.getApiURL(request, request.getPathInfo());
        LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
        if (loadGroup == null) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
            return "redirect:/dr/setup/list";
        }
        model.addAttribute("loadGroup", loadGroup);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash, HttpServletRequest request) {
        model.addAttribute("mode", PageEditMode.EDIT);
        String url = helper.getApiURL(request, drLoadGroupBaseUrl + id);
        LoadGroupBase loadGroup = retrieveGroup(userContext, request, id, url);
        if (loadGroup == null) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
            return "redirect:/dr/setup/list";
        } else if (model.containsAttribute("loadGroup")) {
            loadGroup = (LoadGroupBase) model.get("loadGroup");
        }
        model.addAttribute("loadGroup", loadGroup);
        List<PaoType> switchTypes = Lists.newArrayList(PaoType.LM_GROUP_METER_DISCONNECT);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/view.jsp";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute LoadGroupBase loadGroup, BindingResult result, YukonUserContext userContext,
            FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {

        String url = helper.getApiURL(request, request.getPathInfo());
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.POST, Object.class, loadGroup);
            if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                BindException error = new BindException(loadGroup, "loadGroupBase");
                helper.populateBindingError(result, error, response);
                return bindAndForward(loadGroup, result, redirectAttributes);
            }
            if (response.getStatusCode() == HttpStatus.OK) {
                int groupId = (int) response.getBody();
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "info.saved"));
                return "redirect:/dr/setup/loadGroup/" + groupId;
            }
        } catch (RestClientException ex) {
            log.error("Error creating load group " + ex);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "save.error"));
            return "redirect:/dr/setup/list";
        }
        return null;
    }
    
    /**
     * Make a rest call for retrieving group 
     */
    private LoadGroupBase retrieveGroup(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        LoadGroupBase loadGroup = null;
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url, HttpMethod.GET, LoadGroupBase.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                loadGroup = (LoadGroupBase) response.getBody();
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving load group " + ex);
        }
        return loadGroup;
    }

    
    private String bindAndForward(LoadGroupBase loadGroup, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("loadGroup", loadGroup);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.loadGroup", result);
        if (loadGroup.getId() == null) {
            return "redirect:/dr/setup/loadGroup/create";
        }
        return "redirect:/dr/setup/loadGroup/" + loadGroup.getId() + "/edit";
    }
}
