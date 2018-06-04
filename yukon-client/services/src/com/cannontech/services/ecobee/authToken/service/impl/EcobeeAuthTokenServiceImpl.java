package com.cannontech.services.ecobee.authToken.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.SUCCESS;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenResponse;
import com.cannontech.services.ecobee.authToken.service.EcobeeAuthTokenService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EcobeeAuthTokenServiceImpl implements EcobeeAuthTokenService, MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeAuthTokenServiceImpl.class);

    
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private ConnectionFactory connectionFactory;
    private final RestTemplate restTemplate;

    private Cache<String,String> tokenCache = CacheBuilder.newBuilder().expireAfterWrite(59, TimeUnit.MINUTES).build();
    private static final String authTokenKey = "authTokenKey";
    private static final String authUrlPart = "register?format=json";
    private JmsTemplate jmsTemplate;

    private static final int incomingMessageWaitMillis = 1000;

    
    @PostConstruct
    public void init() {
        jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setReceiveTimeout(incomingMessageWaitMillis);
    }
    
    public EcobeeAuthTokenServiceImpl(RestTemplate proxiedTemplate) {
        this.restTemplate = proxiedTemplate;
    }
    
    @Override
    public EcobeeAuthTokenResponse handle(EcobeeAuthTokenRequest request) throws JMSException {
        
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
            factory.setProxy(httpProxy.getJavaHttpProxy());
        });
        restTemplate.setRequestFactory(factory);
        
        EcobeeAuthTokenResponse response = new EcobeeAuthTokenResponse();

        if (tokenCache.getIfPresent(authTokenKey) == null) {
            if (tokenCache.getIfPresent(authTokenKey) == null) {
                try {
                    String authToken = generateAuthenticationToken();
                    tokenCache.put(authTokenKey, authToken);
                } catch (EcobeeAuthenticationException e) {
                    log.warn("caught exception in handle", e);
                }
            }
        }

        response.setAuthToken(tokenCache.getIfPresent(authTokenKey));
        return response;
    }
        
    private String generateAuthenticationToken() throws EcobeeAuthenticationException {
        
        //The request failed because the energy company's authentication token is expired
        //or no token has been generated yet.
        String urlBase = settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
        String password = settingDao.getString(GlobalSettingType.ECOBEE_PASSWORD);
        String userName = settingDao.getString(GlobalSettingType.ECOBEE_USERNAME);
        //Sanity-check configuration values
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new EcobeeAuthenticationException("One or more ecobee authentication settings is empty.");
        }

        String url = urlBase + authUrlPart;
        log.debug("Attempting login with userName " + userName + " URL: "
                 + url);

        AuthenticationRequest authRequest = new AuthenticationRequest(userName, password);
        AuthenticationResponse authResponse;
        try {
            authResponse = restTemplate.postForObject(url, authRequest, AuthenticationResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }

        if (authResponse.hasCode(SUCCESS)) {
            //Authentication was successful. Cache the token and try the request again.
            log.debug("Successfully logged in");
        } else {
            //Authentication failed. Give up.
            throw new EcobeeAuthenticationException(userName, authResponse.getStatus().getMessage());
        }
        return authResponse.getToken();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            ObjectMessage objMessage = (ObjectMessage) message;
            try {
                if (objMessage.getObject() instanceof EcobeeAuthTokenRequest) {
                    EcobeeAuthTokenResponse response = handle((EcobeeAuthTokenRequest) objMessage.getObject());
                    jmsTemplate.convertAndSend(message.getJMSReplyTo(), response);
                }
            } catch (Exception e) {
                log.error("Unable to process message", e);
            }
        }
    }

}
