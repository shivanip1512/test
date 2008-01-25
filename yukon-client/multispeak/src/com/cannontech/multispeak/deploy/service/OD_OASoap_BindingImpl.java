/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.deploy.service.impl.OD_OAImpl;
import com.cannontech.spring.YukonSpringHook;

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.OD_OASoap_PortType{
    private OD_OASoap_PortType od_oa = (OD_OAImpl)YukonSpringHook.getBean("od_oa");

    public ErrorObject[] cancelODMonitoringRequestByObject(
            ObjectRef[] objectRef, Calendar requestDate) throws RemoteException {
        return od_oa.cancelODMonitoringRequestByObject(objectRef, requestDate);
    }

    public ObjectRef[] displayODMonitoringRequests() throws RemoteException {
        return od_oa.displayODMonitoringRequests();
    }

    public OutageDetectionDevice[] getAllOutageDetectionDevices(
            String lastReceived) throws RemoteException {
        return od_oa.getAllOutageDetectionDevices(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return od_oa.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return od_oa.getDomainNames();
    }

    public String[] getMethods() throws RemoteException {
        return od_oa.getMethods();
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(
            String meterNo) throws RemoteException {
        return od_oa.getOutageDetectionDevicesByMeterNo(meterNo);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByStatus(
            OutageDetectDeviceStatus status, String lastReceived)
            throws RemoteException {
        return od_oa.getOutageDetectionDevicesByStatus(status, lastReceived);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByType(
            OutageDetectDeviceType type, String lastReceived)
            throws RemoteException {
        return od_oa.getOutageDetectionDevicesByType(type, lastReceived);
    }

    public OutageDetectionDevice[] getOutagedODDevices() throws RemoteException {
        return od_oa.getOutagedODDevices();
    }

    public ErrorObject[] initiateODEventRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, Calendar requestDate,
            String responseURL, String transactionID) throws RemoteException {
        return od_oa.initiateODEventRequestByObject(objectName,
                                                    nounType,
                                                    phaseCode,
                                                    requestDate,
                                                    responseURL,
                                                    transactionID);
    }

    public ErrorObject[] initiateODEventRequestByServiceLocation(
            String[] servLoc, Calendar requestDate, String responseURL,
            String transactionID) throws RemoteException {
        return od_oa.initiateODEventRequestByServiceLocation(servLoc,
                                                             requestDate,
                                                             responseURL,
                                                             transactionID);
    }

    public ErrorObject[] initiateODMonitoringRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, int periodicity,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        return od_oa.initiateODMonitoringRequestByObject(objectName,
                                                         nounType,
                                                         phaseCode,
                                                         periodicity,
                                                         requestDate,
                                                         responseURL,
                                                         transactionID);
    }

    public ErrorObject[] initiateOutageDetectionEventRequest(String[] meterNos,
            Calendar requestDate, String responseURL, String transactionID)
            throws RemoteException {
        return od_oa.initiateOutageDetectionEventRequest(meterNos,
                                                         requestDate,
                                                         responseURL,
                                                         transactionID);
    }

    public void modifyODDataForOutageDetectionDevice(
            OutageDetectionDevice device) throws RemoteException {
        od_oa.modifyODDataForOutageDetectionDevice(device);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return od_oa.pingURL();
    }
}
