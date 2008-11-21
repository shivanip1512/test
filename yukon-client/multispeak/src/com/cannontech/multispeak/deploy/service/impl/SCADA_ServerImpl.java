package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.multispeak.client.Multispeak;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.OutageEvent;
import com.cannontech.multispeak.deploy.service.PointSubscriptionListListItem;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.cannontech.multispeak.deploy.service.ScadaControl;
import com.cannontech.multispeak.deploy.service.ScadaPoint;
import com.cannontech.multispeak.deploy.service.ScadaStatus;
import com.cannontech.multispeak.service.MspValidationService;

public class SCADA_ServerImpl implements SCADA_ServerSoap_PortType
{
    public Multispeak multispeak;
    public MultispeakFuncs multispeakFuncs;
    public MspMeterDao mspMeterDao;
    public MspValidationService mspValidationService;

    @Required
    public void setMultispeak(Multispeak multispeak) {
        this.multispeak = multispeak;
    }
    @Required
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    @Required
    public void setMspMeterDao(MspMeterDao mspMeterDao) {
        this.mspMeterDao = mspMeterDao;
    }
    @Required
    public void setMspValidationService(
            MspValidationService mspValidationService) {
        this.mspValidationService = mspValidationService;
    }
    private void init() throws RemoteException{
        multispeakFuncs.init();
    }
    
    @Override
    public ErrorObject[] pingURL() throws java.rmi.RemoteException {
        init();
        return new ErrorObject[0];
    }
    
    @Override
    public String[] getMethods() throws java.rmi.RemoteException {
        init();
        String [] methods = new String[]{"pingURL", "getMethods"};
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR, methods );
    }
    
    @Override
    public String[] getDomainNames() throws java.rmi.RemoteException {
        init();
        String [] strings = new String[]{"Method Not Supported"};
        multispeakFuncs.logStrings(MultispeakDefines.SCADA_Server_STR, "getDomainNames", strings);
        return strings;
    }
    
    @Override
    public DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException {
        init();
        return new DomainMember[0];
    }
    @Override
    public ErrorObject[] analogChangedNotificationByPointID(
            ScadaAnalog[] scadaAnalogs, String transactionID)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions,
            String transactionID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaPoint[] getAllSCADAPoints(String lastReceived)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaStatus[] getAllSCADAStatus(String lastReceived)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID,
            String lastReceived) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject initiateControl(ScadaControl controlAction,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs,
            String responseURL, String transactionID) throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] outageEventChangedNotification(OutageEvent[] events)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] pointSubscriptionListNotification(
            PointSubscriptionListListItem[] pointList, String responseURL)
            throws RemoteException {
        init();
        return null;
    }
    @Override
    public ErrorObject[] statusChangedNotificationByPointID(
            ScadaStatus[] scadaStatuses, String transactionID)
            throws RemoteException {
        init();
        return null;
    }
}
