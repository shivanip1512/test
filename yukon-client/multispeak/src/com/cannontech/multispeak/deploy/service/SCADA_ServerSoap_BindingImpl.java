/**
 * SCADA_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;

import com.cannontech.spring.YukonSpringHook;

public class SCADA_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType{
    
    private SCADA_ServerSoap_PortType scada_server = YukonSpringHook.getBean("scada_server", SCADA_ServerSoap_PortType.class);

    public ErrorObject[] analogChangedNotificationByPointID(
            ScadaAnalog[] scadaAnalogs, String transactionID)
            throws RemoteException {
        return scada_server.analogChangedNotificationByPointID(scadaAnalogs,
                                                               transactionID);
    }

    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions,
            String transactionID) throws RemoteException {
        return scada_server.controlActionCompleted(controlActions,
                                                   transactionID);
    }

    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived)
            throws RemoteException {
        return scada_server.getAllSCADAAnalogs(lastReceived);
    }

    public ScadaPoint[] getAllSCADAPoints(String lastReceived)
            throws RemoteException {
        return scada_server.getAllSCADAPoints(lastReceived);
    }

    public ScadaStatus[] getAllSCADAStatus(String lastReceived)
            throws RemoteException {
        return scada_server.getAllSCADAStatus(lastReceived);
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return scada_server.getDomainMembers(domainName);
    }

    public String[] getDomainNames() throws RemoteException {
        return scada_server.getDomainNames();
    }

    public String[] getMethods() throws RemoteException {
        return scada_server.getMethods();
    }

    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID,
            String lastReceived) throws RemoteException {
        return scada_server.getModifiedSCADAPoints(previousSessionID,
                                                   lastReceived);
    }

    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID)
            throws RemoteException {
        return scada_server.getSCADAAnalogBySCADAPointID(scadaPointID);
    }

    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID)
            throws RemoteException {
        return scada_server.getSCADAStatusBySCADAPointID(scadaPointID);
    }

    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs,
            String responseURL, String transactionID) throws RemoteException {
        return scada_server.initiateAnalogReadByPointID(pointIDs,
                                                        responseURL,
                                                        transactionID);
    }

    public ErrorObject initiateControl(ScadaControl controlAction,
            String responseURL, String transactionID) throws RemoteException {
        return scada_server.initiateControl(controlAction,
                                            responseURL,
                                            transactionID);
    }

    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs,
            String responseURL, String transactionID) throws RemoteException {
        return scada_server.initiateStatusReadByPointID(pointIDs,
                                                        responseURL,
                                                        transactionID);
    }

    public ErrorObject[] outageEventChangedNotification(OutageEvent[] events)
            throws RemoteException {
        return scada_server.outageEventChangedNotification(events);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return scada_server.pingURL();
    }

    public ErrorObject[] pointSubscriptionListNotification(
            PointSubscriptionListListItem[] pointList, String responseURL)
            throws RemoteException {
        return scada_server.pointSubscriptionListNotification(pointList,
                                                              responseURL);
    }

    public ErrorObject[] statusChangedNotificationByPointID(
            ScadaStatus[] scadaStatuses, String transactionID)
            throws RemoteException {
        return scada_server.statusChangedNotificationByPointID(scadaStatuses,
                                                               transactionID);
    }

   
}
