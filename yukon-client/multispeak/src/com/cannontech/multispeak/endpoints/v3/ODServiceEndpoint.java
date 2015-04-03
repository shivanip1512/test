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

import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfObjectRef;
import com.cannontech.msp.beans.v3.ArrayOfOutageDetectionDevice;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.CancelODMonitoringRequestByObject;
import com.cannontech.msp.beans.v3.CancelODMonitoringRequestByObjectResponse;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DisplayODMonitoringRequestsResponse;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllOutageDetectionDevices;
import com.cannontech.msp.beans.v3.GetAllOutageDetectionDevicesResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByMeterNo;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByStatus;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByStatusResponse;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByType;
import com.cannontech.msp.beans.v3.GetOutageDetectionDevicesByTypeResponse;
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
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterChangedNotification;
import com.cannontech.msp.beans.v3.MeterChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ModifyODDataForOutageDetectionDevice;
import com.cannontech.msp.beans.v3.ModifyODDataForOutageDetectionDeviceResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.OutageDetectDeviceStatus;
import com.cannontech.msp.beans.v3.OutageDetectDeviceType;
import com.cannontech.msp.beans.v3.OutageDetectionDevice;
import com.cannontech.msp.beans.v3.OutageEvent;
import com.cannontech.msp.beans.v3.OutageEventChangedNotification;
import com.cannontech.msp.beans.v3.OutageEventChangedNotificationResponse;
import com.cannontech.msp.beans.v3.PhaseCd;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.RegisterForService;
import com.cannontech.msp.beans.v3.RegisterForServiceResponse;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.RequestRegistrationIDResponse;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.msp.beans.v3.UnregisterForService;
import com.cannontech.msp.beans.v3.UnregisterForServiceResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
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
    public @ResponsePayload
    InitiateOutageDetectionEventRequestResponse initiateOutageDetectionEventRequest(
            @RequestPayload InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest)
            throws MultispeakWebServiceException {
        InitiateOutageDetectionEventRequestResponse response =
            objectFactory.createInitiateOutageDetectionEventRequestResponse();

        List<String> meterNumbers = initiateOutageDetectionEventRequest.getMeterNos().getString();
        XMLGregorianCalendar xmlRequestDate = initiateOutageDetectionEventRequest.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        String responseURL = initiateOutageDetectionEventRequest.getResponseURL();
        String transactionID = initiateOutageDetectionEventRequest.getTransactionID();
        Float expirationTime = initiateOutageDetectionEventRequest.getExpirationTime();
        
        List<ErrorObject> errorObjects =
            od_server.initiateOutageDetectionEventRequest(meterNumbers, requestDateTime, responseURL, transactionID,
                expirationTime);
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        response.setInitiateOutageDetectionEventRequestResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse getDomainMembersResponse = objectFactory.createGetDomainMembersResponse();
        
        String domainName = getDomainMembers.getDomainName();
        List<DomainMember> domainMembers = od_server.getDomainMembers(domainName);
        
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        arrayOfDomainMember.getDomainMember().addAll(domainMembers);
        getDomainMembersResponse.setGetDomainMembersResult(arrayOfDomainMember);
        return getDomainMembersResponse;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames() throws MultispeakWebServiceException {
        GetDomainNamesResponse getDomainNamesResponse = objectFactory.createGetDomainNamesResponse();
        
        List<String> domainNames = od_server.getDomainNames();
        
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(domainNames);
        getDomainNamesResponse.setGetDomainNamesResult(arrayOfString);
        return getDomainNamesResponse;
    }

    @PayloadRoot(localPart = "GetAllOutageDetectionDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllOutageDetectionDevicesResponse getAllOutageDetectionDevices(
            @RequestPayload GetAllOutageDetectionDevices getAllOutageDetectionDevices)
            throws MultispeakWebServiceException {
        GetAllOutageDetectionDevicesResponse getAllOutageDetectionDevicesResponse =
            objectFactory.createGetAllOutageDetectionDevicesResponse();
        
        String lastReceived = getAllOutageDetectionDevices.getLastReceived();
        List<OutageDetectionDevice> outageDetectionDevices = od_server.getAllOutageDetectionDevices(lastReceived);
        
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        arrayOfOutageDetectionDevice.getOutageDetectionDevice().addAll(outageDetectionDevices);
        getAllOutageDetectionDevicesResponse.setGetAllOutageDetectionDevicesResult(arrayOfOutageDetectionDevice);
        return getAllOutageDetectionDevicesResponse;
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByMeterNo", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetOutageDetectionDevicesByMeterNoResponse getOutageDetectionDevicesByMeterNo(
            @RequestPayload GetOutageDetectionDevicesByMeterNo getOutageDetectionDevicesByMeterNo)
            throws MultispeakWebServiceException {
        GetOutageDetectionDevicesByMeterNoResponse getOutageDetectionDevicesByMeterNoResponse =
            objectFactory.createGetOutageDetectionDevicesByMeterNoResponse();
        
        String meterNo = getOutageDetectionDevicesByMeterNo.getMeterNo();
        List<OutageDetectionDevice> outageDetectionDevices = od_server.getOutageDetectionDevicesByMeterNo(meterNo);

        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        arrayOfOutageDetectionDevice.getOutageDetectionDevice().addAll(outageDetectionDevices);
        getOutageDetectionDevicesByMeterNoResponse.setGetOutageDetectionDevicesByMeterNoResult(arrayOfOutageDetectionDevice);
        return getOutageDetectionDevicesByMeterNoResponse;
    }
    
    @PayloadRoot(localPart = "GetOutageDetectionDevicesByStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetOutageDetectionDevicesByStatusResponse getOutageDetectionDevicesByStatus(
            @RequestPayload GetOutageDetectionDevicesByStatus GetOutageDetectionDevicesByStatus)
            throws MultispeakWebServiceException {
        GetOutageDetectionDevicesByStatusResponse getOutageDetectionDevicesByStatusResponse =
            objectFactory.createGetOutageDetectionDevicesByStatusResponse();
        
        String lastReceived = GetOutageDetectionDevicesByStatus.getLastReceived();
        OutageDetectDeviceStatus oDDStatus = GetOutageDetectionDevicesByStatus.getODDStatus();
        List<OutageDetectionDevice> outageDetectionDevices =
            od_server.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);

        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        arrayOfOutageDetectionDevice.getOutageDetectionDevice().addAll(outageDetectionDevices);
        getOutageDetectionDevicesByStatusResponse.setGetOutageDetectionDevicesByStatusResult(arrayOfOutageDetectionDevice);
        return getOutageDetectionDevicesByStatusResponse;
    }

    @PayloadRoot(localPart = "GetOutageDetectionDevicesByType", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetOutageDetectionDevicesByTypeResponse getOutageDetectionDevicesByType(
            @RequestPayload GetOutageDetectionDevicesByType getOutageDetectionDevicesByType)
            throws MultispeakWebServiceException {
        GetOutageDetectionDevicesByTypeResponse getOutageDetectionDevicesByTypeResponse =
            objectFactory.createGetOutageDetectionDevicesByTypeResponse();
        
        String lastReceived = getOutageDetectionDevicesByType.getLastReceived();
        OutageDetectDeviceType oddType = getOutageDetectionDevicesByType.getODDType();
        List<OutageDetectionDevice> outageDetectionDevices =
            od_server.getOutageDetectionDevicesByType(oddType, lastReceived);
        
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        arrayOfOutageDetectionDevice.getOutageDetectionDevice().addAll(outageDetectionDevices);
        getOutageDetectionDevicesByTypeResponse.setGetOutageDetectionDevicesByTypeResult(arrayOfOutageDetectionDevice);
        return getOutageDetectionDevicesByTypeResponse;
    }

    @PayloadRoot(localPart = "GetOutagedODDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetOutagedODDevicesResponse getOutagedODDevices() throws MultispeakWebServiceException {
        GetOutagedODDevicesResponse getOutagedODDevicesResponse = objectFactory.createGetOutagedODDevicesResponse();
        
        List<OutageDetectionDevice> outageDetectionDevices = od_server.getOutagedODDevices();
        
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        arrayOfOutageDetectionDevice.getOutageDetectionDevice().addAll(outageDetectionDevices);
        getOutagedODDevicesResponse.setGetOutagedODDevicesResult(arrayOfOutageDetectionDevice);
        return getOutagedODDevicesResponse;
    }

    @PayloadRoot(localPart = "ModifyODDataForOutageDetectionDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ModifyODDataForOutageDetectionDeviceResponse modifyODDataForOutageDetectionDevice(
            @RequestPayload ModifyODDataForOutageDetectionDevice modifyODDataForOutageDetectionDevice)
            throws MultispeakWebServiceException {
        ModifyODDataForOutageDetectionDeviceResponse modifyODDataForOutageDetectionDeviceResponse =
            objectFactory.createModifyODDataForOutageDetectionDeviceResponse();
        
        OutageDetectionDevice odDevice = modifyODDataForOutageDetectionDevice.getODDevice();
        od_server.modifyODDataForOutageDetectionDevice(odDevice);
        return modifyODDataForOutageDetectionDeviceResponse;
    }

    @PayloadRoot(localPart = "DisplayODMonitoringRequests", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DisplayODMonitoringRequestsResponse displayODMonitoringRequests() throws MultispeakWebServiceException {
        DisplayODMonitoringRequestsResponse displayODMonitoringRequestsResponse =
            objectFactory.createDisplayODMonitoringRequestsResponse();
        
        List<ObjectRef> displayODMonitoringRequests = od_server.displayODMonitoringRequests();
        
        ArrayOfObjectRef arrayOfObjectRef = objectFactory.createArrayOfObjectRef();
        arrayOfObjectRef.getObjectRef().addAll(displayODMonitoringRequests);
        displayODMonitoringRequestsResponse.setDisplayODMonitoringRequestsResult(arrayOfObjectRef);
        return displayODMonitoringRequestsResponse;
    }

    @PayloadRoot(localPart = "CancelODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CancelODMonitoringRequestByObjectResponse cancelODMonitoringRequestByObject(
            @RequestPayload CancelODMonitoringRequestByObject cancelODMonitoringRequestByObject)
            throws MultispeakWebServiceException {
        CancelODMonitoringRequestByObjectResponse cancelODMonitoringRequestByObjectResponse =
            objectFactory.createCancelODMonitoringRequestByObjectResponse();
        
        List<ObjectRef> objectRefs = cancelODMonitoringRequestByObject.getObjectRef().getObjectRef();
        XMLGregorianCalendar xmlRequestDate = cancelODMonitoringRequestByObject.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        List<ErrorObject> errorObjects = od_server.cancelODMonitoringRequestByObject(objectRefs, requestDateTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        cancelODMonitoringRequestByObjectResponse.setCancelODMonitoringRequestByObjectResult(arrayOfErrorObject);
        return cancelODMonitoringRequestByObjectResponse;
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateODEventRequestByObjectResponse initiateODEventRequestByObject(
            @RequestPayload InitiateODEventRequestByObject initiateODEventRequestByObject)
            throws MultispeakWebServiceException {
        InitiateODEventRequestByObjectResponse initiateODEventRequestByObjectResponse =
            objectFactory.createInitiateODEventRequestByObjectResponse();
        
        float expirationTime = initiateODEventRequestByObject.getExpirationTime();
        String nounType = initiateODEventRequestByObject.getNounType();
        String objectName = initiateODEventRequestByObject.getObjectName();
        PhaseCd phaseCode = initiateODEventRequestByObject.getPhaseCode();
        String transactionID = initiateODEventRequestByObject.getTransactionID();
        String responseURL = initiateODEventRequestByObject.getResponseURL();
        XMLGregorianCalendar xmlRequestDate = initiateODEventRequestByObject.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        List<ErrorObject> errorObjects =
            od_server.initiateODEventRequestByObject(objectName, nounType, phaseCode, requestDateTime, responseURL,
                transactionID, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateODEventRequestByObjectResponse.setInitiateODEventRequestByObjectResult(arrayOfErrorObject);
        return initiateODEventRequestByObjectResponse;
    }

    @PayloadRoot(localPart = "InitiateODEventRequestByServiceLocation", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateODEventRequestByServiceLocationResponse initiateODEventRequestByServiceLocation(
            @RequestPayload InitiateODEventRequestByServiceLocation initiateODEventRequestByServiceLocation)
            throws MultispeakWebServiceException {
        InitiateODEventRequestByServiceLocationResponse initiateODEventRequestByServiceLocationResponse =
            objectFactory.createInitiateODEventRequestByServiceLocationResponse();
        
        float expirationTime = initiateODEventRequestByServiceLocation.getExpirationTime();
        List<String> servLocs = initiateODEventRequestByServiceLocation.getServLoc().getString();
        String transactionID = initiateODEventRequestByServiceLocation.getTransactionID();
        String responseURL = initiateODEventRequestByServiceLocation.getResponseURL();
        XMLGregorianCalendar xmlRequestDate = initiateODEventRequestByServiceLocation.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        List<ErrorObject> errorObjects =
            od_server.initiateODEventRequestByServiceLocation(servLocs, requestDateTime, responseURL, transactionID, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateODEventRequestByServiceLocationResponse.setInitiateODEventRequestByServiceLocationResult(arrayOfErrorObject);
        return initiateODEventRequestByServiceLocationResponse;
    }

    @PayloadRoot(localPart = "InitiateODMonitoringRequestByObject", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateODMonitoringRequestByObjectResponse initiateODMonitoringRequestByObject(
            @RequestPayload InitiateODMonitoringRequestByObject initiateODMonitoringRequestByObject)
            throws MultispeakWebServiceException {
        InitiateODMonitoringRequestByObjectResponse initiateODMonitoringRequestByObjectResponse =
            objectFactory.createInitiateODMonitoringRequestByObjectResponse();
        
        float expirationTime = initiateODMonitoringRequestByObject.getExpirationTime();
        String nounType = initiateODMonitoringRequestByObject.getNounType();
        String objectName = initiateODMonitoringRequestByObject.getObjectName();
        PhaseCd phaseCode = initiateODMonitoringRequestByObject.getPhaseCode();
        String transactionID = initiateODMonitoringRequestByObject.getTransactionID();
        String responseURL = initiateODMonitoringRequestByObject.getResponseURL();
        XMLGregorianCalendar xmlRequestDate = initiateODMonitoringRequestByObject.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        int periodicity = initiateODMonitoringRequestByObject.getPeriodicity();
        List<ErrorObject> errorObjects =
            od_server.initiateODMonitoringRequestByObject(objectName, nounType, phaseCode, periodicity,
                requestDateTime, responseURL, transactionID, expirationTime);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        initiateODMonitoringRequestByObjectResponse.setInitiateODMonitoringRequestByObjectResult(arrayOfErrorObject);
        return initiateODMonitoringRequestByObjectResponse;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse customerChangedNotificationResponse =
            objectFactory.createCustomerChangedNotificationResponse();
        
        List<Customer> customers = customerChangedNotification.getChangedCustomers().getCustomer();
        List<ErrorObject> errorObjects =
            od_server.customerChangedNotification(customers);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        customerChangedNotificationResponse.setCustomerChangedNotificationResult(arrayOfErrorObject);
        return customerChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceException {
        MeterChangedNotificationResponse meterChangedNotificationResponse =
            objectFactory.createMeterChangedNotificationResponse();
        
        List<Meter> meters = meterChangedNotification.getChangedMeters().getMeter();
        List<ErrorObject> errorObjects = od_server.meterChangedNotification(meters);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        meterChangedNotificationResponse.setMeterChangedNotificationResult(arrayOfErrorObject);
        return meterChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse serviceLocationChangedNotificationResponse =
            objectFactory.createServiceLocationChangedNotificationResponse();
        
        List<ServiceLocation> serviceLocations = serviceLocationChangedNotification.getChangedServiceLocations().getServiceLocation();
        List<ErrorObject> errorObjects = od_server.serviceLocationChangedNotification(serviceLocations);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        serviceLocationChangedNotificationResponse.setServiceLocationChangedNotificationResult(arrayOfErrorObject);
        return serviceLocationChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RequestRegistrationIDResponse requestRegistrationID() throws MultispeakWebServiceException {
        RequestRegistrationIDResponse requestRegistrationIDResponse =
            objectFactory.createRequestRegistrationIDResponse();
        
        String requestRegistrationIDResult = od_server.requestRegistrationID();
        requestRegistrationIDResponse.setRequestRegistrationIDResult(requestRegistrationIDResult);
        return requestRegistrationIDResponse;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse registerForServiceResponse = objectFactory.createRegisterForServiceResponse();
        RegistrationInfo registrationInfo = registerForService.getRegistrationDetails();
        List<ErrorObject> errorObjects = od_server.registerForService(registrationInfo);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        registerForServiceResponse.setRegisterForServiceResult(arrayOfErrorObject);
        return registerForServiceResponse;
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse unregisterForServiceResponse = objectFactory.createUnregisterForServiceResponse();
        
        String registrationID = unregisterForService.getRegistrationID();
        List<ErrorObject> errorObjects = od_server.unregisterForService(registrationID);
        
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        unregisterForServiceResponse.setUnregisterForServiceResult(arrayOfErrorObject);
        return unregisterForServiceResponse;
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {
        GetRegistrationInfoByIDResponse getRegistrationInfoByIDResponse =
            objectFactory.createGetRegistrationInfoByIDResponse();
        
        String registrationID = getRegistrationInfoByID.getRegistrationID();
        RegistrationInfo registrationInfo = od_server.getRegistrationInfoByID(registrationID);
        getRegistrationInfoByIDResponse.setGetRegistrationInfoByIDResult(registrationInfo);
        return getRegistrationInfoByIDResponse;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods(@RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        GetPublishMethodsResponse getPublishMethodsResponse = objectFactory.createGetPublishMethodsResponse();
        
        List<String> publishMethods = od_server.getPublishMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(publishMethods);
        getPublishMethodsResponse.setGetPublishMethodsResult(arrayOfString);
        return getPublishMethodsResponse;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse domainMembersChangedNotificationResponse =
            objectFactory.createDomainMembersChangedNotificationResponse();
        
        List<DomainMember> domainMembers = domainMembersChangedNotification.getChangedDomainMembers().getDomainMember();
        List<ErrorObject> errorObjects = od_server.domainMembersChangedNotification(domainMembers);

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
        List<ErrorObject> errorObjects =
            od_server.domainNamesChangedNotification(domainNameChanges);
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        domainNamesChangedNotificationResponse.setDomainNamesChangedNotificationResult(arrayOfErrorObject);
        return domainNamesChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    OutageEventChangedNotificationResponse outageEventChangedNotification(
            @RequestPayload OutageEventChangedNotification outageEventChangedNotification)
            throws MultispeakWebServiceException {
        OutageEventChangedNotificationResponse outageEventChangedNotificationResponse =
            objectFactory.createOutageEventChangedNotificationResponse();
        
        List<OutageEvent> outageEvents = outageEventChangedNotification.getOEvents().getOutageEvent();
        List<ErrorObject> errorObjects = od_server.outageEventChangedNotification(outageEvents);
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        arrayOfErrorObject.getErrorObject().addAll(errorObjects);
        outageEventChangedNotificationResponse.setOutageEventChangedNotificationResult(arrayOfErrorObject);
        return outageEventChangedNotificationResponse;
    }
}