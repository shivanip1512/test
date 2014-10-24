package com.cannontech.web.stars.gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRole;
import com.cannontech.web.stars.gateway.model.CertificateUpdate;
import com.google.common.collect.Lists;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayCertificateController {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    /** Certificate update popup. */
    @RequestMapping("/gateways/cert-update/options")
    public String certificate(ModelMap model) {
        
        Set<RfnGateway> gateways = rfnGatewayService.getAllGateways();
        model.addAttribute("gateways", gateways);
        
        return "gateways/cert.update.jsp";
    }
    
    @RequestMapping("/gateways/cert-update/data")
    public @ResponseBody Map<String, Object> data(YukonUserContext userContext) {
        
        Map<String, Object> json = new HashMap<>();
        
        // TEST CODE
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
        CertificateUpdate one = new CertificateUpdate();
        one.setFileName("licertupgrade.pkg.nm");
        one.setTimestamp(new Instant().minus(Duration.standardDays(7)));
        one.setSuccessful(Lists.newArrayList(gateways.get(0)));
        one.setFailed(Lists.newArrayList(gateways.get(1)));
        one.setUpdateId("654asd67f54as76f4v");
        
        CertificateUpdate two = new CertificateUpdate();
        two.setFileName("licertupgrade.pkg.nm");
        two.setTimestamp(new Instant().minus(Duration.standardDays(8)));
        two.setPending(Lists.newArrayList(gateways.get(0)));
        two.setFailed(Lists.newArrayList(gateways.get(1)));
        two.setUpdateId("ads6587a56ds96dsaf");
        
        json.put(one.getUpdateId(), one);
        json.put(two.getUpdateId(), two);
        
        // END TEST CODE
        
        return json;
    }
    
    @RequestMapping(value="/gateways/cert-update", method=RequestMethod.POST)
    public String certUpdate(HttpServletResponse resp, ModelMap model, YukonUserContext userContext, 
            @RequestParam("file") MultipartFile file, int[] gateways) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                // TODO Sam does a thing with the stuff
                
                // Success
                model.clear();
                
                // TEST CODE
                List<RfnGateway> rfgateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
                CertificateUpdate update = new CertificateUpdate();
                update.setFileName("licertupgrade.pkg.nm");
                update.setTimestamp(new Instant().minus(Duration.standardDays(7)));
                update.setPending(Lists.newArrayList(rfgateways.get(0)));
                update.setFailed(Lists.newArrayList(rfgateways.get(1)));
                update.setUpdateId("654asd67f54as76f4v");
                // END TEST CODE
                
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