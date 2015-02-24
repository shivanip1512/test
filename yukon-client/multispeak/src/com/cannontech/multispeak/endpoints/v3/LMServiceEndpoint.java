package com.cannontech.multispeak.endpoints.v3;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.ArrayOfCustomer;
import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfLMDeviceExchange;
import com.cannontech.msp.beans.v3.ArrayOfLoadManagementDevice;
import com.cannontech.msp.beans.v3.ArrayOfSubstationLoadControlStatus;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.CustomerChangedNotification;
import com.cannontech.msp.beans.v3.CustomerChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNameChange;
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
import com.cannontech.msp.beans.v3.LMDeviceExchange;
import com.cannontech.msp.beans.v3.LMDeviceExchangeNotification;
import com.cannontech.msp.beans.v3.LMDeviceExchangeNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceRemoveNotification;
import com.cannontech.msp.beans.v3.LMDeviceRemoveNotificationResponse;
import com.cannontech.msp.beans.v3.LMDeviceRetireNotification;
import com.cannontech.msp.beans.v3.LMDeviceRetireNotificationResponse;
import com.cannontech.msp.beans.v3.LoadManagementDevice;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectFactory;
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
import com.cannontech.msp.beans.v3.ScadaPoint;
import com.cannontech.msp.beans.v3.ScadaStatus;
import com.cannontech.msp.beans.v3.ServiceLocation;
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
    PingURLResponse pingURL() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        response.setPingURLResult(multispeakFuncs.toArrayOfErrorObject(Collections.singletonList(lm_Server.pingURL())));
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
        List<ScadaAnalog> scadaAnalogs = SCADAAnalogChangedNotification.getScadaAnalogs().getScadaAnalog();
        if (!scadaAnalogs.isEmpty()) {
            response.setSCADAAnalogChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAAnalogChangedNotification(scadaAnalogs)));
        }
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
        
        List<LoadManagementEvent> loadManagementEvents = initiateLoadManagementEvents.getTheLMEvents().getLoadManagementEvent();
        if (!loadManagementEvents.isEmpty()) {
            response.setInitiateLoadManagementEventsResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.initiateLoadManagementEvents(loadManagementEvents)));
        }
        return response;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames(@RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        GetDomainNamesResponse response = objectFactory.createGetDomainNamesResponse();
        response.setGetDomainNamesResult(multispeakFuncs.toArrayOfString(lm_Server.getDomainNames()));
        return response;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse response = objectFactory.createGetDomainMembersResponse();
        List<DomainMember> domainMembers = lm_Server.getDomainMembers(getDomainMembers.getDomainName());
        ArrayOfDomainMember arrayOfDomainMember = objectFactory.createArrayOfDomainMember();
        arrayOfDomainMember.getDomainMember().addAll(domainMembers);
        response.setGetDomainMembersResult(arrayOfDomainMember);
        return response;
    }

    @PayloadRoot(localPart = "LMDeviceAddNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    LMDeviceAddNotificationResponse lmDeviceAddNotification(
            @RequestPayload LMDeviceAddNotification lmDeviceAddNotification) throws MultispeakWebServiceException {
        LMDeviceAddNotificationResponse response = objectFactory.createLMDeviceAddNotificationResponse();
        ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = lmDeviceAddNotification.getAddedLMDs();
        if (null != arrayOfLoadManagementDevice) {
            List<LoadManagementDevice> loadManagementDevices = arrayOfLoadManagementDevice.getLoadManagementDevice();
            response.setLMDeviceAddNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.LMDeviceAddNotification(loadManagementDevices)));
        }
        return response;
    }

    @PayloadRoot(localPart = "LMDeviceChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    LMDeviceChangedNotificationResponse lmDeviceChangedNotification(
            @RequestPayload LMDeviceChangedNotification lmDeviceChangedNotification)
            throws MultispeakWebServiceException {
        LMDeviceChangedNotificationResponse response = objectFactory.createLMDeviceChangedNotificationResponse();
        ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = lmDeviceChangedNotification.getChangedLMDs();
        if (null != arrayOfLoadManagementDevice) {
            List<LoadManagementDevice> loadManagementDevices = arrayOfLoadManagementDevice.getLoadManagementDevice();
            response.setLMDeviceChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.LMDeviceChangedNotification(loadManagementDevices)));
        }
        return response;
    }

    @PayloadRoot(localPart = "LMDeviceExchangeNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    LMDeviceExchangeNotificationResponse lmDeviceExchangeNotification(
            @RequestPayload LMDeviceExchangeNotification lmDeviceExchangeNotification)
            throws MultispeakWebServiceException {
        LMDeviceExchangeNotificationResponse response = objectFactory.createLMDeviceExchangeNotificationResponse();
        ArrayOfLMDeviceExchange arrayOfLMDeviceExchange = lmDeviceExchangeNotification.getLMDChangeout();
        if (null != arrayOfLMDeviceExchange) {
            List<LMDeviceExchange> lmDeviceExchanges = arrayOfLMDeviceExchange.getLMDeviceExchange();
            response.setLMDeviceExchangeNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.LMDeviceExchangeNotification(lmDeviceExchanges)));
        }
        return response;
    }

    @PayloadRoot(localPart = "LMDeviceRemoveNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    LMDeviceRemoveNotificationResponse lmDeviceRemoveNotification(
            @RequestPayload LMDeviceRemoveNotification lmDeviceRemoveNotification) throws MultispeakWebServiceException {
        LMDeviceRemoveNotificationResponse response = objectFactory.createLMDeviceRemoveNotificationResponse();
        ArrayOfLoadManagementDevice loadManagementDeviceArr = lmDeviceRemoveNotification.getRemovedLMDs();
        if (null != loadManagementDeviceArr) {
            List<LoadManagementDevice> loadManagementDevices = loadManagementDeviceArr.getLoadManagementDevice();
            response.setLMDeviceRemoveNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.LMDeviceRemoveNotification(loadManagementDevices)));
        }
        return response;
    }

    @PayloadRoot(localPart = "LMDeviceRetireNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    LMDeviceRetireNotificationResponse lmDeviceRetireNotification(
            @RequestPayload LMDeviceRetireNotification lmDeviceRetireNotification) throws MultispeakWebServiceException {
        LMDeviceRetireNotificationResponse response = objectFactory.createLMDeviceRetireNotificationResponse();
        ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = lmDeviceRetireNotification.getRetiredLMDs();
        if (null != arrayOfLoadManagementDevice) {
            List<LoadManagementDevice> loadManagementDevices = arrayOfLoadManagementDevice.getLoadManagementDevice();
            response.setLMDeviceRetireNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.LMDeviceRetireNotification(loadManagementDevices)));
        }
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationByPointIDResponse scadaAnalogChangedNotificationByPointID(
            @RequestPayload SCADAAnalogChangedNotificationByPointID scadaAnalogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationByPointIDResponse response =
            objectFactory.createSCADAAnalogChangedNotificationByPointIDResponse();
        lm_Server.SCADAAnalogChangedNotificationByPointID(scadaAnalogChangedNotificationByPointID.getScadaAnalog());
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationForPowerResponse scadaAnalogChangedNotificationForPower(
            @RequestPayload SCADAAnalogChangedNotificationForPower scadaAnalogChangedNotificationForPower)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationForPowerResponse response =
            objectFactory.createSCADAAnalogChangedNotificationForPowerResponse();
        List<ScadaAnalog> scadaAnalogs = scadaAnalogChangedNotificationForPower.getScadaAnalogs().getScadaAnalog();
        response.setSCADAAnalogChangedNotificationForPowerResult((multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAAnalogChangedNotificationForVoltage(scadaAnalogs))));
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationForVoltageResponse scadaAnalogChangedNotificationForVoltage(
            @RequestPayload SCADAAnalogChangedNotificationForVoltage scadaAnalogChangedNotificationForVoltage)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationForVoltageResponse response =
            objectFactory.createSCADAAnalogChangedNotificationForVoltageResponse();
        List<ScadaAnalog> scadaAnalogs = scadaAnalogChangedNotificationForVoltage.getScadaAnalogs().getScadaAnalog();
        response.setSCADAAnalogChangedNotificationForVoltageResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAAnalogChangedNotificationForVoltage(scadaAnalogs)));
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationResponse scadaPointChangedNotification(
            @RequestPayload SCADAPointChangedNotification scadaPointChangedNotification)
            throws MultispeakWebServiceException {
        SCADAPointChangedNotificationResponse response = objectFactory.createSCADAPointChangedNotificationResponse();
        List<ScadaPoint> scadaPoints = scadaPointChangedNotification.getScadaPoints().getScadaPoint();
        response.setSCADAPointChangedNotificationResult((multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAPointChangedNotificationForAnalog(scadaPoints))));
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationForAnalogResponse scadaPointChangedNotificationForAnalog(
            @RequestPayload SCADAPointChangedNotificationForAnalog scadaPointChangedNotificationForAnalog)
            throws MultispeakWebServiceException {
        SCADAPointChangedNotificationForAnalogResponse response =
            objectFactory.createSCADAPointChangedNotificationForAnalogResponse();
        List<ScadaPoint> scadaPoints = scadaPointChangedNotificationForAnalog.getScadaPoints().getScadaPoint();
        response.setSCADAPointChangedNotificationForAnalogResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAPointChangedNotificationForAnalog(scadaPoints)));
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationForStatusResponse scadaPointChangedNotificationForStatus(
            @RequestPayload SCADAPointChangedNotificationForStatus scadaPointChangedNotificationForStatus)
            throws MultispeakWebServiceException {

        SCADAPointChangedNotificationForStatusResponse response =
            objectFactory.createSCADAPointChangedNotificationForStatusResponse();
        List<ScadaPoint> scadaPoints = scadaPointChangedNotificationForStatus.getScadaPoints().getScadaPoint();
        response.setSCADAPointChangedNotificationForStatusResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAPointChangedNotificationForStatus(scadaPoints)));
        return response;
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAStatusChangedNotificationResponse scadaStatusChangedNotification(
            @RequestPayload SCADAStatusChangedNotification scadaStatusChangedNotification)
            throws MultispeakWebServiceException {
        SCADAStatusChangedNotificationResponse response = objectFactory.createSCADAStatusChangedNotificationResponse();
        List<ScadaStatus> scadaStatus = scadaStatusChangedNotification.getScadaStatuses().getScadaStatus();
        response.setSCADAStatusChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.SCADAStatusChangedNotification(scadaStatus)));
        return response;
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAStatusChangedNotificationByPointIDResponse scadaStatusChangedNotificationByPointID(
            @RequestPayload SCADAStatusChangedNotificationByPointID scadaStatusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        SCADAStatusChangedNotificationByPointIDResponse response =
            objectFactory.createSCADAStatusChangedNotificationByPointIDResponse();
        lm_Server.SCADAStatusChangedNotificationByPointID(scadaStatusChangedNotificationByPointID.getScadaStatus());
        return response;
    }

    @PayloadRoot(localPart = "CustomerChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    CustomerChangedNotificationResponse customerChangedNotification(
            @RequestPayload CustomerChangedNotification customerChangedNotification)
            throws MultispeakWebServiceException {
        CustomerChangedNotificationResponse response = objectFactory.createCustomerChangedNotificationResponse();
        ArrayOfCustomer arrayOfCustomer = customerChangedNotification.getChangedCustomers();
        List<Customer> customers = arrayOfCustomer.getCustomer();
        if (!customers.isEmpty()) {
            response.setCustomerChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.customerChangedNotification(customers)));
        }
        return response;
    }

    @PayloadRoot(localPart = "GetAllLoadManagementDevices", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllLoadManagementDevicesResponse getAllLoadManagementDevices(
            @RequestPayload GetAllLoadManagementDevices getAllLoadManagementDevices)
            throws MultispeakWebServiceException {
        GetAllLoadManagementDevicesResponse response = objectFactory.createGetAllLoadManagementDevicesResponse();
        List<LoadManagementDevice> loadManagementDevices =
            lm_Server.getAllLoadManagementDevices(getAllLoadManagementDevices.getLastReceived());
        
        ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = null;
        if (null != loadManagementDevices) {
            arrayOfLoadManagementDevice = objectFactory.createArrayOfLoadManagementDevice();
            arrayOfLoadManagementDevice.getLoadManagementDevice().addAll(loadManagementDevices);
            response.setGetAllLoadManagementDevicesResult(arrayOfLoadManagementDevice);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetAmountOfControllableLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAmountOfControllableLoadResponse getAmountOfControllableLoad(
            @RequestPayload GetAmountOfControllableLoad getAmountOfControllableLoad)
            throws MultispeakWebServiceException {
        GetAmountOfControllableLoadResponse response = objectFactory.createGetAmountOfControllableLoadResponse();
        response.setGetAmountOfControllableLoadResult(lm_Server.getAmountOfControllableLoad());
        return response;
    }

    @PayloadRoot(localPart = "GetAmountOfControlledLoad", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAmountOfControlledLoadResponse getAmountOfControlledLoad(
            @RequestPayload GetAmountOfControlledLoad getAmountOfControlledLoad) throws MultispeakWebServiceException {
        GetAmountOfControlledLoadResponse response = objectFactory.createGetAmountOfControlledLoadResponse();
        response.setGetAmountOfControlledLoadResult(lm_Server.getAmountOfControlledLoad());
        return response;
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByMeterNumber", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLoadManagementDeviceByMeterNumberResponse getLoadManagementDeviceByMeterNumber(
            @RequestPayload GetLoadManagementDeviceByMeterNumber getLoadManagementDeviceByMeterNumber)
            throws MultispeakWebServiceException {
        GetLoadManagementDeviceByMeterNumberResponse response =
            objectFactory.createGetLoadManagementDeviceByMeterNumberResponse();
        List<LoadManagementDevice> loadManagementDevices =
            lm_Server.getLoadManagementDeviceByMeterNumber(getLoadManagementDeviceByMeterNumber.getMeterNo());
        ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = null;
        if (null != loadManagementDevices) {
            arrayOfLoadManagementDevice = objectFactory.createArrayOfLoadManagementDevice();
            arrayOfLoadManagementDevice.getLoadManagementDevice().addAll(loadManagementDevices);
            response.setGetLoadManagementDeviceByMeterNumberResult(arrayOfLoadManagementDevice);
        }
        return response;
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByServLoc", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetLoadManagementDeviceByServLocResponse getLoadManagementDeviceByServLoc(
            @RequestPayload GetLoadManagementDeviceByServLoc getLoadManagementDeviceByServLoc)
            throws MultispeakWebServiceException {
        GetLoadManagementDeviceByServLocResponse response =
            objectFactory.createGetLoadManagementDeviceByServLocResponse();

        List<LoadManagementDevice> loadManagementDevices =
            lm_Server.getLoadManagementDeviceByServLoc(getLoadManagementDeviceByServLoc.getServLoc());
        if (null != loadManagementDevices) {
            ArrayOfLoadManagementDevice arrayOfLoadManagementDevice = objectFactory.createArrayOfLoadManagementDevice();
            arrayOfLoadManagementDevice.getLoadManagementDevice().addAll(loadManagementDevices);
            response.setGetLoadManagementDeviceByServLocResult(arrayOfLoadManagementDevice);
        }
        return response;
    }

    @PayloadRoot(localPart = "InitiatePowerFactorManagementEvent", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiatePowerFactorManagementEventResponse initiatePowerFactorManagementEvent(
            @RequestPayload InitiatePowerFactorManagementEvent initiatePowerFactorManagementEvent)
            throws MultispeakWebServiceException {
        InitiatePowerFactorManagementEventResponse initiatePowerFactorManagementEventResponse =
            objectFactory.createInitiatePowerFactorManagementEventResponse();
        initiatePowerFactorManagementEventResponse.setInitiatePowerFactorManagementEventResult(lm_Server.initiatePowerFactorManagementEvent(initiatePowerFactorManagementEvent.getThePFMEvent()));
        return initiatePowerFactorManagementEventResponse;
    }

    @PayloadRoot(localPart = "IsLoadManagementActive", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    IsLoadManagementActiveResponse isLoadManagementActive(@RequestPayload IsLoadManagementActive isLoadManagementActive)
            throws MultispeakWebServiceException {
        IsLoadManagementActiveResponse isLoadManagementActiveResponse =
            objectFactory.createIsLoadManagementActiveResponse();
        isLoadManagementActiveResponse.setIsLoadManagementActiveResult(lm_Server.isLoadManagementActive(isLoadManagementActive.getServLoc()));
        return isLoadManagementActiveResponse;
    }

    @PayloadRoot(localPart = "ServiceLocationChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ServiceLocationChangedNotificationResponse serviceLocationChangedNotification(
            @RequestPayload ServiceLocationChangedNotification serviceLocationChangedNotification)
            throws MultispeakWebServiceException {
        ServiceLocationChangedNotificationResponse serviceLocationChangedNotificationResponse =
            objectFactory.createServiceLocationChangedNotificationResponse();
        List<ServiceLocation> serviceLocations = serviceLocationChangedNotification.getChangedServiceLocations().getServiceLocation();
        serviceLocationChangedNotificationResponse.setServiceLocationChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.serviceLocationChangedNotification(serviceLocations)));
        return serviceLocationChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RequestRegistrationIDResponse requestRegistrationID(@RequestPayload RequestRegistrationID requestRegistrationID)
            throws MultispeakWebServiceException {
        RequestRegistrationIDResponse requestRegistrationIDResponse =
            objectFactory.createRequestRegistrationIDResponse();
        requestRegistrationIDResponse.setRequestRegistrationIDResult(lm_Server.requestRegistrationID());
        return requestRegistrationIDResponse;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse registerForServiceResponse = objectFactory.createRegisterForServiceResponse();
        registerForServiceResponse.setRegisterForServiceResult((multispeakFuncs.toArrayOfErrorObject(lm_Server.registerForService(registerForService.getRegistrationDetails()))));
        return registerForServiceResponse;
    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse unregisterForServiceResponse = objectFactory.createUnregisterForServiceResponse();
        unregisterForServiceResponse.setUnregisterForServiceResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.unregisterForService(unregisterForService.getRegistrationID())));
        return unregisterForServiceResponse;
    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {
        GetRegistrationInfoByIDResponse getRegistrationInfoByIDResponse =
            objectFactory.createGetRegistrationInfoByIDResponse();
        getRegistrationInfoByIDResponse.setGetRegistrationInfoByIDResult(lm_Server.getRegistrationInfoByID(getRegistrationInfoByID.getRegistrationID()));
        return getRegistrationInfoByIDResponse;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods(@RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        GetPublishMethodsResponse getPublishMethodsResponse = objectFactory.createGetPublishMethodsResponse();
        getPublishMethodsResponse.setGetPublishMethodsResult(multispeakFuncs.toArrayOfString(lm_Server.getPublishMethods()));
        return getPublishMethodsResponse;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse domainMembersChangedNotificationResponse =
            objectFactory.createDomainMembersChangedNotificationResponse();
        if (null != domainMembersChangedNotification.getChangedDomainMembers()) {
            List<DomainMember> domainMembers =
                domainMembersChangedNotification.getChangedDomainMembers().getDomainMember();
            domainMembersChangedNotificationResponse.setDomainMembersChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.domainMembersChangedNotification(domainMembers)));
        }
        return domainMembersChangedNotificationResponse;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse domainNamesChangedNotificationResponse =
            objectFactory.createDomainNamesChangedNotificationResponse();
        if (null != domainNamesChangedNotification.getChangedDomainNames()) {
            List<DomainNameChange> domainNameChanges = domainNamesChangedNotification.getChangedDomainNames().getDomainNameChange();
            domainNamesChangedNotificationResponse.setDomainNamesChangedNotificationResult(multispeakFuncs.toArrayOfErrorObject(lm_Server.domainNamesChangedNotification(domainNameChanges)));
        }
        return domainNamesChangedNotificationResponse;
    }
}