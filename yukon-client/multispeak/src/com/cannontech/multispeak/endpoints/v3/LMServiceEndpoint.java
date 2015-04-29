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
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvent;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvents;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventsResponse;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotification;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.LM_Server;

/*
 * This class is the LM Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/soap/LM_ServerSoap")
public class LMServiceEndpoint {
    @Autowired private LM_Server lm_Server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @PayloadRoot(localPart = "PingURL", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PingURLResponse pingURL() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        lm_Server.pingURL();
        ArrayOfErrorObject arrayOfErrorObject = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrayOfErrorObject);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
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

    @PayloadRoot(localPart = "LMDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void lmDeviceAddNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void lmDeviceChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void lmDeviceExchangeNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void lmDeviceRemoveNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void lmDeviceRetireNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaAnalogChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaAnalogChangedNotificationForPower() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaAnalogChangedNotificationForVoltage() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaPointChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaPointChangedNotificationForAnalog() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaPointChangedNotificationForStatus() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaStatusChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void scadaStatusChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void customerChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllLoadManagementDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAllLoadManagementDevices() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControllableLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAmountOfControllableLoad() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControlledLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAmountOfControlledLoad() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByMeterNumber", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getLoadManagementDeviceByMeterNumber() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByServLoc", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getLoadManagementDeviceByServLoc() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiatePowerFactorManagementEvent", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiatePowerFactorManagementEvent() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "IsLoadManagementActive", namespace = MultispeakDefines.NAMESPACE_v3)
    public void isLoadManagementActive() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void serviceLocationChangedNotification() throws MultispeakWebServiceException {
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
}