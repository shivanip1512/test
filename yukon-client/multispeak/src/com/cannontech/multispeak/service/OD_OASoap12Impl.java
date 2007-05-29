/**
 * OD_OASoap12Impl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public class OD_OASoap12Impl implements com.cannontech.multispeak.service.OD_OASoap_PortType{
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByStatus(com.cannontech.multispeak.service.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutageDetectionDevicesByType(com.cannontech.multispeak.service.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfOutageDetectionDevice getOutagedODDevices() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateOutageDetectionEventRequest(com.cannontech.multispeak.service.ArrayOfString meterNos, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODEventRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject initiateODMonitoringRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.service.PhaseCd phaseCode, int periodicity, java.util.Calendar requestDate, java.lang.String responseURL) throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfObjectRef displayODMonitoringRequests() throws java.rmi.RemoteException {
        return null;
    }

    public com.cannontech.multispeak.service.ArrayOfErrorObject cancelODMonitoringRequestByObject(com.cannontech.multispeak.service.ArrayOfObjectRef objectRef, java.util.Calendar requestDate) throws java.rmi.RemoteException {
        return null;
    }

    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.service.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException {
    }

}
