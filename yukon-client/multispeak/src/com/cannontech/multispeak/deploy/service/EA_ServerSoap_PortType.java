/**
 * EA_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface EA_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Requester pings URL of EA to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by EA.
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
     * Returns all load flow analysis results.  The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.LoadFlowResult[] getAllLoadFlowResults(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all short circuit analysis results.  The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult[] getAllShortCircuitAnalysisResults(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a specific set of load flow analysis results by results
     * objectID.
     */
    public com.cannontech.multispeak.deploy.service.LoadFlowResult getLoadFlowResultsByObjectID(java.lang.String objectID) throws java.rmi.RemoteException;

    /**
     * Returns a specific set of short circuit analysis results by
     * results objectID.
     */
    public com.cannontech.multispeak.deploy.service.ShortCircuitAnalysisResult getShortCircuitAnalysisResultsByObjectID(java.lang.String objectID) throws java.rmi.RemoteException;

    /**
     * Returns all substation names.
     */
    public java.lang.String[] getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns the list of all electrical equipment names in the application's
     * equipment database.  The calling parameter, deviceType, is an enumerated
     * string that holds the type of equipment for which the client is interested
     * in receiving the allowable names.  Acceptable values for deviceType
     * include: Overhead (for all overhead line types whether primary or
     * secondary), Underground (for all underground conductor types, whether
     * primary or secondary), ZsmConductor (for the specification of a generic
     * self and mutual impedance, which may be used for source impedances
     * among other uses), ZabcConductor (for the specification of a generic
     * impedance, which may be used for source impedances among other uses),
     * Transformer (used for all transformer banks), Regulator (used for
     * all regulator banks), Device (used for switching banks and overcurrent
     * device banks) and Other (for other unspecified equipment).  Pairs
     * of vendors may add to this list of device types by agreement.
     */
    public java.lang.String[] getElectricalEquipment(com.cannontech.multispeak.deploy.service.DeviceType equipmentType) throws java.rmi.RemoteException;

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
     * the index number provided by the server as being the lastSent. If
     * the sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
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
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry in subsequent
     * calls the objectID of the data instance noted by the server as being
     * the lastSent.
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
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
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
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
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
     * This method returns the minimum set of connectivity data necessary
     * for AMR systems to group meters on portions of the power system. 
     * This method will not return a complete set of connectivity data; it
     * returns only circuit elements downline of a specific substation, overcurrentDeviceBank,
     * transformer, or serviceLoaction.  Device tree connectivity will not
     * include objects unnecessary to group meters, such as line sections.
     * The MR requests devices downline of a specific object by passing the
     * name and noun type for that object.  For instance, if the MR wishes
     * to receive the minimal device tree downline of a specific substation,
     * named the West substation, it would send name = West and noun = substation.
     * EA would return the minimal device tree downline of the West substation
     * in the form of an array of circuit elements.  The MR can find the
     * instances of the objects of interest using the GetDomainMembers method
     * passing one of the parameters: substation, overcurrentDeviceBank,
     * transformerBank, or serviceLocation.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getDeviceTreeConnectivity(java.lang.String name, java.lang.String noun) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters served from a
     * given substation.  The client requests the data by passing a substation
     * name, which it previously received using the GetSubstationNames method.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getMeterConnectivityBySubstation(java.lang.String substationName) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for a specific meter.  The client
     * requests the data by passing a meterNo.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity getMeterConnectivityByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the circuit element for a specific object (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement getCircuitElementByObject(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns the information for a specific element in the connectivity
     * model.
     */
    public com.cannontech.multispeak.deploy.service.MultiSpeak getConnectivityByObject(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Publisher notifies EA of a change in circuit elements by sending
     * a changed MultiSpeak object.  EA returns failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] circuitElementChangedNotification(com.cannontech.multispeak.deploy.service.MultiSpeak circuitElements) throws java.rmi.RemoteException;

    /**
     * Publisher publishes new meter readings to the Subscriber by
     * sending a formattedBlock object.  CB returns information about failed
     * transactions in an array of errorObjects.The transactionID calling
     * parameter links this Initiate request with the published data method
     * call. The message header attribute 'registrationID' should be added
     * to all publish messages to indicate to the subscriber under which
     * registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] formattedBlockNotification(com.cannontech.multispeak.deploy.service.FormattedBlock changedMeterReads, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies EA of a new or modified work order by sending
     * a changed work order object.  EA returns failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] workOrderChangedNotification(com.cannontech.multispeak.deploy.service.WorkOrder workOrder) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies EA of a change in connectivity by sending
     * a changed MultiSpeak object.  EA returns failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] connectivityChangedNotification(com.cannontech.multispeak.deploy.service.MultiSpeak connectivity) throws java.rmi.RemoteException;

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
     * Publisher notifies MR to remove the associated meter(s).  MR
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated meter(s)have been
     * retired from the system.  MR returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to Add the associated meter(s).MR returns
     * information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that meter(s) have been deployed or exchanged.A
     * meterExchange shall be a paired transaction of a meter being removed
     * and a meter being installed in the same meter base.  MR returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;
}
