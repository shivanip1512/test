package com.cannontech.multispeak.dao.impl.v4;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v4.CDClient;
import com.cannontech.multispeak.client.core.v4.DRClient;
import com.cannontech.multispeak.client.core.v4.MRClient;
import com.cannontech.multispeak.client.core.v4.ODClient;
import com.cannontech.multispeak.client.core.v4.SCADAClient;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.google.common.collect.Lists;

public class MspObjectDaoImpl implements MspObjectDao {
    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @Autowired private MRClient mrClient;
    @Autowired private ODClient odClient;
    @Autowired private CDClient cdClient;
    @Autowired private DRClient drClient;
    @Autowired private SCADAClient scadaClient;

    @Override
    public ErrorObject[] toErrorObject(List<ErrorObject> errorObjects) {

        if (!errorObjects.isEmpty()) {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }

    @Override
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException {

        ErrorObject[] objects = new ErrorObject[] {};
        List<ErrorObject> errorObjects = new ArrayList<>();

        PingURL pingURL = objectFactory.createPingURL();
        PingURLResponse response;

        if (service.contains(MultispeakDefines.MR_Server_STR)) {
            response = mrClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.OD_Server_STR)) {
            response = odClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.CD_Server_STR)) {
            response = cdClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.DR_Server_STR)) {
            response = drClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.SCADA_Server_STR)) {
            response = scadaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else {
            ErrorObject obj = new ErrorObject();
            obj.setObjectID("-100");
            obj.setErrorString("No server for " + service);
            obj.setNounType(null);
            obj.setEventTime(null);
            return new ErrorObject[] { obj };
        }

        if (response != null && response.getPingURLResult() != null) {
            errorObjects = response.getPingURLResult().getErrorObject();
            objects = toErrorObject(errorObjects);
        }
        return objects;

    }

    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String service, String endpointUrl)
            throws MultispeakWebServiceClientException {
        if (endpointUrl == null) {
            endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);
        }
        List<String> methods = new ArrayList<>();

        GetMethods getMethods = objectFactory.createGetMethods();
        GetMethodsResponse response;

        if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            response = mrClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            response = odClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            response = cdClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.DR_Server_STR)) {
            response = drClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            response = scadaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else {
            String string = "No server for " + service;
            return Lists.newArrayList(string);
        }

        if (response != null && response.getGetMethodsResult() != null) {
            methods = response.getGetMethodsResult().getString();
        }

        return methods;
    }
}
