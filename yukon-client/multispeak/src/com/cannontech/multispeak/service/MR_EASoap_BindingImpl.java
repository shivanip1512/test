/**
 * MR_EASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.service.impl.MR_EAImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_EASoap_BindingImpl implements com.cannontech.multispeak.service.MR_EASoap_PortType{
    private MR_EASoap_PortType mr_ea = (MR_EAImpl)YukonSpringHook.getBean("mr_ea");

    public ArrayOfMeter getAMRSupportedMeters(String lastReceived) throws RemoteException {
        return mr_ea.getAMRSupportedMeters(lastReceived);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return mr_ea.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return mr_ea.getDomainNames();
    }

    public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_ea.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_ea.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_ea.getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_ea.getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException {
        return mr_ea.getLatestReadingByMeterNo(meterNo);
    }

    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType) throws RemoteException {
        return mr_ea.getLatestReadingByMeterNoAndType(meterNo, readingType);
    }

    public ArrayOfFormattedBlock getLatestReadingByType(String readingType, String lastReceived) throws RemoteException {
        return mr_ea.getLatestReadingByType(readingType, lastReceived);
    }

    public ArrayOfMeterRead getLatestReadings(String lastReceived) throws RemoteException {
        return mr_ea.getLatestReadings(lastReceived);
    }

    public ArrayOfString getMethods() throws RemoteException {
        return mr_ea.getMethods();
    }

    public ArrayOfMeter getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException {
        return mr_ea.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public ArrayOfMeterRead getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public ArrayOfFormattedBlock getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByDateAndType(startDate, endDate, readingType, lastReceived);
    }

    public ArrayOfMeterRead getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_ea.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public ArrayOfFormattedBlock getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate, String readingType, String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByMeterNoAndType(meterNo, startDate, endDate, readingType, lastReceived);
    }

    public ArrayOfMeterRead getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException {
        return mr_ea.getReadingsByUOMAndDate(uomData, startDate, endDate, lastReceived);
    }

    public ArrayOfString getSupportedReadingTypes() throws RemoteException {
        return mr_ea.getSupportedReadingTypes();
    }

    public ArrayOfErrorObject initiateMeterReadByMeterNoAndType(ArrayOfString meterNos, String responseURL, String readingType) throws RemoteException {
        return mr_ea.initiateMeterReadByMeterNoAndType(meterNos, responseURL, readingType);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return mr_ea.pingURL();
    }
    
}
