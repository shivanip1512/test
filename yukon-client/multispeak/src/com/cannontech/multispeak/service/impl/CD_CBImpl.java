package com.cannontech.multispeak.service.impl;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.ArrayOfConnectDisconnectEvent;
import com.cannontech.multispeak.service.ArrayOfCustomer;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfServiceLocation;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.CD_CBSoap_BindingImpl;
import com.cannontech.multispeak.service.DomainMember;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.LoadActionCode;
import com.cannontech.multispeak.service.Meter;

public class CD_CBImpl extends CD_CBSoap_BindingImpl
{
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
        return MultispeakFuncs.pingURL(MultispeakDefines.CD_CB_STR);
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods",
        								 "getCDSupportedMeters",
                                         "getCDMeterState",
                                         "initiateConnectDisconnect"};
        return MultispeakFuncs.getMethods(MultispeakDefines.CD_CB_STR, methods );
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        MultispeakFuncs.logArrayOfString(MultispeakDefines.CD_CB_STR, "getDomainNames", strings);
        return new ArrayOfString(strings);
    }

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new ArrayOfDomainMember(new DomainMember[0]);
    }
    
    public ArrayOfMeter getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        MultispeakVendor vendor = MultispeakFuncs.getMultispeakVendorFromHeader();

        List meterList = null;
        Date timerStart = new Date();
        try {
            meterList = MultispeakFuncs.getMultispeakDao().getCDSupportedMeters(lastReceived, vendor.getUniqueKey());
        } catch(NotFoundException nfe) {
            //Not an error, it could happen that there are no more entries.
        }
        
        Meter[] arrayOfMeters = new Meter[meterList.size()];
        meterList.toArray(arrayOfMeters);
        CTILogger.info("Returning " + arrayOfMeters.length + " CD Supported Meters. (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");             
        //TODO = need to get the true number of meters remaining
        int numRemaining = (arrayOfMeters.length <= MultispeakDefines.MAX_RETURN_RECORDS ? 0:1); //at least one item remaining, bad assumption.
        ((YukonMultispeakMsgHeader)MessageContext.getCurrentContext().getResponseMessage().getSOAPEnvelope().getHeaderByName("http://www.multispeak.org", "MultiSpeakMsgHeader").getObjectValue()).setObjectsRemaining(new BigInteger(String.valueOf(numRemaining)));
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
            vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new AxisFault("Vendor unknown.  Please contact Yukon administrator to setup a Multispeak Interface Vendor in Yukon.");
        }
        if ( ! Multispeak.getInstance().getPilConn().isValid() ) {
            throw new AxisFault("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        LoadActionCode loadActionCode = Multispeak.getInstance().CDMeterState(vendor, meterNo);
        return loadActionCode;
    }

    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws java.rmi.RemoteException {
        init();
        ErrorObject[] errorObjects = new ErrorObject[0];
        
        MultispeakVendor vendor = null;
        try {
            vendor = MultispeakFuncs.getMultispeakVendorFromHeader();
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new AxisFault("Vendor unknown.  Please contact Yukon administrator to setup a Multispeak Interface Vendor in Yukon.");
        }
        
        if ( ! Multispeak.getInstance().getPilConn().isValid() ) {
            throw new RemoteException("Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }
        else{
            errorObjects = Multispeak.getInstance().CDEvent(vendor, cdEvents.getConnectDisconnectEvent());
        }
        
        MultispeakFuncs.logArrayOfErrorObjects(MultispeakDefines.CD_CB_STR, "initiateConnectDisconnect", errorObjects);
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
    private void init(){
        MultispeakFuncs.init();
    }
}
