package com.cannontech.multispeak.endpoints.v3;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.AccumulatedValueChangedNotification;
import com.cannontech.msp.beans.v3.AccumulatedValueChangedNotificationResponse;
import com.cannontech.msp.beans.v3.AnalogChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.AnalogChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfScadaAnalog;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.ControlActionCompleted;
import com.cannontech.msp.beans.v3.ControlActionCompletedResponse;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetAllSCADAPoints;
import com.cannontech.msp.beans.v3.GetAllSCADAPointsResponse;
import com.cannontech.msp.beans.v3.GetAllSCADAStatus;
import com.cannontech.msp.beans.v3.GetAllSCADAStatusResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNames;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetModifiedSCADAPoints;
import com.cannontech.msp.beans.v3.GetModifiedSCADAPointsResponse;
import com.cannontech.msp.beans.v3.GetPublishMethods;
import com.cannontech.msp.beans.v3.GetPublishMethodsResponse;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByID;
import com.cannontech.msp.beans.v3.GetRegistrationInfoByIDResponse;
import com.cannontech.msp.beans.v3.GetSCADAAnalogBySCADAPointID;
import com.cannontech.msp.beans.v3.GetSCADAAnalogBySCADAPointIDResponse;
import com.cannontech.msp.beans.v3.GetSCADAAnalogsByDateRangeAndPointID;
import com.cannontech.msp.beans.v3.GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock;
import com.cannontech.msp.beans.v3.GetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResponse;
import com.cannontech.msp.beans.v3.GetSCADAAnalogsByDateRangeAndPointIDResponse;
import com.cannontech.msp.beans.v3.GetSCADAStatusBySCADAPointID;
import com.cannontech.msp.beans.v3.GetSCADAStatusBySCADAPointIDResponse;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRange;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeAndPointID;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeAndPointIDFormattedBlock;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeAndPointIDFormattedBlockResponse;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeAndPointIDResponse;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeFormattedBlock;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeFormattedBlockResponse;
import com.cannontech.msp.beans.v3.GetSCADAStatusesByDateRangeResponse;
import com.cannontech.msp.beans.v3.InitiateAnalogReadByPointID;
import com.cannontech.msp.beans.v3.InitiateAnalogReadByPointIDResponse;
import com.cannontech.msp.beans.v3.InitiateControl;
import com.cannontech.msp.beans.v3.InitiateControlResponse;
import com.cannontech.msp.beans.v3.InitiateStatusReadByPointID;
import com.cannontech.msp.beans.v3.InitiateStatusReadByPointIDResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.OutageEventChangedNotification;
import com.cannontech.msp.beans.v3.OutageEventChangedNotificationResponse;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.PointSubscriptionListNotification;
import com.cannontech.msp.beans.v3.PointSubscriptionListNotificationResponse;
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
import com.cannontech.msp.beans.v3.StatusChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.StatusChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.UnregisterForService;
import com.cannontech.msp.beans.v3.UnregisterForServiceResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.SCADA_Server;

/*
 * This class is the SCADA Service endpoint all requests will be processed from
 * here.
 */
