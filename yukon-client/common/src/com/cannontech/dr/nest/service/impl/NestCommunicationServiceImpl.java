package com.cannontech.dr.nest.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestControlHistory;
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
    private NestDao nestDao;
    //this id probably will be in global settings, I am assuming EC will get it from Nest
    private String partnerId = "simulator";
         
    @Autowired
    public NestCommunicationServiceImpl(GlobalSettingDao settingDao) {
        this.settingDao = settingDao;
        proxy = YukonHttpProxy.fromGlobalSetting(settingDao)
                .map(YukonHttpProxy::getJavaHttpProxy)
                .orElse(null);
        restTemplate = new RestTemplate();
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if(proxy != null) {
            factory.setProxy(proxy);
        }
        restTemplate.setRequestFactory(factory);
    }

    /**
     * curl https://enterprise-api.nest.com/api/energy/v2/rush_hour_rewards/events/standard -v -x proxy.etn.com:8080 -H "Authorization:Basic U2FtdWVsVEpvaG5zdG9uQGVhdG9uLmNvbTo3MjRiYzkwMWQ3MDE0YWUyNjA5OGJhZjk1ZjVjMTRiNA==" -H "Content-Type: application/json" -d "{\"start_time\":\"2018-09-14T00:00:00.000Z\",\"duration\":\"PT30M\",\"groups\":[\"Test\"],\"load_shaping_options\":{\"preparation_load_shaping\":\"STANDARD\",\"peak_load_shaping\":\"STANDARD\",\"post_peak_load_shaping\":\"STANDARD\"}}"
     */
    @Override
    public Optional<SchedulabilityError> sendEvent(ControlEvent event, RushHourEventType type) {
        log.debug("Sending request to create standard event");
        String requestUrl = constructNestUrl(NestURL.CANCEL_EVENT);
        log.debug("Request url {}", requestUrl);
        String response = getNestResponse(requestUrl, event, type);
        try {
            EventId nestId = JsonUtils.fromJson(response, EventId.class);
            NestControlHistory history = new NestControlHistory();
            history.setStartTime(event.getStart());
            history.setStopTime(event.getStop());
            history.setKey(nestId.getId());
            //create entry only if response is success
            nestDao.createControlHistory(history);
            return Optional.empty();
        } catch (IOException e) {
            try {
                SchedulabilityError error = JsonUtils.fromJson(response, SchedulabilityError.class);
                log.error("Reply from Nest contains an error="+ error);
                return Optional.of(error);
            } catch (IOException e1) {
                throw new NestException("Error getting valid reponse from Nest.", e);
            }
        }
    }
    
    @Override
    public Optional<String> cancelEvent(NestControlHistory history) {
        //POST https://energy.api.nest.com/v3/partners/{partnerId}/rushHourEvents/{eventId}:cancel
        log.debug("Canceling event {}", history);
        String requestUrl = constructNestUrl(NestURL.STOP_EVENT);
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
            //add type cancel
            nestDao.updateCancelRequestTime(history.getId());
            HttpEntity<String> response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class, partnerId, history.getKey());
            nestDao.updateNestResponse(history.getId(), response.getBody());
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }
    
    @Override
    public Optional<String> stopEvent(NestControlHistory history) {
        //POST https://energy.api.nest.com/v3/partners/{partnerId}/rushHourEvents/{eventId}:stop
        log.debug("Stopping event {}", history);    
        String requestUrl = constructNestUrl(NestURL.STOP_EVENT);
        log.debug("Request url {}", requestUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", encodeAuthorization());
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        try {
           //add type stop
            nestDao.updateCancelRequestTime(history.getId());
            HttpEntity<String> response =
                restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class, partnerId, history.getKey());
            nestDao.updateNestResponse(history.getId(), response.getBody());
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            throw new NestException("Error getting valid reponse from Nest.", e);
        }
    }
    
    @Override 
    public String constructNestUrl(NestURL url) {
        return constructNestUrl(NestURL.CURRENT_VERSION, url);        
    }
    
    @Override 
    public String constructNestUrl(int version, NestURL url) {
        String globalUrl = settingDao.getString(GlobalSettingType.NEST_SERVER_URL);
        return url.buildUrl(version, globalUrl);        
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
        try {
            String body = JsonUtils.toJson(enrollments);
            log.debug("json {}", body);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            try {
                HttpEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.PUT, entity, String.class, partnerId);
                return Optional.ofNullable(response.getBody());
            } catch (HttpClientErrorException e) {
                throw new NestException("Error getting valid reponse from Nest.", e);
            }
        } catch (JsonProcessingException e1) {
            throw new NestException("Unable create json from:" + enrollment, e1);
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
        try {
            String body = JsonUtils.toJson(retrieve);
            HttpEntity<?> entity = new HttpEntity<>(body, headers);
            try {
                HttpEntity<Customers> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, Customers.class);
                return response.getBody().getCustomers();
            } catch (HttpClientErrorException e) {
                throw new NestException("Error getting valid reponse from Nest.", e);
            }
        } catch (JsonProcessingException e1) {
            throw new NestException("Unable create json from:" + retrieve, e1);
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
 
        try {
            String body = JsonUtils.toJson(event);
            HttpEntity<?> entity = new HttpEntity<>(body, headers);
            try {
                HttpEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class, partnerId, type.name());
                log.debug("response:" + response.getBody());
                return response.getBody();
            } catch (HttpClientErrorException e) {
                log.error("Error getting response from Nest:" + e.getResponseBodyAsString(), e);
                return e.getResponseBodyAsString();
            }
        } catch (JsonProcessingException e1) {
            throw new NestException("Unable create json from:" + event, e1);
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
        String key = settingDao.getString(GlobalSettingType.NEST_USERNAME) + ":" + settingDao.getString(GlobalSettingType.NEST_PASSWORD);
        try {
            return "Basic " + DatatypeConverter.printBase64Binary(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new NestException("Error encoding Nest key", e);
        }
    }

}
