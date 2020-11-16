package com.cannontech.web.dev;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.PxMWSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/pxMiddleware/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PxMWSimulatorController {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;
    private static final Logger log = YukonLogManager.getLogger(PxMWSimulatorController.class);
    private SimulatedPxMWSettings settings = new SimulatedPxMWSettings();

    @GetMapping("/home")
    public String home(ModelMap model) {
        model.addAttribute("endpoints", PxMWRetrievalUrl.values());
        model.addAttribute("settings", settings);
        return "pxMW/home.jsp";
    }

    @PostMapping("/updateSettings")
    public String updateSettings(@ModelAttribute("settings") SimulatedPxMWSettings settings, FlashScope flashScope, ModelMap model) {
        try {
            SimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorSettingsUpdateRequest(getStatuses(settings)), SimulatorResponseBase.class);
            if (response.isSuccessful()) {
                this.settings = settings;
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Updated simulator settings"));
                return "redirect:home";
            }
        } catch (ExecutionException e) {
            log.error(e);
        }
        flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Failed to update simulator settings"));
        return "redirect:home";
    }
    private Map<PxMWRetrievalUrl, Integer> getStatuses(SimulatedPxMWSettings settings) {
        Map<PxMWRetrievalUrl, Integer> statuses = settings.getSelectedStatuses().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().value()));
        return statuses;
    }

    @PostMapping("/testEndpoint")
    public String testEndpoint(PxMWRetrievalUrl endpoint,  FlashScope flashScope, ModelMap model) {
        if (endpoint == PxMWRetrievalUrl.DEVICE_PROFILE_BY_GUID_V1) {
            PxMWDeviceProfileV1 profile;
            try {
                profile = pxMWCommunicationServiceV1.getDeviceProfile("A", "222222-d832-49d6-ab60-6212a63bcd10");
                log.info(getFormattedJson(profile));
            } catch (PxMWCommunicationExceptionV1 e) {
                log.info(e.getErrorMessage());
            }
        } else if (endpoint == PxMWRetrievalUrl.DEVICES_BY_SITE_V1) {
            try {
                PxMWSiteV1 site = pxMWCommunicationServiceV1.getSite("A", "222222-d832-49d6-ab60-6212a63bcd10", null, null);
                log.info(getFormattedJson(site));
            } catch (PxMWCommunicationExceptionV1 e) {
                log.info(getFormattedJson(e.getErrorMessage()));
            }
        }
        return "redirect:home";
    }

    private String getFormattedJson(Object profile) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(profile);
    }
}

