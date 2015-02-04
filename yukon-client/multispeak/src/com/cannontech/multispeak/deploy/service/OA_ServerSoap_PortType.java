/**
 * OA_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface OA_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Finds the first upline distribution transformer from a given
     * meter number and returns the meter connectivity for all meters cnnected
     * to it.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters on the same transformer
     * as the given meter number, including the meter being requested.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all information for circuit elements fed by a given
     * line section or node.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the index number provided by the server as being
     * the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getDownlineConnectivity(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all information for circuit elements in the shortest
     * route to source from the given line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getUplineConnectivity(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all information for circuit elements immediately fed
     * by the given line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getChildConnectivity(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all information for circuit elements immediately upstream
     * of the given line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getParentConnectivity(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all information for all elements in the connectivity
     * model. The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string first time in a session that this method is
     * invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the index number provided by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getAllConnectivity(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements that have been modified since the
     * time of a specifed sessionID. The calling parameter lastReceived is
     * included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string first time in a
     * session that this method is invoked.  When multiple calls to this
     * method are required to obtain all of the data, the lastReceived should
     * carry in subsequent calls the index number provided by the server
     * as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getModifiedConnectivity(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Requester pings URL of OA to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by OA.
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
     * Returns the current status of an outage event, given the outage
     * event ID.
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the outage event, if any, associated with a circuitElement
     * given the .  The outageEventID is the objectID of an outageEvent obtained
     * earlier by calling GetActiveOutages.
     */
    public com.cannontech.multispeak.deploy.service.OutageEvent getOutageByCircuitElement(com.cannontech.multispeak.deploy.service.CircuitElement cktElement) throws java.rmi.RemoteException;

    /**
     * Returns the outage event, if any, associated with a circuitElement
     * given the objectRef of the circuitElement.  The outageEventID is the
     * objectID of an outageEvent obtained earlier by calling GetActiveOutages.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElementStatus getCircuitElementStatus(com.cannontech.multispeak.deploy.service.ObjectRef cktElementRef) throws java.rmi.RemoteException;

    /**
     * Returns an array of circuitElements that lie within the distance
     * tolerance of the location expressed in latitude and longitude.  If
     * many circuitElements are found within the distance tolerance, then
     * the server shall return the maximum number of circuitElements expressed
     * as numCEs.  If it is necessary to drop some circuitElements, the server
     * should drop those furthest from the latitude/longitude location specified
     * in the calling parameter list.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElementAndDistance[] getCircuitElementNearLatLong(double latitude, double longitude, com.cannontech.multispeak.deploy.service.LengthUnitValue tolerance, int numCEs) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.
     */
    public java.lang.String[] getActiveOutages() throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * Returns the current outage message prompt list.  The requester
     * system can store these messages and play them back to callers on demand,
     * based on the OutageEventID.
     */
    public com.cannontech.multispeak.deploy.service.Message[] getOutageMessagePromptList() throws java.rmi.RemoteException;

    /**
     * Returns the current outage status of a customer location, given
     * the outageLocation.  The outageLocation object includes the telephone
     * number, service locationID, account number and/or meter number at
     * the location of the outage.
     */
    public com.cannontech.multispeak.deploy.service.LocationStatus getOutageStatusByLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * Returns the outageDetectionDevices that are currently experiencing
     * outage conditions.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutagedODDevices() throws java.rmi.RemoteException;

    /**
     * Returns all active crews that are available for dispatching
     * if the parameter activeOnly is set to be true, otherwise all crews
     * are returned.  The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls. If the sessionID
     * parameter is set in the message header, then the server shall respond
     * as if it were being asked for a GetModifiedXXX since that sessionID;
     * if the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.Crew[] getAllCrews(boolean activeOnly, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all of the outageEvent(s) for all active outages. 
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls. If the sessionID
     * parameter is set in the message header, then the server shall respond
     * as if it were being asked for a GetModifiedXXX since that sessionID;
     * if the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.OutageEvent[] getAllActiveOutageEvents(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the outageEvent for the given outageEventID.  This
     * can be either an active or a historical outage event.  The outageEventID
     * is the objectID for the outageEvent object.
     */
    public com.cannontech.multispeak.deploy.service.OutageEvent getOutageEvent(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns all customers that are affected by a specfic outage
     * of interest, given the outageEventID. The outageEventID is the objectID
     * for the outageEvent object.
     */
    public com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage getCustomersAffectedByOutage(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns all active calls that have been processed by the outage
     * management system in the form of an outageDetectionLogList.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received in subsequent calls. If the sessionID parameter is set in
     * the message header, then the server shall respond as if it were being
     * asked for a GetModifiedXXX since that sessionID; if the sessionID
     * is not included in the method call, then all instances of the object
     * shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getAllActiveCalls(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all calls that have been processed by the outage management
     * system in the form of an outageDetectionLogList.  The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCallsReceivedOnOutage(java.lang.String outageEventID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all outage duration events that have been processed
     * by the outage management system for a given customer account and service
     * location in the form of an outageDurationEventList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getCustomerOutageHistory(java.lang.String accountNumber, java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns all calls that have been processed by the outage management
     * system for a given customer account and service location in the form
     * of an outageDetectionLogList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCustomerCallHistory(java.lang.String accountNumber, java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns all outage duration events that have been processed
     * by the outage management system for a given outage identified by outageEventID,
     * returned in the form of an outageDurationEventList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageDurationEvents(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns all outage duration events that have been processed
     * by the outage management system for a given service location in the
     * form of an outageDurationEventList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDurationEvent[] getOutageHistoryOnServiceLocation(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns all calls that have been processed by the outage management
     * system for a given service location in the form of an outageDetectionLogList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCustomerCallsOnServiceLocation(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the assessmentLocationIDs for all of the active locations
     * for workers to assess storm damage.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public java.lang.String[] getActiveAssessmentLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the locations for storm assessment, given a list of
     * assessmentLocation objectIDs.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.AssessmentLocation[] getAssessmentLocations(java.lang.String[] ALIDs) throws java.rmi.RemoteException;

    /**
     * Returns the list of outage reason codes used by the OMS implementation.
     */
    public com.cannontech.multispeak.deploy.service.OutageReasonContainer getOutageReasonCodes() throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a change in OutageDetectionEvents
     * by sending an array of changed OutageDetectionEvent objects.  OA returns
     * information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODEventNotification(com.cannontech.multispeak.deploy.service.OutageDetectionEvent[] ODEvents, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a change in OutageDetectionDevice
     * by sending an array of changed OutageDetectionDevice objects.  OA
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODDeviceChangedNotification(com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] ODDevices) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of new power monitor output by sending
     * the new PMChangedNotification. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] PMChangedNotification(com.cannontech.multispeak.deploy.service.PowerMonitor[] monitors) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of non-outage events by sending the a
     * customerCall object. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CHEventNotification(com.cannontech.multispeak.deploy.service.CustomerCall[] chEvent) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a list of customer calls to close
     * out.  OA returns status of failed transactions in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] closeCalls(com.cannontech.multispeak.deploy.service.CustomerCall[] oldCalls) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in analog values by sending
     * an array of changed scadaAnalog objects.  OA returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in point status by sending
     * an array of changed scadaStatus objects.  OA returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions
     * by sending an array of changed scadaPoint objects.  OA returns failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions,
     * limited to Analog points, by sending an array of changed scadaPoint
     * objects.  OA returns failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions,
     * limited to Status points, by sending an array of changed scadaPoint
     * objects.  OA returns failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in a specific analog value,
     * chosen by scadaPointID, by sending a changed scadaAnalog object. 
     * If this transaction fails, OA returns information about the failure
     * in a SOAPFault. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in a specific analog value,
     * limited to power analogs, by sending an arrray of changed scadaAnalog
     * objects.  OA returns failed transactions using an array of errorObjects.The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changed analog values, limited to
     * voltage analogs, by sending an array of changed scadaAnalog objects.
     * OA returns failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in the status of a specific
     * point, chosen by PointID, by sending a changed scadaStatus object.
     * If this transaction fails, OA returns information about the failure
     * in a SOAPFault. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in connect disconnect event
     * object(s) by sending the changed connectDisconnectEvent object(s).
     * OA returns information about failed transactions in an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] connectDisconnectChangedNotification(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] changedCDEvents) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of state change for a connect/disconnect
     * device By meterNumber and loadActionCode.  The transactionID calling
     * parameter can be used to link this action with an InitiateConectDisconnect
     * call.  If this transaction fails, CB returns information about the
     * failure in a SOAPFault. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.LoadActionCode stateChange, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state change(s) for connect/disconnect device(s).
     * The transactionID calling parameter can be used to link this action
     * with an InitiateConectDisconnect call.  If this transaction fails,
     * CB returns information about the failure in an array of errorObject(s).
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesChangedNotification(com.cannontech.multispeak.deploy.service.CDStateChange[] stateChanges, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of new AVL events by sending
     * an AVLMessage object.  DGV returns information on failed transactions
     * by returrning an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] AVLChangedNotification(com.cannontech.multispeak.deploy.service.AVLLocation[] events, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of an outage that should be denoted as
     * being restored, given an outage event ID.  OA returns information
     * on failed transactions by returrning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject restoreOutage(java.lang.String outageEventID, java.util.Calendar eventTime, boolean callBackCustomersThatCalled, com.cannontech.multispeak.deploy.service.OutageReasonContainer outageCause, java.lang.String dispatcherResponsible) throws java.rmi.RemoteException;

    /**
     * Assigns crews to an outage given the outage event ID.  OA returns
     * information on failed transactions by returrning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject assignCrewsToOutage(java.lang.String outageEventID, java.lang.String[] crewsAssigned, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * Unassigns crew(s) from an outage given the outage event ID.
     * Server returns information on failed transactions by returrning an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject unassignCrewsFromOutage(java.lang.String outageEventID, java.util.Calendar eventTime, java.lang.String reason, java.lang.String[] crewsUnassigned, java.lang.String comment) throws java.rmi.RemoteException;

    /**
     * Unassigns outages(s) from a crew given the crew ID. Server
     * returns information on failed transactions by returrning an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject unassignOutagesFromCrew(java.lang.String crewID, java.util.Calendar eventTime, java.lang.String reason, java.lang.String[] outageEventIDs, java.lang.String comment) throws java.rmi.RemoteException;

    /**
     * Allows a system operator to add a remark to an outage event.
     * OA returns information on failed transactions by returrning an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutage(java.lang.String outageEventID, java.lang.String remarks, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * This method allows a dispatcher or operator to verify or restore
     * any circuit element by phase.  This controls how the outage analysis
     * behaves if the outage is assumed.  It also allows for connectivity
     * changes or backfeeds by performing switching operations. It is recommended
     * that the dispatcher verify current status using GetOutageElementStatus
     * prior to calling this method to change the outage element status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] setOutageElementStatus(java.lang.String troubledElement, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC, java.util.Calendar eventTime, java.lang.String dispatcherResponsible) throws java.rmi.RemoteException;

    /**
     * This method allows a dispatcher or operator to discard an outage
     * that has been created erroneously or which was generated for training
     * purposes.  Subscriber can return information about erros using an
     * errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] discardOutage(java.lang.String outageEventID, java.util.Calendar eventTime, java.lang.String dispatcherResponsible, java.lang.String reason) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber of a change in the Customer object
     * by sending the changed customer object(s).  Subscriber returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber of a change in the Service Location
     * object by sending the changed serviceLocation object(s).  Subscriber
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber of a change in the Meter object
     * by sending the changed meter object(s).  Subscriber returns information
     * about failed transactions using an array of errorObjects.The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriberto remove the associated meter(s).
     * Subscriber returns information about failed transactions using an
     * array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that the associated meter(s)have
     * been retired from the system. Subscriber returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber to add the associated meter(s).
     * Subscriber returns information about failed transactions using an
     * array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that meter(s) have been deployed
     * or exchanged. A meterExchange shall be a paired transaction of a meter
     * being removed and a meter being installed in the same meter base.
     * Subscriber returns information about failed transactions in an array
     * of errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of power line cut that should be initiated.
     * OA returns information on failed transactions by returrning an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject initiateCut(com.cannontech.multispeak.deploy.service.SwitchDeviceBank newCut, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a cut that should be restored.  OA
     * returns information on failed transactions by returrning an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject restoreCut(com.cannontech.multispeak.deploy.service.SwitchDeviceBank restoreCut, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state of a connect/disconnect device.  The
     * transactionID calling parameter can be used to link this action with
     * an InitiateCDStateRequest call.  If this transaction fails, CB returns
     * information about the failure in a SOAPFault. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public void CDStateNotification(com.cannontech.multispeak.deploy.service.CDState state, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of state of connect/disconnect
     * device(s).  The transactionID calling parameter can be used to link
     * this action with an InitiateCDStateRequest call.  If this transaction
     * fails, CB returns information about the failure in an array of errorObject(s).
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesNotification(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber that an unresolved caller is
     * now resolved by the dispatcher.  Subscriber returns status of failed
     * transactions in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] resolvedCaller(com.cannontech.multispeak.deploy.service.CustomerCall[] resolvedCallers) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber that a call message was listened.
     * Subscriber returns status of failed transactions in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] updateMessageStatus(com.cannontech.multispeak.deploy.service.Message[] updatedMessages) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of new assessmentLocation(s).
     * The transactionID calling parameter can be used to link this action
     * with an asynchronous request, if any.  If this transaction fails,
     * Subscriber returns information about the failure in an array of errorObject(s).
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] assessmentLocationChangedNotification(com.cannontech.multispeak.deploy.service.AssessmentLocation[] locations, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of the causes and other information
     * related to an outage event. Subscriber returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] outageReasonChangedNotification(java.lang.String outageEventID, com.cannontech.multispeak.deploy.service.OutageReasonCodeList reasonCodes, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of changes in the cause codes
     * that may be used to describe outage events at this installation. Subscriber
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] outageReasonContainerChangedNotification(com.cannontech.multispeak.deploy.service.OutageReasonContainer reasons, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of a change in the History Log
     * by sending the changed historyLog object.  MDM returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] historyLogChangedNotification(com.cannontech.multispeak.deploy.service.HistoryLog[] changedHistoryLogs) throws java.rmi.RemoteException;

    /**
     * Returns all substation names.
     */
    public java.lang.String[] getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements in the shortest route to source from
     * the given line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately fed by the given line
     * section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately upstream of the given
     * line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements that have been modified since
     * the previous session identified. The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, lastReceived should
     * carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters downline from
     * a given meterNo.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;
}
