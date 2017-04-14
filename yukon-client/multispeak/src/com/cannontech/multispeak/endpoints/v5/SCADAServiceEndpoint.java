package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfSCADAAnalog;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAAnalogs;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAAnalogsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetMethods;
import com.cannontech.msp.beans.v5.scada_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.scada_server.ObjectFactory;
import com.cannontech.msp.beans.v5.scada_server.PingURL;
import com.cannontech.msp.beans.v5.scada_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.SCADA_Server;

/*
 * This class is the SCADA Service endpoint all requests will be processed from
 * here.
 */
@Endpoint("SCADAServiceEndpointV5")
@RequestMapping("/multispeak/v5/SCADA_Server")
public class SCADAServiceEndpoint {
    @Autowired private SCADA_Server scada_Server;
    @Autowired private ObjectFactory scadaObjectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String SCADA_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/SCADA_Server";

    @PayloadRoot(localPart = "PingURL", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = scadaObjectFactory.createPingURLResponse();
        scada_Server.pingURL();
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = scadaObjectFactory.createGetMethodsResponse();
        List<String> methodNames  = scada_Server.getMethods();
        
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestSCADAAnalogs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetLatestSCADAAnalogsResponse getLatestSCADAAnalogs(@RequestPayload GetLatestSCADAAnalogs getLatestSCADAAnalogs)
            throws MultispeakWebServiceException {
        GetLatestSCADAAnalogsResponse response = scadaObjectFactory.createGetLatestSCADAAnalogsResponse();
        String lastReceived = getLatestSCADAAnalogs.getLastReceived();
        ArrayOfSCADAAnalog scadaAnalogs = commonObjectFactory.createArrayOfSCADAAnalog();
        scadaAnalogs.getSCADAAnalog().addAll(scada_Server.getLatestSCADAAnalogs(lastReceived));
        response.setArrayOfSCADAAnalog(scadaAnalogs);;
        return response;
    }
}