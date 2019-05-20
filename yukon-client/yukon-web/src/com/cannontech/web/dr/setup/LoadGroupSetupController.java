package com.cannontech.web.dr.setup;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.dr.setup.LoadGroupBase;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.DEMAND_RESPONSE)
@RequestMapping("/setup/loadGroup")
public class LoadGroupSetupController {
    
    private static final String baseKey = "yukon.web.modules.dr.setup.loadGroup.";
    private static final Logger log = YukonLogManager.getLogger(LoadGroupSetupController.class);
    
    @Autowired private RestTemplate drRestTemplate;
    @Autowired private SetupControllerHelper helper;

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
    public String view(ModelMap model, @PathVariable int id, FlashScope flash) {
        model.addAttribute("mode", PageEditMode.VIEW);
        LoadGroupBase loadGroup = retrieveGroup(id);
        if (loadGroup == null) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
            return "redirect:/dr/setup/list";
        }
        model.addAttribute("loadGroup", loadGroup);
        return "dr/setup/loadGroup/view.jsp";
    }

    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, @PathVariable int id, FlashScope flash) {
        model.addAttribute("mode", PageEditMode.EDIT);
        LoadGroupBase loadGroup = retrieveGroup(id);
        if (loadGroup == null) {
            flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
            return "redirect:/dr/setup/list";
        }
        model.addAttribute("loadGroup", loadGroup);
        List<PaoType> switchTypes = Lists.newArrayList(PaoType.LM_GROUP_METER_DISCONNECT);
        model.addAttribute("switchTypes", switchTypes);
        return "dr/setup/loadGroup/view.jsp";
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute LoadGroupBase loadGroup, BindingResult result, FlashScope flash, RedirectAttributes redirectAttributes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoadGroupBase> requestEntity = new HttpEntity<LoadGroupBase>(loadGroup, headers);
        String url = "http://localhost:8080/yukon/dr/api/setup/loadGroup/save";

        try {
            ResponseEntity<Object> response = drRestTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
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
    
    /* Make a rest call for retrieving group */
    private LoadGroupBase retrieveGroup(int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = "http://localhost:8080/yukon/dr/api/setup/loadGroup/" + id;
        LoadGroupBase loadGroup = null;
        try {
            ResponseEntity<LoadGroupBase> response = drRestTemplate.getForEntity(url, LoadGroupBase.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                loadGroup = response.getBody();
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
