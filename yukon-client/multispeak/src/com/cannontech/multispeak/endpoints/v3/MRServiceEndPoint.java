package com.cannontech.multispeak.endpoints.v3;

import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.*;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MR_Server;

/*
 * This class is the MR Service endpoint all requests will be processed from
 * here.
 */

@Endpoint
@RequestMapping("/soap/MR_ServerSoap")
public class MRServiceEndPoint {

    @Autowired private MR_Server mr_server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingURL() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        mr_server.pingURL();
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = mr_server.getMethods();
        
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "InitiateDemandReset", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateDemandResetResponse initiateDemandReset(@RequestPayload InitiateDemandReset initiateDemandReset)
            throws MultispeakWebServiceException {
        InitiateDemandResetResponse response = objectFactory.createInitiateDemandResetResponse();

        Float expirationTime = initiateDemandReset.getExpirationTime();
        String responseURL = initiateDemandReset.getResponseURL();
        String transactionId = initiateDemandReset.getTransactionID();
        List<MeterIdentifier> meterIdentifiers =
                (null != initiateDemandReset.getMeterIDs()) ? initiateDemandReset.getMeterIDs().getMeterIdentifier() : null;
        List<ErrorObject> errorObjects = mr_server.initiateDemandReset(meterIdentifiers, responseURL, transactionId, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateDemandResetResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetAMRSupportedMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAMRSupportedMetersResponse getAMRSupportedMeters(@RequestPayload GetAMRSupportedMeters getAMRSupportedMeters)
            throws MultispeakWebServiceException {
        GetAMRSupportedMetersResponse response = objectFactory.createGetAMRSupportedMetersResponse();

        String lastReceived = getAMRSupportedMeters.getLastReceived();
        List<Meter> meters = mr_server.getAMRSupportedMeters(lastReceived);
        
        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().addAll(meters);
        response.setGetAMRSupportedMetersResult(arrayOfMeter);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestReadingByMeterNoAndType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingByMeterNoAndTypeResponse getLatestReadingByMeterNoAndType(
            @RequestPayload GetLatestReadingByMeterNoAndType getLatestReadingByMeterNoAndType)
            throws MultispeakWebServiceException {
        GetLatestReadingByMeterNoAndTypeResponse response =
            objectFactory.createGetLatestReadingByMeterNoAndTypeResponse();

        String meterNo = getLatestReadingByMeterNoAndType.getMeterNo();
        String readingType = getLatestReadingByMeterNoAndType.getReadingType();
        String formattedBlockTemplateName = getLatestReadingByMeterNoAndType.getFormattedBlockTemplateName();
        FormattedBlock formattedBlock =
            mr_server.getLatestReadingByMeterNoAndType(meterNo, readingType, formattedBlockTemplateName);
        response.setGetLatestReadingByMeterNoAndTypeResult(formattedBlock);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByDateAndType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByDateAndTypeResponse getReadingsByDateAndType(
            @RequestPayload GetReadingsByDateAndType getReadingsByDateAndType) throws MultispeakWebServiceException {
        GetReadingsByDateAndTypeResponse response = objectFactory.createGetReadingsByDateAndTypeResponse();

        String readingType = getReadingsByDateAndType.getReadingType();
        String lastReceived = getReadingsByDateAndType.getLastReceived();
        String formattedBlockTemplateName = getReadingsByDateAndType.getFormattedBlockTemplateName();
        XMLGregorianCalendar startDate = getReadingsByDateAndType.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByDateAndType.getEndDate();

        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }
        List<FormattedBlock> formattedBlocks = mr_server.getReadingsByDateAndType(startDate.toGregorianCalendar(),
                                                                                  endDate.toGregorianCalendar(),
                                                                                  readingType,
                                                                                  lastReceived, formattedBlockTemplateName);
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        response.setGetReadingsByDateAndTypeResult(arrayOfFormattedBlock);
        return response;
    }

    @PayloadRoot(localPart = "MeterAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterAddNotificationResponse meterAddNotification(@RequestPayload MeterAddNotification meterAddNotification)
            throws MultispeakWebServiceException {
        MeterAddNotificationResponse response = objectFactory.createMeterAddNotificationResponse();

        List<Meter> meters =
            (null != meterAddNotification.getAddedMeters()) ? meterAddNotification.getAddedMeters().getMeter() : null;
         List<ErrorObject> errorObjects = mr_server.meterAddNotification(meters);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setMeterAddNotificationResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "InitiateUsageMonitoring", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateUsageMonitoringResponse initiateUsageMonitoring(
            @RequestPayload InitiateUsageMonitoring initiateUsageMonitoring) throws MultispeakWebServiceException {
        InitiateUsageMonitoringResponse response = objectFactory.createInitiateUsageMonitoringResponse();

        List<String> meterNumbers =
            (null != initiateUsageMonitoring.getMeterNos()) ? initiateUsageMonitoring.getMeterNos().getString() : null;
        List<ErrorObject> errorObjects = mr_server.initiateUsageMonitoring(meterNumbers);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateUsageMonitoringResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "CancelDisconnectedStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelDisconnectedStatusResponse cancelDisconnectedStatus(
            @RequestPayload CancelDisconnectedStatus cancelDisconnectedStatus) throws MultispeakWebServiceException {
        CancelDisconnectedStatusResponse response = objectFactory.createCancelDisconnectedStatusResponse();

        List<String> meterNumbers =
                (null != cancelDisconnectedStatus.getMeterNos()) ? cancelDisconnectedStatus.getMeterNos().getString() : null;
        List<ErrorObject> errorObjects = mr_server.cancelDisconnectedStatus(meterNumbers);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setCancelDisconnectedStatusResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "EstablishMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishMeterGroupResponse establishMeterGroup(@RequestPayload EstablishMeterGroup establishMeterGroup)
            throws MultispeakWebServiceException {
        EstablishMeterGroupResponse response = objectFactory.createEstablishMeterGroupResponse();

        MeterGroup meterGroup = establishMeterGroup.getMeterGroup();
        List<ErrorObject> errorObjects = mr_server.establishMeterGroup(meterGroup);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setEstablishMeterGroupResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "InitiateMeterReadByMeterNoAndType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateMeterReadByMeterNoAndTypeResponse initiateMeterReadByMeterNoAndType(
            @RequestPayload InitiateMeterReadByMeterNoAndType initiateMeterReadByMeterNoAndType)
            throws MultispeakWebServiceException {
        InitiateMeterReadByMeterNoAndTypeResponse response =
            objectFactory.createInitiateMeterReadByMeterNoAndTypeResponse();

        Float expirationTime = initiateMeterReadByMeterNoAndType.getExpirationTime();
        String responseURL = initiateMeterReadByMeterNoAndType.getResponseURL();
        String transactionId = initiateMeterReadByMeterNoAndType.getTransactionID();
        String meterNo = initiateMeterReadByMeterNoAndType.getMeterNo();
        String readingType = initiateMeterReadByMeterNoAndType.getReadingType();
        List<ErrorObject> errorObjects =
            mr_server.initiateMeterReadByMeterNoAndType(meterNo, responseURL, readingType, transactionId, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateMeterReadByMeterNoAndTypeResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByDate", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByDateResponse getReadingsByDate(@RequestPayload GetReadingsByDate getReadingsByDate)
            throws MultispeakWebServiceException {
        GetReadingsByDateResponse response = objectFactory.createGetReadingsByDateResponse();

        XMLGregorianCalendar startDate = getReadingsByDate.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByDate.getEndDate();
        String lastReceived = getReadingsByDate.getLastReceived();
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<MeterRead> meterReads = mr_server.getReadingsByDate(startDate.toGregorianCalendar(),
                                                                 endDate.toGregorianCalendar(),
                                                                 lastReceived);

        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        arrayOfMeterRead.getMeterRead().addAll(meterReads);
        response.setGetReadingsByDateResult(arrayOfMeterRead);

        return response;
    }

    @PayloadRoot(localPart = "GetLatestReadings", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingsResponse getLatestReadings(@RequestPayload GetLatestReadings getLatestReadings)
            throws MultispeakWebServiceException {
        GetLatestReadingsResponse response = objectFactory.createGetLatestReadingsResponse();
        String lastRecd = getLatestReadings.getLastReceived();
        List<MeterRead> meterReads = mr_server.getLatestReadings(lastRecd);
        
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        arrayOfMeterRead.getMeterRead().addAll(meterReads);
        response.setGetLatestReadingsResult(arrayOfMeterRead);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByMeterNoResponse getReadingsByMeterNo(@RequestPayload GetReadingsByMeterNo getReadingsByMeterNo)
            throws MultispeakWebServiceException {
        GetReadingsByMeterNoResponse response = objectFactory.createGetReadingsByMeterNoResponse();

        XMLGregorianCalendar startDate = getReadingsByMeterNo.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByMeterNo.getEndDate();
        String meterNo = getReadingsByMeterNo.getMeterNo();
        
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }
        List<MeterRead> meterReads = mr_server.getReadingsByMeterNo(meterNo,
                                                                    startDate.toGregorianCalendar(),
                                                                    endDate.toGregorianCalendar());
        
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        arrayOfMeterRead.getMeterRead().addAll(meterReads);
        response.setGetReadingsByMeterNoResult(arrayOfMeterRead);
        return response;
    }

    @PayloadRoot(localPart = "GetSupportedReadingTypes", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSupportedReadingTypesResponse getSupportedReadingTypes() throws MultispeakWebServiceException {
        GetSupportedReadingTypesResponse response = objectFactory.createGetSupportedReadingTypesResponse();

        Set<String> readingTypes = mr_server.getSupportedReadingTypes();
        
        MRArrayOfString2 supportedReadingTypes = objectFactory.createMRArrayOfString2();
        supportedReadingTypes.getString().addAll(readingTypes);
        response.setGetSupportedReadingTypesResult(supportedReadingTypes);
        return response;
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceException {
        MeterChangedNotificationResponse response = objectFactory.createMeterChangedNotificationResponse();
        List<Meter> meters =
            (null != meterChangedNotification.getChangedMeters())
                ? meterChangedNotification.getChangedMeters().getMeter() : null;

        List<ErrorObject> errorObjects = mr_server.meterChangedNotification(meters);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setMeterChangedNotificationResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "InitiateDisconnectedStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateDisconnectedStatusResponse initiateDisconnectedStatus(
            @RequestPayload InitiateDisconnectedStatus initiateDisconnectedStatus) throws MultispeakWebServiceException {
        InitiateDisconnectedStatusResponse response = objectFactory.createInitiateDisconnectedStatusResponse();

        List<String> meterNumbers =
            (null != initiateDisconnectedStatus.getMeterNos()) ? initiateDisconnectedStatus.getMeterNos().getString()
                : null;

        List<ErrorObject> errorObjects = mr_server.initiateDisconnectedStatus(meterNumbers);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateDisconnectedStatusResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "DeleteMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DeleteMeterGroupResponse deleteMeterGroup(@RequestPayload DeleteMeterGroup deleteMeterGroup)
            throws MultispeakWebServiceException {
        DeleteMeterGroupResponse response = objectFactory.createDeleteMeterGroupResponse();

        String meterGroupIds = deleteMeterGroup.getMeterGroupID();
        ErrorObject errorObject = mr_server.deleteMeterGroup(meterGroupIds);
        response.setDeleteMeterGroupResult(errorObject);
        return response;
    }

    @PayloadRoot(localPart = "RemoveMetersFromMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(
            @RequestPayload RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceException {
        RemoveMetersFromMeterGroupResponse response = objectFactory.createRemoveMetersFromMeterGroupResponse();

        String meterGroupIds = removeMetersFromMeterGroup.getMeterGroupID();
        List<String> meterNumbers =
            (null != removeMetersFromMeterGroup.getMeterNumbers())
                ? removeMetersFromMeterGroup.getMeterNumbers().getString() : null;
        List<ErrorObject> errorObjects = mr_server.removeMetersFromMeterGroup(meterNumbers, meterGroupIds);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setRemoveMetersFromMeterGroupResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "IsAMRMeter", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    IsAMRMeterResponse isAMRMeter(@RequestPayload IsAMRMeter isAMRMeter) throws MultispeakWebServiceException {
        IsAMRMeterResponse isAMRMeterResponse = objectFactory.createIsAMRMeterResponse();

        String meterNo = isAMRMeter.getMeterNo();
        boolean response = mr_server.isAMRMeter(meterNo);
        isAMRMeterResponse.setIsAMRMeterResult(response);
        return isAMRMeterResponse;
    }

    @PayloadRoot(localPart = "CancelUsageMonitoring", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelUsageMonitoringResponse cancelUsageMonitoring(@RequestPayload CancelUsageMonitoring cancelUsageMonitoring)
            throws MultispeakWebServiceException {
        CancelUsageMonitoringResponse cancelUsageMonitoringResponse =
            objectFactory.createCancelUsageMonitoringResponse();

        List<String> meterNumbers =
            (null != cancelUsageMonitoring.getMeterNos()) ? cancelUsageMonitoring.getMeterNos().getString() : null;
        List<ErrorObject> errorObjects = mr_server.cancelUsageMonitoring(meterNumbers);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        cancelUsageMonitoringResponse.setCancelUsageMonitoringResult(arrayOfErrorObject);
        return cancelUsageMonitoringResponse;
    }

    @PayloadRoot(localPart = "MeterRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterRemoveNotificationResponse meterRemoveNotification(
            @RequestPayload MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceException {
        MeterRemoveNotificationResponse meterRemoveNotificationResponse =
            objectFactory.createMeterRemoveNotificationResponse();

        List<Meter> meters =
            (null != meterRemoveNotification.getRemovedMeters())
                ? meterRemoveNotification.getRemovedMeters().getMeter() : null;
        List<ErrorObject> errorObjects = mr_server.meterRemoveNotification(meters);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterRemoveNotificationResponse.setMeterRemoveNotificationResult(arrayOfErrorObject);
        return meterRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse serviceLocationChangedNotificationResponse =
            objectFactory.createServiceLocationChangedNotificationResponse();

        List<ServiceLocation> serviceLocation =
            (null != serviceLocationChangedNotification.getChangedServiceLocations())
                ? serviceLocationChangedNotification.getChangedServiceLocations().getServiceLocation() : null;
        List<ErrorObject> errorObjects = mr_server.serviceLocationChangedNotification(serviceLocation);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        serviceLocationChangedNotificationResponse.setServiceLocationChangedNotificationResult(arrayOfErrorObject);
        return serviceLocationChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "InsertMeterInMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InsertMeterInMeterGroupResponse insertMeterInMeterGroup(
            @RequestPayload InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceException {
        InsertMeterInMeterGroupResponse insertMeterInMeterGroupResponse =
            objectFactory.createInsertMeterInMeterGroupResponse();

        List<String> meterNumbers =
            (null != insertMeterInMeterGroup.getMeterNumbers()) ? insertMeterInMeterGroup.getMeterNumbers().getString()
                : null;
        String meterGroupID = insertMeterInMeterGroup.getMeterGroupID();
        List<ErrorObject> errorObjects = mr_server.insertMeterInMeterGroup(meterNumbers, meterGroupID);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        insertMeterInMeterGroupResponse.setInsertMeterInMeterGroupResult(arrayOfErrorObject);
        return insertMeterInMeterGroupResponse;
    }

    @PayloadRoot(localPart = "InitiateMeterReadByMeterNumber", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateMeterReadByMeterNumberResponse initiateMeterReadByMeterNumber(
            @RequestPayload InitiateMeterReadByMeterNumber initiateMeterReadByMeterNumber)
            throws MultispeakWebServiceException {
        InitiateMeterReadByMeterNumberResponse initiateMeterReadByMeterNumberResponse =
            objectFactory.createInitiateMeterReadByMeterNumberResponse();
       
        Float expirationTime = initiateMeterReadByMeterNumber.getExpirationTime();
        String responseURL = initiateMeterReadByMeterNumber.getResponseURL();
        String transactionID = initiateMeterReadByMeterNumber.getTransactionID();
        List<String> meterNumbers =
            (null != initiateMeterReadByMeterNumber.getMeterNos())
                ? initiateMeterReadByMeterNumber.getMeterNos().getString() : null;
        List<ErrorObject> errorObjects = mr_server.initiateMeterReadByMeterNumber(meterNumbers, responseURL, transactionID, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateMeterReadByMeterNumberResponse.setInitiateMeterReadByMeterNumberResult(arrayOfErrorObject);
        return initiateMeterReadByMeterNumberResponse;
    }

    @PayloadRoot(localPart = "GetLatestReadingByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingByMeterNoResponse getLatestReadingByMeterNo(
            @RequestPayload GetLatestReadingByMeterNo getLatestReadingByMeterNo) throws MultispeakWebServiceException {
        GetLatestReadingByMeterNoResponse getLatestReadingByMeterNoResponse =
            objectFactory.createGetLatestReadingByMeterNoResponse();

        String meterNo = getLatestReadingByMeterNo.getMeterNo();
        MeterRead meterRead = mr_server.getLatestReadingByMeterNo(meterNo);
        getLatestReadingByMeterNoResponse.setGetLatestReadingByMeterNoResult(meterRead);
        return getLatestReadingByMeterNoResponse;
    }

    @PayloadRoot(localPart = "GetLatestReadingByType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingByTypeResponse getLatestReadingByType(@RequestPayload GetLatestReadingByType getLatestReadingByType)
            throws MultispeakWebServiceException {
        GetLatestReadingByTypeResponse getLatestReadingByTypeResponse =
            objectFactory.createGetLatestReadingByTypeResponse();

        String lastReceived = getLatestReadingByType.getLastReceived();
        String readingType = getLatestReadingByType.getReadingType();
        String formattedBlockTemplateName = getLatestReadingByType.getFormattedBlockTemplateName();
        List<FormattedBlock> formattedBlocks =
            mr_server.getLatestReadingByType(readingType, lastReceived, formattedBlockTemplateName);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getLatestReadingByTypeResponse.setGetLatestReadingByTypeResult(arrayOfFormattedBlock);
        return getLatestReadingByTypeResponse;
    }

    @PayloadRoot(localPart = "GetReadingsByMeterNoAndType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByMeterNoAndTypeResponse getReadingByMeterNoAndType(
            @RequestPayload GetReadingsByMeterNoAndType getReadingByMeterNoAndType)
            throws MultispeakWebServiceException {
        GetReadingsByMeterNoAndTypeResponse getReadingsByMeterNoAndTypeResponse =
            objectFactory.createGetReadingsByMeterNoAndTypeResponse();
        XMLGregorianCalendar endDate = getReadingByMeterNoAndType.getEndDate();
        XMLGregorianCalendar startDate = getReadingByMeterNoAndType.getStartDate();
        String meterNo = getReadingByMeterNoAndType.getMeterNo();
        String formattedBlockTemplateName = getReadingByMeterNoAndType.getFormattedBlockTemplateName();
        String readingType = getReadingByMeterNoAndType.getReadingType();
        String lastReceived = getReadingByMeterNoAndType.getLastReceived();
        
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<FormattedBlock> formattedBlocks = mr_server.getReadingsByMeterNoAndType(meterNo,
                                                                                     startDate.toGregorianCalendar(),
                                                                                     endDate.toGregorianCalendar(),
                                                                                     readingType,
                                                                                     lastReceived, formattedBlockTemplateName);
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getReadingsByMeterNoAndTypeResponse.setGetReadingsByMeterNoAndTypeResult(arrayOfFormattedBlock);
        return getReadingsByMeterNoAndTypeResponse;
    }

    @PayloadRoot(localPart = "CancelPlannedOutage", namespace = MultispeakDefines.NAMESPACE_v3)
    public void cancelPlannedOutage() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void customerChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "CustomersAffectedByOutageNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void customersAffectedByOutageNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DeleteReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public void deleteReadingSchedule() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DeleteSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public void deleteSchedule() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DisableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public void disableReadingSchedule() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void domainMembersChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void domainNamesChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "EnableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public void enableReadingSchedule() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "EndDeviceShipmentNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void endDeviceShipmentNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "EstablishReadingSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public void establishReadingSchedules() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "EstablishSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public void establishSchedules() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getDomainMembers() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getDomainNames() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetFormattedBlockTemplates", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getFormattedBlockTemplates() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetHistoryLogByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getHistoryLogByMeterNo() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetHistoryLogsByDate", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getHistoryLogsByDate() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetHistoryLogsByDateAndEventCode", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getHistoryLogsByDateAndEventCode() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetHistoryLogsByMeterNoAndEventCode", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getHistoryLogsByMeterNoAndEventCode() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLatestMeterReadingsByMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getLatestMeterReadingsByMeterGroup() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLatestReadingsByMeterNoList", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getLatestReadingsByMeterNoList() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLatestReadingsByMeterNoListFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getLatestReadingsByMeterNoListFormattedBlock() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetModifiedAMRMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getModifiedAMRMeters() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getPublishMethods() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingByMeterNumberFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getReadingByMeterNumberFormattedBlock() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingsByDateFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getReadingsByDateFormattedBlock() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingsByUOMAndDate", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getReadingsByUOMAndDate() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingScheduleByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getReadingScheduleByID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getReadingSchedules() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getRegistrationInfoByID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetScheduleByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getScheduleByID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSchedules() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InHomeDisplayAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void inHomeDisplayAddNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InHomeDisplayChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void inHomeDisplayChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InHomeDisplayExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void inHomeDisplayExchangeNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InHomeDisplayRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void inHomeDisplayRemoveNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InHomeDisplayRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void inHomeDisplayRetireNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateGroupMeterRead", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateGroupMeterRead() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateMeterReadByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateMeterReadByObject() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateMeterReadsByFieldName", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateMeterReadsByFieldName() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiatePlannedOutage", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiatePlannedOutage() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InsertMetersInConfigurationGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public void insertMetersInConfigurationGroup() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterBaseAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterBaseAddNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterBaseChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterBaseChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterBaseExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterBaseExchangeNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterBaseRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterBaseRemoveNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterConnectivityNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterConnectivityNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterExchangeNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterRetireNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public void registerForService() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RemoveMetersFromConfigurationGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public void removeMetersFromConfigurationGroup() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void requestRegistrationID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ScheduleGroupMeterRead", namespace = MultispeakDefines.NAMESPACE_v3)
    public void ScheduleGroupMeterRead() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public void unregisterForService() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UpdateServiceLocationDisplays", namespace = MultispeakDefines.NAMESPACE_v3)
    public void updateServiceLocationDisplays() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterBaseRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterBaseRetireNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetReadingsByBillingCycle", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetReadingsByBillingCycleResponse getReadingsByBillingCycle(
            @RequestPayload GetReadingsByBillingCycle getReadingsByBillingCycle)
            throws MultispeakWebServiceException {
        GetReadingsByBillingCycleResponse getReadingsByBillingCycleResponse = objectFactory.createGetReadingsByBillingCycleResponse();

        XMLGregorianCalendar billingDate = getReadingsByBillingCycle.getBillingDate();
        int kWLookBack = getReadingsByBillingCycle.getKWLookBack();
        String lastReceived = getReadingsByBillingCycle.getLastReceived();
        int kWhLookBack = getReadingsByBillingCycle.getKWhLookBack();
        String billingCycle = getReadingsByBillingCycle.getBillingCycle();
        String formattedBlockTemplateName = getReadingsByBillingCycle.getFormattedBlockTemplateName();
        int kWLookForward = getReadingsByBillingCycle.getKWLookForward();
        
        if (billingDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<FormattedBlock> formattedBlocks = mr_server.getReadingsByBillingCycle(billingCycle,
                                                                                   billingDate.toGregorianCalendar(),
                                                                                   kWhLookBack,
                                                                                   kWLookBack,
                                                                                   kWLookForward,
                                                                                   lastReceived, formattedBlockTemplateName);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        formattedBlockList.addAll(formattedBlocks);
        getReadingsByBillingCycleResponse.setGetReadingsByBillingCycleResult(arrayOfFormattedBlock);
        return getReadingsByBillingCycleResponse;
    }
}