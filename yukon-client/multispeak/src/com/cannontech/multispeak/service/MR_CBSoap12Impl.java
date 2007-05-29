/**
 * MR_CBSoap12Impl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class MR_CBSoap12Impl implements com.cannontech.multispeak.service.MR_CBSoap_PortType{
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
        return false;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfFormattedBlock getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.FormattedBlock getLatestMeterReadingsByMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.service.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.service.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.service.ArrayOfString meterNos, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject establishMeterGroup(com.cannontech.multispeak.service.MeterGroup meterGroup) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ErrorObject deleteMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject insertMeterInMeterGroup(com.cannontech.multispeak.service.ArrayOfString meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject removeMetersFromMeterGroup(com.cannontech.multispeak.service.ArrayOfString meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateGroupMeterRead(java.lang.String meterGroupName, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject scheduleGroupMeterRead(java.lang.String meterGroupName, java.util.Calendar timeToRead, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.service.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.service.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.service.ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.service.ArrayOfMeter removedMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterRetireNotification(com.cannontech.multispeak.service.ArrayOfMeter retiredMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.service.ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject meterExchangeNotification(com.cannontech.multispeak.service.ArrayOfMeterExchange meterChangeout) throws java.rmi.RemoteException {
        return null;
    }

}
