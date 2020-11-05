package com.cannontech.dr.pxmw.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.pxmw.service.PxMWCommunicationService;
import com.cannontech.dr.pxmw.service.model.PxMWDeviceProfile;
import com.cannontech.dr.pxwhite.model.PxWhiteCredentials;
import com.cannontech.dr.pxwhite.model.PxWhiteDeviceChannels;
import com.cannontech.dr.pxwhite.model.PxWhiteRenewToken;
import com.cannontech.dr.pxwhite.model.PxWhiteTokenResponse;
import com.cannontech.dr.pxwhite.model.TokenDetails;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteErrorHandler;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class PxMWCommunicationServiceImpl implements PxMWCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(PxMWCommunicationServiceImpl.class);

    // Base PX Middleware API url
    private static final String urlBase = GlobalSettingType.PX_MIDDLEWARE_URL.toString();

    // PX Middleware API endpoints
    private static final String urlSuffixGetSecurityToken = "/v1/security/token";
    private static final String urlSuffixRefreshSecurityToken = "/v1/security/token/refresh";
    private static final String urlSuffixGetTokenDetails = "/v1/security/tokendetails";
    private static final String urlSuffixDeviceProfile = "/v1/deviceProfile/{deviceGuid}";

    // Template for making requests and receiving responses
    private final RestTemplate restTemplate;

    @Autowired
    public PxMWCommunicationServiceImpl(GlobalSettingDao globalSettingDao) {
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

    public PxMWDeviceProfile getDeviceProfile(String token, String deviceGuid) {
        log.debug("Getting device profile. DeviceGuid: " + deviceGuid);

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("deviceGuid", deviceGuid);
        String url = urlBase + urlSuffixDeviceProfile;

        HttpEntity<String> requestEntity = getEmptyRequestWithAuthHeaders(token);
        ResponseEntity<PxMWDeviceProfile> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PxMWDeviceProfile.class, urlParams);

        return response.getBody();
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
