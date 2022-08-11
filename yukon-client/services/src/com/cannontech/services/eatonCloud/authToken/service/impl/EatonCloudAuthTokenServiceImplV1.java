package com.cannontech.services.eatonCloud.authToken.service.impl;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatRequest;
import com.cannontech.dr.eatonCloud.message.EatonCloudHeartbeatResponse;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenRequestV1;
import com.cannontech.dr.eatonCloud.message.v1.EatonCloudAuthTokenResponseV1;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCredentialsV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudErrorHandlerV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.services.eatonCloud.authToken.service.EatonCloudAuthTokenServiceV1;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.GsonBuilder;
import static com.cannontech.system.GlobalSettingType.EATON_CLOUD_SECRET;
import static com.cannontech.system.GlobalSettingType.EATON_CLOUD_SECRET2;
import static com.cannontech.system.GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID;

public class EatonCloudAuthTokenServiceImplV1 implements EatonCloudAuthTokenServiceV1, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudAuthTokenServiceImplV1.class);

    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EatonCloudErrorHandlerV1());
        restTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);
    }

    private Cache<String, EatonCloudTokenV1> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES)
            .build();

    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        if (tokenCache.size() == 0) {
            return;
        }
        try {
            checkEventForSecretUpdate(event, EATON_CLOUD_SECRET);
            checkEventForSecretUpdate(event, EATON_CLOUD_SECRET2);
        } catch (Exception e) {
            log.error("Unable to retrieve token", e);
        }
    }

    /**
     * Check if secret was updated and refresh the token
     * 
     * It takes a few seconds after the secret rotation to be able to retrieve the token, we will use the secret that
     * was not updated to retrieve the token in order to prevent error from displaying in the log file
     * if we were unable to get the token the token will be removed from cache and it will be retrieved on the next call to
     * cloud
     */
    private void checkEventForSecretUpdate(DatabaseChangeEvent event,
            GlobalSettingType rotatedSecret) {
        GlobalSettingType secretForTokenRetrieval = rotatedSecret == EATON_CLOUD_SECRET ? EATON_CLOUD_SECRET2 : EATON_CLOUD_SECRET;
        if (settingDao.isDbChangeForSetting(event, rotatedSecret)) {
            log.info("Updated {}. Attempting to retrieve token using {}.", rotatedSecret, secretForTokenRetrieval);
            if (Strings.isNullOrEmpty(settingDao.getString(secretForTokenRetrieval))) {
                log.info("Updated {}. {} is blank.", rotatedSecret, secretForTokenRetrieval);
                tokenCache.invalidateAll();
            } else if (getAndCacheToken(secretForTokenRetrieval) == null) {
                log.error("Updated {} failed to retrieve token using {}.", rotatedSecret,
                        secretForTokenRetrieval);
                tokenCache.invalidateAll();
            } else {
                log.info("Updated {}. Retrieved the token using {}.", rotatedSecret, secretForTokenRetrieval);
            }
        }
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof EatonCloudHeartbeatRequest) {
                    try {
                        // Message from Watchdog to verify connection to Eaton Cloud
                        eatonCloudCommunicationService.getServiceAccountDetail();
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudHeartbeatResponse());
                    } catch (EatonCloudCommunicationExceptionV1 e) {
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudHeartbeatResponse(e.getMessage()));
                    }
                } else if (objMessage.getObject() instanceof EatonCloudAuthTokenRequestV1) {
                    String serviceAccountId = settingDao.getString(EATON_CLOUD_SERVICE_ACCOUNT_ID);
                    if (((EatonCloudAuthTokenRequestV1) objMessage.getObject()).isClearCache()) {
                        log.info("Recieved message from the simulator to invalidate Eaton Cloud token cache");
                        tokenCache.invalidateAll();
                        sendResponse(message, null, null);
                        return;
                    }

                    EatonCloudTokenV1 cachedToken = tokenCache.getIfPresent(serviceAccountId);
                    if ((cachedToken != null)) {
                        sendResponse(message, cachedToken, null);
                    } else {
                        refreshToken(message);
                    }
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
                try {
                    sendResponse(message, null, null);
                } catch (JMSException e1) {
                    log.error("Unable to process message", e);
                }
            }
        }
    }

    /**
     * Sends message to Eaton Cloud to get a token using secret1 if unable to get token tries secret2. Sends reply with new
     * token if reply was requested.
     * 
     * @param message - if message is not null the reply will be send
     */
    private void refreshToken(Message message) throws JMSException {
        String secret1 = settingDao.getString(EATON_CLOUD_SECRET);
        String secret2 = settingDao.getString(EATON_CLOUD_SECRET2);

        if (Strings.isNullOrEmpty(secret1) && Strings.isNullOrEmpty(secret2)) {
            log.error("{} and {} are blank.", EATON_CLOUD_SECRET, EATON_CLOUD_SECRET2);
            sendResponse(message, null, new EatonCloudCommunicationExceptionV1());
            return;
        }

        if (Strings.isNullOrEmpty(secret1)) {
            retrieveTokenUsingSecret2(message);
            return;
        }

        try {
            EatonCloudTokenV1 newToken = getAndCacheToken(EATON_CLOUD_SECRET);
            sendResponse(message, newToken, null);
        } catch (EatonCloudCommunicationExceptionV1 e) {
            if (Strings.isNullOrEmpty(secret2)) {
                log.error("{} is blank. Token retrieval using {} failed:{}.",
                        EATON_CLOUD_SECRET2,
                        EATON_CLOUD_SECRET,
                        new GsonBuilder().setPrettyPrinting().create().toJson(e.getErrorMessage()));
                sendResponse(message, null, e);
            } else {
                log.error("Token retrieval using {} failed:{}.",
                        EATON_CLOUD_SECRET,
                        new GsonBuilder().setPrettyPrinting().create().toJson(e.getErrorMessage()));
                retrieveTokenUsingSecret2(message);
            }
        }
    }

    private void retrieveTokenUsingSecret2(Message message) throws JMSException {
        try {
            EatonCloudTokenV1 newToken = getAndCacheToken(EATON_CLOUD_SECRET2);
            sendResponse(message, newToken, null);
        } catch (EatonCloudCommunicationExceptionV1 e) {
            log.error("Token retrieval using {} failed:{}.", EATON_CLOUD_SECRET2,
                    new GsonBuilder().setPrettyPrinting().create().toJson(e.getErrorMessage()));
            sendResponse(message, null, e);
        }
    }

    private EatonCloudTokenV1 getAndCacheToken(GlobalSettingType type) {
        String serviceAccountId = settingDao.getString(EATON_CLOUD_SERVICE_ACCOUNT_ID);
        EatonCloudTokenV1 newToken = retrieveNewToken(type, serviceAccountId);
        if(newToken != null) {
            log.info("Retrieved token using {}", type);
            tokenCache.put(serviceAccountId, newToken);
        }
        return newToken;
    }

    @Override
    public EatonCloudTokenV1 retrieveNewToken(GlobalSettingType type, String serviceAccountId) {
        String url = EatonCloudRetrievalUrl.SECURITY_TOKEN.getUrl(settingDao, restTemplate);
        String secret = settingDao.getString(type);
        if (Strings.isNullOrEmpty(secret)) {
            log.error("{} is blank. Failed t retrieve token.", type);
            return null;
        }
        EatonCloudCredentialsV1 credentials = new EatonCloudCredentialsV1(serviceAccountId, secret);
        EatonCloudTokenV1 newToken = restTemplate.postForObject(url, credentials, EatonCloudTokenV1.class);
        return newToken;
    }

    private void sendResponse(Message message, EatonCloudTokenV1 cachedToken, EatonCloudCommunicationExceptionV1 error)
            throws JMSException {
        if (message != null) {
            jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudAuthTokenResponseV1(cachedToken, error));
        }
    }
}
