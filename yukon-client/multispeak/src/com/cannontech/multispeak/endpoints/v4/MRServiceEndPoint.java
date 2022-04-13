package com.cannontech.multispeak.endpoints.v4;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfMeterReading;
import com.cannontech.msp.beans.v4.ArrayOfMeterReading1;
import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetReadingsByDate;
import com.cannontech.msp.beans.v4.GetReadingsByDateResponse;
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
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = mr_server.getMethods();

        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }
    
    @PayloadRoot(localPart = "GetReadingsByDate", namespace = MR_V4_ENDPOINT_NAMESPACE)
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

        List<MeterReading> meterReading = mr_server.getReadingsByDate(startDate.toGregorianCalendar(),
                                                                 endDate.toGregorianCalendar(),
                                                                 lastReceived);

        
        ArrayOfMeterReading1 arrayOfMeterReading = objectFactory.createArrayOfMeterReading1();
        arrayOfMeterReading.getMeterReading().addAll(meterReading);
        response.setGetReadingsByDateResult(arrayOfMeterReading);
        return response;
    }
}