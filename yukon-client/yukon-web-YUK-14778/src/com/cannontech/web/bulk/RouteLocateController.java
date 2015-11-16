package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.routeLocate.DeviceRouteLocation;
import com.cannontech.common.device.routeLocate.RouteLocateExecutor;
import com.cannontech.common.device.routeLocate.RouteLocateResult;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.ReverseList;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;

@CheckRoleProperty(YukonRoleProperty.LOCATE_ROUTE)
@Controller
@RequestMapping("/routeLocate/*")
public class RouteLocateController {

    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RouteLocateExecutor routeLocateExecutor;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    
    private static final Logger log = YukonLogManager.getLogger(RouteLocateController.class);
    
    // HOME
    @RequestMapping("home")
    public String home(ModelMap model, HttpServletRequest request) throws ServletException {
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        
        // ROUTE OPTIONS
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        Map<Integer, String> routeOptions = new LinkedHashMap<Integer, String>(routes.length);
        for (LiteYukonPAObject route : routes) {
            routeOptions.put(route.getLiteID(), route.getPaoName());
        }
        model.addAttribute("routeOptions", routeOptions);
        
        // ERROR MSG
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        model.addAttribute("errorMsg", errorMsg);
        
        // AUTO UPDATE ROUTE OPTION
        Boolean autoUpdateRoute = ServletRequestUtils.getBooleanParameter(request, "autoUpdateRoute", false);
        model.addAttribute("autoUpdateRoute", autoUpdateRoute);
        
        // ALL RESULTS
        List<RouteLocateResult> routeLocateResultsList = new ArrayList<RouteLocateResult>();
        routeLocateResultsList.addAll(new ReverseList<RouteLocateResult>(routeLocateExecutor.getPending()));
        routeLocateResultsList.addAll(new ReverseList<RouteLocateResult>(routeLocateExecutor.getCompleted()));
        model.addAttribute("routeLocateResultsList", routeLocateResultsList);
        
        return "routeLocate/routeLocateHome.jsp";
    }
    
    // EXECUTE
    @RequestMapping("executeRouteLocation")
    public String executeRouteLocation(ModelMap model, HttpServletRequest request) throws ServletException {
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        
        int[] routesSelect = ServletRequestUtils.getIntParameters(request, "routesSelect");
        List<Integer> selectedRouteIds = new ArrayList<Integer>();
        for (int routeId : routesSelect) {
            selectedRouteIds.add(routeId);
        }
        
        // AUTO UPDATE ROUTE OPTION
        Boolean autoUpdateRoute = ServletRequestUtils.getBooleanParameter(request, "autoUpdateRoute", false);
        
        // NO ROUTES SELECTED
        if (selectedRouteIds.size() < 1) {
            
            for (String param : deviceCollection.getCollectionParameters().keySet()) {
                model.addAttribute(param, deviceCollection.getCollectionParameters().get(param));
            }
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.tools.bulk.routeLocateHome.noRoutesSelectedError");
            model.addAttribute("errorMsg", errorMsg);
            
            model.addAttribute("autoUpdateRoute", autoUpdateRoute);
            
            return "redirect:/bulk/routeLocate/home";
        }
        
        
        // EXECUTOR CALLBACK
        SimpleCallback<RouteLocateResult> executorCallback = new SimpleCallback<RouteLocateResult>() {
            @Override
            public void handle(RouteLocateResult item) throws Exception {
                // maybe send an alert or something cool
                log.debug("Inside routeLocateExecutor callback.");
            }
        };
        
        // RUN EXECUTOR
        String resultId = routeLocateExecutor.execute(deviceCollection, selectedRouteIds, autoUpdateRoute, executorCallback, userContext.getYukonUser());
        
        
        model.addAttribute("resultId", resultId);
        return "redirect:/bulk/routeLocate/results";
    }
    
    // CANCELS A CURRENTLY RUNNING ROUTE LOCATE
    @RequestMapping("cancelCommands")
    public View cancelCommands(String resultId, YukonUserContext userContext) {
        
        routeLocateExecutor.cancelExecution(resultId,userContext.getYukonUser());
        return new JsonView();
    }
  
    // RESULTS
    @RequestMapping("results")
    public String results(ModelMap model, HttpServletRequest request) throws ServletException {
        
        // RESULTID
        String resultId = ServletRequestUtils.getRequiredStringParameter(request, "resultId");
        model.addAttribute("resultId", resultId);
        
        RouteLocateResult result = routeLocateExecutor.getResult(resultId);
        model.addAttribute("result", result);
        model.addAttribute("deviceCollection", result.getDeviceCollection());
        
        long deviceCount = result.getDeviceCollection().getDeviceCount();
        model.addAttribute("deviceCount", deviceCount);
        
        return "routeLocate/routeLocateResults.jsp";
    }
    
    // VIEW ROUTES
    @RequestMapping("routeSettings")
    public String routeSettings(ModelMap model, HttpServletRequest request) throws ServletException {
        
        // RESULT
        String resultId = ServletRequestUtils.getRequiredStringParameter(request, "resultId");
        model.addAttribute("resultId", resultId);
        
        RouteLocateResult result = routeLocateExecutor.getResult(resultId);
        model.addAttribute("result", result);
        model.addAttribute("deviceCollection", result.getDeviceCollection());

        // FOUND/NOT FOUND LISTS
        List<DeviceRouteLocation> foundRoutes = new ArrayList<DeviceRouteLocation>();
        List<DeviceRouteLocation> notFoundRoutes = new ArrayList<DeviceRouteLocation>();
        for (Integer drlId : result.getCompletedDeviceRouteLocations().keySet()) {
            
            DeviceRouteLocation drl = result.getCompletedDeviceRouteLocations().get(drlId);
            
            if (drl.isLocated()) {
                foundRoutes.add(drl);
            } else {
                notFoundRoutes.add(drl);
            }
        }
        
        model.addAttribute("foundRoutes", foundRoutes);
        model.addAttribute("notFoundRoutes", notFoundRoutes);
        
        return "routeLocate/routeLocateRouteSettings.jsp";
    }
    
    // SET SINGLE ROUTE
    @RequestMapping("setRoute")
    public String setRoute(ModelMap model, HttpServletRequest request) throws ServletException {
        
        // get info
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        int routeId = ServletRequestUtils.getRequiredIntParameter(request, "routeId");
        String resultId = ServletRequestUtils.getRequiredStringParameter(request, "resultId");
        int deviceRouteLocationId = ServletRequestUtils.getRequiredIntParameter(request, "deviceRouteLocationId");
        
        // update device
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        deviceUpdateService.changeRoute(device, routeId);
        
        // update DeviceRouteLocation
        RouteLocateResult result = routeLocateExecutor.getResult(resultId);
        DeviceRouteLocation drl = result.getCompletedDeviceRouteLocations().get(deviceRouteLocationId);
        drl.setRouteUpdated(true);
        
        model.addAttribute("oldRouteName", drl.getInitialRouteName());
        model.addAttribute("newRouteName", drl.getRouteName());
        
        return "routeLocate/routeLocateRouteUpdateInfo.jsp";
    }
    
}