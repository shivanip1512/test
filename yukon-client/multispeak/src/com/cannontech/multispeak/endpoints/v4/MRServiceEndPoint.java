package com.cannontech.multispeak.endpoints.v4;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfErrorObject;
import com.cannontech.msp.beans.v4.ArrayOfFormattedBlock;
import com.cannontech.msp.beans.v4.ArrayOfMeterID1;
import com.cannontech.msp.beans.v4.ArrayOfMeterReading1;
import com.cannontech.msp.beans.v4.ArrayOfServiceLocation1;
import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.ArrayOfString18;
import com.cannontech.msp.beans.v4.CancelUsageMonitoring;
import com.cannontech.msp.beans.v4.CancelUsageMonitoringResponse;
import com.cannontech.msp.beans.v4.DeleteMeterGroup;
import com.cannontech.msp.beans.v4.DeleteMeterGroupResponse;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.EstablishMeterGroup;
import com.cannontech.msp.beans.v4.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v4.FormattedBlock;
import com.cannontech.msp.beans.v4.GetAMRSupportedMeters;
import com.cannontech.msp.beans.v4.GetAMRSupportedMetersResponse;
import com.cannontech.msp.beans.v4.GetLatestReadingByFieldName;
import com.cannontech.msp.beans.v4.GetLatestReadingByFieldNameResponse;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterID;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterIDAndFieldName;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterIDAndFieldNameResponse;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetLatestReadings;
import com.cannontech.msp.beans.v4.GetLatestReadingsResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateAndFieldName;
import com.cannontech.msp.beans.v4.GetReadingsByDateAndFieldNameResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v4.GetReadingsByMeterID;
import com.cannontech.msp.beans.v4.GetReadingsByMeterIDAndFieldName;
import com.cannontech.msp.beans.v4.GetReadingsByMeterIDAndFieldNameResponse;
import com.cannontech.msp.beans.v4.GetReadingsByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetSupportedFieldNames;
import com.cannontech.msp.beans.v4.GetSupportedFieldNamesResponse;
import com.cannontech.msp.beans.v4.InitiateUsageMonitoring;
import com.cannontech.msp.beans.v4.InitiateUsageMonitoringResponse;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v4.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v4.IsAMRMeter;
import com.cannontech.msp.beans.v4.IsAMRMeterResponse;
import com.cannontech.msp.beans.v4.MeterAddNotification;
import com.cannontech.msp.beans.v4.MeterAddNotificationResponse;
import com.cannontech.msp.beans.v4.MeterGroup;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v4.RemoveMetersFromMeterGroupResponse;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.msp.beans.v4.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v4.ServiceLocationChangedNotificationResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MR_Server;

/*
 * This class is the MR Service endpoint all requests will be processed from here.
 */

