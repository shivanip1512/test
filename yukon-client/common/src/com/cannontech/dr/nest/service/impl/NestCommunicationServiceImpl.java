package com.cannontech.dr.nest.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.NestEventLogService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.database.YNBoolean;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlEvent;
import com.cannontech.dr.nest.model.NestException;
import com.cannontech.dr.nest.model.NestURL;
import com.cannontech.dr.nest.model.v3.ControlEvent;
import com.cannontech.dr.nest.model.v3.CustomerEnrollment;
import com.cannontech.dr.nest.model.v3.CustomerEnrollments;
import com.cannontech.dr.nest.model.v3.CustomerInfo;
import com.cannontech.dr.nest.model.v3.Customers;
import com.cannontech.dr.nest.model.v3.EnrollmentState;
import com.cannontech.dr.nest.model.v3.EventId;
import com.cannontech.dr.nest.model.v3.RetrieveCustomers;
import com.cannontech.dr.nest.model.v3.RushHourEventType;
import com.cannontech.dr.nest.model.v3.SchedulabilityError;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.core.JsonProcessingException;

public class NestCommunicationServiceImpl implements NestCommunicationService{
    
    private final RestTemplate restTemplate;
    private static final Logger log = YukonLogManager.getLogger(NestCommunicationServiceImpl.class); 
    private Proxy proxy;
    private GlobalSettingDao settingDao;
    @Autowired private NestDao nestDao;
    @Autowired private NestEventLogService nestEventLogService;
    //this id probably will be in global settings, I am assuming EC will get it from Nest
    private String partnerId = "simulator";
         
