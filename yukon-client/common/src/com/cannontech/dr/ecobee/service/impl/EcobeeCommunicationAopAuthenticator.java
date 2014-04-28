package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeAuthenticationCache;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeNotAuthenticatedException;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

/**
 * AOP interceptor for EcobeeCommunicationService.
 * 
 * When a method in EcobeeCommunicationService fails due to expired authentication credentials, it throws an
 * EcobeeNotAuthenticatedException. The exception is caught by this intercepter, which extracts the energy company id
 * and tries to re-authenticate with the Ecobee API. If authentication is successful, the authentication token is stored
 * in EcobeeAuthenticationCache and the failed EcobeeCommunicationService method is invoked again.
 */
public class EcobeeCommunicationAopAuthenticator implements MethodInterceptor {
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired @Qualifier("Ecobee") private RestTemplate restTemplate;
    @Autowired private EcobeeAuthenticationCache authenticationCache;
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationAopAuthenticator.class);
    
    private static final String AUTH_URL_PART = "register?format=json";
    
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        try {
            //First try the request
            log.trace(authenticationCache.get(0));
            return methodInvocation.proceed();
        } catch (EcobeeNotAuthenticatedException exception) {
            //The request failed because the energy company's authentication token is expired
            //or no token has been generated yet.
            int ecId = exception.getEnergyCompanyId();
            String urlBase = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_SERVER_URL, ecId);
            String password = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_PASSWORD, ecId);
            String userName = ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_USERNAME, ecId);
            
            //Sanity-check configuration values
            if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(password)) {
                throw new EcobeeAuthenticationException("One or more ecobee authentication settings is empty. "
                                                        + "Energy company id: " + ecId);
            }
            
            String url = urlBase + AUTH_URL_PART;
            log.debug("Attempting login for energy company id " + ecId + " with userName " + userName + " URL: " 
                     + url);
            
            EcobeeMessages.AuthenticationRequest authRequest = 
                    new EcobeeMessages.AuthenticationRequest(userName, password);
            EcobeeMessages.AuthenticationResponse authResponse;
            try {
                authResponse = restTemplate.postForObject(url, authRequest, 
                                                          EcobeeMessages.AuthenticationResponse.class);
            } catch (RestClientException e) {
                throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
            }
            
            if (authResponse.getStatus().getCode() == SUCCESS.getCode()) {
                //Authentication was successful. Cache the token and try the request again.
                authenticationCache.put(ecId, authResponse.getToken());
                log.debug("Successfully logged in energy company id " + ecId);
                return methodInvocation.proceed();
            } else {
                //Authentication failed. Give up.
                throw new EcobeeAuthenticationException(userName, authResponse.getStatus().getMessage());
            }
        }
    }
}
