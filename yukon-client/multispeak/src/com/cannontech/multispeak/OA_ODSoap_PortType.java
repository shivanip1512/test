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
     * as necessary to communicate application status. (Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * OD requests list of methods supported by OA. (Req)
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
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent obtained
     * earlier by calling GetActiveOutages.(Opt)
     */
    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfString getActiveOutages() throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.(Opt)
     */
    public com.cannontech.multispeak.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * OD Notifies OA of a change in OutageDetectionEvents by sending
     * an array of changed OutageDetectionEvent objects.  OA returns information
     * about failed transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject ODEventNotification(com.cannontech.multispeak.ArrayOfOutageDetectionEvent ODEvents) throws java.rmi.RemoteException;

    /**
     * OD Notifies OA of a change in OutageDetectionDevice by sending
     * an array of changed OutageDetectionDevice objects.  OA returns information
     * about failed transactions using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject ODDeviceChangedNotification(com.cannontech.multispeak.ArrayOfOutageDetectionDevice ODDevices) throws java.rmi.RemoteException;
}
