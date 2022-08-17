package com.cannontech.dr.pxwhite.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.apache.tomcat.util.buf.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.pxwhite.model.PxWhiteCredentials;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceChannels;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceCommand;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceData;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceTimeSeriesData;
import com.cannontech.dr.pxwhite.model.PxWhiteRenewToken;
import com.cannontech.dr.pxwhite.model.PxWhiteTokenResponse;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.PxWhiteCommunicationService;
import com.cannontech.system.dao.GlobalSettingDao;

public class PxWhiteCommunicationServiceImpl implements PxWhiteCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(PxWhiteCommunicationServiceImpl.class);
    
    // Base PX White API url
    private static final String urlBase = "https://adopteriotwebapi.eaton.com/api";
    
    // PX White API endpoints
    private static final String urlSuffixGetSecurityToken = "/v1/security/token";
    private static final String urlSuffixRefreshSecurityToken = "/v1/security/token/refresh";
    private static final String urlSuffixGetTokenDetails = "/v1/security/tokendetails";
    private static final String urlSuffixDeviceDataCurrentValues = "/v1/devices/{deviceId}/timeseries/latest?tags={tags}";
    private static final String urlSuffixDeviceDataOverRange = "/v1/devices/{deviceId}/timeseries?tag_trait_list={tags}&start={start}&end={end}";
    private static final String urlSuffixDeviceChannels = "/v1/devices/{deviceId}/channels";
    private static final String urlSuffixDeviceCommand = "/v1/devices/{deviceId}/channels/{tag}";
    
    // Template for making requests and receiving responses
    private final RestTemplate restTemplate;
    
    @Autowired
    public PxWhiteCommunicationServiceImpl(GlobalSettingDao globalSettingDao) {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new PxWhiteErrorHandler());
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(globalSettingDao).ifPresent(httpProxy -> {
            factory.setProxy(httpProxy.getJavaHttpProxy());
        });
        restTemplate.setRequestFactory(factory);
    }
    
    @Override
    public String getSecurityToken(String user, String password, String applicationId) throws PxWhiteCommunicationException {
        log.info("Retrieving new PX White security token.");
        String url = urlBase + urlSuffixGetSecurityToken;
        PxWhiteCredentials credentials = new PxWhiteCredentials(user, password, applicationId);
        PxWhiteTokenResponse response = restTemplate.postForObject(url, credentials, PxWhiteTokenResponse.class);
        return response.getToken();
    }
    
    @Override
    public String refreshSecurityToken(String user, String expiredToken) {
        log.info("Refreshing PX White security token.");
        String url = urlBase + urlSuffixRefreshSecurityToken;
        PxWhiteRenewToken renewToken = new PxWhiteRenewToken(user, expiredToken);
        PxWhiteTokenResponse response = restTemplate.postForObject(url, renewToken, PxWhiteTokenResponse.class);
        return response.getToken();
    }
    
    @Override
    public TokenDetails getTokenDetails(String token) {
        log.info("Getting PX White security token details.");
        String url = urlBase + urlSuffixGetTokenDetails;
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<TokenDetails> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TokenDetails.class);
        return response.getBody();
    }
    
    @Override
    public PxWhiteDeviceData getDeviceDataCurrentValues(String token, String deviceId, List<String> tags) {
        String tagsString = StringUtils.join(tags, ',');
        log.debug("Getting device data current value. DeviceId: " + deviceId + ", Tags: " + tagsString);
        
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("deviceId", deviceId);
        urlParams.put("tags", tagsString);
        
        String url = urlBase + urlSuffixDeviceDataCurrentValues;
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxWhiteDeviceData> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PxWhiteDeviceData.class, urlParams);
        return response.getBody();
    }
    
    @Override
    public PxWhiteDeviceTimeSeriesData getDeviceDataByDateRange(String token, String deviceId, List<String> tags, Instant startDate, Instant endDate) {
        String tagsString = StringUtils.join(tags, ',');
        log.debug("Getting device data over range. DeviceId: " + deviceId + ", Tags: " + tagsString 
                 + ", StartDate: " + startDate + ", EndDate: " + endDate);
        
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("deviceId", deviceId);
        urlParams.put("tags", tagsString);
        urlParams.put("start", startDate.toString()); //Outputs as ISO 8601
        // End date is optional. This method could be modified to handle a null end date and omit this query parameter.
        urlParams.put("end", endDate.toString()); //Outputs as ISO 8601
        String url = urlBase + urlSuffixDeviceDataOverRange;
        
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxWhiteDeviceTimeSeriesData> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PxWhiteDeviceTimeSeriesData.class, urlParams);
        
        return response.getBody();
    }
    
    @Override
    public PxWhiteDeviceChannels getChannels(String token, String deviceId) {
        log.debug("Getting device channels. DeviceId: " + deviceId);
        
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("deviceId", deviceId);
        String url = urlBase + urlSuffixDeviceChannels;
        
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxWhiteDeviceChannels> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PxWhiteDeviceChannels.class, urlParams);
        
        return response.getBody();
    }
    
    @Override
    public boolean sendCommand(String token, String deviceId, String tag, String commandString) {
        log.debug("Sending command. DeviceId: " + deviceId + ", channel tag: " + tag + ", command: " + commandString);
        
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("deviceId", deviceId);
        urlParams.put("tag", tag);
        String url = urlBase + urlSuffixDeviceCommand;
        
        PxWhiteDeviceCommand command = new PxWhiteDeviceCommand(commandString);
        HttpEntity<PxWhiteDeviceCommand> requestEntity = getRequestWithAuthHeaders(command, token);
        restTemplate.put(url, requestEntity, urlParams);
        
        return true;
    }
    
    private HttpEntity<String> getEmptyRequestWithAuthHeaders(String token) {
        return getRequestWithAuthHeaders("", token);
    }
    
    private <T> HttpEntity<T> getRequestWithAuthHeaders(T requestObject, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(requestObject, headers);
    }
}
