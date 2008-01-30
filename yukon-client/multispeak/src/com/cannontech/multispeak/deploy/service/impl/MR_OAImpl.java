package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;

import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MR_OASoap_PortType;
import com.cannontech.multispeak.deploy.service.MeterConnectivity;
public class MR_OAImpl implements MR_OASoap_PortType
{
    public MultispeakFuncs multispeakFuncs;

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    private void init() throws RemoteException {
        multispeakFuncs.init();
    }
    
    @Override
    public ErrorObject[] customersAffectedByOutageNotification(CustomersAffectedByOutage[] newOutages) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public String[] getDomainNames() throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public String[] getMethods() throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] meterConnectivityNotification(MeterConnectivity[] newConnectivity) throws RemoteException {
        init();
        return null;
    }
    
    @Override
    public ErrorObject[] pingURL() throws RemoteException {
        init();
        return null;
    }
    
}
