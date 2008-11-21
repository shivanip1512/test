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

    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos) throws RemoteException {
        return mr_server.cancelDisconnectedStatus(meterNos);
    }

    public ErrorObject[] cancelPlannedOutage(String[] meterNos) throws RemoteException {
        return mr_server.cancelPlannedOutage(meterNos);
    }

    public ErrorObject[] cancelUsageMonitoring(String[] meterNos) throws RemoteException {
        return mr_server.cancelUsageMonitoring(meterNos);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws RemoteException {
        return mr_server.customerChangedNotification(changedCustomers);
    }

    public ErrorObject deleteMeterGroup(String meterGroupID) throws RemoteException {
        return mr_server.deleteMeterGroup(meterGroupID);
    }

    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup) throws RemoteException {
        return mr_server.establishMeterGroup(meterGroup);
    }

    public Meter[] getAMRSupportedMeters(String lastReceived) throws RemoteException {
        return mr_server.getAMRSupportedMeters(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        return mr_server.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return mr_server.getDomainNames();
    }

    public HistoryLog[] getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_server.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public HistoryLog[] getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate,
            String lastReceived) throws RemoteException {
        return mr_server.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate,
            Calendar endDate) throws RemoteException {
        return mr_server.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID) throws RemoteException {
        return mr_server.getLatestMeterReadingsByMeterGroup(meterGroupID);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException {
        return mr_server.getLatestReadingByMeterNo(meterNo);
    }

    public String[] getMethods() throws RemoteException {
        return mr_server.getMethods();
    }

    public Meter[] getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return mr_server.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(String meterNumber, Calendar billingDate,
            int whLookBack, int lookBack, int lookForward, String lastReceived) throws RemoteException {
        return mr_server.getReadingByMeterNumberFormattedBlock(meterNumber,
                                                           billingDate,
                                                           whLookBack,
                                                           lookBack,
                                                           lookForward,
                                                           lastReceived);
    }

    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle, Calendar billingDate, int whLookBack,
            int lookBack, int lookForward, String lastReceived) throws RemoteException {
        return mr_server.getReadingsByBillingCycle(billingCycle,
                                               billingDate,
                                               whLookBack,
                                               lookBack,
                                               lookForward,
                                               lastReceived);
    }

    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public FormattedBlock[] getReadingsByDateFormattedBlock(Calendar billingDate, int whLookBack, int lookBack,
            int lookForward, String lastReceived) throws RemoteException {
        return mr_server.getReadingsByDateFormattedBlock(billingDate, whLookBack, lookBack, lookForward, lastReceived);
    }

    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_server.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ErrorObject[] inHomeDisplayAddNotification(InHomeDisplay[] addedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayAddNotification(addedIHDs);
    }

    public ErrorObject[] inHomeDisplayChangedNotification(InHomeDisplay[] changedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayChangedNotification(changedIHDs);
    }

    public ErrorObject[] inHomeDisplayExchangeNotification(InHomeDisplayExchange[] IHDChangeout) throws RemoteException {
        return mr_server.inHomeDisplayExchangeNotification(IHDChangeout);
    }

    public ErrorObject[] inHomeDisplayRemoveNotification(InHomeDisplay[] removedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRemoveNotification(removedIHDs);
    }

    public ErrorObject[] inHomeDisplayRetireNotification(InHomeDisplay[] retiredIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRetireNotification(retiredIHDs);
    }

    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos) throws RemoteException {
        return mr_server.initiateDisconnectedStatus(meterNos);
    }

    public ErrorObject[] initiateGroupMeterRead(String meterGroupName, String responseURL, String transactionID)
            throws RemoteException {
        return mr_server.initiateGroupMeterRead(meterGroupName, responseURL, transactionID);
    }

    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos, String responseURL, String transactionID)
            throws RemoteException {
        return mr_server.initiateMeterReadByMeterNumber(meterNos, responseURL, transactionID);
    }

    public ErrorObject[] initiatePlannedOutage(String[] meterNos, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_server.initiatePlannedOutage(meterNos, startDate, endDate);
    }

    public ErrorObject[] initiateUsageMonitoring(String[] meterNos) throws RemoteException {
        return mr_server.initiateUsageMonitoring(meterNos);
    }

    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers, String meterGroupID) throws RemoteException {
        return mr_server.insertMeterInMeterGroup(meterNumbers, meterGroupID);
    }

    public boolean isAMRMeter(String meterNo) throws RemoteException {
        return mr_server.isAMRMeter(meterNo);
    }

    public ErrorObject[] meterAddNotification(Meter[] addedMeters) throws RemoteException {
        return mr_server.meterAddNotification(addedMeters);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws RemoteException {
        return mr_server.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] meterExchangeNotification(MeterExchange[] meterChangeout) throws RemoteException {
        return mr_server.meterExchangeNotification(meterChangeout);
    }

    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters) throws RemoteException {
        return mr_server.meterRemoveNotification(removedMeters);
    }

    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters) throws RemoteException {
        return mr_server.meterRetireNotification(retiredMeters);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return mr_server.pingURL();
    }

    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers, String meterGroupID) throws RemoteException {
        return mr_server.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
    }

    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL,
            String transactionID) throws RemoteException {
        return mr_server.scheduleGroupMeterRead(meterGroupName, timeToRead, responseURL, transactionID);
    }

    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
            throws RemoteException {
        return mr_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    public ErrorObject[] updateServiceLocationDisplays(String servLocID)
            throws RemoteException {
        return mr_server.updateServiceLocationDisplays(servLocID);
    }
}
