package com.cannontech.multispeak.deploy.service.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.msp.beans.v3.AccumulatedValue;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.ListItem;
import com.cannontech.msp.beans.v3.OutageEvent;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.ScadaControl;
import com.cannontech.msp.beans.v3.ScadaPoint;
import com.cannontech.msp.beans.v3.ScadaStatus;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspRawPointHistoryDao;
import com.cannontech.multispeak.data.MspScadaAnalogReturnList;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakLMService;
import com.cannontech.multispeak.service.SCADA_Server;

@Service
public class SCADA_ServerImpl implements SCADA_Server {

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

    private LiteYukonUser init() throws MultispeakWebServiceException {
        multispeakFuncs.init();
        return multispeakFuncs.authenticateMsgHeader();
    }

    @Override
    public ErrorObject[] pingURL() throws MultispeakWebServiceException {
        init();
        return new ErrorObject[0];
    }

    @Override
    public String[] getMethods() throws MultispeakWebServiceException {
        init();
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR , methods);
    }

    @Override
    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException {
        LiteYukonUser user = init();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
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
    public String[] getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public DomainMember[] getDomainMembers(String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String[] getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getAllSCADAStatus(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaPoint[] getAllSCADAPoints(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaAnalog[] getSCADAAnalogsByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus[] getSCADAStatusesByDateRange(Calendar startTime, Calendar endTime, float sampleRate,
            String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeAndPointIDFormattedBlock(String scadaPointID,
            Calendar startTime, Calendar endTime, float sampleRate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public FormattedBlock[] getSCADAStatusesByDateRangeFormattedBlock(Calendar startTime, Calendar endTime,
            float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject initiateControl(ScadaControl controlAction, String responseURL, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] outageEventChangedNotification(OutageEvent[] oEvents) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] pointSubscriptionListNotification(ListItem[] pointList, String responseURL, String errorString)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] analogChangedNotificationByPointID(ScadaAnalog[] scadaAnalogs, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] statusChangedNotificationByPointID(ScadaStatus[] scadaStatuses, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] accumulatedValueChangedNotification(AccumulatedValue[] accumulators)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}