
package com.cannontech.multispeak.endpoints.v3;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.CDDevice;
import com.cannontech.msp.beans.v3.CDDeviceAddNotification;
import com.cannontech.msp.beans.v3.CDDeviceAddNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceChangedNotification;
import com.cannontech.msp.beans.v3.CDDeviceChangedNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceExchange;
import com.cannontech.msp.beans.v3.CDDeviceExchangeNotification;
import com.cannontech.msp.beans.v3.CDDeviceExchangeNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceRemoveNotification;
import com.cannontech.msp.beans.v3.CDDeviceRemoveNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceRetireNotification;
import com.cannontech.msp.beans.v3.CDDeviceRetireNotificationResponse;
import com.cannontech.msp.beans.v3.CDState;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetCDMeterState;
import com.cannontech.msp.beans.v3.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v3.GetCDSupportedMeters;
import com.cannontech.msp.beans.v3.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetModifiedCDMeters;
import com.cannontech.msp.beans.v3.GetModifiedCDMetersResponse;
import com.cannontech.msp.beans.v3.GetPublishMethodsResponse;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByID;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByIDResponse;
import com.cannontech.msp.beans.v3.InitiateArmCDDevice;
import com.cannontech.msp.beans.v3.InitiateArmCDDeviceResponse;
import com.cannontech.msp.beans.v3.InitiateCDStateRequest;
import com.cannontech.msp.beans.v3.InitiateCDStateRequestResponse;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v3.InitiateDisableCDDevice;
import com.cannontech.msp.beans.v3.InitiateDisableCDDeviceResponse;
import com.cannontech.msp.beans.v3.InitiateEnableCDDevice;
import com.cannontech.msp.beans.v3.InitiateEnableCDDeviceResponse;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterChangedNotification;
import com.cannontech.msp.beans.v3.MeterChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.RegisterForService;
import com.cannontech.msp.beans.v3.RegisterForServiceResponse;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.RequestRegistrationID;
import com.cannontech.msp.beans.v3.RequestRegistrationIDResponse;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.msp.beans.v3.UnregisterForService;
import com.cannontech.msp.beans.v3.UnregisterForServiceResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.CD_Server;

/**
 * Endpoint class to service the requests for CD_Server wsdl methods
 * 
  */

@Endpoint
@RequestMapping("/soap/CD_ServerSoap")
public class CDServiceEndpoint {

    @Autowired private CD_Server cd_server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingUrl() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        cd_server.pingURL();

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        
        List<String> methodNames = cd_server.getMethods();
        response.setGetMethodsResult(multispeakFuncs.toArrayOfString(methodNames));
        return response;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames() throws MultispeakWebServiceException {
        GetDomainNamesResponse response = objectFactory.createGetDomainNamesResponse();
        
        List<String> domainNames = cd_server.getDomainNames();
        response.setGetDomainNamesResult(multispeakFuncs.toArrayOfString(domainNames));
        return response;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse response = objectFactory.createGetDomainMembersResponse();
        
        List<DomainMember> domainMembers = cd_server.getDomainMembers(getDomainMembers.getDomainName());
        
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        arrayOfDomainMember.getDomainMember().addAll(domainMembers);
        response.setGetDomainMembersResult(arrayOfDomainMember);
        return response;
    }

