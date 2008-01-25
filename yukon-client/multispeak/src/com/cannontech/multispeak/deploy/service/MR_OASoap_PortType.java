/**
 * MR_OASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MR_OASoap_PortType extends java.rmi.Remote {

    /**
     * OA Pings URL of MR to see if it is alive.  Returns errorObject(s)
     * as necessary to communicate application status. (Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * OA requests list of methods supported by MR. (Required)
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
     * OA Notifies MR of new outages by sending the new lists of CustomersAffectedByOutage.
     * MR returns status of failed tranactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customersAffectedByOutageNotification(com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage[] newOutages) throws java.rmi.RemoteException;

    /**
     * OA Notifies MR of the modified connectivity of meters after
     * a switching event used to restore load during outage situations. 
     * MR returns status of failed tranactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterConnectivityNotification(com.cannontech.multispeak.deploy.service.MeterConnectivity[] newConnectivity) throws java.rmi.RemoteException;
}
