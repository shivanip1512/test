/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.spring.YukonSpringHook;

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.OD_OASoap_PortType{
	private OD_ServerSoap_PortType od_server = YukonSpringHook.getBean("od_server", OD_ServerSoap_PortType.class);

    @Override
    public ErrorObject[] pingURL() throws RemoteException {
        return od_server.pingURL();
    }

    @Override
    public String[] getMethods() throws RemoteException {
        return od_server.getMethods();
    }

    @Override
    public String[] getDomainNames() throws RemoteException {
        return od_server.getDomainNames();
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return od_server.getDomainMembers(domainName);
    }

    @Override
    public OutageDetectionDevice[] getAllOutageDetectionDevices(
            String lastReceived) throws RemoteException {
        return od_server.getAllOutageDetectionDevices(lastReceived);
    }

    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(
            String meterNo) throws RemoteException {
        return od_server.getOutageDetectionDevicesByMeterNo(meterNo);
    }

    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByStatus(
            OutageDetectDeviceStatus oDDStatus, String lastReceived)
            throws RemoteException {
        return od_server.getOutageDetectionDevicesByStatus(oDDStatus, lastReceived);
    }

    @Override
    public OutageDetectionDevice[] getOutageDetectionDevicesByType(
            OutageDetectDeviceType oDDType, String lastReceived)
            throws RemoteException {
        return od_server.getOutageDetectionDevicesByType(oDDType, lastReceived);
    }

    @Override
    public OutageDetectionDevice[] getOutagedODDevices() throws RemoteException {
        return od_server.getOutagedODDevices();
    }

    @Override
    public ErrorObject[] initiateOutageDetectionEventRequest(String[] meterNos,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        return od_server.initiateOutageDetectionEventRequest(meterNos, requestDate, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] initiateODEventRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, Calendar requestDate,
            String responseURL, String transactionID) throws RemoteException {
        return od_server.initiateODEventRequestByObject(objectName, nounType, phaseCode, requestDate, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] initiateODEventRequestByServiceLocation(
            String[] servLoc, Calendar requestDate, String responseURL,
            String transactionID) throws RemoteException {
        return od_server.initiateODEventRequestByServiceLocation(servLoc, requestDate, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ErrorObject[] initiateODMonitoringRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, int periodicity,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        return od_server.initiateODMonitoringRequestByObject(objectName, nounType, phaseCode, periodicity, requestDate, responseURL, transactionID, Float.MIN_NORMAL);
    }

    @Override
    public ObjectRef[] displayODMonitoringRequests() throws RemoteException {
        return od_server.displayODMonitoringRequests();
    }

    @Override
    public ErrorObject[] cancelODMonitoringRequestByObject(
            ObjectRef[] objectRef, Calendar requestDate) throws RemoteException {
        return od_server.cancelODMonitoringRequestByObject(objectRef, requestDate);
    }

    @Override
    public void modifyODDataForOutageDetectionDevice(
            OutageDetectionDevice oDDevice) throws RemoteException {
        od_server.modifyODDataForOutageDetectionDevice(oDDevice);
    }
}