package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfSCADAAnalog;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.scada_server.GetAllDomains;
import com.cannontech.msp.beans.v5.scada_server.GetAllDomainsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetAllSCADAPoints;
import com.cannontech.msp.beans.v5.scada_server.GetAllSCADAPointsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetAttachmentsByObjectRefs;
import com.cannontech.msp.beans.v5.scada_server.GetAttachmentsByObjectRefsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetDomainNames;
import com.cannontech.msp.beans.v5.scada_server.GetDomainNamesResponse;
import com.cannontech.msp.beans.v5.scada_server.GetDomainsByDomainNames;
import com.cannontech.msp.beans.v5.scada_server.GetDomainsByDomainNamesResponse;
import com.cannontech.msp.beans.v5.scada_server.GetFormattedBlockTemplates;
import com.cannontech.msp.beans.v5.scada_server.GetFormattedBlockTemplatesResponse;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAAnalogs;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAAnalogsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAStatuses;
import com.cannontech.msp.beans.v5.scada_server.GetLatestSCADAStatusesResponse;
import com.cannontech.msp.beans.v5.scada_server.GetMethods;
import com.cannontech.msp.beans.v5.scada_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetObjectRefsByNounAndPrimaryIdentifiers;
import com.cannontech.msp.beans.v5.scada_server.GetObjectRefsByNounAndPrimaryIdentifiersResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAAnalogsByDateRangeAndPointIDs;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAAnalogsByDateRangeAndPointIDsFormattedBlocks;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAAnalogsByDateRangeAndPointIDsFormattedBlocksResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAAnalogsByDateRangeAndPointIDsResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAPointCollectionChanges;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAPointCollectionChangesResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAPointsByType;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAPointsByTypeResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAStatusesByDateRangeAndPointIDs;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAStatusesByDateRangeAndPointIDsFormattedBlock;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAStatusesByDateRangeAndPointIDsFormattedBlockResponse;
import com.cannontech.msp.beans.v5.scada_server.GetSCADAStatusesByDateRangeAndPointIDsResponse;
import com.cannontech.msp.beans.v5.scada_server.InitiateControl;
import com.cannontech.msp.beans.v5.scada_server.InitiateControlResponse;
import com.cannontech.msp.beans.v5.scada_server.InitiateDisplayObjectHighlighting;
import com.cannontech.msp.beans.v5.scada_server.InitiateDisplayObjectHighlightingResponse;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAAnalogReadsByPointIDs;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAAnalogReadsByPointIDsResponse;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAPointSubscription;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAPointSubscriptionResponse;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAStatusReadsByPointIDs;
import com.cannontech.msp.beans.v5.scada_server.InitiateSCADAStatusReadsByPointIDsResponse;
import com.cannontech.msp.beans.v5.scada_server.LinkAttachmentsToObjects;
import com.cannontech.msp.beans.v5.scada_server.LinkAttachmentsToObjectsResponse;
import com.cannontech.msp.beans.v5.scada_server.ObjectFactory;
import com.cannontech.msp.beans.v5.scada_server.PingURL;
import com.cannontech.msp.beans.v5.scada_server.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.SCADA_Server;

/*
 * This class is the SCADA Service endpoint all requests will be processed from
 * here.
 */
@Endpoint("SCADAServiceEndpointV5")
@RequestMapping("/soap/SCADA_ServerSoap_v5")
public class SCADAServiceEndpoint {
    @Autowired private SCADA_Server scada_Server;
    @Autowired private ObjectFactory scadaObjectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private final String SCADA_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/SCADA_Server";

