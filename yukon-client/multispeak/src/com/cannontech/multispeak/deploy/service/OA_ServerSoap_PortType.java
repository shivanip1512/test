/**
 * OA_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface OA_ServerSoap_PortType extends java.rmi.Remote {

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
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent obtained
     * earlier by calling GetActiveOutages.
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

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
     * The client requests from the OA to reserve an outage number.
     * The OA returns a reserved outage number in the form of a requestedNumber.
     * (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumber(com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensions, java.lang.String numberType) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a change in OutageDetectionEvents
     * by sending an array of changed OutageDetectionEvent objects.  OA returns
     * information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODEventNotification(com.cannontech.multispeak.deploy.service.OutageDetectionEvent[] ODEvents, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a change in OutageDetectionDevice
     * by sending an array of changed OutageDetectionDevice objects.  OA
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODDeviceChangedNotification(com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] ODDevices) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of new power monitor output by sending
     * the new PMChangedNotification.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] PMChangedNotification(com.cannontech.multispeak.deploy.service.PowerMonitor[] monitors) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of non-outage events by sending the a
     * customerCall object.
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
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in point status by sending
     * an array of changed scadaStatus objects.  OA returns failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions
     * by sending an array of changed scadaPoint objects.  OA returns failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions,
     * limited to Analog points, by sending an array of changed scadaPoint
     * objects.  OA returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in SCADA point definitions,
     * limited to Status points, by sending an array of changed scadaPoint
     * objects.  OA returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in a specific analog value,
     * chosen by scadaPointID, by sending a changed scadaAnalog object. 
     * If this transaction fails, OA returns information about the failure
     * in a SOAPFault.
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in a specific analog value,
     * limited to power analogs, by sending an arrray of changed scadaAnalog
     * objects.  OA returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changed analog values, limited to
     * voltage analogs, by sending an array of changed scadaAnalog objects.
     * OA returns failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in the status of a specific
     * point, chosen by PointID, by sending a changed scadaStatus object.
     * If this transaction fails, OA returns information about the failure
     * in a SOAPFault.
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of changes in connect disconnect event
     * object(s) by sending the changed connectDisconnectEvent object(s).
     * OA returns information about failed transactions in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] connectDisconnectChangedNotification(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] changedCDEvents) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state change for a connect/disconnect device
     * By meterNumber and loadActionCode.  The transactionID calling parameter
     * can be used to link this action with an InitiateConectDisconnect call.
     * If this transaction fails, CB returns information about the failure
     * in a SOAPFault.
     */
    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.LoadActionCode stateChange, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of new AVL events by sending an AVLMessage
     * object.  OA returns information on failed transactions by returrning
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] AVLChangedNotification(com.cannontech.multispeak.deploy.service.AVLLocation[] events) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of an outage that should be denoted as
     * being restored, given an outage event ID.  OA returns information
     * on failed transactions by returrning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject restoreOutage(java.lang.String outageEventID, java.util.Calendar eventTime) throws java.rmi.RemoteException;

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
     * changes or backfeeds by performing switching operations.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] setOutageElementStatus(java.lang.String troubledElement, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Customer object by
     * sending the changed customer object(s).  CD returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Service Location object
     * by sending the changed serviceLocation object(s).  CD returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Meter object by sending
     * the changed meter object(s).  CD returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to remove the associated meter(s).  MR
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated meter(s)have been
     * retired from the system. MR returns information about failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to Add the associated meter(s).MR returns
     * information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that meter(s) have been deployed or exchanged.
     * MR returns information about failed transactions in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA that a previously reserved outage number
     * is being returned. OA returns failed transactions using an errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject returnGeneratedNumber(com.cannontech.multispeak.deploy.service.RequestedNumber requestedNum) throws java.rmi.RemoteException;

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
}
