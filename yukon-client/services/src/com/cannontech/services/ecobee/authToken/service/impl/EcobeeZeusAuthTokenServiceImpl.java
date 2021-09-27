package com.cannontech.services.ecobee.authToken.service.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jms.JmsException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationResponse;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.ZeusEcobeeAuthTokenResponse;
import com.cannontech.services.ecobee.authToken.service.EcobeeZeusAuthTokenService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class EcobeeZeusAuthTokenServiceImpl implements EcobeeZeusAuthTokenService, MessageListener {
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusAuthTokenServiceImpl.class);

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;

    // Cache with expire time 1439 minutes because refresh token is valid for 24 Hours.
    private LoadingCache<String, ZeusEcobeeAuthTokenResponse> ecobeeAuthTokenResponseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1439, TimeUnit.MINUTES)
            .build(new CacheLoader<String, ZeusEcobeeAuthTokenResponse>() {
                @Override
                public ZeusEcobeeAuthTokenResponse load(String responseCacheKey) throws Exception {
                    return generateEcobeeAuthTokenResponse();
                };
            });
    private ScheduledFuture<?> schedulerFuture;
    private RestTemplate restTemplate;
    private static final String responseCacheKey = "responseCacheKey";
    private static final String scope = "utility";
    private static final String authUrlPart = "auth";
    private static final String refreshUrlPart = "auth/refresh?refresh_token=";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();

    private static String ecobeePassword;
    private static String ecobeeUsername;
    private static String ecobeeServerURL;
    private static DateTime tokenGeneratedTime;

    public EcobeeZeusAuthTokenServiceImpl(RestTemplate proxiedTemplate) {
        this.restTemplate = proxiedTemplate;
    }

    @PostConstruct
    public void init() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(globalSettingDao).ifPresent(httpProxy -> {
            HttpHost proxyHost = new HttpHost(httpProxy.getHost(), httpProxy.getPort());
            HttpClient httpClient = HttpClientBuilder.create()
                    .setProxy(proxyHost)
                    .build();
            factory.setHttpClient(httpClient);
        });
        restTemplate.setRequestFactory(factory);

        ecobeePassword = globalSettingDao.getString(GlobalSettingType.ECOBEE_PASSWORD);
        ecobeeUsername = globalSettingDao.getString(GlobalSettingType.ECOBEE_USERNAME);
        ecobeeServerURL = globalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.ECOBEE_PASSWORD) ||
                    globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.ECOBEE_USERNAME) ||
                    globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.ECOBEE_SERVER_URL)) {
                ecobeePassword = globalSettingDao.getString(GlobalSettingType.ECOBEE_PASSWORD);
                ecobeeUsername = globalSettingDao.getString(GlobalSettingType.ECOBEE_USERNAME);
                ecobeeServerURL = globalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
                ecobeeAuthTokenResponseCache.invalidateAll();
                ecobeeAuthTokenResponseCache.put(responseCacheKey, generateEcobeeAuthTokenResponse());
            }
        });
    }

    @Override
    public ZeusEcobeeAuthTokenResponse authenticate() {
        try {
            return ecobeeAuthTokenResponseCache.get(responseCacheKey);
        } catch (ExecutionException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }
    }

    /**
     * Make API call to Ecobee, Populate the cache and update/schedule the scheduler for refreshing the auth token before 1 hour.
     */
    private ZeusEcobeeAuthTokenResponse generateEcobeeAuthTokenResponse() {
        try {
            // Check if username, password and Ecobee URL available in global settings or not.
            if (StringUtils.isEmpty(ecobeeUsername) || StringUtils.isEmpty(ecobeePassword)
                    || StringUtils.isEmpty(ecobeeServerURL)) {
                throw new EcobeeAuthenticationException("One or more ecobee configuration settings is empty.");
            }

            String url = ecobeeServerURL + authUrlPart;
            log.debug("Attempting login with ecobeeUsername " + ecobeeUsername + " URL: " + url);

            ZeusAuthenticationRequest authRequest = new ZeusAuthenticationRequest(ecobeeUsername, ecobeePassword, scope);

            ResponseEntity<ZeusAuthenticationResponse> authenticationResponse = restTemplate.postForEntity(url, authRequest,
                    ZeusAuthenticationResponse.class);

            // if HttpStatus.OK, API call successful Update the cache and start scheduler for refreshing auth token.
            if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully logged in to Ecobee.");
                ZeusAuthenticationResponse response = authenticationResponse.getBody();
                ZeusEcobeeAuthTokenResponse ecobeeAuthTokenResponse = buildZeusEcobeeAuthTokenResponse(response);

                tokenGeneratedTime = DateTime.now(DateTimeZone.UTC);
                scheduleRefreshAuthToken(ecobeeServerURL);
                return ecobeeAuthTokenResponse;
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
            return null;
        }
    }

    private void scheduleRefreshAuthToken(String ecobeeServerURL) {
        // Cancel the previous scheduler if its running.
        cancelExistingScheduler();
        log.info("Scheduling Auth token refresh API call to run before every hour.");
        schedulerFuture = scheduledExecutor.scheduleAtFixedRate(() -> {
            ZeusEcobeeAuthTokenResponse authTokenResponse = ecobeeAuthTokenResponseCache.getIfPresent(responseCacheKey);
            try {
                log.info("trying ecobee refresh");
                //As refresh token is valid for 24 hours and our cache also get invalidated after 1439 minutes. We should cancel this scheduler after that.
                cancelRunningScheduler(authTokenResponse.getExpiryTimestamp());
                // Don't make API call if the scheduler is cancelled.
                if (schedulerFuture.isCancelled()) {
                    return;
                }
                String refreshToken = authTokenResponse.getRefreshToken();
                String url = ecobeeServerURL + refreshUrlPart + refreshToken;
                if (log.isDebugEnabled()) {
                    log.debug("Request Header : " + HttpMethod.GET + " : " + url);
                }
                ecobeeAuthTokenResponseCache.invalidateAll();
                // Make API calls in every 59 minutes for refreshing authentication token.
                ResponseEntity<ZeusAuthenticationResponse> authenticationResponse = restTemplate.getForEntity(url,
                        ZeusAuthenticationResponse.class);

                if (log.isDebugEnabled()) {
                    log.debug("Response Header : " + url + " : " + authenticationResponse.getStatusCode());
                    if (authenticationResponse.hasBody()) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            log.debug("Response Body: \n" + JsonUtils.beautifyJson(mapper.writeValueAsString(authenticationResponse.getBody())));
                        } catch (JsonProcessingException e) {
                            log.error("Error while parsing response body : ", e);
                        }
                    }
                }
                if (authenticationResponse.getStatusCode() == HttpStatus.OK) {
                    authTokenResponse = buildZeusEcobeeAuthTokenResponse(authenticationResponse.getBody());
                } else {
                    authTokenResponse = logAndGenerateNewAuthToken();
                }
            } catch (RestClientException e) {
                authTokenResponse = logAndGenerateNewAuthToken();
            }
            ecobeeAuthTokenResponseCache.put(responseCacheKey, authTokenResponse);
        }, 59, 60, TimeUnit.MINUTES);
    }

    /**
     * Log the error, cancel the existing scheduler if running and generate a fresh access token.
     */
    private ZeusEcobeeAuthTokenResponse logAndGenerateNewAuthToken() {
        log.info("Ecobee refresh API call was unsuccessfull. Generating new tokens.");
        cancelExistingScheduler();
        return generateEcobeeAuthTokenResponse();
    }

    /**
     * Cancel the previous schedulers if its running. It is used to cancel the scheduler when user make a call to /auth API after
     * 24 hours. After 24 hours, ecobeeAuthTokenResponseCache will be invalidated so a fresh call will be made. So here we have to
     * cancel the previous scheduler.
     */
    private void cancelExistingScheduler() {
        if (schedulerFuture != null) {
            schedulerFuture.cancel(true);
        }
    }

    /**
     * Cancel the running schedulers after 24 hours. After 24 hours if there are no activity in Ecobee modules, we should cancel
     * the scheduler. Fresh scheduler will be scheduled for subsequent Ecobee call.
     */
    private void cancelRunningScheduler(String getExpiryTimestamp) {
        if (isExpiredRefreshToken(getExpiryTimestamp)) {
            schedulerFuture.cancel(true);
        }
    }

    /**
     * Build ZeusEcobeeAuthTokenResponse from ZeusAuthenticationResponse.
     */
    private ZeusEcobeeAuthTokenResponse buildZeusEcobeeAuthTokenResponse(ZeusAuthenticationResponse response) {
        ZeusEcobeeAuthTokenResponse ecobeeAuthTokenResponse = new ZeusEcobeeAuthTokenResponse();
        ecobeeAuthTokenResponse.setAuthToken(response.getAuthToken());
        ecobeeAuthTokenResponse.setRefreshToken(response.getRefreshToken());
        ecobeeAuthTokenResponse.setExpiryTimestamp(response.getExpiryTimestamp());
        return ecobeeAuthTokenResponse;
    }

    /**
     * Return true if the refresh token is expired i.e current time is after expire time.
     */
    private boolean isExpiredRefreshToken(String expiryTimestamp) {
        DateTime expiryTime = formatter.parseDateTime(expiryTimestamp);
        return tokenGeneratedTime.plusHours(24).isBefore(expiryTime);
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