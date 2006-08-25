package com.cannontech.multispeak.service.impl;

import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.MR_OASoap_BindingImpl;
import com.cannontech.multispeak.service.*;
public class MR_OAImpl extends MR_OASoap_BindingImpl
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
        return null;
    }

    public ArrayOfString getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public ArrayOfErrorObject customersAffectedByOutageNotification(ArrayOfCustomersAffectedByOutage newOutages) throws java.rmi.RemoteException {
        return null;
    }

    public ArrayOfErrorObject meterConnectivityNotification(ArrayOfMeterConnectivity newConnectivity) throws java.rmi.RemoteException {
        return null;
    }
}
