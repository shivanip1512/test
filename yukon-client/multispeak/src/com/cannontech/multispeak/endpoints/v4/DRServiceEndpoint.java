package com.cannontech.multispeak.endpoints.v4;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.CD_Server;
import com.cannontech.multispeak.service.v4.DR_Server;

/*
 * This class is the DR Service endpoint all requests will be processed from here.
 */

@Endpoint("DRServiceEndpointV4")
@RequestMapping("/multispeak/v4/DR_Server")
public class DRServiceEndpoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private DR_Server dr_server;
    private final String DR_V4_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v4;

    @PayloadRoot(localPart = "PingURL", namespace = DR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        dr_server.pingURL();

        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = DR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = dr_server.getMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }
}

