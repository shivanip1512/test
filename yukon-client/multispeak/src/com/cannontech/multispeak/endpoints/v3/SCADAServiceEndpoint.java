package com.cannontech.multispeak.endpoints.v3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfScadaAnalog;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.SCADA_Server;

/*
 * This class is the SCADA Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/multispeak/v3/SCADA_Server")
public class SCADAServiceEndpoint {
    @Autowired private SCADA_Server scada_Server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        scada_Server.pingURL();
        
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        List<String> methods = scada_Server.getMethods();
        
        ArrayOfString stringArray = objectFactory.createArrayOfString();
        stringArray.getString().addAll(methods);
        response.setGetMethodsResult(stringArray);
        return response;
    }

    @PayloadRoot(localPart = "GetAllSCADAAnalogs", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllSCADAAnalogsResponse getAllSCADAAnalogs(@RequestPayload GetAllSCADAAnalogs getAllSCADAAnalogs)
            throws MultispeakWebServiceException {
        GetAllSCADAAnalogsResponse response = objectFactory.createGetAllSCADAAnalogsResponse();
        
        String lastReceived = getAllSCADAAnalogs.getLastReceived();
        List<ScadaAnalog> scadaAnalogs = scada_Server.getAllSCADAAnalogs(lastReceived);
        
        ArrayOfScadaAnalog arrayOfScadaAnalog = objectFactory.createArrayOfScadaAnalog();
        arrayOfScadaAnalog.getScadaAnalog().addAll(scadaAnalogs);
        response.setGetAllSCADAAnalogsResult(arrayOfScadaAnalog);
        return response;
    }
}