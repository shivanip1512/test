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
import com.cannontech.msp.beans.v3.CancelODMonitoringRequestByObject;
import com.cannontech.msp.beans.v3.CancelODMonitoringRequestByObjectResponse;
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DisplayODMonitoringRequests;
import com.cannontech.msp.beans.v3.DisplayODMonitoringRequestsResponse;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllOutageDetectionDevices;
import com.cannontech.msp.beans.v3.GetAllOutageDetectionDevicesResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNames;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByMeterNo;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByStatus;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByStatusResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByType;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByTypeResponse;
import com.cannontech.msp.beans.v3.GetOutagedODDevices;
import com.cannontech.msp.beans.v3.GetOutagedODDevicesResponse;
import com.cannontech.msp.beans.v3.GetPublishMethods;
import com.cannontech.msp.beans.v3.GetPublishMethodsResponse;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByID;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByIDResponse;
import com.cannontech.msp.beans.v3.InitiateODEventRequestByObject;
import com.cannontech.msp.beans.v3.InitiateODEventRequestByObjectResponse;
import com.cannontech.msp.beans.v3.InitiateODEventRequestByServiceLocation;
import com.cannontech.msp.beans.v3.InitiateODEventRequestByServiceLocationResponse;
import com.cannontech.msp.beans.v3.InitiateODMonitoringRequestByObject;
import com.cannontech.msp.beans.v3.InitiateODMonitoringRequestByObjectResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.MeterChangedNotification;
import com.cannontech.msp.beans.v3.MeterChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ModifyODDataForOutageDetectionDevice;
import com.cannontech.msp.beans.v3.ModifyODDataForOutageDetectionDeviceResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.OutageEventChangedNotification;
import com.cannontech.msp.beans.v3.OutageEventChangedNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.RegisterForService;
import com.cannontech.msp.beans.v3.RegisterForServiceResponse;
import com.cannontech.msp.beans.v3.RequestRegistrationID;
import com.cannontech.msp.beans.v3.RequestRegistrationIDResponse;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.msp.beans.v3.UnregisterForService;
import com.cannontech.msp.beans.v3.UnregisterForServiceResponse;
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

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainMembersResponse getDomainMembers(
            @RequestPayload GetDomainMembers getDomainMembers) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainNamesResponse getDomainNames(
            @RequestPayload GetDomainNames getDomainNames) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllOutageDetectionDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAllOutageDetectionDevicesResponse getAllOutageDetectionDevices(
            @RequestPayload GetAllOutageDetectionDevices getAllOutageDetectionDevices)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetOutageDetectionDevicesByMeterNoResponse getOutageDetectionDevicesByMeterNo(
            @RequestPayload GetOutageDetectionDevicesByMeterNo getOutageDetectionDevicesByMeterNo)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetOutageDetectionDevicesByStatusResponse getOutageDetectionDevicesByStatus(
            @RequestPayload GetOutageDetectionDevicesByStatus GetOutageDetectionDevicesByStatus)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetOutageDetectionDevicesByTypeResponse getOutageDetectionDevicesByType(
            @RequestPayload GetOutageDetectionDevicesByType getOutageDetectionDevicesByType)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetOutagedODDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetOutagedODDevicesResponse getOutagedODDevices(
            @RequestPayload GetOutagedODDevices getOutagedODDevices)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ModifyODDataForOutageDetectionDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload ModifyODDataForOutageDetectionDeviceResponse modifyODDataForOutageDetectionDevice(
            @RequestPayload ModifyODDataForOutageDetectionDevice modifyODDataForOutageDetectionDevice)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DisplayODMonitoringRequests", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload DisplayODMonitoringRequestsResponse displayODMonitoringRequests(
            @RequestPayload DisplayODMonitoringRequests displayODMonitoringRequests)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CancelODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CancelODMonitoringRequestByObjectResponse cancelODMonitoringRequestByObject(
            @RequestPayload CancelODMonitoringRequestByObject cancelODMonitoringRequestByObject)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateODEventRequestByObjectResponse initiateODEventRequestByObject(
            @RequestPayload InitiateODEventRequestByObject initiateODEventRequestByObject)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByServiceLocation", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateODEventRequestByServiceLocationResponse initiateODEventRequestByServiceLocation(
            @RequestPayload InitiateODEventRequestByServiceLocation initiateODEventRequestByServiceLocation)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateODMonitoringRequestByObjectResponse initiateODMonitoringRequestByObject(
            @RequestPayload InitiateODMonitoringRequestByObject initiateODMonitoringRequestByObject)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload RequestRegistrationIDResponse requestRegistrationID(
            @RequestPayload RequestRegistrationID requestRegistrationID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload RegisterForServiceResponse registerForService(
            @RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload UnregisterForServiceResponse unregisterForService(
            @RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetPublishMethodsResponse getPublishMethods(
            @RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload OutageEventChangedNotificationResponse outageEventChangedNotification(
            @RequestPayload OutageEventChangedNotification outageEventChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}