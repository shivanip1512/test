package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;

import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage;
import com.cannontech.multispeak.service.ArrayOfDomainMember;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeterConnectivity;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.MR_OASoap_BindingImpl;
import com.cannontech.multispeak.service.MR_OASoap_PortType;
public class MR_OAImpl implements MR_OASoap_PortType
{
    public ArrayOfErrorObject customersAffectedByOutageNotification(ArrayOfCustomersAffectedByOutage newOutages) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfString getMethods() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject meterConnectivityNotification(ArrayOfMeterConnectivity newConnectivity) throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
