package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.AUTHENTICATION_EXPIRED;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.AUTHENTICATION_FAILED;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.SUCCESS;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

public class EcobeeRestProxyFactory {
    private static final Logger log = YukonLogManager.getLogger(EcobeeRestProxyFactory.class);

    private static final String authUrlPart = "register?format=json";

    @Autowired private EnergyCompanySettingDao ecSettingDao;

    private final RestTemplate proxiedTemplate;

    private String authToken = null;

    public EcobeeRestProxyFactory(RestTemplate proxiedTemplate) {
        this.proxiedTemplate = proxiedTemplate;
    }

    public RestOperations createInstance() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    // Add token to request
                    addAuthorizationToken(args);

                    BaseResponse response = (BaseResponse) method.invoke(proxiedTemplate, args);
                    if (response.hasCode(AUTHENTICATION_EXPIRED) || response.hasCode(AUTHENTICATION_FAILED)) {
                        authToken = null;
                        addAuthorizationToken(args);

                        response = (BaseResponse) method.invoke(proxiedTemplate, args);
                        if (response.hasCode(AUTHENTICATION_EXPIRED) || response.hasCode(AUTHENTICATION_FAILED)) {
                            throw new RuntimeException("Received an authentication exception immediately after "
                                        + "being authenticated successfully. This should not happen.");
                        }
                    }
                    return response;
                } catch (RestClientException e) {
                    throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
                }
            }
        };

        Object obj = Proxy.newProxyInstance(RestOperations.class.getClassLoader(), new Class[] { RestOperations.class },
            invocationHandler);

        return RestOperations.class.cast(obj);
    }

    /**
     * Searches arguments for an HttpHeader. If one is found it is assumed it will have an 'EnergyCompanyId' value
     * which will be removed and an ecobee authorization header added in its place.
     */
    private void addAuthorizationToken(Object[] args)
            throws EcobeeCommunicationException, EcobeeAuthenticationException {
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpEntity) {
                Object httpBody = ((HttpEntity<?>) arg).getBody();
                HttpHeaders httpHeader = ((HttpEntity<?>) arg).getHeaders();

                int ecId = Integer.parseInt(httpHeader.get("EnergyCompanyId").get(0));

                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + getAuthenticationToken(ecId));
                headers.add("EnergyCompanyId", Integer.toString(ecId));
                args[i] = new HttpEntity<>(httpBody, headers);
            }
        }
    }

    private String getAuthenticationToken(int ecId) throws EcobeeCommunicationException, EcobeeAuthenticationException {
        if (authToken != null) {
            return authToken;
        }
        //The request failed because the energy company's authentication token is expired
        //or no token has been generated yet.
        String urlBase = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_SERVER_URL, ecId);
        String password = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_PASSWORD, ecId);
        String userName = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_USERNAME, ecId);

        //Sanity-check configuration values
        if (StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
            throw new EcobeeAuthenticationException("One or more ecobee authentication settings is empty. "
                                                    + "Energy company id: " + ecId);
        }

        String url = urlBase + authUrlPart;
        log.debug("Attempting login for energy company id " + ecId + " with userName " + userName + " URL: "
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
            log.debug("Successfully logged in energy company id " + ecId);
            authToken = authResponse.getToken();
        } else {
            //Authentication failed. Give up.
            throw new EcobeeAuthenticationException(userName, authResponse.getStatus().getMessage());
        }

        return authToken;
    }
}
