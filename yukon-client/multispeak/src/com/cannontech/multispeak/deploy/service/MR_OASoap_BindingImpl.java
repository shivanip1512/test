/**
 * MR_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.deploy.service.impl.MR_OAImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_OASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_OASoap_PortType{
    private MR_OASoap_PortType mr_oa = YukonSpringHook.getBean("mr_oa", MR_OAImpl.class);

    public ErrorObject[] customersAffectedByOutageNotification(CustomersAffectedByOutage[] newOutages)
            throws RemoteException {
        return mr_oa.customersAffectedByOutageNotification(newOutages);
    }

    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        return mr_oa.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return mr_oa.getDomainNames();
    }

    public String[] getMethods() throws RemoteException {
        return mr_oa.getMethods();
    }

    public ErrorObject[] meterConnectivityNotification(MeterConnectivity[] newConnectivity) throws RemoteException {
        return mr_oa.meterConnectivityNotification(newConnectivity);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return mr_oa.pingURL();
    }
}
