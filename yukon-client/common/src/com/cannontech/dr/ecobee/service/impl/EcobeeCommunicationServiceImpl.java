package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.EcobeeAuthenticationCache;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeNotAuthenticatedException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired @Qualifier("Ecobee") RestTemplate restTemplate;
    @Autowired EcobeeAuthenticationCache authenticationCache;
    @Autowired EnergyCompanySettingDao ecSettingDao;
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);
    
    private static final String MODIFY_SET_URL_PART = "hierarchy/set?format=json";
    private static final String MODIFY_THERMOSTAT_URL_PART = "/hierarchy/thermostat?format=json";
    
    @Override
    public boolean registerDevice(String serialNumber, int energyCompanyId)
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + MODIFY_THERMOSTAT_URL_PART;
        
        EcobeeMessages.RegisterDeviceRequest request = new EcobeeMessages.RegisterDeviceRequest(serialNumber);
        HttpEntity<EcobeeMessages.RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        
        EcobeeMessages.RegisterDeviceResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, EcobeeMessages.RegisterDeviceResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        if (response.getStatus().getCode() == EcobeeStatusCode.VALIDATION_ERROR.getCode()) {
            //device already exists
            return true;
        }
        
        checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }
    
    @Override
    public boolean moveDeviceToSet(String serialNumber, String setPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException,
            EcobeeDeviceDoesNotExistException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + MODIFY_THERMOSTAT_URL_PART;
        
        EcobeeMessages.MoveDeviceRequest request = new EcobeeMessages.MoveDeviceRequest(serialNumber, setPath);
        HttpEntity<EcobeeMessages.MoveDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        
        EcobeeMessages.MoveDeviceResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, EcobeeMessages.MoveDeviceResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        //Set doesn't exist, or we don't have permission
        if (response.getStatus().getCode() == EcobeeStatusCode.NOT_AUTHORIZED.getCode()) {
            throw new EcobeeSetDoesNotExistException(setPath);
        } else if (response.getStatus().getCode() == EcobeeStatusCode.PROCESSING_ERROR.getCode()) {
            throw new EcobeeDeviceDoesNotExistException(serialNumber);
        }
        
        checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }
    
    @Override
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange, 
            int energyCompanyId) throws EcobeeAuthenticationException, EcobeeCommunicationException {
//        List<EcobeeDeviceReadings> deviceReadings = new ArrayList<>();
//        for (String serialNumber : serialNumbers) {
//            List<EcobeeDeviceReading> readings = new ArrayList<>();
//            for (int i=0;i<100;i++) {
//                readings.add(new EcobeeDeviceReading(90.98465498798f, 60.298465498798f, 60.3459846534498798f, 
//                                                     50.465498798f, 600, "not sure", Instant.now().plus(Duration.standardHours((long) (Math.random()*100)-50))));
//            }
//            deviceReadings.add(new EcobeeDeviceReadings(serialNumber, dateRange, readings));
//        }
//        return deviceReadings;
        
        //checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.DATA_COLLECTION, energyCompanyId);
        return new ArrayList<>();
    }

    @Override
    public boolean createManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + MODIFY_SET_URL_PART;
        
        EcobeeMessages.CreateSetRequest request = new EcobeeMessages.CreateSetRequest(managementSetName);
        HttpEntity<EcobeeMessages.CreateSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        EcobeeMessages.CreateSetResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, EcobeeMessages.CreateSetResponse.class);
        } catch(RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        if (response.getStatus().getCode() == EcobeeStatusCode.VALIDATION_ERROR.getCode()) {
            //set already exists
            return true;
        }
        
        checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }
    
    @Override
    public boolean deleteManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + MODIFY_SET_URL_PART;
        
        EcobeeMessages.DeleteSetRequest request = new EcobeeMessages.DeleteSetRequest(managementSetName);
        HttpEntity<EcobeeMessages.DeleteSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        log.info("Deleting set " + managementSetName + ", energy company id " + energyCompanyId + " URL: " + url);
        EcobeeMessages.DeleteSetResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, EcobeeMessages.DeleteSetResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }
            
        checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + MODIFY_SET_URL_PART;
        
        EcobeeMessages.MoveSetRequest request = new EcobeeMessages.MoveSetRequest(currentPath, newPath);
        HttpEntity<EcobeeMessages.MoveSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        EcobeeMessages.MoveSetResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, EcobeeMessages.MoveSetResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }
        
        checkForAuthenticationError(response.getStatus(), energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }
    
    /**
     * Build a HttpHeaders object with the specified energy company's authentication token in the Authorization header.
     * @throws EcobeeNotAuthenticatedException if there is no cached authentication token for the energy company.
     */
    private HttpHeaders getHeadersWithAuthentication(int energyCompanyId) throws EcobeeNotAuthenticatedException{
        //Attempt to get stored authentication token
        String authToken = authenticationCache.get(energyCompanyId);
        if (authToken == null) {
            //throw exception - EcobeeCommunicationAopAuthenticator will log us in and try again
            throw new EcobeeNotAuthenticatedException(energyCompanyId);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authToken);
        return headers;
    }
    
    private String getUrlBase(int energyCompanyId) {
        return ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_SERVER_URL, energyCompanyId);
    }
    
    /**
     * Checks the status code to determine if the API query failed due to an expired or invalid authentication token.
     */
    private void checkForAuthenticationError(EcobeeMessages.Status status, int energyCompanyId) {
        if(status.getCode() == EcobeeStatusCode.AUTHENTICATION_EXPIRED.getCode()
                || status.getCode() == EcobeeStatusCode.AUTHENTICATION_FAILED.getCode()) {
            //throw exception - EcobeeCommunicationAopAuthenticator will log us in and try again
            throw new EcobeeNotAuthenticatedException(energyCompanyId);
        }
    }
}
