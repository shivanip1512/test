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
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllLoadManagementDevices;
import com.cannontech.msp.beans.v3.GetAllLoadManagementDevicesResponse;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v3.GetAmountOfControllableLoad;
import com.cannontech.msp.beans.v3.GetAmountOfControllableLoadResponse;
import com.cannontech.msp.beans.v3.GetAmountOfControlledLoad;
import com.cannontech.msp.beans.v3.GetAmountOfControlledLoadResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNames;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetLoadManagementDeviceByMeterNumber;
import com.cannontech.msp.beans.v3.GetLoadManagementDeviceByMeterNumberResponse;
import com.cannontech.msp.beans.v3.GetLoadManagementDeviceByServLoc;
import com.cannontech.msp.beans.v3.GetLoadManagementDeviceByServLocResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetPublishMethods;
import com.cannontech.msp.beans.v3.GetPublishMethodsResponse;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByID;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByIDResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvent;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvents;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventsResponse;
import com.cannontech.msp.beans.v3.InitiatePowerFactorManagementEvent;
import com.cannontech.msp.beans.v3.InitiatePowerFactorManagementEventResponse;
import com.cannontech.msp.beans.v3.IsLoadManagementActive;
import com.cannontech.msp.beans.v3.IsLoadManagementActiveResponse;
import com.cannontech.msp.beans.v3.LMDeviceAddNotification;
import com.cannontech.msp.beans.v3.LMDeviceAddNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceChangedNotification;
import com.cannontech.msp.beans.v3.LMDeviceChangedNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceExchangeNotification;
import com.cannontech.msp.beans.v3.LMDeviceExchangeNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceRemoveNotification;
import com.cannontech.msp.beans.v3.LMDeviceRemoveNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceRetireNotification;
import com.cannontech.msp.beans.v3.LMDeviceRetireNotificationResponse;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.RegisterForService;
import com.cannontech.msp.beans.v3.RegisterForServiceResponse;
import com.cannontech.msp.beans.v3.RequestRegistrationID;
import com.cannontech.msp.beans.v3.RequestRegistrationIDResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotification;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationForPower;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationForPowerResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationForVoltage;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationForVoltageResponse;
import com.cannontech.msp.beans.v3.SCADAAnalogChangedNotificationResponse;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotification;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotificationForAnalog;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotificationForAnalogResponse;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotificationForStatus;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotificationForStatusResponse;
import com.cannontech.msp.beans.v3.SCADAPointChangedNotificationResponse;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotification;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.msp.beans.v3.UnregisterForService;
import com.cannontech.msp.beans.v3.UnregisterForServiceResponse;
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

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainNamesResponse getDomainNames(
            @RequestPayload GetDomainNames getDomainNames) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetDomainMembersResponse getDomainMembers(
            @RequestPayload GetDomainMembers getDomainMembers) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload LMDeviceAddNotificationResponse lmDeviceAddNotification(
            @RequestPayload LMDeviceAddNotification lmDeviceAddNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload LMDeviceChangedNotificationResponse lmDeviceChangedNotification(
            @RequestPayload LMDeviceChangedNotification lmDeviceChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload LMDeviceExchangeNotificationResponse lmDeviceExchangeNotification(
            @RequestPayload LMDeviceExchangeNotification lmDeviceExchangeNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload LMDeviceRemoveNotificationResponse lmDeviceRemoveNotification(
            @RequestPayload LMDeviceRemoveNotification lmDeviceRemoveNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LMDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload LMDeviceRetireNotificationResponse lmDeviceRetireNotification(
            @RequestPayload LMDeviceRetireNotification lmDeviceRetireNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationByPointIDResponse scadaAnalogChangedNotificationByPointID(
            @RequestPayload SCADAAnalogChangedNotificationByPointID scadaAnalogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationForPowerResponse scadaAnalogChangedNotificationForPower(
            @RequestPayload SCADAAnalogChangedNotificationForPower scadaAnalogChangedNotificationForPower)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationForVoltageResponse scadaAnalogChangedNotificationForVoltage(
            @RequestPayload SCADAAnalogChangedNotificationForVoltage scadaAnalogChangedNotificationForVoltage)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationResponse scadaPointChangedNotification(
            @RequestPayload SCADAPointChangedNotification scadaPointChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationForAnalogResponse scadaPointChangedNotificationForAnalog(
            @RequestPayload SCADAPointChangedNotificationForAnalog scadaPointChangedNotificationForAnalog)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationForStatusResponse scadaPointChangedNotificationForStatus(
            @RequestPayload SCADAPointChangedNotificationForStatus scadaPointChangedNotificationForStatus)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAStatusChangedNotificationResponse scadaStatusChangedNotification(
            @RequestPayload SCADAStatusChangedNotification scadaStatusChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAStatusChangedNotificationByPointIDResponse scadaStatusChangedNotificationByPointID(
            @RequestPayload SCADAStatusChangedNotificationByPointID scadaStatusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllLoadManagementDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAllLoadManagementDevicesResponse getAllLoadManagementDevices(
            @RequestPayload GetAllLoadManagementDevices getAllLoadManagementDevices)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControllableLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAmountOfControllableLoadResponse getAmountOfControllableLoad(
            @RequestPayload GetAmountOfControllableLoad getAmountOfControllableLoad)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControlledLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAmountOfControlledLoadResponse getAmountOfControlledLoad(
            @RequestPayload GetAmountOfControlledLoad getAmountOfControlledLoad)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByMeterNumber", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetLoadManagementDeviceByMeterNumberResponse getLoadManagementDeviceByMeterNumber(
            @RequestPayload GetLoadManagementDeviceByMeterNumber getLoadManagementDeviceByMeterNumber)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByServLoc", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetLoadManagementDeviceByServLocResponse getLoadManagementDeviceByServLoc(
            @RequestPayload GetLoadManagementDeviceByServLoc getLoadManagementDeviceByServLoc)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiatePowerFactorManagementEvent", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiatePowerFactorManagementEventResponse initiatePowerFactorManagementEvent(
            @RequestPayload InitiatePowerFactorManagementEvent initiatePowerFactorManagementEvent)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "IsLoadManagementActive", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload IsLoadManagementActiveResponse isLoadManagementActive(
            @RequestPayload IsLoadManagementActive isLoadManagementActive)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload RequestRegistrationIDResponse requestRegistrationID(
            @RequestPayload RequestRegistrationID requestRegistrationID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload RegisterForServiceResponse registerForService(
            @RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload UnregisterForServiceResponse unregisterForService(
            @RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetPublishMethodsResponse getPublishMethods(
            @RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}