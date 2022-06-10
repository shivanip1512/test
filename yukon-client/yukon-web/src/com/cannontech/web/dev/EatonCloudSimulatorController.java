package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.common.util.jms.YukonJmsTemplateFactory;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.EatonCloudVersion;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandResponseV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCredentialsV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudDeviceDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudErrorHandlerV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSecretValueV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudServiceAccountDetailV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteDevicesV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudSiteV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDataRequestV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTimeSeriesDeviceResultV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.simulators.message.request.EatonCloudDataRetrievalSimulatonRequest;
import com.cannontech.simulators.message.request.EatonCloudRuntimeCalcSimulatonRequest;
import com.cannontech.simulators.message.request.EatonCloudSecretRotationSimulationRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorDeviceCreateRequest;
import com.cannontech.simulators.message.request.EatonCloudSimulatorSettingsUpdateRequest;
import com.cannontech.simulators.message.response.SimulatorResponse;
import com.cannontech.simulators.message.response.SimulatorResponseBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckCparm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;

@Controller
@RequestMapping("/eatonCloud/*")
@CheckCparm(MasterConfigBoolean.DEVELOPMENT_MODE)
public class EatonCloudSimulatorController {
    @Autowired private SimulatorsCommunicationService simulatorsCommunicationService;
    @Autowired EatonCloudCommunicationServiceV1 eatonCloudCommunicationServiceV1;
    @Autowired GlobalSettingDao settingDao;
    @Autowired EatonCloudDataReadService eatonCloudDataReadService;
    @Autowired private YukonJmsTemplateFactory jmsTemplateFactory;
    private YukonJmsTemplate jmsTemplate;
    private YukonJmsTemplate jmsTemplateCalc;
    private YukonJmsTemplate jmsTemplateSecretRotation;
    private RestTemplate restTemplate;
    private static final Logger log = YukonLogManager.getLogger(EatonCloudSimulatorController.class);
    private SimulatedEatonCloudSettings settings = new SimulatedEatonCloudSettings();
    
    @PostConstruct
    public void init() {
        jmsTemplate = jmsTemplateFactory.createTemplate(JmsApiDirectory.EATON_CLOUD_SIM_DEVICE_DATA_RETRIEVAL_REQUEST);
        jmsTemplateCalc = jmsTemplateFactory.createTemplate(JmsApiDirectory.EATON_CLOUD_SIM_RUNTIME_CALC_START_REQUEST);
        jmsTemplateSecretRotation = jmsTemplateFactory.createTemplate(JmsApiDirectory.EATON_CLOUD_SIM_SECRET_ROTATION_REQUEST);
    }
    @GetMapping("/home")
    public String home(ModelMap model) {
        model.addAttribute("endpoints", EatonCloudRetrievalUrl.values());
        model.addAttribute("settings", settings);
        String url = settingDao.getString(GlobalSettingType.EATON_CLOUD_URL);
        model.addAttribute("url", url);
        if (url.contains("localhost") || url.contains("127.0.0.1")) {
            model.addAttribute("isLocalHost", true);
            model.addAttribute("urlType", "Simulated URL");
        } else {
            model.addAttribute("urlType", "PX White URL");
        }

        model.addAttribute("autoCreationTypes", List.of(PaoType.LCR6200C, PaoType.LCR6600C));
        
        return "eatonCloud/home.jsp";
    }
    
    @GetMapping("/updateSecretInformation")
    public @ResponseBody Map<String, Object> updateSecretInformation(boolean enableTokenSecretRotationTesting) {
        Map<String, Object> json = new HashMap<>();
        String url = settingDao.getString(GlobalSettingType.EATON_CLOUD_URL);
        boolean isSimulator = url.contains("localhost") || url.contains("127.0.0.1");

        json.put("secret1Token", "None");
        json.put("secret2Token", "None");
        json.put("cachedToken", "None");
        
        if (isSimulator && enableTokenSecretRotationTesting) {
            if (restTemplate == null) {
                restTemplate = new RestTemplate();
                restTemplate.setErrorHandler(new EatonCloudErrorHandlerV1());
                restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));
            }
            json.put("cachedBy", "Simulator");
            String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
            try {
                EatonCloudTokenV1 token1 = retrieveNewToken(GlobalSettingType.EATON_CLOUD_SECRET, serviceAccountId);
                json.put("secret1Token", token1.getToken());
            } catch (Exception e) {
            }

            try {
                EatonCloudTokenV1 token2 = retrieveNewToken(GlobalSettingType.EATON_CLOUD_SECRET2, serviceAccountId);
                json.put("secret2Token", token2.getToken());
            } catch (Exception e) {
            }

