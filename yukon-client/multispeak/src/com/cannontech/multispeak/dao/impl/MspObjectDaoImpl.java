package com.cannontech.multispeak.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.AbstractSoapMessage;
import org.springframework.ws.soap.SoapEnvelope;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.msp.beans.v3.ArrayOfDomainMember;
import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllServiceLocations;
import com.cannontech.msp.beans.v3.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNo;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumber;
import com.cannontech.msp.beans.v3.GetMeterByAccountNumberResponse;
import com.cannontech.msp.beans.v3.GetMeterByCustID;
import com.cannontech.msp.beans.v3.GetMeterByCustIDResponse;
import com.cannontech.msp.beans.v3.GetMeterByMeterNo;
import com.cannontech.msp.beans.v3.GetMeterByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetMeterByServLoc;
import com.cannontech.msp.beans.v3.GetMeterByServLocResponse;
import com.cannontech.msp.beans.v3.GetMetersByEALocation;
import com.cannontech.msp.beans.v3.GetMetersByEALocationResponse;
import com.cannontech.msp.beans.v3.GetMetersByFacilityID;
import com.cannontech.msp.beans.v3.GetMetersByFacilityIDResponse;
import com.cannontech.msp.beans.v3.GetMetersBySearchString;
import com.cannontech.msp.beans.v3.GetMetersBySearchStringResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNo;
import com.cannontech.msp.beans.v3.GetServiceLocationByMeterNoResponse;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MessageContextHolder;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.CBClient;
import com.cannontech.multispeak.client.core.CDClient;
import com.cannontech.multispeak.client.core.EAClient;
import com.cannontech.multispeak.client.core.LMClient;
import com.cannontech.multispeak.client.core.MDMClient;
import com.cannontech.multispeak.client.core.MRClient;
import com.cannontech.multispeak.client.core.OAClient;
import com.cannontech.multispeak.client.core.ODClient;
import com.cannontech.multispeak.client.core.SCADAClient;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.google.common.collect.Lists;

public class MspObjectDaoImpl implements MspObjectDao {
    private static final Logger log = YukonLogManager.getLogger(MspObjectDaoImpl.class);

    @Autowired private ObjectFactory objectFactory;
    @Autowired private MultispeakFuncs multispeakFuncs;

    @Autowired private CBClient cbClient;
    @Autowired private CDClient cdClient;
    @Autowired private EAClient eaClient;
    @Autowired private MDMClient mdmClient;
    @Autowired private OAClient oaClient;
    @Autowired private ODClient odClient;  
    @Autowired private MRClient mrClient;
    @Autowired private LMClient lmClient;
    @Autowired private SCADAClient scadaClient;
    
    
    private SystemLogHelper _systemLogHelper = null;

    private SystemLogHelper getSystemLogHelper() {
        if (_systemLogHelper == null) {
            _systemLogHelper = new SystemLogHelper(PointTypes.SYS_PID_MULTISPEAK);
        }
        return _systemLogHelper;
    }

    @Override
    public Customer getMspCustomer(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }

