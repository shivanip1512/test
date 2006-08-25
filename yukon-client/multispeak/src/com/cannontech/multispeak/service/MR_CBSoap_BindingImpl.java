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

public class MR_CBSoap_BindingImpl implements MR_CBSoap_PortType{

    public MR_CBSoap_PortType mrCb;
    
    /**
     * @param mrCb The mrCb to set.
     */
    public void setMrCb(MR_CBSoap_PortType mrCb)
    {
        this.mrCb = mrCb;
    }

    /**
     * @return Returns the mrCb.
     */
    private MR_CBSoap_PortType getMrCb()
    {
        if (mrCb == null)
            return (MR_CBImpl)YukonSpringHook.getBean("mrCb");
        return mrCb;
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#cancelDisconnectedStatus(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject cancelDisconnectedStatus(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().cancelDisconnectedStatus(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#cancelPlannedOutage(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject cancelPlannedOutage(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().cancelPlannedOutage(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#cancelUsageMonitoring(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject cancelUsageMonitoring(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().cancelUsageMonitoring(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer)
     */
    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws RemoteException
    {
        return getMrCb().customerChangedNotification(changedCustomers);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getAMRSupportedMeters(java.lang.String)
     */
    public ArrayOfMeter getAMRSupportedMeters(String lastReceived) throws RemoteException
    {
        return getMrCb().getAMRSupportedMeters(lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getDomainMembers(java.lang.String)
     */
    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException
    {
        return getMrCb().getDomainMembers(domainName);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getDomainNames()
     */
    public ArrayOfString getDomainNames() throws RemoteException
    {
        return getMrCb().getDomainNames();
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getHistoryLogByMeterNo(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfHistoryLog getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrCb().getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getHistoryLogsByDate(java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfHistoryLog getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrCb().getHistoryLogsByDate(startDate, endDate, lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.EventCode, java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrCb().getHistoryLogsByDateAndEventCode(eventCode, startDate, endDate, lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getHistoryLogsByMeterNoAndEventCode(java.lang.String, com.cannontech.multispeak.EventCode, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrCb().getHistoryLogsByMeterNoAndEventCode(meterNo, eventCode, startDate, endDate);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getLatestReadingByMeterNo(java.lang.String)
     */
    public MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException
    {
        return getMrCb().getLatestReadingByMeterNo(meterNo);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getMethods()
     */
    public ArrayOfString getMethods() throws RemoteException
    {
        return getMrCb().getMethods();
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getModifiedAMRMeters(java.lang.String, java.lang.String)
     */
    public ArrayOfMeter getModifiedAMRMeters(String previousSessionID, String lastReceived) throws RemoteException
    {
        return getMrCb().getModifiedAMRMeters(previousSessionID, lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getReadingsByBillingCycle(java.lang.String, java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfMeterRead getReadingsByBillingCycle(String billingCycle, Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrCb().getReadingsByBillingCycle(billingCycle, startDate, endDate, lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getReadingsByDate(java.util.Calendar, java.util.Calendar, java.lang.String)
     */
    public ArrayOfMeterRead getReadingsByDate(Calendar startDate, Calendar endDate, String lastReceived) throws RemoteException
    {
        return getMrCb().getReadingsByDate(startDate, endDate, lastReceived);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#getReadingsByMeterNo(java.lang.String, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfMeterRead getReadingsByMeterNo(String meterNo, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrCb().getReadingsByMeterNo(meterNo, startDate, endDate);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#initiateDisconnectedStatus(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject initiateDisconnectedStatus(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().initiateDisconnectedStatus(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#initiateMeterReadByMeterNumber(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject initiateMeterReadByMeterNumber(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().initiateMeterReadByMeterNumber(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#initiatePlannedOutage(com.cannontech.multispeak.ArrayOfString, java.util.Calendar, java.util.Calendar)
     */
    public ArrayOfErrorObject initiatePlannedOutage(ArrayOfString meterNos, Calendar startDate, Calendar endDate) throws RemoteException
    {
        return getMrCb().initiatePlannedOutage(meterNos, startDate, endDate);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#initiateUsageMonitoring(com.cannontech.multispeak.ArrayOfString)
     */
    public ArrayOfErrorObject initiateUsageMonitoring(ArrayOfString meterNos) throws RemoteException
    {
        return getMrCb().initiateUsageMonitoring(meterNos);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#isAMRMeter(java.lang.String)
     */
    public boolean isAMRMeter(String meterNo) throws RemoteException
    {
        return getMrCb().isAMRMeter(meterNo);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#meterAddNotification(com.cannontech.multispeak.ArrayOfMeter)
     */
    public ArrayOfErrorObject meterAddNotification(ArrayOfMeter addedMeters) throws RemoteException
    {
        return getMrCb().meterAddNotification(addedMeters);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter)
     */
    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws RemoteException
    {
        return getMrCb().meterChangedNotification(changedMeters);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#meterRemoveNotification(com.cannontech.multispeak.ArrayOfMeter)
     */
    public ArrayOfErrorObject meterRemoveNotification(ArrayOfMeter removedMeters) throws RemoteException
    {
        return getMrCb().meterRemoveNotification(removedMeters);
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#pingURL()
     */
    public ArrayOfErrorObject pingURL() throws RemoteException
    {
        return getMrCb().pingURL();
    }



    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_CBSoap_BindingImpl#serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation)
     */
    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws RemoteException
    {
        return getMrCb().serviceLocationChangedNotification(changedServiceLocations);
    }
}
