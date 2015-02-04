package com.cannontech.multispeak.service;

import java.util.Calendar;

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
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface SCADA_Server {

    /**
     * ping URL
     * 
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods
     * 
     * @return
     * @throws MultispeakWebServiceException
     */
    public String[] getMethods() throws MultispeakWebServiceException;

    /**
     * get All SCADA Analogs
     * 
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaAnalog[] getAllSCADAAnalogs(String lastReceived) throws MultispeakWebServiceException;

    /**
     * @return
     * @throws MultispeakWebServiceException
     */
    public String[] getDomainNames() throws MultispeakWebServiceException;

    /**
     * @param domainName
     * @return
     * @throws MultispeakWebServiceException
     */
    public DomainMember[] getDomainMembers(String domainName) throws MultispeakWebServiceException;

    /**
     * @return
     * @throws MultispeakWebServiceException
     */
    public String requestRegistrationID() throws MultispeakWebServiceException;

    /**
     * @param registrationDetails
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException;

    /**
     * @param registrationID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException;

    /**
     * @param registrationID
     * @return
     * @throws MultispeakWebServiceException
     */
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException;

    /**
     * @return
     * @throws MultispeakWebServiceException
     */
    public String[] getPublishMethods() throws MultispeakWebServiceException;

    /**
     * @param changedDomainMembers
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException;

    /**
     * @param changedDomainNames
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaAnalog getSCADAAnalogBySCADAPointID(String scadaPointID) throws MultispeakWebServiceException;

    /**
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaPoint[] getAllSCADAPoints(String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param previousSessionID
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaPoint[] getModifiedSCADAPoints(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaAnalog[] getSCADAAnalogsByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaStatus[] getSCADAStatusesByDateRangeAndPointID(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaStatus[] getSCADAStatusesByDateRange(Calendar startTime, Calendar endTime, float sampleRate,
            String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public FormattedBlock[] getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(String scadaPointID, Calendar startTime,
            Calendar endTime, float sampleRate, String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public FormattedBlock[] getSCADAStatusesByDateRangeAndPointIDFormattedBlock(String scadaPointID,
            Calendar startTime, Calendar endTime, float sampleRate, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * @param startTime
     * @param endTime
     * @param sampleRate
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public FormattedBlock[] getSCADAStatusesByDateRangeFormattedBlock(Calendar startTime, Calendar endTime,
            float sampleRate, String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param pointIDs
     * @param responseURL
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] initiateStatusReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws MultispeakWebServiceException;

    /**
     * @param pointIDs
     * @param responseURL
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] initiateAnalogReadByPointID(String[] pointIDs, String responseURL, String transactionID)
            throws MultispeakWebServiceException;

    /**
     * @param controlAction
     * @param responseURL
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject initiateControl(ScadaControl controlAction, String responseURL, String transactionID)
            throws MultispeakWebServiceException;

    /**
     * @param oEvents
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] outageEventChangedNotification(OutageEvent[] oEvents) throws MultispeakWebServiceException;

    /**
     * @param pointList
     * @param responseURL
     * @param errorString
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] pointSubscriptionListNotification(ListItem[] pointList, String responseURL, String errorString)
            throws MultispeakWebServiceException;

    /**
     * @param scadaAnalogs
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] analogChangedNotificationByPointID(ScadaAnalog[] scadaAnalogs, String transactionID)
            throws MultispeakWebServiceException;

    /**
     * @param lastReceived
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaStatus[] getAllSCADAStatus(String lastReceived) throws MultispeakWebServiceException;

    /**
     * @param scadaPointID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ScadaStatus getSCADAStatusBySCADAPointID(String scadaPointID) throws MultispeakWebServiceException;

    /**
     * @param scadaStatuses
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] statusChangedNotificationByPointID(ScadaStatus[] scadaStatuses, String transactionID)
            throws MultispeakWebServiceException;

    /**
     * @param scadaAnalogs
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAAnalogChangedNotification(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * @param scadaStatuses
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAStatusChangedNotification(ScadaStatus[] scadaStatuses)
            throws MultispeakWebServiceException;

    /**
     * @param accumulators
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] accumulatedValueChangedNotification(AccumulatedValue[] accumulators)
            throws MultispeakWebServiceException;

    /**
     * @param scadaPoints
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAPointChangedNotification(ScadaPoint[] scadaPoints) throws MultispeakWebServiceException;

    /**
     * @param scadaPoints
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAPointChangedNotificationForAnalog(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException;

    /**
     * @param scadaPoints
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAPointChangedNotificationForStatus(ScadaPoint[] scadaPoints)
            throws MultispeakWebServiceException;

    /**
     * @param scadaAnalog
     * @throws MultispeakWebServiceException
     */
    public void SCADAAnalogChangedNotificationByPointID(ScadaAnalog scadaAnalog) throws MultispeakWebServiceException;

    /**
     * @param scadaAnalogs
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAAnalogChangedNotificationForPower(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * @param scadaAnalogs
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] SCADAAnalogChangedNotificationForVoltage(ScadaAnalog[] scadaAnalogs)
            throws MultispeakWebServiceException;

    /**
     * @param scadaStatus
     * @throws MultispeakWebServiceException
     */
    public void SCADAStatusChangedNotificationByPointID(ScadaStatus scadaStatus) throws MultispeakWebServiceException;

    /**
     * @param controlActions
     * @param transactionID
     * @return
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] controlActionCompleted(ScadaControl[] controlActions, String transactionID)
            throws MultispeakWebServiceException;

}
