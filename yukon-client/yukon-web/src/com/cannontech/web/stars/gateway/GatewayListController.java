package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.gateway.model.ConnectStatus;
import com.cannontech.web.stars.gateway.model.Gateway;
import com.cannontech.web.stars.gateway.model.LastComm;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.GET)
    public String gateways(ModelMap model, FlashScope flashScope) {
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        
        return "gateways/list.jsp";
    }
    
    @RequestMapping("/gateways/data")
    public @ResponseBody Map<Integer, Object> data(YukonUserContext userContext) {
        
        Map<Integer, Object> json = new HashMap<>();
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        for (RfnGateway gateway : gateways) {
            Map<String, Object> data = buildGatewayModel(gateway, userContext);
            
            json.put(gateway.getPaoIdentifier().getPaoId(), data);
        }
        
        return json;
    }
    
    /** Build a i18n friendly json model of the gateway */
    private Map<String, Object> buildGatewayModel(RfnGateway gateway, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> gatewayJson = new HashMap<>();
        gatewayJson.put("name", gateway.getName());
        gatewayJson.put("paoId", gateway.getPaoIdentifier());
        gatewayJson.put("rfnId", gateway.getRfnIdentifier());
        gatewayJson.put("location", gateway.getLocation());
        RfnGatewayData data = gateway.getData();
        if (data == null) {
            gatewayJson.put("data", null);
        } else {
            Map<String, Object> dataJson = new HashMap<>();
            dataJson.put("connected", data.getConnectionStatus() == ConnectionStatus.CONNECTED);
            dataJson.put("connectionStatusText", accessor.getMessage(baseKey + "connectionStatus." + data.getConnectionStatus()));
            dataJson.put("ip", data.getIpAddress());
            dataJson.put("lastComm", data.getLastCommStatus());
            dataJson.put("lastCommText", accessor.getMessage(baseKey + "lastCommStatus." + data.getLastCommStatus()));
            dataJson.put("lastCommTimestamp", data.getLastCommStatusTimestamp());
            dataJson.put("collectionWarning", gateway.isTotalCompletionLevelWarning());
            dataJson.put("collectionDanger", gateway.isTotalCompletionLevelDanger());
            dataJson.put("collectionPercent", gateway.getTotalCompletionPercentage());
            gatewayJson.put("data", dataJson);
        }
        
        return gatewayJson;
    }

    @RequestMapping("/gateways/create")
    public String createDialog(ModelMap model) {
        
        model.addAttribute("mode", "CREATE");
        
        return "gateways/settings.jsp";
    }
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.POST)
    public String create(ModelMap model) {
        
        //TODO
        //PaoIdentifier gatewayId = rfnGatewayService.createGateway(name, ipAddress, location, user, admin, superAdmin)
        log.info("Gateway Created");
        
        return "gateways/list.jsp";
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