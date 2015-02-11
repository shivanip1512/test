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

import com.cannontech.msp.beans.v3.ArrayOfCustomer;
import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfDomainNameChange;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.ArrayOfObjectRef;
import com.cannontech.msp.beans.v3.ArrayOfOutageDetectionDevice;
import com.cannontech.msp.beans.v3.ArrayOfOutageEvent;
import com.cannontech.msp.beans.v3.ArrayOfServiceLocation;
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
        ErrorObject[] errorObjects = od_server.pingURL();
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
        String[] methods = od_server.getMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        List<String> methodNameList = arrayOfString.getString();
        for (String methodName : methods) {
            methodNameList.add(methodName);
        }
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
        ArrayOfString arrOfMeterId = initiateOutageDetectionEventRequest.getMeterNos();
        List<String> meterIds = arrOfMeterId.getString();
        String[] allMeterIds = new String[meterIds.size()];
        int i = 0;
        for (String meterId : meterIds) {
            allMeterIds[i] = meterId;
            i++;
        }
        XMLGregorianCalendar xmlRequestDate = initiateOutageDetectionEventRequest.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        String responseURL = initiateOutageDetectionEventRequest.getResponseURL();
        String transactionID = initiateOutageDetectionEventRequest.getTransactionID();
        Float expirationTime = initiateOutageDetectionEventRequest.getExpirationTime();
        ErrorObject[] errorObjects =
            od_server.initiateOutageDetectionEventRequest(allMeterIds, requestDateTime, responseURL, transactionID,
                expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            for (ErrorObject errorObject : errorObjects) {
                arrOfErrorObj.getErrorObject().add(errorObject);
            }

        }
        response.setInitiateOutageDetectionEventRequestResult(arrOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse getDomainMembersResponse = objectFactory.createGetDomainMembersResponse();
        String domainName = getDomainMembers.getDomainName();
        DomainMember[] domainMemberArr = od_server.getDomainMembers(domainName);
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        List<DomainMember> domainMemberList = arrayOfDomainMember.getDomainMember();
        for (DomainMember domainMember : domainMemberArr) {
            domainMemberList.add(domainMember);
        }
        getDomainMembersResponse.setGetDomainMembersResult(arrayOfDomainMember);
        return getDomainMembersResponse;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames() throws MultispeakWebServiceException {
        GetDomainNamesResponse getDomainNamesResponse = objectFactory.createGetDomainNamesResponse();
        String[] domainNamesArr = od_server.getDomainNames();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        List<String> domainNameList = arrayOfString.getString();
        for (String domainName : domainNamesArr) {
            domainNameList.add(domainName);
        }
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
        OutageDetectionDevice[] outageDetectionDeviceArr = od_server.getAllOutageDetectionDevices(lastReceived);
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        List<OutageDetectionDevice> outageDetectionDeviceList = arrayOfOutageDetectionDevice.getOutageDetectionDevice();
        for (OutageDetectionDevice outageDetectionDevice : outageDetectionDeviceArr) {
            outageDetectionDeviceList.add(outageDetectionDevice);
        }
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
        OutageDetectionDevice[] outageDetectionDeviceArr = od_server.getOutageDetectionDevicesByMeterNo(meterNo);
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        List<OutageDetectionDevice> outageDetectionDeviceList = arrayOfOutageDetectionDevice.getOutageDetectionDevice();
        for (OutageDetectionDevice outageDetectionDevice : outageDetectionDeviceArr) {
            outageDetectionDeviceList.add(outageDetectionDevice);
        }
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
        OutageDetectionDevice[] outageDetectionDeviceArr =
            od_server.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        List<OutageDetectionDevice> outageDetectionDeviceList = arrayOfOutageDetectionDevice.getOutageDetectionDevice();
        for (OutageDetectionDevice outageDetectionDevice : outageDetectionDeviceArr) {
            outageDetectionDeviceList.add(outageDetectionDevice);
        }
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
        OutageDetectionDevice[] outageDetectionDeviceArr =
            od_server.getOutageDetectionDevicesByType(oddType, lastReceived);
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        List<OutageDetectionDevice> outageDetectionDeviceList = arrayOfOutageDetectionDevice.getOutageDetectionDevice();
        for (OutageDetectionDevice outageDetectionDevice : outageDetectionDeviceArr) {
            outageDetectionDeviceList.add(outageDetectionDevice);
        }
        getOutageDetectionDevicesByTypeResponse.setGetOutageDetectionDevicesByTypeResult(arrayOfOutageDetectionDevice);
        return getOutageDetectionDevicesByTypeResponse;
    }

    @PayloadRoot(localPart = "GetOutagedODDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetOutagedODDevicesResponse getOutagedODDevices() throws MultispeakWebServiceException {
        GetOutagedODDevicesResponse getOutagedODDevicesResponse = objectFactory.createGetOutagedODDevicesResponse();
        OutageDetectionDevice[] outageDetectionDeviceArr = od_server.getOutagedODDevices();
        ArrayOfOutageDetectionDevice arrayOfOutageDetectionDevice = objectFactory.createArrayOfOutageDetectionDevice();
        List<OutageDetectionDevice> outageDetectionDeviceList = arrayOfOutageDetectionDevice.getOutageDetectionDevice();
        for (OutageDetectionDevice outageDetectionDevice : outageDetectionDeviceArr) {
            outageDetectionDeviceList.add(outageDetectionDevice);
        }
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
        ObjectRef[] displayODMonitoringRequests = od_server.displayODMonitoringRequests();
        ArrayOfObjectRef arrayOfObjectRef = objectFactory.createArrayOfObjectRef();
        if (displayODMonitoringRequests != null) {
            List<ObjectRef> displayODMonitoringRequestsList = arrayOfObjectRef.getObjectRef();
            for (ObjectRef displayODMonitoringRequest : displayODMonitoringRequests) {
                displayODMonitoringRequestsList.add(displayODMonitoringRequest);
            }
        }
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
        ArrayOfObjectRef objectRef = cancelODMonitoringRequestByObject.getObjectRef();
        List<ObjectRef> objectRefList = objectRef.getObjectRef();
        XMLGregorianCalendar xmlRequestDate = cancelODMonitoringRequestByObject.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        ErrorObject[] errorObjects =
            od_server.cancelODMonitoringRequestByObject(objectRefList.toArray(new ObjectRef[objectRefList.size()]),
                requestDateTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        cancelODMonitoringRequestByObjectResponse.setCancelODMonitoringRequestByObjectResult(arrOfErrorObj);
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
        ErrorObject[] errorObjects =
            od_server.initiateODEventRequestByObject(objectName, nounType, phaseCode, requestDateTime, responseURL,
                transactionID, expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        initiateODEventRequestByObjectResponse.setInitiateODEventRequestByObjectResult(arrOfErrorObj);
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
        ArrayOfString servLoc = initiateODEventRequestByServiceLocation.getServLoc();
        List<String> servLocList = servLoc.getString();
        String transactionID = initiateODEventRequestByServiceLocation.getTransactionID();
        String responseURL = initiateODEventRequestByServiceLocation.getResponseURL();
        XMLGregorianCalendar xmlRequestDate = initiateODEventRequestByServiceLocation.getRequestDate();
        Date requestDate =  (xmlRequestDate != null) ? xmlRequestDate.toGregorianCalendar().getTime() : null;
        Calendar requestDateTime = Calendar.getInstance();
        requestDateTime.setTime(requestDate);
        ErrorObject[] errorObjects =
            od_server.initiateODEventRequestByServiceLocation(servLocList.toArray(new String[servLocList.size()]),
                requestDateTime, responseURL, transactionID, expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        initiateODEventRequestByServiceLocationResponse.setInitiateODEventRequestByServiceLocationResult(arrOfErrorObj);
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
        ErrorObject[] errorObjects =
            od_server.initiateODMonitoringRequestByObject(objectName, nounType, phaseCode, periodicity,
                requestDateTime, responseURL, transactionID, expirationTime);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        initiateODMonitoringRequestByObjectResponse.setInitiateODMonitoringRequestByObjectResult(arrOfErrorObj);
        return initiateODMonitoringRequestByObjectResponse;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse customerChangedNotificationResponse =
            objectFactory.createCustomerChangedNotificationResponse();
        ArrayOfCustomer changedCustomers = customerChangedNotification.getChangedCustomers();
        List<Customer> customerList = changedCustomers.getCustomer();
        ErrorObject[] errorObjects =
            od_server.customerChangedNotification(customerList.toArray(new Customer[customerList.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        customerChangedNotificationResponse.setCustomerChangedNotificationResult(arrOfErrorObj);
        return customerChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceException {
        MeterChangedNotificationResponse meterChangedNotificationResponse =
            objectFactory.createMeterChangedNotificationResponse();
        ArrayOfMeter changedMeters = meterChangedNotification.getChangedMeters();
        List<Meter> meterList = changedMeters.getMeter();
        ErrorObject[] errorObjects = od_server.meterChangedNotification(meterList.toArray(new Meter[meterList.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        meterChangedNotificationResponse.setMeterChangedNotificationResult(arrOfErrorObj);
        return meterChangedNotificationResponse;
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
        List<ServiceLocation> serviceLocationList = changedServiceLocations.getServiceLocation();
        ErrorObject[] errorObjects =
            od_server.serviceLocationChangedNotification(serviceLocationList.toArray(new ServiceLocation[serviceLocationList.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        serviceLocationChangedNotificationResponse.setServiceLocationChangedNotificationResult(arrOfErrorObj);
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
        ErrorObject[] errorObjects = od_server.registerForService(registrationInfo);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        registerForServiceResponse.setRegisterForServiceResult(arrOfErrorObj);
        return registerForServiceResponse;
    }

    /**
     * Un register For service request
     * 
     * @param UnregisterForService - request Object.
     * @return UnregisterForServiceResponse
     */

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse unregisterForServiceResponse = objectFactory.createUnregisterForServiceResponse();
        String registrationID = unregisterForService.getRegistrationID();
        ErrorObject[] errorObjects = od_server.unregisterForService(registrationID);
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        unregisterForServiceResponse.setUnregisterForServiceResult(arrOfErrorObj);
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
        String[] publishMethodsArr = od_server.getPublishMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        if (publishMethodsArr != null) {
            List<String> publishMethodsList = arrayOfString.getString();
            for (String publishMethod : publishMethodsArr) {
                publishMethodsList.add(publishMethod);
            }
        }
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
        ArrayOfDomainMember changedDomainMembers = domainMembersChangedNotification.getChangedDomainMembers();
        List<DomainMember> domainMemberList = changedDomainMembers.getDomainMember();
        ErrorObject[] errorObjects =
            od_server.domainMembersChangedNotification(domainMemberList.toArray(new DomainMember[domainMemberList.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        domainMembersChangedNotificationResponse.setDomainMembersChangedNotificationResult(arrOfErrorObj);
        return domainMembersChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse domainNamesChangedNotificationResponse =
            objectFactory.createDomainNamesChangedNotificationResponse();
        ArrayOfDomainNameChange changedDomainNames = domainNamesChangedNotification.getChangedDomainNames();
        List<DomainNameChange> domainNameChange = changedDomainNames.getDomainNameChange();
        ErrorObject[] errorObjects =
            od_server.domainNamesChangedNotification(domainNameChange.toArray(new DomainNameChange[domainNameChange.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        domainNamesChangedNotificationResponse.setDomainNamesChangedNotificationResult(arrOfErrorObj);
        return domainNamesChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    OutageEventChangedNotificationResponse outageEventChangedNotification(
            @RequestPayload OutageEventChangedNotification outageEventChangedNotification)
            throws MultispeakWebServiceException {
        OutageEventChangedNotificationResponse outageEventChangedNotificationResponse =
            objectFactory.createOutageEventChangedNotificationResponse();
        ArrayOfOutageEvent oEvents = outageEventChangedNotification.getOEvents();
        List<OutageEvent> outageEvent = oEvents.getOutageEvent();
        ErrorObject[] errorObjects =
            od_server.outageEventChangedNotification(outageEvent.toArray(new OutageEvent[outageEvent.size()]));
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        if (errorObjects != null) {
            List<ErrorObject> errorObjList = arrOfErrorObj.getErrorObject();
            for (ErrorObject errorObject : errorObjects) {
                errorObjList.add(errorObject);
            }
        }
        outageEventChangedNotificationResponse.setOutageEventChangedNotificationResult(arrOfErrorObj);
        return outageEventChangedNotificationResponse;
    }

}
