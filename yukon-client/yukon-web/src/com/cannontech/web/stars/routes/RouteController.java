package com.cannontech.web.stars.routes;

import java.util.ArrayList;
import java.util.List;

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
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.api.ApiRequestHelper;
import com.cannontech.web.api.ApiURL;
import com.cannontech.web.api.route.model.RouteBaseModel;
import com.cannontech.web.api.validation.ApiCommunicationException;
import com.cannontech.web.api.validation.ApiControllerHelper;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.VIEW)
@RequestMapping("/device/routes/")
public class RouteController {
    @Autowired private ApiControllerHelper helper;
    @Autowired private ApiRequestHelper apiRequestHelper;
    @Autowired private RouteValidator<? extends RouteBaseModel<?>> routeValidator;
    @Autowired private ServerDatabaseCache dbCache;

    private static final String communicationKey = "yukon.exception.apiCommunicationException.communicationError";
    private static final String redirectListPageLink = "redirect:/stars/device/routes/list";
    private static final String baseKey = "yukon.web.modules.operator.routes.";
    private static final Logger log = YukonLogManager.getLogger(RouteController.class);

    @GetMapping("list")
    public String list(ModelMap model, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {
        ResponseEntity<List<? extends Object>> response = null;
        List<DeviceBaseModel> nonCCUAndMacroRoutes = new ArrayList<DeviceBaseModel>();
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl);
            response = apiRequestHelper.callAPIForList(userContext, request, url, Object.class, HttpMethod.GET);
            if (response.getStatusCode() == HttpStatus.OK) {
                List<DeviceBaseModel> allRoutes = (List<DeviceBaseModel>) response.getBody();
                for (int index = 0; index < allRoutes.size(); index++) {
                    ObjectMapper mapper = new ObjectMapper();
                    DeviceBaseModel baseModel = mapper.readValue(mapper.writeValueAsString(allRoutes.get(index)),
                            DeviceBaseModel.class);
                    // Devices with type CCU710A,CCU711,CCU721 and Routes with type ROUTE_CCU are removed in separate conditions
                    // below
                    if (baseModel != null && baseModel.getDeviceType() != null && (!baseModel.getDeviceType().isCcu())
                            && baseModel.getDeviceType() != PaoType.ROUTE_CCU) {
                        nonCCUAndMacroRoutes.add(baseModel);
                    }
                }
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            return redirectListPageLink;
        } catch (JsonProcessingException ex) {
            log.error("Error retrieving details: " + ex.getMessage());
            return redirectListPageLink;
        }
        model.addAttribute("nonCCUAndMacroRoutes", nonCCUAndMacroRoutes);
        return "/routes/list.jsp";
    }

    @GetMapping("create")
    public String create(ModelMap model) {
        model.addAttribute("mode", PageEditMode.CREATE);
        RouteBaseModel<RouteBase> communicationRoute = new RouteBaseModel<RouteBase>();
        if (model.containsAttribute("communicationRoute")) {
            communicationRoute = (RouteBaseModel<RouteBase>) model.getAttribute("communicationRoute");
        } else {
            communicationRoute.setDefaultRoute(true);
        }
        model.addAttribute("communicationRoute", communicationRoute);
        return "/routes/view.jsp";
    }

