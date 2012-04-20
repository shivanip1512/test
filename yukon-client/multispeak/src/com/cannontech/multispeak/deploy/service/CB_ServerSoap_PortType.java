/**
 * CB_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface CB_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * The STK requests from the CB to reserve a service order number.
     * The CB returns a reserved service order in the form of a requestedNumber.
     */
    public com.cannontech.multispeak.deploy.service.RequestedNumber getNextNumber(com.cannontech.multispeak.deploy.service.ExtensionsItem[] extensions, java.lang.String numberType) throws java.rmi.RemoteException;

    /**
     * Returns the requested meters corresponding to a given location
     * in the engineering connectivity model, as specified by the eaLoc.name.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByEALocation(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns the requested meters corresponding to a given customer's
     * serviceLocation, given the serviceLocation.facilityID.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByFacilityID(java.lang.String facilityID) throws java.rmi.RemoteException;

    /**
     * Returns the requested meters corresponding to a given customer's
     * serviceLocation, given the serviceLocation.siteID
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersBySiteID(java.lang.String siteID) throws java.rmi.RemoteException;

    /**
     * Returns all meters corresponding to a given customer name.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByCustomerName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException;

    /**
     * Returns all meters corresponding to a given customer's home
     * phone number.  HomeAC is the area code and homePhone is the 7-digit
     * local phone number for the customer of interest.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersByHomePhone(java.lang.String homeAC, java.lang.String homePhone) throws java.rmi.RemoteException;

    /**
     * Returns all meters corresponding to a given search string.
     * This method can return any meters associated with customer, serviceLocation,
     * meter,accountNumber, or meterBase containing or starting with the
     * seach string.  For instance, a  search string of 123 could return
     * meters for accountNumber = 12345678, customer = as123666, etc.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMetersBySearchString(java.lang.String searchString) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server a list of names of meter
     * configuration groups.  The server returns an array of strings including
     * the names of the supported configurationGroups.  The Requester can
     * then request the members of a specific group using the GetConfigurationGroupMembers
     * method.
     */
    public java.lang.String[] getConfigurationGroupNames() throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server a list of names of meter
     * configuration groups for a specific meter.  The server returns an
     * array of strings including the names of the supported configurationGroups.
     * The Requester can then request the members of a specific group using
     * the GetConfigurationGroupMembers method.
     */
    public java.lang.String[] getConfigurationGroupNamesByMeterNo(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server the members of a specific
     * meter cofiguration group, identified by the meterGroupName parameter.
     * This method is used, along with the GetConfigurationGroupNames method
     * to enable the Requester to get information about which meters are
     * included in a specific meter configuration group.  The server returns
     * a meterGroups object in response to this method call.  This object
     * can carry meterGroups that only include meters of one service type
     * or a mixed meterGroup that contains meters of mixed service type.
     */
    public com.cannontech.multispeak.deploy.service.MeterGroups getConfigurationGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException;

    /**
     * Returns all street lights.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.StreetLight[] getAllStreetLights(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all security lights.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.SecurityLight[] getAllSecurityLights(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server a list of names of in
     * home display groups.  The server returns an array of strings including
     * the names of the supported inHomeDisplayGroups.  The requester can
     * then request the members of a specific group using the GetIHDGroupMembers
     * method.
     */
    public java.lang.String[] getIHDGroupNames() throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server a list of names of in
     * home display groups for a specific inHomeDisplay.  The server returns
     * an array of strings including the names of the supported inHomeDisplayGroups.
     * The Requester can then request the members of a specific group using
     * the GetIHDGroupMembers method.
     */
    public java.lang.String[] getIHDGroupNamesByInHomeDisplayID(java.lang.String inHomeDisplayID) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the server the members of a specific
     * in home display group, identified by the IHDGroupName parameter. 
     * This method is used, along with the GetIHDGroupNames method to enable
     * the requester to get information about which meters are included in
     * a specific in home display group.  The server returns an inHomeDisplayGroup
     * in response to this method call.
     */
    public com.cannontech.multispeak.deploy.service.InHomeDisplayGroup getIHDGroupMembers(java.lang.String IHDGroupName) throws java.rmi.RemoteException;

    /**
     * Returns the requested meter base data given the objectID of
     * the meterBase.
     */
    public com.cannontech.multispeak.deploy.service.MeterBase getMeterBaseByObjectId(java.lang.String meterBaseID) throws java.rmi.RemoteException;

    /**
     * Returns all connectDisconnectEvents in the specified date range
     * for the specified reasonCode.  See the connectDisconnectCode.reasonCode
     * enumeration for acceptable values.
     */
    public com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] getAllConnectDisconnectEventsByReasonCode(java.lang.String reasonCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Allow client to Modify CB data for customer objects.  If this
     * transaction fails, CB returns information in a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForCustomer(com.cannontech.multispeak.deploy.service.Customer[] customerData) throws java.rmi.RemoteException;

    /**
     * Allow client to Modify CB data for service location objects.
     * If this transaction fails, CB returns information in a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForServiceLocation(com.cannontech.multispeak.deploy.service.ServiceLocation[] serviceLocationData) throws java.rmi.RemoteException;

    /**
     * Allow client to Modify CB data for meter objects.  If this
     * transaction fails, CB returns information in a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForMeter(com.cannontech.multispeak.deploy.service.Meter[] meterData) throws java.rmi.RemoteException;

    /**
     * Allow Requester to modify CB data for the pole object.  CB
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForPole(com.cannontech.multispeak.deploy.service.Pole[] poleData) throws java.rmi.RemoteException;

    /**
     * Allow requester to Modify CB data for a transformer bank object.
     * CB returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForTransformerBank(com.cannontech.multispeak.deploy.service.TransformerBank[] xfmrBankData) throws java.rmi.RemoteException;

    /**
     * Allow requester to Modify CB data for the customer usage object.
     * CB returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForUsage(com.cannontech.multispeak.deploy.service.Usage[] usageData) throws java.rmi.RemoteException;

    /**
     * Allow Requester to modify CB data for the streetLight object.
     * CB returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForStreetLight(com.cannontech.multispeak.deploy.service.StreetLight[] streetLightData) throws java.rmi.RemoteException;

    /**
     * Allow Requester to modify CB data for the securityLight object.
     * CB returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] modifyCBDataForSecurityLight(com.cannontech.multispeak.deploy.service.SecurityLight[] securityLightData) throws java.rmi.RemoteException;

    /**
     * MR Notifies CB of a change in Meter Reads by sending the changed
     * meterRead objects.  CB returns information about failed transactions
     * in an array of errorObjects.The transactionID calling parameter links
     * this Initiate request with the published data method call. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingChangedNotification(com.cannontech.multispeak.deploy.service.MeterRead[] changedMeterReads, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of a change in the History Log
     * by sending the changed historyLog object.  CB returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] historyLogChangedNotification(com.cannontech.multispeak.deploy.service.HistoryLog[] changedHistoryLogs) throws java.rmi.RemoteException;

    /**
     * Publisher publishes new meter readings to the Subscriber by
     * sending a formattedBlock object.  CB returns information about failed
     * transactions in an array of errorObjects.The transactionID calling
     * parameter links this Initiate request with the published data method
     * call. The message header attribute 'registrationID' should be added
     * to all publish messages to indicate to the subscriber under which
     * registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] formattedBlockNotification(com.cannontech.multispeak.deploy.service.FormattedBlock changedMeterReads, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that meter(s) have been installed. CB
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterInstalledNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * MR notifies CB that meter(s) have been deployed or exchanged.
     * A meterExchange shall be a paired transaction of a meter being removed
     * and a meter being installed in the same meter base.  CB returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that load mangement device(s) have been
     * deployed or exchanged.  CB returns information about failed transactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.LMDeviceExchange[] LMDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that load management device(s) have been
     * installed. CB returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] installedLMDs) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state change for a connect/disconnect device
     * By meterNumber and loadActionCode.  The transactionID calling parameter
     * can be used to link this action with an InitiateConectDisconnect call.
     * If this transaction fails, CB returns information about the failure
     * in a SOAPFault. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.LoadActionCode stateChange, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state change(s) for connect/disconnect device(s).
     * The transactionID calling parameter can be used to link this action
     * with an InitiateConectDisconnect call.  If this transaction fails,
     * CB returns information about the failure in an array of errorObject(s).
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesChangedNotification(com.cannontech.multispeak.deploy.service.CDStateChange[] stateChanges, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher requests that CB authorize and process a set of transactions
     * that have been sent as part of a paymentTransactionsList.  CB returns
     * information about the transaction to the Publisher.
     */
    public com.cannontech.multispeak.deploy.service.PaymentTransaction[] processPaymentTransaction(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException;

    /**
     * Publisher requests that CB process a set of transactions that
     * the PP has previously authorized and that have been sent as part of
     * a paymentTransactionsList.  CB returns information about the transactions
     * to the Publisher.
     */
    public com.cannontech.multispeak.deploy.service.PaymentTransaction[] commitPaymentTransaction(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException;

    /**
     * Publisher requests that CB extend payment terms for a specific
     * account by sending a paymentExtensionList object.  CB sends extension
     * information to the PP based on payment extension rules configured
     * on the CB.  PP must subsequently commit the extension using an ExtendPayment
     * method call if the payment extension terms were accepted by the payee.
     * The CB shall return the processed paymentExtensionList with the responseCode
     * and the extendedTo date filled in with the new extended date.
     */
    public com.cannontech.multispeak.deploy.service.PaymentExtension[] requestPaymentExtension(com.cannontech.multispeak.deploy.service.PaymentExtension[] extensionList) throws java.rmi.RemoteException;

    /**
     * Publisher has processed payment extension(s) in accordance
     * with the rules established by the CB and previously communicated with
     * the Publisher using the RequestPaymentExtension method, and is sending
     * the extension information to the CB.  CB returns information on failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] extendPayment(com.cannontech.multispeak.deploy.service.PaymentExtension[] extensionList) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies CB of a change in the LoadProfile object(s)
     * by sending the changed profileObject(s).  CB returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] loadProfileChangedNotification(com.cannontech.multispeak.deploy.service.ProfileObject[] changedLoadProfiles) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB of a change in a pole by sending the
     * changed pole object. CB returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] poleChangedNotification(com.cannontech.multispeak.deploy.service.Pole[] changedPoles) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB of a change in the service location/network
     * data by sending the changed serviceLocation object.  CB returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationNetworkChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB of a change in a transformerBank by sending
     * the changed transformerBank object.  CB returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] transformerBankChangedNotification(com.cannontech.multispeak.deploy.service.TransformerBank[] changedXfmrBanks) throws java.rmi.RemoteException;

    /**
     * EDR Notifies CB of a received end device (meter) shipment by
     * sending the changed endDeviceShipment object.  CB returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] endDeviceShipmentNotification(com.cannontech.multispeak.deploy.service.EndDeviceShipment shipment) throws java.rmi.RemoteException;

    /**
     * EDT Notifies CB of a completion of tests on electric meters
     * by sending the meterTestList object.  CB returns information about
     * failed transactions in an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterTestTransaction(com.cannontech.multispeak.deploy.service.MeterTest[] test) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that in-home display(s) have been installed.
     * CB returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayInstalledNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that in-home displays(s) have been deployed
     * or exchanged.  CB returns information about failed transactions in
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that connect/disconnect device(s) have
     * been deployed or exchanged.  CB returns information about failed transactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.CDDeviceExchange[] CDDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that connect/disconnect device(s) have
     * been installed. CB returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.CDDevice[] installedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that a previously reserved service order
     * number is being returned. CB returns failed transactions using an
     * errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject returnGeneratedNumber(com.cannontech.multispeak.deploy.service.RequestedNumber requestedNum) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that history comment(s) should be written
     * to a customer account. CB returns failed transactions using an errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] writeAccountHistoryComments(com.cannontech.multispeak.deploy.service.HistoryComment[] comments) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state of a connect/disconnect device.  The
     * transactionID calling parameter can be used to link this action with
     * an InitiateCDStateRequest call.  If this transaction fails, CB returns
     * information about the failure in a SOAPFault. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public void CDStateNotification(com.cannontech.multispeak.deploy.service.CDState state, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * CD notifies CB of state of connect/disconnect device(s).  The
     * transactionID calling parameter can be used to link this action with
     * an InitiateCDStateRequest call.  If this transaction fails, CB returns
     * information about the failure in an array of errorObject(s). The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesNotification(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber of the success or failure of
     * meter reading schedule requests by sending readingScheduleResult objects.
     * CB returns information about failed transactions in an array of errorObjects.
     * The transactionID calling parameter links this Initiate request with
     * the published data method call. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingScheduleResultNotification(com.cannontech.multispeak.deploy.service.ReadingScheduleResult[] scheduleResult, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of meter event(s) by sending
     * a meterEventList object.  Subscriber returns information about failed
     * transactions in an array of errorObjects.  The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterEventNotification(com.cannontech.multispeak.deploy.service.MeterEventList events) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of the modified connectivity of meters
     * after a switching event used to restore load during outage situations.
     * MR returns status of failed tranactions in an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterConnectivityNotification(com.cannontech.multispeak.deploy.service.MeterConnectivity[] newConnectivity) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that meter base(es) have been deployed
     * or exchanged.  MDM returns information about failed transactions in
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseExchangeNotification(com.cannontech.multispeak.deploy.service.MeterBaseExchange[] MBChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that meter base(es) have been installed.
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseInstalledNotification(com.cannontech.multispeak.deploy.service.MeterBase[] addedMBs) throws java.rmi.RemoteException;

    /**
     * Requester pings URL of CB to see if it is alive. Returns errorObject(s)
     * as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods supported by CB.
     */
    public java.lang.String[] getMethods() throws java.rmi.RemoteException;

    /**
     * The client requests from the server a list of names of domains
     * supported by the server.  This method is used, along with the GetDomainMembers
     * method to enable systems to exchange information about application-specific
     * or installation-specific lists of information, such as the lists of
     * counties for this installation or the list of serviceStatusCodes used
     * by the server.
     */
    public java.lang.String[] getDomainNames() throws java.rmi.RemoteException;

    /**
     * The client requests from the server the members of a specific
     * domain of information, identified by the domainName parameter, which
     * are supported by the server.  This method is used, along with the
     * GetDomainNames method to enable systems to exchange information about
     * application-specific or installation-specific lists of information,
     * such as the lists of counties for this installation or the list of
     * serviceStatusCodes used by the server.
     */
    public com.cannontech.multispeak.deploy.service.DomainMember[] getDomainMembers(java.lang.String domainName) throws java.rmi.RemoteException;

    /**
     * This service requests of the publisher a unique registration
     * ID that would subsequently be used to refer unambiguously to that
     * specific subscription.  The return parameter is the registrationID,
     * which is a string-type value.  It is recommended that the server not
     * implement registration in such a manner that one client can guess
     * the registrationID of another.  For instance the use of sequential
     * numbers for registrationIDs is discouraged.
     */
    public java.lang.String requestRegistrationID() throws java.rmi.RemoteException;

    /**
     * This method establishs a subscription using a previously requested
     * registrationID. The calling parameter registrationInfo is a complex
     * type that includes the following information: registrationID - the
     * previously requested registrationID obtained from the publisher by
     * calling RequestRegistrationID, responseURL – the URL to which information
     * should subsequently be published on this subscription, msFunction
     * – the abbreviated string name of the MultiSpeak method making the
     * subscription request (for instance, if an application that exposes
     * the Meter Reading function has made the request, then the msFunction
     * variable should include “MR�?), methodsList – An array of strings that
     * contain the string names of the MultiSpeak methods to which the subscriber
     * would like to subscribe.  Subsequent calls to RegisterForService on
     * an existing subscription replace prior subscription details in their
     * entirety - they do NOT add to an existing subscription.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] registerForService(com.cannontech.multispeak.deploy.service.RegistrationInfo registrationDetails) throws java.rmi.RemoteException;

    /**
     * This method deletes a previously established subscription (registration
     * for service) that carries the registration identifer listed in the
     * input parameter registrationID.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] unregisterForService(java.lang.String registrationID) throws java.rmi.RemoteException;

    /**
     * This method requests the return of existing registration information
     * (that is to say the details of what is subscribed on this subscription)
     * for a specific registrationID.  The server should return a SOAPFault
     * if the registrationID is not valid.
     */
    public com.cannontech.multispeak.deploy.service.RegistrationInfo getRegistrationInfoByID(java.lang.String registrationID) throws java.rmi.RemoteException;

    /**
     * Requester requests list of methods to which this server can
     * publish information.
     */
    public java.lang.String[] getPublishMethods() throws java.rmi.RemoteException;

    /**
     * This method permits a client to have changed information on
     * domain members published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainMembersChangedNotification as one of the methods in the list
     * of methods to which the client has subscribed.  The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the  domainMember information.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainMembersChangedNotification(com.cannontech.multispeak.deploy.service.DomainMember[] changedDomainMembers) throws java.rmi.RemoteException;

    /**
     * This method permits a client to have changed information on
     * domain names published to it using a previously arranged subscription,
     * set up using the RegisterForServiceMethod. The client should first
     * obtain a registrationID and then register for service, including the
     * DomainNamesChangedNotification as one of the methods in the list of
     * methods to which the client has subscribed.  The server shall include
     * the registrationID for the subscription in the message header so that
     * the client can determine the source of the  domainName information.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] domainNamesChangedNotification(com.cannontech.multispeak.deploy.service.DomainNameChange[] changedDomainNames) throws java.rmi.RemoteException;

    /**
     * Returns all required customer data for all customers.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent. If
     * the sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
     */
    public com.cannontech.multispeak.deploy.service.OvercurrentDeviceBank[] getAllOCDB(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all required customer data for all customers.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent. If
     * the sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
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
     * the data instance noted by the server as being the lastSent.
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
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getModifiedServiceLocations(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer if it exists.
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer data given a Meter Number.
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer(s) data given First and Last
     * name.
     */
    public com.cannontech.multispeak.deploy.service.Customer[] getCustomerByName(java.lang.String firstName, java.lang.String lastName) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer given the Doing Business As
     * (DBA) name.
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
     * the lastSent.If the sessionID parameter is set in the message header,
     * then the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
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
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceStatus(java.lang.String servStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Service Location
     * ID.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByServLoc(java.lang.String servLocId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Customer
     * ID.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByCustId(java.lang.String custId) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given the meter
     * number of a meter served at that location.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation getServiceLocationByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location data given Account Number.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Grid
     * Location.
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
     * lastSent.
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
     * lastSent.
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
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Service Location(s) data given the Service
     * ShutOff Date.
     */
    public com.cannontech.multispeak.deploy.service.ServiceLocation[] getServiceLocationByShutOffDate(java.util.Calendar shutOffDate) throws java.rmi.RemoteException;

    /**
     * Returns all required Meter data for all Meters.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent. If the
     * sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
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
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given meterID.
     */
    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterId(java.lang.String meterID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter data given Meter Number.
     */
    public com.cannontech.multispeak.deploy.service.Meter getMeterByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Service Location.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Account Number.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given Customer ID.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByCustID(java.lang.String custID) throws java.rmi.RemoteException;

    /**
     * Returns the requested Meter(s) data given AMR Type.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getMeterByAMRType(java.lang.String aMRType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the CB a list of names of meter
     * reading groups.  The CB returns an array of strings including the
     * names of the supported meterGroups.  The MR can then request the members
     * of a specific group using the GetMeterGroupMembers method.
     */
    public java.lang.String[] getMeterGroupNames() throws java.rmi.RemoteException;

    /**
     * The Requester requests from the CB a list of names of meter
     * reading groups for a specific meter.  The CB returns an array of strings
     * including the names of the supported meterGroups.  The MR can then
     * request the members of a specific group using the GetMeterGroupMembers
     * method.
     */
    public java.lang.String[] getMeterGroupNamesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the CB the members of a specific
     * meter reading group, identified by the meterGroupName parameter. 
     * This method is used, along with the GetMeterGroupNames method to enable
     * the MR to get information about which meters are included in a specific
     * meter reading group.  The CB returns an array of meterIDs in response
     * to this method call.
     */
    public java.lang.String[] getMeterGroupMembers(java.lang.String meterGroupName) throws java.rmi.RemoteException;

    /**
     * Returns the requested PPMLocation given a customer's accountNumber.
     */
    public com.cannontech.multispeak.deploy.service.PpmLocation getPPMCustomer(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the chargeable devices associated with an account number
     * given a customer's accountNumber.
     */
    public com.cannontech.multispeak.deploy.service.ChargeableDevice[] getChargeableDevicesByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the payment transactions accepted by the CB between
     * the stated start and stop times.  This is an alternative method for
     * obtaining payments and could replace or be in addition to the publish
     * method CommitPaymentTransaction.
     */
    public com.cannontech.multispeak.deploy.service.PaymentTransaction[][] getPPMPayments(java.util.Calendar startTime, java.util.Calendar stopTime) throws java.rmi.RemoteException;

    /**
     * Returns the balance adjustments to be made on the Requester's
     * system by the CB between the stated start and stop times.  This is
     * an alternative method for obtaining balance adjustments and could
     * replace or be in addition to the publish method AdjustPPMBalance.
     */
    public com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[] getPPMBalanceAdjustments(java.util.Calendar startTime, java.util.Calendar stopTime) throws java.rmi.RemoteException;

    /**
     * Returns billing data for all accounts that were billed between
     * the startBillDate and endBillDate.  This method is used by the PPM
     * to reconcile its records with those in the CB.
     */
    public com.cannontech.multispeak.deploy.service.BillingData[] getBillingData(java.util.Calendar startBillDate, java.util.Calendar endBillDate) throws java.rmi.RemoteException;

    /**
     * Returns detailed billing data for a specific account.  This
     * method is used by the PPM to reconcile its records with those in the
     * CB.
     */
    public com.cannontech.multispeak.deploy.service.BillingDetail[] getBillingDetail(java.lang.String accountNumber, java.util.Calendar cisBillDate) throws java.rmi.RemoteException;

    /**
     * Returns details of the billed usage for a specific account.
     * This method is used by the Requester to reconcile its records with
     * those in the CB.
     */
    public com.cannontech.multispeak.deploy.service.BilledUsage getBilledUsage(java.lang.String accountNumber, java.util.Calendar cisBillDate) throws java.rmi.RemoteException;

    /**
     * Returns the installation history for a meter specified by meterID
     * and service type.
     */
    public com.cannontech.multispeak.deploy.service.MeterHistoryEvent[] getMeterHistoryByMeterID(java.lang.String meterID, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the CB a list of payableitems for
     * a specific account.  The CB returns a payableItemList object.
     */
    public com.cannontech.multispeak.deploy.service.PayableItemList getPayableItemsList(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * The Requester requests from the CB a list of convenience fees
     * for a set of transactions sent in the form of a paymentTransactionsList.
     */
    public com.cannontech.multispeak.deploy.service.ConvenienceFeeItem[] getConvenienceFees(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException;

    /**
     * Returns the billing account load data for all line sections/nodes
     * (eaLocs) for a specific month.  The month should be described by an
     * integer (January=1, February=2, etc.) and the year by an integer in
     * four digit format.   The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the objectID of the data instance noted by the
     * server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.BillingAccountLoad[] getBillingAccountLoadDataByMonth(int month, int year, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the objectIDs for service locations for a given customer,
     * by home phone number.
     */
    public java.lang.String[] getServLocIDByHomePhone(java.lang.String homeAc, java.lang.String homePhone) throws java.rmi.RemoteException;

    /**
     * Returns the requested service location ID given the meter number
     * at the service location.
     */
    public java.lang.String getServLocIDByMeter(java.lang.String meterNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested service location ID given the account
     * number at the service location.
     */
    public java.lang.String getServLocIDByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns the requested Customer by home phone number.
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByHomePhone(java.lang.String homePhone, java.lang.String homeAc) throws java.rmi.RemoteException;

    /**
     * Returns all pole data from the CB that have changed since the
     * specified sessionID. The calling parameter previousSessionID should
     * carry the session identifier for the last session of data that the
     * client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Pole[] getModifiedPolesFromCB(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all pole data from the CB. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.Pole[] getAllPolesFromCB(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns pole data from the CB, chosen by pole number.
     */
    public com.cannontech.multispeak.deploy.service.Pole getPoleByPoleNumberFromCB(java.lang.String poleNumber) throws java.rmi.RemoteException;

    /**
     * Returns all transfromer bank data from the CB.  The calling
     * parameter lastReceived is included so that large sets of data can
     * be returned in manageable blocks.  lastReceived should carry an empty
     * string the first time in a session that this method is invoked.  When
     * multiple calls to this method are required to obtain all of the data,
     * the lastReceived should carry in subsequent calls the objectID of
     * the data instance noted by the server as being the lastSent. If the
     * sessionID parameter is set in the message header, then the server
     * shall respond as if it were being asked for a GetModifiedXXX since
     * that sessionID; if the sessionID is not included in the method call,
     * then all instances of the object shall be returned in response to
     * the call.
     */
    public com.cannontech.multispeak.deploy.service.TransformerBank[] getAllTransformerBanksFromCB(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all transfromer bank data from the CB that has been
     * modified since the specified sessionID. The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.TransformerBank[] getModifiedTransformerBanksFromCB(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns transformer bank data from the CB, chosen by bankID
     * (objectID of transformerBank).
     */
    public com.cannontech.multispeak.deploy.service.TransformerBank getTransformerBankByBankIDFromCB(java.lang.String bankID) throws java.rmi.RemoteException;

    /**
     * Returns transformer bank data from the CB, chosen by unitID
     * (objectID of constituent unit of transformerBank).
     */
    public com.cannontech.multispeak.deploy.service.TransformerBank getTransformerBankByUnitIDFromCB(java.lang.String unitID) throws java.rmi.RemoteException;

    /**
     * Returns customer usage data from the CB, chosen by calendar
     * month number (January =1, February=2, etc.)The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByMonth(int monthNumber, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns customer usage data from the CB, chosen by servLoc
     * (objectID of serviceLocation.
     */
    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns customer usage data from the CB, chosen by account
     * number.
     */
    public com.cannontech.multispeak.deploy.service.Usage[] getUsageByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns customer usage data from the CB, chosen by meter number.
     */
    public com.cannontech.multispeak.deploy.service.Usage getUsageByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

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
     * the lastSent. If the sessionID parameter is set in the message header,
     * then the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
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
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
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
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meters getMeterByAccountNumberAndServiceType(java.lang.String accountNumber, java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all electric meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.ElectricMeter[] getAllElectricMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all gas meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.GasMeter[] getAllGasMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all water meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.WaterMeter[] getAllWaterMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all propane meters.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent. If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.PropaneMeter[] getAllPropaneMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all electric service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.ElectricServiceLocation[] getAllElectricServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all gas service locations. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.GasServiceLocation[] getAllGasServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all water service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
     */
    public com.cannontech.multispeak.deploy.service.WaterServiceLocation[] getAllWaterServiceLocations(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all propane service locations. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.If the sessionID parameter
     * is set in the message header, then the server shall respond as if
     * it were being asked for a GetModifiedXXX since that sessionID; if
     * the sessionID is not included in the method call, then all instances
     * of the object shall be returned in response to the call.
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
     * the data instance noted by the server as being the lastSent.
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
     * noted by the server as being the lastSent.
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
     * the data instance noted by the server as being the lastSent.
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
     * the data instance noted by the server as being the lastSent.
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
     * lastSent. If the sessionID parameter is set in the message header,
     * then the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.Customer[] getAllCustomersByServiceType(java.lang.String serviceType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * The MR requests from the CB a specific customer given a meter
     * number and serviceType (electric, gas, water, or propane). The CB
     * returns a customer object.
     */
    public com.cannontech.multispeak.deploy.service.Customer getCustomerByMeterNumberAndServiceType(java.lang.String meterNo, java.lang.String serviceType) throws java.rmi.RemoteException;
}
