package com.cannontech.multispeak.endpoints.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.CDState;
import com.cannontech.msp.beans.v4.GetCDMeterState;
import com.cannontech.msp.beans.v4.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v4.GetCDSupportedMeters;
import com.cannontech.msp.beans.v4.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.CD_Server;

/*
 * This class is the CD Service endpoint all requests will be processed from here.
 */

@Endpoint("CDServiceEndpointV4")
@RequestMapping("/multispeak/v4/CD_Server")
public class CDServiceEndpoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private CD_Server cd_server;
    private final String CD_V4_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v4;

    @PayloadRoot(localPart = "PingURL", namespace = CD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        cd_server.pingURL();

        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = CD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = cd_server.getMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "GetCDMeterState", namespace = CD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetCDMeterStateResponse getCDMeterState(@RequestPayload GetCDMeterState cdMeterState)
            throws MultispeakWebServiceException {
        GetCDMeterStateResponse response = objectFactory.createGetCDMeterStateResponse();
        CDState cdState = cd_server.getCDMeterState(cdMeterState.getMeterID());
        response.setGetCDMeterStateResult(cdState);
        return response;
    }

    @PayloadRoot(localPart = "GetCDSupportedMeters", namespace = CD_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetCDSupportedMetersResponse getCDSupportedMeters(
            @RequestPayload GetCDSupportedMeters getCDSupportedMeters)
            throws MultispeakWebServiceException {
        GetCDSupportedMetersResponse response = objectFactory.createGetCDSupportedMetersResponse();

        String lastReceived = getCDSupportedMeters.getLastReceived();
        Meters meters = cd_server.getCDSupportedMeters(lastReceived);
        response.setGetCDSupportedMetersResult(meters);
        return response;
    }
}
