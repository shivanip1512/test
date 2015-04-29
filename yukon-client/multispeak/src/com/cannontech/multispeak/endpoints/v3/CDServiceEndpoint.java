
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
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetCDMeterState;
import com.cannontech.msp.beans.v3.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v3.GetCDSupportedMeters;
import com.cannontech.msp.beans.v3.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURLResponse;
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
    public void getDomainNames() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getDomainMembers() throws MultispeakWebServiceException {
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
    public void getModifiedCDMeters() throws MultispeakWebServiceException {
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
    public void serviceLocationChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "MeterChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void meterChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void customerChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void CDDeviceAddNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void CDDeviceChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void CDDeviceExchangeNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void CDDeviceRemoveNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CDDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void CDDeviceRetireNotification() throws MultispeakWebServiceException {
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

    @PayloadRoot(localPart = "InitiateCDStateRequest", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateCDStateRequest() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateArmCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateArmCDDevice() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateEnableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateEnableCDDevice() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateDisableCDDevice", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateDisableCDDevice() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}