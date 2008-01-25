/**
 * MR_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.deploy.service.impl.MR_CBImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_CBSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType{
    private MR_CBSoap_PortType mr_cb = (MR_CBImpl)YukonSpringHook.getBean("mr_cb");

    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_cb.cancelDisconnectedStatus(meterNos);
    }

    public ErrorObject[] cancelPlannedOutage(String[] meterNos)
            throws RemoteException {
        return mr_cb.cancelPlannedOutage(meterNos);
    }

    public ErrorObject[] cancelUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_cb.cancelUsageMonitoring(meterNos);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return mr_cb.customerChangedNotification(changedCustomers);
    }

    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws RemoteException {
        return mr_cb.deleteMeterGroup(meterGroupID);
    }

    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup)
            throws RemoteException {
        return mr_cb.establishMeterGroup(meterGroup);
    }

    public Meter[] getAMRSupportedMeters(String lastReceived)
            throws RemoteException {
        return mr_cb.getAMRSupportedMeters(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return mr_cb.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return mr_cb.getDomainNames();
    }

    public HistoryLog[] getHistoryLogByMeterNo(String meterNo,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cb.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public HistoryLog[] getHistoryLogsByDate(Calendar startDate,
            Calendar endDate, String lastReceived) throws RemoteException {
        return mr_cb.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_cb.getHistoryLogsByDateAndEventCode(eventCode,
                                                      startDate,
                                                      endDate,
                                                      lastReceived);
    }

    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo,
            EventCode eventCode, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_cb.getHistoryLogsByMeterNoAndEventCode(meterNo,
                                                         eventCode,
                                                         startDate,
                                                         endDate);
    }

    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID)
            throws RemoteException {
        return mr_cb.getLatestMeterReadingsByMeterGroup(meterGroupID);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo)
            throws RemoteException {
        return mr_cb.getLatestReadingByMeterNo(meterNo);
    }

    public String[] getMethods() throws RemoteException {
        return mr_cb.getMethods();
    }

    public Meter[] getModifiedAMRMeters(String previousSessionID,
            String lastReceived) throws RemoteException {
        return mr_cb.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle,
            Calendar billingDate, int whLookBack, int lookBack,
            int lookForward, String lastReceived) throws RemoteException {
        return mr_cb.getReadingsByBillingCycle(billingCycle,
                                               billingDate,
                                               whLookBack,
                                               lookBack,
                                               lookForward,
                                               lastReceived);
    }

    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate,
            String lastReceived) throws RemoteException {
        return mr_cb.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate,
            Calendar endDate) throws RemoteException {
        return mr_cb.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_cb.initiateDisconnectedStatus(meterNos);
    }

    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID) throws RemoteException {
        return mr_cb.initiateGroupMeterRead(meterGroupName,
                                            responseURL,
                                            transactionID);
    }

    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos,
            String responseURL, String transactionID) throws RemoteException {
        return mr_cb.initiateMeterReadByMeterNumber(meterNos,
                                                    responseURL,
                                                    transactionID);
    }

    public ErrorObject[] initiatePlannedOutage(String[] meterNos,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_cb.initiatePlannedOutage(meterNos, startDate, endDate);
    }

    public ErrorObject[] initiateUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_cb.initiateUsageMonitoring(meterNos);
    }

    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_cb.insertMeterInMeterGroup(meterNumbers, meterGroupID);
    }

    public boolean isAMRMeter(String meterNo) throws RemoteException {
        return mr_cb.isAMRMeter(meterNo);
    }

    public ErrorObject[] meterAddNotification(Meter[] addedMeters)
            throws RemoteException {
        return mr_cb.meterAddNotification(addedMeters);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return mr_cb.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] meterExchangeNotification(
            MeterExchange[] meterChangeout) throws RemoteException {
        return mr_cb.meterExchangeNotification(meterChangeout);
    }

    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters)
            throws RemoteException {
        return mr_cb.meterRemoveNotification(removedMeters);
    }

    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters)
            throws RemoteException {
        return mr_cb.meterRetireNotification(retiredMeters);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return mr_cb.pingURL();
    }

    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_cb.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
    }

    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName,
            Calendar timeToRead, String responseURL, String transactionID)
            throws RemoteException {
        return mr_cb.scheduleGroupMeterRead(meterGroupName,
                                            timeToRead,
                                            responseURL,
                                            transactionID);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return mr_cb.serviceLocationChangedNotification(changedServiceLocations);
    }
}
