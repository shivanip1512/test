package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.List;
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
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.message.gateway.DataSequence;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.TimeoutExecutionException;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.google.common.collect.Lists;

@Controller
@CheckRoleProperty({YukonRoleProperty.INFRASTRUCTURE_ADMIN, 
    YukonRoleProperty.INFRASTRUCTURE_CREATE_AND_UPDATE, 
    YukonRoleProperty.INFRASTRUCTURE_DELETE, 
    YukonRoleProperty.INFRASTRUCTURE_VIEW})
public class GatewayDetailController {
    
    private static final Logger log = YukonLogManager.getLogger(GatewayDetailController.class);
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private GatewayControllerHelper helper;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    
    @RequestMapping("/gateways/{id}")
    public String detail(ModelMap model, YukonUserContext userContext, @PathVariable int id) 
            throws NmCommunicationException {
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(id);
        model.addAttribute("gateway", gateway);
        helper.addText(model, userContext);
        
        if (gateway.getLocation() != null) {
            FeatureCollection geojson = paoLocationService.getFeatureCollection(Lists.newArrayList(gateway.getLocation()));
            model.addAttribute("geojson", geojson);
        }
        
        if (gateway.getData() != null) {
            List<DataSequence> sequences = Lists.newArrayList(gateway.getData().getSequences());
            helper.sortSequences(sequences, userContext);
            model.addAttribute("sequences", sequences);
        }
        
        return "gateways/detail.jsp";
    }
    
    @CheckRoleProperty(YukonRoleProperty.INFRASTRUCTURE_DELETE)
    @RequestMapping(value="/gateways/{id}", method=RequestMethod.DELETE)
    public String delete(FlashScope flash, LiteYukonUser user, ModelMap model, @PathVariable int id) 
            throws NmCommunicationException {
        
        LiteYukonPAObject pao = cache.getAllPaosMap().get(id);
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoId(id);
        
        try {
            boolean success = rfnGatewayService.deleteGateway(pao.getPaoIdentifier());
            if (success) {
                log.debug("Gateway " + pao.getPaoName() + " deleted.");
                gatewayEventLogService.deletedGateway(user, pao.getPaoName(), 
                                                      gateway.getRfnIdentifier().getSensorSerialNumber());
                
                flash.setConfirm(new YukonMessageSourceResolvable(baseKey + "delete.success", pao.getPaoName()));
                return "redirect:/stars/gateways";
            } else {
                log.error("Could not delete gateway " + pao.getPaoName());
                flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.failure", pao.getPaoName()));
                model.addAttribute("gateway", gateway);
            }
        } catch (NmCommunicationException e) {
            log.error("Could not delete gateway " + pao.getPaoName(), e);
            flash.setError(new YukonMessageSourceResolvable(baseKey + "delete.failure", pao.getPaoName()));
            model.addAttribute("gateway", gateway);
        }
        
        return "gateways/detail.jsp";
    }
    
    @RequestMapping("/gateways/{id}/sequences")
    public String sequences(ModelMap model, YukonUserContext userContext, @PathVariable int id) 
            throws NmCommunicationException {
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(id);
        List<DataSequence> sequences = Lists.newArrayList(gateway.getData().getSequences());
        helper.sortSequences(sequences, userContext);
        model.addAttribute("sequences", sequences);
        
        return "gateways/sequences.jsp";
    }
    
    @RequestMapping("/gateways/{id}/data")
    public @ResponseBody Map<String, Object> data(ModelMap model, YukonUserContext userContext, @PathVariable int id) 
            throws NmCommunicationException {
        
        RfnGateway gateway = rfnGatewayService.getGatewayByPaoIdWithData(id);
        Map<String, Object> data = helper.buildGatewayModel(gateway, userContext);
        
        return data;
    }
    
    /** Test the connection, return result as json. */
    @RequestMapping("/gateways/test-connection")
    public @ResponseBody Map<String, Object> testConnection(YukonUserContext userContext, 
            int id, String ip, String username, String password) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        Map<String, Object> json = new HashMap<>();
        try {
            boolean success = false;
            if (ip == null || username == null || password == null) {
                success = rfnGatewayService.testConnection(id);
            } else {
                success = rfnGatewayService.testConnection(id, ip, username, password);
            }
            json.put("success", success);
        } catch (NmCommunicationException e) {
            json.put("success", false);
            if (e.getCause() instanceof TimeoutExecutionException) {
                json.put("message", accessor.getMessage(baseKey + "login.failed.timeout"));
            }
        }
        
        return json;
    }
    
}