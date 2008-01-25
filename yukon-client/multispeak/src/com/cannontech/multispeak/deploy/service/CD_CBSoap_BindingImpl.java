/**
 * CD_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.deploy.service.impl.CD_CBImpl;
import com.cannontech.spring.YukonSpringHook;

public class CD_CBSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.CD_CBSoap_PortType{
    private CD_CBSoap_PortType cd_cb = (CD_CBImpl)YukonSpringHook.getBean("cd_cb");

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return cd_cb.customerChangedNotification(changedCustomers);
    }

    public LoadActionCode getCDMeterState(String meterNo)
            throws RemoteException {
        return cd_cb.getCDMeterState(meterNo);
    }

    public Meter[] getCDSupportedMeters(String lastReceived)
            throws RemoteException {
        return cd_cb.getCDSupportedMeters(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return cd_cb.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return cd_cb.getDomainNames();
    }

    public String[] getMethods() throws RemoteException {
        return cd_cb.getMethods();
    }

    public Meter[] getModifiedCDMeters(String previousSessionID,
            String lastReceived) throws RemoteException {
        return cd_cb.getModifiedCDMeters(previousSessionID, lastReceived);
    }

    public ErrorObject[] initiateConnectDisconnect(
            ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID) throws RemoteException {
        return cd_cb.initiateConnectDisconnect(cdEvents,
                                               responseURL,
                                               transactionID);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return cd_cb.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return cd_cb.pingURL();
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return cd_cb.serviceLocationChangedNotification(changedServiceLocations);
    }
}
