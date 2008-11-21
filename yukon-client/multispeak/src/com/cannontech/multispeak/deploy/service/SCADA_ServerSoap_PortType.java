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
     * Client requests that SCADA publish the status of SCADA points,
     * as selected by the array of SCADA pointIDs.  DAD returns information
     * about failed transactions using an array of errorObjects.  Possible
     * errorStrings could include 'Initiate already in progress for this
     * pointID' and 'Remote device unreachable'. The DAD status is returned
     * to the SCADA in the form of scadaStatus objects returned in a StatusChangedNotificationByPointID,
     * sent to the URL specified in the responseURL.  The transactionID calling
     * parameter links this Initiate request with the published data method
     * call.(Optional)
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
     * Initiate request with the published data method call.(Optional)
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
     * the InitiateControl and ControlActionCompleted method calls. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject initiateControl(com.cannontech.multispeak.deploy.service.ScadaControl controlAction, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of a change in OutageEvent by sending
     * an array of changed OutageEvent objects.  SCADA returns failed transactions
     * by returning an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] outageEventChangedNotification(com.cannontech.multispeak.deploy.service.OutageEvent[] oEvents) throws java.rmi.RemoteException;

    /**
     * Client notifies SCADA of a new list of points to which it would
     * like to subscribe.  This list replaces any prior lists.  SCADA returns
     * failed transactions by returning an array of errorObjects. Subscriber
     * specifies the URL to which information is to be published by sending
     * the responseURL. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pointSubscriptionListNotification(com.cannontech.multispeak.deploy.service.PointSubscriptionListListItem[] pointList, java.lang.String responseURL) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of changes in analog values by sending
     * an array of changed scadaAnalog objects.  SCADA returns failed transactions
     * using an array of errorObjects.  The transactionID calling parameter
     * links this published data response with the previously received Initiate
     * method call.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] analogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies SCADA of changes in point status by sending
     * an array of changed scadaStatus objects.  SCADA returns failed transactions
     * using an array of errorObjects.  The transactionID calling parameter
     * links this published data response with the previously received Initiate
     * method call.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] statusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * THIS METHOD IS INCLUDED FOR DISCUSSION ONLY; IT SHOULD NOT
     * BE IMPLEMENTED UNTIL THE TECHNICAL COMMITTEE ACCEPTS THE FORM OF THE
     * METHOD. DAC notifies SCADA that a previously requested scada control
     * action request has been completed.  SCADA returns the status of the
     * control action in a modified scadaControl object, containing an updated
     * controlStatus element. The transactionID parameter is used to link
     * the InitiateControl and ControlActionCompleted method calls. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] controlActionCompleted(com.cannontech.multispeak.deploy.service.ScadaControl[] controlActions, java.lang.String transactionID) throws java.rmi.RemoteException;
}
