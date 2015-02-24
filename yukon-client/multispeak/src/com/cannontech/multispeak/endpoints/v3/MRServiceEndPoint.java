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

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingURL() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        ErrorObject errorObject = mr_server.pingURL();
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().add(errorObject);
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
        ArrayOfMeterIdentifier arrayOfMeterIdentifier = initiateDemandReset.getMeterIDs();
        List<MeterIdentifier> meterIdentifiers = arrayOfMeterIdentifier.getMeterIdentifier();
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
        List<String> fieldNames = getLatestReadingByMeterNoAndType.getFieldName().getString();

        FormattedBlock formattedBlock =
            mr_server.getLatestReadingByMeterNoAndType(meterNo, readingType, formattedBlockTemplateName, fieldNames);
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
        List<String> fieldNames = getReadingsByDateAndType.getFieldName().getString();
        List<FormattedBlock> formattedBlocks =
            mr_server.getReadingsByDateAndType((startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                formattedBlockTemplateName, fieldNames);
        
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

        List<Meter> meters = meterAddNotification.getAddedMeters().getMeter();
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

        List<String> meterNumbers = initiateUsageMonitoring.getMeterNos().getString();
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

        List<String> meterNumbers = cancelDisconnectedStatus.getMeterNos().getString();
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
            mr_server.initiateMeterReadByMeterNoAndType(meterNo, responseURL, readingType, transactionId,
                expirationTime);
        
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
        List<MeterRead> meterReads =
            mr_server.getReadingsByDate((startDate != null) ? startDate.toGregorianCalendar() : null, (endDate != null)
                ? endDate.toGregorianCalendar() : null, lastReceived);

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
        List<MeterRead> meterReads =
            mr_server.getReadingsByMeterNo(meterNo, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        arrayOfMeterRead.getMeterRead().addAll(meterReads);
        response.setGetReadingsByMeterNoResult(arrayOfMeterRead);
        return response;
    }

    @PayloadRoot(localPart = "GetSupportedReadingTypes", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSupportedReadingTypesResponse getSupportedReadingTypes(
            @RequestPayload GetSupportedReadingTypes getSupportedReadingTypes) throws MultispeakWebServiceException {
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
        List<Meter> meters = meterChangedNotification.getChangedMeters().getMeter();
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

        List<String> meterNumbers = initiateDisconnectedStatus.getMeterNos().getString();
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
        List<String> meterNumbers = removeMetersFromMeterGroup.getMeterNumbers().getString();
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

        List<String> meterNumbers = cancelUsageMonitoring.getMeterNos().getString();
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

        List<Meter> meters = meterRemoveNotification.getRemovedMeters().getMeter();
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

        List<ServiceLocation> serviceLocation = serviceLocationChangedNotification.getChangedServiceLocations().getServiceLocation();
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

        List<String> meterNumbers = insertMeterInMeterGroup.getMeterNumbers().getString();
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
        List<String> meterNumbers = initiateMeterReadByMeterNumber.getMeterNos().getString();
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

        List<String> fieldNames = getLatestReadingByType.getFieldName().getString();
        String lastReceived = getLatestReadingByType.getLastReceived();
        String readingType = getLatestReadingByType.getReadingType();
        String formattedBlockTemplateName = getLatestReadingByType.getFormattedBlockTemplateName();
        List<FormattedBlock> formattedBlocks =
            mr_server.getLatestReadingByType(readingType, lastReceived, formattedBlockTemplateName, fieldNames);

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
        List<String> fieldName = getReadingByMeterNoAndType.getFieldName().getString();
        List<FormattedBlock> formattedBlocks =
            mr_server.getReadingsByMeterNoAndType(meterNo,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                formattedBlockTemplateName, fieldName);
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getReadingsByMeterNoAndTypeResponse.setGetReadingsByMeterNoAndTypeResult(arrayOfFormattedBlock);
        return getReadingsByMeterNoAndTypeResponse;
    }

    @PayloadRoot(localPart = "CancelPlannedOutage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelPlannedOutageResponse cancelPlannedOutage(@RequestPayload CancelPlannedOutage cancelPlannedOutage)
            throws MultispeakWebServiceException {
        CancelPlannedOutageResponse cancelPlannedOutageResponse = objectFactory.createCancelPlannedOutageResponse();

        List<String> meterNumbers = cancelPlannedOutage.getMeterNos().getString();
        List<ErrorObject> errorObjects = mr_server.cancelPlannedOutage(meterNumbers);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        cancelPlannedOutageResponse.setCancelPlannedOutageResult(arrayOfErrorObject);
        return cancelPlannedOutageResponse;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse customerChangedNotificationResponse =
            objectFactory.createCustomerChangedNotificationResponse();

        List<Customer> customers = customerChangedNotification.getChangedCustomers().getCustomer();
        List<ErrorObject> errorObjects = mr_server.customerChangedNotification(customers);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        customerChangedNotificationResponse.setCustomerChangedNotificationResult(arrayOfErrorObject);
        return customerChangedNotificationResponse;

    }

    @PayloadRoot(localPart = "CustomersAffectedByOutageNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomersAffectedByOutageNotificationResponse customersAffectedByOutageNotification(
            @RequestPayload CustomersAffectedByOutageNotification customersAffectedByOutageNotification)
            throws MultispeakWebServiceException {
        CustomersAffectedByOutageNotificationResponse customersAffectedByOutageNotificationResponse =
            objectFactory.createCustomersAffectedByOutageNotificationResponse();

        List<CustomersAffectedByOutage> customersAffectedByOutages =
                customersAffectedByOutageNotification.getNewOutages().getCustomersAffectedByOutage();

        List<ErrorObject> errorObjects = mr_server.customersAffectedByOutageNotification(customersAffectedByOutages);
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        customersAffectedByOutageNotificationResponse.setCustomersAffectedByOutageNotificationResult(arrayOfErrorObject);
        return customersAffectedByOutageNotificationResponse;
    }

    @PayloadRoot(localPart = "DeleteReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DeleteReadingScheduleResponse deleteReadingSchedule(@RequestPayload DeleteReadingSchedule deleteReadingSchedule)
            throws MultispeakWebServiceException {
        DeleteReadingScheduleResponse deleteReadingScheduleResponse =
            objectFactory.createDeleteReadingScheduleResponse();

        String readingScheduleID = deleteReadingSchedule.getReadingScheduleID();
        List<ErrorObject> errorObjects = mr_server.deleteReadingSchedule(readingScheduleID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        deleteReadingScheduleResponse.setDeleteReadingScheduleResult(arrayOfErrorObject);
        return deleteReadingScheduleResponse;
    }

    @PayloadRoot(localPart = "DeleteSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DeleteScheduleResponse deleteSchedule(@RequestPayload DeleteSchedule deleteSchedule)
            throws MultispeakWebServiceException {
        DeleteScheduleResponse deleteScheduleResponse = objectFactory.createDeleteScheduleResponse();

        String scheduleID = deleteSchedule.getScheduleID();
        List<ErrorObject> errorObjects = mr_server.deleteSchedule(scheduleID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        deleteScheduleResponse.setDeleteScheduleResult(arrayOfErrorObject);
        return deleteScheduleResponse;
    }

    @PayloadRoot(localPart = "DisableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DisableReadingScheduleResponse disableReadingSchedule(@RequestPayload DisableReadingSchedule disableReadingSchedule)
            throws MultispeakWebServiceException {
        DisableReadingScheduleResponse disableReadingScheduleResponse =
            objectFactory.createDisableReadingScheduleResponse();

        String readingScheduleID = disableReadingSchedule.getReadingScheduleID();
        List<ErrorObject> errorObjects = mr_server.disableReadingSchedule(readingScheduleID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        disableReadingScheduleResponse.setDisableReadingScheduleResult(arrayOfErrorObject);
        return disableReadingScheduleResponse;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse domainMembersChangedNotificationResponse =
            objectFactory.createDomainMembersChangedNotificationResponse();

        List<DomainMember> domainMembers = domainMembersChangedNotification.getChangedDomainMembers().getDomainMember();

        List<ErrorObject> errorObjects = mr_server.domainMembersChangedNotification(domainMembers);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        domainMembersChangedNotificationResponse.setDomainMembersChangedNotificationResult(arrayOfErrorObject);
        return domainMembersChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse domainNamesChangedNotificationResponse =
            objectFactory.createDomainNamesChangedNotificationResponse();

        List<DomainNameChange> domainNameChanges = domainNamesChangedNotification.getChangedDomainNames().getDomainNameChange();
        List<ErrorObject> errorObjects = mr_server.domainNamesChangedNotification(domainNameChanges);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        domainNamesChangedNotificationResponse.setDomainNamesChangedNotificationResult(arrayOfErrorObject);
        return domainNamesChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "EnableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EnableReadingScheduleResponse enableReadingSchedule(@RequestPayload EnableReadingSchedule enableReadingSchedule)
            throws MultispeakWebServiceException {
        EnableReadingScheduleResponse enableReadingScheduleResponse =
            objectFactory.createEnableReadingScheduleResponse();

        String readingScheduleID = enableReadingSchedule.getReadingScheduleID();
        List<ErrorObject> errorObjects = mr_server.enableReadingSchedule(readingScheduleID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        enableReadingScheduleResponse.setEnableReadingScheduleResult(arrayOfErrorObject);
        return enableReadingScheduleResponse;
    }

    @PayloadRoot(localPart = "EndDeviceShipmentNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EndDeviceShipmentNotificationResponse endDeviceShipmentNotification(
            @RequestPayload EndDeviceShipmentNotification endDeviceShipmentNotification)
            throws MultispeakWebServiceException {
        EndDeviceShipmentNotificationResponse endDeviceShipmentNotificationResponse =
            objectFactory.createEndDeviceShipmentNotificationResponse();

        EndDeviceShipment shipment = endDeviceShipmentNotification.getShipment();
        List<ErrorObject> errorObjects = mr_server.endDeviceShipmentNotification(shipment);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        endDeviceShipmentNotificationResponse.setEndDeviceShipmentNotificationResult(arrayOfErrorObject);
        return endDeviceShipmentNotificationResponse;
    }

    @PayloadRoot(localPart = "EstablishReadingSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishReadingSchedulesResponse establishReadingSchedules(
            @RequestPayload EstablishReadingSchedules establishReadingSchedules) throws MultispeakWebServiceException {
        EstablishReadingSchedulesResponse establishReadingSchedulesResponse =
            objectFactory.createEstablishReadingSchedulesResponse();

        List<ReadingSchedule> readingSchedules = establishReadingSchedules.getReadingSchedules().getReadingSchedule();
        List<ErrorObject> errorObjects = mr_server.establishReadingSchedules(readingSchedules);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        establishReadingSchedulesResponse.setEstablishReadingSchedulesResult(arrayOfErrorObject);
        return establishReadingSchedulesResponse;
    }

    @PayloadRoot(localPart = "EstablishSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishSchedulesResponse establishSchedules(@RequestPayload EstablishSchedules establishSchedules)
            throws MultispeakWebServiceException {
        EstablishSchedulesResponse establishSchedulesResponse = objectFactory.createEstablishSchedulesResponse();

        List<Schedule> schedules = establishSchedules.getSchedules().getSchedule();
        List<ErrorObject> errorObjects = mr_server.establishSchedules(schedules);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        establishSchedulesResponse.setEstablishSchedulesResult(arrayOfErrorObject);
        return establishSchedulesResponse;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse getDomainMembersResponse = objectFactory.createGetDomainMembersResponse();

        List<DomainMember> domainMembers = mr_server.getDomainMembers(getDomainMembers.getDomainName());
        
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        arrayOfDomainMember.getDomainMember().addAll(domainMembers);
        getDomainMembersResponse.setGetDomainMembersResult(arrayOfDomainMember);
        return getDomainMembersResponse;

    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames(@RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        GetDomainNamesResponse getDomainNamesResponse = objectFactory.createGetDomainNamesResponse();
        List<String> domainNames = mr_server.getDomainNames();
        
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(domainNames);
        getDomainNamesResponse.setGetDomainNamesResult(arrayOfString);
        return getDomainNamesResponse;

    }

    @PayloadRoot(localPart = "GetFormattedBlockTemplates", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetFormattedBlockTemplatesResponse getFormattedBlockTemplates(
            @RequestPayload GetFormattedBlockTemplates getFormattedBlockTemplates) throws MultispeakWebServiceException {
        GetFormattedBlockTemplatesResponse getFormattedBlockTemplatesResponse =
            objectFactory.createGetFormattedBlockTemplatesResponse();
        String lastReceived = getFormattedBlockTemplates.getLastReceived();
        List<FormattedBlockTemplate> formattedBlockTemplates = mr_server.getFormattedBlockTemplates(lastReceived);
        
        ArrayOfFormattedBlockTemplate arrayOfFormattedBlockTemplate = objectFactory.createArrayOfFormattedBlockTemplate();
        arrayOfFormattedBlockTemplate.getFormattedBlockTemplate().addAll(formattedBlockTemplates);
        getFormattedBlockTemplatesResponse.setGetFormattedBlockTemplatesResult(arrayOfFormattedBlockTemplate);
        return getFormattedBlockTemplatesResponse;

    }

    @PayloadRoot(localPart = "GetHistoryLogByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetHistoryLogByMeterNoResponse getHistoryLogByMeterNo(@RequestPayload GetHistoryLogByMeterNo getHistoryLogByMeterNo)
            throws MultispeakWebServiceException {
        GetHistoryLogByMeterNoResponse getHistoryLogByMeterNoResponse =
            objectFactory.createGetHistoryLogByMeterNoResponse();
        XMLGregorianCalendar endDate = getHistoryLogByMeterNo.getEndDate();
        String meterNo = getHistoryLogByMeterNo.getMeterNo();
        XMLGregorianCalendar startDate = getHistoryLogByMeterNo.getStartDate();
        List<HistoryLog> historyLogs =
            mr_server.getHistoryLogByMeterNo(meterNo, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        arrayOfHistoryLog.getHistoryLog().addAll(historyLogs);
        getHistoryLogByMeterNoResponse.setGetHistoryLogByMeterNoResult(arrayOfHistoryLog);
        return getHistoryLogByMeterNoResponse;

    }

    @PayloadRoot(localPart = "GetHistoryLogsByDate", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetHistoryLogsByDateResponse getHistoryLogsByDate(@RequestPayload GetHistoryLogsByDate getHistoryLogsByDate)
            throws MultispeakWebServiceException {
        GetHistoryLogsByDateResponse getHistoryLogsByDateResponse = objectFactory.createGetHistoryLogsByDateResponse();

        XMLGregorianCalendar endDate = getHistoryLogsByDate.getEndDate();
        XMLGregorianCalendar startDate = getHistoryLogsByDate.getStartDate();
        String lastReceived = getHistoryLogsByDate.getLastReceived();
        List<HistoryLog> historyLogs =
            mr_server.getHistoryLogsByDate((startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);
        
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        arrayOfHistoryLog.getHistoryLog().addAll(historyLogs);
        getHistoryLogsByDateResponse.setGetHistoryLogsByDateResult(arrayOfHistoryLog);
        return getHistoryLogsByDateResponse;

    }

    @PayloadRoot(localPart = "GetHistoryLogsByDateAndEventCode", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetHistoryLogsByDateAndEventCodeResponse getHistoryLogsByDateAndEventCode(
            @RequestPayload GetHistoryLogsByDateAndEventCode getHistoryLogsByDateAndEventCode)
            throws MultispeakWebServiceException {
        GetHistoryLogsByDateAndEventCodeResponse getHistoryLogsByDateAndEventCodeResponse =
            objectFactory.createGetHistoryLogsByDateAndEventCodeResponse();

        XMLGregorianCalendar endDate = getHistoryLogsByDateAndEventCode.getEndDate();
        XMLGregorianCalendar startDate = getHistoryLogsByDateAndEventCode.getStartDate();
        String lastReceived = getHistoryLogsByDateAndEventCode.getLastReceived();
        EventCode eventCode = getHistoryLogsByDateAndEventCode.getEventCode();

        List<HistoryLog> historyLogs =
            mr_server.getHistoryLogsByDateAndEventCode(eventCode, (startDate != null) ? startDate.toGregorianCalendar()
                : null, (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);
        
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        arrayOfHistoryLog.getHistoryLog().addAll(historyLogs);
        getHistoryLogsByDateAndEventCodeResponse.setGetHistoryLogsByDateAndEventCodeResult(arrayOfHistoryLog);
        return getHistoryLogsByDateAndEventCodeResponse;
    }

    @PayloadRoot(localPart = "GetHistoryLogsByMeterNoAndEventCode", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetHistoryLogsByMeterNoAndEventCodeResponse getHistoryLogsByMeterNoAndEventCode(
            @RequestPayload GetHistoryLogsByMeterNoAndEventCode getHistoryLogsByMeterNoAndEventCode)
            throws MultispeakWebServiceException {
        GetHistoryLogsByMeterNoAndEventCodeResponse getHistoryLogsByMeterNoAndEventCodeResponse =
            objectFactory.createGetHistoryLogsByMeterNoAndEventCodeResponse();

        XMLGregorianCalendar endDate = getHistoryLogsByMeterNoAndEventCode.getEndDate();
        String meterNo = getHistoryLogsByMeterNoAndEventCode.getMeterNo();
        EventCode eventCode = getHistoryLogsByMeterNoAndEventCode.getEventCode();
        XMLGregorianCalendar startDate = getHistoryLogsByMeterNoAndEventCode.getStartDate();
        List<HistoryLog> historyLogs =
            mr_server.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        arrayOfHistoryLog.getHistoryLog().addAll(historyLogs);
        getHistoryLogsByMeterNoAndEventCodeResponse.setGetHistoryLogsByMeterNoAndEventCodeResult(arrayOfHistoryLog);
        return getHistoryLogsByMeterNoAndEventCodeResponse;
    }

    @PayloadRoot(localPart = "GetLatestMeterReadingsByMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestMeterReadingsByMeterGroupResponse getLatestMeterReadingsByMeterGroup(
            @RequestPayload GetLatestMeterReadingsByMeterGroup getLatestMeterReadingsByMeterGroup)
            throws MultispeakWebServiceException {
        GetLatestMeterReadingsByMeterGroupResponse getLatestMeterReadingsByMeterGroupResponse =
            objectFactory.createGetLatestMeterReadingsByMeterGroupResponse();

        String formattedBlockTemplateName = getLatestMeterReadingsByMeterGroup.getFormattedBlockTemplateName();
        String meterGroupID = getLatestMeterReadingsByMeterGroup.getMeterGroupID();
        List<String> fieldNames = getLatestMeterReadingsByMeterGroup.getFieldName().getString();
        FormattedBlock formattedBlock =
            mr_server.getLatestMeterReadingsByMeterGroup(meterGroupID, formattedBlockTemplateName, fieldNames);
        getLatestMeterReadingsByMeterGroupResponse.setGetLatestMeterReadingsByMeterGroupResult(formattedBlock);
        return getLatestMeterReadingsByMeterGroupResponse;
    }

    @PayloadRoot(localPart = "GetLatestReadingsByMeterNoList", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingsByMeterNoListResponse getLatestReadingsByMeterNoList(
            @RequestPayload GetLatestReadingsByMeterNoList getLatestReadingsByMeterNoList)
            throws MultispeakWebServiceException {
        GetLatestReadingsByMeterNoListResponse getLatestMeterReadingsByMeterGroupResponse =
            objectFactory.createGetLatestReadingsByMeterNoListResponse();

        String formattedBlockTemplateName = getLatestReadingsByMeterNoList.getFormattedBlockTemplateName();
        XMLGregorianCalendar startDate = getLatestReadingsByMeterNoList.getStartDate();
        String lastReceived = getLatestReadingsByMeterNoList.getLastReceived();
        MRArrayOfString2 meterNumbers = getLatestReadingsByMeterNoList.getMeterNo();
        ServiceType serviceType = getLatestReadingsByMeterNoList.getServiceType();
        XMLGregorianCalendar endDate = getLatestReadingsByMeterNoList.getEndDate();
        MRArrayOfString2 fieldNames = getLatestReadingsByMeterNoList.getFieldName();
        String readingType = getLatestReadingsByMeterNoList.getReadingType();

        List<FormattedBlock> formattedBlocks =
            mr_server.getLatestReadingsByMeterNoList(meterNumbers.getString(), (startDate != null) ? startDate.toGregorianCalendar()
                : null, (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                serviceType, formattedBlockTemplateName, fieldNames.getString());
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getLatestMeterReadingsByMeterGroupResponse.setGetLatestReadingsByMeterNoListResult(arrayOfFormattedBlock);
        return getLatestMeterReadingsByMeterGroupResponse;
    }

    @PayloadRoot(localPart = "GetLatestReadingsByMeterNoListFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingsByMeterNoListFormattedBlockResponse getLatestReadingsByMeterNoListFormattedBlock(
            @RequestPayload GetLatestReadingsByMeterNoListFormattedBlock getLatestReadingsByMeterNoListFormattedBlock)
            throws MultispeakWebServiceException {
        GetLatestReadingsByMeterNoListFormattedBlockResponse getLatestReadingsByMeterNoListFormattedBlockResponse =
            objectFactory.createGetLatestReadingsByMeterNoListFormattedBlockResponse();

        List<String> fieldNames = getLatestReadingsByMeterNoListFormattedBlock.getFieldName().getString();
        String lastReceived = getLatestReadingsByMeterNoListFormattedBlock.getLastReceived();
        List<String> meterNumbers = getLatestReadingsByMeterNoListFormattedBlock.getMeterNo().getString();
        String formattedBlockTemplateName = getLatestReadingsByMeterNoListFormattedBlock.getFormattedBlockTemplateName();
        XMLGregorianCalendar endDate = getLatestReadingsByMeterNoListFormattedBlock.getEndDate();
        ServiceType serviceType = getLatestReadingsByMeterNoListFormattedBlock.getServiceType();
        XMLGregorianCalendar startDate = getLatestReadingsByMeterNoListFormattedBlock.getEndDate();
        List<FormattedBlock> formattedBlocks =
            mr_server.getLatestReadingsByMeterNoListFormattedBlock(meterNumbers,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, formattedBlockTemplateName, fieldNames,
                lastReceived, serviceType);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getLatestReadingsByMeterNoListFormattedBlockResponse.setGetLatestReadingsByMeterNoListFormattedBlockResult(arrayOfFormattedBlock);
        return getLatestReadingsByMeterNoListFormattedBlockResponse;

    }

    @PayloadRoot(localPart = "GetModifiedAMRMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetModifiedAMRMetersResponse getModifiedAMRMeters(@RequestPayload GetModifiedAMRMeters getModifiedAMRMeters)
            throws MultispeakWebServiceException {
        GetModifiedAMRMetersResponse getModifiedAMRMetersResponse = objectFactory.createGetModifiedAMRMetersResponse();

        String lastReceived = getModifiedAMRMeters.getLastReceived();
        String previousSessionID = getModifiedAMRMeters.getPreviousSessionID();
        List<Meter> meters = mr_server.getModifiedAMRMeters(previousSessionID, lastReceived);

        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().addAll(meters);
        getModifiedAMRMetersResponse.setGetModifiedAMRMetersResult(arrayOfMeter);
        return getModifiedAMRMetersResponse;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods(@RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        GetPublishMethodsResponse getPublishMethodsResponse = objectFactory.createGetPublishMethodsResponse();

        List<String> publishMethods = mr_server.getPublishMethods();
        
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(publishMethods);
        getPublishMethodsResponse.setGetPublishMethodsResult(arrayOfString);
        return getPublishMethodsResponse;
    }

    @PayloadRoot(localPart = "GetReadingByMeterNumberFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingByMeterNumberFormattedBlockResponse getReadingByMeterNumberFormattedBlock(
            @RequestPayload GetReadingByMeterNumberFormattedBlock getReadingByMeterNumberFormattedBlock)
            throws MultispeakWebServiceException {
        GetReadingByMeterNumberFormattedBlockResponse getReadingByMeterNumberFormattedBlockResponse =
            objectFactory.createGetReadingByMeterNumberFormattedBlockResponse();

        int kWLookForward = getReadingByMeterNumberFormattedBlock.getKWLookForward();
        String meterNumber = getReadingByMeterNumberFormattedBlock.getMeterNumber();
        List<String> fieldNames = getReadingByMeterNumberFormattedBlock.getFieldName().getString();

        String formattedBlockTemplateName = getReadingByMeterNumberFormattedBlock.getFormattedBlockTemplateName();
        int kWLookBack = getReadingByMeterNumberFormattedBlock.getKWhLookBack();
        XMLGregorianCalendar billingDate = getReadingByMeterNumberFormattedBlock.getBillingDate();
        int kWhLookBack = getReadingByMeterNumberFormattedBlock.getKWhLookBack();
        String lastReceived = getReadingByMeterNumberFormattedBlock.getLastReceived();
        List<FormattedBlock> formattedBlocks =
            mr_server.getReadingByMeterNumberFormattedBlock(meterNumber,
                (billingDate != null) ? billingDate.toGregorianCalendar() : null, kWhLookBack, kWLookBack,
                kWLookForward, lastReceived, formattedBlockTemplateName, fieldNames);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getReadingByMeterNumberFormattedBlockResponse.setGetReadingByMeterNumberFormattedBlockResult(arrayOfFormattedBlock);
        return getReadingByMeterNumberFormattedBlockResponse;
    }

    @PayloadRoot(localPart = "GetReadingsByDateFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByDateFormattedBlockResponse getReadingsByDateFormattedBlock(
            @RequestPayload GetReadingsByDateFormattedBlock getReadingsByDateFormattedBlock)
            throws MultispeakWebServiceException {
        GetReadingsByDateFormattedBlockResponse getReadingsByDateFormattedBlockResponse =
            objectFactory.createGetReadingsByDateFormattedBlockResponse();

        int kWLookForward = getReadingsByDateFormattedBlock.getKWLookForward();
        XMLGregorianCalendar billingDate = getReadingsByDateFormattedBlock.getBillingDate();
        int kWLookBack = getReadingsByDateFormattedBlock.getKWLookBack();
        List<String> fieldNames = getReadingsByDateFormattedBlock.getFieldName().getString();
        String formattedBlockTemplateName = getReadingsByDateFormattedBlock.getFormattedBlockTemplateName();
        String lastReceived = getReadingsByDateFormattedBlock.getLastReceived();
        int kWhLookBack = getReadingsByDateFormattedBlock.getKWhLookBack();
        List<FormattedBlock> formattedBlocks =
            mr_server.getReadingsByDateFormattedBlock((billingDate != null) ? billingDate.toGregorianCalendar() : null,
                kWhLookBack, kWLookBack, kWLookForward, lastReceived, formattedBlockTemplateName, fieldNames);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getReadingsByDateFormattedBlockResponse.setGetReadingsByDateFormattedBlockResult(arrayOfFormattedBlock);
        return getReadingsByDateFormattedBlockResponse;
    }

    @PayloadRoot(localPart = "GetReadingsByUOMAndDate", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByUOMAndDateResponse getReadingsByUOMAndDate(
            @RequestPayload GetReadingsByUOMAndDate getReadingsByUOMAndDate) throws MultispeakWebServiceException {
        GetReadingsByUOMAndDateResponse getReadingsByUOMAndDateResponse =
            objectFactory.createGetReadingsByUOMAndDateResponse();
        XMLGregorianCalendar startDate = getReadingsByUOMAndDate.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByUOMAndDate.getEndDate();
        String lastReceived = getReadingsByUOMAndDate.getLastReceived();
        String uomData = getReadingsByUOMAndDate.getUomData();
        
        List<MeterRead> meterReads =
            mr_server.getReadingsByUOMAndDate(uomData, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);

        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        arrayOfMeterRead.getMeterRead().addAll(meterReads);
        getReadingsByUOMAndDateResponse.setGetReadingsByUOMAndDateResult(arrayOfMeterRead);
        return getReadingsByUOMAndDateResponse;
    }

    @PayloadRoot(localPart = "GetReadingScheduleByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingScheduleByIDResponse getReadingScheduleByID(@RequestPayload GetReadingScheduleByID getReadingScheduleByID)
            throws MultispeakWebServiceException {
        GetReadingScheduleByIDResponse getReadingScheduleByIDResponse =
            objectFactory.createGetReadingScheduleByIDResponse();

        String readingScheduleID = getReadingScheduleByID.getReadingScheduleID();
        ReadingSchedule readingSchedule = mr_server.getReadingScheduleByID(readingScheduleID);
        getReadingScheduleByIDResponse.setGetReadingScheduleByIDResult(readingSchedule);
        return getReadingScheduleByIDResponse;
    }

    @PayloadRoot(localPart = "GetReadingSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingSchedulesResponse getReadingSchedules(@RequestPayload GetReadingSchedules getReadingSchedules) throws MultispeakWebServiceException {
        GetReadingSchedulesResponse getReadingSchedulesResponse = objectFactory.createGetReadingSchedulesResponse();
        
        String lastReceived = getReadingSchedules.getLastReceived();
        List<ReadingSchedule> readingSchedules = mr_server.getReadingSchedules(lastReceived);

        ArrayOfReadingSchedule arrayOfReadingSchedule = objectFactory.createArrayOfReadingSchedule();
        arrayOfReadingSchedule.getReadingSchedule().addAll(readingSchedules);
        getReadingSchedulesResponse.setGetReadingSchedulesResult(arrayOfReadingSchedule);
        return getReadingSchedulesResponse;
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {
        GetRegistrationInfoByIDResponse getRegistrationInfoByIDResponse = objectFactory.createGetRegistrationInfoByIDResponse();
        
        String registrationID = getRegistrationInfoByID.getRegistrationID();
        RegistrationInfo registrationInfo = mr_server.getRegistrationInfoByID(registrationID);
        getRegistrationInfoByIDResponse.setGetRegistrationInfoByIDResult(registrationInfo);
        return getRegistrationInfoByIDResponse;
    }

    @PayloadRoot(localPart = "GetScheduleByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetScheduleByIDResponse getScheduleByID(@RequestPayload GetScheduleByID getScheduleByID)
            throws MultispeakWebServiceException {
        GetScheduleByIDResponse getScheduleByIDResponse = objectFactory.createGetScheduleByIDResponse();
        
        String scheduleID = getScheduleByID.getScheduleID();
        Schedule schedule = mr_server.getScheduleByID(scheduleID);
        getScheduleByIDResponse.setGetScheduleByIDResult(schedule);
        return getScheduleByIDResponse;

    }

    @PayloadRoot(localPart = "GetSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSchedulesResponse getSchedules(@RequestPayload GetSchedules getSchedules) throws MultispeakWebServiceException {
        GetSchedulesResponse getSchedulesResponse = objectFactory.createGetSchedulesResponse();

        String lastReceived = getSchedules.getLastReceived();
        List<Schedule> schedules = mr_server.getSchedules(lastReceived);

        ArrayOfSchedule arrayOfSchedule = objectFactory.createArrayOfSchedule();
        arrayOfSchedule.getSchedule().addAll(schedules);
        getSchedulesResponse.setGetSchedulesResult(arrayOfSchedule);
        return getSchedulesResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayAddNotificationResponse inHomeDisplayAddNotification(
            @RequestPayload InHomeDisplayAddNotification inHomeDisplayAddNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayAddNotificationResponse inHomeDisplayAddNotificationResponse =
                objectFactory.createInHomeDisplayAddNotificationResponse();
        
        List<InHomeDisplay> inHomeDisplays = inHomeDisplayAddNotification.getAddedIHDs().getInHomeDisplay();
        List<ErrorObject> errorObjects = mr_server.inHomeDisplayAddNotification(inHomeDisplays);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        inHomeDisplayAddNotificationResponse.setInHomeDisplayAddNotificationResult(arrayOfErrorObject);
        return inHomeDisplayAddNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayChangedNotificationResponse inHomeDisplayChangedNotification(
            @RequestPayload InHomeDisplayChangedNotification inHomeDisplayChangedNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayChangedNotificationResponse inHomeDisplayChangedNotificationResponse =
                objectFactory.createInHomeDisplayChangedNotificationResponse();
        
        List<InHomeDisplay> inHomeDisplays = inHomeDisplayChangedNotification.getChangedIHDs().getInHomeDisplay();
        List<ErrorObject> errorObjects = mr_server.inHomeDisplayChangedNotification(inHomeDisplays);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        inHomeDisplayChangedNotificationResponse.setInHomeDisplayChangedNotificationResult(arrayOfErrorObject);
        return inHomeDisplayChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayExchangeNotificationResponse inHomeDisplayExchangeNotification(
            @RequestPayload InHomeDisplayExchangeNotification inHomeDisplayExchangeNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayExchangeNotificationResponse inHomeDisplayExchangeNotificationResponse =
                objectFactory.createInHomeDisplayExchangeNotificationResponse();
        
        List<InHomeDisplayExchange> inHomeDisplayExchanges = inHomeDisplayExchangeNotification.getIHDChangeout().getInHomeDisplayExchange();
        List<ErrorObject> errorObjects = mr_server.inHomeDisplayExchangeNotification(inHomeDisplayExchanges);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        inHomeDisplayExchangeNotificationResponse.setInHomeDisplayExchangeNotificationResult(arrayOfErrorObject);
        return inHomeDisplayExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayRemoveNotificationResponse inHomeDisplayRemoveNotification(
            @RequestPayload InHomeDisplayRemoveNotification inHomeDisplayRemoveNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayRemoveNotificationResponse inHomeDisplayRemoveNotificationResponse =
            objectFactory.createInHomeDisplayRemoveNotificationResponse();
        List<InHomeDisplay> inHomeDisplays = inHomeDisplayRemoveNotification.getRemovedIHDs().getInHomeDisplay();
        List<ErrorObject> errorObjects = mr_server.inHomeDisplayRemoveNotification(inHomeDisplays);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        inHomeDisplayRemoveNotificationResponse.setInHomeDisplayRemoveNotificationResult(arrayOfErrorObject);
        return inHomeDisplayRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayRetireNotificationResponse inHomeDisplayRetireNotification(
            @RequestPayload InHomeDisplayRetireNotification inHomeDisplayRetireNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayRetireNotificationResponse inHomeDisplayRetireNotificationResponse =
            objectFactory.createInHomeDisplayRetireNotificationResponse();

        List<InHomeDisplay> inHomeDisplays = inHomeDisplayRetireNotification.getRetiredIHDs().getInHomeDisplay();
        List<ErrorObject> errorObjects = mr_server.inHomeDisplayRetireNotification(inHomeDisplays);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        inHomeDisplayRetireNotificationResponse.setInHomeDisplayRetireNotificationResult(arrayOfErrorObject);
        return inHomeDisplayRetireNotificationResponse;
    }

    @PayloadRoot(localPart = "InitiateGroupMeterRead", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateGroupMeterReadResponse initiateGroupMeterRead(@RequestPayload InitiateGroupMeterRead initiateGroupMeterRead)
            throws MultispeakWebServiceException {

        InitiateGroupMeterReadResponse initiateGroupMeterReadResponse =
            objectFactory.createInitiateGroupMeterReadResponse();
        String responseURL = initiateGroupMeterRead.getResponseURL();
        float expirationTime = initiateGroupMeterRead.getExpirationTime();
        String transactionID = initiateGroupMeterRead.getTransactionID();
        String meterGroupName = initiateGroupMeterRead.getMeterGroupName();
        List<ErrorObject> errorObjects =
            mr_server.initiateGroupMeterRead(meterGroupName, responseURL, transactionID, expirationTime);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateGroupMeterReadResponse.setInitiateGroupMeterReadResult(arrayOfErrorObject);
        return initiateGroupMeterReadResponse;
    }

    @PayloadRoot(localPart = "InitiateMeterReadByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateMeterReadByObjectResponse initiateMeterReadByObject(
            @RequestPayload InitiateMeterReadByObject initiateMeterReadByObject) throws MultispeakWebServiceException {
        InitiateMeterReadByObjectResponse initiateMeterReadByObjectResponse =
            objectFactory.createInitiateMeterReadByObjectResponse();
        PhaseCd phaseCode = initiateMeterReadByObject.getPhaseCode();
        String transactionID = initiateMeterReadByObject.getTransactionID();
        String responseURL = initiateMeterReadByObject.getResponseURL();
        String objectName = initiateMeterReadByObject.getObjectName();
        float expirationTime = initiateMeterReadByObject.getExpirationTime();
        String nounType = initiateMeterReadByObject.getNounType();
        List<ErrorObject> errorObjects =
            mr_server.initiateMeterReadByObject(objectName, nounType, phaseCode, responseURL, transactionID,
                expirationTime);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateMeterReadByObjectResponse.setInitiateMeterReadByObjectResult(arrayOfErrorObject);
        return initiateMeterReadByObjectResponse;
    }

    @PayloadRoot(localPart = "InitiateMeterReadsByFieldName", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateMeterReadsByFieldNameResponse initiateMeterReadsByFieldName(
            @RequestPayload InitiateMeterReadsByFieldName initiateMeterReadsByFieldName)
            throws MultispeakWebServiceException {
        InitiateMeterReadsByFieldNameResponse initiateMeterReadsByFieldNameResponse =
            objectFactory.createInitiateMeterReadsByFieldNameResponse();
        String transactionID = initiateMeterReadsByFieldName.getTransactionID();
        List<String> fieldNames = initiateMeterReadsByFieldName.getFieldNames().getString();
        String responseURL = initiateMeterReadsByFieldName.getResponseURL();
        List<String> meterNumbers = initiateMeterReadsByFieldName.getMeterNumbers().getString();
        String formattedBlockTemplateName = initiateMeterReadsByFieldName.getFormattedBlockTemplateName();
        float expirationTime = initiateMeterReadsByFieldName.getExpirationTime();
        List<ErrorObject> errorObjects =
            mr_server.initiateMeterReadsByFieldName(meterNumbers, fieldNames, responseURL, transactionID,
                expirationTime, formattedBlockTemplateName);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateMeterReadsByFieldNameResponse.setInitiateMeterReadsByFieldNameResult(arrayOfErrorObject);
        return initiateMeterReadsByFieldNameResponse;
    }

    @PayloadRoot(localPart = "InitiatePlannedOutage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiatePlannedOutageResponse initiatePlannedOutage(@RequestPayload InitiatePlannedOutage initiatePlannedOutage)
            throws MultispeakWebServiceException {

        InitiatePlannedOutageResponse initiatePlannedOutageResponse =
            objectFactory.createInitiatePlannedOutageResponse();
        XMLGregorianCalendar startDate = initiatePlannedOutage.getEndDate();
        XMLGregorianCalendar endDate = initiatePlannedOutage.getEndDate();
        List<String> meterNumbers = initiatePlannedOutage.getMeterNos().getString();
        List<ErrorObject> errorObjects =
            mr_server.initiatePlannedOutage(meterNumbers, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiatePlannedOutageResponse.setInitiatePlannedOutageResult(arrayOfErrorObject);
        return initiatePlannedOutageResponse;
    }

    @PayloadRoot(localPart = "InsertMetersInConfigurationGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InsertMetersInConfigurationGroupResponse insertMetersInConfigurationGroup(
            @RequestPayload InsertMetersInConfigurationGroup insertMetersInConfigurationGroup)
            throws MultispeakWebServiceException {

        InsertMetersInConfigurationGroupResponse insertMetersInConfigurationGroupResponse =
            objectFactory.createInsertMetersInConfigurationGroupResponse();
        List<String> meterNumbers = insertMetersInConfigurationGroup.getMeterNumbers().getString();
        ServiceType serviceType = insertMetersInConfigurationGroup.getServiceType();
        String meterGroupID = insertMetersInConfigurationGroup.getMeterGroupID();
        List<ErrorObject> errorObjects =
            mr_server.insertMetersInConfigurationGroup(meterNumbers, meterGroupID, serviceType);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        insertMetersInConfigurationGroupResponse.setInsertMetersInConfigurationGroupResult(arrayOfErrorObject);
        return insertMetersInConfigurationGroupResponse;
    }

    @PayloadRoot(localPart = "MeterBaseAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseAddNotificationResponse meterBaseAddNotification(
            @RequestPayload MeterBaseAddNotification meterBaseAddNotification) throws MultispeakWebServiceException {
        MeterBaseAddNotificationResponse meterBaseAddNotificationResponse =
            objectFactory.createMeterBaseAddNotificationResponse();

        List<MeterBase> meterBases = meterBaseAddNotification.getAddedMBs().getMeterBase();
        List<ErrorObject> errorObjects = mr_server.meterBaseAddNotification(meterBases);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterBaseAddNotificationResponse.setMeterBaseAddNotificationResult(arrayOfErrorObject);
        return meterBaseAddNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseChangedNotificationResponse meterBaseChangedNotification(
            @RequestPayload MeterBaseChangedNotification meterBaseChangedNotification)
            throws MultispeakWebServiceException {
        MeterBaseChangedNotificationResponse meterBaseChangedNotificationResponse =
            objectFactory.createMeterBaseChangedNotificationResponse();
        List<MeterBase> meterBases = meterBaseChangedNotification.getChangedMBs().getMeterBase();
        List<ErrorObject> errorObjects = mr_server.meterBaseChangedNotification(meterBases);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterBaseChangedNotificationResponse.setMeterBaseChangedNotificationResult(arrayOfErrorObject);
        return meterBaseChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseExchangeNotificationResponse meterBaseExchangeNotification(
            @RequestPayload MeterBaseExchangeNotification meterBaseExchangeNotification)
            throws MultispeakWebServiceException {
        MeterBaseExchangeNotificationResponse meterBaseExchangeNotificationResponse =
            objectFactory.createMeterBaseExchangeNotificationResponse();

        List<MeterBaseExchange> meterBaseExchanges = meterBaseExchangeNotification.getMBChangeout().getMeterBaseExchange();
        List<ErrorObject> errorObjects = mr_server.meterBaseExchangeNotification(meterBaseExchanges);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterBaseExchangeNotificationResponse.setMeterBaseExchangeNotificationResult(arrayOfErrorObject);
        return meterBaseExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseRemoveNotificationResponse meterBaseRemoveNotification(
            @RequestPayload MeterBaseRemoveNotification meterBaseRemoveNotification)
            throws MultispeakWebServiceException {
        MeterBaseRemoveNotificationResponse meterBaseRemoveNotificationResponse =
            objectFactory.createMeterBaseRemoveNotificationResponse();

        List<MeterBase> meterBases = meterBaseRemoveNotification.getRemovedMBs().getMeterBase();
        List<ErrorObject> errorObjects = mr_server.meterBaseRemoveNotification(meterBases);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterBaseRemoveNotificationResponse.setMeterBaseRemoveNotificationResult(arrayOfErrorObject);
        return meterBaseRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterConnectivityNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterConnectivityNotificationResponse meterConnectivityNotification(
            @RequestPayload MeterConnectivityNotification meterConnectivityNotification)
            throws MultispeakWebServiceException {
        MeterConnectivityNotificationResponse meterConnectivityNotificationResponse =
            objectFactory.createMeterConnectivityNotificationResponse();
        List<MeterConnectivity> meterConnectivities = meterConnectivityNotification.getNewConnectivity().getMeterConnectivity();
        List<ErrorObject> errorObjects = mr_server.meterConnectivityNotification(meterConnectivities);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterConnectivityNotificationResponse.setMeterConnectivityNotificationResult(arrayOfErrorObject);
        return meterConnectivityNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterExchangeNotificationResponse meterExchangeNotification(
            @RequestPayload MeterExchangeNotification meterExchangeNotification) throws MultispeakWebServiceException {
        MeterExchangeNotificationResponse meterExchangeNotificationResponse =
            objectFactory.createMeterExchangeNotificationResponse();

        List<MeterExchange> meterExchanges = meterExchangeNotification.getMeterChangeout().getMeterExchange();
        List<ErrorObject> errorObjects = mr_server.meterExchangeNotification(meterExchanges);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterExchangeNotificationResponse.setMeterExchangeNotificationResult(arrayOfErrorObject);
        return meterExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterRetireNotificationResponse meterRetireNotification(
            @RequestPayload MeterRetireNotification meterRetireNotification) throws MultispeakWebServiceException {
        MeterRetireNotificationResponse meterRetireNotificationResponse =
            objectFactory.createMeterRetireNotificationResponse();

        List<Meter> meters = meterRetireNotification.getRetiredMeters().getMeter();
        List<ErrorObject> errorObjects = mr_server.meterRetireNotification(meters);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterRetireNotificationResponse.setMeterRetireNotificationResult(arrayOfErrorObject);
        return meterRetireNotificationResponse;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse registerForServiceResponse = objectFactory.createRegisterForServiceResponse();
        RegistrationInfo registrationDetails = registerForService.getRegistrationDetails();
        List<ErrorObject> errorObjects = mr_server.registerForService(registrationDetails);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        registerForServiceResponse.setRegisterForServiceResult(arrayOfErrorObject);
        return registerForServiceResponse;
    }

    @PayloadRoot(localPart = "RemoveMetersFromConfigurationGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RemoveMetersFromConfigurationGroupResponse removeMetersFromConfigurationGroup(
            @RequestPayload RemoveMetersFromConfigurationGroup removeMetersFromConfigurationGroup)
            throws MultispeakWebServiceException {
        RemoveMetersFromConfigurationGroupResponse removeMetersFromConfigurationGroupResponse =
            objectFactory.createRemoveMetersFromConfigurationGroupResponse();
        ServiceType serviceType = removeMetersFromConfigurationGroup.getServiceType();
        List<String> meterNumbers  = removeMetersFromConfigurationGroup.getMeterNumbers().getString();
        String meterGroupID = removeMetersFromConfigurationGroup.getMeterGroupID();
        List<ErrorObject> errorObjects =
            mr_server.removeMetersFromConfigurationGroup(meterNumbers, meterGroupID, serviceType);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        removeMetersFromConfigurationGroupResponse.setRemoveMetersFromConfigurationGroupResult(arrayOfErrorObject);
        return removeMetersFromConfigurationGroupResponse;
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RequestRegistrationIDResponse requestRegistrationID(@RequestPayload RequestRegistrationID requestRegistrationID)
            throws MultispeakWebServiceException {
        RequestRegistrationIDResponse requestRegistrationIDIdResponse =
            objectFactory.createRequestRegistrationIDResponse();

        String registrationID = mr_server.requestRegistrationID();
        requestRegistrationIDIdResponse.setRequestRegistrationIDResult(registrationID);
        return requestRegistrationIDIdResponse;
    }

    @PayloadRoot(localPart = "ScheduleGroupMeterRead", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ScheduleGroupMeterReadResponse ScheduleGroupMeterRead(@RequestPayload ScheduleGroupMeterRead scheduleGroupMeterRead)
            throws MultispeakWebServiceException {
        ScheduleGroupMeterReadResponse scheduleGroupMeterReadResponse =
            objectFactory.createScheduleGroupMeterReadResponse();
        
        String transactionID = scheduleGroupMeterRead.getTransactionID();
        String meterGroupName = scheduleGroupMeterRead.getMeterGroupName();
        String responseURL = scheduleGroupMeterRead.getResponseURL();
        XMLGregorianCalendar timeToRead = scheduleGroupMeterRead.getTimeToRead();
        List<ErrorObject> errorObjects =
            mr_server.scheduleGroupMeterRead(meterGroupName, (timeToRead != null) ? timeToRead.toGregorianCalendar()
                : null, responseURL, transactionID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        scheduleGroupMeterReadResponse.setScheduleGroupMeterReadResult(arrayOfErrorObject);
        return scheduleGroupMeterReadResponse;
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse unregisterForServiceResponse = objectFactory.createUnregisterForServiceResponse();

        String registrationID = unregisterForService.getRegistrationID();
        List<ErrorObject> errorObjects = mr_server.unregisterForService(registrationID);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        unregisterForServiceResponse.setUnregisterForServiceResult(arrayOfErrorObject);
        return unregisterForServiceResponse;
    }

    @PayloadRoot(localPart = "UpdateServiceLocationDisplays", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UpdateServiceLocationDisplaysResponse updateServiceLocationDisplays(
            @RequestPayload UpdateServiceLocationDisplays updateServiceLocationDisplays)
            throws MultispeakWebServiceException {
        UpdateServiceLocationDisplaysResponse updateServiceLocationDisplaysResponse =
            objectFactory.createUpdateServiceLocationDisplaysResponse();

        String servLocID = updateServiceLocationDisplays.getServLocID();
        List<ErrorObject> errorObjects = mr_server.updateServiceLocationDisplays(servLocID);

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        updateServiceLocationDisplaysResponse.setUpdateServiceLocationDisplaysResult(arrayOfErrorObject);
        return updateServiceLocationDisplaysResponse;
    }

    @PayloadRoot(localPart = "MeterBaseRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseRetireNotificationResponse meterBaseRetireNotification(
            @RequestPayload MeterBaseRetireNotification meterBaseRetireNotification)
            throws MultispeakWebServiceException {
        MeterBaseRetireNotificationResponse meterBaseRetireNotificationResponse =
            objectFactory.createMeterBaseRetireNotificationResponse();

        List<MeterBase> meterBases = meterBaseRetireNotification.getRetiredMBs().getMeterBase();
        List<ErrorObject> errorObjects = mr_server.meterBaseRetireNotification(meterBases);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterBaseRetireNotificationResponse.setMeterBaseRetireNotificationResult(arrayOfErrorObject);
        return meterBaseRetireNotificationResponse;
    }

    @PayloadRoot(localPart = "GetReadingsByBillingCycle", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByBillingCycleResponse getReadingsByBillingCycle(
            @RequestPayload GetReadingsByBillingCycle getReadingsByBillingCycle) throws MultispeakWebServiceException {
        GetReadingsByBillingCycleResponse getReadingsByBillingCycleResponse =
            objectFactory.createGetReadingsByBillingCycleResponse();
        
        XMLGregorianCalendar billingDate = getReadingsByBillingCycle.getBillingDate();
        List<String> fieldNames = getReadingsByBillingCycle.getFieldName().getString();
        int kWLookBack = getReadingsByBillingCycle.getKWLookBack();
        String lastReceived = getReadingsByBillingCycle.getLastReceived();
        int kWhLookBack = getReadingsByBillingCycle.getKWhLookBack();
        String billingCycle = getReadingsByBillingCycle.getBillingCycle();
        String formattedBlockTemplateName = getReadingsByBillingCycle.getFormattedBlockTemplateName();
        int kWLookForward = getReadingsByBillingCycle.getKWLookForward();

        List<FormattedBlock> formattedBlocks =
            mr_server.getReadingsByBillingCycle(billingCycle, (billingDate != null) ? billingDate.toGregorianCalendar()
                : null, kWhLookBack, kWLookBack, kWLookForward, lastReceived, formattedBlockTemplateName, fieldNames);
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        formattedBlockList.addAll(formattedBlocks);
        getReadingsByBillingCycleResponse.setGetReadingsByBillingCycleResult(arrayOfFormattedBlock);
        return getReadingsByBillingCycleResponse;
    }
}