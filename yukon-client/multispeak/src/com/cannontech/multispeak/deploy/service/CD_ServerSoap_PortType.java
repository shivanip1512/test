/**
 * CD_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface CD_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Requester Pings URL of CD to see if it is alive.   Returns
     * errorObject(s) as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by CD.
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
     * calling RequestRegistrationID, responseURL – the URL to which information
     * should subsequently be published on this subscription, msFunction
     * – the abbreviated string name of the MultiSpeak method making the
     * subscription request (for instance, if an application that exposes
     * the Meter Reading function has made the request, then the msFunction
     * variable should include “MR�?), methodsList – An array of strings that
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
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have Connect/Disconnect Capability
     * and that have been modified since the last identified session.  The
     * calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number.  The default condition is Closed.
     */
    public com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD.  CD returns information
     * about failed transactions by returning an array of errorObjects. 
     * The connect/disconnect function returns infromation about this action
     * using the CDStateChangedNotification to the URL specified in the responseURL
     * calling parameter and references the transactionID specified to link
     * the transaction to this Initiate request.The expiration time parameter
     * indicates the amount of time for which the publisher should try to
     * obtain and publish the data; if the publisher has been unsuccessful
     * in publishing the data after the expiration time (specified in seconds),
     * then the publisher will discard the request and the requestor should
     * not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateConnectDisconnect(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates a switch status check directly from one or more
     * Connect/Disconnect devices. CD returns information about failed transactions
     * by returning an array of errorObjects.  The CD switch state check
     * function returns information asynchronously about this switch state
     * using either the CDStateNotification (for only one device) or the
     * CDStatesNotification (for one or more devices) to the URL specified
     * in the responseURL calling parameter and references the transactionID
     * specified to link the transaction to this Initiate request.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateCDStateRequest(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates arming of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateArmCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates enabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateEnableCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates disabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDisableCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Customer object by
     * sending the changed customer object(s).  CD returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Service Location object
     * by sending the changed serviceLocation object(s).  CD returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CD of a change in the Meter object by sending
     * the changed meter object(s).  CD returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD to add the associated connect/disconnect
     * device(s). CD returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceAddNotification(com.cannontech.multispeak.deploy.service.CDDevice[] addedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD of a change in connect/disconnect device(s).
     * CD returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceChangedNotification(com.cannontech.multispeak.deploy.service.CDDevice[] changedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD that connect/disconnect device(s) have
     * been deployed or exchanged.  CD returns information about failed transactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.CDDeviceExchange[] CDDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD to remove the associated connect/disconnect
     * device(s).  CD returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.CDDevice[] removedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD that the associated connect/disconnect
     * devices(s)have been retired from the system.  CD returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRetireNotification(com.cannontech.multispeak.deploy.service.CDDevice[] retiredCDDs) throws java.rmi.RemoteException;
}
