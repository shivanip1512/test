package com.cannontech.services.eatonCloud.authToken.service.impl;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EatonCloudAuthTokenServiceImplV1 implements EatonCloudAuthTokenServiceV1, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudAuthTokenServiceImplV1.class);
    
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    private RestTemplate restTemplate;
    private static final Logger commsLogger = YukonLogManager
            .getEatonCloudCommsLogger(EatonCloudAuthTokenServiceImplV1.class);
    private static AtomicInteger requestIncrementer = new AtomicInteger(1);
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EatonCloudErrorHandlerV1());
        restTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING, this::databaseChangeEvent);
    }
    
    private Cache<String, EatonCloudTokenV1> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();
       
    /**
     * Called when any global setting is updated
     */
    private void databaseChangeEvent(DatabaseChangeEvent event) {
        if (tokenCache.size() == 0) {
            return;
        }
        try {
            if (settingDao.isDbChangeForSetting(event, GlobalSettingType.EATON_CLOUD_SECRET)
                    || settingDao.isDbChangeForSetting(event, GlobalSettingType.EATON_CLOUD_SECRET2)) {
                String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
                tokenCache.invalidateAll();
                refreshToken(null, serviceAccountId);
                log.info("Secret1 or Secret2 was updated, refreshing the token");
            }
        } catch (Exception e) {
            log.error("Unable to retrieve token", e);
        }
    }

    
    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof EatonCloudHeartbeatRequest) {
                    try {
                        //Message from Watchdog to verify connection to Eaton Cloud
                        eatonCloudCommunicationService.getServiceAccountDetail();
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudHeartbeatResponse());
                    } catch (EatonCloudCommunicationExceptionV1 e) {
                        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudHeartbeatResponse(e.getMessage()));
                    }
                }
                else if (objMessage.getObject() instanceof EatonCloudAuthTokenRequestV1) {
                    String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
                    if (((EatonCloudAuthTokenRequestV1) objMessage.getObject()).isClearCache()) {
                        log.info("Recieved message from the simulator to invalidate Eaton Cloud token cache");
                        tokenCache.invalidate(serviceAccountId);
                        sendResponse(message, null, null);
                        return;
                    }
                   
                    EatonCloudTokenV1 cachedToken = tokenCache.getIfPresent(serviceAccountId);
                    if ((cachedToken != null)) {
                        sendResponse(message, cachedToken, null);
                    } else {
                        refreshToken(message, serviceAccountId);
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
    private void refreshToken(Message message, String serviceAccountId) throws JMSException {
        try {
            EatonCloudTokenV1 newToken = retrieveNewToken(GlobalSettingType.EATON_CLOUD_SECRET, serviceAccountId);
            tokenCache.put(serviceAccountId, newToken);
            sendResponse(message, newToken, null);
        } catch (EatonCloudCommunicationExceptionV1 e) {
            try {
                EatonCloudTokenV1 newToken = retrieveNewToken(GlobalSettingType.EATON_CLOUD_SECRET2,
                        serviceAccountId);
                tokenCache.put(serviceAccountId, newToken);
                sendResponse(message, newToken, null);
            } catch (EatonCloudCommunicationExceptionV1 ex) {
                sendResponse(message, null, ex);
            }
        }
    }
    
    @Override
    public EatonCloudTokenV1 retrieveNewToken(GlobalSettingType type, String serviceAccountId) {
        long requestIdentifier = requestIncrementer.getAndIncrement();
        String url = EatonCloudRetrievalUrl.SECURITY_TOKEN.getUrl(settingDao, restTemplate);
        try {
            EatonCloudCredentialsV1 credentials = getCredentials(type, serviceAccountId);
            commsLogger.info(">>> EC[{}] {} retrieval, request to:{}", requestIdentifier, type, url);
            EatonCloudTokenV1 newToken = restTemplate.postForObject(url, credentials, EatonCloudTokenV1.class);
            commsLogger.info(">>> EC[{}] {} retrieved, request to:{}", requestIdentifier, type, url);
            return newToken;
        } catch (EatonCloudCommunicationExceptionV1 e) {
            commsLogger.info(">>> EC[{}] {} {} retrieval failed, request to:{} Response:", type, requestIdentifier, url, e);
            e.setRequestIdentifier(requestIdentifier);
            throw e;
        } catch (Exception e) {
            commsLogger.info(">>> EC[{}] {} {} retrieval failed, request to:{} Response:", type, requestIdentifier, url, e);
            throw new EatonCloudCommunicationExceptionV1(e, requestIdentifier);
        }
    }

    private void sendResponse(Message message, EatonCloudTokenV1 cachedToken, EatonCloudCommunicationExceptionV1 error)
            throws JMSException {
        if (message != null) {
            jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudAuthTokenResponseV1(cachedToken, error));
        }
    }

    private EatonCloudCredentialsV1 getCredentials(GlobalSettingType type, String serviceAccountId) {
        String secret = settingDao.getString(type);
        return new EatonCloudCredentialsV1(serviceAccountId, secret);
    }
}
