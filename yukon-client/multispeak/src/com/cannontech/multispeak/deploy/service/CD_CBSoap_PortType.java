/**
 * CD_CBSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface CD_CBSoap_PortType extends java.rmi.Remote {

    /**
     * CB Pings URL of CD to see if it is alive.   Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * CB requests list of methods supported by CD. (Required)
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
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent. (Recommended)
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
     * lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number.  The default condition is Closed. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD.  CD returns information
     * about failed transactions by returning an array of errorObjects. 
     * The connect/disconnect function returns infromation about this action
     * using the CDStateChangedNotification to the URL specified in the responseURL
     * calling parameter and references the transactionID specified to link
     * the transaction to this Initiate request.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateConnectDisconnect(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Customer object by sending
     * the changed customer object(s).  CD returns information about failed
     * transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Service Location object by
     * sending the changed serviceLocation object(s).  CD returns information
     * about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Meter object by sending the
     * changed meter object(s).  CD returns information about failed transactions
     * using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD to add the associated connect/disconnect
     * device(s). CD returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceAddNotification(com.cannontech.multispeak.deploy.service.CDDevice[] addedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD of a change in connect/disconnect device(s).
     * CD returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceChangedNotification(com.cannontech.multispeak.deploy.service.CDDevice[] changedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD that connect/disconnect device(s) have
     * been deployed or exchanged.  CD returns information about failed transactions
     * in an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.CDDeviceExchange[] CDDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD to remove the associated connect/disconnect
     * device(s).  CD returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.CDDevice[] removedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CD that the associated connect/disconnect
     * devices(s)have been retired from the system.  CD returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRetireNotification(com.cannontech.multispeak.deploy.service.CDDevice[] retiredCDDs) throws java.rmi.RemoteException;
}
