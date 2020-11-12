package com.cannontech.dr.pxmw.service.impl;

import java.net.URI;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWDeviceProfileV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorHandlerV1;
import com.cannontech.dr.pxmw.model.v1.PxMWSiteV1;
import com.cannontech.dr.pxmw.service.PxMWCommunicationServiceV1;
import com.cannontech.dr.pxwhite.model.PxWhiteCredentials;
import com.cannontech.dr.pxwhite.model.PxWhiteRenewToken;
import com.cannontech.dr.pxwhite.model.PxWhiteTokenResponse;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.gson.GsonBuilder;

public class PxMWCommunicationServiceImplV1 implements PxMWCommunicationServiceV1 {
    @Autowired GlobalSettingDao settingDao;
    private static final Logger log = YukonLogManager.getLogger(PxMWCommunicationServiceImplV1.class);

    // PX Middleware API endpoints
    private static final String urlSuffixGetSecurityToken = "/v1/security/token";
    private static final String urlSuffixRefreshSecurityToken = "/v1/security/token/refresh";
    private static final String urlSuffixGetTokenDetails = "/v1/security/tokendetails";
    
    // Template for making requests and receiving responses
    private final RestTemplate restTemplate;

    public PxMWCommunicationServiceImplV1() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new PxMWErrorHandlerV1());
    }

    @Override
    public String getSecurityToken(String user, String password, String applicationId) throws PxWhiteCommunicationException {
        log.info("Retrieving new PX White security token.");
        String url = getUrl(urlSuffixGetSecurityToken);
        PxWhiteCredentials credentials = new PxWhiteCredentials(user, password, applicationId);
        PxWhiteTokenResponse response = restTemplate.postForObject(url, credentials, PxWhiteTokenResponse.class);
        return response.getToken();
    }

    @Override
    public String refreshSecurityToken(String user, String expiredToken) {
        log.info("Refreshing PX White security token.");
        String url = getUrl(urlSuffixRefreshSecurityToken);
        PxWhiteRenewToken renewToken = new PxWhiteRenewToken(user, expiredToken);
        PxWhiteTokenResponse response = restTemplate.postForObject(url, renewToken, PxWhiteTokenResponse.class);
        return response.getToken();
    }

    @Override
    public TokenDetails getTokenDetails(String token) {
        log.info("Getting PX White security token details.");
        String url = getUrl(urlSuffixGetTokenDetails);
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<TokenDetails> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, TokenDetails.class);
        return response.getBody();
    }

    @Override
    public PxMWDeviceProfileV1 getDeviceProfile(String token, String deviceProfileGuid) throws PxMWCommunicationExceptionV1 {
     
        URI uri = UriComponentsBuilder.fromUriString(getUrl(PxMWRetrievalUrl.DEVICE_PROFILE_BY_GUID_V1.getSuffix()))
                .buildAndExpand(Map.of("id", deviceProfileGuid))
                .toUri();
        log.debug("Getting device profile. Device Profile Guid: {} URL:{}", deviceProfileGuid, uri); 
        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxMWDeviceProfileV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                PxMWDeviceProfileV1.class);
        log.debug("Got device profile. Device Profile Guid:{} Result:{}", deviceProfileGuid, new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        return response.getBody();
    }
    
    @Override
    public PxMWSiteV1 getSite(String token, String siteGuid, Boolean recursive, Boolean includeDetail)
            throws PxMWCommunicationExceptionV1 {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        if (recursive != null) {
            queryParams.add("recursive", recursive.toString());
        }
        if (includeDetail != null) {
            queryParams.add("includeDetail", includeDetail.toString());
        }
        
        URI uri = UriComponentsBuilder.fromUriString(getUrl(PxMWRetrievalUrl.DEVICES_BY_SITE_V1.getSuffix()))
                .buildAndExpand(Map.of("id", siteGuid))
                .toUri();

        uri = addQueryParams(queryParams, uri);
  
        log.debug("Getting site info. Site Guid: {} URL: {}", siteGuid, uri);

        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxMWSiteV1> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity, PxMWSiteV1.class);
        log.debug("Got site info. Site Guid:{} Result:{}", siteGuid, new GsonBuilder().setPrettyPrinting().create().toJson(response.getBody()));
        return response.getBody();
    }

    /**
     * Adds query params to URI
     */
    private URI addQueryParams(MultiValueMap<String, String> queryParams, URI uri) {
        if (!queryParams.isEmpty()) {
            uri = UriComponentsBuilder
                    .fromUri(uri)
                    .queryParams(queryParams)
                    .build()
                    .toUri();
        }
        return uri;
    }

    private HttpEntity<String> getEmptyRequestWithAuthHeaders(String token) {
        return getRequestWithAuthHeaders("", token);
    }

    private <T> HttpEntity<T> getRequestWithAuthHeaders(T requestObject, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return new HttpEntity<>(requestObject, headers);
    }

    public String getUrl(String urlSuffix) {
        String url = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_URL) + urlSuffix;
        log.debug("{}", url);
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (useProxy(url)) {
            YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                factory.setProxy(httpProxy.getJavaHttpProxy());
            });
        }
        restTemplate.setRequestFactory(factory);
        return url;
    }

    public boolean useProxy(String stringUrl) {
        if ((stringUrl.contains("localhost") || stringUrl.contains("127.0.0.1"))) {
            return false;
        }
        return true;
    }
}
