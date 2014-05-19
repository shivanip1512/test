package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeRestProxyFactory {
    private static final Logger log = YukonLogManager.getLogger(EcobeeRestProxyFactory.class);

    private static final String authUrlPart = "register?format=json";

    @Autowired private GlobalSettingDao settingDao;

    private final RestTemplate proxiedTemplate;

    private String authToken = null;

    public EcobeeRestProxyFactory(RestTemplate proxiedTemplate) {
        this.proxiedTemplate = proxiedTemplate;
    }

    public RestOperations createInstance() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws EcobeeException {
                try {
                    addAuthorizationToken(args);
                    Object responseObj = method.invoke(proxiedTemplate, args);
                    if (didAuthenticationFail(responseObj)) {
                        authToken = null;
                        addAuthorizationToken(args);
                        responseObj = method.invoke(proxiedTemplate, args);
                        if (didAuthenticationFail(responseObj)) {
                            throw new RuntimeException("Received an authentication exception immediately after "
                                        + "being authenticated successfully. This should not happen.");
                        }
                    }
                    return responseObj;
                } catch (RestClientException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
                }
            }
        };

        Object obj = Proxy.newProxyInstance(RestOperations.class.getClassLoader(), new Class[] { RestOperations.class },
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
     * Searches arguments for an HttpHeader. If one is found  an ecobee authorization header will be added.
     */
    private void addAuthorizationToken(Object[] args)
            throws EcobeeCommunicationException, EcobeeAuthenticationException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpEntity) {
                Object httpBody = ((HttpEntity<?>) arg).getBody();

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + getAuthenticationToken());
                args[i] = new HttpEntity<>(httpBody, headers);
            }
        }
    }

    private String getAuthenticationToken() throws EcobeeCommunicationException, EcobeeAuthenticationException {
        if (authToken != null) {
            return authToken;
        }
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
            authResponse = proxiedTemplate.postForObject(url, authRequest, AuthenticationResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }

        if (authResponse.hasCode(SUCCESS)) {
            //Authentication was successful. Cache the token and try the request again.
            log.debug("Successfully logged in");
            authToken = authResponse.getToken();
        } else {
            //Authentication failed. Give up.
            throw new EcobeeAuthenticationException(userName, authResponse.getStatus().getMessage());
        }

        return authToken;
    }
}
