package com.cannontech.web.stars.gateway;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayCertificateController {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;
    
    /** Certificate update popup. */
    @RequestMapping("/gateways/cert-update/options")
    public String certificate(ModelMap model) {
        
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        Collections.sort(gateways);
        model.addAttribute("gateways", gateways);
        
        return "gateways/cert.update.jsp";
    }
    
    @RequestMapping("/gateways/cert-update/data")
    public @ResponseBody Map<Integer, Object> data(YukonUserContext userContext) {
        
        Map<Integer, Object> json = new HashMap<>();
        
        List<CertificateUpdate> updates = certificateUpdateService.getAllCertificateUpdates();
        for (CertificateUpdate update : updates) {
            json.put(update.getYukonUpdateId(), update);
        }
        
        return json;
    }
    
    @RequestMapping("/gateways/cert-update/{id}/details")
    public String details(ModelMap model, @PathVariable int id) {
        
        CertificateUpdate update = certificateUpdateService.getCertificateUpdate(id);
        model.addAttribute("update", update);
        
        return "gateways/cert.update.details.jsp";
    }
    
    @RequestMapping(value="/gateways/cert-update", method=RequestMethod.POST)
    public String certUpdate(HttpServletResponse resp, ModelMap model, YukonUserContext userContext, 
            @RequestParam("file") MultipartFile file, Integer[] gateways) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        if (!file.isEmpty()) {
            try {
                Set<RfnGateway> rfnGateways = rfnGatewayService.getGatewaysByPaoIds(Lists.newArrayList(gateways));
                String certificateId = certificateUpdateService.sendUpdate(rfnGateways, file);
                
                // Event logging
                gatewayEventLogService.sentCertificateUpdate(userContext.getYukonUser(), file.getOriginalFilename(), 
                                                             certificateId, gateways.length);
                
                // Success
                model.clear();
                
                int updateId = certificateUpdateDao.getLatestUpdateForCertificate(certificateId);
                CertificateUpdate update = certificateUpdateService.getCertificateUpdate(updateId);
                
                resp.setContentType("application/json");
                JsonUtils.getWriter().writeValue(resp.getOutputStream(), update);
                return null;
            } catch (Exception e) {
                
                model.addAttribute("gateways", rfnGatewayService.getAllGateways());
                String errorMsg = "";
                model.addAttribute("errorMsg", errorMsg);
                
                return "gateways/cert.update.jsp";
            }
        } else {
            
            model.addAttribute("gateways", rfnGatewayService.getAllGateways());
            model.addAttribute("errorMsg", accessor.getMessage(baseKey + "cert.update.file.empty"));
            
            return "gateways/cert.update.jsp";
        }
    }
    
}