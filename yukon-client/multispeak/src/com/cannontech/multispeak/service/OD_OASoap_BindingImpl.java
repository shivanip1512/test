/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.service.impl.OD_OAImpl;
import com.cannontech.spring.YukonSpringHook;

public class OD_OASoap_BindingImpl implements OD_OASoap_PortType{

    private OD_OASoap_PortType odOa = null;

    /**
     * @param odOa The odOa to set.
     */
    public void setOdOa(OD_OASoap_PortType odOa)
    {
        this.odOa = odOa;
    }

    /**
     * @return Returns the cdCb.
     */
    private OD_OASoap_PortType getOdOa()
    {
        if (odOa == null)
            return (OD_OAImpl)YukonSpringHook.getBean("odOa");
        return odOa;
    }
    
    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#cancelODMonitoringRequestByObject(com.cannontech.multispeak.service.ArrayOfObjectRef, java.util.Calendar)
     */
    public ArrayOfErrorObject cancelODMonitoringRequestByObject(ArrayOfObjectRef objectRef, Calendar requestDate) throws RemoteException
    {
        return getOdOa().cancelODMonitoringRequestByObject(objectRef, requestDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#displayODMonitoringRequests()
     */
    public ArrayOfObjectRef displayODMonitoringRequests() throws RemoteException
    {
        return getOdOa().displayODMonitoringRequests();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getAllOutageDetectionDevices(java.lang.String)
     */
    public ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(String lastReceived) throws RemoteException
    {
        return getOdOa().getAllOutageDetectionDevices(lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getDomainMembers(java.lang.String)
     */
    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException
    {
        return getOdOa().getDomainMembers(domainName);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getDomainNames()
     */
    public ArrayOfString getDomainNames() throws RemoteException
    {
        return getOdOa().getDomainNames();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getMethods()
     */
    public ArrayOfString getMethods() throws RemoteException
    {
        return getOdOa().getMethods();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getOutageDetectionDevicesByMeterNo(java.lang.String)
     */
    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(String meterNo) throws RemoteException
    {
        return getOdOa().getOutageDetectionDevicesByMeterNo(meterNo);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getOutageDetectionDevicesByStatus(com.cannontech.multispeak.service.OutageDetectDeviceStatus, java.lang.String)
     */
    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus, String lastReceived) throws RemoteException
    {
        return getOdOa().getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getOutageDetectionDevicesByType(com.cannontech.multispeak.service.OutageDetectDeviceType, java.lang.String)
     */
    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType, String lastReceived) throws RemoteException
    {
        return getOdOa().getOutageDetectionDevicesByType(oDDType, lastReceived);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#getOutagedODDevices()
     */
    public ArrayOfOutageDetectionDevice getOutagedODDevices() throws RemoteException
    {
        return getOdOa().getOutagedODDevices();
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#initiateODEventRequestByObject(com.cannontech.multispeak.service.ObjectRef, com.cannontech.multispeak.service.PhaseCd, java.util.Calendar)
     */
    public ArrayOfErrorObject initiateODEventRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, Calendar requestDate) throws RemoteException
    {
        return getOdOa().initiateODEventRequestByObject(objectRef, phaseCode, requestDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#initiateODMonitoringRequestByObject(com.cannontech.multispeak.service.ObjectRef, com.cannontech.multispeak.service.PhaseCd, int, java.util.Calendar)
     */
    public ArrayOfErrorObject initiateODMonitoringRequestByObject(ObjectRef objectRef, PhaseCd phaseCode, int periodicity, Calendar requestDate) throws RemoteException
    {
        return getOdOa().initiateODMonitoringRequestByObject(objectRef, phaseCode, periodicity, requestDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#initiateOutageDetectionEventRequest(com.cannontech.multispeak.service.ArrayOfString, java.util.Calendar)
     */
    public ArrayOfErrorObject initiateOutageDetectionEventRequest(ArrayOfString meterNos, Calendar requestDate) throws RemoteException
    {
        return getOdOa().initiateOutageDetectionEventRequest(meterNos, requestDate);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.service.OutageDetectionDevice)
     */
    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice) throws RemoteException
    {
        getOdOa().modifyODDataForOutageDetectionDevice(oDDevice);
    }

    /* (non-Javadoc)
     * @see com.cannontech.multispeak.service.impl.OD_OAImpl#pingURL()
     */
    public ArrayOfErrorObject pingURL() throws RemoteException
    {
        return getOdOa().pingURL();
    }
}
