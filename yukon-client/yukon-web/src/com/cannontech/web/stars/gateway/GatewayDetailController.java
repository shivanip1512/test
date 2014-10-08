package com.cannontech.web.stars.gateway;

import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.ImmutableList;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayDetailController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDetailController.class);
    @Autowired RfnGatewayService rfnGatewayService;
    @Autowired PaoLocationService paoLocationService;
    
    @RequestMapping("/gateways/{id}")
    public String detail(ModelMap model, @PathVariable int id) throws NetworkManagerCommunicationException {
        
        //TODO: handle network manager communication exception gracefully
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(new PaoIdentifier(id, PaoType.RFN_GATEWAY));
        model.addAttribute("gateway", gateway);
        return "gateways/detail.jsp";
    }
    
    @RequestMapping("/gateways/{id}/edit")
    public String edit(ModelMap model, @PathVariable int id) throws NetworkManagerCommunicationException {
        
        //TODO: handle network manager communication exception gracefully
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(new PaoIdentifier(id, PaoType.RFN_GATEWAY));
        model.addAttribute("gateway", gateway);
        return "gateways/settings.jsp";
    }
    
    /*
     * TEST CODE BELOW 
     */
    @Autowired GatewayHelper gatewayHelper;

    @RequestMapping("/gatewaysMockup/{id}")
    public String detailsMockup(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("gateway", gatewayHelper.getGateways().get(0));
        model.addAttribute("sequences", gatewayHelper.getSequences());
        
        FeatureCollection fc = paoLocationService.getLocationsAsGeoJson(ImmutableList.of(new SimpleDevice(1140, PaoType.MCT410IL)));
        model.addAttribute("location", fc);
        
        return "gateways/detailsMockup.jsp";
    }
    
    @RequestMapping("/gatewaysMockup/{id}/edit")
    public String editMockup(ModelMap model, @PathVariable int id) {
        
        model.addAttribute("gateway", gatewayHelper.getGateways().get(0));
        
        return "gateways/settingsMockup.jsp";
    }
    
}
