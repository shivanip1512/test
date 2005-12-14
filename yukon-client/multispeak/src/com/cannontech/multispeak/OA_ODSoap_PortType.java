/**
 * OA_ODSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface OA_ODSoap_PortType extends java.rmi.Remote {

    /**
     * OD pings URL of OA to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status. (Required)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * OD requests list of methods supported by OA. (Required)
     */
    public com.cannontech.multispeak.ArrayOfString getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfString getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent obtained
     * earlier by calling GetActiveOutages.(Optional)
     */
    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.(Optional)
     */
    public com.cannontech.multispeak.ArrayOfString getActiveOutages() throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.(Optional)
     */
    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * OD Notifies OA of a change in OutageDetectionEvents by sending
     * an array of changed OutageDetectionEvent objects.  OA returns information
     * about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject ODEventNotification(com.cannontech.multispeak.ArrayOfOutageDetectionEvent ODEvents) throws java.rmi.RemoteException;

    /**
     * OD Notifies OA of a change in OutageDetectionDevice by sending
     * an array of changed OutageDetectionDevice objects.  OA returns information
     * about failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject ODDeviceChangedNotification(com.cannontech.multispeak.ArrayOfOutageDetectionDevice ODDevices) throws java.rmi.RemoteException;

    /**
     * Returns all substation names.(Optional)
     */
    public com.cannontech.multispeak.ArrayOfString getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.(Optional)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements in the shortest route to source from
     * the given line section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately fed by the given line
     * section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately upstream of the given
     * line section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. (Optional)
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
     * calls. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfCircuitElement getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters downline from
     * a given meterNo. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Finds the first upline distribution transformer from a given
     * meter number and returns the meter connectivity for all meters cnnected
     * to it. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters on the same transformer
     * as the given meter number. (Optional)
     */
    public com.cannontech.multispeak.ArrayOfMeterConnectivity getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;
}
