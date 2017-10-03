package com.cannontech.multispeak.endpoints.v5;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfFormattedBlock;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfMeterReading;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectID;
import com.cannontech.msp.beans.v5.mr_server.CancelEndDeviceEventMonitoring;
import com.cannontech.msp.beans.v5.mr_server.CancelEndDeviceEventMonitoringResponse;
import com.cannontech.msp.beans.v5.mr_server.ClearDisconnectedStatus;
import com.cannontech.msp.beans.v5.mr_server.ClearDisconnectedStatusResponse;
import com.cannontech.msp.beans.v5.mr_server.CreateMeterGroups;
import com.cannontech.msp.beans.v5.mr_server.CreateMeterGroupsResponse;
import com.cannontech.msp.beans.v5.mr_server.DeleteMeterGroups;
import com.cannontech.msp.beans.v5.mr_server.DeleteMeterGroupsResponse;
import com.cannontech.msp.beans.v5.mr_server.GetAMRSupportedMeters;
import com.cannontech.msp.beans.v5.mr_server.GetAMRSupportedMetersResponse;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadings;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByMeterIDs;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByMeterIDsAndReadingTypeCodesResponse;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByReadingTypeCodes;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsByReadingTypeCodesResponse;
import com.cannontech.msp.beans.v5.mr_server.GetLatestMeterReadingsResponse;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByDate;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByDateAndReadingTypeCodes;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByDateAndReadingTypeCodesResponse;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByDateResponse;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByMeterIDs;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByMeterIDsAndReadingTypeCodes;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByMeterIDsAndReadingTypeCodesResponse;
import com.cannontech.msp.beans.v5.mr_server.GetMeterReadingsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.mr_server.GetMethods;
import com.cannontech.msp.beans.v5.mr_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.mr_server.InitiateDemandReset;
import com.cannontech.msp.beans.v5.mr_server.InitiateDemandResetResponse;
import com.cannontech.msp.beans.v5.mr_server.InitiateEndDeviceEventMonitoring;
import com.cannontech.msp.beans.v5.mr_server.InitiateEndDeviceEventMonitoringResponse;
import com.cannontech.msp.beans.v5.mr_server.InitiateMeterReadingsByMeterIDs;
import com.cannontech.msp.beans.v5.mr_server.InitiateMeterReadingsByMeterIDsResponse;
import com.cannontech.msp.beans.v5.mr_server.InitiateMeterReadingsByReadingTypeCodes;
import com.cannontech.msp.beans.v5.mr_server.InitiateMeterReadingsByReadingTypeCodesResponse;
import com.cannontech.msp.beans.v5.mr_server.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v5.mr_server.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v5.mr_server.IsAMRMeter;
import com.cannontech.msp.beans.v5.mr_server.IsAMRMeterResponse;
import com.cannontech.msp.beans.v5.mr_server.ObjectFactory;
import com.cannontech.msp.beans.v5.mr_server.PingURL;
import com.cannontech.msp.beans.v5.mr_server.PingURLResponse;
import com.cannontech.msp.beans.v5.mr_server.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v5.mr_server.RemoveMetersFromMeterGroupResponse;
import com.cannontech.msp.beans.v5.mr_server.SetDisconnectedStatus;
import com.cannontech.msp.beans.v5.mr_server.SetDisconnectedStatusResponse;
import com.cannontech.msp.beans.v5.multispeak.EventMonitoringItem;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.MeterGroup;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.MR_Server;

/*
 * This class is the MR Service endpoint all requests will be processed from
 * here.
 */

@Endpoint("MRServiceEndpointV5")
@RequestMapping("/multispeak/v5/MR_Server")
public class MRServiceEndPoint {

    @Autowired private MR_Server mr_server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String MR_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/MR_Server";

