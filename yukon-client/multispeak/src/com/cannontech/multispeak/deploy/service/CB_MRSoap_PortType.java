/**
 * CB_MRSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface CB_MRSoap_PortType extends java.rmi.Remote {

    /**
     * MR Pings URL of CB to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * MR Requests list of methods supported by CB. (Required)
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
     * Returns all required customer data for all customers.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Customer[] getAllCustomers(java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.Customer[] getModifiedCustomers(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer if it exists.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer data given a Meter Number.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer(s) data given First and Last
     * name. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Customer[] getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer given the Doing Business As
     * (DBA) name. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByDBAName(java.lang.String dBAName) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getAllServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Service Location
     * ID.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Customer
     * ID.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given the meter
     * number of a meter served at that location.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Account Number.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Grid
     * Location.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByGridLocation(java.lang.String gridLocation) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByPhaseCode(com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByLoadGroup(java.lang.String loadGroup, java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * ShutOff Date.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getAllMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given meterID.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given Meter Number.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Service Location.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Account Number.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Customer ID.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given AMR Type.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB a list of names of meter reading
     * groups.  The CB returns an array of strings including the names of
     * the supported meterGroups.  The MR can then request the members of
     * a specific group using the GetMeterGroupMembers method. (Optional)
     */
    public java.lang.String[] getMeterGroupNames() throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB the members of a specific meter
     * reading group, identified by the meterGroupName parameter.  This method
     * is used, along with the GetMeterGroupNames method to enable the MR
     * to get information about which meters are included in a specific meter
     * reading group.  The CB returns an array of meterIDs in response to
     * this method call. (Optional)
     */
    public java.lang.String[] getMeterGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB all meters of a given serviceType
     * (electric, gas, water, or propane). The CB returns a meters object,
     * which contains an array of meters by serviceType in response to this
     * method call. The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry in subsequent
     * calls the objectID of the data instance noted by the server as being
     * the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meters getAllMetersByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB all serviceLocations of a given
     * serviceType (electric, gas, water, or propane). The CB returns a serviceLocations
     * object, which contains an array of serviceLocations by serviceType
     * in response to this method call. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocations getAllServiceLocationsByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB a specific meter object, given
     * the account number and serviceType (electric, gas, water, or propane).
     * The CB returns a meters object, which can contain an array of meters
     * by serviceType in response to this method call. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meters getMeterByAccountNumberAndServiceType(java.lang.String accountNumber, java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all electric meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ElectricMeter[] getAllElectricMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all gas meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.GasMeter[] getAllGasMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all water meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.WaterMeter[] getAllWaterMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all propane meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.PropaneMeter[] getAllPropaneMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all electric service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] getAllElectricServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all gas service locations. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all water service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.WaterServiceLocation[] getAllWaterServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all propane service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.PropaneServiceLocation[] getAllPropaneServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB electric meter objects, given the
     * account number. The CB returns a meters object, which can contain
     * an array of electric meters in response to this method call. The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meters getElectricMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB gas meter objects, given the account
     * number. The CB returns a meters object, which can contain an array
     * of gas meters in response to this method call. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meters getGasMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB water meter objects, given the
     * account number. The CB returns a meters object, which can contain
     * an array of water meters in response to this method call. The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meters getWaterMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB propane meter objects, given the
     * account number. The CB returns a meters object, which can contain
     * an array of propane meters in response to this method call. The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meters getPropaneMeterByAccountNumber(java.lang.String accountNumber, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB all customers of a given serviceType
     * (electric, gas, water, or propane). The CB returns an array of customer
     * objects. The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent. (Optional)
     */
    public com.cannontech.multispeak.deploy.service.Customer[] getAllCustomersByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB a specific customer given a meter
     * number and serviceType (electric, gas, water, or propane). The CB
     * returns a customer object. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceType(java.lang.String meterNo, java.lang.String serviceType) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for a specific customer object.
     * If this transaction fails, CB returns information in a SOAPFault.
     * (Optional)
     */
    public void modifyCBDataForCustomer(com.cannontech.multispeak.deploy.service.Customer customerData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Service Location object.
     * If this transaction fails, CB returns information in a SOAPFault.(Optional)
     */
    public void modifyCBDataForServiceLocation(com.cannontech.multispeak.deploy.service.ServiceLocation serviceLocationData) throws java.rmi.RemoteException;

    /**
     * Allow MR to Modify CB data for the Meter object.  If this transaction
     * fails, CB returns information in a SOAPFault.(Optional)
     */
    public void modifyCBDataForMeter(com.cannontech.multispeak.deploy.service.Meter meterData) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in Meter Reads by sending the changed
     * meterRead objects.  CB returns information about failed transactions
     * in an array of errorObjects.The transactionID calling parameter links
     * this Initiate request with the published data method call.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingChangedNotification(com.cannontech.multispeak.deploy.service.MeterRead[] changedMeterReads, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in the History Log by sending the
     * changed historyLog object.  CB returns information about failed transactions
     * in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] historyLogChangedNotification(com.cannontech.multispeak.deploy.service.HistoryLog[] changedHistoryLogs) throws java.rmi.RemoteException;

    /**
     * MR publishes new meter readings to the CB by sending a formattedBlock
     * object.  CB returns information about failed transactions in an array
     * of errorObjects.The transactionID calling parameter links this Initiate
     * request with the published data method call.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] formattedBlockNotification(com.cannontech.multispeak.deploy.service.FormattedBlock changedMeterReads, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * MR notifies CB that meter(s) have been installed. CB returns
     * information about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterInstalledNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * MR notifies CB that meter(s) have been deployed or exchanged.
     * CB returns information about failed transactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that in-home display(s) have been installed.
     * CB returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayInstalledNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] installedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that in-home displays(s) have been deployed
     * or exchanged.  CB returns information about failed transactions in
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException;
}
