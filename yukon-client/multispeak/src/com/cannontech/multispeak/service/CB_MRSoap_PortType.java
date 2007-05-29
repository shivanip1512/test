/**
 * CB_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak.service;

public interface CB_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of CB to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * MR Requests list of methods supported by CB. (Required)
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
     * Returns all required customer data for all customers.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required customer data for all customers that have
     * been modified since the specified sessionID.  The calling parameter
     * previousSessionID should carry the session identifier for the last
     * session of data that the client has successfully received.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required Service Location data for all Service
     * Locations that have been modified since the specified sessionID. 
     * The calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer if it exists.(Recommended)
     */
    public com.cannontech.multispeak.service.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer data given a Meter Number.(Optional)
     */
    public com.cannontech.multispeak.service.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer(s) data given First and Last
     * name. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer given the Doing Business As
     * (DBA) name. (Optional)
     */
    public com.cannontech.multispeak.service.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException;

    /**
     * Returns all required Service Location data for all Service
     * Locations. The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry in subsequent
     * calls the objectID of the data instance noted by the server as being
     * the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * Status. The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Service Location
     * ID.(Recommended)
     */
    public com.cannontech.multispeak.service.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Customer
     * ID.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given the meter
     * number of a meter served at that location.(Optional)
     */
    public com.cannontech.multispeak.service.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Account Number.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Grid
     * Location.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Phase.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByPhaseCode(com.cannontech.multispeak.service.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Load
     * Group.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * Type.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * ShutOff Date.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfServiceLocation getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters that have been
     * modified since the specified sessionID.  The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given meterID.(Recommended)
     */
    public com.cannontech.multispeak.service.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given Meter Number.(Recommended)
     */
    public com.cannontech.multispeak.service.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Service Location.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Account Number.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Customer ID.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given AMR Type.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfMeter getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB a list of names of meter reading
     * groups.  The CB returns an array of strings including the names of
     * the supported meterGroups.  The MR can then request the members of
     * a specific group using the GetMeterGroupMembers method. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getMeterGroupNames() throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB the members of a specific meter
     * reading group, identified by the meterGroupName parameter.  This method
     * is used, along with the GetMeterGroupNames method to enable the MR
     * to get information about which meters are included in a specific meter
     * reading group.  The CB returns an array of meterIDs in response to
     * this method call. (Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfString getMeterGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for a specific customer object.
     * If this transaction fails, CB returns information in a SOAPFault.
     * (Optional)
     */
    public void modifyCBDataForCustomer(com.cannontech.multispeak.service.Customer customerData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Service Location object.
     * If this transaction fails, CB returns information in a SOAPFault.(Optional)
     */
    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.service.ServiceLocation serviceLocationData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Meter object.  If this transaction
     * fails, CB returns information in a SOAPFault.(Optional)
     */
    public void modifyCBDataForMeter(com.cannontech.multispeak.service.Meter meterData) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in Meter Reads by sending the changed
     * meterRead objects.  CB returns information about failed transactions
     * in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject readingChangedNotification(com.cannontech.multispeak.service.ArrayOfMeterRead changedMeterReads) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in the History Log by sending the
     * changed historyLog object.  CB returns information about failed transactions
     * in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject historyLogChangedNotification(com.cannontech.multispeak.service.ArrayOfHistoryLog changedHistoryLogs) throws java.rmi.RemoteException;

    /**
     * MR publishes new meter readings to the CB by sending a formattedBlock
     * object.  CB returns information about failed transactions in an array
     * of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject formattedBlockNotification(com.cannontech.multispeak.service.FormattedBlock changedMeterReads) throws java.rmi.RemoteException;

    /**
     * MR notifies CB that meter(s) have been installed. CB returns
     * information about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.service.ArrayOfErrorObject meterInstalledNotification(com.cannontech.multispeak.service.ArrayOfMeter addedMeters) throws java.rmi.RemoteException;
}
