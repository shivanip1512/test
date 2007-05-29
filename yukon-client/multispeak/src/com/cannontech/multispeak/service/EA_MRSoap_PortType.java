/**
 * EA_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface EA_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of EA to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status. (Required)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * MR requests list of methods supported by EA. (Required)
     */
    public com.cannontech.multispeak.service.ArrayOfString getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfDomainMember getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * MR publishes new meter readings to the EA by sending a formattedBlock
     * object.  EA returns information about failed transactions in an array
     * of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject formattedBlockNotification(com.cannontech.multispeak.service.FormattedBlock changedMeterReads) throws java.rmi.RemoteException;

    /**
     * Returns all substation names.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements in the shortest route to source from
     * the given line section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately fed by the given line
     * section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately upstream of the given
     * line section or node (eaLoc). (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters downline from
     * a given meterNo. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfMeterConnectivity getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Finds the first upline distribution transformer from a given
     * meter number and returns the meter connectivity for all meters cnnected
     * to it. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfMeterConnectivity getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters on the same transformer
     * as the given meter number. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfMeterConnectivity getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

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
     * transformerBank, or serviceLocation. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCircuitElement getDeviceTreeConnectivity(java.lang.String name, java.lang.String noun) throws java.rmi.RemoteException;
}
