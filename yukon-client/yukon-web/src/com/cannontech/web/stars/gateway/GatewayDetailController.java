package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geojson.FeatureCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.NetworkManagerCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.TimeoutExecutionException;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayDetailController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDetailController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private GatewayControllerHelper helper;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("/gateways/{id}")
    public String detail(ModelMap model, YukonUserContext userContext, @PathVariable int id) 
            throws NetworkManagerCommunicationException {
        
        //TODO: handle network manager communication exception gracefully
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(id);
        model.addAttribute("gateway", gateway);
        helper.addText(model, userContext);
        
        if (gateway.getLocation() != null) {
            FeatureCollection geojson = paoLocationService.getFeatureCollection(Lists.newArrayList(gateway.getLocation()));
            model.addAttribute("geojson", geojson);
        }
        
        return "gateways/detail.jsp";
    }
    
    @RequestMapping(value="/gateways/{id}", method=RequestMethod.DELETE)
    public String delete(FlashScope flash, ModelMap model, @PathVariable int id) 
            throws NetworkManagerCommunicationException {
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(id);
        
        try {
            boolean success = rfnGatewayService.deleteGateway(pao.getPaoIdentifier());
            if (success) {
                log.debug("Gateway " + pao.getPaoName() + " deleted.");
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", pao.getPaoName()));
                return "redirect:/stars/gateways";
            } else {
                log.error("Could not delete gateway " + pao.getPaoName());
                flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.failure", pao.getPaoName()));
                model.addAttribute("gateway", gateway);
            }
        } catch (NetworkManagerCommunicationException e) {
            log.error("Could not delete gateway " + pao.getPaoName(), e);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.failure", pao.getPaoName()));
            model.addAttribute("gateway", gateway);
        }
        
        return "gateways/detail.jsp";
    }
    
    /** Test the connection, return result as json. */
    @RequestMapping("/gateways/test-connection")
    public @ResponseBody Map<String, Object> testConnection(YukonUserContext userContext, 
            int id, String ip, String username, String password) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> json = new HashMap<>();
        try {
            boolean success = rfnGatewayService.testConnection(id, ip, username, password);
            json.put("success", success);
        } catch (NetworkManagerCommunicationException e) {
            json.put("success", false);
            if (e.getCause() instanceof TimeoutExecutionException) {
                json.put("message", accessor.getMessage(baseKey + "login.failed.timeout"));
            }
        }
        
        return json;
    }
    
}