/**
 * CD_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.spring.YukonSpringHook;

public class CD_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.CD_ServerSoap_PortType{
    
    private CD_ServerSoap_PortType cd_server = YukonSpringHook.getBean("cd_server", CD_ServerSoap_PortType.class);

    public ErrorObject[] pingURL() throws RemoteException {
        return cd_server.pingURL();
    }

    public String[] getMethods() throws RemoteException {
        return cd_server.getMethods();
    }

    public String[] getDomainNames() throws RemoteException {
        return cd_server.getDomainNames();
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return cd_server.getDomainMembers(domainName);
    }

    public String requestRegistrationID() throws RemoteException {
        return cd_server.requestRegistrationID();
    }

    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        return cd_server.registerForService(registrationDetails);
    }

    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        return cd_server.unregisterForService(registrationID);
    }

    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        return cd_server.getRegistrationInfoByID(registrationID);
    }

    public String[] getPublishMethods() throws RemoteException {
        return cd_server.getPublishMethods();
    }

    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        return cd_server.domainMembersChangedNotification(changedDomainMembers);
    }

    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        return cd_server.domainNamesChangedNotification(changedDomainNames);
    }

    public Meter[] getCDSupportedMeters(String lastReceived)
            throws RemoteException {
        return cd_server.getCDSupportedMeters(lastReceived);
    }

    public Meter[] getModifiedCDMeters(String previousSessionID,
            String lastReceived) throws RemoteException {
        return cd_server.getModifiedCDMeters(previousSessionID, lastReceived);
    }

    public LoadActionCode getCDMeterState(String meterNo)
            throws RemoteException {
        return cd_server.getCDMeterState(meterNo);
    }

    public ErrorObject[] initiateConnectDisconnect(
            ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID, Float expirationTime) throws RemoteException {
        return cd_server.initiateConnectDisconnect(cdEvents,
                                                   responseURL,
                                                   transactionID,
                                                   expirationTime);
    }

    public ErrorObject[] initiateCDStateRequest(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return cd_server.initiateCDStateRequest(states,
                                                responseURL,
                                                transactionID,
                                                expirationTime);
    }

    public ErrorObject[] initiateArmCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return cd_server.initiateArmCDDevice(states,
                                             responseURL,
                                             transactionID,
                                             expirationTime);
    }

    public ErrorObject[] initiateEnableCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return cd_server.initiateEnableCDDevice(states,
                                                responseURL,
                                                transactionID,
                                                expirationTime);
    }

    public ErrorObject[] initiateDisableCDDevice(CDState[] states,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return cd_server.initiateDisableCDDevice(states,
                                                 responseURL,
                                                 transactionID,
                                                 expirationTime);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return cd_server.customerChangedNotification(changedCustomers);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return cd_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return cd_server.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs)
            throws RemoteException {
        return cd_server.CDDeviceAddNotification(addedCDDs);
    }

    public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs)
            throws RemoteException {
        return cd_server.CDDeviceChangedNotification(changedCDDs);
    }

    public ErrorObject[] CDDeviceExchangeNotification(
            CDDeviceExchange[] CDDChangeout) throws RemoteException {
        return cd_server.CDDeviceExchangeNotification(CDDChangeout);
    }

    public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs)
            throws RemoteException {
        return cd_server.CDDeviceRemoveNotification(removedCDDs);
    }

    public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs)
            throws RemoteException {
        return cd_server.CDDeviceRetireNotification(retiredCDDs);
    }

    
}
