package com.cannontech.web.stars.gateway;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cannontech.common.events.loggers.GatewayEventLogService;
import com.cannontech.common.exception.FileImportException;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.rfn.dao.GatewayCertificateUpdateDao;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.GatewayCertificateException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.common.util.FileUploadUtils;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.google.common.collect.Lists;

@Controller
@CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.VIEW)
public class GatewayCertificateController {
    
    private static final String baseKey = "yukon.web.modules.operator.gateways.";

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private GatewayCertificateUpdateDao certificateUpdateDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private GatewayEventLogService gatewayEventLogService;

    /** Certificate update popup. */
    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @GetMapping("/gateways/cert-update/options")
    public String certificate(ModelMap model) {
        return "gateways/certificateUpdatePopup.jsp";
    }

    @GetMapping("/gateways/cert-update/data")
    public @ResponseBody Map<Integer, Object> data() {
        Map<Integer, Object> json = new HashMap<>();
        List<CertificateUpdate> updates = certificateUpdateService.getAllCertificateUpdates();
        for (CertificateUpdate update : updates) {
            json.put(update.getYukonUpdateId(), update);
        }
        return json;
    }

    @GetMapping("/gateways/cert-update/{id}/details")
    public String details(ModelMap model, @PathVariable int id) {
        CertificateUpdate update = certificateUpdateService.getCertificateUpdate(id);
        model.addAttribute("update", update);
        return "gateways/cert.update.details.jsp";
    }

    @CheckPermissionLevel(property = YukonRoleProperty.MANAGE_INFRASTRUCTURE, level = HierarchyPermissionLevel.OWNER)
    @PostMapping("/gateways/cert-update")
    public String certUpdate(HttpServletResponse resp, ModelMap model, YukonUserContext userContext,
            @RequestParam("file") MultipartFile file, @RequestParam("gateways") Integer[] gateways) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        try {
            FileUploadUtils.validateCertUploadFileType(file);
            Set<RfnGateway> rfnGateways = rfnGatewayService.getGatewaysByPaoIds(Lists.newArrayList(gateways));
            String certificateId = certificateUpdateService.sendUpdate(rfnGateways, file);

            // Event logging
            gatewayEventLogService.sentCertificateUpdate(userContext.getYukonUser(), file.getOriginalFilename(),
                    certificateId, gateways.length);

            // Success
            model.clear();

            int updateId = certificateUpdateDao.getLatestUpdateForCertificate(certificateId);
            CertificateUpdate update = certificateUpdateService.getCertificateUpdate(updateId);

            // content type must be text or html or IE will throw up a save/open dialog
            resp.setContentType("text/html");
            JsonUtils.getWriter().writeValue(resp.getOutputStream(), update);
            return null;
        } catch (IOException e) {
            model.addAttribute("errorMsg", accessor.getMessage(baseKey + "cert.update.file.ioerror"));
            return setErrorResponseForCertificateUpdate(resp, model, gateways);
        } catch (GatewayCertificateException e) {
            model.addAttribute("errorMsg", accessor.getMessage(baseKey + "cert.update.file.invalid"));
            return setErrorResponseForCertificateUpdate(resp, model, gateways);
        } catch (FileImportException e) {
            model.addAttribute("errorMsg", accessor.getMessage(e.getMessage()));
            return setErrorResponseForCertificateUpdate(resp, model, gateways);
        }
    }

    private String setErrorResponseForCertificateUpdate(HttpServletResponse resp, ModelMap model, Integer[] gateways) {
        List<Integer> selectedGateways = new ArrayList<Integer>(); 
        Collections.addAll(selectedGateways, gateways); 
        model.addAttribute("selectedGateways", selectedGateways);
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        return "gateways/certificateUpdatePopup.jsp";
    }
}