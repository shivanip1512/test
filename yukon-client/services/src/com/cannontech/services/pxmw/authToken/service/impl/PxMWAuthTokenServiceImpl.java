package com.cannontech.services.pxmw.authToken.service.impl;

import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.jms.YukonJmsTemplate;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
import com.cannontech.dr.pxwhite.service.impl.PxWhiteCommunicationException;
import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenRequest;
import com.cannontech.services.pxmw.authToken.message.PxMWAuthTokenResponse;
import com.cannontech.services.pxmw.authToken.service.PxMWAuthTokenService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class PxMWAuthTokenServiceImpl implements PxMWAuthTokenService, MessageListener {

    private static final Logger log = YukonLogManager.getLogger(PxMWAuthTokenServiceImpl.class);
    
    @Autowired private PxMWCommunicationServiceV1 pxMWCommunicationService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonJmsTemplate jmsTemplate;

    private Cache<String,String> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();
    private static final String authTokenKey = "authTokenKey";

    @Override
    public PxMWAuthTokenResponse handle(PxMWAuthTokenRequest request) throws JMSException {
        PxMWAuthTokenResponse response = new PxMWAuthTokenResponse();
        log.debug("Processing token request");
        if (tokenCache.getIfPresent(authTokenKey) == null) {
            String authToken;
            try {
                authToken = pxMWCommunicationService.getSecurityToken(
                        globalSettingDao.getString(GlobalSettingType.PX_MIDDLEWARE_USERNAME),
                        globalSettingDao.getString(GlobalSettingType.PX_MIDDLEWARE_PASSWORD),
                        globalSettingDao.getString(GlobalSettingType.PX_MIDDLEWARE_SITE_GUID));
                tokenCache.put(authTokenKey, authToken);
            } catch (PxWhiteCommunicationException e) {
                log.error("Error obtaining PX White Auth Token", e);
            }
        }
        
        response.setAuthToken(tokenCache.getIfPresent(authTokenKey));
        return response;
    }

    @Override
    public void onMessage(Message message) {
        log.debug("DO SOMETHING");
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            log.debug("Token Request received by PxMWAuthTokenService");
            try {
                if (objMessage.getObject() instanceof PxMWAuthTokenRequest) {
                    PxMWAuthTokenResponse response = handle((PxMWAuthTokenRequest) objMessage.getObject());
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

}
