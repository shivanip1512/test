package com.cannontech.web.routeLocate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
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
import com.cannontech.web.bulk.BulkControllerBase;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.util.JsonView;

@CheckRoleProperty(YukonRoleProperty.LOCATE_ROUTE)
public class RouteLocateController extends BulkControllerBase {

    private PaoDao paoDao = null;
    private YukonUserContextMessageSourceResolver messageSourceResolver = null;
    private RouteLocateExecutor routeLocateExecutor = null;
    private DeviceUpdateService deviceUpdateService = null;
    private DeviceDao deviceDao = null;
    
    private Logger log = YukonLogManager.getLogger(RouteLocateController.class);
    
    // HOME
    @RequestMapping(value = "/routeLocate/home")
    public ModelAndView home(HttpServletRequest request, HttpServletResponse response) throws Exception, ServletException {
        
        ModelAndView mav = new ModelAndView("routeLocate/routeLocateHome.jsp");
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
        mav.addObject("deviceCollection", deviceCollection);
        
        // ROUTE OPTIONS
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        Map<Integer, String> routeOptions = new LinkedHashMap<Integer, String>(routes.length);
        for (LiteYukonPAObject route : routes) {
            routeOptions.put(route.getLiteID(), route.getPaoName());
        }
        mav.addObject("routeOptions", routeOptions);
        
        // ERROR MSG
        String errorMsg = ServletRequestUtils.getStringParameter(request, "errorMsg", null);
        mav.addObject("errorMsg", errorMsg);
        
        // AUTO UPDATE ROUTE OPTION
        Boolean autoUpdateRoute = ServletRequestUtils.getBooleanParameter(request, "autoUpdateRoute", false);
        mav.addObject("autoUpdateRoute", autoUpdateRoute);
        
        // ALL RESULTS
        List<RouteLocateResult> routeLocateResultsList = new ArrayList<RouteLocateResult>();
        routeLocateResultsList.addAll(new ReverseList<RouteLocateResult>(routeLocateExecutor.getPending()));
        routeLocateResultsList.addAll(new ReverseList<RouteLocateResult>(routeLocateExecutor.getCompleted()));
        mav.addObject("routeLocateResultsList", routeLocateResultsList);
        
        return mav;
    }
    
    // EXECUTE
    @RequestMapping(value = "/routeLocate/executeRouteLocation")
    public ModelAndView executeRouteLocation(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
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
            
            ModelAndView mav = new ModelAndView("redirect:/spring/bulk/routeLocate/home");
            for (String param : deviceCollection.getCollectionParameters().keySet()) {
                mav.addObject(param, deviceCollection.getCollectionParameters().get(param));
            }
            
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            String errorMsg = messageSourceAccessor.getMessage("yukon.web.modules.amr.routeLocateHome.noRoutesSelectedError");
            mav.addObject("errorMsg", errorMsg);
            
            mav.addObject("autoUpdateRoute", autoUpdateRoute);
            
            return mav;
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
        
        
        ModelAndView mav = new ModelAndView("redirect:/spring/bulk/routeLocate/results");
        mav.addObject("resultId", resultId);
        
        return mav;
    }
    
    
    // CANCELS A CURRENTLY RUNNING ROUTE LOCATE
    @RequestMapping(value = "/routeLocate/cancelCommands")
    public ModelAndView cancelCommands(String resultId, YukonUserContext userContext) {
        
        ModelAndView mav = new ModelAndView(new JsonView());
        routeLocateExecutor.cancelExecution(resultId,userContext.getYukonUser());
        return mav;
  
    }
  
    // RESULTS
    @RequestMapping(value = "/routeLocate/results")
    public ModelAndView results(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("routeLocate/routeLocateResults.jsp");
        
        // RESULTID
        String resultId = ServletRequestUtils.getRequiredStringParameter(request, "resultId");
        mav.addObject("resultId", resultId);
        
        
        
        RouteLocateResult result = routeLocateExecutor.getResult(resultId);
        mav.addObject("result", result);
        
        long deviceCount = result.getDeviceCollection().getDeviceCount();
        mav.addObject("deviceCount", deviceCount);
        return mav;
    }
    
    
    // VIEW ROUTES
    @RequestMapping(value = "/routeLocate/routeSettings")
    public ModelAndView routeSettings(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
        ModelAndView mav = new ModelAndView("routeLocate/routeLocateRouteSettings.jsp");
        
        // RESULT
        String resultId = ServletRequestUtils.getRequiredStringParameter(request, "resultId");
        mav.addObject("resultId", resultId);
        
        RouteLocateResult result = routeLocateExecutor.getResult(resultId);
        mav.addObject("result", result);

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
        
        mav.addObject("foundRoutes", foundRoutes);
        mav.addObject("notFoundRoutes", notFoundRoutes);
        
        return mav;
    }
    
    
    // SET SINGLE ROUTE
    @RequestMapping(value = "/routeLocate/setRoute")
    public ModelAndView setRoute(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        
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
        
        // mav
        ModelAndView mav = new ModelAndView("routeLocate/routeLocateRouteUpdateInfo.jsp");
        mav.addObject("oldRouteName", drl.getInitialRouteName());
        mav.addObject("newRouteName", drl.getRouteName());
        
        return mav;
    }
   
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Required
    public void setRouteLocateExecutor(RouteLocateExecutor routeLocateExecutor) {
        this.routeLocateExecutor = routeLocateExecutor;
    }
    
    @Required
    public void setDeviceUpdateService(DeviceUpdateService deviceUpdateService) {
        this.deviceUpdateService = deviceUpdateService;
    }
    
    @Required
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
}
