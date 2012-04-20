/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.spring.YukonSpringHook;

public class MR_CBSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType{
    private MR_ServerSoap_PortType mr_server = YukonSpringHook.getBean("mr_server", MR_ServerSoap_PortType.class);

    @Override
    public ErrorObject[] pingURL() throws RemoteException {
        return mr_server.pingURL();
    }

    @Override
    public String[] getMethods() throws RemoteException {
        return mr_server.getMethods();
    }

    @Override
    public String[] getDomainNames() throws RemoteException {
        return mr_server.getDomainNames();
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return mr_server.getDomainMembers(domainName);
    }

    @Override
    public Meter[] getAMRSupportedMeters(String lastReceived)
            throws RemoteException {
        return mr_server.getAMRSupportedMeters(lastReceived);
    }

    @Override
    public Meter[] getModifiedAMRMeters(String previousSessionID,
            String lastReceived) throws RemoteException {
        return mr_server.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    @Override
    public boolean isAMRMeter(String meterNo) throws RemoteException {
        return mr_server.isAMRMeter(meterNo);
    }

    @Override
    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate,
            String lastReceived) throws RemoteException {
        return mr_server.getReadingsByDate(startDate, endDate, lastReceived);
    }

    @Override
    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate,
            Calendar endDate) throws RemoteException {
        return mr_server.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(String meterNo)
            throws RemoteException {
        return mr_server.getLatestReadingByMeterNo(meterNo);
    }

    @Override
    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle,
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived) throws RemoteException {
        return mr_server.getReadingsByBillingCycle(billingCycle, billingDate, kWhLookBack, kWLookBack, kWLookForward, lastReceived, null, null);
    }

    @Override
    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(
            String meterNumber, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived)
            throws RemoteException {
        return mr_server.getReadingByMeterNumberFormattedBlock(meterNumber, billingDate, kWhLookBack, kWLookBack, kWLookForward, lastReceived, null, null);
    }

    @Override
    public FormattedBlock[] getReadingsByDateFormattedBlock(
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived) throws RemoteException {
        return mr_server.getReadingsByDateFormattedBlock(billingDate, kWhLookBack, kWLookBack, kWLookForward, lastReceived, null, null);
    }

    @Override
    public HistoryLog[] getHistoryLogByMeterNo(String meterNo,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_server.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    @Override
    public HistoryLog[] getHistoryLogsByDate(Calendar startDate,
            Calendar endDate, String lastReceived) throws RemoteException {
        return mr_server.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    @Override
    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo,
            EventCode eventCode, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_server.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    @Override
    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    @Override
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID)
            throws RemoteException {
        return mr_server.getLatestMeterReadingsByMeterGroup(meterGroupID, null, null);
    }

    @Override
    public ErrorObject[] initiatePlannedOutage(String[] meterNos,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_server.initiatePlannedOutage(meterNos, startDate, endDate);
    }

    @Override
    public ErrorObject[] cancelPlannedOutage(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelPlannedOutage(meterNos);
    }

    @Override
    public ErrorObject[] initiateUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_server.initiateUsageMonitoring(meterNos);
    }

    @Override
    public ErrorObject[] cancelUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelUsageMonitoring(meterNos);
    }

    @Override
    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_server.initiateDisconnectedStatus(meterNos);
    }

    @Override
    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelDisconnectedStatus(meterNos);
    }

    @Override
    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos,
            String responseURL, String transactionID) throws RemoteException {
        return mr_server.initiateMeterReadByMeterNumber(meterNos, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup)
            throws RemoteException {
        return mr_server.establishMeterGroup(meterGroup);
    }

    @Override
    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws RemoteException {
        return mr_server.deleteMeterGroup(meterGroupID);
    }

    @Override
    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_server.insertMeterInMeterGroup(meterNumbers, meterGroupID);
    }

    @Override
    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_server.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
    }

    @Override
    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID) throws RemoteException {
        return mr_server.initiateGroupMeterRead(meterGroupName, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName,
            Calendar timeToRead, String responseURL, String transactionID)
            throws RemoteException {
        return mr_server.scheduleGroupMeterRead(meterGroupName, timeToRead, responseURL, transactionID);
    }

    @Override
    public ErrorObject[] updateServiceLocationDisplays(String servLocID)
            throws RemoteException {
        return mr_server.updateServiceLocationDisplays(servLocID);
    }

    @Override
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return mr_server.customerChangedNotification(changedCustomers);
    }

    @Override
    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return mr_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    @Override
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return mr_server.meterChangedNotification(changedMeters);
    }

    @Override
    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters)
            throws RemoteException {
        return mr_server.meterRemoveNotification(removedMeters);
    }

    @Override
    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters)
            throws RemoteException {
        return mr_server.meterRetireNotification(retiredMeters);
    }

    @Override
    public ErrorObject[] meterAddNotification(Meter[] addedMeters)
            throws RemoteException {
        return mr_server.meterAddNotification(addedMeters);
    }

    @Override
    public ErrorObject[] meterExchangeNotification(
            MeterExchange[] meterChangeout) throws RemoteException {
        return mr_server.meterExchangeNotification(meterChangeout);
    }

    @Override
    public ErrorObject[] inHomeDisplayAddNotification(InHomeDisplay[] addedIHDs)
            throws RemoteException {
        return mr_server.inHomeDisplayAddNotification(addedIHDs);
    }

    @Override
    public ErrorObject[] inHomeDisplayChangedNotification(
            InHomeDisplay[] changedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayChangedNotification(changedIHDs);
    }

    @Override
    public ErrorObject[] inHomeDisplayExchangeNotification(
            InHomeDisplayExchange[] IHDChangeout) throws RemoteException {
        return mr_server.inHomeDisplayExchangeNotification(IHDChangeout);
    }

    @Override
    public ErrorObject[] inHomeDisplayRemoveNotification(
            InHomeDisplay[] removedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRemoveNotification(removedIHDs);
    }

    @Override
    public ErrorObject[] inHomeDisplayRetireNotification(
            InHomeDisplay[] retiredIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRetireNotification(retiredIHDs);
    }
}