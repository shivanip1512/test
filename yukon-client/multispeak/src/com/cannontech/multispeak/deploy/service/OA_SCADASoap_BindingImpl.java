/**
 * OA_SCADASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public class OA_SCADASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.OA_SCADASoap_PortType{
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] getActiveOutages() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.Crew[] getAllCrews(boolean activeOnly, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageEvent[] getAllActiveOutageEvents(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageEvent getOutageEvent(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage getCustomersAffectedByOutage(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getAllActiveCalls(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCallsReceivedOnOutage(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getCustomerOutageHistory(java.lang.String accountNumber, java.lang.String servLoc) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCustomerCallHistory(java.lang.String accountNumber, java.lang.String servLoc) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageDurationEvents(java.lang.String outageEventID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageHistoryOnServiceLocation(java.lang.String servLoc) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCustomerCallsOnServiceLocation(java.lang.String servLoc) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException {
        return null;
    }

    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException {
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException {
        return null;
    }

    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException {
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject restoreOutage(java.lang.String outageEventID, java.util.Calendar eventTime) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject assignCrewsToOutage(java.lang.String outageEventID, java.lang.String[] crewsAssigned, java.util.Calendar eventTime) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutage(java.lang.String outageEventID, java.lang.String remarks, java.util.Calendar eventTime) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject[] setOutageElementStatus(java.lang.String troubledElement, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC, java.util.Calendar eventTime) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject unassignCrewsFromOutage(java.lang.String outageEventID, java.util.Calendar eventTime, java.lang.String reason, java.lang.String[] crewsUnassigned, java.lang.String comment) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.deploy.service.ErrorObject unassignOutagesFromCrew(java.lang.String crewID, java.util.Calendar eventTime, java.lang.String reason, java.lang.String[] outageEventIDs, java.lang.String comment) throws java.rmi.RemoteException {
        return null;
    }

}
