/**
 * MR_CBSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MR_CBSoap_PortType extends java.rmi.Remote {

    /**
     * CB Pings URL of MR to see if it is alive.   Returns errorObject(s)
     * as necessary to communicate application status.(Required)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * CB Requests a list of methods supported by MR. (Required)
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
     * Returns all meters that have AMR.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that support AMR and that have been modified
     * since the specified sessionID.   The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.   The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Return true if given meterNo has AMR. Otherwise return false.(Recommended)
     */
    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns Reading Data for All Meters Given a Date Range. The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Meter Reading Data for a given MeterNo and Date Range.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns Most Recent Meter Reading Data for a given MeterNo.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all required reading data for a given BillingCycle
     * and Date Range in the form of an array of formattedBlocks.  The calling
     * parameters include: (i) billingCycle - the CB billing cycle for which
     * readings are to be returned, (ii)billing date - the end date of the
     * billing cycle, (iii) kWhLookBack - the number of days before the billing
     * date for which the CB will accept valid kWh readings (if zero then
     * the reading is only acceptable on the billing date), (iv) kWLookBack-
     * the number of days before the billing date for which the CB will accept
     * valid kW readings (if zero then the reading is only acceptable on
     * the billing date), (v) kWLookForward - the number of days to accept
     * demand readings beyond the billing date to be used in this billing
     * period (if zero then must use demand occurring only through the billing
     * date) (vi) lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received (that is to say the lastSent data instance) in subsequent
     * calls.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns reading data for a given meter and billing date.  Reading(s)are
     * returned in the form of an array of formattedBlocks.  The calling
     * parameters include: (i) meterNumber - the meter number for which readings
     * are to be returned, (ii)billing date - the end date of the billing
     * cycle, (iii) kWhLookBack - the number of days before the billing date
     * for which the CB will accept valid kWh readings (if zero then the
     * reading is only acceptable on the billing date), (iv) kWLookBack-
     * the number of days before the billing date for which the CB will accept
     * valid kW readings (if zero then the reading is only acceptable on
     * the billing date), (v) kWLookForward - the number of days to accept
     * demand readings beyond the billing date to be used in this billing
     * period (if zero then must use demand occurring only through the billing
     * date) (vi) lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry the objectID of the last data instance
     * received (that is to say the lastSent data instance) in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingByMeterNumberFormattedBlock(java.lang.String meterNumber, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns reading data for all billing cycles given a billing
     * date.  Reading(s) are returned in the form of an array of formattedBlocks.
     * The calling parameters include: (i) billing date - the end date of
     * the billing cycle, (ii) kWhLookBack - the number of days before the
     * billing date for which the CB will accept valid kWh readings (if zero
     * then the reading is only acceptable on the billing date), (iii) kWLookBack-
     * the number of days before the billing date for which the CB will accept
     * valid kW readings (if zero then the reading is only acceptable on
     * the billing date), (iv) kWLookForward - the number of days to accept
     * demand readings beyond the billing date to be used in this billing
     * period (if zero then must use demand occurring only through the billing
     * date) and (v) lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry the objectID of the last data
     * instance received (that is to say the lastSent data instance) in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByDateFormattedBlock(java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo and Date Range.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given a Date Range.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo, eventCode and
     * Date Range.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given the eventCode
     * and a Date Range.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the objectID of the data instance noted by the
     * server as being the lastSent.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Most Recent Meter Reading Data for all meters in a
     * given MeterGroup, requested by meter group name.  Meter readings are
     * returned in the form of a formattedBlock.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock getLatestMeterReadingsByMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * Notify MR of Planned Outage Meters Given a List of MeterNumbers
     * and Start and End Dates of the Outage. MR returns information about
     * failed transactions using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiatePlannedOutage(java.lang.String[] meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Notify MR of Cancellation of Planned Outage Given a List of
     * MeterNumbers.  MR returns information about failed transactions using
     * an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelPlannedOutage(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of Meters Where Zero Usage is Expected.(ie Move outs).
     * MR returns information about failed transactions using an array of
     * errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of Cancellation Of Zero Usage Monitoring.(ie Move
     * Ins).  MR returns information about failed transactions using an array
     * of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of Meters that have been disconnected and no
     * AMR reading is expected.  MR returns information about failed transactions
     * using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of Meters that should be removed from disconnected
     * status.(i.e. made active).  MR returns information about failed transactions
     * using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * CB requests a new meter reading from MR, on meters selected
     * by meter number.  MR returns information about failed transactions
     * using an array of errorObjects. The meter read is returned to the
     * CB in the form of a meterRead or formattedBlock, sent to the URL specified
     * in the responseURL.  The transactionID calling parameter links this
     * Initiate request with the published data method call.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNumber(java.lang.String[] meterNos, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to establish a new group of meters to address
     * as a meter group.  MR returns information about failed transactions
     * using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishMeterGroup(com.cannontech.multispeak.deploy.service.MeterGroup meterGroup) throws java.rmi.RemoteException;

    /**
     * CB requests MR to eliminate a previously defined group of meters
     * to address as a meter group.  MR returns information about failed
     * transactions using an errorObject.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject deleteMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to add meter(s) to an existing group of meters
     * to address as a meter group.  MR returns information about failed
     * transaction using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] insertMeterInMeterGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to remove meter(s) from an existing group of
     * meters to address as a meterroup.  MR returns information about failed
     * transaction using an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] removeMetersFromMeterGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to send the most recent meter reading from its
     * database for a group of meters, referred to by meter reading group
     * name.  MR returns an array of errorObjects to signal failed transaction(s).
     * Meter readings are subsequently published to the CB using the FormattedBlockNotification
     * method and sent to the URL specified in the responseURL parameter.The
     * transactionID calling parameter links this Initiate request with the
     * published data method call.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateGroupMeterRead(java.lang.String meterGroupName, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to schedule meter readings for a group of meters.
     * MR returns information about failed transactions using an array of
     * errorObjects.  MR returns meter readings when they have been collected
     * using the FormattedBlockNotification method sent to the URL specified
     * in the responseURL parameter.The transactionID calling parameter links
     * this Initiate request with the published data method call.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] scheduleGroupMeterRead(java.lang.String meterGroupName, java.util.Calendar timeToRead, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to to update the in-home display associated
     * with a specific service location.  MR returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] updateServiceLocationDisplays(java.lang.String servLocID) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Customer object by sending
     * the changed customer object.MR returns information about failed transactions
     * using an array of errorObjects. (Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Service Location object by
     * sending the changed serviceLocation object.MR returns information
     * about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Meter object by sending the
     * changed meter object.  MR returns information about failed transactions
     * using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR to remove the associated Meter(s).MR returns
     * information about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR that the associated Meter(s)have been retired
     * from the system.  MR returns information about failed transactions
     * using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR to Add the associated Meter(s).MR returns information
     * about failed transactions using an array of errorObjects.(Recommended)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * CB notifies MR that meter(s) have been deployed or exchanged.
     * MR returns information about failed transactions in an array of errorObjects.(Optional)
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to add the associated in-home display(s).MR
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayAddNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of a change in the in-home display(s)by
     * sending the changed inHomeDisplay object.  MR returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayChangedNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] changedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that in-home displays(s) have been deployed
     * or exchanged.  MR returns information about failed transactions in
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to remove the associated in-home displays(s).
     * MR returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRemoveNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] removedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated in-home display(s)have
     * been retired from the system.  MR returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRetireNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] retiredIHDs) throws java.rmi.RemoteException;
}
