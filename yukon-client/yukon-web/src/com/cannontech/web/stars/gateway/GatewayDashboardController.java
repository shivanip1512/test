package com.cannontech.web.stars.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.gateway.model.ConnectStatus;
import com.cannontech.web.stars.gateway.model.Gateway;
import com.cannontech.web.stars.gateway.model.LastComm;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayDashboardController {

    @Autowired GatewayHelper gatewayHelper;
    
    @RequestMapping({"/gateways", "/gateways/"})
    public String gateways(ModelMap model) {
        
        model.addAttribute("gateways", gatewayHelper.getGateways());
        
        return "gateways/dashboard.jsp";
    }
    
    @RequestMapping("/gateways/create")
    public String edit(ModelMap model) {
        
        Gateway gateway = new Gateway("GW001", "7500000383", "10.219.240.51", "1000750_01", "R_5_2_1", LastComm.SUCCESS, 100.0, ConnectStatus.CONNECTED);
        model.addAttribute("gateway", gateway);
        
        return "gateways/settings.jsp";
    }
    
}
