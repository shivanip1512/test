package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.xml.soap.Name;

import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.message.dispatch.message.SystemLogHelper;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakGetAllServiceLocationsCallback;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.EA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.MDM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {
    private static final Logger log = YukonLogManager.getLogger(MspObjectDaoImpl.class);
    @Autowired private MultispeakFuncs multispeakFuncs;

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

        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                mspCustomer = port.getCustomerByMeterNo(meterNumber);
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for MeterNo: "
                    + meterNumber);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getCustomerByMeterNo(" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: " + e.getMessage());
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
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                LogHelper.debug(log, "Calling %s CB_Server.getServiceLocationByMeterNo for meterNumber: %s",
                    mspVendor.getCompanyName(), meterNumber);
                mspServiceLocation = port.getServiceLocationByMeterNo(meterNumber);
            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getServiceLocationByMeterNo (" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: " + e.getMessage());
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
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                mspMeter = port.getMeterByMeterNo(meterNumber);
            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for MeterNo: " + meterNumber);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByMeterNo (" + mspVendor.getCompanyName()
                + ") for MeterNo: " + meterNumber);
            log.error("RemoteExceptionDetail: " + e.getMessage());
            log.info("A default(empty) is being used for Meter");
        }
        return mspMeter;
    }

    // GET ALL MSP SERVICE LOCATIONS
    @Override
    public void getAllMspServiceLocations(MultispeakVendor mspVendor, MultispeakGetAllServiceLocationsCallback callback)
            throws RemoteException {

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
            MultispeakGetAllServiceLocationsCallback callback) throws RemoteException {

        String lastSent = null;
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {

            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {

                // get service locations
                ServiceLocation[] serviceLocations = port.getAllServiceLocations(lastReceived);

                int serviceLocationCount = 0;
                if (serviceLocations != null) {
                    serviceLocationCount = serviceLocations.length;
                }

                // objectsRemaining
                int objectsRemaining = 0;
                String objectsRemainingStr = getAttributeValue(port, "objectsRemaining");
                if (!StringUtils.isBlank(objectsRemainingStr)) {
                    try {
                        objectsRemaining = Integer.valueOf(objectsRemainingStr);
                    } catch (NumberFormatException e) {
                        log.error("Non-integer value in header for objectsRemaining: " + objectsRemainingStr, e);
                    }
                }

                if (objectsRemaining != 0) {
                    lastSent = getAttributeValue(port, "lastSent");
                    log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                        + " ServiceLocations using lastReceived = " + lastReceived + ". Response: objectsRemaining = "
                        + objectsRemaining + ", lastSent = " + lastSent);
                } else {
                    log.info("getMoreServiceLocations responded, received " + serviceLocationCount
                        + " ServiceLocations using lastReceived = " + lastReceived + ". Response: objectsRemaining = "
                        + objectsRemaining);
                }

                // process service locations
                if (serviceLocationCount > 0) {
                    List<ServiceLocation> mspServiceLocations = Arrays.asList(serviceLocations);
                    callback.processServiceLocations(mspServiceLocations);
                }

            } else {
                log.error("Port not found for CB_MR (" + mspVendor.getCompanyName() + ") for LastReceived: "
                    + lastReceived);
            }

        } catch (RemoteException e) {

            log.error("TargetService: " + endpointUrl + " - getAllServiceLocations (" + mspVendor.getCompanyName()
                + ") for LastReceived: " + lastReceived);
            log.error("RemoteExceptionDetail: " + e.getMessage());
            log.info("A default(empty) is being used for ServiceLocation");

            throw e;
        }

        return lastSent;
    }

    @SuppressWarnings("unchecked")
    private String getAttributeValue(Stub port, String name) {

        String value = null;
        SOAPHeaderElement[] responseHeaders = port.getResponseHeaders();
        for (SOAPHeaderElement headerElement : responseHeaders) {
            Iterator<Name> attributeNamesItr = headerElement.getAllAttributes();
            while (attributeNamesItr.hasNext()) {
                Name attributeName = attributeNamesItr.next();
                if (attributeName.getLocalName().equalsIgnoreCase(name)) {
                    value = headerElement.getAttributeValue(attributeName);
                    if (!StringUtils.isBlank(value)) {
                        break;
                    }
                }
            }
        }
        return value;
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
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                long start = System.currentTimeMillis();
                LogHelper.debug(log, "Begin call to getMeterByServLoc for ServLoc: %s", serviceLocation);
                Meter[] mspMeters = port.getMeterByServLoc(serviceLocation);
                LogHelper.debug(log, "End call to getMeterByServLoc for ServLoc:" + serviceLocation + "  (took "
                    + (System.currentTimeMillis() - start) + " millis)");
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for ServLoc: "
                    + serviceLocation);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByServLoc (" + mspVendor.getCompanyName()
                + ") for ServLoc: " + serviceLocation);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }
    
    @Override
    public List<Meter> getMetersBySearchString(String searchString, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                long start = System.currentTimeMillis();
                log.debug( "Begin call to getMetersBySearchString for SearchString: %s"+searchString);
                Meter[] mspMeters = port.getMetersBySearchString(searchString);
                log.debug("End call to getMetersBySearchString for SearchString:" + searchString + "  (took "
                    + (System.currentTimeMillis() - start) + " millis)");
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for SearchString: "
                    + searchString);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersBySearchString (" + mspVendor.getCompanyName()
                + ") for SearchString: " + searchString);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }


    @Override
    public ErrorObject getErrorObject(String objectID, String errorMessage, String nounType, String method,
            String userName) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setEventTime(new GregorianCalendar());
        errorObject.setObjectID(objectID);
        errorObject.setErrorString(errorMessage);
        errorObject.setNounType(nounType);

        String description =
            "ErrorObject: (ObjId:" + errorObject.getObjectID() + " Noun:" + errorObject.getNounType() + " Message:"
                + errorObject.getErrorString() + ")";
        logMSPActivity(method, description, userName);
        return errorObject;
    }

    @Override
    public ErrorObject getNotFoundErrorObject(String objectID, String notFoundObjectType, String nounType,
            String method, String userName) {
        ErrorObject errorObject =
            getErrorObject(objectID, notFoundObjectType + ": " + objectID + " - Was NOT found in Yukon.", nounType,
                method, userName);
        return errorObject;
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
        LogHelper.debug(log, "MSP Activity (Method: %s - %s)", method, description);
    }

    @Override
    public List<String> getMspSubstationName(MultispeakVendor mspVendor) {

        List<String> substationNames = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                DomainMember[] domainMembers = port.getDomainMembers("substationCode");
                if (domainMembers != null) {
                    for (DomainMember domainMember : domainMembers) {
                        substationNames.add(domainMember.getDescription());
                    }
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName()
                    + ") for DomainMember 'substationCode'");
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getDomainMembers(" + mspVendor.getCompanyName()
                + ") for DomainMember 'substationCode'");
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return substationNames;
    }

    @Override
    public List<Meter> getMspMetersByEALocation(String eaLocation, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                Meter[] mspMeters = port.getMetersByEALocation(eaLocation);
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for EALocation: "
                    + eaLocation);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByEALocation (" + mspVendor.getCompanyName()
                + ") for EALocation: " + eaLocation);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByFacilityId(String facilityId, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                Meter[] mspMeters = port.getMetersByFacilityID(facilityId);
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for FacilityId: "
                    + facilityId);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMetersByFacilityID (" + mspVendor.getCompanyName()
                + ") for FacilityId: " + facilityId);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByAccountNumber(String accountNumber, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);

        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                Meter[] mspMeters = port.getMeterByAccountNumber(accountNumber);
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for Account Number: "
                    + accountNumber);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByAccountNumber (" + mspVendor.getCompanyName()
                + ") for Account Number: " + accountNumber);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public List<Meter> getMspMetersByCustId(String custId, MultispeakVendor mspVendor) {

        List<Meter> meters = new ArrayList<>();
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, MultispeakDefines.CB_Server_STR);
        try {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            if (port != null) {
                Meter[] mspMeters = port.getMeterByCustID(custId);
                if (mspMeters != null) {
                    meters = Arrays.asList(mspMeters);
                }
            } else {
                log.error("Port not found for CB_Server (" + mspVendor.getCompanyName() + ") for CustId: " + custId);
            }
        } catch (RemoteException e) {
            log.error("TargetService: " + endpointUrl + " - getMeterByCustID (" + mspVendor.getCompanyName()
                + ") for CustId: " + custId);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return meters;
    }

    @Override
    public ErrorObject[] pingURL(MultispeakVendor mspVendor, String service) throws RemoteException {
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);

        ErrorObject[] objects = new ErrorObject[] {};

        if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            OD_ServerSoap_BindingStub port = MultispeakPortFactory.getOD_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            MDM_ServerSoap_PortType port = MultispeakPortFactory.getMDM_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            MR_ServerSoap_BindingStub port = MultispeakPortFactory.getMR_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            LM_ServerSoap_BindingStub port = MultispeakPortFactory.getLM_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            CD_ServerSoap_BindingStub port = MultispeakPortFactory.getCD_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            SCADA_ServerSoap_BindingStub port = MultispeakPortFactory.getSCADA_ServerPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor, endpointUrl);
            objects = port.pingURL();
        } else {
            ErrorObject obj = new ErrorObject("-100", "No server for " + service, null, null);
            return new ErrorObject[] { obj };
        }
        return objects;
    }

    @Override
    public List<String> findMethods(String mspServer, MultispeakVendor mspVendor) {

        try {
            return getMethods(mspVendor, mspServer);
        } catch (RemoteException e) {
            log.error("Exception processing getMethods (" + mspVendor.getCompanyName() + ") for Server: " + mspServer);
            log.error("RemoteExceptionDetail: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getMethods(MultispeakVendor mspVendor, String service) throws RemoteException {
        String endpointUrl = multispeakFuncs.getEndpointUrl(mspVendor, service);

        String[] objects = new String[] {};
        if (service.equalsIgnoreCase(MultispeakDefines.OD_Server_STR)) {
            OD_ServerSoap_BindingStub port = MultispeakPortFactory.getOD_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.OA_Server_STR)) {
            OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.MDM_Server_STR)) {
            MDM_ServerSoap_PortType port = MultispeakPortFactory.getMDM_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.MR_Server_STR)) {
            MR_ServerSoap_BindingStub port = MultispeakPortFactory.getMR_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.EA_Server_STR)) {
            EA_ServerSoap_BindingStub port = MultispeakPortFactory.getEA_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.LM_Server_STR)) {
            LM_ServerSoap_BindingStub port = MultispeakPortFactory.getLM_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CD_Server_STR)) {
            CD_ServerSoap_BindingStub port = MultispeakPortFactory.getCD_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_Server_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.SCADA_Server_STR)) {
            SCADA_ServerSoap_BindingStub port = MultispeakPortFactory.getSCADA_ServerPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        } else if (service.equalsIgnoreCase(MultispeakDefines.CB_CD_STR)) {
            CB_ServerSoap_BindingStub port = MultispeakPortFactory.getCB_CDPort(mspVendor, endpointUrl);
            objects = port.getMethods();
        }

        if (objects == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(objects);
    }
}