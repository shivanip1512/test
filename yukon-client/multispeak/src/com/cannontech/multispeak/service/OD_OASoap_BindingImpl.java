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

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.service.OD_OASoap_PortType{
    private OD_OASoap_PortType od_oa = (OD_OAImpl)YukonSpringHook.getBean("od_oa");

    public ArrayOfErrorObject cancelODMonitoringRequestByObject(ArrayOfObjectRef objectRef, Calendar requestDate) throws RemoteException {
        return od_oa.cancelODMonitoringRequestByObject(objectRef, requestDate);
    }

    public ArrayOfObjectRef displayODMonitoringRequests() throws RemoteException {
        return od_oa.displayODMonitoringRequests();
    }

    public ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(String lastReceived) throws RemoteException {
        return od_oa.getAllOutageDetectionDevices(lastReceived);
    }

    public ArrayOfDomainMember getDomainMembers(String domainName) throws RemoteException {
        return od_oa.getDomainMembers(domainName);
    }

    public ArrayOfString getDomainNames() throws RemoteException {
        return od_oa.getDomainNames();
    }

    public ArrayOfString getMethods() throws RemoteException {
        return od_oa.getMethods();
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(String meterNo) throws RemoteException {
        return od_oa.getOutageDetectionDevicesByMeterNo(meterNo);
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus oDDStatus, String lastReceived) throws RemoteException {
        return od_oa.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
    }

    public ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(OutageDetectDeviceType oDDType, String lastReceived) throws RemoteException {
        return od_oa.getOutageDetectionDevicesByType(oDDType, lastReceived);
    }

    public ArrayOfOutageDetectionDevice getOutagedODDevices() throws RemoteException {
        return od_oa.getOutagedODDevices();
    }

    public ArrayOfErrorObject initiateODEventRequestByObject(String objectName, String nounType, PhaseCd phaseCode, Calendar requestDate, String responseURL) throws RemoteException {
        return od_oa.initiateODEventRequestByObject(objectName, nounType, phaseCode, requestDate, responseURL);
    }

    public ArrayOfErrorObject initiateODMonitoringRequestByObject(String objectName, String nounType, PhaseCd phaseCode, int periodicity, Calendar requestDate, String responseURL) throws RemoteException {
        return od_oa.initiateODMonitoringRequestByObject(objectName, nounType, phaseCode, periodicity, requestDate, responseURL);
    }

    public ArrayOfErrorObject initiateOutageDetectionEventRequest(ArrayOfString meterNos, Calendar requestDate, String responseURL) throws RemoteException {
        return od_oa.initiateOutageDetectionEventRequest(meterNos, requestDate, responseURL);
    }

    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice oDDevice) throws RemoteException {
        od_oa.modifyODDataForOutageDetectionDevice(oDDevice);
    }

    public ArrayOfErrorObject pingURL() throws RemoteException {
        return od_oa.pingURL();
    }    
}
