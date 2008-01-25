/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dynamic.exception.DynamicDataAccessException;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.EventCode;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.HistoryLog;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterExchange;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.yukon.BasicServerConnection;

public class MR_CBImpl implements MR_CBSoap_PortType{

    public Multispeak multispeak;
    public MspMeterDao mspMeterDao;
    public MultispeakFuncs multispeakFuncs;
    public MspRawPointHistoryDao mspRawPointHistoryDao;
    private BasicServerConnection porterConnection;
    
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    public void setMspRawPointHistoryDao(MspRawPointHistoryDao mspRawPointHistoryDao) {
        this.mspRawPointHistoryDao = mspRawPointHistoryDao;
    }

    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    private void init() {
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
                                         "initiateMeterReadByMeterNumber",
                                         "isAMRMeter",
                                         "getAMRSupportedMeters",
                                         "getLatestReadingByMeterNo",
//                                         "getReadingsByDate",
                                         "getReadingsByMeterNo",
//                                         "getReadingsByBillingCycle", //NO LONGER SUPPORTED WITH NEW GROUPING SCHEMA
                                         "meterRemoveNotification",
                                         "meterAddNotification",
                                         "initiateUsageMonitoring",
                                         "cancelUsageMonitoring",
                                         "initiateDisconnectedStatus",
                                         "cancelDisconnectedStatus",
                                         "serviceLocationChangedNotification"};          
        return multispeakFuncs.getMethods(MultispeakDefines.MR_CB_STR , methods);
    }
    
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.MR_CB_STR, "getDomainNames", strings);
        return strings;
    }
    
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    
    @Override
    public Meter[] getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getUniqueKey(), vendor.getMaxReturnRecords());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] meters = new Meter[meterList.size()];
        meterList.toArray(meters);
        CTILogger.info("Returning " + meters.length + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meters.length < vendor.getMaxReturnRecords() ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        return meters;
    }
    
    @Override
    public Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();

        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        if( meterNo != null && meterNo.length() > 0) {
            try {
                com.cannontech.amr.meter.model.Meter meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNo);
                if( meter != null) {
                    CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter(), returning true." );
                    return true;
                }
            }catch (NotFoundException e){
                CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter() NOT found, returning false." );
                return false;
            }
        }
        return false;
    }
    
    @Override
    public MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        throw new RemoteException("Method getReadingsByDate(startDate, endDate, lastReceived) is NOT supported.");
    }
    
    @Override
    public MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
    //      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo)) {
            String message = "Meter Number (" + meterNo + "): NOT Found.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, 
                                                                          meterNo, 
                                                                          startDate.getTime(), 
                                                                          endDate.getTime(), 
                                                                          null,
                                                                          vendor.getMaxReturnRecords());
        
        return meterReads;
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo)) {
            String message = "Meter Number (" + meterNo + "): NOT Found.";
            CTILogger.error(message);            
            throw new RemoteException(message);
        }
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        com.cannontech.amr.meter.model.Meter meter;
        
        try {
            meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNo);
        }catch (NotFoundException e) {
            String message = "Meter Number (" + meterNo + "): NOT Found.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") ) {
        	return multispeak.getLatestReadingInterrogate(vendor, meter, null);
        } else	{ //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
    	        ReadableDevice device = MeterReadFactory.createMeterReadObject(meter);
    	        device.populateWithPointData(meter.getDeviceId());
    	        return device.getMeterRead();
            } catch (DynamicDataAccessException e) {
                String message = "Connection to dispatch is invalid";
                CTILogger.error(message);
                throw new RemoteException(message);
            }
        }
    }
    
    @Override
    public FormattedBlock[] getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException {
        /* TODO
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        return meterReads;
        */
        throw new RemoteException("Method getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) is NOT supported.");
    }
    
    @Override
    public HistoryLog[] getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] initiatePlannedOutage(String[] meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] cancelPlannedOutage(String[] meterNos) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] initiateUsageMonitoring(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.initiateUsageMonitoringStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] cancelUsageMonitoring(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.cancelUsageMonitoringStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.initiateDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.cancelDisconnectedStatus(vendor, meterNos);
        return errorObject;
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    @Override
    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos,
            String responseURL, String transactionID) throws RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            String message = "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }

        errorObjects = multispeak.MeterReadEvent(vendor, meterNos, transactionID);

        multispeakFuncs.logErrorObjects(MultispeakDefines.MR_CB_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return errorObjects;
    }
    
    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.updateServiceLocation(vendor, changedServiceLocations);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.removeMeterObject(vendor, removedMeters);
        return errorObject;
    }
    
    @Override
    public ErrorObject[] meterAddNotification(Meter[] addedMeters) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.addMeterObject(vendor, addedMeters);
        return errorObject;
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterExchangeNotification(
            MeterExchange[] meterChangeout) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters)
            throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        init();
        return null;
    }

    @Override
    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName,
            Calendar timeToRead, String responseURL, String transactionID)
            throws RemoteException {
        init();
        return null;
    }

}
