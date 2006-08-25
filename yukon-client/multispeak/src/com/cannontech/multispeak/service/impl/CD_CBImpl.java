package com.cannontech.multispeak.service.impl;

import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.*;

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
        String [] methods = new String[]{"pingURL", "getMethods"};
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
        return null;
    }

    public ArrayOfMeter getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException {
        init();
        return null;
    }

    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws java.rmi.RemoteException {
        init();
        return null;
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
