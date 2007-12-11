/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service.impl;

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
import com.cannontech.multispeak.service.ArrayOfCustomer;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfFormattedBlock;
import com.cannontech.multispeak.service.ArrayOfHistoryLog;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfMeterExchange;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ArrayOfServiceLocation;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.EventCode;
import com.cannontech.multispeak.service.FormattedBlock;
import com.cannontech.multispeak.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.MeterGroup;
import com.cannontech.multispeak.service.MeterRead;
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

    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
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
    
    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logArrayOfString(MultispeakDefines.MR_CB_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }
    
    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }

    public ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = mspMeterDao.getAMRSupportedMeters(lastReceived, vendor.getUniqueKey());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] arrayOfMeters = new Meter[meterList.size()];
        meterList.toArray(arrayOfMeters);
        CTILogger.info("Returning " + arrayOfMeters.length + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (arrayOfMeters.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        return new ArrayOfMeter(arrayOfMeters);
    }
    
    public ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
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
    
    public ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        throw new RemoteException("Method getReadingsByDate(startDate, endDate, lastReceived) is NOT supported.");
    }
    
    public ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
    //      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo))
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        
        MeterRead[] meterReads = mspRawPointHistoryDao.retrieveMeterReads(ReadBy.METER_NUMBER, meterNo, startDate.getTime(), endDate.getTime(), null);
        ArrayOfMeterRead arrayOfMeterReads = new ArrayOfMeterRead(meterReads);
     
        return arrayOfMeterReads;
    }


    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo))
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        com.cannontech.amr.meter.model.Meter meter;
        
        try {
            meter = multispeakFuncs.getMeter(vendor.getUniqueKey(), meterNo);
        }catch (NotFoundException e) {
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        }
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") ) {
        	return multispeak.getLatestReadingInterrogate(vendor, meter);
        } else	{ //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!!
            try {
    	        ReadableDevice device = MeterReadFactory.createMeterReadObject(meter);
    	        device.populateWithPointData(meter.getDeviceId());
    	        return device.getMeterRead();
            } catch (DynamicDataAccessException e) {
                throw new RemoteException("Connection to dispatch is invalid");
            }
        }
    }
    
    public ArrayOfFormattedBlock getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException {
        /* TODO
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = multispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        ArrayOfMeterRead arrayOfMeterReads = new ArrayOfMeterRead(meterReads);
        return arrayOfMeterReads;
        */
        throw new RemoteException("Method getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) is NOT supported.");
    }
    
    public ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    public ArrayOfErrorObject initiatePlannedOutage(ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject cancelPlannedOutage(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject initiateUsageMonitoring(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.initiateUsageMonitoringStatus(vendor, meterNos.getString());
        return new ArrayOfErrorObject(errorObject);
    }
    
    public ArrayOfErrorObject cancelUsageMonitoring(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.cancelUsageMonitoringStatus(vendor, meterNos.getString());
        return new ArrayOfErrorObject(errorObject);
    }
    
    public ArrayOfErrorObject initiateDisconnectedStatus(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.initiateDisconnectedStatus(vendor, meterNos.getString());
        return new ArrayOfErrorObject(errorObject);
    }
    
    public ArrayOfErrorObject cancelDisconnectedStatus(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.cancelDisconnectedStatus(vendor, meterNos.getString());
        return new ArrayOfErrorObject(errorObject);
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    public ArrayOfErrorObject initiateMeterReadByMeterNumber(ArrayOfString meterNos, String responseURL) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        if ( ! porterConnection.isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        errorObjects = multispeak.MeterReadEvent(vendor, meterNos.getString());

        multispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.MR_CB_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }
    
    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
        init();
        return null;
    }
    
    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.updateServiceLocation(vendor, changedServiceLocations.getServiceLocation());
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject meterRemoveNotification(ArrayOfMeter removedMeters) throws java.rmi.RemoteException {
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.removeMeterObject(vendor, removedMeters.getMeter());
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject meterAddNotification(ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = multispeak.addMeterObject(vendor, addedMeters.getMeter());
        return new ArrayOfErrorObject(errorObject);
    }

    public ErrorObject deleteMeterGroup(String meterGroupID) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject establishMeterGroup(MeterGroup meterGroup) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject initiateGroupMeterRead(String meterGroupName, String responseURL) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject insertMeterInMeterGroup(ArrayOfString meterNumbers, String meterGroupID) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject meterExchangeNotification(ArrayOfMeterExchange meterChangeout) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject meterRetireNotification(ArrayOfMeter retiredMeters) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject removeMetersFromMeterGroup(ArrayOfString meterNumbers, String meterGroupID) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
}
