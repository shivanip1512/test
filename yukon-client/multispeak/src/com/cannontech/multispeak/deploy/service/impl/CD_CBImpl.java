package com.cannontech.multispeak.deploy.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.deploy.service.CDDevice;
import com.cannontech.multispeak.deploy.service.CDDeviceExchange;
import com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.service.MspValidationService;

public class CD_CBImpl implements CD_CBSoap_PortType
{
    public Multispeak multispeak;
    public MultispeakFuncs multispeakFuncs;
    public MspMeterDao mspMeterDao;
    public MspValidationService mspValidationService;

    @Required
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }
    @Required
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Required
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    @Required
    public void setMspValidationService(
            MspValidationService mspValidationService) {
        this.mspValidationService = mspValidationService;
    }
    private void init() throws RemoteException{
        multispeakFuncs.init();
    }
    
    @Override
    public ErrorObject[] pingURL() throws java.rmi.RemoteException {
        init();
        return new ErrorObject[0];
    }
    
    @Override
    public String[] getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods",
        								 "getCDSupportedMeters",
                                         "getCDMeterState",
                                         "initiateConnectDisconnect"};
        return multispeakFuncs.getMethods(MultispeakDefines.CD_CB_STR, methods );
    }
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.CD_CB_STR, "getDomainNames", strings);
        return strings;
    }
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    
    @Override
    public Meter[] getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = mspMeterDao.getCDSupportedMeters(lastReceived, vendor.getMaxReturnRecords());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] meters = new Meter[meterList.size()];
        meterList.toArray(meters);
        CTILogger.info("Returning " + meters.length + " CD Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meters.length <= vendor.getMaxReturnRecords() ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        return meters;
    }
    
    @Override
    public Meter[] getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        com.cannontech.amr.meter.model.Meter meter = mspValidationService.isYukonMeterNumber(meterNo);
        LoadActionCode loadActionCode = multispeak.CDMeterState(vendor, meter, null);
        
        return loadActionCode;
    }

    @Override
    public ErrorObject[] initiateConnectDisconnect(
            ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        errorObjects = multispeak.CDEvent(vendor, cdEvents, transactionID);
        
        multispeakFuncs.logErrorObjects(MultispeakDefines.CD_CB_STR, "initiateConnectDisconnect", errorObjects);
        return errorObjects;
    }
    
    @Override
    public ErrorObject[] serviceLocationChangedNotification(
            com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws java.rmi.RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceExchangeNotification(CDDeviceExchange[] CDDChangeout) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs) throws RemoteException {
        init();
        return null;
    }
}
