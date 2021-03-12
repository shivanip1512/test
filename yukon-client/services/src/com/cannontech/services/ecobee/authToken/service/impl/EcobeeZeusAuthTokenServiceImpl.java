package com.cannontech.services.ecobee.authToken.service.impl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jms.JmsException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationResponse;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;
import com.cannontech.services.ecobee.authToken.service.EcobeeZeusAuthTokenService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EcobeeZeusAuthTokenServiceImpl implements EcobeeZeusAuthTokenService, MessageListener {
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusAuthTokenServiceImpl.class);

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;

    // Cache with expire time 1439 minutes because refresh token is valid for 24 Hours.
    private Cache<String, ZeusEcobeeAuthTokenResponse> ecobeeAuthTokenResponseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1439, TimeUnit.MINUTES).build();
    private ScheduledFuture<?> schedulerFuture;
    private RestTemplate restTemplate;
    private static final String responseCacheKey = "responseCacheKey";
    private static final String scope = "utility";
    private static final String authUrlPart = "auth";
    private static final String refreshUrlPart = "auth/refresh?refresh_token=";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();

    public EcobeeZeusAuthTokenServiceImpl(RestTemplate proxiedTemplate) {
        this.restTemplate = proxiedTemplate;
    }

    @PostConstruct
    public void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(globalSettingDao)
                .ifPresent(httpProxy -> factory.setProxy(httpProxy.getJavaHttpProxy()));
        restTemplate.setRequestFactory(factory);
    }

    @Override
    public ZeusEcobeeAuthTokenResponse authenticate() {

        ZeusEcobeeAuthTokenResponse ecobeeAuthTokenResponse = new ZeusEcobeeAuthTokenResponse();
        ResponseEntity<ZeusAuthenticationResponse> authenticationResponse = null;
        if (ecobeeAuthTokenResponseCache.getIfPresent(responseCacheKey) == null) {
            if (ecobeeAuthTokenResponseCache.getIfPresent(responseCacheKey) == null) {
                try {
                    String ecobeePassword = globalSettingDao.getString(GlobalSettingType.ECOBEE_PASSWORD);
                    String ecobeeUsername = globalSettingDao.getString(GlobalSettingType.ECOBEE_USERNAME);
                    String ecobeeServerURL = globalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
                    // Check if username, password and Ecobee URL available in global settings or not.
                    if (StringUtils.isEmpty(ecobeeUsername) || StringUtils.isEmpty(ecobeePassword)
                            || StringUtils.isEmpty(ecobeeServerURL)) {
                        throw new EcobeeAuthenticationException("One or more ecobee configuration settings is empty.");
                    }

                    String url = ecobeeServerURL + authUrlPart;
                    log.debug("Attempting login with ecobeeUsername " + ecobeeUsername + " URL: " + url);

                    ZeusAuthenticationRequest authRequest = new ZeusAuthenticationRequest(ecobeeUsername, ecobeePassword, scope);

                    authenticationResponse = restTemplate.postForEntity(url, authRequest, ZeusAuthenticationResponse.class);

                    // if HttpStatus.OK, API call successful Update the cache and start scheduler for refreshing auth token.
                    if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                        log.info("Successfully logged in to Ecobee.");
                        ZeusAuthenticationResponse response = authenticationResponse.getBody();
                        ecobeeAuthTokenResponse.setAuthToken(response.getAuthToken());
                        ecobeeAuthTokenResponse.setRefreshToken(response.getRefreshToken());
                        ecobeeAuthTokenResponse.setExpiryTimestamp(response.getExpiryTimestamp());
                        ecobeeAuthTokenResponseCache.put(responseCacheKey, ecobeeAuthTokenResponse);

                        // Cancel the previous scheduler if its running then schedule a fresh scheduler for refreshing the auth
                        // token in every 60 minutes.
                        if (schedulerFuture != null) {
                            schedulerFuture.cancel(true);
                        }
                        scheduleRefreshAuthToken();
                    } else if (authenticationResponse.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        // Authentication failed throw EcobeeAuthenticationException.
                        throw new EcobeeAuthenticationException(ecobeeUsername,
                                authenticationResponse.getStatusCode().toString());
                    } else {
                        throw new EcobeeCommunicationException(
                                "Unable to communicate with Ecobee API." + authenticationResponse.getStatusCode().toString());
                    }
                } catch (RestClientException e) {
                    throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
                } catch (EcobeeAuthenticationException e) {
                    log.warn("Caught exception in authenticate", e);
                }
            }
        }
        return ecobeeAuthTokenResponseCache.getIfPresent(responseCacheKey);
    }

    private void scheduleRefreshAuthToken() {
        log.info("Scheduling Auth token refresh API call to run before every hour.");
        schedulerFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                log.info("trying ecobee refresh");
                String ecobeeServerURL = globalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
                ZeusEcobeeAuthTokenResponse authTokenResponse = ecobeeAuthTokenResponseCache.getIfPresent(responseCacheKey);

                if (isExpiredAuthToken(authTokenResponse.getExpiryTimestamp())) {
                    schedulerFuture.cancel(true);
                }
                String refreshToken = authTokenResponse.getRefreshToken();
                String url = ecobeeServerURL + refreshUrlPart + refreshToken;

                // Make API calls in every 59 minutes for refreshing authentication token.
                ResponseEntity<ZeusAuthenticationResponse> authenticationResponse = restTemplate.getForEntity(url,
                        ZeusAuthenticationResponse.class);

                if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                    authTokenResponse.setAuthToken(authenticationResponse.getBody().getAuthToken());
                    authTokenResponse.setExpiryTimestamp(authenticationResponse.getBody().getExpiryTimestamp());
                } else {
                    throw new EcobeeCommunicationException(
                            "Unable to communicate with Ecobee API." + authenticationResponse.getStatusCode().toString());
                }
            } catch (RestClientException e) {
                throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
            }
        }, 59, 60, TimeUnit.MINUTES);
    }

    /**
     * Return true if the token is expired i.e current time is after expire time.
     */
    private boolean isExpiredAuthToken(String expiryTimestamp) {
        DateTime currentTime = DateTime.now(DateTimeZone.UTC);
        DateTime expiryTime = formatter.parseDateTime(expiryTimestamp);
        return currentTime.isAfter(expiryTime);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof ZeusEcobeeAuthTokenRequest) {
                    ZeusEcobeeAuthTokenResponse response = authenticate();
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
                }
            } catch (JmsException | JMSException e) {
                log.error("Unable to process message", e);
            }
        }
    }
}