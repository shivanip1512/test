package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.NOT_AUTHORIZED;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.PROCESSING_ERROR;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.SUCCESS;
import static com.cannontech.dr.ecobee.service.EcobeeStatusCode.VALIDATION_ERROR;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
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
import com.cannontech.dr.ecobee.message.DrResponse;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.DutyCycleDrRequest;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.ListHierarchyRequest;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.EcobeeReportJob;
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusResponse;
import com.cannontech.dr.ecobee.message.SetpointDrRequest;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.UnregisterDeviceRequest;
import com.cannontech.dr.ecobee.message.partial.DutyCycleDr;
import com.cannontech.dr.ecobee.message.partial.Selection;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeSetpointDrParameters;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeStatusCode;
import com.cannontech.encryption.EcobeeSecurityService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.fasterxml.jackson.core.JsonProcessingException;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeCommunicationServiceImpl.class);

    private static final int MINUTES_TO_WAIT_TO_START_POLLING = 5;

    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired private @Qualifier("ecobee") RestOperations restTemplate;
    @Autowired private GlobalSettingDao settingDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private EcobeeSecurityService ecobeeSecurityService;
    @Autowired private EcobeeCommunicationServiceHelper ecobeeCommunicationServiceHelper;

    private static final String modifySetUrlPart = "hierarchy/set?format=json";
    private static final String modifyThermostatUrlPart = "hierarchy/thermostat?format=json";
    private static final String demandResponseUrlPart = "demandResponse?format=json";
    private static final String createRuntimeReportJobUrlPart = "runtimeReportJob/create";
    private static final String getRuntimeReportJobStatusUrlPart = "runtimeReportJob/status?format=json";

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
    public String sendSetpointDR(EcobeeSetpointDrParameters parameters) {
        log.debug("Sending ecobee setpoint DR.");

        String url = getUrlBase() + demandResponseUrlPart;

        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(YukonUserContext.system);
        String eventDisplayMessage = messageSourceAccessor.getMessage("yukon.web.modules.dr.ecobee.eventDisplayMessage");

        String groupIdString = Integer.toString(parameters.getGroupId());
        // Required to be set to true to use RelativeTemp
        boolean isTemperatureRelative = true;
        // According to the API documentation both the cool and heat relative temp must be set in the message;
        int coolRelativeTemp = 0;
        int heatRelativeTemp = 0;
        if (parameters.istempOptionHeat()) {
            heatRelativeTemp = parameters.getTempOffset();
        } else {
            coolRelativeTemp = parameters.getTempOffset();
        }
        SetpointDrRequest request = new SetpointDrRequest(groupIdString, YUKON_CYCLE_EVENT_NAME, eventDisplayMessage,
                parameters.getStartTime(), parameters.getStopTime(), parameters.isOptional(), isTemperatureRelative,
                coolRelativeTemp, heatRelativeTemp, settingDao.getBoolean(GlobalSettingType.ECOBEE_SEND_NOTIFICATIONS));

        HttpEntity<SetpointDrRequest> requestEntity = new HttpEntity<>(request, new HttpHeaders());

        if (log.isDebugEnabled()) {
            try {
                log.debug("Sending ecobee setpoint DR:");
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
    public synchronized List<EcobeeDeviceReadings> readDeviceData(SelectionType selectionType, Collection<String> selectionMatch,
            Range<LocalDate> dateRange) {

        List<EcobeeDeviceReadings> deviceReadings = new ArrayList<>();
        try {
            RuntimeReportJobResponse response = createRuntimeReportJob(selectionType, selectionMatch, dateRange);
            if (response.getJobId() != null) {
                EcobeeReportJob reportJob = pollForJobCompletion(response.getJobId()).get();

                if (reportJob != null) {
                    if (reportJob.getStatus() == EcobeeJobStatus.COMPLETED) {
                        List<String> dataUrls = Arrays.asList(reportJob.getFiles());
                        deviceReadings = downloadRuntimeReport(dataUrls);
                        if (SelectionType.THERMOSTATS == selectionType) {
                            ecobeeCommunicationServiceHelper.logMissingSerialNumber(deviceReadings, selectionMatch);
                        }
                    }

                    if (reportJob.getStatus() == EcobeeJobStatus.ERROR) {
                        throw new EcobeeCommunicationException("Received error : " + reportJob.getMessage()
                            + " in response with jobId " + reportJob.getJobId());
                    }
                }
            } else {
                throw new EcobeeCommunicationException("Received error message : " + response.getStatus().getMessage()
                    + " with code " + response.getStatus().getCode() + " in response while creating job.");
            }

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error while processing Runtime Report Job. ", e);
            throw new EcobeeCommunicationException("Error while processing Runtime Report Job.");
        }

        return deviceReadings;

    }

    /**
     * Creates a new runtime report job to be processed and return response object containing jobId.
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     */
    private RuntimeReportJobResponse createRuntimeReportJob(SelectionType selectionType,
            Collection<String> selectionMatch, Range<LocalDate> dateRange) {
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

        log.info("Runtime report job has been created. JobId: " + response.getJobId());

        return response;
    }

    /**
     * Retrieve thermostats data for the specified URLs.These URLs are specific to a job.
     * @throws EcobeeCommunicationException if anything goes wrong.
     */
    private List<EcobeeDeviceReadings> downloadRuntimeReport(List<String> dataUrls) {
        List<EcobeeDeviceReadings> ecobeeDeviceReadings = new ArrayList<>();
        for (String url : dataUrls) {
            try {
                String decryptedFileName = ecobeeCommunicationServiceHelper.getDecryptedFileName(url);
                URLConnection connection = YukonHttpProxy.getURLConnection(url, settingDao);

                try (BufferedInputStream gpgInputStream = new BufferedInputStream(connection.getInputStream())) {
                    byte byteArray[] = ecobeeSecurityService.decryptEcobeeFile(gpgInputStream);
                    File tarGzfile = File.createTempFile(decryptedFileName, StringUtils.EMPTY, new File(CtiUtilities.getImportArchiveDirPath()));
                    tarGzfile.deleteOnExit();
                    FileUtils.writeByteArrayToFile(tarGzfile, byteArray);
                    List<File> csvFiles = FileUtil.untar(FileUtil.ungzip(tarGzfile));
                    ecobeeDeviceReadings = ecobeeCommunicationServiceHelper.getEcobeeDeviceReadings(csvFiles);
                } finally {
                    if (connection instanceof HttpURLConnection) {
                        ((HttpURLConnection) connection).disconnect();
                    }
                }
            } catch (Exception e) {
                log.error("Unable to connect with proxy server or URL is not correct", e);
                throw new EcobeeCommunicationException("Unable to connect with proxy server or URL is not correct");
            }
        }
        return ecobeeDeviceReadings;
    }

    /**
     * Polling Job completion based on jobId in every 5 min.
     * Provide 2 min initial delay for executor
     */
    
    private CompletableFuture<EcobeeReportJob> pollForJobCompletion(String jobId) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        CompletableFuture<EcobeeReportJob> completionFuture = new CompletableFuture<>();
        final ScheduledFuture<?> checkFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                RuntimeReportJobStatusResponse jobStatusResponse = getRuntimeReportJobStatus(jobId);

                if (jobStatusResponse.getStatus().getCode() != EcobeeStatusCode.SUCCESS.getCode()) {
                    completionFuture.completeExceptionally(
                        new EcobeeCommunicationException("Received error message : " + jobStatusResponse.getStatus().getMessage()
                            + " with code " + jobStatusResponse.getStatus().getCode()
                            + " in response while polling for runtime report job."));
                }

                if (jobStatusResponse != null && CollectionUtils.isNotEmpty(jobStatusResponse.getJobs())) {
                    EcobeeReportJob reportJob = jobStatusResponse.getJobs().get(0);
                    if (reportJob != null) {
                        log.info("Response : RunTime Report Job received for JobId: " + jobId + " with status: "
                            + reportJob.getStatus().getEcobeeStatusString());
                        if (reportJob.getStatus() == EcobeeJobStatus.COMPLETED || reportJob.getStatus() == EcobeeJobStatus.ERROR) {
                            completionFuture.complete(reportJob);
                        }
                    }
                } else {
                    completionFuture.completeExceptionally(new EcobeeCommunicationException("No job reported in response"));
                }
            } catch (Throwable e) {
                log.error("Received error while polling for runtime report job " + e);
                completionFuture.completeExceptionally(new EcobeeCommunicationException("Received error while polling for runtime report job."));
            }
         // provide 2 min delay to check the status of Job
        }, 2, MINUTES_TO_WAIT_TO_START_POLLING, TimeUnit.MINUTES); 

        completionFuture.whenComplete((result, thrown) -> {
            checkFuture.cancel(true);
            scheduledExecutorService.shutdown();
        });

        return completionFuture;
    }
    
    /**
     * Retrieve job status for based on jobId
     */
    private RuntimeReportJobStatusResponse getRuntimeReportJobStatus(String jobId) {
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());
        String url = getUrlBase() + getRuntimeReportJobStatusUrlPart + "&body={bodyJson}";
        RuntimeReportJobStatusRequest jobStatusRequest = new RuntimeReportJobStatusRequest(jobId);

        if (log.isDebugEnabled()) {
            try {
                String requestJson = JsonUtils.toJson(jobStatusRequest);
                log.debug("Request Body json: " + requestJson);
            } catch (JsonProcessingException e) {
                log.warn("Error while parsing json in debug.", e);
            }
        }
        log.debug("Request : Runtime Report Job status. JobId: " + jobId);
        RuntimeReportJobStatusResponse response = queryEcobeeGet(url, requestEntity, jobStatusRequest,
            EcobeeQueryType.DATA_COLLECTION, RuntimeReportJobStatusResponse.class);
        return response;
    }

}
