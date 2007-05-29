/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.service.impl.MR_CBImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_CBSoap_BindingImpl implements com.cannontech.multispeak.service.MR_CBSoap_PortType{
    private MR_CBSoap_PortType mr_cbImpl = (MR_CBImpl)YukonSpringHook.getBean("mr_cbImpl");

    public ArrayOfErrorObject cancelDisconnectedStatus(ArrayOfString meterNos) throws RemoteException {
        return mr_cbImpl.cancelDisconnectedStatus(meterNos);
    }

    public ArrayOfErrorObject cancelPlannedOutage(ArrayOfString meterNos) throws RemoteException {
        return mr_cbImpl.cancelPlannedOutage(meterNos);
    }

    public ArrayOfErrorObject cancelUsageMonitoring(ArrayOfString meterNos) throws RemoteException {
        return mr_cbImpl.cancelUsageMonitoring(meterNos);
    }

    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws RemoteException {
        return mr_cbImpl.customerChangedNotification(changedCustomers);
    }

    public ErrorObject deleteMeterGroup(String meterGroupID) throws RemoteException {
        return mr_cbImpl.deleteMeterGroup(meterGroupID);
    }

    public ArrayOfErrorObject establishMeterGroup(MeterGroup meterGroup) throws RemoteException {
        return mr_cbImpl.establishMeterGroup(meterGroup);
    }

    public ArrayOfMeter getAMRSupportedMeters(String lastReceived) throws RemoteException {
        return mr_cbImpl.getAMRSupportedMeters(lastReceived);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return mr_cbImpl.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return mr_cbImpl.getDomainNames();
    }

    public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cbImpl.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_cbImpl.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_cbImpl.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cbImpl.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID) throws RemoteException {
        return mr_cbImpl.getLatestMeterReadingsByMeterGroup(meterGroupID);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException {
        return mr_cbImpl.getLatestReadingByMeterNo(meterNo);
    }

    public ArrayOfString getMethods() throws RemoteException {
        return mr_cbImpl.getMethods();
    }

    public ArrayOfMeter getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return mr_cbImpl.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public ArrayOfFormattedBlock getReadingsByBillingCycle(String billingCycle, Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, String lastReceived) throws RemoteException {
        return mr_cbImpl.getReadingsByBillingCycle(billingCycle, billingDate, kWhLookBack, kWLookBack, kWLookForward, lastReceived);
    }

    public ArrayOfMeterRead getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_cbImpl.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public ArrayOfMeterRead getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cbImpl.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfErrorObject initiateDisconnectedStatus(ArrayOfString meterNos) throws RemoteException {
        return mr_cbImpl.initiateDisconnectedStatus(meterNos);
    }

    public ArrayOfErrorObject initiateGroupMeterRead(String meterGroupName, String responseURL) throws RemoteException {
        return mr_cbImpl.initiateGroupMeterRead(meterGroupName, responseURL);
    }

    public ArrayOfErrorObject initiateMeterReadByMeterNumber(ArrayOfString meterNos, String responseURL) throws RemoteException {
        return mr_cbImpl.initiateMeterReadByMeterNumber(meterNos, responseURL);
    }

    public ArrayOfErrorObject initiatePlannedOutage(ArrayOfString meterNos, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cbImpl.initiatePlannedOutage(meterNos, startDate, endDate);
    }

    public ArrayOfErrorObject initiateUsageMonitoring(ArrayOfString meterNos) throws RemoteException {
        return mr_cbImpl.initiateUsageMonitoring(meterNos);
    }

    public ArrayOfErrorObject insertMeterInMeterGroup(ArrayOfString meterNumbers, String meterGroupID) throws RemoteException {
        return mr_cbImpl.insertMeterInMeterGroup(meterNumbers, meterGroupID);
    }

    public boolean isAMRMeter(String meterNo) throws RemoteException {
        return mr_cbImpl.isAMRMeter(meterNo);
    }

    public ArrayOfErrorObject meterAddNotification(ArrayOfMeter addedMeters) throws RemoteException {
        return mr_cbImpl.meterAddNotification(addedMeters);
    }

    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws RemoteException {
        return mr_cbImpl.meterChangedNotification(changedMeters);
    }

    public ArrayOfErrorObject meterExchangeNotification(ArrayOfMeterExchange meterChangeout) throws RemoteException {
        return mr_cbImpl.meterExchangeNotification(meterChangeout);
    }

    public ArrayOfErrorObject meterRemoveNotification(ArrayOfMeter removedMeters) throws RemoteException {
        return mr_cbImpl.meterRemoveNotification(removedMeters);
    }

    public ArrayOfErrorObject meterRetireNotification(ArrayOfMeter retiredMeters) throws RemoteException {
        return mr_cbImpl.meterRetireNotification(retiredMeters);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return mr_cbImpl.pingURL();
    }

    public ArrayOfErrorObject removeMetersFromMeterGroup(ArrayOfString meterNumbers, String meterGroupID) throws RemoteException {
        return mr_cbImpl.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
    }

    public ArrayOfErrorObject scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL) throws RemoteException {
        return mr_cbImpl.scheduleGroupMeterRead(meterGroupName, timeToRead, responseURL);
    }

    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws RemoteException {
        return mr_cbImpl.serviceLocationChangedNotification(changedServiceLocations);
    }
}
