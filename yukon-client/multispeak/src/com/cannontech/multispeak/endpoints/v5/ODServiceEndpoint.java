package com.cannontech.multispeak.endpoints.v5;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.cannontech.msp.beans.v5.commonarrays.ArrayOfObjectRef;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfString;
import com.cannontech.msp.beans.v5.od_server.GetAllDomains;
import com.cannontech.msp.beans.v5.od_server.GetAllDomainsResponse;
import com.cannontech.msp.beans.v5.od_server.GetAttachmentsByObjectRefs;
import com.cannontech.msp.beans.v5.od_server.GetAttachmentsByObjectRefsResponse;
import com.cannontech.msp.beans.v5.od_server.GetDomainNames;
import com.cannontech.msp.beans.v5.od_server.GetDomainNamesResponse;
import com.cannontech.msp.beans.v5.od_server.GetDomainsByDomainNames;
import com.cannontech.msp.beans.v5.od_server.GetDomainsByDomainNamesResponse;
import com.cannontech.msp.beans.v5.od_server.GetMeterIDsByEndDeviceStateTypes;
import com.cannontech.msp.beans.v5.od_server.GetMeterIDsByEndDeviceStateTypesResponse;
import com.cannontech.msp.beans.v5.od_server.GetMethods;
import com.cannontech.msp.beans.v5.od_server.GetMethodsResponse;
import com.cannontech.msp.beans.v5.od_server.GetObjectRefsByNounAndPrimaryIdentifiers;
import com.cannontech.msp.beans.v5.od_server.GetObjectRefsByNounAndPrimaryIdentifiersResponse;
import com.cannontech.msp.beans.v5.od_server.InitiateEndDevicePings;
import com.cannontech.msp.beans.v5.od_server.InitiateEndDevicePingsResponse;
import com.cannontech.msp.beans.v5.od_server.LinkAttachmentsToObjects;
import com.cannontech.msp.beans.v5.od_server.LinkAttachmentsToObjectsResponse;
import com.cannontech.msp.beans.v5.od_server.ObjectFactory;
import com.cannontech.msp.beans.v5.od_server.PingURL;
import com.cannontech.msp.beans.v5.od_server.PingURLResponse;
import com.cannontech.msp.beans.v5.od_server.UnlinkAttachmentsFromObjects;
import com.cannontech.msp.beans.v5.od_server.UnlinkAttachmentsFromObjectsResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.OD_Server;

@Endpoint("ODServiceEndpointV5")
@RequestMapping("/soap/OD_ServerSoap_v5")
public class ODServiceEndpoint {
    @Autowired private OD_Server od_Server;
    @Autowired private ObjectFactory odObjectFactory;
    @Autowired private com.cannontech.msp.beans.v5.commonarrays.ObjectFactory commonObjectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;
    private static final String OD_V5_ENDPOINT_NAMESPACE = MultispeakDefines.NAMESPACE_v5 + "/wsdl/OD_Server";

    @PayloadRoot(localPart = "PingURL", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload PingURLResponse pingURL(@RequestPayload PingURL pingURL)
            throws MultispeakWebServiceException {
        PingURLResponse response = odObjectFactory.createPingURLResponse();
        od_Server.pingURL();
        return response;
    }

    @PayloadRoot(localPart = "GetMethods", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMethodsResponse getMethods(@RequestPayload GetMethods getMethods)
            throws MultispeakWebServiceException {
        GetMethodsResponse response = odObjectFactory.createGetMethodsResponse();
        List<String> methodNames = od_Server.getMethods();
        ArrayOfString methods = commonObjectFactory.createArrayOfString();
        methods.getTheString().addAll(methodNames);
        response.setArrayOfString(methods);
        return response;
    }

    @PayloadRoot(localPart = "InitiateEndDevicePings", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload InitiateEndDevicePingsResponse initiateEndDevicePingsRequest(
            @RequestPayload InitiateEndDevicePings initiateEndDevicePings)
            throws MultispeakWebServiceException {
        InitiateEndDevicePingsResponse response = odObjectFactory.createInitiateEndDevicePingsResponse();
        ArrayOfObjectRef arrayOfObjectRef = (null != initiateEndDevicePings.getArrayOfObjectRef()) ? initiateEndDevicePings.getArrayOfObjectRef() : null;
        String responseURL = initiateEndDevicePings.getResponseURL();
        String transactionID = initiateEndDevicePings.getTransactionID();
        XMLGregorianCalendar expirationTime = initiateEndDevicePings.getExpirationTime();

        multispeakFuncs.addErrorObjectsInResponseHeader(od_Server.initiateEndDevicePings(arrayOfObjectRef,
                                                                                         responseURL,
                                                                                         transactionID,
                                                                                         expirationTime));

        return response;
    }

    @PayloadRoot(localPart = "GetAllDomains", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAllDomainsResponse getAllDomainsResponse(
            @RequestPayload GetAllDomains getAllDomains) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetAttachmentsByObjectRefs", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetAttachmentsByObjectRefsResponse getAttachmentsByObjectRefsResponse(
            @RequestPayload GetAttachmentsByObjectRefs getAttachmentsByObjectRefs) throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainNames", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetDomainNamesResponse  getDomainNamesResponse (
            @RequestPayload GetDomainNames getDomainNames)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetDomainsByDomainNames", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetDomainsByDomainNamesResponse getDomainsByDomainNamesResponse(
            @RequestPayload GetDomainsByDomainNames  getDomainsByDomainNames )
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetMeterIDsByEndDeviceStateTypes", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetMeterIDsByEndDeviceStateTypesResponse getMeterIDsByEndDeviceStateTypesResponse(
            @RequestPayload GetMeterIDsByEndDeviceStateTypes getMeterIDsByEndDeviceStateTypes)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "GetObjectRefsByNounAndPrimaryIdentifiers", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload GetObjectRefsByNounAndPrimaryIdentifiersResponse  getObjectRefsByNounAndPrimaryIdentifiersResponse (
            @RequestPayload GetObjectRefsByNounAndPrimaryIdentifiers getObjectRefsByNounAndPrimaryIdentifiers)
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "LinkAttachmentsToObjects", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload LinkAttachmentsToObjectsResponse  linkAttachmentsToObjectsResponse (
            @RequestPayload LinkAttachmentsToObjects  linkAttachmentsToObjects )
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @PayloadRoot(localPart = "UnlinkAttachmentsFromObjects", namespace = OD_V5_ENDPOINT_NAMESPACE)
    public @ResponsePayload UnlinkAttachmentsFromObjectsResponse unlinkAttachmentsFromObjectsResponse(
            @RequestPayload UnlinkAttachmentsFromObjects  unlinkAttachmentsFromObjects )
            throws MultispeakWebServiceException {
        multispeakFuncs.init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}
