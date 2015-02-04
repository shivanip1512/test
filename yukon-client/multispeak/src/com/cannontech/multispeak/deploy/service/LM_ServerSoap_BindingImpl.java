/**
 * LM_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.spring.YukonSpringHook;

public class LM_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.LM_ServerSoap_PortType{
    private LM_ServerSoap_PortType lm_server = YukonSpringHook.getBean("lm_server", LM_ServerSoap_PortType.class);

    public ErrorObject[] pingURL() throws RemoteException {
        return lm_server.pingURL();
    }

    public String[] getMethods() throws RemoteException {
        return lm_server.getMethods();
    }

    public String[] getDomainNames() throws RemoteException {
        return lm_server.getDomainNames();
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return lm_server.getDomainMembers(domainName);
    }

    public String requestRegistrationID() throws RemoteException {
        return lm_server.requestRegistrationID();
    }

    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        return lm_server.registerForService(registrationDetails);
    }

    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        return lm_server.unregisterForService(registrationID);
    }

    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        return lm_server.getRegistrationInfoByID(registrationID);
    }

    public String[] getPublishMethods() throws RemoteException {
        return lm_server.getPublishMethods();
    }

    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        return lm_server.domainMembersChangedNotification(changedDomainMembers);
    }

    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        return lm_server.domainNamesChangedNotification(changedDomainNames);
    }

    public LoadManagementDevice[] getAllLoadManagementDevices(
            String lastReceived) throws RemoteException {
        return lm_server.getAllLoadManagementDevices(lastReceived);
    }

    public LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(
            String meterNo) throws RemoteException {
        return lm_server.getLoadManagementDeviceByMeterNumber(meterNo);
    }

    public LoadManagementDevice[] getLoadManagementDeviceByServLoc(
            String servLoc) throws RemoteException {
        return lm_server.getLoadManagementDeviceByServLoc(servLoc);
    }

    public boolean isLoadManagementActive(String servLoc)
            throws RemoteException {
        return lm_server.isLoadManagementActive(servLoc);
    }

    public float getAmountOfControllableLoad() throws RemoteException {
        return lm_server.getAmountOfControllableLoad();
    }

    public float getAmountOfControlledLoad() throws RemoteException {
        return lm_server.getAmountOfControlledLoad();
    }

    public SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses()
            throws RemoteException {
        return lm_server.getAllSubstationLoadControlStatuses();
    }

    public ErrorObject initiateLoadManagementEvent(
            LoadManagementEvent theLMEvent) throws RemoteException {
        return lm_server.initiateLoadManagementEvent(theLMEvent);
    }

    public ErrorObject[] initiateLoadManagementEvents(
            LoadManagementEvent[] theLMEvents) throws RemoteException {
        return lm_server.initiateLoadManagementEvents(theLMEvents);
    }

    public ErrorObject initiatePowerFactorManagementEvent(
            PowerFactorManagementEvent thePFMEvent) throws RemoteException {
        return lm_server.initiatePowerFactorManagementEvent(thePFMEvent);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return lm_server.customerChangedNotification(changedCustomers);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return lm_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    public ErrorObject[] LMDeviceAddNotification(
            LoadManagementDevice[] addedLMDs) throws RemoteException {
        return lm_server.LMDeviceAddNotification(addedLMDs);
    }

    public ErrorObject[] LMDeviceChangedNotification(
            LoadManagementDevice[] changedLMDs) throws RemoteException {
        return lm_server.LMDeviceChangedNotification(changedLMDs);
    }

    public ErrorObject[] LMDeviceExchangeNotification(
            LMDeviceExchange[] LMDChangeout) throws RemoteException {
        return lm_server.LMDeviceExchangeNotification(LMDChangeout);
    }

    public ErrorObject[] LMDeviceRemoveNotification(
            LoadManagementDevice[] removedLMDs) throws RemoteException {
        return lm_server.LMDeviceRemoveNotification(removedLMDs);
    }

    public ErrorObject[] LMDeviceRetireNotification(
            LoadManagementDevice[] retiredLMDs) throws RemoteException {
        return lm_server.LMDeviceRetireNotification(retiredLMDs);
    }

    public ErrorObject[] SCADAAnalogChangedNotification(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return lm_server.SCADAAnalogChangedNotification(scadaAnalogs);
    }

    public ErrorObject[] SCADAStatusChangedNotification(
            ScadaStatus[] scadaStatuses) throws RemoteException {
        return lm_server.SCADAStatusChangedNotification(scadaStatuses);
    }

    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints)
            throws RemoteException {
        return lm_server.SCADAPointChangedNotification(scadaPoints);
    }

    public ErrorObject[] SCADAPointChangedNotificationForAnalog(
            ScadaPoint[] scadaPoints) throws RemoteException {
        return lm_server.SCADAPointChangedNotificationForAnalog(scadaPoints);
    }

    public ErrorObject[] SCADAPointChangedNotificationForStatus(
            ScadaPoint[] scadaPoints) throws RemoteException {
        return lm_server.SCADAPointChangedNotificationForStatus(scadaPoints);
    }

    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog)
            throws RemoteException {
        lm_server.SCADAAnalogChangedNotificationByPointID(scadaAnalog);
    }

    public ErrorObject[] SCADAAnalogChangedNotificationForPower(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return lm_server.SCADAAnalogChangedNotificationForPower(scadaAnalogs);
    }

    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return lm_server.SCADAAnalogChangedNotificationForVoltage(scadaAnalogs);
    }

    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus)
            throws RemoteException {
        lm_server.SCADAStatusChangedNotificationByPointID(scadaStatus);
    }

    
}
