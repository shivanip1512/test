/**
 * OA_SCADASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface OA_SCADASoap_PortType extends java.rmi.Remote {

    /**
     * SCADA Pings URL of OA to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * SCADA requests list of methods supported by OA. (Required)
     */
    public java.lang.String[] getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Optional)
     */
    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent obtained
     * earlier using the GetActiveOutages method.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.(Optional)
     */
    public java.lang.String[] getActiveOutages() throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * Returns all active crews that are available for dispatching
     * if the parameter activeOnly is set to be true, otherwise all crews
     * are returned.  The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.Crew[] getAllCrews(boolean activeOnly, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all of the outageEvent(s) for all active outages. 
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.
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
     * received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getAllActiveCalls(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all calls that have been processed by the outage management
     * system in the form of an outageDetectionLogList.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionLog[] getCallsReceivedOnOutage(java.lang.String outageEventID) throws java.rmi.RemoteException;

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
     * SCADA Notifies OA of changes in analog values by sending an
     * array of changed scadaAnalog objects.  OA returns failed transactions
     * using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in point status by sending an
     * array of changed scadaStatus objects.  OA returns failed transactions
     * using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in SCADA point definitions by
     * sending an array of changed scadaPoint objects.  OA returns failed
     * transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in SCADA point definitions, limited
     * to Analog points, by sending an array of changed scadaPoint objects.
     * OA returns failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in SCADA point definitions, limited
     * to Status points, by sending an array of changed scadaPoint objects.
     * OA returns failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in a specific analog value, chosen
     * by scadaPointID, by sending a changed scadaAnalog object.  If this
     * transaction fails, OA returns information about the failure in a SOAPFault.
     * (Optional)
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in a specific analog value, limited
     * to power analogs, by sending an arrray of changed scadaAnalog objects.
     * OA returns failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changed analog values, limited to voltage
     * analogs, by sending an array of changed scadaAnalog objects.  OA returns
     * failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies OA of changes in the status of a specific point,
     * chosen by PointID, by sending a changed scadaStatus object.  If this
     * transaction fails, OA returns information about the failure in a SOAPFault.(Optional)
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of an outage that should be denoted
     * as being restored, given an outage event ID.  OA returns information
     * on failed transactions by returrning an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject restoreOutage(java.lang.String outageEventID, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * Assigns crews to an outage given the outage event ID.  OA returns
     * information on failed transactions by returrning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject assignCrewsToOutage(java.lang.String outageEventID, java.lang.String[] crewsAssigned, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * Allows a system operator to add a remark to an outage event.
     * OA returns information on failed transactions by returrning an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject addRemarkToOutage(java.lang.String outageEventID, java.lang.String remarks, java.util.Calendar eventTime) throws java.rmi.RemoteException;

    /**
     * This method allows a dispatcher or operator to verify or restore
     * any circuit element by phase.  This controls how the outage analysis
     * behaves if the outage is of assumed status.  It also allows for connectivity
     * changes or backfeeds by performing switching operations.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] setOutageElementStatus(java.lang.String troubledElement, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseA, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseB, com.cannontech.multispeak.deploy.service.OutageElementStatus statusPhaseC, java.util.Calendar eventTime) throws java.rmi.RemoteException;

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
}
