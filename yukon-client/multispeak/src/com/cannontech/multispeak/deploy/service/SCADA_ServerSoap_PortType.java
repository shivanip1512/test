/**
 * SCADA_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface SCADA_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Requester pings URL of SCADA to see if it is alive. Returns
     * errorObject(s) as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by SCADA.
     */
    public java.lang.String[] getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server.
     */
    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server.
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * This service requests of the publisher a unique registration
     * ID that would subsequently be used to refer unambiguously to that
     * specific subscription.  The return parameter is the registrationID,
     * which is a string-type value.  It is recommended that the server not
     * implement registration in such a manner that one client can guess
     * the registrationID of another.  For instance the use of sequential
     * numbers for registrationIDs is discouraged.
     */
    public java.lang.String requestRegistrationID() throws java.rmi.RemoteException;

    /**
     * This method establishs a subscription using a previously requested
     * registrationID. The calling parameter registrationInfo is a complex
     * type that includes the following information: registrationID - the
     * previously requested registrationID obtained from the publisher by
     * calling RequestRegistrationID, responseURL - the URL to which information
     * should subsequently be published on this subscription, msFunction
     * - the abbreviated string name of the MultiSpeak method making the
     * subscription request (for instance, if an application that exposes
     * the Meter Reading function has made the request, then the msFunction
     * variable should include "MR"?), methodsList - An array of strings that
     * contain the string names of the MultiSpeak methods to which the subscriber
     * would like to subscribe.  Subsequent calls to RegisterForService on
     * an existing subscription replace prior subscription details in their
     * entirety - they do NOT add to an existing subscription.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] registerForService(com.cannontech.multispeak.deploy.service.RegistrationInfo registrationDetails) throws java.rmi.RemoteException;

    /**
     * This method deletes a previously established subscription (registration
     * for service) that carries the registration identifer listed in the
     * input parameter registrationID.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] unregisterForService(java.lang.String registrationID) throws java.rmi.RemoteException;

    /**
     * This method requests the return of existing registration information
     * (that is to say the details of what is subscribed on this subscription)
     * for a specific registrationID.  The server should return a SOAPFault
     * if the registrationID is not valid.
     */
    public com.cannontech.multispeak.deploy.service.RegistrationInfo getRegistrationInfoByID(java.lang.String registrationID) throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods to which this server can
     * publish information.
     */
    public java.lang.String[] getPublishMethods() throws java.rmi.RemoteException;

    /**
     * This method permits a client to have changed information on
     * domain members published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainMembersChangedNotification as one of the methods in the list
     * of methods to which the client has subscribed.  The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the  domainMember information.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainMembersChangedNotification(com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers) throws java.rmi.RemoteException;

    /**
     * This method permits a client to have changed information on
     * domain names published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainNamesChangedNotification as one of the methods in the list of
     * methods to which the client has subscribed.  The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the  domainName information.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainNamesChangedNotification(com.cannontech.multispeak.deploy.service.DomainNameChange[] changedDomainNames) throws java.rmi.RemoteException;

    /**
     * Returns all SCADA Analogs.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. If the sessionID parameter is set in the message header, then
     * the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog[] getAllSCADAAnalogs(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a specific SCADA Analog by SCADAPointID.
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog getSCADAAnalogBySCADAPointID(java.lang.String scadaPointID) throws java.rmi.RemoteException;

    /**
     * Returns all SCADA Status data. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. If the sessionID parameter is set in the message header, then
     * the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus[] getAllSCADAStatus(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a specific SCADA Status by SCADAPointID.
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus getSCADAStatusBySCADAPointID(java.lang.String scadaPointID) throws java.rmi.RemoteException;

    /**
     * Returns a list of SCADA point definitions. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. If the sessionID parameter is set in the message header, then
     * the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.ScadaPoint[] getAllSCADAPoints(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a list of SCADA Point definitions that has changed
     * since the session identified by the sessionID.  The calling parameter
     * previousSessionID should carry the session identifier for the last
     * session of data that the client has successfully received.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.ScadaPoint[] getModifiedSCADAPoints(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for a specific SCADA Analog by SCADAPointID
     * within a range of times. The sample rate should be expressed in seconds.
     * If the server cannot provide the data at the requested sample rate,
     * it should provide what it has.
     */
    public com.cannontech.multispeak.deploy.service.ScadaAnalog[] getSCADAAnalogsByDateRangeAndPointID(java.lang.String scadaPointID, java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for a specific SCADA Status by SCADAPointID
     * within a range of times. The sample rate should be expressed in seconds.
     * If the server cannot provide the data at the requested sample rate,
     * it should provide what it has.
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus[] getSCADAStatusesByDateRangeAndPointID(java.lang.String scadaPointID, java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for all SCADA Status points within
     * a range of times. The sample rate should be expressed in seconds.
     * If the server cannot provide the data at the requested sample rate,
     * it should provide what it has.
     */
    public com.cannontech.multispeak.deploy.service.ScadaStatus[] getSCADAStatusesByDateRange(java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for a specific SCADA Analog by SCADAPointID
     * within a range of times.  The data are returned in one or more formattedBlocks.
     * The sample rate should be expressed in seconds.  If the server cannot
     * provide the data at the requested sample rate, it should provide what
     * it has.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getSCADAAnalogsByDateRangeAndPointIDFormattedBlock(java.lang.String scadaPointID, java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for a specific SCADA Status by SCADAPointID
     * within a range of times.  The data are returned in one or more formattedBlocks.
     * The sample rate should be expressed in seconds.  If the server cannot
     * provide the data at the requested sample rate, it should provide what
     * it has.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getSCADAStatusesByDateRangeAndPointIDFormattedBlock(java.lang.String scadaPointID, java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history records for all SCADA Status points within
     * a range of times.  The data are returned in one or more formattedBlocks.
     * The sample rate should be expressed in seconds.  If the server cannot
     * provide the data at the requested sample rate, it should provide what
     * it has.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getSCADAStatusesByDateRangeFormattedBlock(java.util.Calendar startTime, java.util.Calendar endTime, float sampleRate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Client requests that SCADA publish the status of SCADA points,
     * as selected by the array of SCADA pointIDs.  DAD returns information
     * about failed transactions using an array of errorObjects.  Possible
     * errorStrings could include 'Initiate already in progress for this
     * pointID' and 'Remote device unreachable'. The DAD status is returned
     * to the SCADA in the form of scadaStatus objects returned in a StatusChangedNotificationByPointID,
     * sent to the URL specified in the responseURL.  The transactionID calling
     * parameter links this Initiate request with the published data method
     * call.The expiration time parameter indicates the amount of time for
     * which the publisher should try to obtain and publish the data; if
     * the publisher has been unsuccessful in publishing the data after the
     * expiration time (specified in seconds), then the publisher will discard
     * the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateStatusReadByPointID(java.lang.String[] pointIDs, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Client requests that SCADA publish the value of SCADA analog
     * points, as selected by the array of SCADA pointIDs.  DAD returns information
     * about failed transactions using an array of errorObjects.  Possible
     * errorStrings could include 'Initiate already in progress for this
     * pointID' and 'Remote device unreachable'. The DAD analog information
     * is returned to the SCADA in the form of scadaAnalog objects returned
     * in a AnalogChangedNotificationByPointID, sent to the URL specified
     * in the responseURL.  The transactionID calling parameter links this
     * Initiate request with the published data method call.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateAnalogReadByPointID(java.lang.String[] pointIDs, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * THIS METHOD IS INCLUDED FOR DISCUSSION ONLY; IT SHOULD NOT
     * BE IMPLEMENTED UNTIL THE TECHNICAL COMMITTEE ACCEPTS THE FORM OF THE
     * METHOD. DAC requests that SCADA take a control action on a SCADA point,
     * as selected by the scadaControl object.  SCADA returns information
     * about a failed transaction in an errorobject.  Once the action is
     * processed, the SCADA publishes the fact to the DAC using a ControlActionCompleted
     * method call.  The transactionID calling parameter is used to link
     * the InitiateControl and ControlActionCompleted method calls.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject initiateControl(com.cannontech.multispeak.deploy.service.ScadaControl controlAction, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of a change in OutageEvent by sending
     * an array of changed OutageEvent objects.  SCADA returns failed transactions
     * by returning an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] outageEventChangedNotification(com.cannontech.multispeak.deploy.service.OutageEvent[] oEvents) throws java.rmi.RemoteException;

    /**
     * Client notifies SCADA of a new list of points to which it would
     * like to subscribe.  This list replaces any prior lists.  SCADA returns
     * failed transactions by returning an array of errorObjects. Subscriber
     * specifies the URL to which information is to be published by sending
     * the responseURL. (The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pointSubscriptionListNotification(com.cannontech.multispeak.deploy.service.ListItem[] pointList, java.lang.String responseURL, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of changes in analog values by sending
     * an array of changed scadaAnalog objects.  SCADA returns failed transactions
     * using an array of errorObjects.  The transactionID calling parameter
     * links this published data response with the previously received Initiate
     * method call.The message header attribute 'registrationID' should be
     * added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] analogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of changes in point status by sending
     * an array of changed scadaStatus objects.  SCADA returns failed transactions
     * using an array of errorObjects.  The transactionID calling parameter
     * links this published data response with the previously received Initiate
     * method call. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] statusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Client notifies DAC of changes in analog values by sending
     * an array of changed scadaAnalog objects.  DAC returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in point status by sending an
     * array of changed scadaStatus objects.  DAC returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of changes in accumulators by
     * sending an array of changed accumulatedValue objects.  DAC returns
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] accumulatedValueChangedNotification(com.cannontech.multispeak.deploy.service.AccumulatedValue[] accumulators) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in SCADA point definitions by
     * sending an array of changed scadaPoint objects.  DAC returns failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in SCADA point definitions, limited
     * to Analog points, by sending an array of changed scadaPoint objects.
     * DAC returns failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in SCADA point definitions, limited
     * to Status points, by sending an array of changed scadaPoint objects.
     * DAC returns failed transactions using an array of errorObjects.The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in a specific analog value, chosen
     * by scadaPointID, by sending a changed scadaAnalog object.  If this
     * transaction fails, DAC returns information about the failure in a
     * SOAPFault.  The message header attribute 'registrationID' should be
     * added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in a specific analog value, limited
     * to power analogs, by sending an arrray of changed scadaAnalog objects.
     * DAC returns failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changed analog values, limited to voltage
     * analogs, by sending an array of changed scadaAnalog objects.  DAC
     * returns failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies DAC of changes in the status of a specific point,
     * chosen by PointID, by sending a changed scadaStatus object.  If this
     * transaction fails, DAC returns information about the failure in a
     * SOAPFault. The message header attribute 'registrationID' should be
     * added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;

    /**
     * THIS METHOD IS INCLUDED FOR DISCUSSION ONLY; IT SHOULD NOT
     * BE IMPLEMENTED UNTIL THE TECHNICAL COMMITTEE ACCEPTS THE FORM OF THE
     * METHOD. DAC notifies SCADA that a previously requested scada control
     * action request has been completed.  SCADA returns the status of the
     * control action in a modified scadaControl object, containing an updated
     * controlStatus element. The transactionID parameter is used to link
     * the InitiateControl and ControlActionCompleted method calls.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] controlActionCompleted(com.cannontech.multispeak.deploy.service.ScadaControl[] controlActions, java.lang.String transactionID) throws java.rmi.RemoteException;
}
