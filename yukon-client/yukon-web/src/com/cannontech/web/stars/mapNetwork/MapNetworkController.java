package com.cannontech.web.stars.mapNetwork;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
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
import com.cannontech.common.rfn.service.RfnDeviceMetadataService;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.NmNetworkException;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;

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
    
    @RequestMapping("home")
    public String home(ModelMap model, @RequestParam("deviceId") int deviceId, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        FeatureCollection geojson = paoLocationService.getLocationsAsGeoJson(Arrays.asList(device));
        
        model.addAttribute("geojson", geojson);
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("displayNeighborsLayer", !device.getDeviceType().isWaterMeter());
        model.addAttribute("displayParentNodeLayer", device.getDeviceType().isWaterMeter());
        
        model.addAttribute("isGateway", PaoType.getRfGatewayTypes().contains(device.getDeviceType()));
        model.addAttribute("isRelay", PaoType.getRfRelayTypes().contains(device.getDeviceType()));
        
        //try to get commstatus for device
        try {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            RfnDevice rfnDevice = rfnDeviceDao.getDeviceForId(deviceId);
            Map<RfnMetadata, Object> metadata = metadataService.getMetadata(rfnDevice);        
            Object commStatus = metadata.get(RfnMetadata.COMM_STATUS);
            CommStatusType status = CommStatusType.valueOf(commStatus.toString());
            String statusString = accessor.getMessage(nameKey + "status." + status);
            model.addAttribute("deviceStatus", statusString);
        } catch (NmCommunicationException e) {
            log.warn("caught exception getting metadata", e);
        }

        return "mapNetwork/home.jsp";
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
            List<Neighbor> neighbors =  nmNetworkService.getNeighbors(deviceId, accessor);
            json.put("neighbors",  neighbors);
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
            List<RouteInfo> route = nmNetworkService.getRoute(deviceId, accessor);
            json.put("routeInfo",  route);
        } catch (NmNetworkException e) {
            json.put("errorMsg",  accessor.getMessage(e.getMessageSourceResolvable()));
        }
        return json;
    }
    
}