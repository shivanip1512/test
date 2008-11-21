/**
 * LM_SCADASoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface LM_SCADASoap_PortType extends java.rmi.Remote {

    /**
     * SCADA Pings URL of LM to see if it is alive.   Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * SCADA requests list of methods supported by LM. (Required)
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
     * Returns the theoretical total amount of load that can be controlled,
     * expressed in kW.(Optional)
     */
    public float getAmountOfControllableLoad() throws java.rmi.RemoteException;

    /**
     * Returns the amount of load that is currently under control,
     * expressed in kW.(Optional)
     */
    public float getAmountOfControlledLoad() throws java.rmi.RemoteException;

    /**
     * Returns all of the substation load control statuses
     */
    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws java.rmi.RemoteException;

    /**
     * SCADA calls this LM service to initiate a load management event
     * via the loadManagementEvent object.  If substation and feeder information
     * is included in the loadManagementEvent, then the LM should attempt
     * to control the requested amount of load in that substation/feeder
     * area.  Otherwise, the controlled load should be spread over the entire
     * system. If this transaction should fail, LM returns information about
     * the failure using a SOAPFault. (Recommended)
     */
    public void intiateLoadManagementEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate an array of load
     * management events via multiple loadManagementEvent objects.  If substation
     * and feeder information is included in the loadManagementEvent, then
     * the LM should attempt to control the requested amount of load in that
     * substation/feeder area.  Otherwise, the controlled load should be
     * spread over the entire system. If this transaction should fail, LM
     * returns information about the failure using a SOAPFault.
     */
    public void intiateLoadManagementEvents(com.cannontech.multispeak.deploy.service.LoadManagementEvent[] theLMEvents) throws java.rmi.RemoteException;

    /**
     * SCADA calls this LM service to initiate a power factor management
     * event via the powerFactorManagementEvent object.  If this transaction
     * should fail, LM returns information about the failure using a SOAPFault.(Optional)
     */
    public void intiatePowerFactorManagementEvent(com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent thePFMEvent) throws java.rmi.RemoteException;
}
