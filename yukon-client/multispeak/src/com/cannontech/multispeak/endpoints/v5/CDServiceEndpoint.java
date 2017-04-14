package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.cd_server.GetAllCDDevices;
import com.cannontech.msp.beans.v5.cd_server.GetAllCDDevicesResponse;
import com.cannontech.msp.beans.v5.cd_server.GetCDDeviceStates;
import com.cannontech.msp.beans.v5.cd_server.GetCDDeviceStatesResponse;
import com.cannontech.msp.beans.v5.cd_server.GetCDEnabledMeters;
import com.cannontech.msp.beans.v5.cd_server.GetCDEnabledMetersResponse;
import com.cannontech.msp.beans.v5.cd_server.GetMethods;
import com.cannontech.msp.beans.v5.cd_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.cd_server.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v5.cd_server.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v5.cd_server.ObjectFactory;
import com.cannontech.msp.beans.v5.cd_server.PingURL;
import com.cannontech.msp.beans.v5.cd_server.PingURLResponse;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfCDDevice;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfCDState;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.multispeak.CDDevice;
import com.cannontech.msp.beans.v5.multispeak.CDDeviceIdentifier;
import com.cannontech.msp.beans.v5.multispeak.CDState;
import com.cannontech.msp.beans.v5.multispeak.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v5.multispeak.Meters;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.CD_Server;

@Endpoint("CDServiceEndpointV5")
@RequestMapping("/multispeak/v5/CD_Server")
public class CDServiceEndpoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private CD_Server cdServer;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String CD_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/CD_Server";

    @PayloadRoot(localPart = "PingURL", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingUrl(@RequestPayload PingURL pingURL)
            throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        cdServer.pingURL();
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methodNames = cdServer.getMethods();
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;
    }

    @PayloadRoot(localPart = "GetAllCDDevices", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllCDDevicesResponse GetAllCDDevices(@RequestPayload GetAllCDDevices getAllCDDevices)
            throws MultispeakWebServiceException {
        GetAllCDDevicesResponse response = objectFactory.createGetAllCDDevicesResponse();
        List<CDDevice> cdDevices = cdServer.getAllCDDevices(getAllCDDevices.getLastReceived());
        ArrayOfCDDevice arrayOfCDDevice = commonObjectFactory.createArrayOfCDDevice();
        arrayOfCDDevice.getCDDevice().addAll(cdDevices);
        response.setArrayOfCDDevice(arrayOfCDDevice);
        return response;
    }

    @PayloadRoot(localPart = "GetCDEnabledMeters", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetCDEnabledMetersResponse getCDEnabledMeters(
            @RequestPayload GetCDEnabledMeters getCDEnabledMeters) throws MultispeakWebServiceException {
        GetCDEnabledMetersResponse response = objectFactory.createGetCDEnabledMetersResponse();
        Meters cdEnabledMeters = cdServer.getCDEnabledMeters(getCDEnabledMeters.getLastReceived());
        response.setMeters(cdEnabledMeters);

        return response;
    }

    @PayloadRoot(localPart = "GetCDDeviceStates", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetCDDeviceStatesResponse getCDDeviceStates(
            @RequestPayload GetCDDeviceStates getCDDeviceStates) throws MultispeakWebServiceException {
        GetCDDeviceStatesResponse response = objectFactory.createGetCDDeviceStatesResponse();
        List<CDDeviceIdentifier> cdDeviceIdentifier =
            getCDDeviceStates.getArrayOfCDDeviceIdentifier() != null
                ? getCDDeviceStates.getArrayOfCDDeviceIdentifier().getCDDeviceIdentifier() : null;
        List<CDState> cdStatelist = cdServer.getCDDeviceStates(cdDeviceIdentifier);
        ArrayOfCDState arrayOfCDState = commonObjectFactory.createArrayOfCDState();

        arrayOfCDState.getCDState().addAll(cdStatelist);
        response.setArrayOfCDState(arrayOfCDState);
        return response;
    }

    @PayloadRoot(localPart = "InitiateConnectDisconnect", namespace = CD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateConnectDisconnectResponse initiateConnectDisconnect(
            @RequestPayload InitiateConnectDisconnect initiateConnectDisconnect) throws MultispeakWebServiceException {
        InitiateConnectDisconnectResponse response = objectFactory.createInitiateConnectDisconnectResponse();
        List<ConnectDisconnectEvent> cdEvents =
            initiateConnectDisconnect.getArrayOfConnectDisconnectEvent() != null
                ? initiateConnectDisconnect.getArrayOfConnectDisconnectEvent().getConnectDisconnectEvent() : null;
        String responseURL = initiateConnectDisconnect.getResponseURL();
        XMLGregorianCalendar xmlExpirationTime = initiateConnectDisconnect.getExpirationTime();
        
        String transactionID = initiateConnectDisconnect.getTransactionID();

        multispeakFuncs.addErrorObjectsInResponseHeader(cdServer.initiateConnectDisconnect(cdEvents, responseURL,
            transactionID, xmlExpirationTime));
        return response;
    }
}