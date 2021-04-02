package com.cannontech.web.dev;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.cannontech.common.util.Range;
import com.cannontech.dr.pxmw.model.PxMWException;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWChannelValueV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceTimeseriesLatestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTimeSeriesDataRequestV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.PxMWSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/pxMiddleware/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class PxMWSimulatorController {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired PxMWCommunicationServiceV1 pxMWCommunicationServiceV1;
    @Autowired GlobalSettingDao settingDao;
    private static final Logger log = YukonLogManager.getLogger(PxMWSimulatorController.class);
    private SimulatedPxMWSettings settings = new SimulatedPxMWSettings();

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

    @GetMapping("/testEndpoint")
    public @ResponseBody Map<String, Object> testEndpoint(PxMWRetrievalUrl endpoint, String params) {
        Map<String, Object> json = new HashMap<>();

        if (StringUtils.isEmpty(params)) {
            json.put("alertError", "Unable to parse parameters, please see parameter help text.");
            return json;
        }
        try {
            String jsonParam = params.substring(params.indexOf("{"), params.lastIndexOf("}") + 1);
            if(!StringUtils.isEmpty(jsonParam)) {
                log.info(jsonParam);
                params = StringUtils.replace(params, jsonParam, "");
            }
            List<String> paramList = Stream.of(params.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            if (endpoint == PxMWRetrievalUrl.DEVICE_PROFILE_BY_GUID_V1) {
                PxMWDeviceProfileV1 profile = pxMWCommunicationServiceV1.getDeviceProfile(paramList.get(0));
                processSuccess(params, json, getFormattedJson(profile));
            } else if (endpoint == PxMWRetrievalUrl.DEVICES_BY_SITE_V1) {
                PxMWSiteV1 site = pxMWCommunicationServiceV1.getSite(paramList.get(0), parseBoolean(paramList, 1),
                        parseBoolean(paramList, 2));
                processSuccess(params, json, getFormattedJson(site));
            } else if (endpoint == PxMWRetrievalUrl.DEVICE_TIMESERIES_LATEST) {
                String deviceGuid = paramList.get(0);
                paramList.remove(deviceGuid);
                PxMWDeviceTimeseriesLatestV1 details = pxMWCommunicationServiceV1.getTimeseriesLatest(deviceGuid, paramList);
                processSuccess(params, json, getFormattedJson(details));
            } else if (endpoint == PxMWRetrievalUrl.SECURITY_TOKEN) {
                PxMWTokenV1 token = pxMWCommunicationServiceV1.getToken();
                processSuccess(params, json, getFormattedJson(token));
            } else if (endpoint == PxMWRetrievalUrl.DEVICE_GET_CHANNEL_VALUES_V1) {
                String deviceGuid = paramList.get(0);
                paramList.remove(deviceGuid);
                List<PxMWChannelValueV1> values = pxMWCommunicationServiceV1.getChannelValues(paramList.get(0), paramList);
                processSuccess(params, json, getFormattedJson(values));
            } else if (endpoint == PxMWRetrievalUrl.CLOUD_ENABLE) {
                pxMWCommunicationServiceV1.cloudEnable(paramList.get(0), parseBoolean(paramList, 1));
            } else if (endpoint == PxMWRetrievalUrl.COMMANDS) {
                try {
                    PxMWCommandRequestV1 request = new ObjectMapper().readValue(jsonParam, PxMWCommandRequestV1.class);
                    pxMWCommunicationServiceV1.sendCommand(paramList.get(0), paramList.get(1), request);
                } catch (JsonProcessingException e) {
                    json.put("alertError", e.getMessage());
                }
            } else if (endpoint == PxMWRetrievalUrl.TREND_DATA_RETRIEVAL) {
                try {
                    PxMWTimeSeriesDataRequestV1 request = new ObjectMapper().readValue(jsonParam, PxMWTimeSeriesDataRequestV1.class);
                    String startTime = request.getStartTime();
                    String stopTime = request.getEndTime();
                    DateTimeFormatter parser = ISODateTimeFormat.dateTime();
                    DateTime startDateTime = parser.parseDateTime(startTime);
                    DateTime stopDateTime = parser.parseDateTime(stopTime);
                            
                    Range<Instant> timeRange = new Range<Instant>(startDateTime.toInstant(), false, stopDateTime.toInstant(), false);
                    pxMWCommunicationServiceV1.getTimeSeriesValues(request.getDevices(), timeRange);
                } catch (JsonProcessingException e) {
                    json.put("alertError", e.getMessage());
                }


            }
        } catch (PxMWCommunicationExceptionV1 e) {
            processError(json, e);
        } catch (PxMWException e) {
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
}

