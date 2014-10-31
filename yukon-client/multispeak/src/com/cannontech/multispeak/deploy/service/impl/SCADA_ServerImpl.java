package com.cannontech.multispeak.deploy.service.impl;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MspScadaAnalogReturnList;
import com.cannontech.multispeak.deploy.service.AccumulatedValue;
import com.cannontech.multispeak.deploy.service.DomainMember;
import com.cannontech.multispeak.deploy.service.DomainNameChange;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.ListItem;
import com.cannontech.multispeak.deploy.service.OutageEvent;
import com.cannontech.multispeak.deploy.service.RegistrationInfo;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.cannontech.multispeak.deploy.service.ScadaControl;
import com.cannontech.multispeak.deploy.service.ScadaPoint;
import com.cannontech.multispeak.deploy.service.ScadaStatus;
import com.cannontech.multispeak.service.MultispeakLMService;

public class SCADA_ServerImpl implements SCADA_ServerSoap_PortType {

    @Autowired private MspRawPointHistoryDao mspRawPointHistoryDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private MultispeakLMService multispeakLMService;

    private final Logger log = YukonLogManager.getLogger(SCADA_ServerImpl.class);
    private final static String[] methods = new String[] {
        "pingURL",
        "getMethods",
        "getAllSCADAAnalogs",
    };

    private LiteYukonUser init() throws RemoteException {
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }

    @Override
    public ErrorObject[] pingURL() throws RemoteException {
        init();
        return new ErrorObject[0];
    }

    @Override
    public String[] getMethods() throws RemoteException {
        init();
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR , methods);
    }

    @Override
    public String[] getDomainNames() throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public String[] getPublishMethods() throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived) throws RemoteException {
        LiteYukonUser user = init();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader();
        multispeakEventLogService.methodInvoked("getAllSCADAAnalogs", mspVendor.getCompanyName());

        Date timerStart = new Date();

        MspScadaAnalogReturnList scadaAnalogList = mspRawPointHistoryDao.retrieveLatestScadaAnalogs(user);
        
        multispeakFuncs.updateResponseHeader(scadaAnalogList);
        ScadaAnalog[] scadaAnalogs = new ScadaAnalog[scadaAnalogList.getScadaAnalogs().size()];
        scadaAnalogList.getScadaAnalogs().toArray(scadaAnalogs);
        log.info("Returning All Scada Analog data (" + scadaAnalogs.length + " points). (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(scadaAnalogs.length, scadaAnalogList.getObjectsRemaining(), "ScadaAnalog", scadaAnalogList.getLastSent(),
                                                "getAllSCADAAnalogs", mspVendor.getCompanyName());
        return scadaAnalogs;
    }

    @Override
    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getAllSCADAStatus(String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaPoint[] getAllSCADAPoints(String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaAnalog[] getSCADAAnalogsByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRange(Calendar startTime, Calendar endTime, float sampleRate,
            String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeAndPointIDFormattedBlock(String scadaPointID,
            Calendar startTime, Calendar endTime, float sampleRate, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeFormattedBlock(Calendar startTime, Calendar endTime,
            float sampleRate, String lastReceived) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject initiateControl(ScadaControl controlAction, String responseURL, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] outageEventChangedNotification(OutageEvent[] oEvents) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] pointSubscriptionListNotification(ListItem[] pointList, String responseURL, String errorString)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] analogChangedNotificationByPointID(ScadaAnalog[] scadaAnalogs, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] statusChangedNotificationByPointID(ScadaStatus[] scadaStatuses, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] accumulatedValueChangedNotification(AccumulatedValue[] accumulators) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions, String transactionID)
            throws RemoteException {
        init();
        throw new RemoteException("Method is NOT supported.");
    }
}