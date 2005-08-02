/**
 * MR_CBSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2 May 03, 2005 (02:20:24 EDT) WSDL2Java emitter.
 */

package com.cannontech.multispeak;

public interface MR_CBSoap_PortType extends java.rmi.Remote {

    /**
     * CB Pings URL of MR to see if it is alive.   Returns errorObject(s)
     * as necessary to communicate application status.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject pingURL() throws java.rmi.RemoteException;

    /**
     * CB Requests a list of methods supported by MR. (Req)
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
     * Returns all meters that have AMR.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeter getAMRSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that support AMR and that have been modified
     * since the specified sessionID.   The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.   The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeter getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Return true if given meterNo has AMR. Otherwise return false.(Req)
     */
    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns Reading Data for All Meters Given a Date Range. The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry the objectID of the last data
     * instance received in subsequent calls. (Req)
     */
    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Meter Reading Data for a given MeterNo and Date Range.(Req)
     */
    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns Most Recent Meter Reading Data for a given MeterNo.(Req)
     */
    public com.cannontech.multispeak.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all required Reading Data for a given BillingCycle
     * and Date Range.  The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfMeterRead getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo and Date Range.(Req)
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given a Date Range.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a given MeterNo, eventCode and
     * Date Range.(Req)
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns History Log Data for a all Meters Given the eventCode
     * and a Date Range.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * the objectID of the last data instance received in subsequent calls.(Req)
     */
    public com.cannontech.multispeak.ArrayOfHistoryLog getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Notify MR of Planned Outage Meters Given a List of MeterNumbers
     * and Start and End Dates of the Outage. MR returns information about
     * failed transactions using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiatePlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Notify MR of Cancellation of Planned Outage Given a List of
     * MeterNumbers.  MR returns information about failed transactions using
     * an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject cancelPlannedOutage(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of Meters Where Zero Usage is Expected.(ie Move outs).
     * MR returns information about failed transactions using an array of
     * errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiateUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of Cancellation Of Zero Usage Monitoring.(ie Move
     * Ins).  MR returns information about failed transactions using an array
     * of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject cancelUsageMonitoring(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of Meters that have been disconnected and no
     * AMR reading is expected.  MR returns information about failed transactions
     * using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiateDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of Meters that should be removed from disconnected
     * status.(i.e. made active).  MR returns information about failed transactions
     * using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject cancelDisconnectedStatus(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * CB requests a new meter reading from MR, on meters selected
     * by meter number.  MR returns information about failed transactions
     * using an array of errorObjects.(Opt)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject initiateMeterReadByMeterNumber(com.cannontech.multispeak.ArrayOfString meterNos) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Customer object by sending
     * the changed customer object.MR returns information about failed transactions
     * using an array of errorObjects. (Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject customerChangedNotification(com.cannontech.multispeak.ArrayOfCustomer changedCustomers) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Service Location object by
     * sending the changed serviceLocation object.MR returns information
     * about failed transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject serviceLocationChangedNotification(com.cannontech.multispeak.ArrayOfServiceLocation changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR of a change in the Meter object by sending the
     * changed meter object.  MR returns information about failed transactions
     * using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject meterChangedNotification(com.cannontech.multispeak.ArrayOfMeter changedMeters) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR to remove the associated Meter(s).MR returns
     * information about failed transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject meterRemoveNotification(com.cannontech.multispeak.ArrayOfMeter removedMeters) throws java.rmi.RemoteException;

    /**
     * CB Notifies MR to Add the associated Meter(s).MR returns information
     * about failed transactions using an array of errorObjects.(Req)
     */
    public com.cannontech.multispeak.ArrayOfErrorObject meterAddNotification(com.cannontech.multispeak.ArrayOfMeter addedMeters) throws java.rmi.RemoteException;
}
