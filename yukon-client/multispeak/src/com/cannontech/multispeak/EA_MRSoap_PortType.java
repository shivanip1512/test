/**
 * EA_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface EA_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of EA to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status. (Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * MR requests list of methods supported by EA. (Req)
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
     * Returns all substation names.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfString getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements in the shortest route to source from
     * the given line section or node (eaLoc). (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately fed by the given line
     * section or node (eaLoc). (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately upstream of the given
     * line section or node (eaLoc). (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException;

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
     * calls. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters downline from
     * a given meterNo. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Finds the first upline distribution transformer from a given
     * meter number and returns the meter connectivity for all meters cnnected
     * to it. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters on the same transformer
     * as the given meter number. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;
}
