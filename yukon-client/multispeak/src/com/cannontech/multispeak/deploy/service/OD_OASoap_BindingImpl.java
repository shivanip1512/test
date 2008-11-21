/**
 * OD_OASoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.multispeak.deploy.service.impl.OD_ServerImpl;
import com.cannontech.spring.YukonSpringHook;

public class OD_OASoap_BindingImpl implements com.cannontech.multispeak.deploy.service.OD_OASoap_PortType{
    private OD_ServerSoap_PortType od_server = YukonSpringHook.getBean("od_server", OD_ServerImpl.class);

    public ErrorObject[] cancelODMonitoringRequestByObject(ObjectRef[] objectRef, Calendar requestDate)
            throws RemoteException {
        return od_server.cancelODMonitoringRequestByObject(objectRef, requestDate);
    }

    public ObjectRef[] displayODMonitoringRequests() throws RemoteException {
        return od_server.displayODMonitoringRequests();
    }

    public OutageDetectionDevice[] getAllOutageDetectionDevices(String lastReceived) throws RemoteException {
        return od_server.getAllOutageDetectionDevices(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        return od_server.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return od_server.getDomainNames();
    }

    public String[] getMethods() throws RemoteException {
        return od_server.getMethods();
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(String meterNo) throws RemoteException {
        return od_server.getOutageDetectionDevicesByMeterNo(meterNo);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByStatus(OutageDetectDeviceStatus status,
            String lastReceived) throws RemoteException {
        return od_server.getOutageDetectionDevicesByStatus(status, lastReceived);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByType(OutageDetectDeviceType type, String lastReceived)
            throws RemoteException {
        return od_server.getOutageDetectionDevicesByType(type, lastReceived);
    }

    public OutageDetectionDevice[] getOutagedODDevices() throws RemoteException {
        return od_server.getOutagedODDevices();
    }

    public ErrorObject[] initiateODEventRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            Calendar requestDate, String responseURL, String transactionID) throws RemoteException {
        return od_server.initiateODEventRequestByObject(objectName,
                                                    nounType,
                                                    phaseCode,
                                                    requestDate,
                                                    responseURL,
                                                    transactionID);
    }

    public ErrorObject[] initiateODEventRequestByServiceLocation(String[] servLoc, Calendar requestDate,
            String responseURL, String transactionID) throws RemoteException {
        return od_server.initiateODEventRequestByServiceLocation(servLoc, requestDate, responseURL, transactionID);
    }

    public ErrorObject[] initiateODMonitoringRequestByObject(String objectName, String nounType, PhaseCd phaseCode,
            int periodicity, Calendar requestDate, String responseURL, String transactionID) throws RemoteException {
        return od_server.initiateODMonitoringRequestByObject(objectName,
                                                         nounType,
                                                         phaseCode,
                                                         periodicity,
                                                         requestDate,
                                                         responseURL,
                                                         transactionID);
    }

    public ErrorObject[] initiateOutageDetectionEventRequest(String[] meterNos, Calendar requestDate,
            String responseURL, String transactionID) throws RemoteException {
        return od_server.initiateOutageDetectionEventRequest(meterNos, requestDate, responseURL, transactionID);
    }

    public void modifyODDataForOutageDetectionDevice(OutageDetectionDevice device) throws RemoteException {
        od_server.modifyODDataForOutageDetectionDevice(device);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return od_server.pingURL();
    }
}
