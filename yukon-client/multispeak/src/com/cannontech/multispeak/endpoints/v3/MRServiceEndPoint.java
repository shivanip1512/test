package com.cannontech.multispeak.endpoints.v3;

import java.util.List;

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

        ErrorObject[] errorObjects = mr_server.pingURL();
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();

        List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();

        for (ErrorObject errorObject : errorObjects) {
            errorObjList.add(errorObject);
        }
        response.setPingURLResult(arrOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        String[] methods = mr_server.getMethods();
        ArrayOfString stringArray = objectFactory.createArrayOfString();
        List<String> methodNameList = stringArray.getString();

        for (String methodName : methods) {
            methodNameList.add(methodName);
        }
        response.setGetMethodsResult(stringArray);

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
        MeterIdentifier[] meterIDs = meterIdentifiers.toArray(new MeterIdentifier[meterIdentifiers.size()]);
        ErrorObject[] errorObj = mr_server.initiateDemandReset(meterIDs, responseURL, transactionId, expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setInitiateDemandResetResult(arrOfErrorObj);

        return response;
    }

    @PayloadRoot(localPart = "GetAMRSupportedMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAMRSupportedMetersResponse getAMRSupportedMeters(@RequestPayload GetAMRSupportedMeters getAMRSupportedMeters)
            throws MultispeakWebServiceException {
        GetAMRSupportedMetersResponse response = objectFactory.createGetAMRSupportedMetersResponse();

        String lastReceived = getAMRSupportedMeters.getLastReceived();
        Meter[] meters = mr_server.getAMRSupportedMeters(lastReceived);
        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        List<Meter> meterList = arrayOfMeter.getMeter();
        for (Meter meter : meters) {
            meterList.add(meter);
        }
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
        MRArrayOfString2 arrayOfString2 = getLatestReadingByMeterNoAndType.getFieldName();
        List<String> fieldName = arrayOfString2.getString();
        FormattedBlock formattedBlock =
            mr_server.getLatestReadingByMeterNoAndType(meterNo, readingType, formattedBlockTemplateName,
                fieldName.toArray(new String[fieldName.size()]));
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
        MRArrayOfString2 arrayOfString2 = getReadingsByDateAndType.getFieldName();
        List<String> fieldName = arrayOfString2.getString();
        FormattedBlock[] formattedBlocks =
            mr_server.getReadingsByDateAndType((startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                formattedBlockTemplateName, fieldName.toArray(new String[fieldName.size()]));
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlock : formattedBlocks) {
            formattedBlockList.add(formattedBlock);
        }
        response.setGetReadingsByDateAndTypeResult(arrayOfFormattedBlock);

        return response;
    }

    @PayloadRoot(localPart = "MeterAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterAddNotificationResponse meterAddNotification(@RequestPayload MeterAddNotification meterAddNotification)
            throws MultispeakWebServiceException {
        MeterAddNotificationResponse response = objectFactory.createMeterAddNotificationResponse();

        ArrayOfMeter arrayOfMeter = meterAddNotification.getAddedMeters();
        List<Meter> addedMeters = arrayOfMeter.getMeter();
        ErrorObject[] errorObj = mr_server.meterAddNotification(addedMeters.toArray(new Meter[addedMeters.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setMeterAddNotificationResult(arrOfErrorObj);

        return response;
    }

    @PayloadRoot(localPart = "InitiateUsageMonitoring", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateUsageMonitoringResponse initiateUsageMonitoring(
            @RequestPayload InitiateUsageMonitoring initiateUsageMonitoring) throws MultispeakWebServiceException {
        InitiateUsageMonitoringResponse response = objectFactory.createInitiateUsageMonitoringResponse();

        MRArrayOfString2 arrayOfString2 = initiateUsageMonitoring.getMeterNos();
        List<String> meterNos = arrayOfString2.getString();
        ErrorObject[] errorObj = mr_server.initiateUsageMonitoring(meterNos.toArray(new String[meterNos.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setInitiateUsageMonitoringResult(arrOfErrorObj);

        return response;
    }

    @PayloadRoot(localPart = "CancelDisconnectedStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelDisconnectedStatusResponse cancelDisconnectedStatus(
            @RequestPayload CancelDisconnectedStatus cancelDisconnectedStatus) throws MultispeakWebServiceException {
        CancelDisconnectedStatusResponse response = objectFactory.createCancelDisconnectedStatusResponse();

        MRArrayOfString2 arrayOfString2 = cancelDisconnectedStatus.getMeterNos();
        List<String> meterNos = arrayOfString2.getString();
        ErrorObject[] errorObj = mr_server.cancelDisconnectedStatus(meterNos.toArray(new String[meterNos.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setCancelDisconnectedStatusResult(arrOfErrorObj);

        return response;
    }

    @PayloadRoot(localPart = "EstablishMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishMeterGroupResponse establishMeterGroup(@RequestPayload EstablishMeterGroup establishMeterGroup)
            throws MultispeakWebServiceException {
        EstablishMeterGroupResponse response = objectFactory.createEstablishMeterGroupResponse();

        MeterGroup meterGroup = establishMeterGroup.getMeterGroup();
        ErrorObject[] errorObj = mr_server.establishMeterGroup(meterGroup);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setEstablishMeterGroupResult(arrOfErrorObj);

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
        ErrorObject[] errorObj =
            mr_server.initiateMeterReadByMeterNoAndType(meterNo, responseURL, readingType, transactionId,
                expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        response.setInitiateMeterReadByMeterNoAndTypeResult(arrOfErrorObj);

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
        MeterRead[] meterReadArray =
            mr_server.getReadingsByDate((startDate != null) ? startDate.toGregorianCalendar() : null, (endDate != null)
                ? endDate.toGregorianCalendar() : null, lastReceived);
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        for (MeterRead meterRead : meterReadArray) {
            arrayOfMeterRead.getMeterRead().add(meterRead);
        }
        response.setGetReadingsByDateResult(arrayOfMeterRead);

        return response;
    }

    @PayloadRoot(localPart = "GetLatestReadings", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLatestReadingsResponse getLatestReadings(@RequestPayload GetLatestReadings getLatestReadings)
            throws MultispeakWebServiceException {
        GetLatestReadingsResponse response = objectFactory.createGetLatestReadingsResponse();
        String lastRecd = getLatestReadings.getLastReceived();
        MeterRead[] meterReadArray = mr_server.getLatestReadings(lastRecd);
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        for (MeterRead meterRead : meterReadArray) {
            arrayOfMeterRead.getMeterRead().add(meterRead);
        }
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
        MeterRead[] meterReadArray =
            mr_server.getReadingsByMeterNo(meterNo, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();

        for (MeterRead meterRead : meterReadArray) {
            arrayOfMeterRead.getMeterRead().add(meterRead);
        }
        response.setGetReadingsByMeterNoResult(arrayOfMeterRead);

        return response;
    }

    @PayloadRoot(localPart = "GetSupportedReadingTypes", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSupportedReadingTypesResponse getSupportedReadingTypes(
            @RequestPayload GetSupportedReadingTypes getSupportedReadingTypes) throws MultispeakWebServiceException {
        GetSupportedReadingTypesResponse response = objectFactory.createGetSupportedReadingTypesResponse();

        MRArrayOfString2 arrayOfString2 = objectFactory.createMRArrayOfString2();
        String[] readingTypes = mr_server.getSupportedReadingTypes();
        for (String reading : readingTypes) {
            arrayOfString2.getString().add(reading);
        }
        response.setGetSupportedReadingTypesResult(arrayOfString2);

        return response;
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceException {
        MeterChangedNotificationResponse response = objectFactory.createMeterChangedNotificationResponse();
        Meter[] changedMeters = null;
        ArrayOfMeter arrayOfMeter = meterChangedNotification.getChangedMeters();
        if (arrayOfMeter != null && arrayOfMeter.getMeter() != null) {
            changedMeters = new Meter[arrayOfMeter.getMeter().size()];
            for (int arrayIndex = 0; arrayIndex < arrayOfMeter.getMeter().size(); arrayIndex++) {
                changedMeters[arrayIndex] = arrayOfMeter.getMeter().get(arrayIndex);
            }
        }
        ErrorObject[] errorObjects = mr_server.meterChangedNotification(changedMeters);
        ArrayOfErrorObject arrayOfErrorObjects = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObjects) {
            arrayOfErrorObjects.getErrorObject().add(errorObject);
        }
        response.setMeterChangedNotificationResult(arrayOfErrorObjects);

        return response;
    }

    @PayloadRoot(localPart = "InitiateDisconnectedStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateDisconnectedStatusResponse initiateDisconnectedStatus(
            @RequestPayload InitiateDisconnectedStatus initiateDisconnectedStatus) throws MultispeakWebServiceException {
        InitiateDisconnectedStatusResponse response = objectFactory.createInitiateDisconnectedStatusResponse();

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        MRArrayOfString2 arrayOfString2 = initiateDisconnectedStatus.getMeterNos();
        String[] meterNos = new String[arrayOfString2.getString().size()];
        for (int arrayIndex = 0; arrayIndex < arrayOfString2.getString().size(); arrayIndex++) {
            meterNos[arrayIndex] = arrayOfString2.getString().get(arrayIndex);
        }
        ErrorObject[] errorObjects = mr_server.initiateDisconnectedStatus(meterNos);
        for (ErrorObject errorObject : errorObjects) {
            arrayOfErrorObject.getErrorObject().add(errorObject);
        }
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
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        MRArrayOfString2 arrayOfString2 = removeMetersFromMeterGroup.getMeterNumbers();
        String[] meterNos = new String[arrayOfString2.getString().size()];
        for (int arrayIndex = 0; arrayIndex < arrayOfString2.getString().size(); arrayIndex++) {
            meterNos[arrayIndex] = arrayOfString2.getString().get(arrayIndex);
        }
        ErrorObject[] errorObjects = mr_server.removeMetersFromMeterGroup(meterNos, meterGroupIds);
        for (ErrorObject errorObject : errorObjects) {
            arrayOfErrorObject.getErrorObject().add(errorObject);
        }

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

        MRArrayOfString2 arrayOfString2 = cancelUsageMonitoring.getMeterNos();
        List<String> meterList = arrayOfString2.getString();
        String[] meterNos = new String[meterList.size()];
        int i = 0;
        for (String meter : meterList) {
            meterNos[i] = meter;
            i++;
        }
        ErrorObject[] errorObj = mr_server.cancelUsageMonitoring(meterNos);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        cancelUsageMonitoringResponse.setCancelUsageMonitoringResult(arrOfErrorObj);

        return cancelUsageMonitoringResponse;
    }

    @PayloadRoot(localPart = "MeterRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterRemoveNotificationResponse meterRemoveNotification(
            @RequestPayload MeterRemoveNotification meterRemoveNotification) throws MultispeakWebServiceException {
        MeterRemoveNotificationResponse meterRemoveNotificationResponse =
            objectFactory.createMeterRemoveNotificationResponse();

        ArrayOfMeter arrayOfMeter = meterRemoveNotification.getRemovedMeters();
        List<Meter> meters = arrayOfMeter.getMeter();
        Meter[] removedMeter = new Meter[meters.size()];
        int i = 0;
        for (Meter meter : meters) {
            removedMeter[i] = meter;
            i++;
        }
        ErrorObject[] errorObj = mr_server.meterRemoveNotification(removedMeter);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        meterRemoveNotificationResponse.setMeterRemoveNotificationResult(arrOfErrorObj);

        return meterRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse serviceLocationChangedNotificationResponse =
            objectFactory.createServiceLocationChangedNotificationResponse();

        ArrayOfServiceLocation changedServiceLocations =
            serviceLocationChangedNotification.getChangedServiceLocations();
        List<ServiceLocation> serviceLocation = changedServiceLocations.getServiceLocation();
        ServiceLocation[] location = new ServiceLocation[serviceLocation.size()];
        int i = 0;
        for (ServiceLocation serviceLocationChange : location) {
            location[i] = serviceLocationChange;
            i++;
        }
        ErrorObject[] errorObj = mr_server.serviceLocationChangedNotification(location);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        serviceLocationChangedNotificationResponse.setServiceLocationChangedNotificationResult(arrOfErrorObj);

        return serviceLocationChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "InsertMeterInMeterGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InsertMeterInMeterGroupResponse insertMeterInMeterGroup(
            @RequestPayload InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceException {
        InsertMeterInMeterGroupResponse insertMeterInMeterGroupResponse =
            objectFactory.createInsertMeterInMeterGroupResponse();

        MRArrayOfString2 arrayOfString2 = insertMeterInMeterGroup.getMeterNumbers();
        List<String> meterList = arrayOfString2.getString();
        String[] meterNos = new String[meterList.size()];
        int i = 0;
        for (String meter : meterList) {
            meterNos[i] = meter;
            i++;
        }
        String meterGroupID = insertMeterInMeterGroup.getMeterGroupID();

        ErrorObject[] errorObj = mr_server.insertMeterInMeterGroup(meterNos, meterGroupID);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        insertMeterInMeterGroupResponse.setInsertMeterInMeterGroupResult(arrOfErrorObj);

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
        MRArrayOfString2 arrayOfString2 = initiateMeterReadByMeterNumber.getMeterNos();
        List<String> meterList = arrayOfString2.getString();
        String[] meterNos = new String[meterList.size()];
        int i = 0;
        for (String meter : meterList) {
            meterNos[i] = meter;
            i++;
        }
        ErrorObject[] errorObj =
            mr_server.initiateMeterReadByMeterNumber(meterNos, responseURL, transactionID, expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        for (ErrorObject errorObject : errorObj) {
            arrOfErrorObj.getErrorObject().add(errorObject);
        }
        initiateMeterReadByMeterNumberResponse.setInitiateMeterReadByMeterNumberResult(arrOfErrorObj);

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

        MRArrayOfString2 arrayOfString2 = getLatestReadingByType.getFieldName();
        List<String> fieldNameList = arrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String meter : fieldNameList) {
            fieldName[i] = meter;
            i++;
        }
        String lastReceived = getLatestReadingByType.getLastReceived();
        String readingType = getLatestReadingByType.getReadingType();
        String formattedBlockTemplateName = getLatestReadingByType.getFormattedBlockTemplateName();
        FormattedBlock[] formattedBlocks =
            mr_server.getLatestReadingByType(readingType, lastReceived, formattedBlockTemplateName, fieldName);
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();

        for (FormattedBlock block : formattedBlocks) {
            arrayOfFormattedBlock.getFormattedBlock().add(block);
        }
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
        MRArrayOfString2 arrayOfString2 = getReadingByMeterNoAndType.getFieldName();
        List<String> fieldNameList = arrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String meter : fieldNameList) {
            fieldName[i] = meter;
            i++;
        }
        FormattedBlock[] formattedBlocks =
            mr_server.getReadingsByMeterNoAndType(meterNo,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                formattedBlockTemplateName, fieldName);
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        for (FormattedBlock block : formattedBlocks) {
            arrayOfFormattedBlock.getFormattedBlock().add(block);
        }
        getReadingsByMeterNoAndTypeResponse.setGetReadingsByMeterNoAndTypeResult(arrayOfFormattedBlock);
        return getReadingsByMeterNoAndTypeResponse;
    }

    @PayloadRoot(localPart = "CancelPlannedOutage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelPlannedOutageResponse cancelPlannedOutage(@RequestPayload CancelPlannedOutage cancelPlannedOutage)
            throws MultispeakWebServiceException {
        CancelPlannedOutageResponse cancelPlannedOutageResponse = objectFactory.createCancelPlannedOutageResponse();

        MRArrayOfString2 arrayOfString2 = cancelPlannedOutage.getMeterNos();
        List<String> meterList = arrayOfString2.getString();
        String[] meterNos = new String[meterList.size()];
        int i = 0;
        for (String meter : meterList) {
            meterNos[i] = meter;
            i++;
        }
        ErrorObject[] errorObj = mr_server.cancelPlannedOutage(meterNos);
        if (errorObj != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObj) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            cancelPlannedOutageResponse.setCancelPlannedOutageResult(arrOfErrorObj);
        }
        return cancelPlannedOutageResponse;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse customerChangedNotificationResponse =
            objectFactory.createCustomerChangedNotificationResponse();

        ArrayOfCustomer arrayOfChangedCustomers = customerChangedNotification.getChangedCustomers();
        List<Customer> changedCustomersList = arrayOfChangedCustomers.getCustomer();
        Customer[] changedCustomers = changedCustomersList.toArray(new Customer[changedCustomersList.size()]);

        ErrorObject[] errorObj = mr_server.customerChangedNotification(changedCustomers);
        if (errorObj != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObj) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            customerChangedNotificationResponse.setCustomerChangedNotificationResult(arrOfErrorObj);
        }
        return customerChangedNotificationResponse;

    }

    @PayloadRoot(localPart = "CustomersAffectedByOutageNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomersAffectedByOutageNotificationResponse customersAffectedByOutageNotification(
            @RequestPayload CustomersAffectedByOutageNotification customersAffectedByOutageNotification)
            throws MultispeakWebServiceException {
        CustomersAffectedByOutageNotificationResponse customersAffectedByOutageNotificationResponse =
            objectFactory.createCustomersAffectedByOutageNotificationResponse();

        ArrayOfCustomersAffectedByOutage arrayOfCustomersAffectedByOutage =
            customersAffectedByOutageNotification.getNewOutages();
        List<CustomersAffectedByOutage> customersAffectedByOutages =
            arrayOfCustomersAffectedByOutage.getCustomersAffectedByOutage();
        CustomersAffectedByOutage[] newOutages =
            customersAffectedByOutages.toArray(new CustomersAffectedByOutage[customersAffectedByOutages.size()]);

        ErrorObject[] errorObj = mr_server.customersAffectedByOutageNotification(newOutages);
        if (errorObj != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObj) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            customersAffectedByOutageNotificationResponse.setCustomersAffectedByOutageNotificationResult(arrOfErrorObj);
        }
        return customersAffectedByOutageNotificationResponse;

    }

    @PayloadRoot(localPart = "DeleteReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DeleteReadingScheduleResponse deleteReadingSchedule(@RequestPayload DeleteReadingSchedule deleteReadingSchedule)
            throws MultispeakWebServiceException {
        DeleteReadingScheduleResponse deleteReadingScheduleResponse =
            objectFactory.createDeleteReadingScheduleResponse();

        String readingScheduleID = deleteReadingSchedule.getReadingScheduleID();
        ErrorObject[] errorObjects = mr_server.deleteReadingSchedule(readingScheduleID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }

            deleteReadingScheduleResponse.setDeleteReadingScheduleResult(arrOfErrorObj);
        }
        return deleteReadingScheduleResponse;

    }

    @PayloadRoot(localPart = "DeleteSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DeleteScheduleResponse deleteSchedule(@RequestPayload DeleteSchedule deleteSchedule)
            throws MultispeakWebServiceException {
        DeleteScheduleResponse deleteScheduleResponse = objectFactory.createDeleteScheduleResponse();

        String scheduleID = deleteSchedule.getScheduleID();
        ErrorObject[] errorObjects = mr_server.deleteSchedule(scheduleID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }

            deleteScheduleResponse.setDeleteScheduleResult(arrOfErrorObj);
        }
        return deleteScheduleResponse;

    }

    @PayloadRoot(localPart = "DisableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DisableReadingScheduleResponse disableReadingSchedule(@RequestPayload DisableReadingSchedule disableReadingSchedule)
            throws MultispeakWebServiceException {
        DisableReadingScheduleResponse disableReadingScheduleResponse =
            objectFactory.createDisableReadingScheduleResponse();

        String readingScheduleID = disableReadingSchedule.getReadingScheduleID();
        ErrorObject[] errorObjects = mr_server.disableReadingSchedule(readingScheduleID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            disableReadingScheduleResponse.setDisableReadingScheduleResult(arrOfErrorObj);
        }
        return disableReadingScheduleResponse;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse domainMembersChangedNotificationResponse =
            objectFactory.createDomainMembersChangedNotificationResponse();

        ArrayOfDomainMember arrayOfDomainMember = domainMembersChangedNotification.getChangedDomainMembers();
        List<DomainMember> changedDomainMembersList = arrayOfDomainMember.getDomainMember();
        DomainMember[] changedDomainMembers =
            changedDomainMembersList.toArray(new DomainMember[changedDomainMembersList.size()]);

        ErrorObject[] errorObjects = mr_server.domainMembersChangedNotification(changedDomainMembers);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            domainMembersChangedNotificationResponse.setDomainMembersChangedNotificationResult(arrOfErrorObj);
        }

        return domainMembersChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse domainNamesChangedNotificationResponse =
            objectFactory.createDomainNamesChangedNotificationResponse();

        ArrayOfDomainNameChange arrayOfDomainNameChange = domainNamesChangedNotification.getChangedDomainNames();
        List<DomainNameChange> domainNameChangesList = arrayOfDomainNameChange.getDomainNameChange();
        DomainNameChange[] changedDomainNames =
            domainNameChangesList.toArray(new DomainNameChange[domainNameChangesList.size()]);
        ErrorObject[] errorObjects = mr_server.domainNamesChangedNotification(changedDomainNames);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            domainNamesChangedNotificationResponse.setDomainNamesChangedNotificationResult(arrOfErrorObj);
        }
        return domainNamesChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "EnableReadingSchedule", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EnableReadingScheduleResponse enableReadingSchedule(@RequestPayload EnableReadingSchedule enableReadingSchedule)
            throws MultispeakWebServiceException {
        EnableReadingScheduleResponse enableReadingScheduleResponse =
            objectFactory.createEnableReadingScheduleResponse();

        String readingScheduleID = enableReadingSchedule.getReadingScheduleID();
        ErrorObject[] errorObjects = mr_server.enableReadingSchedule(readingScheduleID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            enableReadingScheduleResponse.setEnableReadingScheduleResult(arrOfErrorObj);
        }
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
        ErrorObject[] errorObjects = mr_server.endDeviceShipmentNotification(shipment);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            endDeviceShipmentNotificationResponse.setEndDeviceShipmentNotificationResult(arrOfErrorObj);
        }
        return endDeviceShipmentNotificationResponse;
    }

    @PayloadRoot(localPart = "EstablishReadingSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishReadingSchedulesResponse establishReadingSchedules(
            @RequestPayload EstablishReadingSchedules establishReadingSchedules) throws MultispeakWebServiceException {
        EstablishReadingSchedulesResponse establishReadingSchedulesResponse =
            objectFactory.createEstablishReadingSchedulesResponse();

        ArrayOfReadingSchedule arrayOfReadingSchedule = establishReadingSchedules.getReadingSchedules();
        List<ReadingSchedule> readingSchedulesList = arrayOfReadingSchedule.getReadingSchedule();
        ReadingSchedule[] readingSchedules =
            readingSchedulesList.toArray(new ReadingSchedule[readingSchedulesList.size()]);
        ErrorObject[] errorObjects = mr_server.establishReadingSchedules(readingSchedules);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            establishReadingSchedulesResponse.setEstablishReadingSchedulesResult(arrOfErrorObj);
        }

        return establishReadingSchedulesResponse;
    }

    @PayloadRoot(localPart = "EstablishSchedules", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    EstablishSchedulesResponse establishSchedules(@RequestPayload EstablishSchedules establishSchedules)
            throws MultispeakWebServiceException {
        EstablishSchedulesResponse establishSchedulesResponse = objectFactory.createEstablishSchedulesResponse();

        ArrayOfSchedule arrayOfSchedule = establishSchedules.getSchedules();
        List<Schedule> schedulesList = arrayOfSchedule.getSchedule();
        Schedule[] schedules = schedulesList.toArray(new Schedule[schedulesList.size()]);
        ErrorObject[] errorObjects = mr_server.establishSchedules(schedules);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }

            establishSchedulesResponse.setEstablishSchedulesResult(arrOfErrorObj);
        }
        return establishSchedulesResponse;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse getDomainMembersResponse = objectFactory.createGetDomainMembersResponse();

        String domainNaString = getDomainMembers.getDomainName();
        DomainMember[] domainMembers = mr_server.getDomainMembers(domainNaString);
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        List<DomainMember> domainMemberList = arrayOfDomainMember.getDomainMember();
        for (DomainMember domainMemberValue : domainMembers) {
            domainMemberList.add(domainMemberValue);
        }

        getDomainMembersResponse.setGetDomainMembersResult(arrayOfDomainMember);
        return getDomainMembersResponse;

    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames(@RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        GetDomainNamesResponse getDomainNamesResponse = objectFactory.createGetDomainNamesResponse();
        String[] domainNames = mr_server.getDomainNames();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        List<String> domainNameList = arrayOfString.getString();
        for (String domainNameValue : domainNames) {
            domainNameList.add(domainNameValue);
        }

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
        FormattedBlockTemplate[] formattedBlockTemplates = mr_server.getFormattedBlockTemplates(lastReceived);
        ArrayOfFormattedBlockTemplate arrayOfFormattedBlockTemplate =
            objectFactory.createArrayOfFormattedBlockTemplate();
        List<FormattedBlockTemplate> formattedBlockTemplateList =
            arrayOfFormattedBlockTemplate.getFormattedBlockTemplate();
        for (FormattedBlockTemplate formattedBlockTemplateValue : formattedBlockTemplates) {
            formattedBlockTemplateList.add(formattedBlockTemplateValue);
        }

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
        HistoryLog[] historyLogs =
            mr_server.getHistoryLogByMeterNo(meterNo, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        List<HistoryLog> historyLogsList = arrayOfHistoryLog.getHistoryLog();

        for (HistoryLog historyLogValue : historyLogs) {
            historyLogsList.add(historyLogValue);
        }
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
        HistoryLog[] historyLogs =
            mr_server.getHistoryLogsByDate((startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        List<HistoryLog> historyLogsList = arrayOfHistoryLog.getHistoryLog();
        for (HistoryLog historyLogValue : historyLogs) {
            historyLogsList.add(historyLogValue);
        }
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

        HistoryLog[] historyLogs =
            mr_server.getHistoryLogsByDateAndEventCode(eventCode, (startDate != null) ? startDate.toGregorianCalendar()
                : null, (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        List<HistoryLog> historyLogsList = arrayOfHistoryLog.getHistoryLog();
        for (HistoryLog historyLogValue : historyLogs) {
            historyLogsList.add(historyLogValue);
        }
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
        HistoryLog[] historyLogs =
            mr_server.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        ArrayOfHistoryLog arrayOfHistoryLog = objectFactory.createArrayOfHistoryLog();
        List<HistoryLog> historyLogsList = arrayOfHistoryLog.getHistoryLog();
        for (HistoryLog historyLogValue : historyLogs) {
            historyLogsList.add(historyLogValue);
        }

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
        MRArrayOfString2 arrayOfString2 = getLatestMeterReadingsByMeterGroup.getFieldName();
        List<String> fieldNameList = arrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String meter : fieldNameList) {
            fieldName[i] = meter;
            i++;
        }
        FormattedBlock formattedBlock =
            mr_server.getLatestMeterReadingsByMeterGroup(meterGroupID, formattedBlockTemplateName, fieldName);
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
        MRArrayOfString2 meterNoArrayOfString2 = getLatestReadingsByMeterNoList.getMeterNo();
        List<String> meterNoList = meterNoArrayOfString2.getString();
        String[] meterNo = new String[meterNoList.size()];
        int j = 0;
        for (String meterNoValue : meterNoList) {
            meterNo[j] = meterNoValue;
            j++;
        }

        ServiceType serviceType = getLatestReadingsByMeterNoList.getServiceType();
        XMLGregorianCalendar endDate = getLatestReadingsByMeterNoList.getEndDate();

        MRArrayOfString2 mrArrayOfString2 = getLatestReadingsByMeterNoList.getFieldName();
        List<String> fieldNameList = mrArrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldName[i] = fieldNameValue;
            i++;
        }
        String readingType = getLatestReadingsByMeterNoList.getReadingType();

        FormattedBlock[] formattedBlock =
            mr_server.getLatestReadingsByMeterNoList(meterNo, (startDate != null) ? startDate.toGregorianCalendar()
                : null, (endDate != null) ? endDate.toGregorianCalendar() : null, readingType, lastReceived,
                serviceType, formattedBlockTemplateName, fieldName);
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlockValue : formattedBlock) {
            formattedBlockList.add(formattedBlockValue);
        }
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

        MRArrayOfString2 arrayOfString2 = getLatestReadingsByMeterNoListFormattedBlock.getFieldName();
        List<String> fieldNameList = arrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldName[i] = fieldNameValue;
            i++;
        }
        String lastReceived = getLatestReadingsByMeterNoListFormattedBlock.getLastReceived();
        MRArrayOfString2 meterNoArrayOfString2 = getLatestReadingsByMeterNoListFormattedBlock.getMeterNo();
        List<String> meterNoList = meterNoArrayOfString2.getString();
        String[] meterNo = new String[meterNoList.size()];
        int j = 0;
        for (String meterNoValue : meterNoList) {
            meterNo[j] = meterNoValue;
            j++;
        }

        String formattedBlockTemplateName =
            getLatestReadingsByMeterNoListFormattedBlock.getFormattedBlockTemplateName();
        XMLGregorianCalendar endDate = getLatestReadingsByMeterNoListFormattedBlock.getEndDate();
        ServiceType serviceType = getLatestReadingsByMeterNoListFormattedBlock.getServiceType();
        XMLGregorianCalendar startDate = getLatestReadingsByMeterNoListFormattedBlock.getEndDate();
        FormattedBlock[] formattedBlock =
            mr_server.getLatestReadingsByMeterNoListFormattedBlock(meterNo,
                (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, formattedBlockTemplateName, fieldName,
                lastReceived, serviceType);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlockValue : formattedBlock) {
            formattedBlockList.add(formattedBlockValue);
        }
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
        Meter[] meter = mr_server.getModifiedAMRMeters(previousSessionID, lastReceived);

        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        List<Meter> meterList1 = arrayOfMeter.getMeter();
        for (Meter meterValue : meter) {
            meterList1.add(meterValue);
        }
        getModifiedAMRMetersResponse.setGetModifiedAMRMetersResult(arrayOfMeter);
        return getModifiedAMRMetersResponse;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods(@RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        GetPublishMethodsResponse getPublishMethodsResponse = objectFactory.createGetPublishMethodsResponse();

        String[] publishMethods = mr_server.getPublishMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        List<String> publishMethodsList = arrayOfString.getString();
        for (String publishMethodValue : publishMethods) {
            publishMethodsList.add(publishMethodValue);
        }
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
        MRArrayOfString2 mrArrayOfString2 = getReadingByMeterNumberFormattedBlock.getFieldName();
        List<String> fieldNameList = mrArrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldName[i] = fieldNameValue;
            i++;
        }

        String formattedBlockTemplateName = getReadingByMeterNumberFormattedBlock.getFormattedBlockTemplateName();
        int kWLookBack = getReadingByMeterNumberFormattedBlock.getKWhLookBack();
        XMLGregorianCalendar billingDate = getReadingByMeterNumberFormattedBlock.getBillingDate();
        int kWhLookBack = getReadingByMeterNumberFormattedBlock.getKWhLookBack();
        String lastReceived = getReadingByMeterNumberFormattedBlock.getLastReceived();
        FormattedBlock[] formattedBlock =
            mr_server.getReadingByMeterNumberFormattedBlock(meterNumber,
                (billingDate != null) ? billingDate.toGregorianCalendar() : null, kWhLookBack, kWLookBack,
                kWLookForward, lastReceived, formattedBlockTemplateName, fieldName);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlockValue : formattedBlock) {
            formattedBlockList.add(formattedBlockValue);
        }
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
        MRArrayOfString2 mrArrayOfString2 = getReadingsByDateFormattedBlock.getFieldName();
        List<String> fieldNameList = mrArrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldName[i] = fieldNameValue;
            i++;
        }
        String formattedBlockTemplateName = getReadingsByDateFormattedBlock.getFormattedBlockTemplateName();
        String lastReceived = getReadingsByDateFormattedBlock.getLastReceived();
        int kWhLookBack = getReadingsByDateFormattedBlock.getKWhLookBack();
        FormattedBlock[] formattedBlocks =
            mr_server.getReadingsByDateFormattedBlock((billingDate != null) ? billingDate.toGregorianCalendar() : null,
                kWhLookBack, kWLookBack, kWLookForward, lastReceived, formattedBlockTemplateName, fieldName);
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlockValue : formattedBlocks) {
            formattedBlockList.add(formattedBlockValue);
        }
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
        MeterRead[] meterReads =
            mr_server.getReadingsByUOMAndDate(uomData, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null, lastReceived);

        ArrayOfMeterRead arrayOfMeterRead = objectFactory.createArrayOfMeterRead();
        List<MeterRead> meterReadList = arrayOfMeterRead.getMeterRead();
        for (MeterRead meterReadValue : meterReads) {
            meterReadList.add(meterReadValue);

        }

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
    GetReadingSchedulesResponse getReadingSchedules(@RequestPayload GetReadingSchedules getReadingSchedules)
            throws MultispeakWebServiceException {
        GetReadingSchedulesResponse getReadingSchedulesResponse = objectFactory.createGetReadingSchedulesResponse();
        String lastReceived = getReadingSchedules.getLastReceived();

        ReadingSchedule[] readingSchedules = mr_server.getReadingSchedules(lastReceived);

        ArrayOfReadingSchedule arrayOfReadingSchedule = objectFactory.createArrayOfReadingSchedule();
        List<ReadingSchedule> readingScheduleList = arrayOfReadingSchedule.getReadingSchedule();
        for (ReadingSchedule readingScheduleValue : readingSchedules) {
            readingScheduleList.add(readingScheduleValue);

        }
        getReadingSchedulesResponse.setGetReadingSchedulesResult(arrayOfReadingSchedule);
        return getReadingSchedulesResponse;
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {

        GetRegistrationInfoByIDResponse getRegistrationInfoByIDResponse =
            objectFactory.createGetRegistrationInfoByIDResponse();
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
        Schedule[] schedules = mr_server.getSchedules(lastReceived);
        ArrayOfSchedule arrayOfSchedule = objectFactory.createArrayOfSchedule();
        List<Schedule> scheduleList = arrayOfSchedule.getSchedule();
        for (Schedule scheudleValue : schedules) {
            scheduleList.add(scheudleValue);

        }
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
        ArrayOfInHomeDisplay arrayOfInHomeDisplay = inHomeDisplayAddNotification.getAddedIHDs();

        List<InHomeDisplay> inHomeDisplayList = arrayOfInHomeDisplay.getInHomeDisplay();
        InHomeDisplay[] addedIHDs = new InHomeDisplay[inHomeDisplayList.size()];
        int i = 0;
        for (InHomeDisplay addedIHDsValue : inHomeDisplayList) {
            addedIHDs[i] = addedIHDsValue;
            i++;
        }
        ErrorObject[] errorObjects = mr_server.inHomeDisplayAddNotification(addedIHDs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            inHomeDisplayAddNotificationResponse.setInHomeDisplayAddNotificationResult(arrOfErrorObj);
        }
        return inHomeDisplayAddNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayChangedNotificationResponse inHomeDisplayChangedNotification(
            @RequestPayload InHomeDisplayChangedNotification inHomeDisplayChangedNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayChangedNotificationResponse inHomeDisplayChangedNotificationResponse =
            objectFactory.createInHomeDisplayChangedNotificationResponse();
        ArrayOfInHomeDisplay arrayOfInHomeDisplay = inHomeDisplayChangedNotification.getChangedIHDs();
        List<InHomeDisplay> inHomeDisplayList = arrayOfInHomeDisplay.getInHomeDisplay();
        InHomeDisplay[] changedIHDs = new InHomeDisplay[inHomeDisplayList.size()];
        int i = 0;
        for (InHomeDisplay addedIHDsValue : inHomeDisplayList) {
            changedIHDs[i] = addedIHDsValue;
            i++;
        }

        ErrorObject[] errorObjects = mr_server.inHomeDisplayChangedNotification(changedIHDs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            inHomeDisplayChangedNotificationResponse.setInHomeDisplayChangedNotificationResult(arrOfErrorObj);
        }

        return inHomeDisplayChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayExchangeNotificationResponse inHomeDisplayExchangeNotification(
            @RequestPayload InHomeDisplayExchangeNotification inHomeDisplayExchangeNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayExchangeNotificationResponse inHomeDisplayExchangeNotificationResponse =
            objectFactory.createInHomeDisplayExchangeNotificationResponse();
        ArrayOfInHomeDisplayExchange arrayOfInHomeDisplayExchange = objectFactory.createArrayOfInHomeDisplayExchange();
        List<InHomeDisplayExchange> inHomeDisplayExchangesList =
            arrayOfInHomeDisplayExchange.getInHomeDisplayExchange();
        InHomeDisplayExchange[] IHDChangeout = new InHomeDisplayExchange[inHomeDisplayExchangesList.size()];
        int i = 0;
        for (InHomeDisplayExchange inHomeDisplayExchangeValue : inHomeDisplayExchangesList) {
            IHDChangeout[i] = inHomeDisplayExchangeValue;
            i++;
        }
        ErrorObject[] errorObjects = mr_server.inHomeDisplayExchangeNotification(IHDChangeout);
        if (errorObjects != null) {

            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                inHomeDisplayExchangeNotificationResponse.setInHomeDisplayExchangeNotificationResult(arrOfErrorObj);
            }
        }

        return inHomeDisplayExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayRemoveNotificationResponse inHomeDisplayRemoveNotification(
            @RequestPayload InHomeDisplayRemoveNotification inHomeDisplayRemoveNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayRemoveNotificationResponse inHomeDisplayRemoveNotificationResponse =
            objectFactory.createInHomeDisplayRemoveNotificationResponse();
        ArrayOfInHomeDisplay arrayOfInHomeDisplay = inHomeDisplayRemoveNotification.getRemovedIHDs();

        List<InHomeDisplay> inHomeDisplayRemoveList = arrayOfInHomeDisplay.getInHomeDisplay();
        InHomeDisplay[] removedIHDs = new InHomeDisplay[inHomeDisplayRemoveList.size()];
        int i = 0;
        for (InHomeDisplay InHomeDisplayValue : inHomeDisplayRemoveList) {
            removedIHDs[i] = InHomeDisplayValue;
            i++;
        }
        ErrorObject[] errorObjects = mr_server.inHomeDisplayRemoveNotification(removedIHDs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                inHomeDisplayRemoveNotificationResponse.setInHomeDisplayRemoveNotificationResult(arrOfErrorObj);
            }
        }
        return inHomeDisplayRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "InHomeDisplayRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InHomeDisplayRetireNotificationResponse inHomeDisplayRetireNotification(
            @RequestPayload InHomeDisplayRetireNotification inHomeDisplayRetireNotification)
            throws MultispeakWebServiceException {
        InHomeDisplayRetireNotificationResponse inHomeDisplayRetireNotificationResponse =
            objectFactory.createInHomeDisplayRetireNotificationResponse();

        ArrayOfInHomeDisplay arrayOfInHomeDisplay = inHomeDisplayRetireNotification.getRetiredIHDs();
        List<InHomeDisplay> inHomeDisplayRetireList = arrayOfInHomeDisplay.getInHomeDisplay();
        InHomeDisplay[] retiredIHDs = new InHomeDisplay[inHomeDisplayRetireList.size()];
        int i = 0;
        for (InHomeDisplay InHomeDisplayRetireValue : inHomeDisplayRetireList) {
            retiredIHDs[i] = InHomeDisplayRetireValue;
            i++;
        }
        ErrorObject[] errorObjects = mr_server.inHomeDisplayRetireNotification(retiredIHDs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                inHomeDisplayRetireNotificationResponse.setInHomeDisplayRetireNotificationResult(arrOfErrorObj);
            }

        }
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
        ErrorObject[] errorObjects =
            mr_server.initiateGroupMeterRead(meterGroupName, responseURL, transactionID, expirationTime);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                initiateGroupMeterReadResponse.setInitiateGroupMeterReadResult(arrOfErrorObj);
            }

        }
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
        ErrorObject[] errorObjects =
            mr_server.initiateMeterReadByObject(objectName, nounType, phaseCode, responseURL, transactionID,
                expirationTime);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                initiateMeterReadByObjectResponse.setInitiateMeterReadByObjectResult(arrOfErrorObj);
            }

        }
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
        MRArrayOfString2 mrArrayOfString2 = initiateMeterReadsByFieldName.getFieldNames();
        List<String> fieldNameList = mrArrayOfString2.getString();
        String[] fieldNames = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldNames[i] = fieldNameValue;
            i++;
        }
        String responseURL = initiateMeterReadsByFieldName.getResponseURL();
        MRArrayOfString2 meterNoArrayOfString2 = initiateMeterReadsByFieldName.getMeterNumbers();
        List<String> meterNoList = meterNoArrayOfString2.getString();
        String[] meterNumbers = new String[meterNoList.size()];
        int j = 0;
        for (String meterNoValue : meterNoList) {
            meterNumbers[j] = meterNoValue;
            j++;
        }
        String formattedBlockTemplateName = initiateMeterReadsByFieldName.getFormattedBlockTemplateName();
        float expirationTime = initiateMeterReadsByFieldName.getExpirationTime();
        ErrorObject[] errorObjects =
            mr_server.initiateMeterReadsByFieldName(meterNumbers, fieldNames, responseURL, transactionID,
                expirationTime, formattedBlockTemplateName);

        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                initiateMeterReadsByFieldNameResponse.setInitiateMeterReadsByFieldNameResult(arrOfErrorObj);
            }
        }
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
        MRArrayOfString2 meterNoArrayOfString2 = initiatePlannedOutage.getMeterNos();
        List<String> meterNoList = meterNoArrayOfString2.getString();
        String[] meterNos = new String[meterNoList.size()];
        int j = 0;
        for (String meterNoValue : meterNoList) {
            meterNos[j] = meterNoValue;
            j++;
        }
        ErrorObject[] errorObjects =
            mr_server.initiatePlannedOutage(meterNos, (startDate != null) ? startDate.toGregorianCalendar() : null,
                (endDate != null) ? endDate.toGregorianCalendar() : null);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                initiatePlannedOutageResponse.setInitiatePlannedOutageResult(arrOfErrorObj);
            }
        }
        return initiatePlannedOutageResponse;
    }

    @PayloadRoot(localPart = "InsertMetersInConfigurationGroup", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InsertMetersInConfigurationGroupResponse insertMetersInConfigurationGroup(
            @RequestPayload InsertMetersInConfigurationGroup insertMetersInConfigurationGroup)
            throws MultispeakWebServiceException {

        InsertMetersInConfigurationGroupResponse insertMetersInConfigurationGroupResponse =
            objectFactory.createInsertMetersInConfigurationGroupResponse();
        MRArrayOfString2 meterNoArrayOfString2 = insertMetersInConfigurationGroup.getMeterNumbers();
        List<String> meterNoList = meterNoArrayOfString2.getString();
        String[] meterNumbers = new String[meterNoList.size()];
        int j = 0;
        for (String meterNoValue : meterNoList) {
            meterNumbers[j] = meterNoValue;
            j++;
        }
        ServiceType serviceType = insertMetersInConfigurationGroup.getServiceType();
        String meterGroupID = insertMetersInConfigurationGroup.getMeterGroupID();
        ErrorObject[] errorObjects =
            mr_server.insertMetersInConfigurationGroup(meterNumbers, meterGroupID, serviceType);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                insertMetersInConfigurationGroupResponse.setInsertMetersInConfigurationGroupResult(arrOfErrorObj);
            }
        }
        return insertMetersInConfigurationGroupResponse;
    }

    @PayloadRoot(localPart = "MeterBaseAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseAddNotificationResponse meterBaseAddNotification(
            @RequestPayload MeterBaseAddNotification meterBaseAddNotification) throws MultispeakWebServiceException {
        MeterBaseAddNotificationResponse meterBaseAddNotificationResponse =
            objectFactory.createMeterBaseAddNotificationResponse();
        ArrayOfMeterBase arrayOfMeterBase = meterBaseAddNotification.getAddedMBs();

        List<MeterBase> meterBaseList = arrayOfMeterBase.getMeterBase();
        MeterBase[] addedMBs = new MeterBase[meterBaseList.size()];
        int j = 0;
        for (MeterBase addedMBsValue : meterBaseList) {
            addedMBs[j] = addedMBsValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterBaseAddNotification(addedMBs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            meterBaseAddNotificationResponse.setMeterBaseAddNotificationResult(arrOfErrorObj);
        }
        return meterBaseAddNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseChangedNotificationResponse meterBaseChangedNotification(
            @RequestPayload MeterBaseChangedNotification meterBaseChangedNotification)
            throws MultispeakWebServiceException {
        MeterBaseChangedNotificationResponse meterBaseChangedNotificationResponse =
            objectFactory.createMeterBaseChangedNotificationResponse();
        ArrayOfMeterBase arrayOfMeterBase = meterBaseChangedNotification.getChangedMBs();
        List<MeterBase> meterBaseList = arrayOfMeterBase.getMeterBase();
        MeterBase[] changedMBs = new MeterBase[meterBaseList.size()];
        int j = 0;
        for (MeterBase addedMBsValue : meterBaseList) {
            changedMBs[j] = addedMBsValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterBaseChangedNotification(changedMBs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            meterBaseChangedNotificationResponse.setMeterBaseChangedNotificationResult(arrOfErrorObj);
        }
        return meterBaseChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseExchangeNotificationResponse meterBaseExchangeNotification(
            @RequestPayload MeterBaseExchangeNotification meterBaseExchangeNotification)
            throws MultispeakWebServiceException {
        MeterBaseExchangeNotificationResponse meterBaseExchangeNotificationResponse =
            objectFactory.createMeterBaseExchangeNotificationResponse();

        ArrayOfMeterBaseExchange ofMeterBaseExchange = meterBaseExchangeNotification.getMBChangeout();
        List<MeterBaseExchange> meterBaseList = ofMeterBaseExchange.getMeterBaseExchange();
        MeterBaseExchange[] MBChangeout = new MeterBaseExchange[meterBaseList.size()];
        int j = 0;
        for (MeterBaseExchange addedMBsValue : meterBaseList) {
            MBChangeout[j] = addedMBsValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterBaseExchangeNotification(MBChangeout);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                meterBaseExchangeNotificationResponse.setMeterBaseExchangeNotificationResult(arrOfErrorObj);
            }

        }
        return meterBaseExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterBaseRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseRemoveNotificationResponse meterBaseRemoveNotification(
            @RequestPayload MeterBaseRemoveNotification meterBaseRemoveNotification)
            throws MultispeakWebServiceException {
        MeterBaseRemoveNotificationResponse meterBaseRemoveNotificationResponse =
            objectFactory.createMeterBaseRemoveNotificationResponse();

        ArrayOfMeterBase arrayOfMeterBase = meterBaseRemoveNotification.getRemovedMBs();
        List<MeterBase> meterBaseList = arrayOfMeterBase.getMeterBase();
        MeterBase[] removedMBs = new MeterBase[meterBaseList.size()];
        int j = 0;
        for (MeterBase removedMBsValue : meterBaseList) {
            removedMBs[j] = removedMBsValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterBaseRemoveNotification(removedMBs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                meterBaseRemoveNotificationResponse.setMeterBaseRemoveNotificationResult(arrOfErrorObj);
            }

        }
        return meterBaseRemoveNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterConnectivityNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterConnectivityNotificationResponse meterConnectivityNotification(
            @RequestPayload MeterConnectivityNotification meterConnectivityNotification)
            throws MultispeakWebServiceException {
        MeterConnectivityNotificationResponse meterConnectivityNotificationResponse =
            objectFactory.createMeterConnectivityNotificationResponse();
        ArrayOfMeterConnectivity arrayOfMeterConnectivity = meterConnectivityNotification.getNewConnectivity();
        List<MeterConnectivity> meterConnectivitiesList = arrayOfMeterConnectivity.getMeterConnectivity();
        MeterConnectivity[] newConnectivity = new MeterConnectivity[meterConnectivitiesList.size()];
        int j = 0;
        for (MeterConnectivity meterConnectivityValue : meterConnectivitiesList) {
            newConnectivity[j] = meterConnectivityValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterConnectivityNotification(newConnectivity);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
                meterConnectivityNotificationResponse.setMeterConnectivityNotificationResult(arrOfErrorObj);
            }

        }
        return meterConnectivityNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterExchangeNotificationResponse meterExchangeNotification(
            @RequestPayload MeterExchangeNotification meterExchangeNotification) throws MultispeakWebServiceException {
        MeterExchangeNotificationResponse meterExchangeNotificationResponse =
            objectFactory.createMeterExchangeNotificationResponse();

        ArrayOfMeterExchange arrayOfMeterExchange = null;
        MeterExchange[] meterChangeout = null;
        arrayOfMeterExchange = meterExchangeNotification.getMeterChangeout();
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (null != arrayOfMeterExchange) {
            List<MeterExchange> meterExchangeList = arrayOfMeterExchange.getMeterExchange();
            meterChangeout = new MeterExchange[meterExchangeList.size()];
            int j = 0;
            for (MeterExchange meterChangeoutValue : meterExchangeList) {
                meterChangeout[j] = meterChangeoutValue;
                j++;
            }
        }
        ErrorObject[] errorObjects = mr_server.meterExchangeNotification(meterChangeout);

        if (errorObjects != null) {
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
        }

        meterExchangeNotificationResponse.setMeterExchangeNotificationResult(arrOfErrorObj);
        return meterExchangeNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterRetireNotificationResponse meterRetireNotification(
            @RequestPayload MeterRetireNotification meterRetireNotification) throws MultispeakWebServiceException {
        MeterRetireNotificationResponse meterRetireNotificationResponse =
            objectFactory.createMeterRetireNotificationResponse();

        ArrayOfMeter arrayOfMeter = meterRetireNotification.getRetiredMeters();
        List<Meter> meterList = arrayOfMeter.getMeter();
        Meter[] retiredMeters = new Meter[meterList.size()];
        int j = 0;
        for (Meter meterValue : meterList) {
            retiredMeters[j] = meterValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterRetireNotification(retiredMeters);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            meterRetireNotificationResponse.setMeterRetireNotificationResult(arrOfErrorObj);
        }
        return meterRetireNotificationResponse;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse registerForServiceResponse = objectFactory.createRegisterForServiceResponse();
        RegistrationInfo registrationDetails = registerForService.getRegistrationDetails();
        ErrorObject[] errorObjects = mr_server.registerForService(registrationDetails);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            registerForServiceResponse.setRegisterForServiceResult(arrOfErrorObj);

        }
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
        MRArrayOfString2 mrArrayOfString2 = removeMetersFromConfigurationGroup.getMeterNumbers();
        List<String> meterList = mrArrayOfString2.getString();
        String[] meterNumbers = new String[meterList.size()];
        int j = 0;
        for (String meterValue : meterList) {
            meterNumbers[j] = meterValue;
            j++;
        }
        String meterGroupID = removeMetersFromConfigurationGroup.getMeterGroupID();
        ErrorObject[] errorObjects =
            mr_server.removeMetersFromConfigurationGroup(meterNumbers, meterGroupID, serviceType);

        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            removeMetersFromConfigurationGroupResponse.setRemoveMetersFromConfigurationGroupResult(arrOfErrorObj);

        }
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
        ErrorObject[] errorObjects =
            mr_server.scheduleGroupMeterRead(meterGroupName, (timeToRead != null) ? timeToRead.toGregorianCalendar()
                : null, responseURL, transactionID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            scheduleGroupMeterReadResponse.setScheduleGroupMeterReadResult(arrOfErrorObj);
        }
        return scheduleGroupMeterReadResponse;

    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse unregisterForServiceResponse = objectFactory.createUnregisterForServiceResponse();

        String registrationID = unregisterForService.getRegistrationID();
        ErrorObject[] errorObjects = mr_server.unregisterForService(registrationID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            unregisterForServiceResponse.setUnregisterForServiceResult(arrOfErrorObj);
        }
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
        ErrorObject[] errorObjects = mr_server.updateServiceLocationDisplays(servLocID);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            updateServiceLocationDisplaysResponse.setUpdateServiceLocationDisplaysResult(arrOfErrorObj);
        }
        return updateServiceLocationDisplaysResponse;
    }

    @PayloadRoot(localPart = "MeterBaseRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterBaseRetireNotificationResponse meterBaseRetireNotification(
            @RequestPayload MeterBaseRetireNotification meterBaseRetireNotification)
            throws MultispeakWebServiceException {
        MeterBaseRetireNotificationResponse meterBaseRetireNotificationResponse =
            objectFactory.createMeterBaseRetireNotificationResponse();

        ArrayOfMeterBase arrayOfMeterBase = meterBaseRetireNotification.getRetiredMBs();
        List<MeterBase> meterBaseList = arrayOfMeterBase.getMeterBase();
        MeterBase[] retiredMBs = new MeterBase[meterBaseList.size()];
        int j = 0;
        for (MeterBase meterBaseValue : meterBaseList) {
            retiredMBs[j] = meterBaseValue;
            j++;
        }
        ErrorObject[] errorObjects = mr_server.meterBaseRetireNotification(retiredMBs);
        if (errorObjects != null) {
            ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }
            meterBaseRetireNotificationResponse.setMeterBaseRetireNotificationResult(arrOfErrorObj);
        }
        return meterBaseRetireNotificationResponse;

    }

    @PayloadRoot(localPart = "GetReadingsByBillingCycle", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetReadingsByBillingCycleResponse getReadingsByBillingCycle(
            @RequestPayload GetReadingsByBillingCycle getReadingsByBillingCycle) throws MultispeakWebServiceException {
        GetReadingsByBillingCycleResponse getReadingsByBillingCycleResponse =
            objectFactory.createGetReadingsByBillingCycleResponse();
        XMLGregorianCalendar billingDate = getReadingsByBillingCycle.getBillingDate();
        MRArrayOfString2 mrArrayOfString2 = getReadingsByBillingCycle.getFieldName();
        List<String> fieldNameList = mrArrayOfString2.getString();
        String[] fieldName = new String[fieldNameList.size()];
        int i = 0;
        for (String fieldNameValue : fieldNameList) {
            fieldName[i] = fieldNameValue;
            i++;
        }
        int kWLookBack = getReadingsByBillingCycle.getKWLookBack();
        String lastReceived = getReadingsByBillingCycle.getLastReceived();
        int kWhLookBack = getReadingsByBillingCycle.getKWhLookBack();
        String billingCycle = getReadingsByBillingCycle.getBillingCycle();
        String formattedBlockTemplateName = getReadingsByBillingCycle.getFormattedBlockTemplateName();
        int kWLookForward = getReadingsByBillingCycle.getKWLookForward();

        FormattedBlock[] formattedBlocks =
            mr_server.getReadingsByBillingCycle(billingCycle, (billingDate != null) ? billingDate.toGregorianCalendar()
                : null, kWhLookBack, kWLookBack, kWLookForward, lastReceived, formattedBlockTemplateName, fieldName);
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        List<FormattedBlock> formattedBlockList = arrayOfFormattedBlock.getFormattedBlock();
        for (FormattedBlock formattedBlock : formattedBlocks) {
            formattedBlockList.add(formattedBlock);
        }
        getReadingsByBillingCycleResponse.setGetReadingsByBillingCycleResult(arrayOfFormattedBlock);

        return getReadingsByBillingCycleResponse;

    }

}
