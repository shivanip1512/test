package com.cannontech.multispeak.deploy.service.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    public void pingURL() throws MultispeakWebServiceException {
        init();
    }

    @Override
    public List<String> getMethods() throws MultispeakWebServiceException {
        init();
        return multispeakFuncs.getMethods(MultispeakDefines.SCADA_Server_STR , Arrays.asList(methods));
    }

    @Override
    public List<ScadaAnalog> getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException {
        LiteYukonUser user = init();
        MultispeakVendor mspVendor = multispeakFuncs.getMultispeakVendorFromHeader(MultiSpeakVersion.V3);
        multispeakEventLogService.methodInvoked("getAllSCADAAnalogs", mspVendor.getCompanyName());

        Date timerStart = new Date();

        MspScadaAnalogReturnList scadaAnalogList = mspRawPointHistoryDao.retrieveLatestScadaAnalogs(user);
        
        multispeakFuncs.updateResponseHeader(scadaAnalogList);
        List<ScadaAnalog> scadaAnalogs = scadaAnalogList.getScadaAnalogs();
        log.info("Returning All Scada Analog data (" + scadaAnalogs.size() + " points). (" + (new Date().getTime() - timerStart.getTime())*.001 + " secs)");
        multispeakEventLogService.returnObjects(scadaAnalogs.size(), scadaAnalogList.getObjectsRemaining(), "ScadaAnalog", scadaAnalogList.getLastSent(),
                                                "getAllSCADAAnalogs", mspVendor.getCompanyName());
        return scadaAnalogs;
    }

    @Override
    public List<String> getDomainNames() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<DomainMember> getDomainMembers(String domainName) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public String requestRegistrationID() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> unregisterForService(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<String> getPublishMethods() throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainMembersChangedNotification(List<DomainMember> changedDomainMembers)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> domainNamesChangedNotification(List<DomainNameChange> changedDomainNames)
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
    public List<ScadaStatus> getAllSCADAStatus(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ScadaPoint> getAllSCADAPoints(String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ScadaPoint> getModifiedSCADAPoints(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ScadaAnalog> getSCADAAnalogsByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ScadaStatus> getSCADAStatusesByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ScadaStatus> getSCADAStatusesByDateRange(Calendar startTime, Calendar endTime, float sampleRate,
            String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getSCADAStatusesByDateRangeAndPointIDFormattedBlock(String scadaPointID,
            Calendar startTime, Calendar endTime, float sampleRate, String lastReceived)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<FormattedBlock> getSCADAStatusesByDateRangeFormattedBlock(Calendar startTime, Calendar endTime,
            float sampleRate, String lastReceived) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateStatusReadByPointID(List<String> pointIDs, String responseURL, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> initiateAnalogReadByPointID(List<String> pointIDs, String responseURL, String transactionID)
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
    public List<ErrorObject> outageEventChangedNotification(List<OutageEvent> oEvents) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> pointSubscriptionListNotification(List<ListItem> pointList, String responseURL, String errorString)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> analogChangedNotificationByPointID(List<ScadaAnalog> scadaAnalogs, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> statusChangedNotificationByPointID(List<ScadaStatus> scadaStatuses, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAAnalogChangedNotification(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAStatusChangedNotification(List<ScadaStatus> scadaStatuses)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> accumulatedValueChangedNotification(List<AccumulatedValue> accumulators)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAPointChangedNotification(List<ScadaPoint> scadaPoints) throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAPointChangedNotificationForAnalog(List<ScadaPoint> scadaPoints)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAPointChangedNotificationForStatus(List<ScadaPoint> scadaPoints)
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
    public List<ErrorObject> SCADAAnalogChangedNotificationForPower(List<ScadaAnalog> scadaAnalogs)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }

    @Override
    public List<ErrorObject> SCADAAnalogChangedNotificationForVoltage(List<ScadaAnalog> scadaAnalogs)
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
    public List<ErrorObject> controlActionCompleted(List<ScadaControl> controlActions, String transactionID)
            throws MultispeakWebServiceException {
        init();
        throw new MultispeakWebServiceException("Method is NOT supported.");
    }
}