package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.NOT_AUTHORIZED;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.PROCESSING_ERROR;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.SUCCESS;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.VALIDATION_ERROR;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.MutableDateTime;
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
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileUtil;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.YukonHttpProxy;
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
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.UnregisterDeviceRequest;
import com.cannontech.dr.ecobee.message.partial.DutyCycleDr;
import com.cannontech.dr.ecobee.message.partial.RuntimeReport;
import com.cannontech.dr.ecobee.message.partial.RuntimeReportRow;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.csv.CSVReader;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);

    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private @Qualifier("ecobee") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private EcobeeSecurityService ecobeeSecurityService;

    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "hierarchy/thermostat?format=json";
    private static final String demandResponseUrlPart = "demandResponse?format=json";
    private static final String runtimeReportUrlPart = "runtimeReport?format=json";
    private static final String createRuntimeReportJobUrlPart = "runtimeReportJob/create";
    private static final List<String> deviceReadColumns = ImmutableList.of(
        // If the order is changed here or something is added or removed we need to update
        // JsonSerializers.RuntimeReportRow and RuntimeReport
        "zoneCalendarEvent", //currently running event
        "zoneAveTemp", // indoor temp
        "outdoorTemp", // outdoor temp
        "zoneCoolTemp", // cool set point
        "zoneHeatTemp", // heat set point
        "compCool1", // cool runtime
        "compHeat1"); // heat runtime

    @Override
    public void registerDevice(String serialNumber) {
        log.debug("Registering ecobee device with serial number " + serialNumber);
        
        String url = getUrlBase() + modifyThermostatUrlPart;

        RegisterDeviceRequest request = new RegisterDeviceRequest(serialNumber);
        HttpEntity<RegisterDeviceRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        if (response.hasCode(VALIDATION_ERROR)) {
            //device already exists
            log.debug("Ecobee response code was 7 (validation error). Message: \"" + response.getStatus().getMessage()
                      + "\". Assuming device already exists.");
        } else if(!response.getSuccess()) {
            String message = "Error registering device with ecobee. (Code "  + response.getStatus().getCode() + ") " 
                             + response.getStatus().getMessage();
            log.info(message);
            throw new EcobeeCommunicationException(message);
        };
    }
    
    @Override
    public void deleteDevice(String serialNumber) {
        log.debug("Deleting ecobee device with serial number " + serialNumber);
        
        String url = getUrlBase() + modifyThermostatUrlPart;
        
        UnregisterDeviceRequest request = new UnregisterDeviceRequest(serialNumber);
        HttpEntity<UnregisterDeviceRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());
        
        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);
        
        if (response.hasCode(NOT_AUTHORIZED)) {
            //ecobee status codes are crap - "not authorized" usually means the device doesn't exist in ecobee.
            //It should therefore be safe to delete the Yukon device...probably.
        } else if (!response.getSuccess()) {
            //If we get any other error response, the operation failed and we need to roll back.
            int statusCode = response.getStatus().getCode();
            String message = response.getStatus().getMessage();
            log.debug("Unregister device failed with status code: " + statusCode + ". Message: \""  + message + "\".");
            throw new EcobeeCommunicationException("Unable to unregister device. Ecobee status code: " + statusCode 
                                                   + ". Message: \""  + message + "\".");
        }
    }
    
    @Override
    public boolean moveDeviceToSet(String serialNumber, String setPath) 
            throws EcobeeDeviceDoesNotExistException, EcobeeSetDoesNotExistException {
        
        log.debug("Moving ecobee device with serial number " + serialNumber + " to set " + setPath);
        
        boolean success = false;
        try {
            success = attemptMoveDeviceToSet(serialNumber, setPath);
        } catch (EcobeeSetDoesNotExistException e) {
            log.debug("The specified set does not exist. Attempting to create it.");
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
            log.debug("Ecobee response code was 2 (not authorized). Message: \"" + response.getStatus().getMessage()
                      + "\".");
            throw new EcobeeSetDoesNotExistException(setPath);
        } else if (response.hasCode(PROCESSING_ERROR)) {
            log.debug("Ecobee response code was 3 (processing error). Message: \"" + response.getStatus().getMessage()
                      + "\".");
            throw new EcobeeDeviceDoesNotExistException(serialNumber);
        }

        return response.getSuccess();
    }

    @Override
    public List<EcobeeDeviceReadings> readDeviceData(Collection<String> serialNumbers, Range<Instant> dateRange) {
        log.trace("Reading ecobee devices: " + serialNumbers + " Date range: " + dateRange);
        
        MutableDateTime mutableStartDate = new MutableDateTime(dateRange.getMin());
        mutableStartDate.setMillisOfSecond(0);
        mutableStartDate.setSecondOfMinute(0);
        int minuteOfHour = mutableStartDate.getMinuteOfHour();
        mutableStartDate.setMinuteOfHour(minuteOfHour / 5 * 5);
        Instant requestStartDate = mutableStartDate.toInstant();
        Instant requestStopDate = dateRange.getMax();

        String url = getUrlBase() + runtimeReportUrlPart + "&body={bodyJson}";
        RuntimeReportRequest request =
                new RuntimeReportRequest(requestStartDate, requestStopDate, serialNumbers, deviceReadColumns);

        DeviceDataResponse response = queryEcobeeGet(url, new HttpEntity<>(new HttpHeaders()), request,
                                              EcobeeQueryType.DATA_COLLECTION, DeviceDataResponse.class);

        Set<String> missingSerialNumbers = new HashSet<>(serialNumbers);
        if (response.getReportList() != null) {
            Iterables.removeAll(missingSerialNumbers, 
                                Lists.transform(response.getReportList(), RuntimeReport.TO_SERIAL_NUMBER));
        }

        for (String missingSerialNumber : missingSerialNumbers) {
            log.error("Unable to read Serial number: " + missingSerialNumber 
                      + ". It may have been removed from ecobee's system.");
        }

        List<EcobeeDeviceReadings> deviceData = new ArrayList<>();
        if (response.getReportList() != null) {
            for (RuntimeReport runtimeReport : response.getReportList()) {
                List<RuntimeReportRow> sortedReports = new ArrayList<>(runtimeReport.getRuntimeReports());
                Collections.sort(sortedReports, RuntimeReportRow.ON_THERMOSTAT_TIME);

                LocalDateTime reportStartDate = sortedReports.get(0).getThermostatTime();
                Period offsetPeriod = new Period(requestStartDate, reportStartDate.toDateTime(DateTimeZone.UTC));
                DateTimeZone thermostatDateTime = 
                        DateTimeZone.forOffsetHoursMinutes(offsetPeriod.getHours(), offsetPeriod.getMinutes());

                List<EcobeeDeviceReading> readings = new ArrayList<>();
                for (RuntimeReportRow reportRow : sortedReports) {
                    Instant date = reportRow.getThermostatTime().toDateTime(thermostatDateTime).toInstant();
                    EcobeeDeviceReading reading = new EcobeeDeviceReading(reportRow.getOutdoorTemp(),
                        reportRow.getIndoorTemp(), reportRow.getCoolSetPoint(), reportRow.getHeatSetPoint(),
                        reportRow.getRuntime(), reportRow.getEventName(), date);
                    readings.add(reading);
                }
                deviceData.add(new EcobeeDeviceReadings(runtimeReport.getThermostatIdentifier(), dateRange, readings));
            }
        }
        return deviceData;
    }

    @Override
    public boolean createManagementSet(String managementSetName) {
        log.debug("Creating ecobee management set: " + managementSetName);
        
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
    public boolean deleteManagementSet(String managementSetName) throws EcobeeSetDoesNotExistException {
        log.debug("Deleting ecobee management set: " + managementSetName);
        
        String url = getUrlBase() + modifySetUrlPart;

        DeleteSetRequest request = new DeleteSetRequest(managementSetName);
        HttpEntity<DeleteSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        log.info("Deleting set " + managementSetName + " URL: " + url);
        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);
        
        // Set doesn't exist, or we don't have permission
        if (response.hasCode(NOT_AUTHORIZED)) {
            log.debug("Ecobee response code was 2 (not authorized). Message: \"" + response.getStatus().getMessage()
                      + "\".");
            throw new EcobeeSetDoesNotExistException(managementSetName);
        }
        
        return response.getSuccess();
    }

    @Override
    public boolean moveManagementSet(String currentPath, String newParentPath) {
        log.debug("Moving ecobee management set: " + currentPath + " New path: " + newParentPath);
        
        String url = getUrlBase() + modifySetUrlPart;

        MoveSetRequest request = new MoveSetRequest(currentPath, newParentPath);
        HttpEntity<MoveSetRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        StandardResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.SYSTEM, StandardResponse.class);

        return response.getSuccess();
    }

    @Override
    public String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters) {
        log.debug("Sending ecobee duty cycle DR.");
        
        String url = getUrlBase() + demandResponseUrlPart;
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String eventDisplayMessage = messageSourceAccessor.getMessage("yukon.web.modules.dr.ecobee.eventDisplayMessage");
        
        String groupIdString = Integer.toString(parameters.getGroupId());
        DutyCycleDrRequest request = new DutyCycleDrRequest(groupIdString, YUKON_CYCLE_EVENT_NAME, eventDisplayMessage,
                    parameters.getDutyCyclePercent(), parameters.getStartTime(), parameters.isRampIn(),
                    parameters.getEndTime(), parameters.isRampOut(), parameters.isOptional(),
                    settingDao.getBoolean(GlobalSettingType.ECOBEE_SEND_NOTIFICATIONS));

        HttpEntity<DutyCycleDrRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());
        
        if (log.isDebugEnabled()) {
            try {
                log.debug("Sending ecobee duty cycle DR:");
                log.debug("Headers: " + requestEntity.getHeaders());
                log.debug("Body: " + JsonUtils.toJson(request));
            } catch (JsonProcessingException e) {
                log.warn("Error parsing json in debug.", e);
            }
        }
        DrResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, DrResponse.class);

        return response.getDemandResponseRef();
    }

    @Override
    public void sendOverrideControl(String serialNumber) {
        log.debug("Sending ecobee override event to " + serialNumber);
        
        String url = getUrlBase() + demandResponseUrlPart;
        
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String displayMessage = messageSourceAccessor.getMessage("yukon.web.modules.dr.ecobee.overrideDisplayMessage");
        
        Selection selection = new Selection(SelectionType.THERMOSTATS, serialNumber);
        
        int utcOffset = DateTimeZone.getDefault().getOffset(Instant.now());
        Instant startTime = Instant.now().plus(utcOffset);
        Instant endTime = startTime.plus(Duration.standardMinutes(5));
        
        // if isOptional = true: enables ecobee opt-out event message to be voluntary otherwise mandatory.
        DutyCycleDr dr = new DutyCycleDr(YUKON_OVERRIDE_EVENT_NAME, displayMessage, 100, startTime, false, endTime, 
                                         false, true, settingDao.getBoolean(GlobalSettingType.ECOBEE_SEND_NOTIFICATIONS));
        DutyCycleDrRequest request = new DutyCycleDrRequest(selection, dr);
        
        HttpEntity<DutyCycleDrRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());
        DrResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, DrResponse.class);
        
        if (log.isDebugEnabled()) {
            log.debug("Override event response - code: " + response.getStatus().getCode() + ", message: " +
                      response.getStatus().getMessage());
        }
    }

    @Override
    public boolean sendRestore(String drIdentifier) {
        log.debug("Sending ecobee DR restore. Event ID: " + drIdentifier);
        
        String url = getUrlBase() + demandResponseUrlPart;

        DrRestoreRequest request = new DrRestoreRequest(drIdentifier);
        HttpEntity<DrRestoreRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        BaseResponse response = queryEcobee(url, requestEntity, EcobeeQueryType.DEMAND_RESPONSE, BaseResponse.class);

        return response.hasCode(SUCCESS);
    }

    @Override
    public List<SetNode> getHierarchy() {
        log.debug("Retrieving ecobee management set hierarchy.");
        
        //Create base url
        String url = getUrlBase() + modifySetUrlPart + "&body={bodyJson}";

        ListHierarchyRequest request = new ListHierarchyRequest();
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());

        HierarchyResponse response = queryEcobeeGet(url, requestEntity, request, EcobeeQueryType.SYSTEM,
                                         HierarchyResponse.class);

        return response.getSets();
    }

    private <E extends BaseResponse> E queryEcobeeGet(String url, HttpEntity<?> requestEntity, Object request,
        EcobeeQueryType queryType, Class<E> responseType) {

        ecobeeQueryCountDao.incrementQueryCount(queryType);
        try {
            String requestJson = JsonUtils.toJson(request);
            log.debug("Body json: " + requestJson);
            log.debug("Full URL: " + url + requestJson);
            log.debug("Headers: " + requestEntity.getHeaders());
            ResponseEntity<E> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType,
                                                   Collections.singletonMap("bodyJson", requestJson));
            E response = responseEntity.getBody();
            log.debug("Ecobee status code: " + response.getStatus().getCode());
            log.debug("Ecobee status message: " + response.getStatus().getMessage());
            return response;
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

    @Override
    public RuntimeReportJobResponse createRuntimeReportJob(SelectionType selectionType,
            Collection<String> selectionMatch, Range<Instant> dateRange) {
        RuntimeReportJobRequest request = new RuntimeReportJobRequest(dateRange.getMin(), dateRange.getMax(),
            selectionMatch, selectionType, deviceReadColumns);
        if (log.isDebugEnabled()) {
            try {
                String requestJson = JsonUtils.toJson(request);
                log.debug("Request Body json: " + requestJson);
            } catch (JsonProcessingException e) {
                log.warn("Error while parsing json in debug.", e);
            }
        }

        String url = getUrlBase() + createRuntimeReportJobUrlPart;
        HttpEntity<RuntimeReportJobRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        log.debug("Sending request to create a new runtime report job.");
        RuntimeReportJobResponse response =
            queryEcobee(url, requestEntity, EcobeeQueryType.DATA_COLLECTION, RuntimeReportJobResponse.class);

        log.debug("Runtime report job has been created. JobId: " + response.getJobId());
        return response;
    }

    @Override
    public List<EcobeeDeviceReadings> downloadRuntimeReport(List<String> dataUrls) {
        List<EcobeeDeviceReadings> ecobeeDeviceReadings = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        for (String url : dataUrls) {
            try {
                String decryptedFileName = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
                log.debug("Encrypted file : " + url.substring(url.lastIndexOf('/') + 1, url.length())
                    + "received for Job ID : " + decryptedFileName.substring(0, decryptedFileName.indexOf("-")));
                HttpURLConnection connection = getHttpURLConnection(url);

                try (BufferedInputStream gpgInputStream = new BufferedInputStream(connection.getInputStream())) {
                    byte byteArray[] = ecobeeSecurityService.decryptEcobeeFile(gpgInputStream);
                    File tarGzfile = File.createTempFile(decryptedFileName, StringUtils.EMPTY,
                        new File(CtiUtilities.getImportArchiveDirPath()));
                    tarGzfile.deleteOnExit();
                    FileUtils.writeByteArrayToFile(tarGzfile, byteArray);
                    List<File> csvFiles = FileUtil.untar(FileUtil.ungzip(tarGzfile));
                    EcobeeDeviceReadings deviceReadings = null;

                    for (File file : csvFiles) {
                        try (FileInputStream inputStream = new FileInputStream(file);
                             BOMInputStream bomInputStream =
                                 new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16LE,
                                     ByteOrderMark.UTF_16BE, ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE);
                             InputStreamReader inputStreamReader = new InputStreamReader(bomInputStream)) {
                            CSVReader csvReader = new CSVReader(inputStreamReader);
                            Iterator<String[]> iterator = csvReader.readAll().iterator();
                            Map<String, Integer> headerMap = getHeaderIndex(iterator.next());
                            List<EcobeeDeviceReading> readings = new ArrayList<EcobeeDeviceReading>();
                            String serialNumber = file.getName().substring(0, file.getName().indexOf('-'));

                            log.debug("Received runtime reports for Thermostat : " + serialNumber + "in file name"
                                + file.getName().substring(0, file.getName().indexOf(".csv") + 4));

                            while (iterator.hasNext()) {
                                String[] thermostatData = iterator.next();
                                Instant date = new DateTime(formatter.parseDateTime(
                                    thermostatData[0] + StringUtils.SPACE + thermostatData[1])).toInstant();
                                String eventActivity = thermostatData[headerMap.get("zoneCalendarEvent")];
                                Float indoorTempInF = StringUtils.isEmpty(thermostatData[headerMap.get("zoneAveTemp")])
                                    ? null : Float.parseFloat(thermostatData[headerMap.get("zoneAveTemp")]);
                                Float outdoorTempInF = StringUtils.isEmpty(thermostatData[headerMap.get("outdoorTemp")])
                                    ? null : Float.parseFloat(thermostatData[headerMap.get("outdoorTemp")]);
                                Float setCoolTempInF =
                                    StringUtils.isEmpty(thermostatData[headerMap.get("zoneCoolTemp")]) ? null
                                        : Float.parseFloat(thermostatData[headerMap.get("zoneCoolTemp")]);
                                Float setHeatTempInF =
                                    StringUtils.isEmpty(thermostatData[headerMap.get("zoneHeatTemp")]) ? null
                                        : Float.parseFloat(thermostatData[headerMap.get("zoneHeatTemp")]);
                                Integer setCompCool = StringUtils.isEmpty(thermostatData[headerMap.get("compCool1")])
                                    ? null : Integer.parseInt(thermostatData[headerMap.get("compCool1")]);
                                Integer setCompHeat = StringUtils.isEmpty(thermostatData[headerMap.get("compHeat1")])
                                    ? null : Integer.parseInt(thermostatData[headerMap.get("compHeat1")]);
                                Integer runtime;
                                // Add the values if they're both non-null
                                if (setCompCool != null && setCompHeat != null) {
                                    runtime = setCompCool + setCompHeat;
                                    // If only one is non-null, use that value. Otherwise return null.
                                } else if (setCompCool == null) {
                                    runtime = setCompHeat;
                                } else {
                                    runtime = setCompCool;
                                }
                                EcobeeDeviceReading deviceReading = new EcobeeDeviceReading(outdoorTempInF,
                                    indoorTempInF, setCoolTempInF, setHeatTempInF, runtime, eventActivity, date);
                                readings.add(deviceReading);
                            }

                            deviceReadings = new EcobeeDeviceReadings(serialNumber, null, readings);
                            CtiUtilities.close(iterator, csvReader);
                            file.delete();
                        }
                        ecobeeDeviceReadings.add(deviceReadings);
                    }

                } catch (Exception e) {
                    log.error("Error while processing runtimereport data.", e);
                    throw new EcobeeCommunicationException("Error occured while processing runtimereport data.");
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                log.error("Unable to connect with proxy server or URL is not correct", e);
                throw new EcobeeCommunicationException("Unable to connect with proxy server or URL is not correct");
            }
        }
        return ecobeeDeviceReadings;
    }

    private Map<String, Integer> getHeaderIndex(String[] headers) {
        Map<String, Integer> headerMap = new HashMap<String, Integer>();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            if (deviceReadColumns.contains(header)) {
                headerMap.put(header, i);
            }
        }
        return headerMap;
    }

    private HttpURLConnection getHttpURLConnection(String url) throws Exception {
        Optional<YukonHttpProxy> proxy = YukonHttpProxy.fromGlobalSetting(settingDao);
        HttpURLConnection urlConnection = null;
        try {
            URL connectionUrl = new URL(url);
            if (proxy.isPresent()) {
                urlConnection = (HttpURLConnection) connectionUrl.openConnection(proxy.get().getJavaHttpProxy());
            } else {
                urlConnection = (HttpURLConnection) connectionUrl.openConnection();
            }
            urlConnection.connect();
        } catch (Exception e) {
            log.error("Unable to connect with proxy server or URL is not correct", e);
            throw new Exception("Unable to connect with proxy server or URL is not correct");
        }
        return urlConnection;
    }
}
