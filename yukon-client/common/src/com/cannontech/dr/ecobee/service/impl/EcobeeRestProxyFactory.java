package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.service.BlockingJmsReplyHandler;
import com.cannontech.common.util.YukonHttpProxy;
import com.cannontech.common.util.jms.RequestReplyTemplate;
import com.cannontech.common.util.jms.RequestReplyTemplateImpl;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenRequest;
import com.cannontech.services.ecobee.authToken.message.EcobeeAuthTokenResponse;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeRestProxyFactory {
    private static final Logger log = YukonLogManager.getLogger(EcobeeRestProxyFactory.class);

    @Autowired private ConfigurationSource configSource;
    @Autowired private ConnectionFactory connectionFactory;
    @Autowired private GlobalSettingDao settingDao;

    private final RestTemplate proxiedTemplate;
    private RequestReplyTemplate<EcobeeAuthTokenResponse> ecobeeAuthTokenRequestTemplate;
    
    @PostConstruct
    public void init() {
        ecobeeAuthTokenRequestTemplate = new RequestReplyTemplateImpl<>(JmsApiDirectory.ECOBEE_AUTH_TOKEN.getName(),
                configSource, connectionFactory, JmsApiDirectory.ECOBEE_AUTH_TOKEN.getQueue().getName(), false);
    }
    
    public EcobeeRestProxyFactory(RestTemplate proxiedTemplate) {
        this.proxiedTemplate = proxiedTemplate;
    }

    public RestOperations createInstance() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                try {
                    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                    YukonHttpProxy.fromGlobalSetting(settingDao).ifPresent(httpProxy -> {
                        factory.setProxy(httpProxy.getJavaHttpProxy());
                    });
                    proxiedTemplate.setRequestFactory(factory) ;
                    addAuthorizationToken(args);
                    Object responseObj = method.invoke(proxiedTemplate, args);
                    if (didAuthenticationFail(responseObj)) {
                        addAuthorizationToken(args);
                        responseObj = method.invoke(proxiedTemplate, args);
                        if (didAuthenticationFail(responseObj)) {
                            throw new EcobeeCommunicationException("Communication error during authentication with Ecobee API.");
                        }
                    }
                    return responseObj;
                } catch (RestClientException | IllegalAccessException | IllegalArgumentException | 
                        InvocationTargetException | EcobeeAuthenticationException e) {
                    throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
                }
            }
        };

        Object obj = Proxy.newProxyInstance(RestOperations.class.getClassLoader(), new Class[] {RestOperations.class},
                                            invocationHandler);

        return RestOperations.class.cast(obj);
    }

    private boolean didAuthenticationFail(Object responseObj) {
        if (responseObj instanceof ResponseEntity) {
            responseObj = ((ResponseEntity<?>) responseObj).getBody();
        }
        BaseResponse response = (BaseResponse) responseObj;
        return response.hasCode(AUTHENTICATION_EXPIRED) || response.hasCode(AUTHENTICATION_FAILED);
    }

    /**
     * Searches arguments for an HttpHeader. If one is found an ecobee authorization header will be added.
     */
    private void addAuthorizationToken(Object[] args) throws EcobeeAuthenticationException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpEntity) {
                Object httpBody = ((HttpEntity<?>) arg).getBody();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + getAuthenticationToken());
                log.debug("Added auth header. Headers: " + headers);
                args[i] = new HttpEntity<>(httpBody, headers);
            }
        }
    }

    private String getAuthenticationToken() throws EcobeeAuthenticationException {
        String authToken = null;
        BlockingJmsReplyHandler<EcobeeAuthTokenResponse> reply = new BlockingJmsReplyHandler<>(EcobeeAuthTokenResponse.class);
        EcobeeAuthTokenRequest request = new EcobeeAuthTokenRequest();
        ecobeeAuthTokenRequestTemplate.send(request, reply);
        try {
            EcobeeAuthTokenResponse response = reply.waitForCompletion();
            authToken = response.getAuthToken();
            if (authToken != null) {
                log.debug("Successfully logged in");
            }   
        } catch (Exception e) {
            log.debug("Error getting authToken", e);
        }
        return authToken;
    }
}
