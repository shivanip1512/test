/**
 * SCADA_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.spring.YukonSpringHook;

public class SCADA_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType{
    
    private SCADA_ServerSoap_PortType scada_server = YukonSpringHook.getBean("scada_server", SCADA_ServerSoap_PortType.class);

    @Override
    public ErrorObject[] pingURL() throws RemoteException {
        return scada_server.pingURL();
    }

    @Override
    public String[] getMethods() throws RemoteException {
        return scada_server.getMethods();
    }

    @Override
    public String[] getDomainNames() throws RemoteException {
        return scada_server.getDomainNames();
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        return scada_server.getDomainMembers(domainName);
    }

    @Override
    public String requestRegistrationID() throws RemoteException {
        return scada_server.requestRegistrationID();
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws RemoteException {
        return scada_server.registerForService(registrationDetails);
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws RemoteException {
        return scada_server.unregisterForService(registrationID);
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws RemoteException {
        return scada_server.getRegistrationInfoByID(registrationID);
    }

    @Override
    public String[] getPublishMethods() throws RemoteException {
        return scada_server.getPublishMethods();
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers) throws RemoteException {
        return scada_server.domainMembersChangedNotification(changedDomainMembers);
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames) throws RemoteException {
        return scada_server.domainNamesChangedNotification(changedDomainNames);
    }

    @Override
    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived) throws RemoteException {
        return scada_server.getAllSCADAAnalogs(lastReceived);
    }

    @Override
    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID) throws RemoteException {
        return scada_server.getSCADAAnalogBySCADAPointID(scadaPointID);
    }

    @Override
    public ScadaStatus[] getAllSCADAStatus(String lastReceived) throws RemoteException {
        return scada_server.getAllSCADAStatus(lastReceived);
    }

    @Override
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID) throws RemoteException {
        return scada_server.getSCADAStatusBySCADAPointID(scadaPointID);
    }

    @Override
    public ScadaPoint[] getAllSCADAPoints(String lastReceived) throws RemoteException {
        return scada_server.getAllSCADAPoints(lastReceived);
    }

    @Override
    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID, String lastReceived) throws RemoteException {
        return scada_server.getModifiedSCADAPoints(previousSessionID, lastReceived);
    }

    @Override
    public ScadaAnalog[] getSCADAAnalogsByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        return scada_server.getSCADAAnalogsByDateRangeAndPointID(scadaPointID,
                                                                 startTime,
                                                                 endTime,
                                                                 sampleRate,
                                                                 lastReceived);
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        return scada_server.getSCADAStatusesByDateRangeAndPointID(scadaPointID,
                                                                  startTime,
                                                                  endTime,
                                                                  sampleRate,
                                                                  lastReceived);
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRange(Calendar startTime, Calendar endTime, float sampleRate,
            String lastReceived) throws RemoteException {
        return scada_server.getSCADAStatusesByDateRange(startTime, endTime, sampleRate, lastReceived);
    }

    @Override
    public FormattedBlock[] getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        return scada_server.getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(scadaPointID,
                                                                               startTime,
                                                                               endTime,
                                                                               sampleRate,
                                                                               lastReceived);
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeAndPointIDFormattedBlock(String scadaPointID,
            Calendar startTime, Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        return scada_server.getSCADAStatusesByDateRangeAndPointIDFormattedBlock(scadaPointID,
                                                                                startTime,
                                                                                endTime,
                                                                                sampleRate,
                                                                                lastReceived);
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeFormattedBlock(Calendar startTime, Calendar endTime,
            float sampleRate, String lastReceived) throws RemoteException {
        return scada_server.getSCADAStatusesByDateRangeFormattedBlock(startTime, endTime, sampleRate, lastReceived);
    }

    @Override
    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws RemoteException {
        return scada_server.initiateStatusReadByPointID(pointIDs, responseURL, transactionID);
    }

    @Override
    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws RemoteException {
        return scada_server.initiateAnalogReadByPointID(pointIDs, responseURL, transactionID);
    }

    @Override
    public ErrorObject initiateControl(ScadaControl controlAction, String responseURL, String transactionID)
            throws RemoteException {
        return scada_server.initiateControl(controlAction, responseURL, transactionID);
    }

    @Override
    public ErrorObject[] outageEventChangedNotification(OutageEvent[] oEvents) throws RemoteException {
        return scada_server.outageEventChangedNotification(oEvents);
    }

    @Override
    public ErrorObject[] pointSubscriptionListNotification(ListItem[] pointList, String responseURL, String errorString)
            throws RemoteException {
        return scada_server.pointSubscriptionListNotification(pointList, responseURL, errorString);
    }

    @Override
    public ErrorObject[] analogChangedNotificationByPointID(ScadaAnalog[] scadaAnalogs, String transactionID)
            throws RemoteException {
        return scada_server.analogChangedNotificationByPointID(scadaAnalogs, transactionID);
    }

    @Override
    public ErrorObject[] statusChangedNotificationByPointID(ScadaStatus[] scadaStatuses, String transactionID)
            throws RemoteException {
        return scada_server.statusChangedNotificationByPointID(scadaStatuses, transactionID);
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return scada_server.SCADAAnalogChangedNotification(scadaAnalogs);
    }

    @Override
    public ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses) throws RemoteException {
        return scada_server.SCADAStatusChangedNotification(scadaStatuses);
    }

    @Override
    public ErrorObject[] accumulatedValueChangedNotification(AccumulatedValue[] accumulators) throws RemoteException {
        return scada_server.accumulatedValueChangedNotification(accumulators);
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws RemoteException {
        return scada_server.SCADAPointChangedNotification(scadaPoints);
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints) throws RemoteException {
        return scada_server.SCADAPointChangedNotificationForAnalog(scadaPoints);
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints) throws RemoteException {
        return scada_server.SCADAPointChangedNotificationForStatus(scadaPoints);
    }

    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws RemoteException {
        scada_server.SCADAAnalogChangedNotificationByPointID(scadaAnalog);
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return scada_server.SCADAAnalogChangedNotificationForPower(scadaAnalogs);
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        return scada_server.SCADAAnalogChangedNotificationForVoltage(scadaAnalogs);
    }

    @Override
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws RemoteException {
        scada_server.SCADAStatusChangedNotificationByPointID(scadaStatus);
    }

    @Override
    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions, String transactionID)
            throws RemoteException {
        return scada_server.controlActionCompleted(controlActions, transactionID);
    }
    
}