    @PayloadRoot(localPart = "PingURL", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    PingURLResponse pingURL(@RequestPayload PingURL pingURL) throws MultispeakWebServiceException {
        PingURLResponse response = scadaObjectFactory.createPingURLResponse();
        scada_Server.pingURL();
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods) throws MultispeakWebServiceException {
        GetMethodsResponse response = scadaObjectFactory.createGetMethodsResponse();
        List<String> methodNames  = scada_Server.getMethods();
        
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;
    }

    @PayloadRoot(localPart = "GetLatestSCADAAnalogs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload
    GetLatestSCADAAnalogsResponse getLatestSCADAAnalogs(@RequestPayload GetLatestSCADAAnalogs getLatestSCADAAnalogs)
            throws MultispeakWebServiceException {
        GetLatestSCADAAnalogsResponse response = scadaObjectFactory.createGetLatestSCADAAnalogsResponse();
        String lastReceived = getLatestSCADAAnalogs.getLastReceived();
        ArrayOfSCADAAnalog scadaAnalogs = commonObjectFactory.createArrayOfSCADAAnalog();
        scadaAnalogs.getSCADAAnalog().addAll(scada_Server.getLatestSCADAAnalogs(lastReceived));
        response.setArrayOfSCADAAnalog(scadaAnalogs);;
        return response;
    }

    @PayloadRoot(localPart = "GetAllSCADAPoints", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllSCADAPointsResponse getAllSCADAPoints(
            @RequestPayload GetAllSCADAPoints getAllSCADAPoints) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAllDomains", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllDomainsResponse getAllDomains(
            @RequestPayload GetAllDomains getAllDomains) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAttachmentsByObjectRefs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAttachmentsByObjectRefsResponse getAttachmentsByObjectRefs(
            @RequestPayload GetAttachmentsByObjectRefs getAttachmentsByObjectRefs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetDomainNamesResponse getDomainNames(
            @RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetDomainsByDomainNames", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetDomainsByDomainNamesResponse getDomainsByDomainNames(
            @RequestPayload GetDomainsByDomainNames getDomainsByDomainNames)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "GetFormattedBlockTemplates", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetFormattedBlockTemplatesResponse getFormattedBlockTemplates(
            @RequestPayload GetFormattedBlockTemplates getFormattedBlockTemplates)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetLatestSCADAStatuses", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetLatestSCADAStatusesResponse getLatestSCADAStatuses(
            @RequestPayload GetLatestSCADAStatuses getLatestSCADAStatuses)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetObjectRefsByNounAndPrimaryIdentifiers", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetObjectRefsByNounAndPrimaryIdentifiersResponse getObjectRefsByNounAndPrimaryIdentifiers(
            @RequestPayload GetObjectRefsByNounAndPrimaryIdentifiers getObjectRefsByNounAndPrimaryIdentifiers)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointIDs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAAnalogsByDateRangeAndPointIDsResponse getSCADAAnalogsByDateRangeAndPointIDs(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointIDs  getSCADAAnalogsByDateRangeAndPointIDs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @PayloadRoot(localPart = "GetSCADAAnalogsByDateRangeAndPointIDsFormattedBlocks", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAAnalogsByDateRangeAndPointIDsFormattedBlocksResponse getSCADAAnalogsByDateRangeAndPointIDsFormattedBlocks(
            @RequestPayload GetSCADAAnalogsByDateRangeAndPointIDsFormattedBlocks  getSCADAAnalogsByDateRangeAndPointIDsFormattedBlocks)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAPointCollectionChanges", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAPointCollectionChangesResponse getSCADAPointCollectionChanges(
            @RequestPayload GetSCADAPointCollectionChanges getSCADAPointCollectionChanges)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    
    @PayloadRoot(localPart = "GetSCADAPointsByType", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAPointsByTypeResponse getSCADAPointsByType(
            @RequestPayload GetSCADAPointsByType getSCADAPointsByType)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointIDs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAStatusesByDateRangeAndPointIDsResponse getSCADAStatusesByDateRangeAndPointIDs(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointIDs getSCADAStatusesByDateRangeAndPointIDs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetSCADAStatusesByDateRangeAndPointIDsFormattedBlock", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetSCADAStatusesByDateRangeAndPointIDsFormattedBlockResponse getSCADAStatusesByDateRangeAndPointIDsFormattedBlock(
            @RequestPayload GetSCADAStatusesByDateRangeAndPointIDsFormattedBlock getSCADAStatusesByDateRangeAndPointIDsFormattedBlock)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateControl", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateControlResponse initiateControl(
            @RequestPayload InitiateControl initiateControl)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateDisplayObjectHighlighting", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateDisplayObjectHighlightingResponse initiateDisplayObjectHighlighting(
            @RequestPayload InitiateDisplayObjectHighlighting initiateDisplayObjectHighlighting)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");

    }

    @PayloadRoot(localPart = "InitiateSCADAAnalogReadsByPointIDs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateSCADAAnalogReadsByPointIDsResponse initiateSCADAAnalogReadsByPointIDs(
            @RequestPayload InitiateSCADAAnalogReadsByPointIDs initiateSCADAAnalogReadsByPointIDs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateSCADAPointSubscription", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateSCADAPointSubscriptionResponse initiateSCADAPointSubscription(
            @RequestPayload InitiateSCADAPointSubscription initiateSCADAPointSubscription)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "InitiateSCADAStatusReadsByPointIDs", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateSCADAStatusReadsByPointIDsResponse initiateSCADAStatusReadsByPointIDs(
            @RequestPayload InitiateSCADAStatusReadsByPointIDs initiateSCADAStatusReadsByPointIDs)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LinkAttachmentsToObjects", namespace = SCADA_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LinkAttachmentsToObjectsResponse linkAttachmentsToObjects(
            @RequestPayload LinkAttachmentsToObjects linkAttachmentsToObjects)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}