package com.cannontech.web.stars.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.rfn.model.GatewayFirmwareUpdateStatus;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateResult;
import com.cannontech.common.rfn.model.RfnGatewayFirmwareUpdateSummary;
import com.cannontech.common.rfn.service.NMConfigurationService;
import com.cannontech.common.rfn.service.RfnGatewayFirmwareUpgradeService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.web.security.annotation.CheckRole;

@Controller
@CheckRole(YukonRole.INVENTORY)
public class GatewayFirmwareUpdateController {
    
    @Autowired private NMConfigurationService nmConfigurationService;
    @Autowired private RfnGatewayFirmwareUpgradeService firmwareUpgradeService;
    @Autowired private RfnGatewayService rfnGatewayService;
    
    /**
     * Ajax updates of firmware section on gateway list page.
     */
    @RequestMapping("/gateways/firmware-update/data")
    public @ResponseBody Map<Integer, Object> data() {
        
        Map<Integer, Object> json = new HashMap<>();
        
        if (nmConfigurationService.isFirmwareUpdateSupported()) {
            List<RfnGatewayFirmwareUpdateSummary> results = firmwareUpgradeService.getFirmwareUpdateSummaries();
            for (RfnGatewayFirmwareUpdateSummary result : results) {
                json.put(result.getUpdateId(), result);
            }
        }
        
        return json;
    }
    
    /**
     * Firmware update details popup.
     */
    @RequestMapping("/gateways/firmware-update/{updateId}/details")
    public String firmwareUpdates(ModelMap model, @PathVariable int updateId) {
        List<RfnGatewayFirmwareUpdateResult> results = firmwareUpgradeService.getFirmwareUpgradeResults(updateId);
        model.addAttribute("results", new RfnGatewayFirmwareUpdateResults(results));
        model.addAttribute("gateways", getGatewaysMap());
        
        return "gateways/firmware.update.details.jsp";
    }
    
    /**
     * Get a map of gateway paoIds to gateways.
     */
    private Map<Integer, RfnGateway> getGatewaysMap() {
        Set<RfnGateway> gatewaySet = rfnGatewayService.getAllGateways();
        Map<Integer, RfnGateway> gateways = new HashMap<>();
        for (RfnGateway gateway : gatewaySet) {
            gateways.put(gateway.getPaoIdentifier().getPaoId(), gateway);
        }
        return gateways;
    }
    
    public static class RfnGatewayFirmwareUpdateResults {
        private List<RfnGatewayFirmwareUpdateResult> failed = new ArrayList<>();
        private List<RfnGatewayFirmwareUpdateResult> success = new ArrayList<>();
        private List<RfnGatewayFirmwareUpdateResult> pending = new ArrayList<>();

        public RfnGatewayFirmwareUpdateResults(List<RfnGatewayFirmwareUpdateResult> results) {
            for (RfnGatewayFirmwareUpdateResult result : results) {
                if (result.getStatus() == GatewayFirmwareUpdateStatus.ACCEPTED) {
                    success.add(result);
                } else if (result.getStatus() == GatewayFirmwareUpdateStatus.STARTED) {
                    pending.add(result);
                } else {
                    //everything else is a failure
                    failed.add(result);
                }
            }
        }

        public List<RfnGatewayFirmwareUpdateResult> getFailed() {
            return failed;
        }

        public List<RfnGatewayFirmwareUpdateResult> getSuccess() {
            return success;
        }

        public List<RfnGatewayFirmwareUpdateResult> getPending() {
            return pending;
        }
        
        public void setFailed(List<RfnGatewayFirmwareUpdateResult> failedResults) {
            failed = failedResults;
        }

        public void setSuccess(List<RfnGatewayFirmwareUpdateResult> successResults) {
            success = successResults;
        }

        public void setPending(List<RfnGatewayFirmwareUpdateResult> pendingResults) {
            pending = pendingResults;
        }
    }
    
}