    @Autowired
    public NestCommunicationServiceImpl(GlobalSettingDao settingDao) {
        this.settingDao = settingDao;
        String nestUrl = "";//settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
        restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (useProxy(nestUrl)) {
            YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                factory.setProxy(httpProxy.getJavaHttpProxy());
            });
        }
        restTemplate.setRequestFactory(factory);
    }

    /**
     * v2 example
     * curl https://enterprise-api.nest.com/api/energy/v2/rush_hour_rewards/events/standard -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA==" -H "Content-Type: application/json" -d "{\"start_time\":\"2018-09-14T00:00:00.000Z\",\"duration\":\"PT30M\",\"groups\":[\"Test\"],\"load_shaping_options\":{\"preparation_load_shaping\":\"STANDARD\",\"peak_load_shaping\":\"STANDARD\",\"post_peak_load_shaping\":\"STANDARD\"}}"
     */
    @Override
    public Optional<SchedulabilityError> sendEvent(ControlEvent event, RushHourEventType type) {
        log.debug("Sending {} event {}", type, event);
        String requestUrl = constructNestUrl(NestURL.CREATE_EVENT);
        nestEventLogService.sendStartEvent(event.getStart(), event.getDuration(), type.name());
        String response = getNestResponse(requestUrl, event, type);
        try {
            EventId nestId = JsonUtils.fromJson(response, EventId.class);
            NestControlEvent history = new NestControlEvent();
            history.setGroup(event.getGroupIds().get(0));
            history.setStartTime(event.getStart());
            history.setStopTime(event.getStop());
            history.setKey(nestId.getId());
            //create entry only if response is success
            nestDao.saveControlEvent(history);
            nestEventLogService.responseStartEvent(event.getStart(), event.getStop(), event.getDuration(), type.name(), StringUtils.abbreviate(response, 2000));
            return Optional.empty();
        } catch (Exception e) {
            try {
                SchedulabilityError error = JsonUtils.fromJson(response, SchedulabilityError.class);
                log.error("Reply from Nest contains an error="+ error);
                nestEventLogService.responseStartEvent(event.getStart(), event.getStop(), event.getDuration(), type.name(), error.name());
                return Optional.of(error);
            } catch (IOException e1) {
                throw new NestException("Error getting valid reponse from Nest.", e);
            }
        }
    }
    
    @Override
    public Optional<String> cancelEvent(String key, NestURL url) {
        log.debug("{} event {}", url, key);
        String requestUrl = constructNestUrl(url);
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("partnerId", partnerId);
        urlParams.put("eventId", key);
        
        try {
            HttpEntity<String> response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class, urlParams);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }
    
    @Override
    public Optional<String> cancelEvent(NestControlEvent event) {
        // POST https://energy.api.nest.com/v3/partners/{partnerId}/rushHourEvents/{eventId}:cancel
        log.debug("Canceling event {}", event);
        try {
            nestEventLogService.sendCancelEvent(event.getKey(), event.getGroup(), event.getStartTime());
            HttpEntity<String> response = sendStopOrCancelRequest(event, NestURL.CANCEL_EVENT);
            nestEventLogService.responseCancelEvent(event.getKey(), event.getGroup(), event.getStartTime(), event.getCancelRequestTime(), event.getSuccess().getBoolean());

            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }

    @Override
    public Optional<String> stopEvent(NestControlEvent event) {
        // POST https://energy.api.nest.com/v3/partners/{partnerId}/rushHourEvents/{eventId}:stop
        log.debug("Stopping event {}", event);
        try {
            nestEventLogService.sendStopEvent(event.getKey(), event.getGroup(), event.getStartTime());
            HttpEntity<String> response = sendStopOrCancelRequest(event, NestURL.STOP_EVENT);
            nestEventLogService.responseStopEvent(event.getKey(), event.getGroup(), event.getStartTime(), event.getStopTime(), event.getSuccess().getBoolean());

            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }

    /**
     * Sends stop/cancel event request to Nest, persists results in the database
     */
    private HttpEntity<String> sendStopOrCancelRequest(NestControlEvent event, NestURL url) {
        // Saving the cancellation/stop request initiation to database
        event.setCancelOrStop(url == NestURL.CANCEL_EVENT ? "C" : "S");
        event.setCancelRequestTime(Instant.now());
        nestDao.saveControlEvent(event);

        // Create URL for cancel or stop
        String requestUrl = constructNestUrl(url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());

        HttpEntity<?> entity = new HttpEntity<>(headers);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("partnerId", partnerId);
        urlParams.put("eventId", event.getKey());

        HttpEntity<String> response =
            restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class, urlParams);
        log.debug("Response from Nest {}", response.getBody());
        // We are not sure what response we will get from nest
        if (response.getBody() == null) {
            event.setSuccess(YNBoolean.YES);
        } else {
            event.setSuccess(YNBoolean.NO);
            event.setCancelResponse(response.getBody());
        }
        // Persisting response to the database;
        nestDao.saveControlEvent(event);
        return response;
    }
        
    @Override 
    public String constructNestUrl(NestURL url) {
        return constructNestUrl(NestURL.CURRENT_VERSION, url);        
    }
    
    @Override 
    public String constructNestUrl(int version, NestURL url) {
        return ""; /*
        String globalUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
        return url.buildUrl(version, globalUrl);        */
    }
    
    @Override
    public Optional<String> updateEnrollment(CustomerEnrollment enrollment) {
        //PUT https://energy.api.nest.com/v3/partners/{partnerId}/customers:batchUpdateEnrollments
        log.debug("Updating group to {}", enrollment);
        String requestUrl = constructNestUrl(NestURL.ENROLLMENT);
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        CustomerEnrollments enrollments = new CustomerEnrollments();
        enrollments.addEnrollment(enrollment);
        HttpEntity<CustomerEnrollments> requestEntity = new HttpEntity<>(enrollments, headers);
        try {
            nestEventLogService.sendUpdateEnrollment(enrollment.getCustomerId(), enrollment.getGroupId());
            HttpEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, requestEntity, String.class, partnerId);
            //nestEventLogService.responseUpdateEnrollment(enrollment.getCustomerId(), enrollment.getGroupId(), enrollment.getRejectionReason().name());
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }
    

    @Override
    public List<CustomerInfo> retrieveCustomers(EnrollmentState state) {
        //GET https://energy.api.nest.com/v3/partners/{partnerId}/customers
        log.debug("Retrieving {} customers from Nest", state);
        String requestUrl = constructNestUrl(NestURL.GET_CUSTOMER_LIST);
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        RetrieveCustomers retrieve = new  RetrieveCustomers();
        retrieve.setEnrollmentStateFilter(state);
        HttpEntity<RetrieveCustomers> requestEntity = new HttpEntity<>(retrieve, headers);
        try {
            nestEventLogService.sendRetrieveCustomers(state.name());
            ResponseEntity<Customers> response = restTemplate.exchange(requestUrl, HttpMethod.GET, requestEntity, Customers.class, partnerId);
            nestEventLogService.responseRetrieveCustomers(response.getBody().getCustomers().size());
            return response.getBody().getCustomers();
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }
    
    @Override
    public String getNestResponse(String url, ControlEvent event, RushHourEventType type) {
        //POST https://energy.api.nest.com/v3/partners/{partnerId}/rushHourEvents/{eventType}
        log.debug("Request url {}", url);
        try {
            log.debug(JsonUtils.toJson(event));
        } catch (JsonProcessingException e) {
            throw new NestException("Unable to convert " + event + " to json");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("partnerId", partnerId);
        urlParams.put("eventType", type.name());

        HttpEntity<ControlEvent> entity = new HttpEntity<>(event, headers);
        try {
            ResponseEntity<Object> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, Object.class, urlParams);
            log.debug("response:" + response.getBody());
            return JsonUtils.toJson(response.getBody());
        } catch (HttpClientErrorException e) {
            log.error("Error getting response from Nest:" + e.getResponseBodyAsString(), e);
            return e.getResponseBodyAsString();
        } catch (JsonProcessingException e) {
            log.error("Error parsing json from Nest", e);
            return e.getMessage();
        }
    }
        
    @Override
    public boolean useProxy(String stringUrl) {
        if (proxy == null) {
            return false;
        } else if ((stringUrl.contains("localhost") || stringUrl.contains("127.0.0.1"))) {
            return false;
        }
        return true;
    }
     
    @Override
    public String encodeAuthorization() {
        /*
        String key = settingDao.getString(GlobalSettingType.NEST_USERNAME) + ":" + settingDao.getString(GlobalSettingType.NEST_PASSWORD);
        try {
            return "Basic " + DatatypeConverter.printBase64Binary(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new NestException("Error encoding Nest key", e);
        }
        */
        return "";
    }

}
