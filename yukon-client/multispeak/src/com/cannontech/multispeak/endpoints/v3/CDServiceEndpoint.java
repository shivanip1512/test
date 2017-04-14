
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
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.CD_Server;

/**
 * Endpoint class to service the requests for CD_Server wsdl methods
 * 
  */

@Endpoint("CDServiceEndpointV3")
@RequestMapping("/multispeak/v3/CD_Server")
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
}