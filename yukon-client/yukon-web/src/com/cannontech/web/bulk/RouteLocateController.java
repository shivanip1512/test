package com.cannontech.web.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.routeLocate.DeviceRouteLocation;
import com.cannontech.common.device.routeLocate.RouteLocationService;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yukon.IDatabaseCache;


@CheckRoleProperty(YukonRoleProperty.LOCATE_ROUTE)
@Controller
@RequestMapping("/routeLocate/*")
public class RouteLocateController {

    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RouteLocationService routeLocationService;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private DeviceGroupService deviceGroupService;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;
    @Autowired private AlertService alertService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private IDatabaseCache databaseCache;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    
    private final static String baseKey = "yukon.web.modules.tools.bulk.routeLocateHome.";
        
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                       @RequestParam(value = "commandString", required = false, defaultValue = "ping") String commandString, 
                       @RequestParam(value = "commandSelectValue", required = false, defaultValue = "ping") String commandSelectValue, 
                       String errorMsg, boolean autoUpdateRoute) throws ServletException {
        setupModel(model, request, userContext, commandString, commandSelectValue, errorMsg, autoUpdateRoute);
        model.addAttribute("action", CollectionAction.LOCATE_ROUTE);
        model.addAttribute("actionInputs", "/WEB-INF/pages/bulk/routeLocate/routeLocateHome.jsp");
        return "../collectionActions/collectionActionsHome.jsp";
    }
    
    @RequestMapping(value = "routeLocateInputs", method = RequestMethod.GET)
    public String routeLocateInputs(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                       @RequestParam(value = "commandString", required = false, defaultValue = "ping") String commandString, 
                       @RequestParam(value = "commandSelectValue", required = false, defaultValue = "ping") String commandSelectValue, 
                       String errorMsg, boolean autoUpdateRoute) throws ServletException {
        setupModel(model, request, userContext, commandString, commandSelectValue, errorMsg, autoUpdateRoute);
        return "routeLocate/routeLocateHome.jsp";
    }
    
    private void setupModel(ModelMap model, HttpServletRequest request, YukonUserContext userContext, 
                            String commandString, String commandSelectValue, String errorMsg, boolean autoUpdateRoute) throws ServletException {
        DeviceCollection deviceCollection = deviceCollectionFactory.createDeviceCollection(request);
        model.addAttribute("deviceCollection", deviceCollection);
        List<LiteCommand> commands = deviceGroupService.getDeviceCommands(deviceCollection.getDeviceList(), userContext.getYukonUser());
        model.addAttribute("commands", commands);
        model.addAttribute("commandSelectValue", commandSelectValue);
        model.addAttribute("commandString", commandString);
        LiteYukonPAObject[] routes = paoDao.getAllLiteRoutes();
        Map<Integer, String> routeOptions = new LinkedHashMap<>(routes.length);
        for (LiteYukonPAObject route : routes) {
            routeOptions.put(route.getLiteID(), route.getPaoName());
        }
        model.addAttribute("routeOptions", routeOptions);
        model.addAttribute("errorMsg", errorMsg);
        model.addAttribute("autoUpdateRoute", autoUpdateRoute);
    }
    
    // EXECUTE
    @RequestMapping(value = "executeRouteLocation", method = RequestMethod.POST)
    public String executeRouteLocation(ModelMap model, HttpServletRequest request, String commandFromDropdown, String commandSelectValue, 
                                       String commandString, boolean autoUpdateRoute, int[] routesSelect, HttpServletResponse resp) throws ServletException {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        // DEVICE COLLECTION
        DeviceCollection deviceCollection = this.deviceCollectionFactory.createDeviceCollection(request);
                
        List<Integer> selectedRouteIds = new ArrayList<>();
        if (routesSelect != null) {
            for (int routeId : routesSelect) {
                selectedRouteIds.add(routeId);
            }
        }
        
        // AUTO UPDATE ROUTE OPTION
        model.addAttribute("autoUpdateRoute", autoUpdateRoute);

        model.addAttribute("commandSelectValue", commandSelectValue);
        model.addAttribute("commandString", commandString);
        
        // NO ROUTES SELECTED
        if (selectedRouteIds.size() < 1) {
            
            for (String param : deviceCollection.getCollectionParameters().keySet()) {
                model.addAttribute(param, deviceCollection.getCollectionParameters().get(param));
            }
            
            String errorMsg = messageSourceAccessor.getMessage(baseKey + "noRoutesSelectedError");
            model.addAttribute("errorMsg", errorMsg);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(model, request, userContext, commandString, commandSelectValue, errorMsg, autoUpdateRoute);
            return "routeLocate/routeLocateHome.jsp";
        }
        
        if (StringUtils.isBlank(commandString)) {
            String errorMsg = messageSourceAccessor.getMessage(baseKey + "noCommandSpecified");
            model.addAttribute("errorMsg", errorMsg);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(model, request, userContext, commandString, commandSelectValue, errorMsg, autoUpdateRoute);
            return "routeLocate/routeLocateHome.jsp";
        }
        
        //check if authorized to execute command
        if (!commandAuthorizationService.isAuthorized(userContext.getYukonUser(), commandString)) {
            for (String param : deviceCollection.getCollectionParameters().keySet()) {
                model.addAttribute(param, deviceCollection.getCollectionParameters().get(param));
            }
            String errorMsg = messageSourceAccessor.getMessage(baseKey + "commandNotAuthorizedError");
            model.addAttribute("errorMsg", errorMsg);
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            setupModel(model, request, userContext, commandString, commandSelectValue, errorMsg, autoUpdateRoute);
            return "routeLocate/routeLocateHome.jsp";
        } else {
            SimpleCallback<CollectionActionResult> alertCallback = CollectionActionAlertHelper.createAlert(
                AlertType.LOCATE_ROUTE, alertService, messageResolver.getMessageSourceAccessor(userContext), request);
            LinkedHashMap<String, String> userInputs = new LinkedHashMap<>();
            List<String> selectedRouteNames = new ArrayList<>();
            List<LiteYukonPAObject> routes = Arrays.asList(paoDao.getAllLiteRoutes());
            routes.forEach(route -> {
                if (selectedRouteIds.contains(route.getLiteID())) {
                    selectedRouteNames.add(route.getPaoName());
                }
            });  
            userInputs.put(CollectionActionInput.SELECTED_ROUTES.name(),  StringUtils.join(selectedRouteNames, ", "));
            userInputs.put(CollectionActionInput.AUTOMATICALLY_UPDATE_ROUTE.name(), Boolean.valueOf(autoUpdateRoute).toString());
            userInputs.put(CollectionActionInput.SELECTED_COMMAND.name(), commandFromDropdown);
            userInputs.put(CollectionActionInput.COMMAND.name(), commandString);
            int cacheKey = routeLocationService.locate(userInputs, deviceCollection, selectedRouteIds, autoUpdateRoute,
                commandString, alertCallback, userContext);
            return "redirect:/collectionActions/progressReport/detail?key=" + cacheKey;
        }
        
    }
    
    // VIEW ROUTES
    @RequestMapping(value = "routeSettings", method = RequestMethod.GET)
    public String routeSettings(ModelMap model, HttpServletRequest request, String resultId) throws ServletException {
        
        // RESULT
        model.addAttribute("resultId", resultId);
        
        List<DeviceRouteLocation> locations = routeLocationService.getLocations(Integer.parseInt(resultId));
        CollectionActionResult result = collectionActionService.getResult(Integer.parseInt(resultId));
        List<DeviceRouteLocation> notFoundRoutes = new ArrayList<>();
        
        List<SimpleDevice> foundDevices = new ArrayList<SimpleDevice>();
        List<SimpleDevice> notFoundDevices = new ArrayList<SimpleDevice>();

        for (SimpleDevice device : result.getInputs().getCollection().getDeviceList()) {
            boolean found = false;
            for (DeviceRouteLocation location : locations) {
                if (location.getDevice().equals(device)) {
                    foundDevices.add(device);
                    found = true;
                    break;
                }
            }
            if (!found) {
                LiteYukonPAObject devicePaoObj = databaseCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
                DeviceRouteLocation location = new DeviceRouteLocation(device);
                location.setDeviceName(devicePaoObj.getPaoName());
                notFoundRoutes.add(location);
                notFoundDevices.add(device);
            }
        }
        
        StoredDeviceGroup foundGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(foundGroup,  foundDevices);
        DeviceCollection foundCollection = groupHelper.buildDeviceCollection(foundGroup);
        model.addAttribute("foundCollection", foundCollection);
        
        StoredDeviceGroup notFoundGroup = tempDeviceGroupService.createTempGroup();
        deviceGroupMemberEditorDao.addDevices(notFoundGroup,  notFoundDevices);
        DeviceCollection notFoundCollection = groupHelper.buildDeviceCollection(notFoundGroup);
        model.addAttribute("notFoundCollection", notFoundCollection);

        model.addAttribute("foundRoutes", locations);
        model.addAttribute("notFoundRoutes", notFoundRoutes);
        
        return "routeLocate/routeLocateRouteSettings.jsp";
    }
    
    // SET SINGLE ROUTE
    @RequestMapping(value = "setRoute", method = RequestMethod.POST)
    public String setRoute(ModelMap model, int deviceId, int routeId) throws ServletException {
        // update device
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        deviceUpdateService.changeRoute(device, routeId);
        List<LiteYukonPAObject> routes = Arrays.asList(paoDao.getAllLiteRoutes());
        Optional<LiteYukonPAObject> newRoute = routes.stream().filter(route -> route.getLiteID() == routeId).findFirst();
        if (newRoute.isPresent()) {
            model.addAttribute("newRouteName", newRoute.get().getPaoName());
        }
        return "routeLocate/routeLocateRouteUpdateInfo.jsp";
    }
    
}