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
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.OD_Server;

/*
 * This class is the OD Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/soap/OD_ServerSoap")
public class ODServiceEndpoint {

    @Autowired private OD_Server od_server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingUrl() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        
        od_server.pingURL();
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
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

    @PayloadRoot(localPart = "GetAllOutageDetectionDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAllOutageDetectionDevices() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getOutageDetectionDevicesByMeterNo() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getOutageDetectionDevicesByStatus() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByType", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getOutageDetectionDevicesByType() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutagedODDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getOutagedODDevices() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ModifyODDataForOutageDetectionDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public void modifyODDataForOutageDetectionDevice() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DisplayODMonitoringRequests", namespace = MultispeakDefines.NAMESPACE_v3)
    public void displayODMonitoringRequests() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CancelODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public void cancelODMonitoringRequestByObject() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateODEventRequestByObject() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByServiceLocation", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateODEventRequestByServiceLocation() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateODMonitoringRequestByObject() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void customerChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void serviceLocationChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void requestRegistrationID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public void registerForService() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public void unregisterForService() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getRegistrationInfoByID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getPublishMethods() throws MultispeakWebServiceException {
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

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void outageEventChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}