package com.cannontech.web.stars.mapNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.mapping.Location;
import com.cannontech.web.tools.mapping.LocationValidator;
import com.cannontech.web.tools.mapping.model.NearbyDevice;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.tools.mapping.service.impl.NmNetworkServiceImpl.Neighbors;
import com.cannontech.web.tools.mapping.service.impl.NmNetworkServiceImpl.Route;

@RequestMapping("/mapNetwork/*")
@Controller
public class MapNetworkController {
    
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.";
    
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private LocationValidator locationValidator;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private LocationService locationService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    
    @RequestMapping(value = "home", method = RequestMethod.GET)
    public String home(ModelMap model, @RequestParam("deviceId") int deviceId,
                       YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        FeatureCollection geojson = paoLocationService.getLocationsAsGeoJson(Arrays.asList(device));
        Location coordinates = new Location();
        
        if (geojson.getFeatures().size() > 0) {
            Point point = (Point) geojson.getFeatures().get(0).getGeometry();
            coordinates.setLatitude(point.getCoordinates().getLatitude());
            coordinates.setLongitude(point.getCoordinates().getLongitude());
        }
        model.addAttribute("coordinates", coordinates);
        
        model.addAttribute("geojson", geojson);
        model.addAttribute("deviceId", deviceId);
        
        boolean isGateway = PaoType.getRfGatewayTypes().contains(device.getDeviceType());
        model.addAttribute("isGateway", isGateway);
        model.addAttribute("isRelay", PaoType.getRfRelayTypes().contains(device.getDeviceType()));
        boolean isLcr = device.getDeviceType().isTwoWayLcr();
        model.addAttribute("isLcr", isLcr);
        
        boolean isPlc = device.getDeviceType().isPlc();
        boolean displayNeighborsLayer = !isPlc && !device.getDeviceType().isWaterMeter();
        boolean displayParentNodeLayer = !isPlc && device.getDeviceType().isWaterMeter();
        boolean displayPrimaryRouteLayer = !isPlc && !isGateway;
        boolean displayNearbyLayer = isPlc || device.getDeviceType().isRfn();
        model.addAttribute("displayNeighborsLayer", displayNeighborsLayer);
        model.addAttribute("displayParentNodeLayer", displayParentNodeLayer);
        model.addAttribute("displayPrimaryRouteLayer", displayPrimaryRouteLayer);
        model.addAttribute("displayNearbyLayer", displayNearbyLayer);
        
        int numLayers = BooleanUtils.toInteger(displayNeighborsLayer) + BooleanUtils.toInteger(displayParentNodeLayer) + BooleanUtils.toInteger(displayPrimaryRouteLayer);
        model.addAttribute("numLayers", numLayers);

        return "mapNetwork/home.jsp";
    }
    
    @RequestMapping(value = "saveCoordinates", method = RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.LIMITED)
    public String saveCoordinates(HttpServletResponse resp, HttpServletRequest request,
            @ModelAttribute("location") Location location, BindingResult bindingResult, FlashScope flash,
            YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        boolean errorFound = false;
        String errorMsg = StringUtils.EMPTY;
        if (bindingResult.hasErrors()) {
            if (bindingResult.hasFieldErrors("latitude")) {
                json.put("error", true);
                json.put("latError", true);
                errorMsg = accessor.getMessage(nameKey + "location.latitude.numberError");
            }
            if (bindingResult.hasFieldErrors("longitude")) {
                json.put("error", true);
                json.put("lonError", true);
                errorMsg = errorMsg + accessor.getMessage(nameKey + "location.longitude.numberError");
            }
        }
        if (!errorMsg.isEmpty()) {
            json.put("errorMessages", errorMsg);
            return JsonUtils.writeResponse(resp, json);
        }
        DataBinder binder = new DataBinder(location);
        binder.setValidator(locationValidator);
        binder.validate();
        BindingResult results = binder.getBindingResult();
        List<String> errorMessages = new ArrayList<>();
        results.getAllErrors().stream().forEach(e -> errorMessages.add(accessor.getMessage(e.getCode())));

        if (errorMessages.size() != 0) {
            errorFound = true;
        }
        if (!errorFound) {
            if (location.getLatitude() == null && location.getLongitude() == null) {
                paoLocationService.deleteLocationForPaoId(location.getPaoId());
            } else {
                paoLocationService.saveLocationForPaoId(location.getPaoId(), location.getLatitude(),
                    location.getLongitude());
            }
            flash.setConfirm(new YukonMessageSourceResolvable(nameKey + "location.update.successful"));
            json.put("success", true);
        } else {
            json.put("error", true);
            json.put("errorMessages", StringUtils.join(errorMessages, " "));
        }

        return JsonUtils.writeResponse(resp, json);
    }
    
