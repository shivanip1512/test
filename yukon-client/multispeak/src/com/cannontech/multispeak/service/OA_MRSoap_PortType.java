/**
 * OA_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface OA_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of OA to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status. (Required)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * MR Requests list of methods supported by OA. (Required)
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
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent sent
     * earlier using the CustomersAffectedByOutageNotification method on
     * the MR-OA interface or obtained using the GetActiveOutages method.(Recommended)
     */
    public com.cannontech.multispeak.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.(Optional)
     */
    public com.cannontech.multispeak.service.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.service.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getActiveOutages() throws java.rmi.RemoteException;
}
