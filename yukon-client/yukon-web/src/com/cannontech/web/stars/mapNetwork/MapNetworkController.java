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
import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
import org.geojson.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.message.metadata.CommStatusType;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.tools.mapping.service.impl.NmNetworkServiceImpl.Neighbors;
import com.cannontech.web.tools.mapping.service.impl.NmNetworkServiceImpl.Route;
import com.cannontech.web.stars.gateway.model.LocationValidator;
@RequestMapping("/mapNetwork/*")
@Controller
public class MapNetworkController {
    
    private static final Logger log = YukonLogManager.getLogger(MapNetworkController.class);
    private static final String nameKey= "yukon.web.modules.operator.mapNetwork.";
    
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private NmNetworkService nmNetworkService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnDeviceMetadataService metadataService;
    @Autowired private RfnGatewayDataCache gatewayDataCache;
    
    @RequestMapping("home")
    public String home(ModelMap model, @RequestParam("deviceId") int deviceId, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        FeatureCollection geojson = paoLocationService.getLocationsAsGeoJson(Arrays.asList(device));
        Coordinates coordinates = new Coordinates();
        
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
        
        boolean displayNeighborsLayer = !device.getDeviceType().isWaterMeter();
        boolean displayParentNodeLayer = device.getDeviceType().isWaterMeter();
        boolean displayPrimaryRouteLayer = !isGateway;
        model.addAttribute("displayNeighborsLayer", displayNeighborsLayer);
        model.addAttribute("displayParentNodeLayer", displayParentNodeLayer);
        model.addAttribute("displayPrimaryRouteLayer", displayPrimaryRouteLayer);
        
        int numLayers = BooleanUtils.toInteger(displayNeighborsLayer) + BooleanUtils.toInteger(displayParentNodeLayer) + BooleanUtils.toInteger(displayPrimaryRouteLayer);
        model.addAttribute("numLayers", numLayers);
        
        // try to get commstatus for device
        try {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(deviceId);
            Object commStatus = null;
            if (rfnDevice.getPaoIdentifier().getPaoType().isRfGateway()) {
                RfnGatewayData gateway = gatewayDataCache.get(rfnDevice.getPaoIdentifier());
                String statusString = accessor.getMessage("yukon.web.modules.operator.gateways.connectionStatus." + gateway.getConnectionStatus().toString());
                model.addAttribute("deviceStatus", statusString);
            } else {
                Map<RfnMetadata, Object> metadata = metadataService.getMetadata(rfnDevice);
                commStatus = metadata.get(RfnMetadata.COMM_STATUS);
                if (commStatus != null) {
                    CommStatusType status = CommStatusType.valueOf(commStatus.toString());
                    String statusString = accessor.getMessage(nameKey + "status." + status);
                    model.addAttribute("deviceStatus", statusString);
                } else {
                    // ignore, status will be set to "UNKNOWN"
                    log.error("NM didn't return communication status for " + deviceId);
                }
            }
        } catch (NmCommunicationException e) {
            // ignore, status will be set to "UNKNOWN"
            log.error("Failed to get meta-data for " + deviceId, e);
        }

        return "mapNetwork/home.jsp";
    }
    
    @RequestMapping(value="saveCoordinates")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.LIMITED)
    public String saveCoordinates(HttpServletResponse resp, @RequestParam("deviceId") int deviceId, @RequestParam("latitude") Double latitude, 
                  @RequestParam("longitude") Double longitude, FlashScope flash, YukonUserContext userContext) throws ServletException {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Map<String, Object> json = new HashMap<>();
        boolean errorFound = false;
        List<String> errorMessages = LocationValidator.getErrorMessages(latitude, longitude, accessor);
        if (errorMessages.size()!=0) {
           errorFound = true; 
        }
        if (!errorFound) {
            if (latitude == null && longitude == null) {
                paoLocationService.deleteLocationForPaoId(deviceId);
            } else {
                paoLocationService.saveLocationForPaoId(deviceId, latitude, longitude);
            }
            flash.setConfirm(new YukonMessageSourceResolvable(nameKey + "location.update.successful"));
            json.put("success", true);
        } else {
            json.put("error",  true);
            json.put("errorMessages",  StringUtils.join(errorMessages, " "));
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
    
    public class Coordinates {
        private Double latitude;
        private Double longitude;
        
        public Double getLatitude() {
            return latitude;
        }
        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }
        public Double getLongitude() {
            return longitude;
        }
        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }
    }
    
}