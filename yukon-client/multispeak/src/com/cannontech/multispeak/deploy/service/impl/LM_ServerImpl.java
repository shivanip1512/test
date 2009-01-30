package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.db.MspLoadControl;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LMDeviceExchange;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.LoadManagementDevice;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.cannontech.multispeak.deploy.service.ScadaPoint;
import com.cannontech.multispeak.deploy.service.ScadaStatus;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus;
import com.cannontech.multispeak.service.MspValidationService;
import com.cannontech.multispeak.service.MultispeakLMService;

public class LM_ServerImpl implements LM_ServerSoap_PortType
{
    public MultispeakFuncs multispeakFuncs;
    public MultispeakLMService multispeakLMService;
    public MspObjectDao mspObjectDao;
    public MspValidationService mspValidationService;
    
    private LiteYukonUser init() throws RemoteException{
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }
    
    @Override
    public ErrorObject[] pingURL() throws java.rmi.RemoteException {
        init();
        return new ErrorObject[0];
    }
    
    @Override
    public String[] getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods"};
        return multispeakFuncs.getMethods(MultispeakDefines.LM_Server_STR, methods );
    }
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.LM_Server_STR, "getDomainNames", strings);
        return strings;
    }
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    @Override
    public ErrorObject[] LMDeviceAddNotification(
            LoadManagementDevice[] addedLMDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] LMDeviceChangedNotification(
            LoadManagementDevice[] changedLMDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] LMDeviceExchangeNotification(
            LMDeviceExchange[] LMDChangeout) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] LMDeviceRemoveNotification(
            LoadManagementDevice[] removedLMDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] LMDeviceRetireNotification(
            LoadManagementDevice[] retiredLMDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAAnalogChangedNotification(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        LiteYukonUser liteYukonUser = init();

        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
        	ErrorObject errorObject = mspValidationService.isValidScadaAnalog(scadaAnalog);
        	if( errorObject == null) {
        		errorObject = multispeakLMService.writeAnalogPointData(scadaAnalog, liteYukonUser);
        	} 
        	if (errorObject != null) {
        		errorObjects.add(errorObject);
        	}
		}
        return mspObjectDao.toErrorObject(errorObjects);
    }
    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog)
            throws RemoteException {
        init();
        
    }
    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(
            ScadaPoint[] scadaPoints) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAPointChangedNotificationForStatus(
            ScadaPoint[] scadaPoints) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] SCADAStatusChangedNotification(
            ScadaStatus[] scadaStatuses) throws RemoteException {
        init();
        return null;
    }
    @Override
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus)
            throws RemoteException {
        init();
        
    }
    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public LoadManagementDevice[] getAllLoadManagementDevices(
            String lastReceived) throws RemoteException {
        init();
        return null;
    }
    @Override
    public SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses()
            throws RemoteException {
        init();
        return multispeakLMService.getActiveLoadControlStatus();
    }
    @Override
    public float getAmountOfControllableLoad() throws RemoteException {
        init();
        return 0;
    }
    @Override
    public float getAmountOfControlledLoad() throws RemoteException {
        init();
        return 0;
    }
    @Override
    public LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(
            String meterNo) throws RemoteException {
        init();
        return null;
    }
    @Override
    public LoadManagementDevice[] getLoadManagementDeviceByServLoc(
            String servLoc) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject initiateLoadManagementEvent(LoadManagementEvent theLMEvent)
            throws RemoteException {

        LiteYukonUser liteYukonUser = init();
        ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(theLMEvent);
    	if (errorObject == null) {
            MspLoadControl mspLoadControl = multispeakLMService.buildMspLoadControl(theLMEvent);
            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
    	} 
        return errorObject;
    }
    
    @Override
    public ErrorObject[] initiateLoadManagementEvents(LoadManagementEvent[] theLMEvents)
            throws RemoteException {
        LiteYukonUser liteYukonUser = init();
        
        Vector<ErrorObject> errorObjects = new Vector<ErrorObject>();
        
    	//Need to figure out some transactional way of thinking about this.  Rollbacks on starting control?
        for (LoadManagementEvent loadManagementEvent : theLMEvents) {
        	ErrorObject errorObject = mspValidationService.isValidLoadManagementEvent(loadManagementEvent);
        	if (errorObject == null) {
	            MspLoadControl mspLoadControl = multispeakLMService.buildMspLoadControl(loadManagementEvent);
	            errorObject = multispeakLMService.control(mspLoadControl, liteYukonUser);
        	} 
        	if (errorObject != null) {
           		errorObjects.add(errorObject);
           	}
		}
        return mspObjectDao.toErrorObject(errorObjects);
    }
    @Override
    public ErrorObject initiatePowerFactorManagementEvent(
            PowerFactorManagementEvent thePFMEvent) throws RemoteException {
        init();
        return null;
    }
    @Override
    public boolean isLoadManagementActive(String servLoc)
            throws RemoteException {
        init();
        return false;
    }
    @Override
    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        init();
        return null;
    }
    
    @Autowired
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Autowired
    public void setMultispeakLMService(MultispeakLMService multispeakLMService) {
		this.multispeakLMService = multispeakLMService;
	}
    @Autowired
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
		this.mspObjectDao = mspObjectDao;
	}
    @Autowired
    public void setMspValidationService(
			MspValidationService mspValidationService) {
		this.mspValidationService = mspValidationService;
	}
}