            try {
                EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationServiceV1.getServiceAccountDetail();
                json.put("secret1Expiration", detail.getExpiryTime(1));
                json.put("secret2Expiration", detail.getExpiryTime(2));
            } catch (Exception e) {
                log.error("Error", e);
            }
        } else {
            json.put("cachedBy", isSimulator ? "Simulator" : "Cloud");
            json.put("secret1Expiration", null);
            json.put("secret2Expiration", null);
        }       

        try {
            String cachedToken = eatonCloudCommunicationServiceV1.getToken().getToken();
            json.put("cachedToken", cachedToken);
        } catch (Exception e) {
            log.error("Error", e);
        }
        
        json.put("secret1", settingDao.getString(GlobalSettingType.EATON_CLOUD_SECRET));
        json.put("secret2", settingDao.getString(GlobalSettingType.EATON_CLOUD_SECRET2));
        
        return json;
    }
    
    private EatonCloudTokenV1 retrieveNewToken(GlobalSettingType type, String serviceAccountId) {
        String url = EatonCloudRetrievalUrl.SECURITY_TOKEN.getUrl(settingDao, restTemplate);
        EatonCloudCredentialsV1 credentials = getCredentials(type, serviceAccountId);
        EatonCloudTokenV1 newToken = restTemplate.postForObject(url, credentials, EatonCloudTokenV1.class);
        return newToken;
    }
    
    private EatonCloudCredentialsV1 getCredentials(GlobalSettingType type, String serviceAccountId) {
        String secret = settingDao.getString(type);
        return new EatonCloudCredentialsV1(serviceAccountId, secret);
    }

    @PostMapping("/updateSettings")
    public String updateSettings(@ModelAttribute("settings") SimulatedEatonCloudSettings newSettings, FlashScope flashScope, ModelMap model) {
        try {   
            EatonCloudSimulatorSettingsUpdateRequest request = new EatonCloudSimulatorSettingsUpdateRequest(EatonCloudVersion.V1);
            request.setStatuses(getStatuses(newSettings));
            request.setSuccessPercentages(newSettings.getSuccessPercentages());
            request.setUnknownPercentages(newSettings.getUnknownPercentages());
            
            SimulatorResponse response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            
            if (response.isSuccessful()) {
                settings.setSelectedStatuses(newSettings.getSelectedStatuses());
                settings.setSuccessPercentages(newSettings.getSuccessPercentages());
                settings.setUnknownPercentages(newSettings.getUnknownPercentages());
                settings.setEnableTokenSecretRotationTesting(newSettings.isEnableTokenSecretRotationTesting());
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Updated simulator settings"));
                return "redirect:home";
            }
        } catch (ExecutionException e) {
            log.error("Error", e);
        }
        
        flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Failed to update simulator settings"));
        return "redirect:home";
    }
    
    private Map<EatonCloudRetrievalUrl, Integer> getStatuses(SimulatedEatonCloudSettings settings) {
        Map<EatonCloudRetrievalUrl, Integer> statuses = settings.getSelectedStatuses().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().value()));
        return statuses;
    }

    @GetMapping("/testEndpoint")
    public @ResponseBody Map<String, Object> testEndpoint(EatonCloudRetrievalUrl endpoint, String params, String jsonParam) {
        Map<String, Object> json = new HashMap<>();
        List<String> paramList = new ArrayList<>();
        try {
            if (!StringUtils.isEmpty(params)) {
                paramList = Stream.of(params.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }
            if (endpoint == EatonCloudRetrievalUrl.DEVICES_BY_SITE) {
                EatonCloudSiteDevicesV1 site = eatonCloudCommunicationServiceV1.getSiteDevices(paramList.get(0), parseBoolean(paramList, 1),
                        parseBoolean(paramList, 2));
                processSuccess(params, json, getFormattedJson(site));
            } else if (endpoint == EatonCloudRetrievalUrl.SECURITY_TOKEN) {
                EatonCloudTokenV1 token = eatonCloudCommunicationServiceV1.getToken();
                processSuccess(params, json, getFormattedJson(token));
            } else if (endpoint == EatonCloudRetrievalUrl.COMMANDS) {
                EatonCloudCommandRequestV1 request = new ObjectMapper().readValue(jsonParam, EatonCloudCommandRequestV1.class);
                EatonCloudCommandResponseV1 response = eatonCloudCommunicationServiceV1.sendCommand(paramList.get(0), request);
                processSuccess(params, json, getFormattedJson(response));
            } else if (endpoint == EatonCloudRetrievalUrl.TREND_DATA_RETRIEVAL) {
                EatonCloudTimeSeriesDataRequestV1 request = new ObjectMapper().readValue(jsonParam, EatonCloudTimeSeriesDataRequestV1.class);
                String startTime = request.getStartTime();
                String stopTime = request.getEndTime();
                DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
                DateTime startDateTime = parser.parseDateTime(startTime);
                DateTime stopDateTime = parser.parseDateTime(stopTime);

                Range<Instant> timeRange = new Range<Instant>(startDateTime.toInstant(), false, stopDateTime.toInstant(), false);
                List<EatonCloudTimeSeriesDeviceResultV1> response = eatonCloudCommunicationServiceV1.getTimeSeriesValues(request.getDevices(),
                        timeRange);
                processSuccess(params, json, getFormattedJson(response));
            } else if (endpoint == EatonCloudRetrievalUrl.DEVICE_DETAIL) {
                EatonCloudDeviceDetailV1 detail = eatonCloudCommunicationServiceV1.getDeviceDetails(paramList.get(0), parseBoolean(paramList, 1));
                processSuccess(params, json, getFormattedJson(detail));
            } 
            else if (endpoint == EatonCloudRetrievalUrl.SITES) {
                String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
                List<EatonCloudSiteV1> detail = eatonCloudCommunicationServiceV1.getSites(siteGuid);
                processSuccess(params, json, getFormattedJson(detail));
            } else if (endpoint == EatonCloudRetrievalUrl.ACCOUNT_DETAIL) {
                EatonCloudServiceAccountDetailV1 detail = eatonCloudCommunicationServiceV1.getServiceAccountDetail();
                processSuccess(params, json, getFormattedJson(detail));
            } else if (endpoint == EatonCloudRetrievalUrl.ROTATE_ACCOUNT_SECRET) {
                EatonCloudSecretValueV1 value = eatonCloudCommunicationServiceV1.rotateAccountSecret(1);
                processSuccess(params, json, getFormattedJson(value));
            }
        } catch (EatonCloudCommunicationExceptionV1 e) {
            processError(json, e);
        } catch (Exception e) {
            log.error("Error", e);
            json.put("alertError", e.getMessage());
        } 
        return json;
    }

    private void processSuccess(String params, Map<String, Object> json, String value) {
        log.info("params:{} json:{}", params, value);
        json.put("testResultJson", value);
    }

    private void processError(Map<String, Object> json, EatonCloudCommunicationExceptionV1 e) {
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
        eatonCloudCommunicationServiceV1.clearCache();
        json.put("userMessage", "Cache was successfully cleared.");
        return json; 
    }

    private String getFormattedJson(Object profile) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(profile);
    } 

    @PostMapping("/deviceAutoCreation")
    public String deviceAutoCreation(@ModelAttribute("paoType") PaoType paoType,
            @ModelAttribute("textInput") String total, FlashScope flashScope) {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        
        if(Strings.isNullOrEmpty(siteGuid)){
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("PX System Service Account Id is required, doesn't need to be real."));
            return "redirect:home";
        }
        try {
            // Send a request to simulator service with pao type and number of devices to create
            SimulatorResponse response = simulatorsCommunicationService
                    .sendRequest(new EatonCloudSimulatorDeviceCreateRequest(EatonCloudVersion.V1, paoType, Integer.parseInt(total)),
                            SimulatorResponseBase.class);
            // Simulator will cache the request and wait
            if (response.isSuccessful()) {
                flashScope.setConfirm(
                        YukonMessageSourceResolvable.createDefaultWithoutCode("See Service Manager logs for results."));
            }
            // Send request to SM to run auto device creation (otherwise it will run once a day)
            jmsTemplate.convertAndSend(new EatonCloudDataRetrievalSimulatonRequest(true));
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
    
    @PostMapping("/deviceAutoRead")
    public String deviceAutoRead(FlashScope flashScope) {
        String siteGuid = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        
        if(Strings.isNullOrEmpty(siteGuid)){
            flashScope.setError(YukonMessageSourceResolvable.createDefaultWithoutCode("PX System Service Account Id is required, doesn't need to be real."));
            return "redirect:home";
        }
        try {
            // Send request to SM to run read on all Could LCRs (otherwise it will run once an hour)
            jmsTemplate.convertAndSend(new EatonCloudDataRetrievalSimulatonRequest(false));
        } catch (Exception e) {
            log.error("Error", e);
        }
       
        return "redirect:home";
    }
    
    @PostMapping("/forceRuntimeCalc")
    public String forceRuntimeCalc(FlashScope flashScope) {
        try {
            jmsTemplateCalc.convertAndSend(new EatonCloudRuntimeCalcSimulatonRequest());
            flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Runtime calculation started. See SM log for details."));
        } catch (Exception e) {
            log.error("Error", e);
        }
        return "redirect:home";
    }
    
    @PostMapping("/rotateSecrets")
    public String rotateSecrets(FlashScope flashScope) {
        try {
            EatonCloudSimulatorSettingsUpdateRequest request = new EatonCloudSimulatorSettingsUpdateRequest(EatonCloudVersion.V1);
            request.setResetSecretsExpireTime(true);
            //notify simulator to changes secret expiration dates in preparation for the SM request to rotate secrets
            SimulatorResponse response = simulatorsCommunicationService.sendRequest(request, SimulatorResponseBase.class);
            if (response.isSuccessful()) {
                //send request to SM to start secret rotation
                jmsTemplateSecretRotation.convertAndSend(new EatonCloudSecretRotationSimulationRequest());
                flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Sent message to SM to rotate secrets."));
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
        return "redirect:home";
    }
    
    @PostMapping("/validateSecrets")
    public String validateSecrets(FlashScope flashScope) {
        try {
            // send request to SM to start secret rotation
            jmsTemplateSecretRotation.convertAndSend(new EatonCloudSecretRotationSimulationRequest());
            flashScope.setConfirm(YukonMessageSourceResolvable.createDefaultWithoutCode("Sent message to SM to rotate secrets."));
        } catch (Exception e) {
            log.error("Error", e);
        }
        return "redirect:home";
    }
}