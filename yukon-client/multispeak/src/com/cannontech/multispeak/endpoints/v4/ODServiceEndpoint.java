package com.cannontech.multispeak.endpoints.v4;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfErrorObject;
import com.cannontech.msp.beans.v4.ArrayOfMeterID1;
import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v4.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.OD_Server;

/*
 * This class is the OD Service endpoint all requests will be processed from here.
 */

@Endpoint("ODServiceEndpointV4")
@RequestMapping("/multispeak/v4/OD_Server")
public class ODServiceEndpoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private OD_Server od_server;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String OD_V4_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v4;

    @PayloadRoot(localPart = "PingURL", namespace = OD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        od_server.pingURL();

        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = OD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = od_server.getMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }
    
    @PayloadRoot(localPart = "InitiateOutageDetectionEventRequest", namespace = OD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateOutageDetectionEventRequestResponse initiateOutageDetectionEventRequest(
            @RequestPayload InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest)
            throws MultispeakWebServiceException {
        InitiateOutageDetectionEventRequestResponse response = objectFactory.createInitiateOutageDetectionEventRequestResponse();

        ArrayOfMeterID1 arrOfMeterIds = initiateOutageDetectionEventRequest.getMeterIDs();
        if (arrOfMeterIds != null) {
            List<MeterID> meterIds = null != arrOfMeterIds.getMeterID() ? arrOfMeterIds.getMeterID() : null;
            if (CollectionUtils.isNotEmpty(meterIds)) {
                String responseURL = initiateOutageDetectionEventRequest.getResponseURL();
                String transactionId = initiateOutageDetectionEventRequest.getTransactionID();

                List<ErrorObject> errorObjects = od_server.initiateOutageDetectionEventRequest(meterIds,
                        responseURL, transactionId);

                ArrayOfErrorObject arrayOfErrorObject = multispeakFuncs.toArrayOfErrorObject(errorObjects);
                response.setInitiateOutageDetectionEventRequestResult(arrayOfErrorObject);
            }
        }
        return response;

    }
}