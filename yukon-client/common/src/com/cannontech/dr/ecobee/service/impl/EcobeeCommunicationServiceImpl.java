package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.dr.ecobee.EcobeeAuthenticationCache;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.EcobeeNotAuthenticatedException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.message.BaseResponse;
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DeleteSetRequest;
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.DutyCycleDrRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired @Qualifier("Ecobee") private RestTemplate restTemplate;
    @Autowired private EcobeeAuthenticationCache authenticationCache;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private DateFormattingService dateFormattingService;
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);
    
    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "hierarchy/thermostat?format=json";
    private static final String demandResponseUrlPart = "demandResponse?format=json";
    private static final DateTimeFormatter drDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");
    private static final DateTimeFormatter drTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
    
    private <E extends BaseResponse> E queryEcobee(String url, HttpEntity<?> request, int ecId, Class<E> responseType) 
            throws EcobeeCommunicationException, EcobeeNotAuthenticatedException {
        try {
            E response = restTemplate.postForObject(url, request, responseType);
            if (response.hasCode(AUTHENTICATION_EXPIRED) || response.hasCode(AUTHENTICATION_FAILED)) {
                // causes EcobeeCommunicationAopAuthenticator to log us back in and try again
                throw new EcobeeNotAuthenticatedException(ecId);
            }
            return response;
        } catch (RestClientException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
    }

    @Override
    public boolean registerDevice(String serialNumber, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + modifyThermostatUrlPart;

        RegisterDeviceRequest request = new RegisterDeviceRequest(serialNumber);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, ecId);
        StandardResponse response = queryEcobee(url, requestEntity, ecId, StandardResponse.class);

        if (response.hasCode(VALIDATION_ERROR)) {
            //device already exists
            return true;
        }
        
        return response.getSuccess();
    }

    @Override
    public boolean moveDeviceToSet(String serialNumber, String setPath, int ecId) throws EcobeeException {
        boolean success = false;
        try {
            success = attempMoveDeviceToSet(serialNumber, setPath, ecId);
        } catch (EcobeeSetDoesNotExistException e) {
            createManagementSet(setPath, ecId);
            success =  attempMoveDeviceToSet(serialNumber, setPath, ecId);
        }
        return success;
    }

    private boolean attempMoveDeviceToSet(String serialNumber, String setPath, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + modifyThermostatUrlPart;

        MoveDeviceRequest request = new MoveDeviceRequest(serialNumber, setPath);
        HttpEntity<MoveDeviceRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, ecId);
        StandardResponse response = queryEcobee(url, requestEntity, ecId, StandardResponse.class);

        // Set doesn't exist, or we don't have permission
        if (response.hasCode(NOT_AUTHORIZED)) {
            throw new EcobeeSetDoesNotExistException(setPath);
        } else if (response.hasCode(PROCESSING_ERROR)) {
            throw new EcobeeDeviceDoesNotExistException(serialNumber);
        }
        
        return response.getSuccess();
    }
    
    @Override
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange, int ecId)
            throws EcobeeException {

        // TODO: implement this 
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.DATA_COLLECTION, ecId);
        return new ArrayList<>();
    }

    private boolean createManagementSet(String managementSetName, int ecId) throws EcobeeException {
        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + modifySetUrlPart;
        
        CreateSetRequest request = new CreateSetRequest(managementSetName);
        HttpEntity<CreateSetRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, ecId);
        StandardResponse response = queryEcobee(url, requestEntity, ecId, StandardResponse.class);

        if (response.hasCode(VALIDATION_ERROR)) {
            //set already exists
            return true;
        }
        
        return response.getSuccess();
    }
    
    @Override
    public boolean deleteManagementSet(String managementSetName, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + modifySetUrlPart;
        
        DeleteSetRequest request = new DeleteSetRequest(managementSetName);
        HttpEntity<DeleteSetRequest> requestEntity = new HttpEntity<>(request, headers);
        
        log.info("Deleting set " + managementSetName + ", energy company id " + ecId + " URL: " + url);
        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, ecId);
        StandardResponse response = queryEcobee(url, requestEntity, ecId, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newPath, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + modifySetUrlPart;
        
        MoveSetRequest request = new MoveSetRequest(currentPath, newPath);
        HttpEntity<MoveSetRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.SYSTEM, ecId);
        StandardResponse response = queryEcobee(url, requestEntity, ecId, StandardResponse.class);

        return response.getSuccess();
    }
    
    @Override
    public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + demandResponseUrlPart;
        
        String startDateString = drDateFormatter.print(parameters.getStartTime());
        String startTimeString = drTimeFormatter.print(parameters.getStartTime());
        String endDateString = drDateFormatter.print(parameters.getEndTime());
        String endTimeString = drTimeFormatter.print(parameters.getEndTime());
        
        String groupIdString = Integer.toString(parameters.getGroupId());
        DutyCycleDrRequest request = new DutyCycleDrRequest(groupIdString, "yukonDutyCycleDr", 
                                                            parameters.getDutyCyclePercent(), startDateString,
                                                            startTimeString, parameters.isRampIn(), endDateString,
                                                            endTimeString, parameters.isRampOut());
        HttpEntity<DutyCycleDrRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.DEMAND_RESPONSE, ecId);
        DrResponse response = queryEcobee(url, requestEntity, ecId, DrResponse.class);

        return response.getDemandResponseRef();
    }

    @Override
    public boolean sendRestore(String drIdentifier, int ecId) throws EcobeeException {

        HttpHeaders headers = getHeadersWithAuthentication(ecId);
        String url = getUrlBase(ecId) + demandResponseUrlPart;

        DrRestoreRequest request = new DrRestoreRequest(drIdentifier);
        HttpEntity<DrRestoreRequest> requestEntity = new HttpEntity<>(request, headers);

        ecobeeQueryCountDao.incrementQueryCount(EcobeeQueryType.DEMAND_RESPONSE, ecId);
        BaseResponse response = queryEcobee(url, requestEntity, ecId, BaseResponse.class);

        return response.hasCode(SUCCESS);
    }

    /**
     * Build a HttpHeaders object with the specified energy company's authentication token in the Authorization header.
     * @throws EcobeeNotAuthenticatedException if there is no cached authentication token for the energy company.
     */
    private HttpHeaders getHeadersWithAuthentication(int energyCompanyId) throws EcobeeException {
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

    private String getUrlBase(int ecId) {
        return ecSettingDao.getString(EnergyCompanySettingType.ECOBEE_SERVER_URL, ecId);
    }
}