    @Override
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        Customer mspCustomer = new Customer();
        GetCustomerByMeterNo getCustomerByMeterNo = objectFactory.createGetCustomerByMeterNo();
        getCustomerByMeterNo.setMeterNo(meterNumber);
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetCustomerByMeterNoResponse getCustomerByMeterNoResponse =
                cbClient.getCustomerByMeterNo(mspVendor, endpointUrl, getCustomerByMeterNo);
            mspCustomer = getCustomerByMeterNoResponse.getGetCustomerByMeterNoResult();
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getCustomerByMeterNo(" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for Customer");
        }
        return mspCustomer;
    }

    @Override
    public ServiceLocation getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }

    @Override
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
       ServiceLocation mspServiceLocation = new ServiceLocation();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            GetServiceLocationByMeterNo getServiceLocationByMeterNo =
                    objectFactory.createGetServiceLocationByMeterNo();
            getServiceLocationByMeterNo.setMeterNo(meterNumber);
            log.debug("Calling " + mspVendor.getCompanyName()
                + " CB_Server.getServiceLocationByMeterNo for meterNumber: " + meterNumber);
            GetServiceLocationByMeterNoResponse getServiceLocationByMeterNoResponse =
                cbClient.getServiceLocationByMeterNo(mspVendor, endpointUrl, getServiceLocationByMeterNo);
            mspServiceLocation = getServiceLocationByMeterNoResponse.getGetServiceLocationByMeterNoResult();
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");
        }
        return mspServiceLocation;
    }

    @Override
    public Meter getMspMeter(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspMeter(meter.getMeterNumber(), mspVendor);
    }

    @Override
    public Meter getMspMeter(String meterNumber, MultispeakVendor mspVendor) {
        Meter mspMeter = new Meter();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMeterByMeterNo getMeterByMeterNo = objectFactory.createGetMeterByMeterNo();
            getMeterByMeterNo.setMeterNo(meterNumber);
            GetMeterByMeterNoResponse getMeterByMeterNoResponse =
                cbClient.getMeterByMeterNo(mspVendor, endpointUrl, getMeterByMeterNo);
            mspMeter = getMeterByMeterNoResponse.getGetMeterByMeterNoResult();
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByMeterNo (" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for Meter");
        }
        return mspMeter;
    }

    // GET ALL MSP SERVICE LOCATIONS
    @Override
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws MultispeakWebServiceClientException {

        boolean firstGet = true;
        String lastReceived = null;

        while (firstGet || lastReceived != null) {

            // kill before gathering more substations if callback is canceled
            if (callback.isCanceled()) {
                log.info("MultispeakGetAllServiceLocationsCallback in canceled state, aborting next call to getMoreServiceLocations");
                return;
            }

            log.info("Calling getMoreServiceLocations, lastReceived = " + lastReceived);
            lastReceived = getMoreServiceLocations(mspVendor, lastReceived, callback);
            firstGet = false;
        }

        callback.finish();
    }

    private String getMoreServiceLocations(MultispeakVendor mspVendor, String lastReceived,
            MultispeakGetAllServiceLocationsCallback callback) throws MultispeakWebServiceClientException {

        String lastSent = null;
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetAllServiceLocations getAllServiceLocations = objectFactory.createGetAllServiceLocations();
            getAllServiceLocations.setLastReceived(lastReceived);
            // get service locations
            GetAllServiceLocationsResponse getAllServiceLocationsResponse =
                cbClient.getAllServiceLocations(mspVendor, endpointUrl, getAllServiceLocations);
            List<ServiceLocation> serviceLocations = getAllServiceLocationsResponse.getGetAllServiceLocationsResult()
                                                                                   .getServiceLocation();
            int serviceLocationCount = 0;
            if (serviceLocations != null) {
                serviceLocationCount = serviceLocations.size();
            }

            // objectsRemaining
            int objectsRemaining = 0;
            String objectsRemainingStr = getAttributeValue("ObjectsRemaining");
            if (!StringUtils.isBlank(objectsRemainingStr)) {
                try {
                    objectsRemaining = Integer.valueOf(objectsRemainingStr);
                } catch (NumberFormatException e) {
                    log.error("Non-integer value in header for ObjectsRemaining: " + objectsRemainingStr, e);
                }
            }

            if (objectsRemaining != 0) {
                lastSent = getAttributeValue("LastSent");
                log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                    + " ServiceLocations using lastReceived = " + lastReceived + ". Response: ObjectsRemaining = "
                    + objectsRemaining + ", LastSent = " + lastSent);
            } else {
                log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                    + " ServiceLocations using LastSent = " + lastReceived + ". Response: ObjectsRemaining = "
                    + objectsRemaining);
            }

            // process service locations
            if (serviceLocationCount > 0) {
                callback.processServiceLocations(serviceLocations);
            }
        } catch (MultispeakWebServiceClientException e) {

            log.error("TargetService: " + endpointUrl + " - getAllServiceLocations (" + mspVendor.getCompanyName()
                + ") for LastReceived: " + lastReceived);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");

            throw e;
        }

        return lastSent;
    }

    /**
     * This method returns an Attribute value of response header.
     * 
     * @param name - attribute name.
     * @return String - Attribute value.
     **/
    public String getAttributeValue(String name) {
        String attributeValue = null;
        MessageContext message = MessageContextHolder.getMessageContext();
        WebServiceMessage responseMessage = message.getResponse();
        AbstractSoapMessage abstractSoapMessage = (AbstractSoapMessage) responseMessage;
        SaajSoapMessage saajSoapMessage = (SaajSoapMessage) abstractSoapMessage;
        SoapEnvelope soapEnvelop = saajSoapMessage.getEnvelope();
        SoapHeader soapHeader = soapEnvelop.getHeader();
        Iterator<SoapHeaderElement> iterator = soapHeader.examineAllHeaderElements();
        while (iterator.hasNext()) {
            SoapHeaderElement element = iterator.next();
            attributeValue = element.getAttributeValue(new QName(name));
        }
        return attributeValue;
    }

    @Override
    public List<Meter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor mspVendor) {
        return getMspMetersByServiceLocation(mspServiceLocation.getObjectID(), mspVendor);
    }

    @Override
    public List<Meter> getMspMetersByServiceLocation(String serviceLocation, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            long start = System.currentTimeMillis();
            log.debug("Begin call to getMeterByServLoc for ServLoc: " + serviceLocation);
            GetMeterByServLoc getMeterByServLoc = objectFactory.createGetMeterByServLoc();
            getMeterByServLoc.setServLoc(serviceLocation);
            GetMeterByServLocResponse getMeterByMeterNoResponse =
                cbClient.getMeterByServLoc(mspVendor, endpointUrl, getMeterByServLoc);
            ArrayOfMeter arrayOfMeter = getMeterByMeterNoResponse.getGetMeterByServLocResult();
            meters = arrayOfMeter.getMeter();
            log.debug("End call to getMeterByServLoc for ServLoc:" + serviceLocation + "  (took "
                + (System.currentTimeMillis() - start) + " millis)");

        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByServLoc (" + mspVendor.getCompanyName()
                + ") for ServLoc: " + serviceLocation);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(new Date()));

        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);

        String description = "ErrorObject: (ObjId:" + errorObject.getObjectID() + " Noun:" + errorObject.getNounType() + " Message:" + errorObject.getErrorString() + ")";
        logMSPActivity(method, description, userName);
       
        return errorObject;
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName, String exceptionMessage) {
        ErrorObject errorObject =
            getErrorObject(objectID, notFoundObjectType + ": " + objectID + " - " + exceptionMessage + ".", nounType,
                method, userName);
        return errorObject;
    }
    
    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName) {
        return getNotFoundErrorObject(objectID, notFoundObjectType, nounType, method, userName, "Was NOT found in Yukon");
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
    public void logMSPActivity(String method, String description, String userName) {
        getSystemLogHelper().log(PointTypes.SYS_PID_MULTISPEAK, method, description, userName,
            SystemLog.TYPE_MULTISPEAK);
        log.debug("MSP Activity (Method: " + method + "-" + description + " )");
    }

    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        List<String> substationNames = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetDomainMembers domainMembersRequest = objectFactory.createGetDomainMembers();
            domainMembersRequest.setDomainName("substationCode");
            GetDomainMembersResponse domainMembersResponse =
                cbClient.getDomainMembers(mspVendor, endpointUrl, domainMembersRequest);
            ArrayOfDomainMember arrayOfDomainMember = domainMembersResponse.getGetDomainMembersResult();
            List<DomainMember> domainMemberList = arrayOfDomainMember.getDomainMember();
            if (!domainMemberList.isEmpty()) {
                DomainMember[] domainMembers = new DomainMember[domainMemberList.size()];
                domainMemberList.toArray(domainMembers);
                if (domainMembers != null) {
                    for (DomainMember domainMember : domainMembers) {
                        substationNames.add(domainMember.getDescription());
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

    @Override
    public List<Meter> getMspMetersByEALocation(String eaLocation, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            GetMetersByEALocation getMetersByEALocation = objectFactory.createGetMetersByEALocation();
            getMetersByEALocation.setEaLoc(eaLocation);
            GetMetersByEALocationResponse getMetersByEALocationResponse =
                cbClient.getMetersByEALocation(mspVendor, endpointUrl, getMetersByEALocation);
            ArrayOfMeter arrayOfMeter = getMetersByEALocationResponse.getGetMetersByEALocationResult();
            if (null != arrayOfMeter) {
                meters = arrayOfMeter.getMeter();
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByEALocation (" + mspVendor.getCompanyName()
                + ") for EALocation: " + eaLocation);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByFacilityId(String facilityId, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            GetMetersByFacilityID facility = objectFactory.createGetMetersByFacilityID();
            facility.setFacilityID(facilityId);

            GetMetersByFacilityIDResponse response = cbClient.getMetersByFacilityID(mspVendor, endpointUrl, facility);
            if (response != null) {
                meters = response.getGetMetersByFacilityIDResult().getMeter();
            } else {
                log.error("Response not recieved for (" + mspVendor.getCompanyName() + ")");
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMspMetersByFacilityId (" + mspVendor.getCompanyName()
                + ") for facilityId: " + facilityId);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByAccountNumber(String accountNumber, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            GetMeterByAccountNumber getMeterByAccountNumber = objectFactory.createGetMeterByAccountNumber();
            getMeterByAccountNumber.setAccountNumber(accountNumber);

            GetMeterByAccountNumberResponse response =
                cbClient.getMeterByAccountNumber(mspVendor, endpointUrl, getMeterByAccountNumber);
            if (response != null) {
                meters = response.getGetMeterByAccountNumberResult().getMeter();
            } else {
                log.error("Response not recieved for (" + mspVendor.getCompanyName() + ")");
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMspMetersByAccountNumber (" + mspVendor.getCompanyName()
                + ") for accountNumber: " + accountNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByCustId(String custId, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetMeterByCustID getMeterByAccountNumber = objectFactory.createGetMeterByCustID();
            getMeterByAccountNumber.setCustID(custId);

            GetMeterByCustIDResponse response =
                cbClient.getMeterByCustID(mspVendor, endpointUrl, getMeterByAccountNumber);
            if (response != null) {
                meters = response.getGetMeterByCustIDResult().getMeter();
            } else {
                log.error("Response not recieved for (" + mspVendor.getCompanyName() + ")");
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMspMetersByCustId (" + mspVendor.getCompanyName()
                + ") for custId: " + custId);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws MultispeakWebServiceClientException {
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);

        ErrorObject[] objects = new ErrorObject[] {};
        List<ErrorObject> errorObjects = new ArrayList<>();
        
        PingURL pingURL = objectFactory.createPingURL();
        PingURLResponse response;
        
        if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            response = odClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            response = oaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            response = mdmClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            response = mrClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            response = eaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            response = lmClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            response = cdClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            response = cbClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            response = scadaClient.pingURL(mspVendor, endpointUrl, pingURL);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            response = cbClient.pingURL(mspVendor, endpointUrl, pingURL);
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
    public List<String> findMethods(String mspServer, MultispeakVendor mspVendor) {

        try {
            return getMethods(mspVendor, mspServer);
        } catch (MultispeakWebServiceClientException e) {
            log.error("Exception processing getMethods (" + mspVendor.getCompanyName() + ") for Server: " + mspServer);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String service)
            throws MultispeakWebServiceClientException {

        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);
        List<String> methods = new ArrayList<>();
        
        GetMethods getMethods = objectFactory.createGetMethods();
        GetMethodsResponse response;
        
        if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            response = odClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            response = oaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            response = mdmClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            response = mrClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            response = eaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            response = lmClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            response = cdClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            response = cbClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            response = scadaClient.getMethods(mspVendor, endpointUrl, getMethods);
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            response = cbClient.getMethods(mspVendor, endpointUrl, getMethods);
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
    public List<Meter> getMetersBySearchString(String searchString, MultispeakVendor mspVendor) {
        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            long start = System.currentTimeMillis();
            log.debug("Begin call to getMetersBySearchString for SearchString: %s" + searchString);
            GetMetersBySearchString getMetersBySearchString = objectFactory.createGetMetersBySearchString();
            getMetersBySearchString.setSearchString(searchString);
            GetMetersBySearchStringResponse getMetersBySearchStringResponse =
                cbClient.getMetersBySearchString(mspVendor, endpointUrl, getMetersBySearchString);
            log.debug("End call to getMetersBySearchString for SearchString:" + searchString + "  (took "
                + (System.currentTimeMillis() - start) + " millis)");
            if (getMetersBySearchStringResponse != null) {
                ArrayOfMeter arrayOfMeter = getMetersBySearchStringResponse.getGetMetersBySearchStringResult();
                meters = arrayOfMeter.getMeter();
            }
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersBySearchString (" + mspVendor.getCompanyName()
                + ") for SearchString: " + searchString);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }
}