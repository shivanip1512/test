package com.cannontech.services.pxmw.authToken.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.dr.pxmw.message.PxMWAuthTokenRequestV1;
import com.cannontech.dr.pxmw.message.v1.PxMWAuthTokenResponseV1;
import com.cannontech.dr.pxmw.model.PxMWRetrievalUrl;
import com.cannontech.dr.pxmw.model.v1.PxMWCommunicationExceptionV1;
import com.cannontech.dr.pxmw.model.v1.PxMWCredentialsV1;
import com.cannontech.dr.pxmw.model.v1.PxMWErrorHandlerV1;
import com.cannontech.dr.pxmw.model.v1.PxMWTokenV1;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.GsonBuilder;

public class PxMWAuthTokenServiceV1 implements MessageListener {

    private static final Logger log = YukonLogManager.getLogger(PxMWAuthTokenServiceV1.class);
    
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;
    private RestTemplate restTemplate;
    
    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new PxMWErrorHandlerV1());
    }
    
    private Cache<String, PxMWTokenV1> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof PxMWAuthTokenRequestV1) {
                    PxMWCredentialsV1 credentials = getCredentials();
                    if(((PxMWAuthTokenRequestV1) objMessage.getObject()).isClearCache()) {
                        log.error("Recieved message from the simulator to invalidate Eaton Cloud token cache");
                        tokenCache.invalidate(credentials.getServiceAccountId());
                        sendResponse(message, null, null);
                        return;
                    }
                    PxMWTokenV1 cachedToken = tokenCache.getIfPresent(credentials.getServiceAccountId());
                    if ((cachedToken != null)) {
                        sendResponse(message, cachedToken, null);
                    } else {
                        String url = PxMWRetrievalUrl.SECURITY_TOKEN.getUrl(settingDao, log, restTemplate);
                        log.info("Retrieving {} new Eaton Cloud token for {}.", url, credentials.getServiceAccountId());
                        try {
                            PxMWTokenV1 newToken = restTemplate.postForObject(url, credentials, PxMWTokenV1.class);
                            log.info("Retrieved new Eaton Cloud token for {}.", credentials.getServiceAccountId());
                            tokenCache.put(credentials.getServiceAccountId(), newToken);
                            sendResponse(message, newToken, null);
                        } catch (PxMWCommunicationExceptionV1 e) {
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

    private void sendResponse(Message message, PxMWTokenV1 cachedToken, PxMWCommunicationExceptionV1 error) throws JMSException {
        if (cachedToken != null) {
            log.debug("Got Eaton Cloud token:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(cachedToken));
        } else if (error != null) {
            log.error("Attempted to retrieve Eaton Cloud token but got error:{}",
                    new GsonBuilder().setPrettyPrinting().create().toJson(error.getErrorMessage()));
        }
        jmsTemplate.convertAndSend(message.getJMSReplyTo(), new PxMWAuthTokenResponseV1(cachedToken, error));
    }

    private PxMWCredentialsV1 getCredentials() {
        String serviceAccountId = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SERVICE_ACCOUNT_ID);
        String secret = settingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SECRET);
        return new PxMWCredentialsV1(serviceAccountId, secret);
    }
}