    @PayloadRoot(localPart = "GetCDSupportedMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetCDSupportedMetersResponse getCDSupportedMeters(@RequestPayload GetCDSupportedMeters getCDSupportedMeters)
            throws MultispeakWebServiceException {
        GetCDSupportedMetersResponse response = objectFactory.createGetCDSupportedMetersResponse();
        
        List<Meter> meters = cd_server.getCDSupportedMeters(getCDSupportedMeters.getLastReceived());

        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().addAll(meters);
        response.setGetCDSupportedMetersResult(arrayOfMeter);
        return response;
    }

    @PayloadRoot(localPart = "GetModifiedCDMeters", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetModifiedCDMetersResponse getModifiedCDMeters(@RequestPayload GetModifiedCDMeters getModifiedCDMeters)
            throws MultispeakWebServiceException {
        GetModifiedCDMetersResponse response = objectFactory.createGetModifiedCDMetersResponse();
        
        List<Meter> meters =
            cd_server.getModifiedCDMeters(getModifiedCDMeters.getPreviousSessionID(), getModifiedCDMeters.getLastReceived());
        
        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().addAll(meters);
        response.setGetModifiedCDMetersResult(arrayOfMeter);
        return response;
    }

    @PayloadRoot(localPart = "GetCDMeterState", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetCDMeterStateResponse getCDMeterState(@RequestPayload GetCDMeterState getCDMeterState)
            throws MultispeakWebServiceException {
        GetCDMeterStateResponse response = objectFactory.createGetCDMeterStateResponse();
        LoadActionCode loadActionCode = cd_server.getCDMeterState(getCDMeterState.getMeterNo());
        response.setGetCDMeterStateResult(loadActionCode);
        return response;
    }

    @PayloadRoot(localPart = "InitiateConnectDisconnect", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateConnectDisconnectResponse initiateConnectDisconnect(
            @RequestPayload InitiateConnectDisconnect initiateConnectDisconnect) throws MultispeakWebServiceException {
        InitiateConnectDisconnectResponse response = objectFactory.createInitiateConnectDisconnectResponse();
        List<ConnectDisconnectEvent> cdEvents = initiateConnectDisconnect.getCdEvents().getConnectDisconnectEvent();

        List<ErrorObject> errorObjects =
            cd_server.initiateConnectDisconnect(cdEvents,
                initiateConnectDisconnect.getResponseURL(), initiateConnectDisconnect.getTransactionID(),
                initiateConnectDisconnect.getExpirationTime());

        response.setInitiateConnectDisconnectResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse response =
            objectFactory.createServiceLocationChangedNotificationResponse();
        
        if (serviceLocationChangedNotification != null
            && serviceLocationChangedNotification.getChangedServiceLocations() != null) {
            List<ServiceLocation> serviceLocations = serviceLocationChangedNotification.getChangedServiceLocations().getServiceLocation();
            List<ErrorObject> errorObjects = cd_server.serviceLocationChangedNotification(serviceLocations);
            response.setServiceLocationChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        }       
        return response;
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    MeterChangedNotificationResponse meterChangedNotification(
            @RequestPayload MeterChangedNotification meterChangedNotification) throws MultispeakWebServiceException {
        MeterChangedNotificationResponse response = objectFactory.createMeterChangedNotificationResponse();
        
        List<Meter> meters = meterChangedNotification.getChangedMeters().getMeter();
        List<ErrorObject> errorObjects = cd_server.meterChangedNotification(meters);
        response.setMeterChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse response = objectFactory.createCustomerChangedNotificationResponse();
        List<Customer> customers = customerChangedNotification.getChangedCustomers().getCustomer();
        List<ErrorObject> errorObjects = cd_server.customerChangedNotification(customers);
        response.setCustomerChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CDDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CDDeviceAddNotificationResponse CDDeviceAddNotification(
            @RequestPayload CDDeviceAddNotification cdDeviceAddNotification) throws MultispeakWebServiceException {
        CDDeviceAddNotificationResponse response = objectFactory.createCDDeviceAddNotificationResponse();
        List<CDDevice> cdDevices = cdDeviceAddNotification.getAddedCDDs().getCDDevice();
        List<ErrorObject> errorObjects = cd_server.CDDeviceAddNotification(cdDevices);
        response.setCDDeviceAddNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CDDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CDDeviceChangedNotificationResponse CDDeviceChangedNotification(
            @RequestPayload CDDeviceChangedNotification cdDeviceChangedNotification)
            throws MultispeakWebServiceException {
        CDDeviceChangedNotificationResponse response = objectFactory.createCDDeviceChangedNotificationResponse();
        List<CDDevice> cdDevices = cdDeviceChangedNotification.getChangedCDDs().getCDDevice();
        List<ErrorObject> errorObjects = cd_server.CDDeviceChangedNotification(cdDevices);
        response.setCDDeviceChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CDDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CDDeviceExchangeNotificationResponse CDDeviceExchangeNotification(
            @RequestPayload CDDeviceExchangeNotification cdDeviceExchangeNotification)
            throws MultispeakWebServiceException {
        CDDeviceExchangeNotificationResponse response = objectFactory.createCDDeviceExchangeNotificationResponse();
        List<CDDeviceExchange> cdDeviceExchanges = cdDeviceExchangeNotification.getCDDChangeout().getCDDeviceExchange();
        List<ErrorObject> errorObjects = cd_server.CDDeviceExchangeNotification(cdDeviceExchanges);
        response.setCDDeviceExchangeNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CDDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CDDeviceRemoveNotificationResponse CDDeviceRemoveNotification(
            @RequestPayload CDDeviceRemoveNotification cdDeviceRemoveNotification) throws MultispeakWebServiceException {
        CDDeviceRemoveNotificationResponse response = objectFactory.createCDDeviceRemoveNotificationResponse();
        List<CDDevice> cdDevices= cdDeviceRemoveNotification.getRemovedCDDs().getCDDevice();
        List<ErrorObject> errorObjects = cd_server.CDDeviceRemoveNotification(cdDevices);
        response.setCDDeviceRemoveNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "CDDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CDDeviceRetireNotificationResponse CDDeviceRetireNotification(
            @RequestPayload CDDeviceRetireNotification cdDeviceRetireNotification) throws MultispeakWebServiceException {
        CDDeviceRetireNotificationResponse response = objectFactory.createCDDeviceRetireNotificationResponse();
        List<CDDevice> cdDevices = cdDeviceRetireNotification.getRetiredCDDs().getCDDevice();
        List<ErrorObject> errorObjects = cd_server.CDDeviceRetireNotification(cdDevices);
        response.setCDDeviceRetireNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RequestRegistrationIDResponse requestRegistrationID(@RequestPayload RequestRegistrationID requestRegistrationID)
            throws MultispeakWebServiceException {
        RequestRegistrationIDResponse response = objectFactory.createRequestRegistrationIDResponse();
        response.setRequestRegistrationIDResult(cd_server.requestRegistrationID());
        return response;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse response = objectFactory.createRegisterForServiceResponse();
        List<ErrorObject> errorObjects = cd_server.registerForService(registerForService.getRegistrationDetails());
        response.setRegisterForServiceResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse response = objectFactory.createUnregisterForServiceResponse();
        List<ErrorObject> errorObjects = cd_server.unregisterForService(unregisterForService.getRegistrationID());
        response.setUnregisterForServiceResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {
        GetRegistrationInfoByIDResponse response = objectFactory.createGetRegistrationInfoByIDResponse();
        RegistrationInfo registrationInfo = cd_server.getRegistrationInfoByID(getRegistrationInfoByID.getRegistrationID());
        response.setGetRegistrationInfoByIDResult(registrationInfo);
        return response;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods() throws MultispeakWebServiceException {
        GetPublishMethodsResponse response = objectFactory.createGetPublishMethodsResponse();
        List<String> publishMethods = cd_server.getPublishMethods();
        response.setGetPublishMethodsResult(multispeakFuncs.toArrayOfString(publishMethods));
        return response;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse response = objectFactory.createDomainMembersChangedNotificationResponse();
        List<DomainMember> domainMembers = domainMembersChangedNotification.getChangedDomainMembers().getDomainMember();
        List<ErrorObject> errorObjects = cd_server.domainMembersChangedNotification(domainMembers);
        response.setDomainMembersChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse response = objectFactory.createDomainNamesChangedNotificationResponse();
        List<DomainNameChange> domainNameChanges = domainNamesChangedNotification.getChangedDomainNames().getDomainNameChange();
        List<ErrorObject> errorObjects = cd_server.domainNamesChangedNotification(domainNameChanges);
        response.setDomainNamesChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "InitiateCDStateRequest", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateCDStateRequestResponse initiateCDStateRequest(@RequestPayload InitiateCDStateRequest initiateCDStateRequest)
            throws MultispeakWebServiceException {
        InitiateCDStateRequestResponse response = objectFactory.createInitiateCDStateRequestResponse();
        List<CDState> cdStates = initiateCDStateRequest.getStates().getCDState();
        List<ErrorObject> errorObjects =
            cd_server.initiateCDStateRequest(cdStates, initiateCDStateRequest.getResponseURL(),
                initiateCDStateRequest.getTransactionID(), initiateCDStateRequest.getExpirationTime());
        response.setInitiateCDStateRequestResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "InitiateArmCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateArmCDDeviceResponse initiateArmCDDevice(@RequestPayload InitiateArmCDDevice initiateArmCDDevice)
            throws MultispeakWebServiceException {
        InitiateArmCDDeviceResponse response = objectFactory.createInitiateArmCDDeviceResponse();
        List<CDState> cdStates = initiateArmCDDevice.getStates().getCDState();
        List<ErrorObject> errorObjects =
            cd_server.initiateArmCDDevice(cdStates, initiateArmCDDevice.getResponseURL(),
                initiateArmCDDevice.getTransactionID(), initiateArmCDDevice.getExpirationTime());
        response.setInitiateArmCDDeviceResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "InitiateEnableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateEnableCDDeviceResponse initiateEnableCDDevice(@RequestPayload InitiateEnableCDDevice initiateEnableCDDevice)
            throws MultispeakWebServiceException {
        InitiateEnableCDDeviceResponse response = objectFactory.createInitiateEnableCDDeviceResponse();
        List<CDState> cdStates = initiateEnableCDDevice.getStates().getCDState();
        List<ErrorObject> errorObjects =
            cd_server.initiateEnableCDDevice(cdStates, initiateEnableCDDevice.getResponseURL(),
                initiateEnableCDDevice.getTransactionID(), initiateEnableCDDevice.getExpirationTime());
        response.setInitiateEnableCDDeviceResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "InitiateDisableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateDisableCDDeviceResponse initiateDisableCDDevice(
            @RequestPayload InitiateDisableCDDevice initiateDisableCDDevice) throws MultispeakWebServiceException {
        InitiateDisableCDDeviceResponse response = objectFactory.createInitiateDisableCDDeviceResponse();
        List<CDState> cdStates = initiateDisableCDDevice.getStates().getCDState();
        List<ErrorObject> errorObjects =
            cd_server.initiateDisableCDDevice(cdStates, initiateDisableCDDevice.getResponseURL(),
                initiateDisableCDDevice.getTransactionID(), initiateDisableCDDevice.getExpirationTime());
        response.setInitiateDisableCDDeviceResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }
}