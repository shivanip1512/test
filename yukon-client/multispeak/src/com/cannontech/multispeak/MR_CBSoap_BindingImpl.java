/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

import com.cannontech.database.cache.functions.PAOFuncs;

public class MR_CBSoap_BindingImpl implements com.cannontech.multispeak.MR_CBSoap_PortType{
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException {
        return false;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
    	MeterRead mr = new MeterRead();
        return null;
    }

    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter changedMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.ArrayOfMeter removedMeters) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.ArrayOfMeter addedMeters) throws java.rmi.RemoteException {
        return null;
    }

}
