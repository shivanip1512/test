/**
 * CD_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.service.impl.CD_CBImpl;
import com.cannontech.spring.YukonSpringHook;

public class CD_CBSoap_BindingImpl implements com.cannontech.multispeak.service.CD_CBSoap_PortType{
    private CD_CBSoap_PortType cd_cb = (CD_CBImpl)YukonSpringHook.getBean("cd_cb");

    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws RemoteException {
        return cd_cb.customerChangedNotification(changedCustomers);
    }

    public LoadActionCode getCDMeterState(String meterNo) throws RemoteException {
        return cd_cb.getCDMeterState(meterNo);
    }

    public ArrayOfMeter getCDSupportedMeters(String lastReceived) throws RemoteException {
        return cd_cb.getCDSupportedMeters(lastReceived);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return cd_cb.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return cd_cb.getDomainNames();
    }

    public ArrayOfString getMethods() throws RemoteException {
        return cd_cb.getMethods();
    }

    public ArrayOfMeter getModifiedCDMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return cd_cb.getModifiedCDMeters(previousSessionID, lastReceived);
    }

    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws RemoteException {
        return cd_cb.initiateConnectDisconnect(cdEvents);
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws RemoteException {
        return cd_cb.meterChangedNotification(changedMeters);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return cd_cb.pingURL();
    }

    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws RemoteException {
        return cd_cb.serviceLocationChangedNotification(changedServiceLocations);
    }
}
