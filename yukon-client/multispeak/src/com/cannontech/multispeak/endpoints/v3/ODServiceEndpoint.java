package com.cannontech.multispeak.endpoints.v3;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.OD_Server;

/*
 * This class is the OD Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/multispeak/v3/OD_Server")
public class ODServiceEndpoint {

    @Autowired private OD_Server od_server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingUrl(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        
        od_server.pingURL();
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        
        List<String> methods = od_server.getMethods();
        
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "InitiateOutageDetectionEventRequest", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateOutageDetectionEventRequestResponse initiateOutageDetectionEventRequest(
            @RequestPayload InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest)
            throws MultispeakWebServiceException {
        InitiateOutageDetectionEventRequestResponse response = objectFactory.createInitiateOutageDetectionEventRequestResponse();

        List<String> meterNumbers = (null != initiateOutageDetectionEventRequest.getMeterNos()) ? 
                initiateOutageDetectionEventRequest.getMeterNos().getString() : null;
        XMLGregorianCalendar xmlRequestDate = initiateOutageDetectionEventRequest.getRequestDate();

        if (xmlRequestDate == null) {
            throw new MultispeakWebServiceException("Invalid date/time.");
        }

        Date requestDate = xmlRequestDate.toGregorianCalendar().getTime();
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        String responseURL = initiateOutageDetectionEventRequest.getResponseURL();
        String transactionID = initiateOutageDetectionEventRequest.getTransactionID();
        Float expirationTime = initiateOutageDetectionEventRequest.getExpirationTime();

        List<ErrorObject> errorObjects = od_server.initiateOutageDetectionEventRequest(meterNumbers, requestDateTime,
                                                                                       responseURL, transactionID, expirationTime);
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateOutageDetectionEventRequestResult(arrayOfErrorObject);
        return response;
    }
}