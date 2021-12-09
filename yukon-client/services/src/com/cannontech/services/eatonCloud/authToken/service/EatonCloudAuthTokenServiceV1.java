package com.cannontech.services.eatonCloud.authToken.service;

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
import com.cannontech.dr.eatonCloud.message.EatonCloudAuthTokenRequestV1;
import com.cannontech.dr.eatonCloud.message.V1.EatonCloudAuthTokenResponseV1;
import com.cannontech.dr.eatonCloud.model.EatonCloudRetrievalUrl;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCredentialsV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudErrorHandlerV1;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudTokenV1;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.GsonBuilder;

public class EatonCloudAuthTokenServiceV1 implements MessageListener {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudAuthTokenServiceV1.class);
    
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    @Autowired private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new EatonCloudErrorHandlerV1());
        restTemplate.setMessageConverters(Arrays.asList(mappingJackson2HttpMessageConverter));
    }
    
    private Cache<String, EatonCloudTokenV1> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof EatonCloudAuthTokenRequestV1) {
                    EatonCloudCredentialsV1 credentials = getCredentials();
                    if(((EatonCloudAuthTokenRequestV1) objMessage.getObject()).isClearCache()) {
                        log.error("Recieved message from the simulator to invalidate Eaton Cloud token cache");
                        tokenCache.invalidate(credentials.getServiceAccountId());
                        sendResponse(message, null, null);
                        return;
                    }
                    EatonCloudTokenV1 cachedToken = tokenCache.getIfPresent(credentials.getServiceAccountId());
                    if ((cachedToken != null)) {
                        sendResponse(message, cachedToken, null);
                    } else {
                        String url = EatonCloudRetrievalUrl.SECURITY_TOKEN.getUrl(settingDao, log, restTemplate);
                        log.info("Retrieving {} new Eaton Cloud token for {}.", url, credentials.getServiceAccountId());
                        try {
                            EatonCloudTokenV1 newToken = restTemplate.postForObject(url, credentials, EatonCloudTokenV1.class);
                            log.info("Retrieved new Eaton Cloud token for {}.", credentials.getServiceAccountId());
                            tokenCache.put(credentials.getServiceAccountId(), newToken);
                            sendResponse(message, newToken, null);
                        } catch (EatonCloudCommunicationExceptionV1 e) {
                            sendResponse(message, null, e);
                        }
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

    private void sendResponse(Message message, EatonCloudTokenV1 cachedToken, EatonCloudCommunicationExceptionV1 error) throws JMSException {
        if (cachedToken != null) {
            log.debug("Got Eaton Cloud token:{}", cachedToken);
        } else if (error != null) {
            log.error("Attempted to retrieve Eaton Cloud token but got error:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(error.getErrorMessage()));
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new EatonCloudAuthTokenResponseV1(cachedToken, error));
    }

    private EatonCloudCredentialsV1 getCredentials() {
        String serviceAccountId = settingDao.getString(GlobalSettingType.EATON_CLOUD_SERVICE_ACCOUNT_ID);
        String secret = settingDao.getString(GlobalSettingType.EATON_CLOUD_SECRET);
        return new EatonCloudCredentialsV1(serviceAccountId, secret);
    }
}
