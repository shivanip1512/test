package com.cannontech.multispeak.endpoints.v3;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v3.AccumulatedValue;
import com.cannontech.msp.beans.v3.AccumulatedValueChangedNotification;
import com.cannontech.msp.beans.v3.AccumulatedValueChangedNotificationResponse;
import com.cannontech.msp.beans.v3.AnalogChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.AnalogChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.ArrayOfFormattedBlock;
import com.cannontech.msp.beans.v3.ArrayOfScadaAnalog;
import com.cannontech.msp.beans.v3.ArrayOfScadaPoint1;
import com.cannontech.msp.beans.v3.ArrayOfScadaStatus;
import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.ControlActionCompleted;
import com.cannontech.msp.beans.v3.ControlActionCompletedResponse;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotification;
import com.cannontech.msp.beans.v3.DomainMembersChangedNotificationResponse;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotification;
import com.cannontech.msp.beans.v3.DomainNamesChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetAllSCADAPoints;
import com.cannontech.msp.beans.v3.GetAllSCADAPointsResponse;
import com.cannontech.msp.beans.v3.GetAllSCADAStatus;
import com.cannontech.msp.beans.v3.GetAllSCADAStatusResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetDomainNamesResponse;
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
import com.cannontech.msp.beans.v3.ListItem;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.OutageEvent;
import com.cannontech.msp.beans.v3.OutageEventChangedNotification;
import com.cannontech.msp.beans.v3.OutageEventChangedNotificationResponse;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.PointSubscriptionListNotification;
import com.cannontech.msp.beans.v3.PointSubscriptionListNotificationResponse;
import com.cannontech.msp.beans.v3.RegisterForService;
import com.cannontech.msp.beans.v3.RegisterForServiceResponse;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.RequestRegistrationIDResponse;
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
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationByPointID;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationByPointIDResponse;
import com.cannontech.msp.beans.v3.SCADAStatusChangedNotificationResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.ScadaControl;
import com.cannontech.msp.beans.v3.ScadaPoint;
import com.cannontech.msp.beans.v3.ScadaStatus;
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
    PingURLResponse pingURL() throws MultispeakWebServiceException {
        PingURLResponse response = objectFactory.createPingURLResponse();

        scada_Server.pingURL();
        
        ArrayOfErrorObject arrOfErrorObj = objectFactory.createArrayOfErrorObject();
        response.setPingURLResult(arrOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetMethodsResponse getMethods() throws MultispeakWebServiceException {
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
    public @ResponsePayload
    GetDomainNamesResponse getDomainNames() throws MultispeakWebServiceException {
        GetDomainNamesResponse response = objectFactory.createGetDomainNamesResponse();
        List<String> domains = scada_Server.getDomainNames();
        ArrayOfString domainArray = objectFactory.createArrayOfString();
        domainArray.getString().addAll(domains);
        response.setGetDomainNamesResult(domainArray);
        return response;
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetDomainMembersResponse getDomainMembers(@RequestPayload GetDomainMembers getDomainMembers)
            throws MultispeakWebServiceException {
        GetDomainMembersResponse response = objectFactory.createGetDomainMembersResponse();
        List<DomainMember> domainMembers = scada_Server.getDomainMembers(getDomainMembers.getDomainName());
        
        ArrayOfDomainMember domainMemberArray = objectFactory.createArrayOfDomainMember();
        domainMemberArray.getDomainMember().addAll(domainMembers);
        response.setGetDomainMembersResult(domainMemberArray);
        return response;
    }

    @PayloadRoot(localPart = "RequestRegistrationID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RequestRegistrationIDResponse requestRegistrationID() throws MultispeakWebServiceException {
        RequestRegistrationIDResponse response = objectFactory.createRequestRegistrationIDResponse();
        String registrationResult = scada_Server.requestRegistrationID();
        response.setRequestRegistrationIDResult(registrationResult);
        return response;
    }

    @PayloadRoot(localPart = "RegisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    RegisterForServiceResponse registerForService(@RequestPayload RegisterForService registerForService)
            throws MultispeakWebServiceException {
        RegisterForServiceResponse response = objectFactory.createRegisterForServiceResponse();
        List<ErrorObject> errorObj = scada_Server.registerForService(registerForService.getRegistrationDetails());
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setRegisterForServiceResult(arrayOfErrorObj);
        return response;

    }

    @PayloadRoot(localPart = "UnregisterForService", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    UnregisterForServiceResponse unregisterForService(@RequestPayload UnregisterForService unregisterForService)
            throws MultispeakWebServiceException {
        UnregisterForServiceResponse response = objectFactory.createUnregisterForServiceResponse();
        List<ErrorObject> errorObj = scada_Server.unregisterForService(unregisterForService.getRegistrationID());
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setUnregisterForServiceResult(arrayOfErrorObj);
        return response;

    }

    @PayloadRoot(localPart = "GetRegistrationInfoByID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetRegistrationInfoByIDResponse getRegistrationInfoByID(
            @RequestPayload GetRegistrationInfoByID getRegistrationInfoByID) throws MultispeakWebServiceException {
        GetRegistrationInfoByIDResponse response = objectFactory.createGetRegistrationInfoByIDResponse();
        RegistrationInfo regInfoResult = scada_Server.getRegistrationInfoByID(getRegistrationInfoByID.getRegistrationID());
        response.setGetRegistrationInfoByIDResult(regInfoResult);
        return response;
    }

    @PayloadRoot(localPart = "GetPublishMethods", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetPublishMethodsResponse getPublishMethods(@RequestPayload GetPublishMethods getPublishMethods)
            throws MultispeakWebServiceException {
        GetPublishMethodsResponse response = objectFactory.createGetPublishMethodsResponse();
        
        List<String> publishMethods = scada_Server.getPublishMethods();

        ArrayOfString arrayOfStringObj = objectFactory.createArrayOfString();
        arrayOfStringObj.getString().addAll(publishMethods);
        response.setGetPublishMethodsResult(arrayOfStringObj);
        return response;
    }

    @PayloadRoot(localPart = "DomainMembersChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainMembersChangedNotificationResponse domainMembersChangedNotification(
            @RequestPayload DomainMembersChangedNotification domainMembersChangedNotification)
            throws MultispeakWebServiceException {
        DomainMembersChangedNotificationResponse response = objectFactory.createDomainMembersChangedNotificationResponse();
        
        List<DomainMember> domainMembers = domainMembersChangedNotification.getChangedDomainMembers().getDomainMember();
        List<ErrorObject> errorObj = scada_Server.domainMembersChangedNotification(domainMembers);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setDomainMembersChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "DomainNamesChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    DomainNamesChangedNotificationResponse domainNamesChangedNotification(
            @RequestPayload DomainNamesChangedNotification domainNamesChangedNotification)
            throws MultispeakWebServiceException {
        DomainNamesChangedNotificationResponse response = objectFactory.createDomainNamesChangedNotificationResponse();
        
        List<DomainNameChange> domainNameChanges = domainNamesChangedNotification.getChangedDomainNames().getDomainNameChange();
        List<ErrorObject> errorObj = scada_Server.domainNamesChangedNotification(domainNameChanges);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setDomainNamesChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAAnalogBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAAnalogBySCADAPointIDResponse getSCADAAnalogBySCADAPointID(
            @RequestPayload GetSCADAAnalogBySCADAPointID getSCADAAnalogBySCADAPointID)
            throws MultispeakWebServiceException {
        GetSCADAAnalogBySCADAPointIDResponse response = objectFactory.createGetSCADAAnalogBySCADAPointIDResponse();
        ScadaAnalog scdAnalog = scada_Server.getSCADAAnalogBySCADAPointID(getSCADAAnalogBySCADAPointID.getScadaPointID());
        response.setGetSCADAAnalogBySCADAPointIDResult(scdAnalog);
        return response;
    }

    @PayloadRoot(localPart = "GetAllSCADAStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllSCADAStatusResponse getAllSCADAStatus(@RequestPayload GetAllSCADAStatus getAllSCADAStatus)
            throws MultispeakWebServiceException {
        GetAllSCADAStatusResponse response = objectFactory.createGetAllSCADAStatusResponse();
        List<ScadaStatus> scadaStatus = scada_Server.getAllSCADAStatus(getAllSCADAStatus.getLastReceived());
        
        ArrayOfScadaStatus arrayOfScada = objectFactory.createArrayOfScadaStatus();
        arrayOfScada.getScadaStatus().addAll(scadaStatus);
        response.setGetAllSCADAStatusResult(arrayOfScada);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAStatusBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAStatusBySCADAPointIDResponse getSCADAStatusBySCADAPointID(
            @RequestPayload GetSCADAStatusBySCADAPointID getSCADAStatusBySCADAPointID)
            throws MultispeakWebServiceException {
        GetSCADAStatusBySCADAPointIDResponse response = objectFactory.createGetSCADAStatusBySCADAPointIDResponse();
        ScadaStatus scadaStatus = scada_Server.getSCADAStatusBySCADAPointID(getSCADAStatusBySCADAPointID.getScadaPointID());
        response.setGetSCADAStatusBySCADAPointIDResult(scadaStatus);
        return response;
    }

    @PayloadRoot(localPart = "GetAllSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetAllSCADAPointsResponse getAllSCADAPoints(@RequestPayload GetAllSCADAPoints getAllSCADAPoints)
            throws MultispeakWebServiceException {
        GetAllSCADAPointsResponse response = objectFactory.createGetAllSCADAPointsResponse();
        List<ScadaPoint> scadaPoints = scada_Server.getAllSCADAPoints(getAllSCADAPoints.getLastReceived());
        
        ArrayOfScadaPoint1 arrayOfScada = objectFactory.createArrayOfScadaPoint1();
        arrayOfScada.getScadaPoint().addAll(scadaPoints);
        response.setGetAllSCADAPointsResult(arrayOfScada);
        return response;
    }

    @PayloadRoot(localPart = "GetModifiedSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetModifiedSCADAPointsResponse getModifiedSCADAPoints(@RequestPayload GetModifiedSCADAPoints getModifiedSCADAPoints)
            throws MultispeakWebServiceException {
        GetModifiedSCADAPointsResponse response = objectFactory.createGetModifiedSCADAPointsResponse();
        List<ScadaPoint> scadaPoints =
            scada_Server.getModifiedSCADAPoints(getModifiedSCADAPoints.getPreviousSessionID(),
                getModifiedSCADAPoints.getLastReceived());
        
        ArrayOfScadaPoint1 arrayOfScada = objectFactory.createArrayOfScadaPoint1();
        arrayOfScada.getScadaPoint().addAll(scadaPoints);
        response.setGetModifiedSCADAPointsResult(arrayOfScada);
        return response;

    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAAnalogsByDateRangeAndPointIDResponse getSCADAAnalogsByDateRangeAndPointID(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointID getSCADAAnalogsByDateRangeAndPointID)
            throws MultispeakWebServiceException {
        GetSCADAAnalogsByDateRangeAndPointIDResponse response =
            objectFactory.createGetSCADAAnalogsByDateRangeAndPointIDResponse();
        XMLGregorianCalendar startTime = getSCADAAnalogsByDateRangeAndPointID.getStartTime();
        XMLGregorianCalendar endTime = getSCADAAnalogsByDateRangeAndPointID.getEndTime();

        List<ScadaAnalog> scadaAnalogs =
            scada_Server.getSCADAAnalogsByDateRangeAndPointID(getSCADAAnalogsByDateRangeAndPointID.getScadaPointID(),
                (startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null,
                getSCADAAnalogsByDateRangeAndPointID.getSampleRate(),
                getSCADAAnalogsByDateRangeAndPointID.getLastReceived());

        ArrayOfScadaAnalog arrayOfScada = objectFactory.createArrayOfScadaAnalog();
        arrayOfScada.getScadaAnalog().addAll(scadaAnalogs);
        response.setGetSCADAAnalogsByDateRangeAndPointIDResult(arrayOfScada);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAStatusesByDateRangeAndPointIDResponse getSCADAStatusesByDateRangeAndPointID(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointID getSCADAStatusesByDateRangeAndPointID)
            throws MultispeakWebServiceException {
        GetSCADAStatusesByDateRangeAndPointIDResponse response =
            objectFactory.createGetSCADAStatusesByDateRangeAndPointIDResponse();
        XMLGregorianCalendar startTime = getSCADAStatusesByDateRangeAndPointID.getStartTime();
        XMLGregorianCalendar endTime = getSCADAStatusesByDateRangeAndPointID.getEndTime();

        List<ScadaStatus> scadaStatus  =
            scada_Server.getSCADAStatusesByDateRangeAndPointID(getSCADAStatusesByDateRangeAndPointID.getScadaPointID(),
                (startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null,
                getSCADAStatusesByDateRangeAndPointID.getSampleRate(),
                getSCADAStatusesByDateRangeAndPointID.getLastReceived());

        ArrayOfScadaStatus arrayOfScada = objectFactory.createArrayOfScadaStatus();
        arrayOfScada.getScadaStatus().addAll(scadaStatus);
        response.setGetSCADAStatusesByDateRangeAndPointIDResult(arrayOfScada);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRange", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAStatusesByDateRangeResponse getSCADAStatusesByDateRange(
            @RequestPayload GetSCADAStatusesByDateRange getSCADAStatusesByDateRange)
            throws MultispeakWebServiceException {
        GetSCADAStatusesByDateRangeResponse response = objectFactory.createGetSCADAStatusesByDateRangeResponse();
        XMLGregorianCalendar startTime = getSCADAStatusesByDateRange.getStartTime();
        XMLGregorianCalendar endTime = getSCADAStatusesByDateRange.getEndTime();
        List<ScadaStatus> scadaStatus =
            scada_Server.getSCADAStatusesByDateRange((startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null, getSCADAStatusesByDateRange.getSampleRate(),
                getSCADAStatusesByDateRange.getLastReceived());

        ArrayOfScadaStatus arrayOfScada = objectFactory.createArrayOfScadaStatus();
        arrayOfScada.getScadaStatus().addAll(scadaStatus);
        response.setGetSCADAStatusesByDateRangeResult(arrayOfScada);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResponse getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock getSCADAAnalogsByDateRangeAndPointIDFormattedBlock)
            throws MultispeakWebServiceException {
        GetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResponse response =
            objectFactory.createGetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResponse();
        XMLGregorianCalendar startTime = getSCADAAnalogsByDateRangeAndPointIDFormattedBlock.getStartTime();
        XMLGregorianCalendar endTime = getSCADAAnalogsByDateRangeAndPointIDFormattedBlock.getEndTime();

        List<FormattedBlock> formattedBlocks =
            scada_Server.getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(
                getSCADAAnalogsByDateRangeAndPointIDFormattedBlock.getScadaPointID(),
                (startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null,
                getSCADAAnalogsByDateRangeAndPointIDFormattedBlock.getSampleRate(),
                getSCADAAnalogsByDateRangeAndPointIDFormattedBlock.getLastReceived());

        ArrayOfFormattedBlock formattedBlock = objectFactory.createArrayOfFormattedBlock();
        formattedBlock.getFormattedBlock().addAll(formattedBlocks);
        response.setGetSCADAAnalogsByDateRangeAndPointIDFormattedBlockResult(formattedBlock);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAStatusesByDateRangeAndPointIDFormattedBlockResponse getSCADAStatusesByDateRangeAndPointIDFormattedBlock(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointIDFormattedBlock getSCADAStatusesByDateRangeAndPointIDFormattedBlock)
            throws MultispeakWebServiceException {
        GetSCADAStatusesByDateRangeAndPointIDFormattedBlockResponse response =
            objectFactory.createGetSCADAStatusesByDateRangeAndPointIDFormattedBlockResponse();
        XMLGregorianCalendar startTime = getSCADAStatusesByDateRangeAndPointIDFormattedBlock.getStartTime();
        XMLGregorianCalendar endTime = getSCADAStatusesByDateRangeAndPointIDFormattedBlock.getEndTime();

        List<FormattedBlock> formattedBlocks =
            scada_Server.getSCADAStatusesByDateRangeAndPointIDFormattedBlock(
                getSCADAStatusesByDateRangeAndPointIDFormattedBlock.getScadaPointID(),
                (startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null,
                getSCADAStatusesByDateRangeAndPointIDFormattedBlock.getSampleRate(),
                getSCADAStatusesByDateRangeAndPointIDFormattedBlock.getLastReceived());

        ArrayOfFormattedBlock formattedBlock = objectFactory.createArrayOfFormattedBlock();
        formattedBlock.getFormattedBlock().addAll(formattedBlocks);
        response.setGetSCADAStatusesByDateRangeAndPointIDFormattedBlockResult(formattedBlock);
        return response;
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    GetSCADAStatusesByDateRangeFormattedBlockResponse getSCADAStatusesByDateRangeFormattedBlock(
            @RequestPayload GetSCADAStatusesByDateRangeFormattedBlock getSCADAStatusesByDateRangeFormattedBlock)
            throws MultispeakWebServiceException {
        GetSCADAStatusesByDateRangeFormattedBlockResponse response =
            objectFactory.createGetSCADAStatusesByDateRangeFormattedBlockResponse();
        XMLGregorianCalendar startTime = getSCADAStatusesByDateRangeFormattedBlock.getStartTime();
        XMLGregorianCalendar endTime = getSCADAStatusesByDateRangeFormattedBlock.getEndTime();

        List<FormattedBlock> formattedBlocks =
            scada_Server.getSCADAStatusesByDateRangeFormattedBlock(
                (startTime != null) ? startTime.toGregorianCalendar() : null,
                (endTime != null) ? endTime.toGregorianCalendar() : null,
                getSCADAStatusesByDateRangeFormattedBlock.getSampleRate(),
                getSCADAStatusesByDateRangeFormattedBlock.getLastReceived());

        ArrayOfFormattedBlock formattedBlock = objectFactory.createArrayOfFormattedBlock();
        formattedBlock.getFormattedBlock().addAll(formattedBlocks);
        response.setGetSCADAStatusesByDateRangeFormattedBlockResult(formattedBlock);
        return response;
    }

    @PayloadRoot(localPart = "InitiateStatusReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateStatusReadByPointIDResponse initiateStatusReadByPointID(
            @RequestPayload InitiateStatusReadByPointID initiateStatusReadByPointID)
            throws MultispeakWebServiceException {
        InitiateStatusReadByPointIDResponse response = objectFactory.createInitiateStatusReadByPointIDResponse();
        List<String> pointIds = initiateStatusReadByPointID.getPointIDs().getString();
        List<ErrorObject> errorObj =
            scada_Server.initiateStatusReadByPointID(pointIds, initiateStatusReadByPointID.getResponseURL(),
                initiateStatusReadByPointID.getTransactionID());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setInitiateStatusReadByPointIDResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "InitiateAnalogReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateAnalogReadByPointIDResponse initiateAnalogReadByPointID(
            @RequestPayload InitiateAnalogReadByPointID initiateAnalogReadByPointID)
            throws MultispeakWebServiceException {
        InitiateAnalogReadByPointIDResponse response = objectFactory.createInitiateAnalogReadByPointIDResponse();
        List<String> pointIds = initiateAnalogReadByPointID.getPointIDs().getString();
        List<ErrorObject> errorObj =
            scada_Server.initiateAnalogReadByPointID(pointIds, initiateAnalogReadByPointID.getResponseURL(),
                initiateAnalogReadByPointID.getTransactionID());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setInitiateAnalogReadByPointIDResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "InitiateControl", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    InitiateControlResponse initiateControl(@RequestPayload InitiateControl initiateControl)
            throws MultispeakWebServiceException {
        InitiateControlResponse response = objectFactory.createInitiateControlResponse();
        ErrorObject errorObj =
            scada_Server.initiateControl(initiateControl.getControlAction(), initiateControl.getResponseURL(),
                initiateControl.getTransactionID());
        response.setInitiateControlResult(errorObj);
        return response;
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    OutageEventChangedNotificationResponse outageEventChangedNotification(
            @RequestPayload OutageEventChangedNotification outageEventChangedNotification)
            throws MultispeakWebServiceException {
        OutageEventChangedNotificationResponse response = objectFactory.createOutageEventChangedNotificationResponse();
        List<OutageEvent> outageEvents = outageEventChangedNotification.getOEvents().getOutageEvent();
        List<ErrorObject> errorObj = scada_Server.outageEventChangedNotification(outageEvents);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setOutageEventChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "PointSubscriptionListNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    PointSubscriptionListNotificationResponse pointSubscriptionListNotification(
            @RequestPayload PointSubscriptionListNotification pointSubscriptionListNotification)
            throws MultispeakWebServiceException {

        PointSubscriptionListNotificationResponse response =
            objectFactory.createPointSubscriptionListNotificationResponse();
        List<ListItem> listItems = pointSubscriptionListNotification.getPointList().getListItem();
        List<ErrorObject> errorObj =
            scada_Server.pointSubscriptionListNotification(listItems, pointSubscriptionListNotification.getResponseURL(),
                pointSubscriptionListNotification.getErrorString());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setPointSubscriptionListNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "AnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    AnalogChangedNotificationByPointIDResponse analogChangedNotificationByPointID(
            @RequestPayload AnalogChangedNotificationByPointID analogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        AnalogChangedNotificationByPointIDResponse response =
            objectFactory.createAnalogChangedNotificationByPointIDResponse();
        
        List<ScadaAnalog> scadaAnalogs = analogChangedNotificationByPointID.getScadaAnalogs().getScadaAnalog();
        List<ErrorObject> errorObj =
            scada_Server.analogChangedNotificationByPointID(scadaAnalogs,
                analogChangedNotificationByPointID.getTransactionID());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setAnalogChangedNotificationByPointIDResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "StatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    StatusChangedNotificationByPointIDResponse statusChangedNotificationByPointID(
            @RequestPayload StatusChangedNotificationByPointID statusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        StatusChangedNotificationByPointIDResponse response =
            objectFactory.createStatusChangedNotificationByPointIDResponse();
        List<ScadaStatus> scadaStatus = statusChangedNotificationByPointID.getScadaStatuses().getScadaStatus();
        List<ErrorObject> errorObj =
            scada_Server.statusChangedNotificationByPointID(scadaStatus,
                statusChangedNotificationByPointID.getTransactionID());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setStatusChangedNotificationByPointIDResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationResponse SCADAAnalogChangedNotification(
            @RequestPayload com.cannontech.msp.beans.v3.SCADAAnalogChangedNotification analogChangedNotification)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationResponse response = objectFactory.createSCADAAnalogChangedNotificationResponse();
        List<ScadaAnalog> scadaAnalogs = analogChangedNotification.getScadaAnalogs().getScadaAnalog();
        List<ErrorObject> errorObj = scada_Server.SCADAAnalogChangedNotification(scadaAnalogs);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAAnalogChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAStatusChangedNotificationResponse SCADAStatusChangedNotification(
            @RequestPayload com.cannontech.msp.beans.v3.SCADAStatusChangedNotification statusChangedNotification)
            throws MultispeakWebServiceException {
        SCADAStatusChangedNotificationResponse response = objectFactory.createSCADAStatusChangedNotificationResponse();
        List<ScadaStatus> scadaStatus = statusChangedNotification.getScadaStatuses().getScadaStatus();
        List<ErrorObject> errorObj = scada_Server.SCADAStatusChangedNotification(scadaStatus);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAStatusChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "AccumulatedValueChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    AccumulatedValueChangedNotificationResponse accumulatedValueChangedNotification(
            @RequestPayload AccumulatedValueChangedNotification accumulatedValueChangedNotification)
            throws MultispeakWebServiceException {
        AccumulatedValueChangedNotificationResponse response =
            objectFactory.createAccumulatedValueChangedNotificationResponse();
        List<AccumulatedValue> accumulatedValues = accumulatedValueChangedNotification.getAccumulators().getAccumulatedValue();
        List<ErrorObject> errorObj = scada_Server.accumulatedValueChangedNotification(accumulatedValues);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setAccumulatedValueChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationResponse SCADAPointChangedNotification(
            @RequestPayload SCADAPointChangedNotification pointChangedNotification)
            throws MultispeakWebServiceException {
        SCADAPointChangedNotificationResponse response = objectFactory.createSCADAPointChangedNotificationResponse();
        List<ScadaPoint> scadaPoints = pointChangedNotification.getScadaPoints().getScadaPoint();
        List<ErrorObject> errorObj = scada_Server.SCADAPointChangedNotification(scadaPoints);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAPointChangedNotificationResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationForAnalogResponse SCADAPointChangedNotificationForAnalog(
            @RequestPayload SCADAPointChangedNotificationForAnalog pointChangedNotificationForAnalog)
            throws MultispeakWebServiceException {
        SCADAPointChangedNotificationForAnalogResponse response =
            objectFactory.createSCADAPointChangedNotificationForAnalogResponse();
        List<ScadaPoint> scadaPoints = pointChangedNotificationForAnalog.getScadaPoints().getScadaPoint();
        List<ErrorObject> errorObj = scada_Server.SCADAPointChangedNotificationForAnalog(scadaPoints);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAPointChangedNotificationForAnalogResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAPointChangedNotificationForStatusResponse SCADAPointChangedNotificationForStatus(
            @RequestPayload SCADAPointChangedNotificationForStatus pointChangedNotificationForStatus)
            throws MultispeakWebServiceException {
        SCADAPointChangedNotificationForStatusResponse response =
            objectFactory.createSCADAPointChangedNotificationForStatusResponse();
        List<ScadaPoint> scadaPoints = pointChangedNotificationForStatus.getScadaPoints().getScadaPoint();
        List<ErrorObject> errorObj = scada_Server.SCADAPointChangedNotificationForStatus(scadaPoints);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAPointChangedNotificationForStatusResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationByPointIDResponse SCADAAnalogChangedNotificationByPointID(
            @RequestPayload SCADAAnalogChangedNotificationByPointID analogChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationByPointIDResponse response =
            objectFactory.createSCADAAnalogChangedNotificationByPointIDResponse();
        scada_Server.SCADAAnalogChangedNotificationByPointID(analogChangedNotificationByPointID.getScadaAnalog());
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationForPowerResponse SCADAAnalogChangedNotificationForPower(
            @RequestPayload SCADAAnalogChangedNotificationForPower analogChangedNotificationForPower)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationForPowerResponse response =
            objectFactory.createSCADAAnalogChangedNotificationForPowerResponse();
        List<ScadaAnalog> scadaAnalogs = analogChangedNotificationForPower.getScadaAnalogs().getScadaAnalog();
        List<ErrorObject> errorObj = scada_Server.SCADAAnalogChangedNotificationForPower(scadaAnalogs);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAAnalogChangedNotificationForPowerResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAAnalogChangedNotificationForVoltageResponse SCADAAnalogChangedNotificationForVoltage(
            @RequestPayload SCADAAnalogChangedNotificationForVoltage analogChangedNotificationForVoltage)
            throws MultispeakWebServiceException {
        SCADAAnalogChangedNotificationForVoltageResponse response =
            objectFactory.createSCADAAnalogChangedNotificationForVoltageResponse();
        List<ScadaAnalog> scadaAnalogs = analogChangedNotificationForVoltage.getScadaAnalogs().getScadaAnalog();
        List<ErrorObject> errorObj = scada_Server.SCADAAnalogChangedNotificationForVoltage(scadaAnalogs);
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setSCADAAnalogChangedNotificationForVoltageResult(arrayOfErrorObj);
        return response;
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    SCADAStatusChangedNotificationByPointIDResponse SCADAStatusChangedNotificationByPointID(
            @RequestPayload SCADAStatusChangedNotificationByPointID statusChangedNotificationByPointID)
            throws MultispeakWebServiceException {
        SCADAStatusChangedNotificationByPointIDResponse response =
            objectFactory.createSCADAStatusChangedNotificationByPointIDResponse();
        scada_Server.SCADAStatusChangedNotificationByPointID(statusChangedNotificationByPointID.getScadaStatus());
        return response;
    }

    @PayloadRoot(localPart = "ControlActionCompleted", namespace = MultispeakDefines.NAMESPACE_v3)
    public @ResponsePayload
    ControlActionCompletedResponse controlActionCompleted(@RequestPayload ControlActionCompleted controlActionCompleted)
            throws MultispeakWebServiceException {
        ControlActionCompletedResponse response = objectFactory.createControlActionCompletedResponse();
        List<ScadaControl> scadaControls = controlActionCompleted.getControlActions().getScadaControl();
        List<ErrorObject> errorObj =
            scada_Server.controlActionCompleted(scadaControls, controlActionCompleted.getTransactionID());
        
        ArrayOfErrorObject arrayOfErrorObj = multispeakFuncs.toArrayOfErrorObject(errorObj);
        response.setControlActionCompletedResult(arrayOfErrorObj);
        return response;
    }
}