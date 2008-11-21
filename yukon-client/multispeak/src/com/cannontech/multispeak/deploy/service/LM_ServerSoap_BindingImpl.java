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

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return lm_server.customerChangedNotification(changedCustomers);
    }

    public LoadManagementDevice[] getAllLoadManagementDevices(
            String lastReceived) throws RemoteException {
        return lm_server.getAllLoadManagementDevices(lastReceived);
    }

    public SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses()
            throws RemoteException {
        return lm_server.getAllSubstationLoadControlStatuses();
    }

    public float getAmountOfControllableLoad() throws RemoteException {
        return lm_server.getAmountOfControllableLoad();
    }

    public float getAmountOfControlledLoad() throws RemoteException {
        return lm_server.getAmountOfControlledLoad();
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return lm_server.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return lm_server.getDomainNames();
    }

    public LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(
            String meterNo) throws RemoteException {
        return lm_server.getLoadManagementDeviceByMeterNumber(meterNo);
    }

    public LoadManagementDevice[] getLoadManagementDeviceByServLoc(
            String servLoc) throws RemoteException {
        return lm_server.getLoadManagementDeviceByServLoc(servLoc);
    }

    public String[] getMethods() throws RemoteException {
        return lm_server.getMethods();
    }

    public void intiateLoadManagementEvent(LoadManagementEvent theLMEvent)
            throws RemoteException {
        lm_server.intiateLoadManagementEvent(theLMEvent);
    }

    public void intiateLoadManagementEvents(LoadManagementEvent[] theLMEvents)
            throws RemoteException {
        lm_server.intiateLoadManagementEvents(theLMEvents);
    }

    public void intiatePowerFactorManagementEvent(
            PowerFactorManagementEvent thePFMEvent) throws RemoteException {
        lm_server.intiatePowerFactorManagementEvent(thePFMEvent);
    }

    public boolean isLoadManagementActive(String servLoc)
            throws RemoteException {
        return lm_server.isLoadManagementActive(servLoc);
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

    public ErrorObject[] pingURL() throws RemoteException {
        return lm_server.pingURL();
    }

    public ErrorObject[] SCADAAnalogChangedNotification(
            ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return lm_server.SCADAAnalogChangedNotification(scadaAnalogs);
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

    public ErrorObject[] SCADAStatusChangedNotification(
            ScadaStatus[] scadaStatuses) throws RemoteException {
        return lm_server.SCADAStatusChangedNotification(scadaStatuses);
    }

    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus)
            throws RemoteException {
        lm_server.SCADAStatusChangedNotificationByPointID(scadaStatus);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return lm_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    
}
