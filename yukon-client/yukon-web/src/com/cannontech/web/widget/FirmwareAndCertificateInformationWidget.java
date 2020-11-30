package com.cannontech.web.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.rfn.dao.RfnGatewayFirmwareUpgradeDao;
import com.cannontech.common.rfn.model.CertificateUpdate;
import com.cannontech.common.rfn.model.NmCommunicationException;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayCertificateUpdateService;
import com.cannontech.common.rfn.service.RfnGatewayService;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.widget.support.AdvancedWidgetControllerBase;
import com.google.common.collect.Lists;

@Controller
@RequestMapping("/firmwareAndCertificateInformationWidget")
public class FirmwareAndCertificateInformationWidget extends AdvancedWidgetControllerBase {

    @Autowired private RfnGatewayService rfnGatewayService;
    @Autowired private RfnGatewayCertificateUpdateService certificateUpdateService;
    @Autowired private RfnGatewayFirmwareUpgradeDao firmwareUpgradeDao;
    private static final Logger log = YukonLogManager.getLogger(FirmwareAndCertificateInformationWidget.class);

    @GetMapping("render")
    public String render(ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        List<RfnGateway> gateways = Lists.newArrayList(rfnGatewayService.getAllGateways());

        List<String> gatewayReleaseVersion = new ArrayList<>();

        gateways.forEach(gw -> {
            if (gw.getData() != null) {
                gatewayReleaseVersion.add(gw.getData().getReleaseVersion());
            }
        });

        // Build a string of Gateway current versions
        StringBuilder gatewayReleaseVersions = new StringBuilder();
        if (!gatewayReleaseVersion.isEmpty()) {
            gatewayReleaseVersions.append(gatewayReleaseVersion.stream().distinct().collect(Collectors.joining(", ")));
            model.addAttribute("gatewayReleaseVersions", gatewayReleaseVersions);
            model.addAttribute("isCurrentVersionAvailable", true);
        }

        // Build a string of Gateway available versions
        Map<String, String> gatewayAvailableVersion = new HashMap<>();
        StringBuilder gatewayAvailableVersions = new StringBuilder();
        try {
            if (!gatewayReleaseVersion.isEmpty()) {
                gatewayAvailableVersion = firmwareUpgradeDao.getFirmwareUpdateServerVersions(gateways);
                gatewayAvailableVersions.append(gatewayAvailableVersion.values().stream().distinct().collect(Collectors.joining(", ")));
                model.addAttribute("gatewayAvailableVersions", gatewayAvailableVersions);
                model.addAttribute("isUpgradeVersionAvailable", true);
            }
        } catch (NmCommunicationException e) {
            log.error("Error while retrieving the list of available versions", e);
            model.addAttribute("isUpgradeVersionAvailable", false);
        }

        // Sort the certificate updates entries in descending order by timestamp
        List<CertificateUpdate> certUpdates = certificateUpdateService.getAllCertificateUpdates();
        Comparator<CertificateUpdate> comparator = (o1, o2) -> {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        };
        Collections.sort(certUpdates, Collections.reverseOrder(comparator));
        if (certUpdates.size() > 3) {
            certUpdates = certUpdates.subList(0, 3);
        }
        model.addAttribute("certUpdates", certUpdates);

        return "firmwareAndCertificateInformationWidget/render.jsp";
    }

}
