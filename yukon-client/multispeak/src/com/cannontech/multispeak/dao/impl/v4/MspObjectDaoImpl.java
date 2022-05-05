package com.cannontech.multispeak.dao.impl.v4;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.msp.beans.v4.ArrayOfDomainMember;
import com.cannontech.msp.beans.v4.DomainMember;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GetDomainMembers;
import com.cannontech.msp.beans.v4.GetDomainMembersResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v4.CBClient;
import com.cannontech.multispeak.client.core.v4.CDClient;
import com.cannontech.multispeak.client.core.v4.DRClient;
import com.cannontech.multispeak.client.core.v4.EAClient;
import com.cannontech.multispeak.client.core.v4.MDMClient;
import com.cannontech.multispeak.client.core.v4.MRClient;
import com.cannontech.multispeak.client.core.v4.NOTClient;
import com.cannontech.multispeak.client.core.v4.OAClient;
import com.cannontech.multispeak.client.core.v4.ODClient;
import com.cannontech.multispeak.client.core.v4.SCADAClient;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.google.common.collect.Lists;

public class MspObjectDaoImpl implements MspObjectDao {
    private static final Logger log = YukonLogManager.getLogger(MspObjectDaoImpl.class);

    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @Autowired private MRClient mrClient;
    @Autowired private ODClient odClient;
    @Autowired private CDClient cdClient;
    @Autowired private DRClient drClient;
    @Autowired private SCADAClient scadaClient;
    @Autowired private CBClient cbClient;
    @Autowired private EAClient eaClient;
    @Autowired private OAClient oaClient;
    @Autowired private MDMClient mdmClient;
    @Autowired private NOTClient notClient;
    private SystemLogHelper _systemLogHelper = null;

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null) {
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        }
        return _systemLogHelper;
    }

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
        } else if (service.contains(MultispeakDefines.CB_Server_STR)) {
            response = cbClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.EA_Server_STR)) {
            response = eaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.OA_Server_STR)) {
            response = oaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.MDM_Server_STR)) {
            response = mdmClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.contains(MultispeakDefines.NOT_Server_STR)) {
            response = notClient.pingURL(mspVendor, endpointUrl, pingURL);
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
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            response = cbClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            response = eaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            response = oaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            response = mdmClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.NOT_Server_STR)) {
            response = notClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else {
            String string = "No server for " + service;
            return Lists.newArrayList(string);
        }

        if (response != null && response.getGetMethodsResult() != null) {
            methods = response.getGetMethodsResult().getString();
        }

        return methods;
    }

    @Override
    public void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName,
                SystemLog.TYPE_MULTISPEAK);
        log.debug("MSP Activity (Method: " + method + "-" + description + " )");
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName, String exceptionMessage) {
        ErrorObject errorObject = getErrorObject(objectID, notFoundObjectType + ": " + objectID + " - " + exceptionMessage + ".",
                nounType,
                method, userName);
        return errorObject;
    }

    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(new Date()));

        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);

        String description = "ErrorObject: (ObjId:" + errorObject.getObjectID() + " Noun:" + errorObject.getNounType()
                + " Message:"
                + errorObject.getErrorString() + ")";
        logMSPActivity(method, description, userName);

        return errorObject;
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName) {
        return getNotFoundErrorObject(objectID, notFoundObjectType, nounType, method, userName,
                "Was NOT found in Yukon");
    }

    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        List<String> substationNames = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetDomainMembers domainMembersRequest = objectFactory.createGetDomainMembers();
            domainMembersRequest.setDomainName("substationCode");
            GetDomainMembersResponse domainMembersResponse = cbClient.getDomainMembers(mspVendor, endpointUrl,
                    domainMembersRequest);
            if (domainMembersResponse != null) {
                ArrayOfDomainMember arrayOfDomainMember = domainMembersResponse.getGetDomainMembersResult();
                if (arrayOfDomainMember != null) {
                    List<DomainMember> domainMemberList = arrayOfDomainMember.getDomainMember();
                    if (domainMemberList != null && !domainMemberList.isEmpty()) {
                        domainMemberList.forEach(domainMember -> {
                            substationNames.add(domainMember.getDescription());
                        });
                    }
                }
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getDomainMembers(" + mspVendor.getCompanyName()
                    + ") for DomainMember 'substationCode'");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return substationNames;
    }

}
