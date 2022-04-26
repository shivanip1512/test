package com.cannontech.multispeak.endpoints.v4;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v4.ArrayOfString;
import com.cannontech.msp.beans.v4.ArrayOfSubstationLoadControlStatus;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v4.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.InitiateLoadManagementEvent;
import com.cannontech.msp.beans.v4.InitiateLoadManagementEventResponse;
import com.cannontech.msp.beans.v4.InitiateLoadManagementEvents;
import com.cannontech.msp.beans.v4.InitiateLoadManagementEventsResponse;
import com.cannontech.msp.beans.v4.LoadManagementEvent;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.SCADAAnalogChangedNotification;
import com.cannontech.msp.beans.v4.SCADAAnalogChangedNotificationResponse;
import com.cannontech.msp.beans.v4.ScadaAnalog;
import com.cannontech.msp.beans.v4.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.DR_Server;

/*
 * This class is the DR Service endpoint all requests will be processed from here.
 */

@Endpoint("DRServiceEndpointV4")
@RequestMapping("/multispeak/v4/DR_Server")
public class DRServiceEndpoint {

    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private DR_Server dr_Server;

    @Autowired private DR_Server dr_server;
    private final String DR_V4_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v4;

    @PayloadRoot(localPart = "PingURL", namespace = DR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        dr_server.pingURL();

        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = DR_V4_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();

        List<String> methods = dr_server.getMethods();
        ArrayOfString arrayOfString = objectFactory.createArrayOfString();
        arrayOfString.getString().addAll(methods);
        response.setGetMethodsResult(arrayOfString);
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotification", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload SCADAAnalogChangedNotificationResponse SCADAAnalogChangedNotification(
            @RequestPayload SCADAAnalogChangedNotification SCADAAnalogChangedNotification)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationResponse response = objectFactory.createSCADAAnalogChangedNotificationResponse();
        List<ScadaAnalog> scadaAnalogs = (null != SCADAAnalogChangedNotification
                .getScadaAnalogs()) ? SCADAAnalogChangedNotification.getScadaAnalogs().getScadaAnalog() : null;

        response.setSCADAAnalogChangedNotificationResult(
                multispeakFuncs.toArrayOfErrorObject(dr_server.SCADAAnalogChangedNotification(scadaAnalogs)));

        return response;
    }

    @PayloadRoot(localPart = "GetAllSubstationLoadControlStatuses", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload GetAllSubstationLoadControlStatusesResponse getAllSubstationLoadControlStatuses(
            @RequestPayload GetAllSubstationLoadControlStatuses getAllSubstationLoadControlStatuses)
            throws MultispeakWebServiceException {
        GetAllSubstationLoadControlStatusesResponse response = objectFactory.createGetAllSubstationLoadControlStatusesResponse();
        List<SubstationLoadControlStatus> substationLoadControlStatus = dr_Server.getAllSubstationLoadControlStatuses();

        ArrayOfSubstationLoadControlStatus arrayOfSubstationLoadControlStatus = objectFactory
                .createArrayOfSubstationLoadControlStatus();
        arrayOfSubstationLoadControlStatus.getSubstationLoadControlStatus().addAll(substationLoadControlStatus);
        response.setGetAllSubstationLoadControlStatusesResult(arrayOfSubstationLoadControlStatus);
        return response;
    }

    @PayloadRoot(localPart = "InitiateLoadManagementEvent", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload InitiateLoadManagementEventResponse initiateLoadManagementEvent(
            @RequestPayload InitiateLoadManagementEvent initiateLoadManagementEvent)
            throws MultispeakWebServiceException {
        InitiateLoadManagementEventResponse response = objectFactory.createInitiateLoadManagementEventResponse();
        List<ErrorObject> errorObjectList = dr_Server.initiateLoadManagementEvent(initiateLoadManagementEvent.getTheLMEvent());
        response.setInitiateLoadManagementEventResult(multispeakFuncs.toArrayOfErrorObject(errorObjectList));
        return response;
    }

    @PayloadRoot(localPart = "InitiateLoadManagementEvents", namespace = MultispeakDefines.NAMESPACE_v4)
    public @ResponsePayload InitiateLoadManagementEventsResponse initiateLoadManagementEvents(
            @RequestPayload InitiateLoadManagementEvents initiateLoadManagementEvents)
            throws MultispeakWebServiceException {
        InitiateLoadManagementEventsResponse response = objectFactory.createInitiateLoadManagementEventsResponse();

        List<LoadManagementEvent> loadManagementEvents = (null != initiateLoadManagementEvents
                .getTheLMEvents()) ? initiateLoadManagementEvents.getTheLMEvents().getLoadManagementEvent() : null;

        response.setInitiateLoadManagementEventsResult(
                multispeakFuncs.toArrayOfErrorObject(dr_Server.initiateLoadManagementEvents(loadManagementEvents)));

        return response;
    }
}
