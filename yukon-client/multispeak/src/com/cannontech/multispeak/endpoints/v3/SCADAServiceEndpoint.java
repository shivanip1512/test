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
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
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
    public void getDomainNames() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainMembers", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getDomainMembers() throws MultispeakWebServiceException {
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

    @PayloadRoot(localPart = "GetSCADAAnalogBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAAnalogBySCADAPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllSCADAStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAllSCADAStatus() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusBySCADAPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAStatusBySCADAPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getAllSCADAPoints() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetModifiedSCADAPoints", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getModifiedSCADAPoints() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAAnalogsByDateRangeAndPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAStatusesByDateRangeAndPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRange", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAStatusesByDateRange() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAAnalogsByDateRangeAndPointIDFormattedBlock()
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointIDFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAStatusesByDateRangeAndPointIDFormattedBlock()
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeFormattedBlock", namespace = MultispeakDefines.NAMESPACE_v3)
    public void getSCADAStatusesByDateRangeFormattedBlock() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateStatusReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateStatusReadByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateAnalogReadByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateAnalogReadByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateControl", namespace = MultispeakDefines.NAMESPACE_v3)
    public void initiateControl() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "OutageEventChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void outageEventChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "PointSubscriptionListNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void pointSubscriptionListNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void analogChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "StatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void statusChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAAnalogChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAStatusChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "AccumulatedValueChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void accumulatedValueChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotification", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAPointChangedNotification() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForAnalog", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAPointChangedNotificationForAnalog() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAPointChangedNotificationForStatus", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAPointChangedNotificationForStatus() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAAnalogChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForPower", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAAnalogChangedNotificationForPower() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAAnalogChangedNotificationForVoltage", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAAnalogChangedNotificationForVoltage() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "SCADAStatusChangedNotificationByPointID", namespace = MultispeakDefines.NAMESPACE_v3)
    public void SCADAStatusChangedNotificationByPointID() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "ControlActionCompleted", namespace = MultispeakDefines.NAMESPACE_v3)
    public void controlActionCompleted() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}