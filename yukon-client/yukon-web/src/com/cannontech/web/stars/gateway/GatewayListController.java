package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.ConnectionStatus;
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayData;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.GET)
    public String gateways(ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        
        model.addAttribute("certUpdates", getCertUpdates());
        
        Map<String, String> text = new HashMap<>();
        text.put("connect.pending", accessor.getMessage(baseKey + "connect.pending"));
        text.put("connect.success", accessor.getMessage(baseKey + "connect.success"));
        text.put("connect.failure", accessor.getMessage(baseKey + "connect.failure"));
        text.put("login.successful", accessor.getMessage(baseKey + "login.successful"));
        text.put("login.failed", accessor.getMessage(baseKey + "login.failed"));
        text.put("disconnect.pending", accessor.getMessage(baseKey + "disconnect.pending"));
        text.put("disconnect.success", accessor.getMessage(baseKey + "disconnect.success"));
        text.put("disconnect.failure", accessor.getMessage(baseKey + "disconnect.failure"));
        text.put("collect.data.pending", accessor.getMessage(baseKey + "collect.data.pending"));
        text.put("collect.data.success", accessor.getMessage(baseKey + "collect.data.success"));
        text.put("collect.data.failure", accessor.getMessage(baseKey + "collect.data.failure"));
        text.put("collect.data.title", accessor.getMessage(baseKey + "collect.data.title"));
        text.put("cert.update.more", accessor.getMessage(baseKey + "cert.update.more"));
        text.put("cert.update.label", accessor.getMessage(baseKey + "cert.update.label"));
        text.put("complete", accessor.getMessage("yukon.common.complete"));
        model.addAttribute("text", text);
        
        return "gateways/list.jsp";
    }
    
    private List<CertificateUpdate> getCertUpdates() {
        
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        
        List<CertificateUpdate> updates = new ArrayList<>();
        
        CertificateUpdate one = new CertificateUpdate();
        one.setFileName("licertupgrade.pkg.nm");
        one.setTimestamp(new Instant().minus(Duration.standardDays(7)));
        one.setSuccessful(Lists.newArrayList((RfnDevice)gateways.get(0)));
        one.setFailed(Lists.newArrayList((RfnDevice)gateways.get(1)));
        one.setUpdateId("654asd67f54as76f4v");
        
        CertificateUpdate two = new CertificateUpdate();
        two.setFileName("licertupgrade.pkg.nm");
        two.setTimestamp(new Instant().minus(Duration.standardDays(8)));
        two.setPending(Lists.newArrayList((RfnDevice)gateways.get(0)));
        two.setFailed(Lists.newArrayList((RfnDevice)gateways.get(1)));
        two.setUpdateId("ads6587a56ds96dsaf");
        
        updates.add(one);
        updates.add(two);
        
        return updates;
        
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
    
    @RequestMapping("/gateways/{id}/connect")
    public @ResponseBody Map<String, Object> connect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.connectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NetworkManagerCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
    @RequestMapping("/gateways/{id}/disconnect")
    public @ResponseBody Map<String, Object> disconnect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.disconnectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NetworkManagerCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
    @RequestMapping(value="/gateways/{id}/collect-data", consumes=json, produces=json)
    public @ResponseBody Map<String, Object> collectData(YukonUserContext userContext, @PathVariable int id,
            @RequestBody DataType[] types) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.collectData(gateway.getPaoIdentifier(), types);
            json.put("success", success);
        } catch (NetworkManagerCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
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
            dataJson.put("connectionStatusText", accessor.getMessage(baseKey + "connectionStatus." 
                    + data.getConnectionStatus()));
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
    
}