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

public class MR_EASoap_BindingImpl implements MR_EASoap_PortType{
    
    private MR_EASoap_PortType mrEa = null;

    /**
     * @param mrEa The mrEa to set.
     */
    public void setMrEa(MR_EASoap_PortType mrEa)
    {
        this.mrEa = mrEa;
    }

    /**
     * @return Returns the mrEa.
     */
    private MR_EASoap_PortType getMrEa()
    {
        if (mrEa == null)
            return (MR_EAImpl)YukonSpringHook.getBean("mrEa");
        return mrEa;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getAMRSupportedMeters(java.lang.String)
     */
    public ArrayOfMeter getAMRSupportedMeters(String lastReceived) throws RemoteException
    {
        return getMrEa().getAMRSupportedMeters(lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getDomainMembers(java.lang.String)
     */
    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException
    {
        return getMrEa().getDomainMembers(domainName);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getDomainNames()
     */
    public ArrayOfString getDomainNames() throws RemoteException
    {
        return getMrEa().getDomainNames();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getHistoryLogByMeterNo(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrEa().getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getHistoryLogsByDate(java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrEa().getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.service.EventCode, java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrEa().getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getHistoryLogsByMeterNoAndEventCode(java.lang.String, com.cannontech.multispeak.service.EventCode, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrEa().getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getLatestReadingByMeterNo(java.lang.String)
     */
    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException
    {
        return getMrEa().getLatestReadingByMeterNo(meterNo);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getLatestReadings(java.lang.String)
     */
    public ArrayOfMeterRead getLatestReadings(String lastReceived) throws RemoteException
    {
        return getMrEa().getLatestReadings(lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getMethods()
     */
    public ArrayOfString getMethods() throws RemoteException
    {
        return getMrEa().getMethods();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getModifiedAMRMeters(java.lang.String, java.lang.String)
     */
    public ArrayOfMeter getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException
    {
        return getMrEa().getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getReadingsByDate(java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfMeterRead getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrEa().getReadingsByDate(startDate, endDate, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getReadingsByMeterNo(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfMeterRead getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrEa().getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#getReadingsByUOMAndDate(java.lang.String, java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfMeterRead getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrEa().getReadingsByUOMAndDate(uomData, startDate, endDate, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_EAImpl#pingURL()
     */
    public ArrayOfErrorObject pingURL() throws RemoteException
    {
        return getMrEa().pingURL();
    }

}
