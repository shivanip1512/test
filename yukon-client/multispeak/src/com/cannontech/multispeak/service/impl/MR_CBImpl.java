/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.dao.RawPointHistoryDao.ReadBy;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.service.ArrayOfCustomer;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfHistoryLog;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ArrayOfServiceLocation;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.EventCode;
import com.cannontech.multispeak.service.MR_CBSoap_BindingImpl;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.MeterRead;

public class MR_CBImpl extends MR_CBSoap_BindingImpl{

    public MultispeakDao multispeakDao;
    
    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao)
    {
        this.multispeakDao = multispeakDao;
    }

    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return MultispeakFuncs.pingURL(MultispeakDefines.MR_CB_STR);
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
                                         "getReadingsByBillingCycle",
                                         "meterRemoveNotification",
                                         "meterAddNotification",
                                         "initiateUsageMonitoring",
                                         "cancelUsageMonitoring",
                                         "initiateDisconnectedStatus",
                                         "cancelDisconnectedStatus",
                                         "serviceLocationChangedNotification"};          
        return MultispeakFuncs.getMethods(MultispeakDefines.MR_CB_STR , methods);
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        MultispeakFuncs.logArrayOfString(MultispeakDefines.MR_CB_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }

    public ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = MultispeakFuncs.getMultispeakDao().getAMRSupportedMeters(lastReceived, vendor.getUniqueKey());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] arrayOfMeters = new Meter[meterList.size()];
        meterList.toArray(arrayOfMeters);
        CTILogger.info("Returning " + arrayOfMeters.length + " AMR Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (arrayOfMeters.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        MultispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        return new ArrayOfMeter(arrayOfMeters);
    }

    public ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();

        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        if( meterNo != null && meterNo.length() > 0)
        {
            LiteYukonPAObject lPao = MultispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNo);
            if( lPao != null)
            {
                CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter(), returning true." );
                return true;
            }                   
        }       
        CTILogger.info("MSP: MeterNumber: " + meterNo + " isAMRMeter() NOT found, returning false." );
        return false;
    }

    public ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        throw new AxisFault("Method getReadingsByDate(startDate, endDate, lastReceived) is NOT supported.");
    }

    public ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
    //      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo))
            throw new AxisFault( "Meter Number (" + meterNo + "): NOT Found.");
        
        MeterRead[] meterReads = MultispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.METER_NUMBER, meterNo, startDate.getTime(), endDate.getTime(), null);
        ArrayOfMeterRead arrayOfMeterReads = new ArrayOfMeterRead(meterReads);
     
        return arrayOfMeterReads;
    }

    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
//      init(); //init is already performed on the call to isAMRMeter()
        if( ! isAMRMeter(meterNo))
            throw new RemoteException( "Meter Number (" + meterNo + "): NOT Found.");
        
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        
        //Custom hack put in only for SEDC.  Performs an actual meter read instead of simply replying from the database.
        if ( vendor.getCompanyName().equalsIgnoreCase("SEDC") ) {
        	return Multispeak.getInstance().getLatestReadingInterrogate(vendor, meterNo);
        } else	{ //THIS SHOULD BE WHERE EVERYONE ELSE GOES!!! 
	        LiteYukonPAObject lPao = MultispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNo);
	        ReadableDevice device = MeterReadFactory.createMeterReadObject(lPao.getCategory(), lPao.getType(), meterNo);
	        device.populateWithPointData(lPao.getYukonID());
	        return device.getMeterRead();
        }
    }

    public ArrayOfMeterRead getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        MeterRead[] meterReads = MultispeakFuncs.getMspRawPointHistoryDao().retrieveMeterReads(ReadBy.BILL_GROUP, billingCycle, startDate.getTime(), endDate.getTime(), lastReceived);
        //TODO = need to get the true number of meters remaining
        int numRemaining = (meterReads.length < MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        MultispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));

        ArrayOfMeterRead arrayOfMeterReads = new ArrayOfMeterRead(meterReads);
        return arrayOfMeterReads;
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
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().initiateStatusChange(vendor, meterNos.getString(), DeviceMeterGroup.USAGE_MONITORING_GROUP_PREFIX);
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject cancelUsageMonitoring(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().cancelStatusChange(vendor, meterNos.getString(), DeviceMeterGroup.USAGE_MONITORING_GROUP_PREFIX);
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject initiateDisconnectedStatus(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().initiateStatusChange(vendor, meterNos.getString(), DeviceMeterGroup.DISCONNECTED_GROUP_PREFIX);
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject cancelDisconnectedStatus(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().cancelStatusChange(vendor, meterNos.getString(), DeviceMeterGroup.DISCONNECTED_GROUP_PREFIX);
        return new ArrayOfErrorObject(errorObject);
    }

    //Perform an actual read of the meter and return a CB_MR readingChangedNotification message for each meterNo
    public ArrayOfErrorObject initiateMeterReadByMeterNumber(ArrayOfString meterNos) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        String url = (vendor != null ? vendor.getUrl() : "(none)");
        if( url == null || url.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
            throw new AxisFault("Vendor unknown.  Please contact Yukon administrator to setup a Multispeak Interface Vendor in Yukon.");
        }
        else if ( ! Multispeak.getInstance().getPilConn().isValid() ) {
            throw new AxisFault("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        errorObjects = Multispeak.getInstance().MeterReadEvent(vendor, meterNos.getString());

        MultispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.MR_CB_STR, "initiateMeterReadByMeterNumberRequest", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }

    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().updateServiceLocation(vendor, changedServiceLocations.getServiceLocation());
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject meterRemoveNotification(ArrayOfMeter removedMeters) throws java.rmi.RemoteException {
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().removeMeterObject(vendor, removedMeters.getMeter());
        return new ArrayOfErrorObject(errorObject);
    }

    public ArrayOfErrorObject meterAddNotification(ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        ErrorObject[] errorObject = Multispeak.getInstance().addMeterObject(vendor, addedMeters.getMeter());
        return new ArrayOfErrorObject(errorObject);
    }
    private void init()
    {
        MultispeakFuncs.init();
    }
}
