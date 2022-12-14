package com.cannontech.web.stars.mapNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.rfn.model.NmCommunicationException;
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
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;

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
    @Autowired private RfnDeviceDao rfnDeviceDao;
    
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
        model.addAttribute("displayInfrastructure", !isPlc);
        
        int numLayers = BooleanUtils.toInteger(displayNeighborsLayer) + BooleanUtils.toInteger(displayParentNodeLayer) + BooleanUtils.toInteger(displayPrimaryRouteLayer);
        model.addAttribute("numLayers", numLayers);
        
        model.addAttribute("gatewayPaoTypes", PaoType.getRfGatewayTypes());
        model.addAttribute("relayPaoTypes", PaoType.getRfRelayTypes());
        model.addAttribute("wifiPaoTypes", PaoType.getWifiTypes());
        
        model.addAttribute("mileValues", NearbyMiles.values());

        return "mapNetwork/home.jsp";
    }
    
    @PostMapping("saveCoordinates")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public String saveCoordinates(HttpServletResponse resp, @ModelAttribute("location") Location location,
            BindingResult bindingResult, FlashScope flash, YukonUserContext userContext) throws ServletException {
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
    public @ResponseBody Map<String, Object> parentNode(HttpServletRequest request, @RequestParam("deviceId") int deviceId,
            YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            Pair<RfnDevice, FeatureCollection> parent = nmNetworkService.getParent(deviceId, accessor);
            if (parent == null) {
                // no parent
                json.put("errorMsg", accessor.getMessage(nameKey + "exception.noParent"));
            } else if (parent.getValue() == null) {
                // no location
                json.put("errorMsg", accessor.getMessage(nameKey + "exception.noParentLocation", parent.getKey().getName()));
            }
            json.put("parent", parent);
        } catch (NmCommunicationException e) {
            json.put("errorMsg", e.getMessage());
        }
        return json;
    }
    
    @RequestMapping("neighbors")
    public @ResponseBody Map<String, Object> neighbors(HttpServletRequest request, @RequestParam("deviceId") int deviceId,
            YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            List<Pair<RfnDevice, FeatureCollection>> neighbors = nmNetworkService.getNeighbors(deviceId, accessor);
            if (neighbors.isEmpty()) {
                // no neighbors
                json.put("errorMsg", accessor.getMessage(nameKey + "exception.neighbors.noDevicesReturned"));
            }
            json.put("neighbors", neighbors);
            // check for any neighbors that have missing location data
            List<String> missingNeighborNames = neighbors.stream()
                    .filter(value -> value != null && value.getRight() == null)
                    .map(value -> value.getKey().getName())
                    .collect(Collectors.toList());
            if (!missingNeighborNames.isEmpty()) {
                json.put("errorMsg", accessor.getMessage(nameKey + "exception.neighbors.missingLocationData",
                        String.join(", ", missingNeighborNames)));
            }
        } catch (NmCommunicationException e) {
            json.put("errorMsg", e.getMessage());
        }
        return json;
    }
    
    @RequestMapping("primaryRoute")
    public @ResponseBody Map<String, Object> primaryRoute(HttpServletRequest request, @RequestParam("deviceId") int deviceId, YukonUserContext userContext) throws ServletException {
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        try {
            List<Pair<RfnDevice, FeatureCollection>> entireRoute = nmNetworkService.getRoute(deviceId, accessor);
            json.put("entireRoute", entireRoute);
            if (entireRoute.isEmpty()) {
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.primaryRoute.noDevicesReturned"));
            }
            // devices in the route that have missing location data
            List<String> missingRoute = entireRoute.stream()
                    .filter(value -> value != null && value.getRight() == null)
                    .map(value -> value.getKey().getName())
                    .collect(Collectors.toList());
            if (!missingRoute.isEmpty()) {
                json.put("errorMsg",  accessor.getMessage(nameKey + "exception.primaryRoute.missingLocationData", String.join(", ", missingRoute)));
            }
        } catch (NmCommunicationException e) {
            json.put("errorMsg",  e.getMessage());
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
    
    //This request needs to be a post because there can be many devices that we pass in
    @PostMapping("selectedGateways")
    public @ResponseBody Set<Integer> selectedGateways(@RequestParam(value="deviceIds[]", required=true, defaultValue="") Integer[] deviceIds) {
       return rfnDeviceDao.getGatewayIdsForDevices(new HashSet<>(Arrays.asList(deviceIds)));
    }
    
}