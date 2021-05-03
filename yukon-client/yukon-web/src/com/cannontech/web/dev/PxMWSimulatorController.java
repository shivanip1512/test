package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.PxMWVersion;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceDetail;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDeviceResultV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.dr.pxmw.service.v1.PxMWDataReadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.PxMWDeviceAutoCreationSimulatonRequest;
import com.cannontech.simulators.message.request.PxMWSimulatorDeviceCreateRequest;
import com.cannontech.simulators.message.request.PxMWSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/pxMiddleware/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PxMWSimulatorController {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;
    @Autowired GlobalSettingDao settingDao;
    @Autowired PxMWDataReadService pxMWDataReadService;
    private static final Logger log = YukonLogManager.getLogger(PxMWSimulatorController.class);
    private SimulatedPxMWSettings settings = new SimulatedPxMWSettings();
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;

    
    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.PxMW_SIM_DEVICE_AUTO_CREATION_REQUEST);
    }
    @GetMapping("/home")
    public String home(ModelMap model) {
        model.addAttribute("endpoints", PxMWRetrievalUrl.values());
        model.addAttribute("settings", settings);
        String url = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_URL);
        model.addAttribute("url", url);
        if (url.contains("localhost") || url.contains("127.0.0.1")) {
            model.addAttribute("isLocalHost", true);
            model.addAttribute("urlType", "Simulated URL");
        } else {
            model.addAttribute("urlType", "PX White URL");
        }

        model.addAttribute("autoCreationTypes", List.of(PaoType.LCR6200C, PaoType.LCR6600C));

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
            log.error("Error", e);
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

    @GetMapping("/testEndpoint")
    public @ResponseBody Map<String, Object> testEndpoint(PxMWRetrievalUrl endpoint, String params, String jsonParam) {
        Map<String, Object> json = new HashMap<>();
        List<String> paramList = new ArrayList<>();
        try {
            if (!StringUtils.isEmpty(params)) {
                paramList = Stream.of(params.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }
            if (endpoint == PxMWRetrievalUrl.DEVICES_BY_SITE) {
                PxMWSiteV1 site = pxMWCommunicationServiceV1.getSiteDevices(paramList.get(0), parseBoolean(paramList, 1),
                        parseBoolean(paramList, 2));
                processSuccess(params, json, getFormattedJson(site));
            } else if (endpoint == PxMWRetrievalUrl.SECURITY_TOKEN) {
                PxMWTokenV1 token = pxMWCommunicationServiceV1.getToken();
                processSuccess(params, json, getFormattedJson(token));
            } else if (endpoint == PxMWRetrievalUrl.COMMANDS) {
                PxMWCommandRequestV1 request = new ObjectMapper().readValue(jsonParam, PxMWCommandRequestV1.class);
                pxMWCommunicationServiceV1.sendCommand(paramList.get(0), paramList.get(1), request);
            } else if (endpoint == PxMWRetrievalUrl.TREND_DATA_RETRIEVAL) {
                PxMWTimeSeriesDataRequestV1 request = new ObjectMapper().readValue(jsonParam, PxMWTimeSeriesDataRequestV1.class);
                String startTime = request.getStartTime();
                String stopTime = request.getEndTime();
                DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
                DateTime startDateTime = parser.parseDateTime(startTime);
                DateTime stopDateTime = parser.parseDateTime(stopTime);

                Range<Instant> timeRange = new Range<Instant>(startDateTime.toInstant(), false, stopDateTime.toInstant(), false);
                List<PxMWTimeSeriesDeviceResultV1> response = pxMWCommunicationServiceV1.getTimeSeriesValues(request.getDevices(),
                        timeRange);
                processSuccess(params, json, getFormattedJson(response));
            } else if (endpoint == PxMWRetrievalUrl.DEVICE_DETAIL) {
                PxMWDeviceDetail detail = pxMWCommunicationServiceV1.getDeviceDetails(paramList.get(0), parseBoolean(paramList, 1));
                processSuccess(params, json, getFormattedJson(detail));
            }
        } catch (PxMWCommunicationExceptionV1 e) {
            processError(json, e);
        } catch (PxMWException | JsonProcessingException e) {
            json.put("alertError", e.getMessage());
        } 
        return json;
    }

    private void processSuccess(String params, Map<String, Object> json, String value) {
        log.info("params:{} json:{}", params, value);
        json.put("testResultJson", value);
    }

    private void processError(Map<String, Object> json, PxMWCommunicationExceptionV1 e) {
        log.info(e.getErrorMessage());
        json.put("errorMessage", getFormattedJson(e.getErrorMessage()));
    }
    
    private Boolean parseBoolean(List<String> paramList, int index) {
        try {
            return Boolean.parseBoolean(paramList.get(index));
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/clearCache")
    public @ResponseBody Map<String, Object> clearCache() {
        Map<String, Object> json = new HashMap<>();
        pxMWCommunicationServiceV1.clearCache();
        json.put("userMessage", "Cache was successfully cleared.");
        return json;   
    }

    private String getFormattedJson(Object profile) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(profile);
    } 

    @PostMapping("/deviceAutoCreation")
    public String deviceAutoCreation(@ModelAttribute("paoType") PaoType paoType,
            @ModelAttribute("textInput") String total, FlashScope flashScope) {
        String siteGuid = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SERVICE_ACCOUNT_ID);
        
        if(Strings.isNullOrEmpty(siteGuid)){
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("PX System Service Account Id is required, doesn't need to be real."));
            return "redirect:home";
        }
        try {
            // Send a request to simulator service with pao type and number of devices to create
            SimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new PxMWSimulatorDeviceCreateRequest(PxMWVersion.V1, paoType, Integer.parseInt(total)),
                            SimulatorResponseBase.class);
            // Simulator will cache the request and wait
            if (response.isSuccessful()) {
                flashScope.setConfirm(
                        YukonMessageSourceResolvable.createDefaultWithoutCode("See Service Manager logs for results."));
            }
            // Send request to SM to run auto device creation (otherwise it will run once a day)
            jmsTemplate.convertAndSend(new PxMWDeviceAutoCreationSimulatonRequest());
            // SM sends request to simulator
            // Simulator builds responses from cached values
            // Simulator waits for SM to tell it that auto creation is complete
            // Simulator clears its cache
            // When device auto creation runs again, no devices are created
        } catch (ExecutionException e) {
            log.error("Error", e);
        }
       
        return "redirect:home";
    }
}

