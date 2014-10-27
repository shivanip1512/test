package com.cannontech.web.stars.gateway;

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

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayCertificateController {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";
    
    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
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
//        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
//        CertificateUpdate one = new CertificateUpdate();
//        one.setFileName("licertupgrade.pkg.nm");
//        one.setTimestamp(new Instant().minus(Duration.standardDays(7)));
//        one.setSuccessful(Lists.newArrayList((RfnDevice)gateways.get(0)));
//        one.setFailed(Lists.newArrayList((RfnDevice)gateways.get(1)));
//        one.setUpdateId("654asd67f54as76f4v");
//        
//        CertificateUpdate two = new CertificateUpdate();
//        two.setFileName("licertupgrade.pkg.nm");
//        two.setTimestamp(new Instant().minus(Duration.standardDays(8)));
//        two.setPending(Lists.newArrayList((RfnDevice)gateways.get(0)));
//        two.setFailed(Lists.newArrayList((RfnDevice)gateways.get(1)));
//        two.setUpdateId("ads6587a56ds96dsaf");
//        
//        json.put(one.getUpdateId(), one);
//        json.put(two.getUpdateId(), two);
       
        // END TEST CODE
        
        List<CertificateUpdate> updates = certificateUpdateService.getAllCertificateUpdates();
        for (CertificateUpdate update : updates) {
            json.put(update.getUpdateId(), update);
        }
        
        return json;
    }
    
    @RequestMapping("/gateways/cert-update/{updateId}/details")
    public String details(ModelMap model, @PathVariable String updateId) {
        
        int dbId = certificateUpdateDao.getLatestUpdateForCertificate(updateId);
        CertificateUpdate update = certificateUpdateService.getCertificateUpdate(dbId);
        
//        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
//        CertificateUpdate update = new CertificateUpdate();
//        update.setFileName("licertupgrade.pkg.nm");
//        update.setTimestamp(new Instant().minus(Duration.standardDays(7)));
//        update.setSuccessful(Lists.newArrayList((RfnDevice)gateways.get(0)));
//        update.setFailed(Lists.newArrayList((RfnDevice)gateways.get(1)));
//        update.setUpdateId("654asd67f54as76f4v");
        
        model.addAttribute("update", update);
        
        return "gateways/cert.update.details.jsp";
    }
    
    @RequestMapping(value="/gateways/cert-update", method=RequestMethod.POST)
    public String certUpdate(HttpServletResponse resp, ModelMap model, YukonUserContext userContext, 
            @RequestParam("file") MultipartFile file, List<Integer> gateways) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        
        if (!file.isEmpty()) {
            try {
                String certificateId;
                if (gateways.size() == 0) {
                    certificateId = certificateUpdateService.sendUpdateAll(file);
                } else {
                    Set<RfnGateway> rfnGateways = rfnGatewayService.getGatewaysByPaoIds(gateways);
                    certificateId = certificateUpdateService.sendUpdate(rfnGateways, file);
                }
                
                // Success
                model.clear();
                
                int updateId = certificateUpdateDao.getLatestUpdateForCertificate(certificateId);
                CertificateUpdate update = certificateUpdateService.getCertificateUpdate(updateId);
                // TEST CODE
//                List<RfnGateway> rfgateways = Lists.newArrayList(rfnGatewayService.getAllGateways());
//                CertificateUpdate update = new CertificateUpdate();
//                update.setFileName("licertupgrade.pkg.nm");
//                update.setTimestamp(new Instant().minus(Duration.standardDays(7)));
//                update.setPending(Lists.newArrayList((RfnDevice)rfgateways.get(0)));
//                update.setFailed(Lists.newArrayList((RfnDevice)rfgateways.get(1)));
//                update.setUpdateId("654asd67f54as76f4v");
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