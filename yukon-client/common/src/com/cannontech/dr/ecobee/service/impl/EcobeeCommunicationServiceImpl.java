package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

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
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DeleteSetRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.AbstractResponse;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired @Qualifier("Ecobee") private RestTemplate restTemplate;
    @Autowired private EcobeeAuthenticationCache authenticationCache;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);
    
    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "/hierarchy/thermostat?format=json";
    
    @Override
    public boolean registerDevice(String serialNumber, int energyCompanyId)
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + modifyThermostatUrlPart;
        
        RegisterDeviceRequest request = new RegisterDeviceRequest(serialNumber);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        
        StandardResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, StandardResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        checkForAuthenticationError(response, energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        
        if (response.hasCode(VALIDATION_ERROR)) {
            //device already exists
            return true;
        }
        
        return response.getSuccess();
    }
    
    @Override
    public boolean moveDeviceToSet(String serialNumber, String setPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException,
            EcobeeDeviceDoesNotExistException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + modifyThermostatUrlPart;
        
        MoveDeviceRequest request = new MoveDeviceRequest(serialNumber, setPath);
        HttpEntity<MoveDeviceRequest> requestEntity = new HttpEntity<>(request, headers);
        
        StandardResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, StandardResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        checkForAuthenticationError(response, energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        
        //Set doesn't exist, or we don't have permission
        if (response.hasCode(NOT_AUTHORIZED)) {
            throw new EcobeeSetDoesNotExistException(setPath);
        } else if (response.hasCode(PROCESSING_ERROR)) {
            throw new EcobeeDeviceDoesNotExistException(serialNumber);
        }
        
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
        String url = getUrlBase(energyCompanyId) + modifySetUrlPart;
        
        CreateSetRequest request = new CreateSetRequest(managementSetName);
        HttpEntity<CreateSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        StandardResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, StandardResponse.class);
        } catch(RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
        
        checkForAuthenticationError(response, energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        
        if (response.hasCode(VALIDATION_ERROR)) {
            //set already exists
            return true;
        }
        
        return response.getSuccess();
    }
    
    @Override
    public boolean deleteManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + modifySetUrlPart;
        
        DeleteSetRequest request = new DeleteSetRequest(managementSetName);
        HttpEntity<DeleteSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        log.info("Deleting set " + managementSetName + ", energy company id " + energyCompanyId + " URL: " + url);
        StandardResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, StandardResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }
            
        checkForAuthenticationError(response, energyCompanyId);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, energyCompanyId);
        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        
        HttpHeaders headers = getHeadersWithAuthentication(energyCompanyId);
        String url = getUrlBase(energyCompanyId) + modifySetUrlPart;
        
        MoveSetRequest request = new MoveSetRequest(currentPath, newPath);
        HttpEntity<MoveSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        StandardResponse response;
        try {
            response = restTemplate.postForObject(url, requestEntity, StandardResponse.class);
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API.", e);
        }
        
        checkForAuthenticationError(response, energyCompanyId);
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
    private void checkForAuthenticationError(AbstractResponse response, int energyCompanyId) {
        if(response.hasCode(AUTHENTICATION_EXPIRED) || response.hasCode(AUTHENTICATION_FAILED)) {
            //throw exception - EcobeeCommunicationAopAuthenticator will log us in and try again
            throw new EcobeeNotAuthenticatedException(energyCompanyId);
        }
    }
}
