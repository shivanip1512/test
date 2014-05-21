package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
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
import com.google.common.collect.ImmutableList;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);

    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private @Qualifier("ecobee") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;

    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "hierarchy/thermostat?format=json";
    private static final String demandResponseUrlPart = "demandResponse?format=json";
    private static final String runtimeReportUrlPart = "runtimeReport?format=json";
    private static final List<String> deviceReadColumns = ImmutableList.of(
        // If the order is changed here or something is added or removed we need to update
        // JsonSerializers.EcobeeRuntimeReportRow and RuntimeReport
        "zoneCalendarEvent", //currently running event
        "zoneAveTemp", // indoor temp
        "outdoorTemp", // outdoor temp
        "zoneCoolTemp", // cool set point
        "zoneHeatTemp", // heat set point
        "compCool1", // cool runtime
        "compHeat1"); // heat runtime

    @Override
    public boolean registerDevice(String serialNumber) {
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
    public boolean moveDeviceToSet(String serialNumber, String setPath) 
            throws EcobeeDeviceDoesNotExistException, EcobeeSetDoesNotExistException {
        boolean success = false;
        try {
            success = attemptMoveDeviceToSet(serialNumber, setPath);
        } catch (EcobeeSetDoesNotExistException e) {
            createManagementSet(setPath);
            success = attemptMoveDeviceToSet(serialNumber, setPath);
        }
        return success;
    }

    private boolean attemptMoveDeviceToSet(String serialNumber, String setPath) 
            throws EcobeeSetDoesNotExistException, EcobeeDeviceDoesNotExistException {

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
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange) {
        DateTimeFormatter ecobeeDateTimeFormatter =
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZoneUTC();

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
            List<RuntimeReportRow> sortedReports = new ArrayList<>(runtimeReport.getRuntimeReports());
            Collections.sort(sortedReports, new Comparator<RuntimeReportRow>() {
                @Override
                public int compare(RuntimeReportRow rowA, RuntimeReportRow rowB) {
                    return rowA.getDateStr().compareTo(rowB.getDateStr());
                }
            });

            DateTime firstRowDateTime = ecobeeDateTimeFormatter.parseDateTime(sortedReports.get(0).getDateStr());
            int timeZoneHoursOffset = new Period(dateRange.getMin(), firstRowDateTime.toInstant()).getHours();
            for (RuntimeReportRow reportRow : sortedReports) {
                DateTime rowDateTime = ecobeeDateTimeFormatter.parseDateTime(reportRow.getDateStr());
                Instant date = rowDateTime.minusHours(timeZoneHoursOffset).toInstant();
                EcobeeDeviceReading reading = new EcobeeDeviceReading(reportRow.getOutdoorTemp(),
                    reportRow.getIndoorTemp(), reportRow.getCoolSetPoint(), reportRow.getHeatSetPoint(),
                    reportRow.getRuntime(), reportRow.getEventName(), date);
                readings.add(reading);
            }
            deviceData.add(new EcobeeDeviceReadings(runtimeReport.getThermostatIdentifier(), dateRange, readings));
        }
        return deviceData;
    }

    @Override
    public boolean createManagementSet(String managementSetName) {
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
    public boolean deleteManagementSet(String managementSetName) {
        String url = getUrlBase() + modifySetUrlPart;

        DeleteSetRequest request = new DeleteSetRequest(managementSetName);
        HttpEntity<DeleteSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        log.info("Deleting set " + managementSetName + " URL: " + url);
        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newPath) {
        String url = getUrlBase() + modifySetUrlPart;

        MoveSetRequest request = new MoveSetRequest(currentPath, newPath);
        HttpEntity<MoveSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) {
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
    public boolean sendRestore(String drIdentifier) {

        String url = getUrlBase() + demandResponseUrlPart;

        DrRestoreRequest request = new DrRestoreRequest(drIdentifier);
        HttpEntity<DrRestoreRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        BaseResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, BaseResponse.class);

        return response.hasCode(SUCCESS);
    }

    @Override
    public List<SetNode> getHierarchy() {

        //Create base url
        String url = getUrlBase() + modifySetUrlPart + "&body={bodyJson}";

        ListHierarchyRequest request = new ListHierarchyRequest();
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());

        HierarchyResponse response = queryEcobeeGet(url, requestEntity, request, EcobeeQueryType.SYSTEM,
                                         HierarchyResponse.class);

        return response.getSets();
    }

    private <E extends BaseResponse> E queryEcobeeGet(String url, HttpEntity<?> requestEntity, Object request,
        EcobeeQueryType queryType, Class<E> responseType) throws EcobeeCommunicationException {

        ecobeeQueryCountDao.incrementQueryCount(queryType);
        try {
            ResponseEntity<E> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType,
                                                   Collections.singletonMap("bodyJson", JsonUtils.toJson(request)));
            return responseEntity.getBody();
        } catch (JsonProcessingException e) {
            throw new EcobeeCommunicationException("Unable to parse request object into valid JSON.", e);
        }
    }

    private <E extends BaseResponse> E queryEcobee(String url, HttpEntity<?> request, EcobeeQueryType queryType,
            Class<E> responseType) {
        ecobeeQueryCountDao.incrementQueryCount(queryType);
        return restTemplate.postForObject(url, request, responseType);
    }

    private String getUrlBase() {
        return settingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);
    }
}
