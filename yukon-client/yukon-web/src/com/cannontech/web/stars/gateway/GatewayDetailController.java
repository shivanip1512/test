package com.cannontech.web.stars.gateway;

import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayDetailController {
    
    @Autowired GatewayHelper gatewayHelper;
    @Autowired PaoLocationService paoLocationService;

    @RequestMapping("/gateways/{id}")
    public String details(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("gateway", gatewayHelper.getGateways().get(0));
        model.addAttribute("sequences", gatewayHelper.getSequences());
        
        FeatureCollection fc = paoLocationService.getLocationsAsGeoJson(ImmutableList.of(new SimpleDevice(1140, PaoType.MCT410IL)));
        model.addAttribute("location", fc);
        
        return "gateways/detail.jsp";
    }
    
    @RequestMapping("/gateways/{id}/edit")
    public String edit(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("gateway", gatewayHelper.getGateways().get(0));
        
        return "gateways/settings.jsp";
    }
    
}
