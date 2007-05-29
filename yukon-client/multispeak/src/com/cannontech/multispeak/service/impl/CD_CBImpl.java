package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.ArrayOfConnectDisconnectEvent;
import com.cannontech.multispeak.service.ArrayOfCustomer;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfServiceLocation;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.CD_CBSoap_PortType;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.LoadActionCode;
import com.cannontech.multispeak.service.Meter;

public class CD_CBImpl implements CD_CBSoap_PortType
{
    public Multispeak multispeak;
    public MultispeakDao multispeakDao;
    public MultispeakFuncs multispeakFuncs;
    
    /**
     * @param multispeakDao The multispeakDao to set.
     */
    public void setMultispeakDao(MultispeakDao multispeakDao)
    {
        this.multispeakDao = multispeakDao;
    }

    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    private void init(){
        multispeakFuncs.init();
    }
    
    public ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        init();
        return new ArrayOfErrorObject(new ErrorObject[0]);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods",
        								 "getCDSupportedMeters",
                                         "getCDMeterState",
                                         "initiateConnectDisconnect"};
        return multispeakFuncs.getMethods(MultispeakDefines.CD_CB_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logArrayOfString(MultispeakDefines.CD_CB_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }
    
    public ArrayOfMeter getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = multispeakFuncs.getMultispeakVendorFromHeader();

        List<Meter> meterList = null;
        Date timerStart = new Date();
        try {
            meterList = multispeakDao.getCDSupportedMeters(lastReceived, vendor.getUniqueKey());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] arrayOfMeters = new Meter[meterList.size()];
        meterList.toArray(arrayOfMeters);
        CTILogger.info("Returning " + arrayOfMeters.length + " CD Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (arrayOfMeters.length <= MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        multispeakFuncs.getResponseHeader().setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
        return new ArrayOfMeter(arrayOfMeters);
    }

    public ArrayOfMeter getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = null;
        
        try {
            vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new AxisFault("Vendor unknown.  Please contact Yukon administrator to setup a Multispeak Interface Vendor in Yukon.");
        }

        LoadActionCode loadActionCode = multispeak.CDMeterState(vendor, meterNo);
        return loadActionCode;
    }

    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = null;
        try {
            vendor = multispeakFuncs.getMultispeakVendorFromHeader();
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new AxisFault("Vendor unknown.  Please contact Yukon administrator to setup a Multispeak Interface Vendor in Yukon.");
        }
        
        errorObjects = multispeak.CDEvent(vendor, cdEvents.getConnectDisconnectEvent());
        
        multispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.CD_CB_STR, "initiateConnectDisconnect", errorObjects);
        return new ArrayOfErrorObject(errorObjects);
    }

    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
        init();
        return null;
    }
}
