package com.cannontech.multispeak.service;

import com.cannontech.msp.beans.v3.CDDevice;
import com.cannontech.msp.beans.v3.CDDeviceExchange;
import com.cannontech.msp.beans.v3.CDState;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface CD_Server {
    /**
     * Requester Pings URL of CD to see if it is alive. Returns
     * errorObject(s) as necessary to communicate application status.
     * 
     * @throws MultispeakWebServiceException
     */
    public ErrorObject[] pingURL() throws MultispeakWebServiceException;

    /**
     * Requester requests list of methods supported by CD.
     */
    public String[] getMethods() throws MultispeakWebServiceException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server. This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server.
     */
    public String[] getDomainNames() throws MultispeakWebServiceException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server. This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server.
     */
    public DomainMember[] getDomainMembers(String domainName) throws MultispeakWebServiceException;

    /**
     * This service requests of the publisher a unique registration
     * ID that would subsequently be used to refer unambiguously to that
     * specific subscription. The return parameter is the registrationID,
     * which is a string-type value. It is recommended that the server not
     * implement registration in such a manner that one client can guess
     * the registrationID of another. For instance the use of sequential
     * numbers for registrationIDs is discouraged.
     */
    public String requestRegistrationID() throws MultispeakWebServiceException;

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
     * would like to subscribe. Subsequent calls to RegisterForService on
     * an existing subscription replace prior subscription details in their
     * entirety - they do NOT add to an existing subscription.
     */
    public ErrorObject[] registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException;

    /**
     * This method deletes a previously established subscription (registration
     * for service) that carries the registration identifer listed in the
     * input parameter registrationID.
     */
    public ErrorObject[] unregisterForService(String registrationID) throws MultispeakWebServiceException;

    /**
     * This method requests the return of existing registration information
     * (that is to say the details of what is subscribed on this subscription)
     * for a specific registrationID. The server should return a SOAPFault
     * if the registrationID is not valid.
     */
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException;

    /**
     * Requester requests list of methods to which this server can
     * publish information.
     */
    public String[] getPublishMethods() throws MultispeakWebServiceException;

    /**
     * This method permits a client to have changed information on
     * domain members published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainMembersChangedNotification as one of the methods in the list
     * of methods to which the client has subscribed. The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the domainMember information.
     */
    public ErrorObject[] domainMembersChangedNotification(DomainMember[] changedDomainMembers)
            throws MultispeakWebServiceException;

    /**
     * This method permits a client to have changed information on
     * domain names published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainNamesChangedNotification as one of the methods in the list of
     * methods to which the client has subscribed. The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the domainName information.
     */
    public ErrorObject[] domainNamesChangedNotification(DomainNameChange[] changedDomainNames)
            throws MultispeakWebServiceException;

    /**
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks. lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked. When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public Meter[] getCDSupportedMeters(String lastReceived) throws MultispeakWebServiceException;

    /**
     * Returns all meters that have Connect/Disconnect Capability
     * and that have been modified since the last identified session. The
     * calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks. lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked. When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public Meter[] getModifiedCDMeters(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number. The default condition is Closed.
     */
    public LoadActionCode getCDMeterState(String meterNo) throws MultispeakWebServiceException;

    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD. CD returns information
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
    public ErrorObject[] initiateConnectDisconnect(ConnectDisconnectEvent[] cdEvents, String responseURL,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException;

    /**
     * CB initiates a switch status check directly from one or more
     * Connect/Disconnect devices. CD returns information about failed transactions
     * by returning an array of errorObjects. The CD switch state check
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
    public ErrorObject[] initiateCDStateRequest(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException;

    /**
     * CB initiates arming of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects. The CD function returns information asynchronously
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
    public ErrorObject[] initiateArmCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException;

    /**
     * CB initiates enabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects. The CD function returns information asynchronously
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
    public ErrorObject[] initiateEnableCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException;

    /**
     * CB initiates disabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects. The CD function returns information asynchronously
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
    public ErrorObject[] initiateDisableCDDevice(CDState[] states, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException;

    /**
     * Publisher Notifies CD of a change in the Customer object by
     * sending the changed customer object(s). CD returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers) throws MultispeakWebServiceException;

    /**
     * Publisher Notifies CD of a change in the Service Location object
     * by sending the changed serviceLocation object(s). CD returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public ErrorObject[] serviceLocationChangedNotification(ServiceLocation[] changedServiceLocations)
            throws MultispeakWebServiceException;

    /**
     * Publisher Notifies CD of a change in the Meter object by sending
     * the changed meter object(s). CD returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public ErrorObject[] meterChangedNotification(Meter[] changedMeters) throws MultispeakWebServiceException;

    /**
     * Publisher notifies CD to add the associated connect/disconnect
     * device(s). CD returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public ErrorObject[] CDDeviceAddNotification(CDDevice[] addedCDDs) throws MultispeakWebServiceException;

    /**
     * Publisher notifies CD of a change in connect/disconnect device(s).
     * CD returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public ErrorObject[] CDDeviceChangedNotification(CDDevice[] changedCDDs) throws MultispeakWebServiceException;

    /**
     * Publisher notifies CD that connect/disconnect device(s) have
     * been deployed or exchanged. CD returns information about failed transactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public ErrorObject[] CDDeviceExchangeNotification(CDDeviceExchange[] CDDChangeout)
            throws MultispeakWebServiceException;

    /**
     * Publisher notifies CD to remove the associated connect/disconnect
     * device(s). CD returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public ErrorObject[] CDDeviceRemoveNotification(CDDevice[] removedCDDs) throws MultispeakWebServiceException;

    /**
     * Publisher notifies CD that the associated connect/disconnect
     * devices(s)have been retired from the system. CD returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public ErrorObject[] CDDeviceRetireNotification(CDDevice[] retiredCDDs) throws MultispeakWebServiceException;
}
