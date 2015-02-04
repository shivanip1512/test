/**
 * MR_EASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.deploy.service.impl.MR_ServerImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_EASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_EASoap_PortType{
    private MR_ServerSoap_PortType mr_server = YukonSpringHook.getBean("mr_server", MR_ServerImpl.class);

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
    public MeterRead[] getLatestReadings(String lastReceived)
            throws RemoteException {
        return mr_server.getLatestReadings(lastReceived);
    }

    @Override
    public MeterRead getLatestReadingByMeterNo(String meterNo)
            throws RemoteException {
        return mr_server.getLatestReadingByMeterNo(meterNo);
    }

    @Override
    public MeterRead[] getReadingsByUOMAndDate(String uomData,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getReadingsByUOMAndDate(uomData, startDate, endDate, lastReceived);
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
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo,
            String readingType) throws RemoteException {
        return mr_server.getLatestReadingByMeterNoAndType(meterNo, readingType, null, null);
    }

    @Override
    public FormattedBlock[] getLatestReadingByType(String readingType,
            String lastReceived) throws RemoteException {
        return mr_server.getLatestReadingByType(readingType, lastReceived, null, null);
    }

    @Override
    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate,
            Calendar endDate, String readingType, String lastReceived)
            throws RemoteException {
        return mr_server.getReadingsByDateAndType(startDate, endDate, readingType, lastReceived, null, null);
    }

    @Override
    public String[] getSupportedReadingTypes() throws RemoteException {
        return mr_server.getSupportedReadingTypes();
    }

    @Override
    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived) throws RemoteException {
        return mr_server.getReadingsByMeterNoAndType(meterNo, startDate, endDate, readingType, lastReceived, null, null);
    }

    @Override
    public ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNo,
            String responseURL, String readingType, String transactionID)
            throws RemoteException {
        return mr_server.initiateMeterReadByMeterNoAndType(meterNo, responseURL, readingType, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] initiateMeterReadByObject(String objectName,
            String nounType, PhaseCd phaseCode, String responseURL,
            String transactionID) throws RemoteException {
        return mr_server.initiateMeterReadByObject(objectName, nounType, phaseCode, responseURL, transactionID, Float.MIN_NORMAL);
    }
}