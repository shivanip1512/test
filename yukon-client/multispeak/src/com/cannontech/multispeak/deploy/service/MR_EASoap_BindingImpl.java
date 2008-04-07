/**
 * MR_EASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.deploy.service.impl.MR_EAImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_EASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_EASoap_PortType{
    private MR_EASoap_PortType mr_ea = (MR_EAImpl)YukonSpringHook.getBean("mr_ea");

    public Meter[] getAMRSupportedMeters(String lastReceived) throws RemoteException {
        return mr_ea.getAMRSupportedMeters(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        return mr_ea.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return mr_ea.getDomainNames();
    }

    public HistoryLog[] getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_ea.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public HistoryLog[] getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_ea.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate,
            String lastReceived) throws RemoteException {
        return mr_ea.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate,
            Calendar endDate) throws RemoteException {
        return mr_ea.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException {
        return mr_ea.getLatestReadingByMeterNo(meterNo);
    }

    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType) throws RemoteException {
        return mr_ea.getLatestReadingByMeterNoAndType(meterNo, readingType);
    }

    public FormattedBlock[] getLatestReadingByType(String readingType, String lastReceived) throws RemoteException {
        return mr_ea.getLatestReadingByType(readingType, lastReceived);
    }

    public MeterRead[] getLatestReadings(String lastReceived) throws RemoteException {
        return mr_ea.getLatestReadings(lastReceived);
    }

    public String[] getMethods() throws RemoteException {
        return mr_ea.getMethods();
    }

    public Meter[] getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return mr_ea.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_ea.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType,
            String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByDateAndType(startDate, endDate, readingType, lastReceived);
    }

    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_ea.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByMeterNoAndType(meterNo, startDate, endDate, readingType, lastReceived);
    }

    public MeterRead[] getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_ea.getReadingsByUOMAndDate(uomData, startDate, endDate, lastReceived);
    }

    public String[] getSupportedReadingTypes() throws RemoteException {
        return mr_ea.getSupportedReadingTypes();
    }

    public ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, String readingType,
            String transactionID) throws RemoteException {
        return mr_ea.initiateMeterReadByMeterNoAndType(meterNo, responseURL, readingType, transactionID);
    }

    public ErrorObject[] initiateMeterReadByObject(String objectName, String nounType, PhaseCd phaseCode,
            String responseURL, String transactionID) throws RemoteException {
        return mr_ea.initiateMeterReadByObject(objectName, nounType, phaseCode, responseURL, transactionID);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return mr_ea.pingURL();
    }

}
