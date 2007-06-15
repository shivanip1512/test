/**
 * MR_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.service.impl.MR_OAImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_OASoap_BindingImpl implements com.cannontech.multispeak.service.MR_OASoap_PortType{
    private MR_OASoap_PortType mr_oa = (MR_OAImpl)YukonSpringHook.getBean("mr_oa");

    public ArrayOfErrorObject customersAffectedByOutageNotification(ArrayOfCustomersAffectedByOutage newOutages) throws RemoteException {
        return mr_oa.customersAffectedByOutageNotification(newOutages);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return mr_oa.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return mr_oa.getDomainNames();
    }

    public ArrayOfString getMethods() throws RemoteException {
        return mr_oa.getMethods();
    }

    public ArrayOfErrorObject meterConnectivityNotification(ArrayOfMeterConnectivity newConnectivity) throws RemoteException {
        return mr_oa.meterConnectivityNotification(newConnectivity);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return mr_oa.pingURL();
    }
}