    @PostMapping("save")
    public String save(@ModelAttribute("communicationRoute") RouteBaseModel<?> communicationRoute, BindingResult result,
            YukonUserContext userContext, FlashScope flash, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            routeValidator.validate(communicationRoute, result);
            if (result.hasErrors()) {
                return bindAndForward(communicationRoute, result, redirectAttributes);
            }
            ResponseEntity<? extends Object> response = null;
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl);
            if (communicationRoute.getDeviceId() == null) {
                response = apiRequestHelper.callAPIForObject(userContext, request, url,
                        HttpMethod.POST, RouteBaseModel.class, communicationRoute);
            } else {
                url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl) + "/" + communicationRoute.getDeviceId();
                response = apiRequestHelper.callAPIForObject(userContext, request, url,
                        HttpMethod.PATCH, RouteBaseModel.class, communicationRoute);
            }
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                RouteBaseModel<?> routeCreated = (RouteBaseModel<?>) response.getBody();
                flash.setConfirm(
                        new YukonMessageSourceResolvable("yukon.common.save.success", communicationRoute.getDeviceName()));
                return "redirect:/stars/device/routes/" + routeCreated.getDeviceId();
            }

        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        } catch (RestClientException ex) {
            log.error("Error creating route: {}. Error: {}", communicationRoute.getDeviceName(), ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.save.error", communicationRoute.getDeviceName(), ex.getMessage()));
            return redirectListPageLink;
        }
        return null;
    }
    
    private String bindAndForward(RouteBaseModel<?> communicationRoute, BindingResult result, RedirectAttributes attrs) {
        attrs.addFlashAttribute("communicationRoute", communicationRoute);
        attrs.addFlashAttribute("org.springframework.validation.BindingResult.communicationRoute", result);
        return "redirect:/stars/device/routes/create";
    }

    @GetMapping("/{id}")
    public String view(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            model.addAttribute("mode", PageEditMode.VIEW);
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl+ "/" + id);
            RouteBaseModel<?> communicationRoute = retrieveCommunicationRoute(userContext, request, id, url);
            
            if (communicationRoute == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "retrieve.error"));
                return redirectListPageLink;
            }
            model.addAttribute("communicationRoute", communicationRoute);
            return "/routes/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }
    
    @GetMapping("/{id}/edit")
    public String edit(ModelMap model, YukonUserContext userContext, @PathVariable int id, FlashScope flash,
            HttpServletRequest request) {
        try {
            String url = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl + "/" + id);
            model.addAttribute("mode", PageEditMode.EDIT);
            RouteBaseModel<?> communicationRoute = retrieveCommunicationRoute(userContext, request, id, url);
            if (communicationRoute == null) {
                flash.setError(new YukonMessageSourceResolvable(baseKey + "communicationRoute.retrieve.error"));
                return redirectListPageLink;
            } else if (model.containsAttribute("communicationRoute")) {
                communicationRoute = (RouteBaseModel<?>) model.get("communicationRoute");
                communicationRoute.setDeviceId(id);
            }
            
            model.addAttribute("communicationRoute", communicationRoute);
            return "/routes/view.jsp";
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return redirectListPageLink;
        }
    }
    
    @DeleteMapping("/{id}/delete")
    public String delete(@PathVariable int id, YukonUserContext userContext, FlashScope flash, HttpServletRequest request) {

        try {
            String deleteUrl = helper.findWebServerUrl(request, userContext, ApiURL.retrieveAllRoutesUrl + "/" + id);
            String routeName = dbCache.getAllPaosMap().get(id).getPaoName();
            ResponseEntity<? extends Object> deleteResponse = apiRequestHelper.callAPIForObject(userContext,
                    request,
                    deleteUrl,
                    HttpMethod.DELETE,
                    Object.class,
                    Integer.class);

            if (deleteResponse.getStatusCode() == HttpStatus.OK) {
                flash.setConfirm(new YukonMessageSourceResolvable("yukon.common.delete.success", routeName));
                return redirectListPageLink;
            }
        } catch (ApiCommunicationException e) {
            log.error(e.getMessage());
            flash.setError(new YukonMessageSourceResolvable(communicationKey));
            return "redirect:" + "/stars/device/routes/" + id;
        } catch (RestClientException ex) {
            String routeName = dbCache.getAllPaosMap().get(id).getPaoName();
            log.error("Error deleting route: {}. Error: {}", routeName, ex.getMessage());
            flash.setError(new YukonMessageSourceResolvable("yukon.web.api.delete.error", routeName, ex.getMessage()));
            return "redirect:" + "/stars/device/routes/" + id;
        }
        return redirectListPageLink;
    }

    private RouteBaseModel<?> retrieveCommunicationRoute(YukonUserContext userContext, HttpServletRequest request, int id,
            String url) {
        RouteBaseModel<?> communicationRoute = null;
        try {
            ResponseEntity<? extends Object> response = apiRequestHelper.callAPIForObject(userContext, request, url,
                    HttpMethod.GET, RouteBaseModel.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                communicationRoute = (RouteBaseModel<?>) response.getBody();
                communicationRoute.setDeviceId(id);
            }
        } catch (RestClientException ex) {
            log.error("Error retrieving route : " + ex.getMessage());
        }
        return communicationRoute;
    }

}
