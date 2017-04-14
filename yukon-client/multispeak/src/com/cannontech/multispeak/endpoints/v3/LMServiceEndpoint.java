package com.cannontech.multispeak.endpoints.v3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfSubstationLoadControlStatus;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvent;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvents;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventsResponse;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotification;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.LM_Server;

/*
 * This class is the LM Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/multispeak/v3/LM_Server")
public class LMServiceEndpoint {
    @Autowired private LM_Server lm_Server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        lm_Server.pingURL();
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        response.setGetMethodsResult(multispeakFuncs.toArrayOfString(lm_Server.getMethods()));
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationResponse SCADAAnalogChangedNotification(
            @RequestPayload SCADAAnalogChangedNotification SCADAAnalogChangedNotification)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationResponse response = objectFactory.createSCADAAnalogChangedNotificationResponse();
        List<ScadaAnalog> scadaAnalogs = (null != SCADAAnalogChangedNotification.getScadaAnalogs()) ? 
                SCADAAnalogChangedNotification.getScadaAnalogs().getScadaAnalog() : null;

        response.setSCADAAnalogChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAAnalogChangedNotification(scadaAnalogs)));

        return response;
    }

    @PayloadRoot(localPart = "GetAllSubstationLoadControlStatuses", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllSubstationLoadControlStatusesResponse getAllSubstationLoadControlStatuses(
            @RequestPayload GetAllSubstationLoadControlStatuses getAllSubstationLoadControlStatuses)
            throws MultispeakWebServiceException {
        GetAllSubstationLoadControlStatusesResponse response =
            objectFactory.createGetAllSubstationLoadControlStatusesResponse();
        List<SubstationLoadControlStatus> substationLoadControlStatus =
            lm_Server.getAllSubstationLoadControlStatuses();
        
        ArrayOfSubstationLoadControlStatus arrayOfSubstationLoadControlStatus = objectFactory.createArrayOfSubstationLoadControlStatus();
        arrayOfSubstationLoadControlStatus.getSubstationLoadControlStatus().addAll(substationLoadControlStatus);
        response.setGetAllSubstationLoadControlStatusesResult(arrayOfSubstationLoadControlStatus);
        return response;
    }

    @PayloadRoot(localPart = "InitiateLoadManagementEvent", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateLoadManagementEventResponse initiateLoadManagementEvent(
            @RequestPayload InitiateLoadManagementEvent initiateLoadManagementEvent)
            throws MultispeakWebServiceException {
        InitiateLoadManagementEventResponse response = objectFactory.createInitiateLoadManagementEventResponse();
        ErrorObject errorObject = lm_Server.initiateLoadManagementEvent(initiateLoadManagementEvent.getTheLMEvent());
        response.setInitiateLoadManagementEventResult(errorObject);
        return response;
    }

    @PayloadRoot(localPart = "InitiateLoadManagementEvents", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateLoadManagementEventsResponse initiateLoadManagementEvents(
            @RequestPayload InitiateLoadManagementEvents initiateLoadManagementEvents)
            throws MultispeakWebServiceException {
        InitiateLoadManagementEventsResponse response = objectFactory.createInitiateLoadManagementEventsResponse();
        
        List<LoadManagementEvent> loadManagementEvents = (null != initiateLoadManagementEvents.getTheLMEvents()) ? 
                initiateLoadManagementEvents.getTheLMEvents().getLoadManagementEvent() : null;

        response.setInitiateLoadManagementEventsResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.initiateLoadManagementEvents(loadManagementEvents)));
        
        return response;
    }
}