    @RequestMapping("parentNode")
    public @ResponseBody Map<String, Object> parentNode(HttpServletRequest request, @RequestParam("deviceId") int deviceId, YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            Parent parent = nmNetworkService.getParent(deviceId, accessor);
            json.put("parent",  parent);
        } catch (NmNetworkException e) {
            json.put("errorMsg",  accessor.getMessage(e.getMessageSourceResolvable()));
        }
        return json;
    }
    
    @RequestMapping("neighbors")
    public @ResponseBody Map<String, Object> neighbors(HttpServletRequest request, @RequestParam("deviceId") int deviceId, YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            Neighbors allNeighbors = nmNetworkService.getNeighbors(deviceId, accessor);
            if (allNeighbors.getErrorMsg() != null) {
                json.put("errorMsg",  allNeighbors.getErrorMsg());
            }
            List<Neighbor> neighbors =  allNeighbors.getNeighbors();
            json.put("neighbors",  neighbors);
            //check for any neighbors that have missing location data
            List<RfnDevice> missingNeighbors = allNeighbors.getNeighborsWithoutLocation();
            if (missingNeighbors.size() > 0) {
                List<String> missingNeighborNames = new ArrayList<>();
                missingNeighbors.forEach(neighbor -> missingNeighborNames.add(neighbor.getName()));
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.neighbors.missingLocationData", String.join(", ",  missingNeighborNames)));
            }
        } catch (NmNetworkException e) {
            json.put("errorMsg",  accessor.getMessage(e.getMessageSourceResolvable()));
        }
        return json;
    }
    
    @RequestMapping("primaryRoute")
    public @ResponseBody Map<String, Object> primaryRoute(HttpServletRequest request, @RequestParam("deviceId") int deviceId, YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            Route entireRoute = nmNetworkService.getRoute(deviceId, accessor);
            if (entireRoute.getErrorMsg() != null) {
                json.put("errorMsg", entireRoute.getErrorMsg());
            }
            List<RouteInfo> route = entireRoute.getRoute();
            json.put("routeInfo",  route);
            //check if any devices in the route have missing location data
            RfnDevice missingRoute = entireRoute.getDeviceWithoutLocation();
            if (missingRoute != null) {
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.primaryRoute.missingLocationData", missingRoute.getName()));
            }
        } catch (NmNetworkException e) {
            json.put("errorMsg",  accessor.getMessage(e.getMessageSourceResolvable()));
        }
        return json;
    }
    
    @RequestMapping("nearby")
    public @ResponseBody Map<String, Object> nearby(@RequestParam("deviceId") int deviceId, @RequestParam("miles") double miles, YukonUserContext userContext) {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<NearbyDevice> devices = new ArrayList<>();
        PaoLocation location = paoLocationDao.getLocation(deviceId);
        if (location != null) {
            int maxDevices = 1000;
            List<PaoDistance> nearby = locationService.getNearbyLocations(location, miles, DistanceUnit.MILES, null);
            if (nearby.size() > maxDevices) { 
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.TOO_MANY_NEARBY_DEVICES", nearby.size(), maxDevices));
                nearby = nearby.subList(0, maxDevices);
            } 
            for (PaoDistance distance : nearby) {
                NearbyDevice device = new NearbyDevice();
                device.setDistance(distance);
                FeatureCollection features = paoLocationService.getLocationsAsGeoJson(new ArrayList<>(Arrays.asList(distance.getPao())));
                device.setLocation(features);
                devices.add(device);
            }
            
            if (devices.size() == 0) {
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.NO_NEARBY_DEVICES"));
            }

            json.put("nearby", devices);
        }

        return json;
    }
    
}