    @PayloadRoot(localPart = "PingURL", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        
        mr_server.pingURL();
        
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = mr_server.getMethods();
        
        ArrayOfString arrayOfString = commonObjectFactory.createArrayOfString();
        arrayOfString.getTheString().addAll(methods);
        response.setArrayOfString(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "GetMeterReadingsByDate", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMeterReadingsByDateResponse getMeterReadingsByDate(
            @RequestPayload GetMeterReadingsByDate getMeterReadingsByDate) throws MultispeakWebServiceException {
        GetMeterReadingsByDateResponse response = objectFactory.createGetMeterReadingsByDateResponse();

        XMLGregorianCalendar startDate = getMeterReadingsByDate.getStartDate();
        XMLGregorianCalendar endDate = getMeterReadingsByDate.getEndDate();
        String lastReceived = getMeterReadingsByDate.getLastReceived();
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        List<MeterReading> meterReadings =
            mr_server.getMeterReadingsByDate(startDate.toGregorianCalendar(), endDate.toGregorianCalendar(), lastReceived);

        ArrayOfMeterReading arrayOfMeterReading = commonObjectFactory.createArrayOfMeterReading();
        arrayOfMeterReading.getMeterReading().addAll(meterReadings);
        response.setArrayOfMeterReading(arrayOfMeterReading);
        ;
        return response;
    }

    @PayloadRoot(localPart = "GetAMRSupportedMeters", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAMRSupportedMetersResponse getAMRSupportedMeters(
            @RequestPayload GetAMRSupportedMeters getAMRSupportedMeters) throws MultispeakWebServiceException {
        GetAMRSupportedMetersResponse response = objectFactory.createGetAMRSupportedMetersResponse();

        String lastReceived = getAMRSupportedMeters.getLastReceived();
        Meters meters = mr_server.getAMRSupportedMeters(lastReceived);
        response.setMeters(meters);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestMeterReadings", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLatestMeterReadingsResponse getLatestMeterReadings(
            @RequestPayload GetLatestMeterReadings getLatestReadings) throws MultispeakWebServiceException {
        GetLatestMeterReadingsResponse response = objectFactory.createGetLatestMeterReadingsResponse();
        String lastRecd = getLatestReadings.getLastReceived();
        List<MeterReading> meterReads = mr_server.getLatestMeterReadings(lastRecd);

        ArrayOfMeterReading arrayOfMeterReadingValue = commonObjectFactory.createArrayOfMeterReading();
        arrayOfMeterReadingValue.getMeterReading().addAll(meterReads);
        response.setArrayOfMeterReading(arrayOfMeterReadingValue);
        return response;
    }

    @PayloadRoot(localPart = "GetMeterReadingsByMeterIDs", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMeterReadingsByMeterIDsResponse getMeterReadingsByMeterIDs(
            @RequestPayload GetMeterReadingsByMeterIDs getReadingsByMeterNo) throws MultispeakWebServiceException {
        GetMeterReadingsByMeterIDsResponse response = objectFactory.createGetMeterReadingsByMeterIDsResponse();

        XMLGregorianCalendar startDate = getReadingsByMeterNo.getStartDate();
        XMLGregorianCalendar endDate = getReadingsByMeterNo.getEndDate();
        List<MeterID> meterIDs = null;
        if (getReadingsByMeterNo.getArrayOfMeterID() != null
            && getReadingsByMeterNo.getArrayOfMeterID().getMeterID() != null
            && getReadingsByMeterNo.getArrayOfMeterID().getMeterID().size() > 0) {
            meterIDs = getReadingsByMeterNo.getArrayOfMeterID().getMeterID();
        }
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }
        List<MeterReading> meterReads =
            mr_server.getMeterReadingsByMeterIDs(meterIDs, startDate.toGregorianCalendar(), endDate.toGregorianCalendar());

        ArrayOfMeterReading arrayOfMeterReadingValue = commonObjectFactory.createArrayOfMeterReading();
        arrayOfMeterReadingValue.getMeterReading().addAll(meterReads);
        response.setArrayOfMeterReading(arrayOfMeterReadingValue);
        return response;
    }
    
    @PayloadRoot(localPart = "GetLatestMeterReadingsByMeterIDs", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetLatestMeterReadingsByMeterIDsResponse getLatestReadingByMeterNo(
            @RequestPayload GetLatestMeterReadingsByMeterIDs getLatestMeterReadingsByMeterIDs) throws MultispeakWebServiceException {
        GetLatestMeterReadingsByMeterIDsResponse getLatestReadingByMeterNoResponse =
            objectFactory.createGetLatestMeterReadingsByMeterIDsResponse();
        List<MeterID> meterIDs = null;
        if (getLatestMeterReadingsByMeterIDs.getArrayOfMeterID() != null
            && getLatestMeterReadingsByMeterIDs.getArrayOfMeterID().getMeterID() != null
            && getLatestMeterReadingsByMeterIDs.getArrayOfMeterID().getMeterID().size() > 0) {
            meterIDs = getLatestMeterReadingsByMeterIDs.getArrayOfMeterID().getMeterID();
        }
        List<MeterReading> meterReads = mr_server.getLatestMeterReadingsByMeterIDs(meterIDs);
        
        ArrayOfMeterReading arrayOfMeterReading = commonObjectFactory.createArrayOfMeterReading();
        arrayOfMeterReading.getMeterReading().addAll(meterReads);
        getLatestReadingByMeterNoResponse.setArrayOfMeterReading(arrayOfMeterReading);
        return getLatestReadingByMeterNoResponse;
    }
    

    @PayloadRoot(localPart = "GetMeterReadingsByDateAndReadingTypeCodes", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMeterReadingsByDateAndReadingTypeCodesResponse getMeterReadingsByDateAndReadingTypeCodes(
            @RequestPayload GetMeterReadingsByDateAndReadingTypeCodes getMeterReadingsByDateAndReadingTypeCodes)
            throws MultispeakWebServiceException {
        GetMeterReadingsByDateAndReadingTypeCodesResponse response =
                objectFactory.createGetMeterReadingsByDateAndReadingTypeCodesResponse();

        XMLGregorianCalendar startDate = getMeterReadingsByDateAndReadingTypeCodes.getStartDate();
        XMLGregorianCalendar endDate = getMeterReadingsByDateAndReadingTypeCodes.getEndDate();

        String lastReceived = getMeterReadingsByDateAndReadingTypeCodes.getLastReceived();
        ObjectID formattedBlockTemplateID = getMeterReadingsByDateAndReadingTypeCodes.getFormattedBlockTemplateID();

        List<ReadingTypeCode> readingTypeCodes =
            getMeterReadingsByDateAndReadingTypeCodes.getArrayOfReadingTypeCode() != null
                ? getMeterReadingsByDateAndReadingTypeCodes.getArrayOfReadingTypeCode().getReadingTypeCode() : null;

        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }
        List<FormattedBlock> blocks =
            mr_server.getMeterReadingsByDateAndReadingTypeCodes(startDate.toGregorianCalendar(),
                endDate.toGregorianCalendar(), readingTypeCodes, lastReceived, formattedBlockTemplateID);
        ArrayOfFormattedBlock arrayOfFormattedBlock = commonObjectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(blocks);
        response.setArrayOfFormattedBlock(arrayOfFormattedBlock);
        return response;

    }

    @PayloadRoot(localPart = "GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLatestMeterReadingsByMeterIDsAndReadingTypeCodesResponse getLatestMeterReadingsByMeterIDsAndReadingTypeCodes(
            @RequestPayload GetLatestMeterReadingsByMeterIDsAndReadingTypeCodes getLatestMeterReadingsByMeterIDsAndReadingTypeCodes)
            throws MultispeakWebServiceException {
        GetLatestMeterReadingsByMeterIDsAndReadingTypeCodesResponse response =
                objectFactory.createGetLatestMeterReadingsByMeterIDsAndReadingTypeCodesResponse();

        ObjectID formattedBlockTemplateID =
            getLatestMeterReadingsByMeterIDsAndReadingTypeCodes.getFormattedBlockTemplateID();

        List<ReadingTypeCode> readingTypeCodes =
            getLatestMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfReadingTypeCode() != null
                ? getLatestMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfReadingTypeCode().getReadingTypeCode()
                : null;

        List<MeterID> meterIDs =
            getLatestMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfMeterID() != null
                ? getLatestMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfMeterID().getMeterID() : null;

        FormattedBlock block =
            mr_server.getLatestMeterReadingsByMeterIDsAndReadingTypeCodes(meterIDs, readingTypeCodes,
                formattedBlockTemplateID);
        response.setFormattedBlock(block);
        return response;

    }

    @PayloadRoot(localPart = "GetLatestMeterReadingsByReadingTypeCodes", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLatestMeterReadingsByReadingTypeCodesResponse getLatestMeterReadingsByReadingTypeCodes(
            @RequestPayload GetLatestMeterReadingsByReadingTypeCodes getLatestMeterReadingsByReadingTypeCodes)
            throws MultispeakWebServiceException {
        GetLatestMeterReadingsByReadingTypeCodesResponse response =
                objectFactory.createGetLatestMeterReadingsByReadingTypeCodesResponse();
        ObjectID formattedBlockTemplateID = getLatestMeterReadingsByReadingTypeCodes.getFormattedBlockTemplateID();

        List<ReadingTypeCode> readingTypeCodes =
            getLatestMeterReadingsByReadingTypeCodes.getArrayOfReadingTypeCode() != null
                ? getLatestMeterReadingsByReadingTypeCodes.getArrayOfReadingTypeCode().getReadingTypeCode() : null;

        String lastReceived = getLatestMeterReadingsByReadingTypeCodes.getLastReceived();

        List<FormattedBlock> blocks =
            mr_server.getLatestMeterReadingsByReadingTypeCodes(readingTypeCodes, lastReceived, formattedBlockTemplateID);
        ArrayOfFormattedBlock arrayOfFormattedBlock = commonObjectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(blocks);
        response.setArrayOfFormattedBlock(arrayOfFormattedBlock);
        return response;

    }

    @PayloadRoot(localPart = "GetMeterReadingsByMeterIDsAndReadingTypeCodes", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMeterReadingsByMeterIDsAndReadingTypeCodesResponse getMeterReadingsByMeterIDsAndReadingTypeCodes(
            @RequestPayload GetMeterReadingsByMeterIDsAndReadingTypeCodes getMeterReadingsByMeterIDsAndReadingTypeCodes)
            throws MultispeakWebServiceException {
        GetMeterReadingsByMeterIDsAndReadingTypeCodesResponse response =
                objectFactory.createGetMeterReadingsByMeterIDsAndReadingTypeCodesResponse();

        XMLGregorianCalendar startDate = getMeterReadingsByMeterIDsAndReadingTypeCodes.getStartDate();
        XMLGregorianCalendar endDate = getMeterReadingsByMeterIDsAndReadingTypeCodes.getEndDate();
        
        if (startDate == null || endDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        ObjectID formattedBlockTemplateID = getMeterReadingsByMeterIDsAndReadingTypeCodes.getFormattedBlockTemplateID();

        List<ReadingTypeCode> readingTypeCodes =
            getMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfReadingTypeCode() != null
                ? getMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfReadingTypeCode().getReadingTypeCode() : null;

        List<MeterID> meterIDs =
            getMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfMeterID() != null
                ? getMeterReadingsByMeterIDsAndReadingTypeCodes.getArrayOfMeterID().getMeterID() : null;

        List<FormattedBlock> blocks =
            mr_server.getMeterReadingsByMeterIDsAndReadingTypeCodes(meterIDs, startDate.toGregorianCalendar(),
                endDate.toGregorianCalendar(), readingTypeCodes, formattedBlockTemplateID);

        ArrayOfFormattedBlock arrayOfFormattedBlock = commonObjectFactory.createArrayOfFormattedBlock();
        arrayOfFormattedBlock.getFormattedBlock().addAll(blocks);
        response.setArrayOfFormattedBlock(arrayOfFormattedBlock);
        return response;

    }
    
    @PayloadRoot(localPart = "InitiateMeterReadingsByMeterIDs", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateMeterReadingsByMeterIDsResponse initiateMeterReadingsByMeterIDs(
            @RequestPayload InitiateMeterReadingsByMeterIDs initiateMeterReadingsByMeterIDs)
            throws MultispeakWebServiceException {
        InitiateMeterReadingsByMeterIDsResponse initiateMeterReadingsByMeterIDsResponse = objectFactory.createInitiateMeterReadingsByMeterIDsResponse();

        XMLGregorianCalendar expirationTime = initiateMeterReadingsByMeterIDs.getExpirationTime();
        String responseURL = initiateMeterReadingsByMeterIDs.getResponseURL();
        String transactionID = initiateMeterReadingsByMeterIDs.getTransactionID();
        List<MeterID> meterIDs = (initiateMeterReadingsByMeterIDs.getArrayOfMeterID() != null)
                ? initiateMeterReadingsByMeterIDs.getArrayOfMeterID().getMeterID() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.initiateMeterReadingsByMeterIDs(meterIDs,
                        responseURL, transactionID, expirationTime));

        return initiateMeterReadingsByMeterIDsResponse;
    }
    
    @PayloadRoot(localPart = "InitiateMeterReadingsByReadingTypeCodes", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateMeterReadingsByReadingTypeCodesResponse initiateMeterReadingsByReadingTypeCodes(
            @RequestPayload InitiateMeterReadingsByReadingTypeCodes initiateMeterReadingsByReadingTypeCodes)
            throws MultispeakWebServiceException {
        InitiateMeterReadingsByReadingTypeCodesResponse response = objectFactory.createInitiateMeterReadingsByReadingTypeCodesResponse();

        XMLGregorianCalendar expirationTime = initiateMeterReadingsByReadingTypeCodes.getExpirationTime();
        String responseURL = initiateMeterReadingsByReadingTypeCodes.getResponseURL();
        String transactionId = initiateMeterReadingsByReadingTypeCodes.getTransactionID();
        ObjectID formattedBlockTemplateID = initiateMeterReadingsByReadingTypeCodes.getFormattedBlockTemplateID();
        
        List<MeterID> meterIDs = (initiateMeterReadingsByReadingTypeCodes.getArrayOfMeterID() != null) 
                ? initiateMeterReadingsByReadingTypeCodes.getArrayOfMeterID().getMeterID() : null;

        if (CollectionUtils.isNotEmpty(meterIDs)) {
            List<ReadingTypeCode> readingTypeCodes = initiateMeterReadingsByReadingTypeCodes.getArrayOfReadingTypeCode() != null 
                    ? initiateMeterReadingsByReadingTypeCodes.getArrayOfReadingTypeCode().getReadingTypeCode() : null;

            multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.initiateMeterReadingsByReadingTypeCodes(meterIDs,
                                                                     responseURL, readingTypeCodes, transactionId,
                                                                     expirationTime, formattedBlockTemplateID));
        }
        
        return response;
    }
    
    @PayloadRoot(localPart = "InitiateDemandReset", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateDemandResetResponse initiateDemandReset(
            @RequestPayload InitiateDemandReset initiateDemandReset)
            throws MultispeakWebServiceException {
        InitiateDemandResetResponse response = objectFactory.createInitiateDemandResetResponse();

        XMLGregorianCalendar expirationTime = initiateDemandReset.getExpirationTime();
        String responseURL = initiateDemandReset.getResponseURL();
        String transactionId = initiateDemandReset.getTransactionID();

        // TODO Currently meterID are taken from this
        // demandResetEvents.get(0).getMeterIDs().getMeterID().get(0).getMeterName()
        // Other option is:
        // demandResetEvents.get(0).getMeterGroups().getElectricMeterGroups().getMeterGroup().get(0).getMeterIDs().getMeterID().get(0).getMeterName();
                
        List<MeterID> meterIDList = Optional.ofNullable(initiateDemandReset.getArrayOfDemandResetEvent()) //get ArrayOfDemandResetEvent if it exists
                .map(array -> array.getDemandResetEvent())                    //get list of events (if it exists)
                .orElse(new ArrayList<>())                                    //if it doesn't exist, just use an empty list
                .stream()                                                     //convert to stream
                .flatMap(demandResetEvent -> demandResetEvent.getMeterIDs()   //flatten meter id lists into a single stream
                                                             .getMeterID()
                                                             .stream())
                .collect(Collectors.toList());                                //collect to list

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.initiateDemandReset(meterIDList, responseURL,
            transactionId, expirationTime));

        return response;
    }
    
    @PayloadRoot(localPart = "IsAMRMeter", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload IsAMRMeterResponse isAMRMeter(@RequestPayload IsAMRMeter isAMRMeter)
            throws MultispeakWebServiceException {
        IsAMRMeterResponse isAMRMeterResponse = objectFactory.createIsAMRMeterResponse();

        String meterNo = isAMRMeter.getMeterID().getMeterName();
        boolean response = mr_server.isAMRMeter(meterNo);
        isAMRMeterResponse.setBoolean(response);
        return isAMRMeterResponse;
    }
    
    @PayloadRoot(localPart = "InitiateEndDeviceEventMonitoring", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateEndDeviceEventMonitoringResponse initiateEndDeviceEventMonitoring(
            @RequestPayload InitiateEndDeviceEventMonitoring initiateEndDeviceEventMonitoring)
            throws MultispeakWebServiceException {
        InitiateEndDeviceEventMonitoringResponse response = objectFactory.createInitiateEndDeviceEventMonitoringResponse();

        List<EventMonitoringItem> eventMonitoringItem = (null == initiateEndDeviceEventMonitoring.getArrayOfEventMonitoringItem()) ? null
                : (null != initiateEndDeviceEventMonitoring.getArrayOfEventMonitoringItem().getEventMonitoringItem())
                ? initiateEndDeviceEventMonitoring.getArrayOfEventMonitoringItem().getEventMonitoringItem()
                        : null;

        if (eventMonitoringItem == null) {
            throw new MultispeakWebServiceException("EventMonitoringItem not available.");
        }
        List<MeterID> meterID = new ArrayList<MeterID>();
       
        for (EventMonitoringItem item : eventMonitoringItem) {
            List<MeterID> meterIDs = (item.getMeterIDs() != null) ? item.getMeterIDs().getMeterID()
                    : null;
            if (meterIDs != null) {
                for (MeterID meter : meterIDs) {
                    meterID.add(meter);
                }
            }
        }

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.initiateEndDeviceEventMonitoring(meterID));
        return response;
    }
    
    @PayloadRoot(localPart = "CancelEndDeviceEventMonitoring", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CancelEndDeviceEventMonitoringResponse cancelEndDeviceEventMonitoring(
            @RequestPayload CancelEndDeviceEventMonitoring cancelEndDeviceEventMonitoring)
            throws MultispeakWebServiceException {
        CancelEndDeviceEventMonitoringResponse cancelEndDeviceEventMonitoringResponse = objectFactory.createCancelEndDeviceEventMonitoringResponse();

        // TODO The eventMonitoringItemId should correspond to the referableID
        // received from the initiateEndDeviceEventMonitoring request
        // This change is required to support keeping track of the request’s
        // referableId
        String meterID = (null != cancelEndDeviceEventMonitoring.getEventMonitoringItemID()) 
                ? cancelEndDeviceEventMonitoring.getEventMonitoringItemID() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.cancelEndDeviceEventMonitoring(meterID));
        return cancelEndDeviceEventMonitoringResponse;
    }
    
    @PayloadRoot(localPart = "SetDisconnectedStatus", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload SetDisconnectedStatusResponse setDisconnectedStatus(
            @RequestPayload SetDisconnectedStatus setDisconnectedStatus)
            throws MultispeakWebServiceException {
        SetDisconnectedStatusResponse response = objectFactory.createSetDisconnectedStatusResponse();

        List<MeterID> meterIDs = (setDisconnectedStatus.getArrayOfMeterID() != null)
                                 ? setDisconnectedStatus.getArrayOfMeterID().getMeterID() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.setDisconnectedStatus(meterIDs));

        return response;
    }

    @PayloadRoot(localPart = "ClearDisconnectedStatus", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload ClearDisconnectedStatusResponse clearDisconnectedStatus(
            @RequestPayload ClearDisconnectedStatus clearDisconnectedStatus)
            throws MultispeakWebServiceException {
        ClearDisconnectedStatusResponse response = objectFactory.createClearDisconnectedStatusResponse();

        List<MeterID> meterIDs = (clearDisconnectedStatus.getArrayOfMeterID() != null) 
                                 ? clearDisconnectedStatus.getArrayOfMeterID().getMeterID() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.clearDisconnectedStatus(meterIDs));
        return response;
    }

    @PayloadRoot(localPart = "DeleteMeterGroups", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload DeleteMeterGroupsResponse deleteMeterGroups(
            @RequestPayload DeleteMeterGroups deleteMeterGroups)
            throws MultispeakWebServiceException {
        DeleteMeterGroupsResponse response = objectFactory.createDeleteMeterGroupsResponse();

        List<ObjectID> objectIDs = (deleteMeterGroups.getArrayOfMeterGroupID() != null) 
                                   ? deleteMeterGroups.getArrayOfMeterGroupID().getMeterGroupID(): null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.deleteMeterGroups(objectIDs));
        return response;
    }

    @PayloadRoot(localPart = "RemoveMetersFromMeterGroup", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload RemoveMetersFromMeterGroupResponse removeMetersFromMeterGroup(
            @RequestPayload RemoveMetersFromMeterGroup removeMetersFromMeterGroup)
            throws MultispeakWebServiceException {
        RemoveMetersFromMeterGroupResponse response = objectFactory.createRemoveMetersFromMeterGroupResponse();

        String meterGroupId = (removeMetersFromMeterGroup.getMeterGroupID() != null) 
                              ? removeMetersFromMeterGroup.getMeterGroupID().getObjectGUID() : null;

        List<MeterID> meterIDs = (removeMetersFromMeterGroup.getArrayOfMeterID() != null) 
                                 ? removeMetersFromMeterGroup.getArrayOfMeterID().getMeterID() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.removeMetersFromMeterGroup(meterIDs, meterGroupId));

        return response;
    }

    @PayloadRoot(localPart = "CreateMeterGroups", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload CreateMeterGroupsResponse createMeterGroups(
            @RequestPayload CreateMeterGroups createMeterGroups)
            throws MultispeakWebServiceException {
        CreateMeterGroupsResponse response = objectFactory.createCreateMeterGroupsResponse();

        List<MeterGroup> meterGroups = (createMeterGroups.getArrayOfMeterGroup() != null) 
                                       ? createMeterGroups.getArrayOfMeterGroup().getMeterGroup() : null;

        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.createMeterGroups(meterGroups));

        return response;
    }

    @PayloadRoot(localPart = "InsertMeterInMeterGroup", namespace = MR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InsertMeterInMeterGroupResponse insertMeterInMeterGroup(
            @RequestPayload InsertMeterInMeterGroup insertMeterInMeterGroup)
            throws MultispeakWebServiceException {
        InsertMeterInMeterGroupResponse insertMeterInMeterGroupResponse = objectFactory.createInsertMeterInMeterGroupResponse();

        List<MeterID> meterIDs = (insertMeterInMeterGroup.getArrayOfMeterID() != null) 
                                 ? insertMeterInMeterGroup.getArrayOfMeterID().getMeterID() : null;

        String meterGroupID = (insertMeterInMeterGroup.getMeterGroupID() != null) 
                              ? insertMeterInMeterGroup.getMeterGroupID().getObjectGUID() : null;
                              
        multispeakFuncs.addErrorObjectsInResponseHeader(mr_server.insertMeterInMeterGroup(meterIDs,
                                                                  meterGroupID));

        return insertMeterInMeterGroupResponse;
    }
}