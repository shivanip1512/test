/**
 * MR_OASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface MR_OASoap_PortType extends java.rmi.Remote {

    /**
     * OA Pings URL of MR to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status. (Required)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * OA requests list of methods supported by MR. (Required)
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
     * OA Notifies MR of new outages by sending the new lists of CustomersAffectedByOutage.
     * MR returns status of failed tranactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject customersAffectedByOutageNotification(com.cannontech.multispeak.service.ArrayOfCustomersAffectedByOutage newOutages) throws java.rmi.RemoteException;

    /**
     * OA Notifies MR of the modified connectivity of meters after
     * a switching event used to restore load during outage situations. 
     * MR returns status of failed tranactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject meterConnectivityNotification(com.cannontech.multispeak.service.ArrayOfMeterConnectivity newConnectivity) throws java.rmi.RemoteException;
}
