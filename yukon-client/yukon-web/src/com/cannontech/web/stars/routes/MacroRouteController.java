package com.cannontech.web.stars.routes;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/routes/macroRoutes/")

public class MacroRouteController {
    
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ServerDatabaseCache dbCache;

    private static final Logger log = YukonLogManager.getLogger(MacroRouteController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.operator.routes.macroRoutes.";
    private static final String redirectListPageLink = "redirect:/stars/device/routes/list";

    @GetMapping("create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        MacroRouteModel<?> macroRouteModel = null;
        
        if (model.containsKey("macroRouteModel")) {
            macroRouteModel = (MacroRouteModel) model.getAttribute("macroRouteModel");
        } else {
            macroRouteModel = new MacroRouteModel<>();
        }
        model.addAttribute("macroRouteModel", macroRouteModel);
        List<Integer> selectedIds = macroRouteModel.getRouteList().stream()
                                                                  .map(route -> route.getRouteId())
                                                                  .collect(Collectors.toList());
        model.addAttribute("selectedIds", selectedIds);
        return "/routes/macroRouteView.jsp";
    }
    
    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl+ "/" + id);
            MacroRouteModel<?> macroRoute = retrieveMacroRoute(userContext, request, id, url);
            
            if (macroRoute == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            }
            model.addAttribute("macroRoute", macroRoute);
            return "/routes/macroRouteView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }

    private MacroRouteModel<?> retrieveMacroRoute(YukonUserContext userContext, HttpServletRequest request, int id, String url) {
        MacroRouteModel<?> macroRoute = null;
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, MacroRouteModel.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                macroRoute = (MacroRouteModel<?>) response.getBody();
                macroRoute.setDeviceId(id);
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving route : " + ex.getMessage());
        }
        return macroRoute;
    }
    
    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        String macroRoute = dbCache.getAllPaosMap().get(id).getPaoName();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext,
                    request, url, HttpMethod.DELETE, Object.class, macroRoute);

            if (response.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", macroRoute));
                return redirectListPageLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:/stars/device/routes/macroRoutes/" + id;
        } catch (RestClientException ex) {
            log.error("Error deleting route: {}. Error: {}", macroRoute, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", macroRoute, ex.getMessage()));
            return "redirect:/stars/device/routes/macroRoutes/" + id;
        }
        return redirectListPageLink;
    }
    
    @PostMapping("/save")
    public String save(@ModelAttribute("macroRouteModel") MacroRouteModel<?> macroRouteModel, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        return null;
    }

}
