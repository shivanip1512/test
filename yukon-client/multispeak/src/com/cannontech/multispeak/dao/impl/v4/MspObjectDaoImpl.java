package com.cannontech.multispeak.dao.impl.v4;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
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
import com.cannontech.msp.beans.v4.ArrayOfDomainMember;
import com.cannontech.msp.beans.v4.ArrayOfServiceLocation1;
import com.cannontech.msp.beans.v4.Customer;
import com.cannontech.msp.beans.v4.DomainMember;
import com.cannontech.msp.beans.v4.ElectricService;
import com.cannontech.msp.beans.v4.ErrorObject;
import com.cannontech.msp.beans.v4.GasService;
import com.cannontech.msp.beans.v4.GetAllServiceLocations;
import com.cannontech.msp.beans.v4.GetAllServiceLocationsResponse;
import com.cannontech.msp.beans.v4.GetCustomerByMeterID;
import com.cannontech.msp.beans.v4.GetCustomerByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetDomainMembers;
import com.cannontech.msp.beans.v4.GetDomainMembersResponse;
import com.cannontech.msp.beans.v4.GetMeterByMeterID;
import com.cannontech.msp.beans.v4.GetMeterByMeterIDResponse;
import com.cannontech.msp.beans.v4.GetDomainMembers;
import com.cannontech.msp.beans.v4.GetDomainMembersResponse;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationID;
import com.cannontech.msp.beans.v4.GetMeterByServiceLocationIDResponse;
import com.cannontech.msp.beans.v4.GetMethods;
import com.cannontech.msp.beans.v4.GetMethodsResponse;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterID;
import com.cannontech.msp.beans.v4.GetServiceLocationByMeterIDResponse;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.Meters;
import com.cannontech.msp.beans.v4.MspMeter;
import com.cannontech.msp.beans.v4.MspObject;
import com.cannontech.msp.beans.v4.ObjectFactory;
import com.cannontech.msp.beans.v4.PingURL;
import com.cannontech.msp.beans.v4.PingURLResponse;
import com.cannontech.msp.beans.v4.ServiceLocation;
import com.cannontech.msp.beans.v4.WaterService;
import com.cannontech.multispeak.client.MessageContextHolder;
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
import com.cannontech.multispeak.dao.v4.MultispeakGetAllServiceLocationsCallback;
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
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setEventTime(MultispeakFuncs.toXMLGregorianCalendar(new Date()));

        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);

        String description = "ErrorObject: (ObjId:" + errorObject.getObjectID() + " Noun:" + errorObject.getNounType()
                + " Message:" + errorObject.getErrorString() + ")";
        logMSPActivity(method, description, userName);

        return errorObject;
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
        ErrorObject errorObject = getErrorObject(objectID, 
                                                 notFoundObjectType + ": " + objectID + " - " + exceptionMessage + ".",
                                                 nounType,
                                                 method, 
                                                 userName);
        return errorObject;
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName) {
        return getNotFoundErrorObject(objectID, 
                                      notFoundObjectType, 
                                      nounType, 
                                      method, 
                                      userName,
                                      "Was NOT found in Yukon");
    }

    
    @Override
    public ServiceLocation getMspServiceLocation(MspObject mspObject, MultispeakVendor mspVendor) {
        ServiceLocation mspServiceLocation = new ServiceLocation();

        String meterNo = null;
        if (mspObject instanceof ElectricService) {
            meterNo = ((ElectricService) mspObject).getElectricMeterID();
        } else if (mspObject instanceof WaterService) {
            meterNo = ((WaterService) mspObject).getWaterMeterID();
        } else if (mspObject instanceof GasService) {
            meterNo = ((GasService) mspObject).getGasMeterID();
        }

        mspServiceLocation = getServiceLocationByMeterNo(meterNo, mspVendor);
            
        return mspServiceLocation;
    }
    
    @Override
    public ServiceLocation getServiceLocationByMeterNo(String meterNo,MultispeakVendor mspVendor){
        ServiceLocation mspSerLoction = new ServiceLocation();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        
        try {
            GetServiceLocationByMeterID getServiceLocationByMspMeterId = objectFactory.createGetServiceLocationByMeterID();

            MeterID mspMeterId = new MeterID();
            mspMeterId.setMeterNo(meterNo);
            getServiceLocationByMspMeterId.setMeterID(mspMeterId);
            log.debug("Calling " + mspVendor.getCompanyName()
                    + " CB_Server.GetServiceLocationByMeterID for meterID: " + meterNo);
            GetServiceLocationByMeterIDResponse getServiceLocationByMeterNoResponse = cbClient
                    .getServiceLocationByMeterID(mspVendor, endpointUrl, getServiceLocationByMspMeterId);

            ArrayOfServiceLocation1 locationByMeterIDResult = getServiceLocationByMeterNoResponse
                    .getGetServiceLocationByMeterIDResult();
            List<ServiceLocation> serviceLocation = locationByMeterIDResult.getServiceLocation();
            mspSerLoction = serviceLocation != null ? serviceLocation.get(0) : null;
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - GetServiceLocationByMeterID (" + mspVendor.getCompanyName()
                    + ") for MeterID: " + meterNo);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");
        }
        return mspSerLoction;
    }
    
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        List<String> substationNames = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetDomainMembers domainMembers = objectFactory.createGetDomainMembers();
            domainMembers.setDomainName("substationCode");
            GetDomainMembersResponse domainMembersResponse = cbClient.getDomainMembers(mspVendor, 
                                                                                       endpointUrl,
                                                                                       domainMembers);
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
            GetAllServiceLocationsResponse getAllServiceLocationsResponse = cbClient.getAllServiceLocations(mspVendor,
                                                                                                            endpointUrl,
                                                                                                            getAllServiceLocations);

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
    
    @Override
    public Customer getMspCustomer(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    
    @Override
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        Customer mspCustomer = new Customer();
        GetCustomerByMeterID getCustomerByMeterId = objectFactory.createGetCustomerByMeterID();
        MeterID meterId = objectFactory.createMeterID();
        meterId.setMeterNo(meterNumber);
        getCustomerByMeterId.setMeterID(meterId);
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            GetCustomerByMeterIDResponse getCustomerByMeterIdResponse =
                cbClient.getCustomerByMeterId(mspVendor, endpointUrl, getCustomerByMeterId);
            mspCustomer = getCustomerByMeterIdResponse.getGetCustomerByMeterIDResult();
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getCustomerByMeterID(" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for Customer");
        } catch (Exception e) 
            {e.printStackTrace();}
        return mspCustomer;
    }

    
    @Override
    public ArrayOfServiceLocation1 getMspServiceLocation(SimpleMeter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }

    @Override
    public ArrayOfServiceLocation1 getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {
        ArrayOfServiceLocation1 mspServiceLocation = objectFactory.createArrayOfServiceLocation1();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        log.debug("Calling " + mspVendor.getCompanyName()
                + " CB_Server.getServiceLocationByMeterID for meterNumber: " + meterNumber);

        try {
            GetServiceLocationByMeterID getServiceLocationByMeterId = objectFactory.createGetServiceLocationByMeterID();
            MeterID meterId = objectFactory.createMeterID();
            meterId.setMeterNo(meterNumber);
            getServiceLocationByMeterId.setMeterID(meterId);

            GetServiceLocationByMeterIDResponse getServiceLocationByMeterNoResponse = cbClient
                    .getServiceLocationByMeterId(mspVendor, endpointUrl, getServiceLocationByMeterId);
            mspServiceLocation = getServiceLocationByMeterNoResponse.getGetServiceLocationByMeterIDResult();
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getServiceLocationByMeterID (" + mspVendor.getCompanyName()
                    + ") for MeterNo: " + meterNumber);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");
        }
        return mspServiceLocation;
    }
     
    @Override
    public Meters getMspMeter(SimpleMeter meter, MultispeakVendor mspVendor) {
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            return getMspMeter(meter.getMeterNumber(), mspVendor);
        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByMeterID (" + mspVendor.getCompanyName()
                    + ") for MeterNo: " + meter.getMeterNumber());
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
            log.info("A default(empty) is being used for Meter");
        }
        return null;
    }
    
    @Override
    public Meters getMspMeter(String meterNumber, MultispeakVendor mspVendor) throws MultispeakWebServiceClientException {
        Meters mspMeter = new Meters();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        MeterID meterId = new MeterID();
        meterId.setMeterNo(meterNumber);
        GetMeterByMeterID getMeterByMeterId = objectFactory.createGetMeterByMeterID();
        getMeterByMeterId.setMeterID(meterId);
        GetMeterByMeterIDResponse getMeterByMeterIdResponse = cbClient.getMeterByMeterID(mspVendor, endpointUrl,
                getMeterByMeterId);
        mspMeter = getMeterByMeterIdResponse.getGetMeterByMeterIDResult();
        return mspMeter;
    }


    @Override
    public List<MspMeter> getMspMetersByServiceLocation(ServiceLocation mspServiceLocation, MultispeakVendor vendor) {
        return getMspMetersByServiceLocation(mspServiceLocation.getObjectID(), vendor);
    }
    
    private List<MspMeter> getMspMetersByServiceLocation(String serviceLocation, MultispeakVendor mspVendor) {

        List<MspMeter> listOfMeters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            long start = System.currentTimeMillis();
            log.debug("Begin call to getMspMetersByServiceLocation for ServLoc: " + serviceLocation);

            GetMeterByServiceLocationID getMeterByServLocID = objectFactory.createGetMeterByServiceLocationID();
            getMeterByServLocID.setServiceLocationID(serviceLocation);
            GetMeterByServiceLocationIDResponse getMeterByMeterNoResponse = cbClient.getMeterByServiceLocationID(mspVendor,
                    endpointUrl, getMeterByServLocID);
            Meters meters = getMeterByMeterNoResponse.getGetMeterByServiceLocationIDResult();

            if (meters != null) {
                listOfMeters = multispeakFuncs.getMspMeters(meters);
            }

            log.debug("End call to getMspMetersByServiceLocation for ServLoc:" + serviceLocation + " (took "
                    + (System.currentTimeMillis() - start) + " millis)");

        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByServiceLocationID (" + mspVendor.getCompanyName()
                    + ") for ServLoc: " + serviceLocation);
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
        return listOfMeters;
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
}
