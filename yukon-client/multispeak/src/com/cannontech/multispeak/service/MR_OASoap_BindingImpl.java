/**
 * MR_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.multispeak.service.impl.MR_OAImpl;
import com.cannontech.spring.YukonSpringHook;

public class MR_OASoap_BindingImpl implements MR_OASoap_PortType{
    
    private MR_OASoap_PortType mrOa = null;

    /**
     * @param mrOa The mrOa to set.
     */
    public void setMrOa(MR_OASoap_PortType mrOa)
    {
        this.mrOa = mrOa;
    }

    /**
     * @return Returns the mrOa.
     */
    private MR_OASoap_PortType getMrOa()
    {
        if (mrOa == null)
            return (MR_OAImpl)YukonSpringHook.getBean("mrOa");
        return mrOa;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#customersAffectedByOutageNotification(com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage)
     */
    public ArrayOfErrorObject customersAffectedByOutageNotification(ArrayOfCustomersAffectedByOutage newOutages) throws RemoteException
    {
        return getMrOa().customersAffectedByOutageNotification(newOutages);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#getDomainMembers(java.lang.String)
     */
    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException
    {
        return getMrOa().getDomainMembers(domainName);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#getDomainNames()
     */
    public ArrayOfString getDomainNames() throws RemoteException
    {
        return getMrOa().getDomainNames();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#getMethods()
     */
    public ArrayOfString getMethods() throws RemoteException
    {
        return getMrOa().getMethods();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#meterConnectivityNotification(com.cannontech.multispeak.service.ArrayOfMeterConnectivity)
     */
    public ArrayOfErrorObject meterConnectivityNotification(ArrayOfMeterConnectivity newConnectivity) throws RemoteException
    {
        return getMrOa().meterConnectivityNotification(newConnectivity);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.MR_OAImpl#pingURL()
     */
    public ArrayOfErrorObject pingURL() throws RemoteException
    {
        return getMrOa().pingURL();
    }
    
}
