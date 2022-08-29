package com.cannontech.web.stars.routes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.macroRoute.model.MacroRouteList;
import com.cannontech.web.api.macroRoute.model.MacroRouteModel;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.fasterxml.jackson.core.type.TypeReference;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/routes/macroRoutes/")
public class MacroRouteController {

    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private ServerDatabaseCache dbCache;
    @Autowired private MacroRouteValidator macroRouteValidator;

    private static final Logger log = YukonLogManager.getLogger(MacroRouteController.class);
    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String baseKey = "yukon.web.modules.operator.macroRoutes.";
    private static final String redirectListPageLink = "redirect:/stars/device/routes/list";

    private static final TypeReference<List<MacroRouteList>> routeListTargetType = new TypeReference<List<MacroRouteList>>() {
    };

    @GetMapping("create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        MacroRouteModel<?> macroRouteModel = null;

        if (model.containsKey("macroRouteModel")) {
            macroRouteModel = (MacroRouteModel<?>) model.getAttribute("macroRouteModel");
        } else {
            macroRouteModel = new MacroRouteModel<>();
        }
        model.addAttribute("macroRouteModel", macroRouteModel);
        List<Integer> selectedRouteIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(macroRouteModel.getRouteList())) {
            selectedRouteIds = macroRouteModel.getRouteList().stream().map(route -> route.getRouteId())
                    .collect(Collectors.toList());
        }
        model.addAttribute("selectedRouteIds", selectedRouteIds);
        return "/routes/macroRouteView.jsp";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl + "/" + id);
            MacroRouteModel<?> macroRoute = retrieveMacroRoute(userContext, request, id, url);

            if (macroRoute == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            }
            model.addAttribute("macroRouteModel", macroRoute);
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
    
    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl + "/" + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            MacroRouteModel<?> macroRouteModel = retrieveMacroRoute(userContext, request, id, url);
            if (macroRouteModel == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            } else if (model.containsAttribute("macroRouteModel")) {
                macroRouteModel = (MacroRouteModel<?>) model.get("macroRouteModel");
                macroRouteModel.setDeviceId(id);
            }
            model.addAttribute("selectedRouteIds", getSelectedRouteIds(macroRouteModel.getRouteList()));
            model.addAttribute("macroRouteModel", macroRouteModel);
            return "/routes/macroRouteView.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }
    private List<Integer> getSelectedRouteIds(List<MacroRouteList> allRoutes) {
        List<Integer> selectedRouteIds = Lists.newArrayList();
        CollectionUtils.emptyIfNull(allRoutes).stream().forEach(route -> {
            selectedRouteIds.add(route.getRouteId());
        });
        return selectedRouteIds;
    }

    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        String macroRoute = dbCache.getAllPaosMap().get(id).getPaoName();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl + "/" + id);
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext,
                    request, url, HttpMethod.DELETE, Object.class, Integer.class);

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
    public String save(@ModelAttribute("macroRouteModel") MacroRouteModel<?> macroRouteModel,
            @RequestParam("routeListJsonString") String routeListJsonSting, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request)
            throws IOException {
        List<MacroRouteList> routes = Lists.newArrayList();
        if (StringUtils.isNotBlank(routeListJsonSting)) {
            routes = JsonUtils.fromJson(routeListJsonSting, routeListTargetType);
        }
        macroRouteModel.setRouteList(routes);

        try {
            macroRouteValidator.validate(macroRouteModel, result);
            if (result.hasErrors()) {
                if (result.hasGlobalErrors()) {
                    List<String> errors = result.getGlobalErrors().stream()
                                                                  .map(obj -> obj.getCode())
                                                                  .collect(Collectors.toList());
                    flash.setError(YukonMessageSourceResolvable.createMultipleCodes(String.join(",", errors)));
                }
                return bindAndForward(macroRouteModel, result, redirectAttributes);
            }
            String url = null;
            ResponseEntity<? extends Object> apiResponse = null;
            if (macroRouteModel.getDeviceId() == null) {
                url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl);
                apiResponse = apiRequestHelper.callAPIForObject(userContext, request, url,
                        HttpMethod.POST, Object.class, macroRouteModel);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.macroRoutesUrl) + "/" + macroRouteModel.getDeviceId();
                apiResponse = apiRequestHelper.callAPIForObject(userContext, request, url,
                        HttpMethod.PATCH, Object.class, macroRouteModel);
            }

            if (apiResponse.getStatusCode() == HttpStatus.OK || apiResponse.getStatusCode() == HttpStatus.CREATED) {
                HashMap<String, Object> responseMap = (HashMap<String, Object>) apiResponse.getBody();
                flash.setConfirm(
                        new YukonMessageSourceResolvable("yukon.common.save.success", responseMap.get("deviceName")));
                return "redirect:/stars/device/routes/macroRoutes/" + responseMap.get("deviceId");
            }

            if (apiResponse.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                flash.setError(new YukonMessageSourceResolvable("yukon.web.error.genericMainMessage"));
                log.error("Error saving macro route");
                log.error(JsonUtils.beautifyJson(JsonUtils.toJson(apiResponse.getBody())));
                return bindAndForward(macroRouteModel, result, redirectAttributes);
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error creating macro route: {}. Error: {}", macroRouteModel.getDeviceName(), ex.getMessage());
            log.error(ex.getStackTrace());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", macroRouteModel.getDeviceName(),
                    ex.getMessage()));
            return redirectListPageLink;
        }
        return null;
    }

    private String bindAndForward(MacroRouteModel macroRouteModel, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("macroRouteModel", macroRouteModel);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.macroRouteModel", result);
        if (macroRouteModel.getDeviceId() == null) {
            return "redirect:/stars/device/routes/macroRoutes/create";
        }
        return "redirect:/stars/device/routes/macroRoutes/" + macroRouteModel.getDeviceId() + "/edit";
    }

}