@Endpoint
@RequestMapping("/soap/SCADA_ServerSoap")
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

    @PayloadRoot(localPart = "GetSCADAAnalogBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAAnalogBySCADAPointIDResponse getSCADAAnalogBySCADAPointID(
            @RequestPayload GetSCADAAnalogBySCADAPointID getSCADAAnalogBySCADAPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllSCADAStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAllSCADAStatusResponse getAllSCADAStatus(
            @RequestPayload GetAllSCADAStatus getAllSCADAStatus)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAStatusBySCADAPointIDResponse getSCADAStatusBySCADAPointID(
            @RequestPayload GetSCADAStatusBySCADAPointID getSCADAStatusBySCADAPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetAllSCADAPointsResponse getAllSCADAPoints(
            @RequestPayload GetAllSCADAPoints getAllSCADAPoints)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetModifiedSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetModifiedSCADAPointsResponse getModifiedSCADAPoints(
            @RequestPayload GetModifiedSCADAPoints getModifiedSCADAPoints)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAAnalogsByDateRangeAndPointIDResponse getSCADAAnalogsByDateRangeAndPointID(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointID getSCADAAnalogsByDateRangeAndPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAStatusesByDateRangeAndPointIDResponse getSCADAStatusesByDateRangeAndPointID(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointID getSCADAStatusesByDateRangeAndPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRange", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAStatusesByDateRangeResponse getSCADAStatusesByDateRange(
            @RequestPayload GetSCADAStatusesByDateRange getSCADAStatusesByDateRange)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResponse getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock getSCADAAnalogsByDateRangeAndPointIDFormattedBlock)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAStatusesByDateRangeAndPointIDFormattedBlockResponse getSCADAStatusesByDateRangeAndPointIDFormattedBlock(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointIDFormattedBlock getSCADAStatusesByDateRangeAndPointIDFormattedBlock)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload GetSCADAStatusesByDateRangeFormattedBlockResponse getSCADAStatusesByDateRangeFormattedBlock(
            @RequestPayload GetSCADAStatusesByDateRangeFormattedBlock getSCADAStatusesByDateRangeFormattedBlock)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateStatusReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateStatusReadByPointIDResponse initiateStatusReadByPointID(
            @RequestPayload InitiateStatusReadByPointID initiateStatusReadByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateAnalogReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateAnalogReadByPointIDResponse initiateAnalogReadByPointID(
            @RequestPayload InitiateAnalogReadByPointID initiateAnalogReadByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateControl", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload InitiateControlResponse initiateControl(
            @RequestPayload InitiateControl initiateControl) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload OutageEventChangedNotificationResponse outageEventChangedNotification(
            @RequestPayload OutageEventChangedNotification outageEventChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PointSubscriptionListNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload PointSubscriptionListNotificationResponse pointSubscriptionListNotification(
            @RequestPayload PointSubscriptionListNotification pointSubscriptionListNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload AnalogChangedNotificationByPointIDResponse analogChangedNotificationByPointID(
            @RequestPayload AnalogChangedNotificationByPointID analogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "StatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload StatusChangedNotificationByPointIDResponse statusChangedNotificationByPointID(
            @RequestPayload StatusChangedNotificationByPointID statusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationResponse SCADAAnalogChangedNotification(
            @RequestPayload SCADAAnalogChangedNotification analogChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAStatusChangedNotificationResponse SCADAStatusChangedNotification(
            @RequestPayload SCADAStatusChangedNotification statusChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AccumulatedValueChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload AccumulatedValueChangedNotificationResponse accumulatedValueChangedNotification(
            @RequestPayload AccumulatedValueChangedNotification accumulatedValueChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationResponse SCADAPointChangedNotification(
            @RequestPayload SCADAPointChangedNotification pointChangedNotification)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationForAnalogResponse SCADAPointChangedNotificationForAnalog(
            @RequestPayload SCADAPointChangedNotificationForAnalog pointChangedNotificationForAnalog)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAPointChangedNotificationForStatusResponse SCADAPointChangedNotificationForStatus(
            @RequestPayload SCADAPointChangedNotificationForStatus pointChangedNotificationForStatus)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationByPointIDResponse SCADAAnalogChangedNotificationByPointID(
            @RequestPayload SCADAAnalogChangedNotificationByPointID analogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationForPowerResponse SCADAAnalogChangedNotificationForPower(
            @RequestPayload SCADAAnalogChangedNotificationForPower analogChangedNotificationForPower)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAAnalogChangedNotificationForVoltageResponse SCADAAnalogChangedNotificationForVoltage(
            @RequestPayload SCADAAnalogChangedNotificationForVoltage analogChangedNotificationForVoltage)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload SCADAStatusChangedNotificationByPointIDResponse SCADAStatusChangedNotificationByPointID(
            @RequestPayload SCADAStatusChangedNotificationByPointID statusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ControlActionCompleted", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload ControlActionCompletedResponse controlActionCompleted(
            @RequestPayload ControlActionCompleted controlActionCompleted)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}