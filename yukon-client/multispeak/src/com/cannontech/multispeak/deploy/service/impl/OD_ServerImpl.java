package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.OutageDetectDeviceStatus;
import com.cannontech.msp.beans.v3.OutageDetectDeviceType;
import com.cannontech.msp.beans.v3.OutageDetectionDevice;
import com.cannontech.msp.beans.v3.OutageEvent;
import com.cannontech.msp.beans.v3.PhaseCd;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.multispeak.service.OD_Server;

@Service
public class OD_ServerImpl implements OD_Server {

    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakMeterService multispeakMeterService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    
    private void init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
    }
    
    @Override
    public ErrorObject pingURL() throws MultispeakWebServiceException {
        init();
        return new ErrorObject();
    }
    
    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = new String[] { "pingURL", "getMethods", "initiateOutageDetectionEventRequest" };
        return multispeakFuncs.getMethods(MultispeakDefines.OD_Server_STR, Arrays.asList(methods));
    }
    
    @Override
    public List<String> getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<DomainMember> getDomainMembers(String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<OutageDetectionDevice> getAllOutageDetectionDevices(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<OutageDetectionDevice> getOutageDetectionDevicesByMeterNo(String meterNo) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<OutageDetectionDevice> getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<OutageDetectionDevice> getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<OutageDetectionDevice> getOutagedODDevices() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<ErrorObject> initiateOutageDetectionEventRequest(List<String> meterNos, Calendar requestDate,
            String responseURL, String transactionID, Float expirationTime) throws MultispeakWebServiceException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateOutageDetectionEventRequest", vendor.getCompanyName());
        String actualResponseUrl = multispeakFuncs.getResponseUrl(vendor, responseURL, MultispeakDefines.OA_Server_STR);

        List<ErrorObject> errorObjects = multispeakMeterService.odEvent(vendor, meterNos, transactionID, actualResponseUrl);
        multispeakFuncs.logErrorObjects(MultispeakDefines.OD_Server_STR, "initiateOutageDetectionEventRequest",
            errorObjects);
        return errorObjects;
    }
    
    @Override
    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<ObjectRef> displayODMonitoringRequests() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<ErrorObject> cancelODMonitoringRequestByObject(List<ObjectRef> objectRef, Calendar requestDate)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<ErrorObject> initiateODEventRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            Calendar requestDate, String responseURL, String transactionID, float expirationTime)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateODEventRequestByServiceLocation(List<String> servLoc, Calendar requestDate,
            String responseURL, String transactionID, float expirationTime) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateODMonitoringRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            int periodicity, Calendar requestDate, String responseURL, String transactionID, float expirationTime)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> customerChangedNotification(List<Customer> changedCustomers) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<String> getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainMembersChangedNotification(List<DomainMember> changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainNamesChangedNotification(List<DomainNameChange> changedDomainNames)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> outageEventChangedNotification(List<OutageEvent> oEvents) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}