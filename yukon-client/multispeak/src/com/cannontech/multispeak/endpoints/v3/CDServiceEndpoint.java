
package com.cannontech.multispeak.endpoints.v3;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.CDDeviceAddNotification;
import com.cannontech.msp.beans.v3.CDDeviceAddNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceChangedNotification;
import com.cannontech.msp.beans.v3.CDDeviceChangedNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceExchangeNotification;
import com.cannontech.msp.beans.v3.CDDeviceExchangeNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceRemoveNotification;
import com.cannontech.msp.beans.v3.CDDeviceRemoveNotificationResponse;
import com.cannontech.msp.beans.v3.CDDeviceRetireNotification;
import com.cannontech.msp.beans.v3.CDDeviceRetireNotificationResponse;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetCDMeterState;
import com.cannontech.msp.beans.v3.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v3.GetCDSupportedMeters;
import com.cannontech.msp.beans.v3.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNames;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
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
    PingURLResponse pingUrl(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        cd_server.pingURL();

        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        
        List<String> methodNames = cd_server.getMethods();
        response.setGetMethodsResult(multispeakFuncs.toArrayOfString(methodNames));
        return response;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainNamesResponse getDomainNames(
            @RequestPayload GetDomainNames getDomainNames) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainMembersResponse getDomainMembers(
            @RequestPayload GetDomainMembers getDomainMembers) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
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
    public @ResponsePayload GetModifiedCDMetersResponse getModifiedCDMeters(
            @RequestPayload GetModifiedCDMeters getModifiedCDMeters)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
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
        List<ConnectDisconnectEvent> cdEvents = (null != initiateConnectDisconnect.getCdEvents()) 
                ? initiateConnectDisconnect.getCdEvents().getConnectDisconnectEvent() : null;

        List<ErrorObject> errorObjects =
            cd_server.initiateConnectDisconnect(cdEvents,
                initiateConnectDisconnect.getResponseURL(), initiateConnectDisconnect.getTransactionID(),
                initiateConnectDisconnect.getExpirationTime());

        response.setInitiateConnectDisconnectResult(multispeakFuncs.toArrayOfErrorObject(errorObjects));
        return response;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
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

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CDDeviceAddNotificationResponse CDDeviceAddNotification(
            @RequestPayload CDDeviceAddNotification cdDeviceAddNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CDDeviceChangedNotificationResponse CDDeviceChangedNotification(
            @RequestPayload CDDeviceChangedNotification cdDeviceChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CDDeviceExchangeNotificationResponse CDDeviceExchangeNotification(
            @RequestPayload CDDeviceExchangeNotification cdDeviceExchangeNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CDDeviceRemoveNotificationResponse CDDeviceRemoveNotification(
            @RequestPayload CDDeviceRemoveNotification cdDeviceRemoveNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CDDeviceRetireNotificationResponse CDDeviceRetireNotification(
            @RequestPayload CDDeviceRetireNotification cdDeviceRetireNotification)
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
    public @ResponsePayload GetPublishMethodsResponse getPublishMethods()
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

    @PayloadRoot(localPart = "InitiateCDStateRequest", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateCDStateRequestResponse initiateCDStateRequest(
            @RequestPayload InitiateCDStateRequest initiateCDStateRequest)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateArmCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateArmCDDeviceResponse initiateArmCDDevice(
            @RequestPayload InitiateArmCDDevice initiateArmCDDevice)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateEnableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateEnableCDDeviceResponse initiateEnableCDDevice(
            @RequestPayload InitiateEnableCDDevice initiateEnableCDDevice)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateDisableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateDisableCDDeviceResponse initiateDisableCDDevice(
            @RequestPayload InitiateDisableCDDevice initiateDisableCDDevice)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}