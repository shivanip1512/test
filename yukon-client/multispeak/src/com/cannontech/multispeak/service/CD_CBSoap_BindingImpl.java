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
    private CD_CBSoap_PortType cd_cbImpl = (CD_CBImpl)YukonSpringHook.getBean("cd_cbImpl");

    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws RemoteException {
        return cd_cbImpl.customerChangedNotification(changedCustomers);
    }

    public LoadActionCode getCDMeterState(String meterNo) throws RemoteException {
        return cd_cbImpl.getCDMeterState(meterNo);
    }

    public ArrayOfMeter getCDSupportedMeters(String lastReceived) throws RemoteException {
        return cd_cbImpl.getCDSupportedMeters(lastReceived);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return cd_cbImpl.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return cd_cbImpl.getDomainNames();
    }

    public ArrayOfString getMethods() throws RemoteException {
        return cd_cbImpl.getMethods();
    }

    public ArrayOfMeter getModifiedCDMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return cd_cbImpl.getModifiedCDMeters(previousSessionID, lastReceived);
    }

    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws RemoteException {
        return cd_cbImpl.initiateConnectDisconnect(cdEvents);
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws RemoteException {
        return cd_cbImpl.meterChangedNotification(changedMeters);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return cd_cbImpl.pingURL();
    }

    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws RemoteException {
        return cd_cbImpl.serviceLocationChangedNotification(changedServiceLocations);
    }
}
