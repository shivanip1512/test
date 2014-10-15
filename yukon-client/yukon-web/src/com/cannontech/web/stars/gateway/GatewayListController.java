package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.gateway.model.ConnectStatus;
import com.cannontech.web.stars.gateway.model.Gateway;
import com.cannontech.web.stars.gateway.model.LastComm;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    @Autowired private RfnGatewayService rfnGatewayService;
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.GET)
    public String gateways(ModelMap model, FlashScope flashScope) throws NetworkManagerCommunicationException {
        
        //TODO: handle network manager communication exception gracefully
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        
        return "gateways/list.jsp";
    }
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.POST)
    public String create(ModelMap model) {
        
        //TODO
        //PaoIdentifier gatewayId = rfnGatewayService.createGateway(name, ipAddress, location, user, admin, superAdmin)
        
        return "gateways/list.jsp";
    }
    
    @RequestMapping("/gateways/data")
    public @ResponseBody Map<String, Object> data(@RequestBody Map<String, ArrayList<Integer>> pending) {
        
        Map<String, Object> data = new HashMap<>();
        data.put("hello", "world");
        
        return data;
    }
    
    @RequestMapping("/gateways/create")
    public String createDialog(ModelMap model) {
        
        model.addAttribute("mode", "CREATE");
        
        return "gateways/settings.jsp";
    }
    
    /*
     * TEST CODE BELOW
     */
    @Autowired GatewayHelper gatewayHelper;
    
    @RequestMapping({"/gatewaysMockup", "/gatewaysMockup/"})
    public String gatewaysMockup(ModelMap model) {
        
        model.addAttribute("gateways", gatewayHelper.getGateways());
        
        return "gateways/dashboardMockup.jsp";
    }
    
    @RequestMapping("/gatewaysMockup/create")
    public String editMockup(ModelMap model) {
        
        Gateway gateway = new Gateway("GW001", "7500000383", "10.219.240.51", "1000750_01", "R_5_2_1", LastComm.SUCCESS, 100.0, ConnectStatus.CONNECTED);
        model.addAttribute("gateway", gateway);
        
        return "gateways/settingsMockup.jsp";
    }
}
