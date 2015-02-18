package com.cannontech.web.stars.gateway;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
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
import com.cannontech.common.rfn.message.gateway.DataType;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty({YukonRoleProperty.INFRASTRUCTURE_ADMIN, 
                    YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, 
                    YukonRoleProperty.INFRASTRUCTURE_DELETE, 
                    YukonRoleProperty.INFRASTRUCTURE_VIEW})
public class GatewayListController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayListController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @Autowired private GatewayControllerHelper helper;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping(value = {"/gateways", "/gateways/"}, method = RequestMethod.GET)
    public String gateways(ModelMap model, FlashScope flashScope, YukonUserContext userContext) {
        
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        List<CertificateUpdate> certUpdates = certificateUpdateService.getAllCertificateUpdates();
        Collections.sort(certUpdates, new Comparator<CertificateUpdate>() {
            @Override
            public int compare(CertificateUpdate o1, CertificateUpdate o2) {
                return o2.getTimestamp().compareTo(o1.getTimestamp());
            }
        });
        model.addAttribute("certUpdates", certUpdates);
        
        helper.addText(model, userContext);
        
        return "gateways/list.jsp";
    }
    
    @RequestMapping("/gateways/data")
    public @ResponseBody Map<Integer, Object> data(YukonUserContext userContext) {
        
        Map<Integer, Object> json = new HashMap<>();
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        for (RfnGateway gateway : gateways) {
            Map<String, Object> data = helper.buildGatewayModel(gateway, userContext);
            json.put(gateway.getPaoIdentifier().getPaoId(), data);
        }
        
        return json;
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_ADMIN)
    @RequestMapping("/gateways/{id}/connect")
    public @ResponseBody Map<String, Object> connect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.connectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_ADMIN)
    @RequestMapping("/gateways/{id}/disconnect")
    public @ResponseBody Map<String, Object> disconnect(YukonUserContext userContext, @PathVariable int id) {
        
        Map<String, Object> json = new HashMap<>();
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        LiteYukonPAObject gateway = cache.getAllPaosMap().get(id);
        try {
            boolean success = rfnGatewayService.disconnectGateway(gateway.getPaoIdentifier());
            json.put("success", success);
        } catch (NmCommunicationException e) {
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
        } catch (NmCommunicationException e) {
            String errorMsg = accessor.getMessage(baseKey + "error.comm");
            json.put("success", false);
            json.put("message", errorMsg);
            log.error("Failed communicating to NM.", e);
        }
        
        return json;
    }
    
}