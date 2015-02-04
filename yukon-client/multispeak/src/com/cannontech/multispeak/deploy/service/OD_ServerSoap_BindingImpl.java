/**
 * OD_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.spring.YukonSpringHook;

public class OD_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.OD_ServerSoap_PortType{
	
	private OD_ServerSoap_PortType od_server = YukonSpringHook.getBean("od_server", OD_ServerSoap_PortType.class);

    public ErrorObject[] pingURL() throws RemoteException {
        return od_server.pingURL();
    }

    public String[] getMethods() throws RemoteException {
        return od_server.getMethods();
    }

    public String[] getDomainNames() throws RemoteException {
        return od_server.getDomainNames();
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return od_server.getDomainMembers(domainName);
    }

    public String requestRegistrationID() throws RemoteException {
        return od_server.requestRegistrationID();
    }

    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        return od_server.registerForService(registrationDetails);
    }

    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        return od_server.unregisterForService(registrationID);
    }

    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        return od_server.getRegistrationInfoByID(registrationID);
    }

    public String[] getPublishMethods() throws RemoteException {
        return od_server.getPublishMethods();
    }

    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        return od_server.domainMembersChangedNotification(changedDomainMembers);
    }

    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        return od_server.domainNamesChangedNotification(changedDomainNames);
    }

    public OutageDetectionDevice[] getAllOutageDetectionDevices(
            String lastReceived) throws RemoteException {
        return od_server.getAllOutageDetectionDevices(lastReceived);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(
            String meterNo) throws RemoteException {
        return od_server.getOutageDetectionDevicesByMeterNo(meterNo);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByStatus(
            OutageDetectDeviceStatus oDDStatus, String lastReceived)
            throws RemoteException {
        return od_server.getOutageDetectionDevicesByStatus(oDDStatus,
                                                           lastReceived);
    }

    public OutageDetectionDevice[] getOutageDetectionDevicesByType(
            OutageDetectDeviceType oDDType, String lastReceived)
            throws RemoteException {
        return od_server.getOutageDetectionDevicesByType(oDDType, lastReceived);
    }

    public OutageDetectionDevice[] getOutagedODDevices() throws RemoteException {
        return od_server.getOutagedODDevices();
    }

    public ErrorObject[] initiateOutageDetectionEventRequest(String[] meterNos,
            Calendar requestDate, String responseURL, String transactionID,
            Float expirationTime) throws RemoteException {
        return od_server.initiateOutageDetectionEventRequest(meterNos,
                                                             requestDate,
                                                             responseURL,
                                                             transactionID,
                                                             expirationTime);
    }

    public ErrorObject[] initiateODEventRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, Calendar requestDate,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return od_server.initiateODEventRequestByObject(objectName,
                                                        nounType,
                                                        phaseCode,
                                                        requestDate,
                                                        responseURL,
                                                        transactionID,
                                                        expirationTime);
    }

    public ErrorObject[] initiateODEventRequestByServiceLocation(
            String[] servLoc, Calendar requestDate, String responseURL,
            String transactionID, float expirationTime) throws RemoteException {
        return od_server.initiateODEventRequestByServiceLocation(servLoc,
                                                                 requestDate,
                                                                 responseURL,
                                                                 transactionID,
                                                                 expirationTime);
    }

    public ErrorObject[] initiateODMonitoringRequestByObject(String objectName,
            String nounType, PhaseCd phaseCode, int periodicity,
            Calendar requestDate, String responseURL, String transactionID,
            float expirationTime) throws RemoteException {
        return od_server.initiateODMonitoringRequestByObject(objectName,
                                                             nounType,
                                                             phaseCode,
                                                             periodicity,
                                                             requestDate,
                                                             responseURL,
                                                             transactionID,
                                                             expirationTime);
    }

    public ObjectRef[] displayODMonitoringRequests() throws RemoteException {
        return od_server.displayODMonitoringRequests();
    }

    public ErrorObject[] cancelODMonitoringRequestByObject(
            ObjectRef[] objectRef, Calendar requestDate) throws RemoteException {
        return od_server.cancelODMonitoringRequestByObject(objectRef,
                                                           requestDate);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return od_server.customerChangedNotification(changedCustomers);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return od_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return od_server.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] outageEventChangedNotification(OutageEvent[] oEvents)
            throws RemoteException {
        return od_server.outageEventChangedNotification(oEvents);
    }

    public void modifyODDataForOutageDetectionDevice(
            OutageDetectionDevice oDDevice) throws RemoteException {
        od_server.modifyODDataForOutageDetectionDevice(oDDevice);
    }

	
}
