package com.cannontech.web.stars.mapNetwork;

import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.mapping.model.Neighbor;
import com.cannontech.web.tools.mapping.model.Parent;
import com.cannontech.web.tools.mapping.model.RouteInfo;
import com.cannontech.web.tools.mapping.service.NmNetworkTestService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;

@RequestMapping("/mapNetwork/*")
@Controller
public class MapNetworkController {
    
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private NmNetworkTestService nmNetworkTestService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @RequestMapping("home")
    public String home(ModelMap model, YukonUserContext userContext, HttpServletRequest request) throws ServletException {
        
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        
        SimpleDevice device = deviceDao.getYukonDevice(deviceId);
        FeatureCollection geojson = paoLocationService.getLocationsAsGeoJson(Arrays.asList(device));
        
        model.addAttribute("geojson", geojson);
        model.addAttribute("deviceId", deviceId);
        
        model.addAttribute("displayNeighborsLayer", !device.getDeviceType().isWaterMeter());
        model.addAttribute("displayParentNodeLayer", device.getDeviceType().isWaterMeter());
        
        model.addAttribute("isGateway", PaoType.getRfGatewayTypes().contains(device.getDeviceType()));
        
        return "mapNetwork/home.jsp";
    }
    
    @RequestMapping("parentNode")
    public @ResponseBody Parent parentNode(HttpServletRequest request) throws ServletException {
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        return nmNetworkTestService.getParent(deviceId);
    }
    
    @RequestMapping("neighbors")
    public @ResponseBody List<Neighbor> neighbors(HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return nmNetworkTestService.getNeighbors(deviceId, accessor);
    }
    
    @RequestMapping("primaryRoute")
    public @ResponseBody List<RouteInfo> primaryRoute(HttpServletRequest request, YukonUserContext userContext) throws ServletException {
        int deviceId = ServletRequestUtils.getRequiredIntParameter(request, "deviceId");
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return nmNetworkTestService.getRoute(deviceId, accessor);
    }
    
}