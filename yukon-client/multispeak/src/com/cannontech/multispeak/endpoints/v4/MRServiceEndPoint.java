package com.cannontech.multispeak.endpoints.v4;

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
import com.cannontech.msp.beans.v4.ArrayOfMeterID1;
import com.cannontech.msp.beans.v4.ArrayOfMeterReading1;
import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterID;
import com.cannontech.msp.beans.v4.GetLatestReadingByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetLatestReadings;
import com.cannontech.msp.beans.v4.GetLatestReadingsResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v4.GetReadingsByMeterID;
import com.cannontech.msp.beans.v4.GetReadingsByMeterIDResponse;
import com.cannontech.msp.beans.v4.InitiateUsageMonitoring;
import com.cannontech.msp.beans.v4.InitiateUsageMonitoringResponse;
import com.cannontech.msp.beans.v4.IsAMRMeter;
import com.cannontech.msp.beans.v4.IsAMRMeterResponse;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
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

        if (errorObjects != null && !errorObjects.isEmpty()) {
            ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
            arrayOfErrorObject.getErrorObject().addAll(errorObjects);
            response.setInitiateUsageMonitoringResult(arrayOfErrorObject);
        }
        return response;
    }

}