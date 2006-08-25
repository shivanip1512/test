/**
 * CD_CBSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.service.impl.CD_CBImpl;
import com.cannontech.spring.YukonSpringHook;

public class CD_CBSoap_BindingImpl implements CD_CBSoap_PortType{
    private CD_CBSoap_PortType cdCb = null;

    /**
     * @param cdCb The cdCb to set.
     */
    public void setCdCb(CD_CBSoap_PortType cdCb)
    {
        this.cdCb = cdCb;
    }
    
    /**
     * @return Returns the cdCb.
     */
    private CD_CBSoap_PortType getCdCb()
    {
        if (cdCb == null)
            return (CD_CBImpl)YukonSpringHook.getBean("cdCb");
        return cdCb;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#customerChangedNotification(com.cannontech.multispeak.service.ArrayOfCustomer)
     */
    public ArrayOfErrorObject customerChangedNotification(ArrayOfCustomer changedCustomers) throws RemoteException
    {
        return getCdCb().customerChangedNotification(changedCustomers);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getCDMeterState(java.lang.String)
     */
    public LoadActionCode getCDMeterState(String meterNo) throws RemoteException
    {
        return getCdCb().getCDMeterState(meterNo);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getCDSupportedMeters(java.lang.String)
     */
    public ArrayOfMeter getCDSupportedMeters(String lastReceived) throws RemoteException
    {
        return getCdCb().getCDSupportedMeters(lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getDomainMembers(java.lang.String)
     */
    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException
    {
        return getCdCb().getDomainMembers(domainName);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getDomainNames()
     */
    public ArrayOfString getDomainNames() throws RemoteException
    {
        return getCdCb().getDomainNames();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getMethods()
     */
    public ArrayOfString getMethods() throws RemoteException
    {
        return getCdCb().getMethods();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#getModifiedCDMeters(java.lang.String, java.lang.String)
     */
    public ArrayOfMeter getModifiedCDMeters(String previousSessionID, String lastReceived) throws RemoteException
    {
        return getCdCb().getModifiedCDMeters(previousSessionID, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#initiateConnectDisconnect(com.cannontech.multispeak.service.ArrayOfConnectDisconnectEvent)
     */
    public ArrayOfErrorObject initiateConnectDisconnect(ArrayOfConnectDisconnectEvent cdEvents) throws RemoteException
    {
        return getCdCb().initiateConnectDisconnect(cdEvents);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#meterChangedNotification(com.cannontech.multispeak.service.ArrayOfMeter)
     */
    public ArrayOfErrorObject meterChangedNotification(ArrayOfMeter changedMeters) throws RemoteException
    {
        return getCdCb().meterChangedNotification(changedMeters);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#pingURL()
     */
    public ArrayOfErrorObject pingURL() throws RemoteException
    {
        return getCdCb().pingURL();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.CD_CBImpl#serviceLocationChangedNotification(com.cannontech.multispeak.service.ArrayOfServiceLocation)
     */
    public ArrayOfErrorObject serviceLocationChangedNotification(ArrayOfServiceLocation changedServiceLocations) throws RemoteException
    {
        return getCdCb().serviceLocationChangedNotification(changedServiceLocations);
    }

}
