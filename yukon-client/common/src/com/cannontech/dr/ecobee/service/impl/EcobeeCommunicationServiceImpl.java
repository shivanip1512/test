package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.NOT_AUTHORIZED;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.PROCESSING_ERROR;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.SUCCESS;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.VALIDATION_ERROR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
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
import com.cannontech.dr.ecobee.message.DeviceDataResponse;
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.DutyCycleDrRequest;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.ListHierarchyRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);

    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private @Qualifier("ecobee") RestOperations restTemplate;
    @Autowired private EcobeeAuthenticationCache authenticationCache;
    @Autowired private GlobalSettingDao settingDao;

    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "hierarchy/thermostat?format=json";
    private static final String demandResponseUrlPart = "demandResponse?format=json";
    private static final String runtimeReportUrlPart = "runtimeReport?format=json";
    private static final List<String> deviceReadColumns = new ArrayList<>();
    static {
        // If the order is changed here or something is added or removed we need to update
        // JsonSerializers.EcobeeRuntimeReportRow and RuntimeReport
        deviceReadColumns.add("zoneCalendarEvent"); //currently running event
        deviceReadColumns.add("zoneAveTemp"); // indoor temp
        deviceReadColumns.add("outdoorTemp"); // outdoor temp
        deviceReadColumns.add("zoneCoolTemp"); // cool set point
        deviceReadColumns.add("zoneHeatTemp"); // heat set point
        deviceReadColumns.add("compCool1"); // cool runtime
        deviceReadColumns.add("compHeat1"); // heat runtime
    }

    @Override
    public boolean registerDevice(String serialNumber) throws EcobeeException {

        String url = getUrlBase() + modifyThermostatUrlPart;

        RegisterDeviceRequest request = new RegisterDeviceRequest(serialNumber);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        if (response.hasCode(VALIDATION_ERROR)) {
            //device already exists
            return true;
        }

        return response.getSuccess();
    }

    @Override
    public boolean moveDeviceToSet(String serialNumber, String setPath) throws EcobeeException {
        boolean success = false;
        try {
            success = attemptMoveDeviceToSet(serialNumber, setPath);
        } catch (EcobeeSetDoesNotExistException e) {
            createManagementSet(setPath);
            success = attemptMoveDeviceToSet(serialNumber, setPath);
        }
        return success;
    }

    private boolean attemptMoveDeviceToSet(String serialNumber, String setPath) throws EcobeeException {

        String url = getUrlBase() + modifyThermostatUrlPart;

        MoveDeviceRequest request = new MoveDeviceRequest(serialNumber, setPath);
        HttpEntity<MoveDeviceRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        // Set doesn't exist, or we don't have permission
        if (response.hasCode(NOT_AUTHORIZED)) {
            throw new EcobeeSetDoesNotExistException(setPath);
        } else if (response.hasCode(PROCESSING_ERROR)) {
            throw new EcobeeDeviceDoesNotExistException(serialNumber);
        }

        return response.getSuccess();
    }

    @Override
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange)
            throws EcobeeException {

        HttpEntity<RuntimeReportRequest> requestEntity = new HttpEntity<>(new HttpHeaders());
        //Build base url
        //RestTemplate could do the parameter substitution for us, except it chokes on the JSON that ecobee, in their
        //infinite wisdom, forces us to use in the URL.
        String url = getUrlBase() + runtimeReportUrlPart + "&body={bodyJson}";

        //Add url parameters
        int startInterval = dateRange.getMin().get(DateTimeFieldType.minuteOfDay()) / 5;
        int endInterval = dateRange.getMax().get(DateTimeFieldType.minuteOfDay()) / 5;

        RuntimeReportRequest request = new RuntimeReportRequest(dateRange.getMin(), startInterval,
                  dateRange.getMax(), endInterval, serialNumbers, deviceReadColumns);

        DeviceDataResponse response = queryEcobeeGet(url, requestEntity, request, EcobeeQueryType.DATA_COLLECTION,
                                          DeviceDataResponse.class);

        List<EcobeeDeviceReadings> deviceData = new ArrayList<>();
        for (RuntimeReport runtimeReport : response.getReportList()) {
            List<EcobeeDeviceReading> readings = new ArrayList<>();
            for (RuntimeReportRow reportRow : runtimeReport.getRuntimeReports()) {
                // TODO: only using HeatRuntime here not CoolRuntime since EcobeeDeviceReading only has one runtime
                // property. Need to figure out if EcobeeDeviceReading should have both as a runtime or if some other
                // value is needed
                EcobeeDeviceReading reading = new EcobeeDeviceReading(reportRow.getOutdoorTemp(),
                    reportRow.getIndoorTemp(), reportRow.getCoolSetPoint(), reportRow.getHeatSetPoint(),
                    reportRow.getHeatRuntime(), reportRow.getEventName(), reportRow.getDate());
                readings.add(reading);
            }
            deviceData.add(new EcobeeDeviceReadings(runtimeReport.getThermostatIdentifier(), dateRange, readings));
        }
        return deviceData;
    }

    @Override
    public boolean createManagementSet(String managementSetName) throws EcobeeException {
        String url = getUrlBase() + modifySetUrlPart;

        CreateSetRequest request = new CreateSetRequest(managementSetName);
        HttpEntity<CreateSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        if (response.hasCode(VALIDATION_ERROR)) {
            //set already exists
            return true;
        }

        return response.getSuccess();
    }

    @Override
    public boolean deleteManagementSet(String managementSetName) throws EcobeeException {

        String url = getUrlBase() + modifySetUrlPart;

        DeleteSetRequest request = new DeleteSetRequest(managementSetName);
        HttpEntity<DeleteSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        log.info("Deleting set " + managementSetName + " URL: " + url);
        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newPath) throws EcobeeException {

        String url = getUrlBase() + modifySetUrlPart;

        MoveSetRequest request = new MoveSetRequest(currentPath, newPath);
        HttpEntity<MoveSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) throws EcobeeException {
        String url = getUrlBase() + demandResponseUrlPart;

        String groupIdString = Integer.toString(parameters.getGroupId());
        DutyCycleDrRequest request = new DutyCycleDrRequest(groupIdString, "yukonDutyCycleDr",
                    parameters.getDutyCyclePercent(), parameters.getStartTime(), parameters.isRampIn(),
                    parameters.getEndTime(), parameters.isRampOut());

        HttpEntity<DutyCycleDrRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        DrResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, DrResponse.class);

        return response.getDemandResponseRef();
    }

    @Override
    public boolean sendRestore(String drIdentifier) throws EcobeeException {

        String url = getUrlBase() + demandResponseUrlPart;

        DrRestoreRequest request = new DrRestoreRequest(drIdentifier);
        HttpEntity<DrRestoreRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        BaseResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, BaseResponse.class);

        return response.hasCode(SUCCESS);
    }

    @Override
    public List<SetNode> getHierarchy() throws EcobeeException {

        //Create base url
        String url = getUrlBase() + modifySetUrlPart + "&body={bodyJson}";

        ListHierarchyRequest request = new ListHierarchyRequest();
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());

        HierarchyResponse response = queryEcobeeGet(url, requestEntity, request, EcobeeQueryType.SYSTEM,
                                         HierarchyResponse.class);

        return response.getSets();
    }

    private <E extends BaseResponse> E queryEcobeeGet(String url, HttpEntity<?> requestEntity, Object request,
        EcobeeQueryType queryType, Class<E> responseType)
            throws EcobeeCommunicationException, EcobeeNotAuthenticatedException {

        ecobeeQueryCountDao.incrementQueryCount(queryType);
        try {
            ResponseEntity<E> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType,
                                                   Collections.singletonMap("bodyJson", JsonUtils.toJson(request)));
            return responseEntity.getBody();
        } catch (JsonProcessingException e) {
            throw new EcobeeCommunicationException("Unable to communicate with Ecobee API", e);
        }
    }

    private <E extends BaseResponse> E queryEcobee(String url, HttpEntity<?> request, EcobeeQueryType queryType,
            Class<E> responseType) throws EcobeeCommunicationException, EcobeeNotAuthenticatedException {
        ecobeeQueryCountDao.incrementQueryCount(queryType);
        return restTemplate.postForObject(url, request, responseType);
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}