@Endpoint("MRServiceEndpointV4")
@RequestMapping("/multispeak/v4/MR_Server")
public class MRServiceEndPoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private MR_Server mr_server;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String MR_V4_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v4;

    @PayloadRoot(localPart = "PingURL", namespace = MR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        mr_server.pingURL();

        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = mr_server.getMethods();

        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByDate", namespace = MR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetReadingsByDateResponse getReadingsByDate(@RequestPayload GetReadingsByDate getReadingsByDate)
            throws MultispeakWebServiceException {
        GetReadingsByDateResponse response = objectFactory.createGetReadingsByDateResponse();

        XMLGregorianCalendar startDate = getReadingsByDate.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByDate.getEndDate();
        String lastReceived = getReadingsByDate.getLastReceived();
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<MeterReading> meterReading = mr_server.getReadingsByDate(startDate.toGregorianCalendar(),
                endDate.toGregorianCalendar(),
                lastReceived);

        ArrayOfMeterReading1 arrayOfMeterReading = objectFactory.createArrayOfMeterReading1();
        arrayOfMeterReading.getMeterReading().addAll(meterReading);
        response.setGetReadingsByDateResult(arrayOfMeterReading);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByMeterID", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetReadingsByMeterIDResponse getReadingsByMeterID(
            @RequestPayload GetReadingsByMeterID getReadingsByMeterID)
            throws MultispeakWebServiceException {
        GetReadingsByMeterIDResponse response = objectFactory.createGetReadingsByMeterIDResponse();

        XMLGregorianCalendar startDate = getReadingsByMeterID.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByMeterID.getEndDate();

        if (getReadingsByMeterID.getMeterID() == null) {
            throw new MultispeakWebServiceException("Missing MeterID or MeterNo in request");
        }
        String meterNo = getReadingsByMeterID.getMeterID().getMeterNo();

        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<MeterReading> meterReading = mr_server.getReadingsByMeterID(meterNo,
                startDate.toGregorianCalendar(),
                endDate.toGregorianCalendar());

        ArrayOfMeterReading1 arrayOfMeterReading = objectFactory.createArrayOfMeterReading1();
        arrayOfMeterReading.getMeterReading().addAll(meterReading);
        response.setGetReadingsByMeterIDResult(arrayOfMeterReading);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestReadings", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetLatestReadingsResponse getLatestReadings(@RequestPayload GetLatestReadings getLatestReadings)
            throws MultispeakWebServiceException {
        GetLatestReadingsResponse response = objectFactory.createGetLatestReadingsResponse();
        String lastRecd = getLatestReadings.getLastReceived();
        List<MeterReading> meterReading = mr_server.getLatestReadings(lastRecd);

        ArrayOfMeterReading1 arrayOfMeterReading = objectFactory.createArrayOfMeterReading1();
        arrayOfMeterReading.getMeterReading().addAll(meterReading);
        response.setGetLatestReadingsResult(arrayOfMeterReading);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestReadingByMeterID", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetLatestReadingByMeterIDResponse getLatestReadingByMeterID(
            @RequestPayload GetLatestReadingByMeterID getLatestReadingByMeterId) throws MultispeakWebServiceException {
        GetLatestReadingByMeterIDResponse getLatestReadingByMeterIDResponse = objectFactory
                .createGetLatestReadingByMeterIDResponse();

        if (getLatestReadingByMeterId.getMeterID() == null) {
            throw new MultispeakWebServiceException("Missing MeterID or MeterNo in request");
        }

        String meterNo = getLatestReadingByMeterId.getMeterID().getMeterNo();
        MeterReading meterReading = mr_server.getLatestReadingByMeterID(meterNo);
        getLatestReadingByMeterIDResponse.setGetLatestReadingByMeterIDResult(meterReading);

        return getLatestReadingByMeterIDResponse;
    }

    @PayloadRoot(localPart = "IsAMRMeter", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload IsAMRMeterResponse isAMRMeter(@RequestPayload IsAMRMeter isAMRMeter)
            throws MultispeakWebServiceException {
        IsAMRMeterResponse isAMRMeterResponse = objectFactory.createIsAMRMeterResponse();

        String meterNo = isAMRMeter.getMeterID().getMeterNo();
        boolean response = mr_server.isAMRMeter(meterNo);
        isAMRMeterResponse.setIsAMRMeterResult(response);
        return isAMRMeterResponse;
    }

    @PayloadRoot(localPart = "InitiateUsageMonitoring", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload InitiateUsageMonitoringResponse initiateUsageMonitoring(
            @RequestPayload InitiateUsageMonitoring initiateUsageMonitoring) throws MultispeakWebServiceException {
        InitiateUsageMonitoringResponse response = objectFactory.createInitiateUsageMonitoringResponse();

        ArrayOfMeterID1 ArrOfMeterIDs = initiateUsageMonitoring.getMeterIDs();
        List<MeterID> meterIDs = (null != ArrOfMeterIDs.getMeterID()) ? ArrOfMeterIDs.getMeterID() : null;
        List<ErrorObject> errorObjects = mr_server.initiateUsageMonitoring(ListUtils.emptyIfNull(meterIDs));

        ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects);
        response.setInitiateUsageMonitoringResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "CancelUsageMonitoring", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload CancelUsageMonitoringResponse cancelUsageMonitoring(
            @RequestPayload CancelUsageMonitoring cancelUsageMonitoring)
            throws MultispeakWebServiceException {
        CancelUsageMonitoringResponse cancelUsageMonitoringResponse = objectFactory.createCancelUsageMonitoringResponse();

        ArrayOfMeterID1 ArrOfMeterIDs = cancelUsageMonitoring.getMeterIDs();
        List<MeterID> meterIDs = null != ArrOfMeterIDs.getMeterID() ? ArrOfMeterIDs.getMeterID() : null;

        List<ErrorObject> errorObjects = mr_server.cancelUsageMonitoring(ListUtils.emptyIfNull(meterIDs));

        ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects);
        cancelUsageMonitoringResponse.setCancelUsageMonitoringResult(arrayOfErrorObject);
        return cancelUsageMonitoringResponse;
    }

    @PayloadRoot(localPart = "GetAMRSupportedMeters", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetAMRSupportedMetersResponse getAMRSupportedMeters(
            @RequestPayload GetAMRSupportedMeters getAMRSupportedMeters)
            throws MultispeakWebServiceException {
        GetAMRSupportedMetersResponse response = objectFactory.createGetAMRSupportedMetersResponse();

        String lastReceived = getAMRSupportedMeters.getLastReceived();
        Meters meters = mr_server.getAMRSupportedMeters(lastReceived);
        response.setGetAMRSupportedMetersResult(meters);
        return response;
    }

    @PayloadRoot(localPart = "GetSupportedFieldNames", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetSupportedFieldNamesResponse getSupportedFieldNames(
            @RequestPayload GetSupportedFieldNames getSupportedFieldNames) throws MultispeakWebServiceException {
        GetSupportedFieldNamesResponse response = objectFactory.createGetSupportedFieldNamesResponse();

        List<String> supportedFieldNames = mr_server.getSupportedFieldNames();

        ArrayOfString18 arrOfSupportedFieldNames = objectFactory.createArrayOfString18();
        arrOfSupportedFieldNames.getVal().addAll(supportedFieldNames);
        response.setGetSupportedFieldNamesResult(arrOfSupportedFieldNames);
        return response;
    }

    @PayloadRoot(localPart = "GetReadingsByDateAndFieldName", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetReadingsByDateAndFieldNameResponse getReadingsByDateAndFieldName(
            @RequestPayload GetReadingsByDateAndFieldName getReadingsByDateAndFieldName) throws MultispeakWebServiceException {
        GetReadingsByDateAndFieldNameResponse response = objectFactory.createGetReadingsByDateAndFieldNameResponse();

        String lastReceived = getReadingsByDateAndFieldName.getLastReceived();
        String formattedBlockTemplateName = getReadingsByDateAndFieldName.getFormattedBlockTemplateName();
        XMLGregorianCalendar startDate = getReadingsByDateAndFieldName.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByDateAndFieldName.getEndDate();

        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }
        List<FormattedBlock> formattedBlocks = mr_server.getReadingsByDateAndFieldName(startDate.toGregorianCalendar(),
                endDate.toGregorianCalendar(),
                lastReceived, formattedBlockTemplateName);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        response.setGetReadingsByDateAndFieldNameResult(arrayOfFormattedBlock);
        return response;
    }
    
    @PayloadRoot(localPart = "GetReadingsByMeterIDAndFieldName", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload
    GetReadingsByMeterIDAndFieldNameResponse getReadingsByMeterIDAndFieldName(
            @RequestPayload GetReadingsByMeterIDAndFieldName getReadingsByMeterIDAndFieldName)
            throws MultispeakWebServiceException {
        GetReadingsByMeterIDAndFieldNameResponse getReadingsByMeterIDAndFieldNameResponse =
            objectFactory.createGetReadingsByMeterIDAndFieldNameResponse();
        XMLGregorianCalendar endDate = getReadingsByMeterIDAndFieldName.getEndDate();
        XMLGregorianCalendar startDate = getReadingsByMeterIDAndFieldName.getStartDate();
        
        if (getReadingsByMeterIDAndFieldName.getMeterID() == null) {
            throw new MultispeakWebServiceException("Missing MeterID or MeterNo in request");
        }
        
        String meterNo = getReadingsByMeterIDAndFieldName.getMeterID().getMeterNo();
        String formattedBlockTemplateName = getReadingsByMeterIDAndFieldName.getFormattedBlockTemplateName();
        String lastReceived = getReadingsByMeterIDAndFieldName.getLastReceived();
        
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<FormattedBlock> formattedBlocks = mr_server.getReadingsByMeterIDAndFieldName(meterNo,
                                                                                     startDate.toGregorianCalendar(),
                                                                                     endDate.toGregorianCalendar(),
                                                                                     lastReceived, formattedBlockTemplateName);
        
        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getReadingsByMeterIDAndFieldNameResponse.setGetReadingsByMeterIDAndFieldNameResult(arrayOfFormattedBlock);
        return getReadingsByMeterIDAndFieldNameResponse;
    }
    
    @PayloadRoot(localPart = "GetLatestReadingByMeterIDAndFieldName", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload
    GetLatestReadingByMeterIDAndFieldNameResponse getLatestReadingByMeterIDAndFieldName(
            @RequestPayload GetLatestReadingByMeterIDAndFieldName getLatestReadingByMeterIDAndFieldName)
            throws MultispeakWebServiceException {
        GetLatestReadingByMeterIDAndFieldNameResponse response =
            objectFactory.createGetLatestReadingByMeterIDAndFieldNameResponse();
        
        if (getLatestReadingByMeterIDAndFieldName.getMeterID() == null) {
            throw new MultispeakWebServiceException("Missing MeterID or MeterNo in request");
        }

        String meterNo = getLatestReadingByMeterIDAndFieldName.getMeterID().getMeterNo();
        String formattedBlockTemplateName = getLatestReadingByMeterIDAndFieldName.getFormattedBlockTemplateName();
        FormattedBlock formattedBlock = mr_server.getLatestReadingByMeterIDAndFieldName(meterNo, 
                                                                                        formattedBlockTemplateName);
        response.setGetLatestReadingByMeterIDAndFieldNameResult(formattedBlock);
        return response;
    }
    
    @PayloadRoot(localPart = "GetLatestReadingByFieldName", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload
    GetLatestReadingByFieldNameResponse getLatestReadingByFieldName(@RequestPayload GetLatestReadingByFieldName getLatestReadingByFieldName)
            throws MultispeakWebServiceException {
        GetLatestReadingByFieldNameResponse getLatestReadingByFieldNameResponse =
            objectFactory.createGetLatestReadingByFieldNameResponse();

        String lastReceived = getLatestReadingByFieldName.getLastReceived();
        String formattedBlockTemplateName = getLatestReadingByFieldName.getFormattedBlockTemplateName();
        List<FormattedBlock> formattedBlocks = mr_server.getLatestReadingByFieldName(lastReceived, 
                                                                                     formattedBlockTemplateName);

        ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(formattedBlocks);
        getLatestReadingByFieldNameResponse.setGetLatestReadingByFieldNameResult(arrayOfFormattedBlock);
        return getLatestReadingByFieldNameResponse;
    }
    
    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse response = objectFactory.createServiceLocationChangedNotificationResponse();
        
        ArrayOfServiceLocation1 ArrOfServiceLocations = serviceLocationChangedNotification.getChangedServiceLocations();
        List<ServiceLocation> serviceLocationList = null != ArrOfServiceLocations ? ArrOfServiceLocations.getServiceLocation() : null;
        List<ErrorObject> errorObjects = mr_server
                .serviceLocationChangedNotification(ListUtils.emptyIfNull(serviceLocationList));
        
        ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects);
        response.setServiceLocationChangedNotificationResult(arrayOfErrorObject);
        return response;
    }
    
    @PayloadRoot(localPart = "MeterAddNotification", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload MeterAddNotificationResponse meterAddNotification(
            @RequestPayload MeterAddNotification meterAddNotification)
            throws MultispeakWebServiceException {
        MeterAddNotificationResponse response = objectFactory.createMeterAddNotificationResponse();

        List<MspMeter> mspMeters = new ArrayList<>();

        if (meterAddNotification.getAddedMeters() != null) {
            mspMeters = multispeakFuncs.getMspMeters(meterAddNotification.getAddedMeters());
        }
        List<ErrorObject> errorObjects = mr_server.meterAddNotification(ListUtils.emptyIfNull((mspMeters)));

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setMeterAddNotificationResult(arrayOfErrorObject);

        return response;
    }

    @PayloadRoot(localPart = "EstablishMeterGroup", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload EstablishMeterGroupResponse establishMeterGroup(
            @RequestPayload EstablishMeterGroup establishMeterGroup)
            throws MultispeakWebServiceException {
        EstablishMeterGroupResponse response = objectFactory.createEstablishMeterGroupResponse();

        MeterGroup meterGroup = establishMeterGroup.getMeterGroup();
        List<ErrorObject> errorObjects = mr_server.establishMeterGroup(meterGroup);

        ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects); 
        response.setEstablishMeterGroupResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "InsertMeterInMeterGroup", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload InsertMeterInMeterGroupResponse insertMeterInMeterGroup(
            @RequestPayload InsertMeterInMeterGroup insertMeterInMeterGroup) throws MultispeakWebServiceException {

        InsertMeterInMeterGroupResponse insertMeterInMeterGroupResponse = objectFactory
                .createInsertMeterInMeterGroupResponse();
        ArrayOfMeterID1 arrayOfMeterId1 = (null != insertMeterInMeterGroup.getMeterIDs()) ? insertMeterInMeterGroup
                .getMeterIDs() : null;

        if (arrayOfMeterId1 != null) {
            List<MeterID> meterIds = arrayOfMeterId1.getMeterID();
            String meterGroupId = insertMeterInMeterGroup.getMeterGroupID();
            List<ErrorObject> errorObjects = mr_server.insertMeterInMeterGroup(meterIds, meterGroupId);
            ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects); 
            insertMeterInMeterGroupResponse.setInsertMeterInMeterGroupResult(arrayOfErrorObject);
        }
        return insertMeterInMeterGroupResponse;
    }

    @PayloadRoot(localPart = "DeleteMeterGroup", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload DeleteMeterGroupResponse deleteMeterGroup(@RequestPayload DeleteMeterGroup deleteMeterGroup)
            throws MultispeakWebServiceException {
        DeleteMeterGroupResponse response = objectFactory.createDeleteMeterGroupResponse();

        String meterGroupIds = deleteMeterGroup.getMeterGroupID();
        ErrorObject errorObject = mr_server.deleteMeterGroup(meterGroupIds);
        ArrayOfErrorObject createArrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        if (errorObject != null) {
            createArrayOfErrorObject.getErrorObject().add(errorObject);
        }
        response.setDeleteMeterGroupResult(createArrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "RemoveMetersFromMeterGroup", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(
            @RequestPayload RemoveMetersFromMeterGroup removeMetersFromMeterGroup) throws MultispeakWebServiceException {
        RemoveMetersFromMeterGroupResponse response = objectFactory.createRemoveMetersFromMeterGroupResponse();

        String meterGroupIds = removeMetersFromMeterGroup.getMeterGroupID();
        ArrayOfMeterID1 arrayOfMeterId1 = (null != removeMetersFromMeterGroup.getMeterIDs()) ? removeMetersFromMeterGroup
                .getMeterIDs() : null;
        if (arrayOfMeterId1 != null) {
            List<ErrorObject> errorObjects = mr_server.removeMetersFromMeterGroup(arrayOfMeterId1.getMeterID(), meterGroupIds);
            ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects); 
            response.setRemoveMetersFromMeterGroupResult(arrayOfErrorObject);
        }
        return response;
    }
}