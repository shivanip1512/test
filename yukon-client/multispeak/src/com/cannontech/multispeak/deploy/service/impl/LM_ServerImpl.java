package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LMDeviceExchange;
import com.cannontech.msp.beans.v3.LoadManagementDevice;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.PowerFactorManagementEvent;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.ScadaPoint;
import com.cannontech.msp.beans.v3.ScadaStatus;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.LM_Server;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.google.common.collect.Lists;

@Service
public class LM_ServerImpl implements LM_Server
{
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspValidationService mspValidationService;
    
    private LiteYukonUser init() throws MultispeakWebServiceException{
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }
    
    @Override
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }
    
    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods = new String[] {
                "pingURL",
                "getMethods",
                "SCADAAnalogChangedNotification",
                "getAllSubstationLoadControlStatuses",
                "initiateLoadManagementEvent",
                "initiateLoadManagementEvents"
                };
        return multispeakFuncs.getMethods(MultispeakDefines.LM_Server_STR, Arrays.asList(methods));
    }
    
    @Override
    public List<String> getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    
    @Override
    public List<DomainMember> getDomainMembers(java.lang.String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> LMDeviceAddNotification(List<LoadManagementDevice> addedLMDs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> LMDeviceChangedNotification(List<LoadManagementDevice> changedLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> LMDeviceExchangeNotification(List<LMDeviceExchange> LMDChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> LMDeviceRemoveNotification(List<LoadManagementDevice> removedLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> LMDeviceRetireNotification(List<LoadManagementDevice> retiredLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAAnalogChangedNotification(
            List<ScadaAnalog> scadaAnalogs) throws MultispeakWebServiceException {
        LiteYukonUser liteYukonUser = init();
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("SCADAAnalogChangedNotification", vendor.getCompanyName());
        
        List<ErrorObject> errorObjects = Lists.newArrayList();
        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
            ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
            if (errorObject == null) {
                errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }
    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAAnalogChangedNotificationForPower(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAAnalogChangedNotificationForVoltage(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAPointChangedNotification(List<ScadaPoint> scadaPoints) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAPointChangedNotificationForAnalog(List<ScadaPoint> scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<SubstationLoadControlStatus> getAllSubstationLoadControlStatuses()
            throws MultispeakWebServiceException {
        init();
        return multispeakLMService.getActiveLoadControlStatus();
    } 
    @Override
    public List<ErrorObject> SCADAPointChangedNotificationForStatus(List<ScadaPoint> scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> SCADAStatusChangedNotification(List<ScadaStatus> scadaStatuses)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<ErrorObject> customerChangedNotification(List<Customer> changedCustomers) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<LoadManagementDevice> getAllLoadManagementDevices(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public float getAmountOfControllableLoad() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public float getAmountOfControlledLoad() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<LoadManagementDevice> getLoadManagementDeviceByMeterNumber(String meterNo)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public List<LoadManagementDevice> getLoadManagementDeviceByServLoc(String servLoc) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
 
    @Override
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent)
            throws MultispeakWebServiceException {

        LiteYukonUser liteYukonUser = init();
        
    	MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateLoadManagementEvent", vendor.getCompanyName());

        ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(theLMEvent);
    	if (errorObject == null) {
    		MspLoadControl mspLoadControl = new MspLoadControl();
            List<ErrorObject> errorObject2 = multispeakLMService.buildMspLoadControl(theLMEvent, mspLoadControl, vendor);
            if (errorObject2.size() > 0) {
            	//We may have more than one error possibly, just return the first error.
            	return errorObject2.get(0);
            }
            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
    	} 
        return errorObject;
    }
    
    @Override
    public List<ErrorObject> initiateLoadManagementEvents(List<LoadManagementEvent> theLMEvents)
            throws MultispeakWebServiceException {
        LiteYukonUser liteYukonUser = init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateLoadManagementEvents", vendor.getCompanyName());

        List<ErrorObject> errorObjects = Lists.newArrayList();

        for (LoadManagementEvent loadManagementEvent : theLMEvents) {
            ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(loadManagementEvent);
            if (errorObject == null) {
                MspLoadControl mspLoadControl = new MspLoadControl();
                // If errorObjects are returned, we still continue on and control what we can.
                List<ErrorObject> errorObject2 =
                        multispeakLMService.buildMspLoadControl(loadManagementEvent, mspLoadControl, vendor);
                for (ErrorObject err : errorObject2) {
                    errorObjects.add(err);
                }
                errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return errorObjects;
    }
    @Override
    public ErrorObject initiatePowerFactorManagementEvent(PowerFactorManagementEvent thePFMEvent)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
    @Override
    public boolean isLoadManagementActive(String servLoc) throws MultispeakWebServiceException {
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
}