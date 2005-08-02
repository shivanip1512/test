/**
 * CB_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface CB_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of CB to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * MR Requests list of methods supported by CB. (Req)
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
     * Returns all required customer data for all customers.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry the objectID of the last data
     * instance received in subsequent calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfCustomer getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required customer data for all customers that have
     * been modified since the specified sessionID.  The calling parameter
     * previousSessionID should carry the session identifier for the last
     * session of data that the client has successfully received.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfCustomer getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required Service Location data for all Service
     * Locations that have been modified since the specified sessionID. 
     * The calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt.)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer if it exists.(Req)
     */
    public com.cannontech.multispeak.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer data given a Meter Number.(Opt)
     */
    public com.cannontech.multispeak.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer(s) data given First and Last
     * name. (Opt)
     */
    public com.cannontech.multispeak.ArrayOfCustomer getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer given the Doing Business As
     * (DBA) name. (Opt)
     */
    public com.cannontech.multispeak.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException;

    /**
     * Returns all required Service Location data for all Service
     * Locations. The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.(Req.)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * Status. The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Service Location
     * ID.(Req)
     */
    public com.cannontech.multispeak.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Customer
     * ID.(Req)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Account Number.(Req)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Grid
     * Location.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Phase.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByPhaseCode(com.cannontech.multispeak.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Load
     * Group.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * Type.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * ShutOff Date.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfServiceLocation getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received in subsequent calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters that have been
     * modified since the specified sessionID.  The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeter getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given meterID.(Req)
     */
    public com.cannontech.multispeak.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given Meter Number.(Req)
     */
    public com.cannontech.multispeak.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Service Location.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Account Number.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Customer ID.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given AMR Type.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeter getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for a specific customer object.
     * If this transaction fails, CB returns information in a SOAPFault.
     * (Opt)
     */
    public void modifyCBDataForCustomer(com.cannontech.multispeak.Customer customerData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Service Location object.
     * If this transaction fails, CB returns information in a SOAPFault.(Opt)
     */
    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.ServiceLocation serviceLocationData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Meter object.  If this transaction
     * fails, CB returns information in a SOAPFault.(Opt)
     */
    public void modifyCBDataForMeter(com.cannontech.multispeak.Meter meterData) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in Meter Reads by sending the changed
     * meterRead objects.  CB returns information about failed transactions
     * in an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject readingChangedNotification(com.cannontech.multispeak.ArrayOfMeterRead changedMeterReads) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in the History Log by sending the
     * changed historyLog object.  CB returns information about failed transactions
     * in an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject historyLogChangedNotification(com.cannontech.multispeak.ArrayOfHistoryLog changedHistoryLogs) throws java.rmi.RemoteException;
}
