/**
 * CD_CBSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface CD_CBSoap_PortType extends java.rmi.Remote {

    /**
     * CB Pings URL of CD to see if it is alive.   Returns errorObject(s)
     * as necessary to communicate application status.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * CB requests list of methods supported by CD. (Req)
     */
    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls. (Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have Connect/Disconnect Capability
     * and that have been modified since the last identified session.  The
     * calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeter getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number.  The default condition is Closed. (Req)
     */
    public com.cannontech.multispeak.LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD.  CD returns information
     * about failed transactions by returning an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiateConnectDisconnect(com.cannontech.multispeak.ArrayOfConnectDisconnectEvent cdEvents) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Customer object by sending
     * the changed customer object(s).  CD returns information about failed
     * transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Service Location object by
     * sending the changed serviceLocation object(s).  CD returns information
     * about failed transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * CB Notifies CD of a change in the Meter object by sending the
     * changed meter object(s).  CD returns information about failed transactions
     * using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter changedMeters) throws java.rmi.RemoteException;
}
