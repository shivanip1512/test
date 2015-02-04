package com.cannontech.multispeak.deploy.service.impl;

import java.util.Vector;

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

@Service
public class LM_ServerImpl implements LM_Server {
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakLMService multispeakLMService;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MspValidationService mspValidationService;

    private LiteYukonUser init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }

    @Override
    public ErrorObject[] pingURL() throws MultispeakWebServiceException {
        init();
        return new ErrorObject[0];
    }

    @Override
    public String[] getMethods() throws MultispeakWebServiceException {
        init();
        String[] methods =
            new String[] { "pingURL", "getMethods", "SCADAAnalogChangedNotification",
                "getAllSubstationLoadControlStatuses", "initiateLoadManagementEvent", "initiateLoadManagementEvents" };
        return multispeakFuncs.getMethods(MultispeakDefines.LM_Server_STR, methods);
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        LiteYukonUser liteYukonUser = init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("SCADAAnalogChangedNotification", vendor.getCompanyName());

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
            ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
            if (errorObject == null) {
                errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, liteYukonUser);
            }
            if (errorObject != null) {
                errorObjects.add(errorObject);
            }
        }
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws MultispeakWebServiceException {
        init();
        return multispeakLMService.getActiveLoadControlStatus();
    }

    @Override
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent) throws MultispeakWebServiceException {

        LiteYukonUser liteYukonUser = init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateLoadManagementEvent", vendor.getCompanyName());

        ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(theLMEvent);
        if (errorObject == null) {
            MspLoadControl mspLoadControl = new MspLoadControl();
            ErrorObject[] errorObject2 = multispeakLMService.buildMspLoadControl(theLMEvent, mspLoadControl, vendor);
            if (errorObject2.length > 0) {
                // We may have more than one error possibly, just return the first error.
                return errorObject2[0];
            }
            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
        }
        return errorObject;
    }

    @Override
    public ErrorObject[] initiateLoadManagementEvents(LoadManagementEvent[] theLMEvents)
            throws MultispeakWebServiceException {
        LiteYukonUser liteYukonUser = init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("initiateLoadManagementEvents", vendor.getCompanyName());

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();

        for (LoadManagementEvent loadManagementEvent : theLMEvents) {
            ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(loadManagementEvent);
            if (errorObject == null) {
                MspLoadControl mspLoadControl = new MspLoadControl();
                // If errorObjects are returned, we still continue on and control what we can.
                ErrorObject[] errorObject2 =
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
        return mspObjectDao.toErrorObject(errorObjects);
    }

    @Override
    public String[] getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] LMDeviceAddNotification(LoadManagementDevice[] addedLMDs) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] LMDeviceChangedNotification(LoadManagementDevice[] changedLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] LMDeviceExchangeNotification(LMDeviceExchange[] LMDChangeout)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] LMDeviceRemoveNotification(LoadManagementDevice[] removedLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] LMDeviceRetireNotification(LoadManagementDevice[] retiredLMDs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses)
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
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public LoadManagementDevice[] getAllLoadManagementDevices(String lastReceived) throws MultispeakWebServiceException {
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
    public LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(String meterNo)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public LoadManagementDevice[] getLoadManagementDeviceByServLoc(String servLoc) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
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
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
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
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String[] getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}