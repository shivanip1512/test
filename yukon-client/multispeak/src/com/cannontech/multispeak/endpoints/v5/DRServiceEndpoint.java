package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfSubstationLoadControlStatus;
import com.cannontech.msp.beans.v5.dr_server.GetAllLoadManagementDevices;
import com.cannontech.msp.beans.v5.dr_server.GetAllLoadManagementDevicesResponse;
import com.cannontech.msp.beans.v5.dr_server.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v5.dr_server.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v5.dr_server.GetAmountOfControllableLoad;
import com.cannontech.msp.beans.v5.dr_server.GetAmountOfControllableLoadResponse;
import com.cannontech.msp.beans.v5.dr_server.GetAmountOfControlledLoad;
import com.cannontech.msp.beans.v5.dr_server.GetAmountOfControlledLoadResponse;
import com.cannontech.msp.beans.v5.dr_server.GetDomainNames;
import com.cannontech.msp.beans.v5.dr_server.GetDomainNamesResponse;
import com.cannontech.msp.beans.v5.dr_server.GetLoadManagementDevicesByMeterIDs;
import com.cannontech.msp.beans.v5.dr_server.GetLoadManagementDevicesByMeterIDsResponse;
import com.cannontech.msp.beans.v5.dr_server.GetLoadManagementDevicesByServiceLocationIDs;
import com.cannontech.msp.beans.v5.dr_server.GetLoadManagementDevicesByServiceLocationIDsResponse;
import com.cannontech.msp.beans.v5.dr_server.GetMethods;
import com.cannontech.msp.beans.v5.dr_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.dr_server.InitiateLoadManagementEvents;
import com.cannontech.msp.beans.v5.dr_server.InitiateLoadManagementEventsResponse;
import com.cannontech.msp.beans.v5.dr_server.InitiatePowerFactorManagementEvents;
import com.cannontech.msp.beans.v5.dr_server.InitiatePowerFactorManagementEventsResponse;
import com.cannontech.msp.beans.v5.dr_server.IsLoadManagementActive;
import com.cannontech.msp.beans.v5.dr_server.IsLoadManagementActiveResponse;
import com.cannontech.msp.beans.v5.dr_server.ObjectFactory;
import com.cannontech.msp.beans.v5.dr_server.PingURL;
import com.cannontech.msp.beans.v5.dr_server.PingURLResponse;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.DR_Server;

@Endpoint("DRServiceEndPointV5")
@RequestMapping("/multispeak/v5/DR_Server")
public class DRServiceEndpoint {
    @Autowired private DR_Server dr_Server;
    @Autowired private ObjectFactory objectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    public static final String DR_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/DR_Server";

    @PayloadRoot(localPart = "PingURL", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL)
            throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();
        dr_Server.pingURL();
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = objectFactory.createGetMethodsResponse();
        List<String> methodNames = dr_Server.getMethods();
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;
    }

    @PayloadRoot(localPart = "GetAllSubstationLoadControlStatuses", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllSubstationLoadControlStatusesResponse getAllSubstationLoadControlStatuses(
            @RequestPayload GetAllSubstationLoadControlStatuses getAllSubstationLoadControlStatuses)
            throws MultispeakWebServiceException {
        GetAllSubstationLoadControlStatusesResponse response =
            objectFactory.createGetAllSubstationLoadControlStatusesResponse();
        List<SubstationLoadControlStatus> substationLoadControlStatus = dr_Server.getAllSubstationLoadControlStatuses();

        ArrayOfSubstationLoadControlStatus arrayOfSubstationLoadControlStatus =
            commonObjectFactory.createArrayOfSubstationLoadControlStatus();
        arrayOfSubstationLoadControlStatus.getSubstationLoadControlStatus().addAll(substationLoadControlStatus);
        response.setArrayOfSubstationLoadControlStatus(arrayOfSubstationLoadControlStatus);
        return response;
    }

    @PayloadRoot(localPart = "InitiateLoadManagementEvents", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateLoadManagementEventsResponse initiateLoadManagementEvents(
            @RequestPayload InitiateLoadManagementEvents initiateLoadManagementEvents)
            throws MultispeakWebServiceException {
        InitiateLoadManagementEventsResponse response = objectFactory.createInitiateLoadManagementEventsResponse();
        if (initiateLoadManagementEvents.getArrayOfLoadManagementEvent() != null) {
            List<LoadManagementEvent> loadManagementEvents =
                (null != initiateLoadManagementEvents.getArrayOfLoadManagementEvent().getLoadManagementEvent())
                    ? initiateLoadManagementEvents.getArrayOfLoadManagementEvent().getLoadManagementEvent() : null;
            multispeakFuncs.addErrorObjectsInResponseHeader(dr_Server.initiateLoadManagementEvents(loadManagementEvents));

        }
        return response;
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetDomainNamesResponse getDomainNames(@RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllLoadManagementDevices", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllLoadManagementDevicesResponse getAllLoadManagementDevices(
            @RequestPayload GetAllLoadManagementDevices getAllLoadManagementDevices)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControllableLoad", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAmountOfControllableLoadResponse getAmountOfControllableLoad(
            @RequestPayload GetAmountOfControllableLoad getAmountOfControllableLoad)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAmountOfControlledLoad", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAmountOfControlledLoadResponse getAmountOfControlledLoad(
            @RequestPayload GetAmountOfControlledLoad getAmountOfControlledLoad) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDevicesByMeterIDs", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLoadManagementDevicesByMeterIDsResponse getLoadManagementDevicesByMeterIDs(
            @RequestPayload GetLoadManagementDevicesByMeterIDs getLoadManagementDevicesByMeterIDs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLoadManagementDeviceByServLoc", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLoadManagementDevicesByServiceLocationIDsResponse getLoadManagementDeviceByServLoc(
            @RequestPayload GetLoadManagementDevicesByServiceLocationIDs getLoadManagementDeviceByServLoc)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiatePowerFactorManagementEvents", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiatePowerFactorManagementEventsResponse initiatePowerFactorManagementEvent(
            @RequestPayload InitiatePowerFactorManagementEvents initiatePowerFactorManagementEvents)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "IsLoadManagementActive", namespace = DR_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload IsLoadManagementActiveResponse isLoadManagementActive(
            @RequestPayload IsLoadManagementActive isLoadManagementActive) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

}
