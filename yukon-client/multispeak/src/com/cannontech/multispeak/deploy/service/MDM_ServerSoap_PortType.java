/**
 * MDM_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MDM_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Returns the meter connectivity for all meters on the same transformer
     * as the given meter number, including the meter being requested.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getSiblingMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Requester establishes a new readingSchedule on the server.
     * The server returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishReadingSchedules(com.cannontech.multispeak.deploy.service.ReadingSchedule[] readingSchedules) throws java.rmi.RemoteException;

    /**
     * Requester enables a previously established readingSchedule
     * on the server, specified by sending the readingScheduleID.  This action
     * instructs the server to begin taking readings based on this readingSchedule.
     * The server returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] enableReadingSchedule(java.lang.String readingScheduleID) throws java.rmi.RemoteException;

    /**
     * Requester disables a previously established readingSchedule
     * on the server, specified by sending the readingScheduleID. This action
     * instructs the server to stop taking readings based on this readingSchedule.
     * The server returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] disableReadingSchedule(java.lang.String readingScheduleID) throws java.rmi.RemoteException;

    /**
     * Requester deletes a previously established readingSchedule
     * on the server, specified by sending the readingSscheduleID.  The server
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] deleteReadingSchedule(java.lang.String readingScheduleID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to schedule a meter reading on a group of meters,
     * to obtain specific types of meter data, specified by fieldName.  Valid
     * values for fieldName are those that are specified in the most current
     * formattedBlock Implementation Guidelines Document, as issued by the
     * MultiSpeak Initiative.  The expiration time parameter indicates the
     * amount of time for which the MR should try to obtain and publish the
     * data; if the MR has been unsuccessful in publishing the data after
     * the expiration time (specified in seconds), then the publisher will
     * discard the request and the requestor should not expect a response.
     * Meter readings are subsequently published to the requestor using the
     * FormattedBlockNotification method and sent to the URL specified in
     * the responseURL parameter.  The transactionID calling parameter links
     * this Initiate request with the published data method call.  The requestor
     * may specify a preferred format for the returned formattedBlock using
     * the formattedBlockTemplateName parameter.  If the publisher supports
     * this template, the data should be returned in that format; if not
     * the publisher should still return the data, but in its perferred formattedBlockTemplate
     * format. Error handling may be of six types for this method: 1)The
     * publisher cannot read any of the reading types requested - Expected
     * response: The publisher should return an errorObject in the synchronous
     * response to the Initiate message (a synchronous error response). 2)
     * The publisher can read only some of the values being requested - Expected
     * Response: The publisher should send what it can, without flagging
     * an error condition. 3) The meter for which data is being requested
     * does not exist in the publisher's system - Expected Response: A synchronous
     * error (see definition in point 1 above) should be returned. 4) The
     * publisher is too busy to respond or too much information is being
     * requested - Expected Response: A synchronous error (see definition
     * in point 1 above) should be returned. 5) The request is delayed beyond
     * the specified expiration time - Expected Response: The publisher closes
     * the request and does not make an error response. 6) The publisher
     * tried and failed to successfully make the readings - Expected Response:
     * The publisher should send an asynchronous return message (either a
     * ReadingChangedNotification or FormattedBlockNotification) with the
     * errorString attribute of the meterRead set.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadsByFieldName(java.lang.String[] meterNumbers, java.lang.String[] fieldNames, java.lang.String responseURL, java.lang.String transactionID, float expirationTime, java.lang.String formattedBlockTemplateName) throws java.rmi.RemoteException;

    /**
     * Client requests server to return outage detection events that
     * are known to be of type Outage or Inferred on an array of service
     * locations. Server responds by publishing a revised outageDetectionEvent
     * (using the ODEventNotification method on OA_Server)to the URL specified
     * in the responseURL parameter.  Server returns information about failed
     * transactions using an array of errorObjects.  The transactionID calling
     * parameter is included to link a returned ODEventNotification with
     * this request.The expiration time parameter indicates the amount of
     * time for which the publisher should try to obtain and publish the
     * data; if the publisher has been unsuccessful in publishing the data
     * after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODEventRequestByServiceLocation(java.lang.String[] servLoc, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Allow requester to modify OD data for a specific outage detection
     * device object.  If this transaction failes,OD returns information
     * about the failure using a SAOPFault.
     */
    public void modifyODDataForOutageDetectionDevice(com.cannontech.multispeak.deploy.service.OutageDetectionDevice oDDevice) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of a change in the customer object by
     * sending the changed customer object.MDM returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Requests that PPM process a set of meter exchnages.A meterExchange
     * shall be a paired transaction of a meter being removed and a meter
     * being installed in the same meter base. PPM returns information about
     * failed transactions in the form of an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] PPMMeterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] changeouts) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that messages should be shown
     * on the associated in-home display.  Subscriber returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayMessageNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayMessage message) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that billing messages should
     * be shown on the associated in-home display.  Subscriber returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayBillingMessageNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayBillingMessage message) throws java.rmi.RemoteException;

    /**
     * Publisher notifies PPM to enroll customers in pre-paid metering.
     * PPM returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] enrollPPMCustomer(com.cannontech.multispeak.deploy.service.PpmLocation[] newPPMCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher notifies PPM to unenroll customers in pre-paid metering.
     * PPM returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] unenrollPPMCustomer(com.cannontech.multispeak.deploy.service.PpmLocation[] newPPMCustomers) throws java.rmi.RemoteException;

    /**
     * Requester establishes a new in-home display group on the server.
     * The server returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishIHDGroup(com.cannontech.multispeak.deploy.service.InHomeDisplayGroup IHDGroup) throws java.rmi.RemoteException;

    /**
     * Requester deletes a previously established in-home display
     * group on the server, specified by sending the inHomeDisplayGroupID.
     * The server returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] deleteIHDGroup(java.lang.String IHDGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests that CB process a set of transactions that
     * the PP has previously authorized and that have been sent as part of
     * a paymentTransactionsList.  CB returns information about the transactions
     * to the Publisher.
     */
    public com.cannontech.multispeak.deploy.service.PaymentTransaction[] commitPaymentTransaction(com.cannontech.multispeak.deploy.service.PaymentTransaction[] transactions) throws java.rmi.RemoteException;

    /**
     * Notifies the PPM that changes have occurred in chargeable devices.
     * PPM returns information about failed transactions in the form of an
     * array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] chargeableDeviceChangedNotification(com.cannontech.multispeak.deploy.service.ChargeableDeviceList[] deviceList) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber to add the associated load mangement
     * device(s). GIS returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceAddNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] addedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of a change in load mangement
     * device(s)by sending the changed loadManagementDevice object(s).  GIS
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceChangedNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] changedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that load mangement device(s)
     * have been deployed or exchanged.  CB returns information about failed
     * transactions in an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.LMDeviceExchange[] LMDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that load management device(s)
     * have been installed. CB returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] installedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber to remove the associated load
     * mangement device(s).  GIS returns information about failed transactions
     * using an array of errorObjects.The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] removedLMDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber that the associated load mangement
     * devices(s)have been retired from the system.  GIS returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] LMDeviceRetireNotification(com.cannontech.multispeak.deploy.service.LoadManagementDevice[] retiredLMDs) throws java.rmi.RemoteException;

    /**
     * Requests that receiver process a set of balance adjustment
     * transactions. Receiver returns information about failed transactions
     * in the form of an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] adjustPPMBalance(com.cannontech.multispeak.deploy.service.PpmBalanceAdjustment[] adjustments) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of a change in the serviceLocation object
     * by sending the changed serviceLocation object.MDM returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of a change in the meter object by sending
     * the changed meter object.  MDM returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to remove the associated meter(s). 
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in analog values by sending
     * an array of changed scadaAnalog objects.  DAD returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotification(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in point status by sending
     * an array of changed scadaStatus objects.  DAD returns failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAStatusChangedNotification(com.cannontech.multispeak.deploy.service.ScadaStatus[] scadaStatuses) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in SCADA point definitions
     * by sending an array of changed scadaPoint objects.  DAD returns failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotification(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in SCADA point definitions,
     * limited to Analog points, by sending an array of changed scadaPoint
     * objects.  DAD returns failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForAnalog(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in SCADA point definitions,
     * limited to Status points, by sending an array of changed scadaPoint
     * objects.  DAD returns failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAPointChangedNotificationForStatus(com.cannontech.multispeak.deploy.service.ScadaPoint[] scadaPoints) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in a specific analog value,
     * chosen by scadaPointID, by sending a changed scadaAnalog object. 
     * If this transaction fails, DAD returns information about the failure
     * in a SOAPFault.  The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAAnalogChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaAnalog scadaAnalog) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in a specific analog value,
     * limited to power analogs, by sending an arrray of changed scadaAnalog
     * objects.  DAD returns failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForPower(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changed analog values, limited
     * to voltage analogs, by sending an array of changed scadaAnalog objects.
     * DAD returns failed transactions using an array of errorObjects. The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] SCADAAnalogChangedNotificationForVoltage(com.cannontech.multispeak.deploy.service.ScadaAnalog[] scadaAnalogs) throws java.rmi.RemoteException;

    /**
     * SCADA Notifies subscriber of changes in the status of a specific
     * point, chosen by PointID, by sending a changed scadaStatus object.
     * If this transaction fails, DAD returns information about the failure
     * in a SOAPFault. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public void SCADAStatusChangedNotificationByPointID(com.cannontech.multispeak.deploy.service.ScadaStatus scadaStatus) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that the associated meter(s)have been
     * retired from the system.  MDM returns information about failed transactions
     * using an array of errorObjects.  The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to Add the associated meter(s).MDM returns
     * information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that meter(s) have been deployed or
     * exchanged. A meterExchange shall be a paired transaction of a meter
     * being removed and a meter being installed in the same meter base.
     * MDM returns information about failed transactions in an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of new outages by sending the new lists
     * of CustomersAffectedByOutage.  MDM returns status of failed tranactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customersAffectedByOutageNotification(com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage[] newOutages) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of the modified connectivity of meters
     * after a switching event used to restore load during outage situations.
     * MDM returns status of failed tranactions in an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterConnectivityNotification(com.cannontech.multispeak.deploy.service.MeterConnectivity[] newConnectivity) throws java.rmi.RemoteException;

    /**
     * EDR Notifies MDM of a received end device (meter) shipment
     * by sending the changed endDeviceShipment object.  MDM returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] endDeviceShipmentNotification(com.cannontech.multispeak.deploy.service.EndDeviceShipment shipment) throws java.rmi.RemoteException;

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
     * Publisher notifies OA of a change in OutageDetectionEvents
     * by sending an array of changed OutageDetectionEvent objects.  OA returns
     * information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODEventNotification(com.cannontech.multispeak.deploy.service.OutageDetectionEvent[] ODEvents, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies OA of a change in OutageDetectionDevice
     * by sending an array of changed OutageDetectionDevice objects.  OA
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] ODDeviceChangedNotification(com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] ODDevices) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to add the associated connect/disconnect
     * device(s). MDM returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceAddNotification(com.cannontech.multispeak.deploy.service.CDDevice[] addedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of a change in connect/disconnect device(s).
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceChangedNotification(com.cannontech.multispeak.deploy.service.CDDevice[] changedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that connect/disconnect device(s) have
     * been deployed or exchanged.  MDM returns information about failed
     * transactions in an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceExchangeNotification(com.cannontech.multispeak.deploy.service.CDDeviceExchange[] CDDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to remove the associated connect/disconnect
     * device(s).  MDM returns information about failed transactions using
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRemoveNotification(com.cannontech.multispeak.deploy.service.CDDevice[] removedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that connect/disconnect device(s) have
     * been installed. MDM returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceInstalledNotification(com.cannontech.multispeak.deploy.service.CDDevice[] installedCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that the associated connect/disconnect
     * devices(s)have been retired from the system.  MDM returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDDeviceRetireNotification(com.cannontech.multispeak.deploy.service.CDDevice[] retiredCDDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies Subscriber of a change in the History Log
     * by sending the changed historyLog object.  MDM returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] historyLogChangedNotification(com.cannontech.multispeak.deploy.service.HistoryLog[] changedHistoryLogs) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies MDM of a change in the LoadProfile object(s)
     * by sending the changed profileObject(s).  MDM returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] loadProfileChangedNotification(com.cannontech.multispeak.deploy.service.ProfileObject[] changedLoadProfiles) throws java.rmi.RemoteException;

    /**
     * EDT Notifies MDM of a completion of tests on electric meters
     * by sending the meterTestList object.  MDM returns information about
     * failed transactions in an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterTestTransaction(com.cannontech.multispeak.deploy.service.MeterTest[] test) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that meter(s) have been installed. MDM
     * returns information about failed transactions using an array of errorObjects.The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterInstalledNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

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
     * Client notifies MDM of a change in meter reads by sending the
     * changed meterRead objects.  CB returns information about failed transactions
     * in an array of errorObjects.The transactionID calling parameter links
     * this Initiate request with the published data method call.The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] readingChangedNotification(com.cannontech.multispeak.deploy.service.MeterRead[] changedMeterReads, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to add the associated in-home display(s).
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayAddNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of a change in in-home display(s) by
     * sending the changed inHomeDisplay object.  MDM returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayChangedNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] changedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that in-home displays(s) have been deployed
     * or exchanged.  MDM returns information about failed transactions in
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that in home display(s) have been installed.
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayInstalledNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM to remove the associated in-home displays(s).
     * MDM returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRemoveNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] removedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM that the associated in-home display(s)have
     * been retired from the system.  MDM returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRetireNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] retiredIHDs) throws java.rmi.RemoteException;

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
     * Publisher notifies MR of a change in the meterBase object by
     * sending the changed meterBase object.  MR returns information about
     * failed transactions using an array of errorObjects. The message header
     * attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseChangedNotification(com.cannontech.multispeak.deploy.service.MeterBase[] changedMBs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to remove the associated meterBase(4s).
     * MR returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseRemoveNotification(com.cannontech.multispeak.deploy.service.MeterBase[] removedMBs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated meterBase(es)have
     * been retired from the system.  MR returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseRetireNotification(com.cannontech.multispeak.deploy.service.MeterBase[] retiredMBs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to add the associated meterBase(es).MR
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseAddNotification(com.cannontech.multispeak.deploy.service.MeterBase[] addedMBs) throws java.rmi.RemoteException;

    /**
     * Publisher Notifies subscriber of a change in the connect disconnect
     * event object(s) by sending the changed connectDisconnectEvent object(s).
     * Subscriber returns information about failed transactions using an
     * array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] connectDisconnectChangedNotification(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] changedCDEvents) throws java.rmi.RemoteException;

    /**
     * Returns all substation names.
     */
    public java.lang.String[] getSubstationNames() throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements fed by a given line section or
     * node (eaLoc).   The calling parameter lastReceived is included so
     * that large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry the objectID
     * of the last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getDownlineCircuitElements(java.lang.String eaLoc, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements in the shortest route to source from
     * the given line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getUplineCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately fed by the given line
     * section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getChildCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns circuit elements immediately upstream of the given
     * line section or node (eaLoc).
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getParentCircuitElements(java.lang.String eaLoc) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getAllCircuitElements(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all circuit elements that have been modified since
     * the previous session identified. The calling parameter previousSessionID
     * should carry the session identifier for the last session of data that
     * the client has successfully received.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, lastReceived should
     * carry the objectID of the last data instance received in subsequent
     * calls.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getModifiedCircuitElements(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters downline from
     * a given meterNo.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getDownlineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Finds the first upline distribution transformer from a given
     * meter number and returns the meter connectivity for all meters cnnected
     * to it.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getUplineMeterConnectivity(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * This method returns the minimum set of connectivity data necessary
     * for AMR systems to group meters on portions of the power system. 
     * This method will not return a complete set of connectivity data; it
     * returns only circuit elements downline of a specific substation, overcurrentDeviceBank,
     * transformer, or serviceLoaction.  Device tree connectivity will not
     * include objects unnecessary to group meters, such as line sections.
     * The MR requests devices downline of a specific object by passing the
     * name and noun type for that object.  For instance, if the MR wishes
     * to receive the minimal device tree downline of a specific substation,
     * named the West substation, it would send name = West and noun = substation.
     * EA would return the minimal device tree downline of the West substation
     * in the form of an array of circuit elements.  The MR can find the
     * instances of the objects of interest using the GetDomainMembers method
     * passing one of the parameters: substation, overcurrentDeviceBank,
     * transformerBank, or serviceLocation.
     */
    public com.cannontech.multispeak.deploy.service.CircuitElement[] getDeviceTreeConnectivity(java.lang.String name, java.lang.String noun) throws java.rmi.RemoteException;

    /**
     * Returns the meter connectivity for all meters served from a
     * given substation.  The client requests the data by passing a substation
     * name, which it previously received using the GetSubstationNames method.
     */
    public com.cannontech.multispeak.deploy.service.MeterConnectivity[] getMeterConnectivityBySubstation(java.lang.String substationName) throws java.rmi.RemoteException;

    /**
     * Returns the endDeviceShipment object given the shipmentID.
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByShipmentID(java.lang.String shipmentID) throws java.rmi.RemoteException;

    /**
     * Returns the endDeviceShipments given a date range.
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipment[] getEndDeviceShipmentsByDateRange(java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns the endDeviceShipment object given the identifier for
     * a meter that was included in that shipment.
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByMeterID(java.lang.String meterID) throws java.rmi.RemoteException;

    /**
     * Returns the endDeviceShipment object given the transponderID
     * for a meter that was included in that shipment.
     */
    public com.cannontech.multispeak.deploy.service.EndDeviceShipment getEndDeviceShipmentByTransponderID(java.lang.String transponderID) throws java.rmi.RemoteException;

    /**
     * Returns load profile data, chosen by meter number.
     */
    public com.cannontech.multispeak.deploy.service.ProfileObject[] getLPDataByMeterNumber(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the installation history for a meter specified by meterID
     * and service type.
     */
    public com.cannontech.multispeak.deploy.service.MeterHistoryEvent[] getMeterHistoryByMeterID(java.lang.String meterID, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

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
     * The Requester requests from the MDM a list of names of meter
     * reading groups for a specific meter.  The MDM returns an array of
     * strings including the names of the supported meterGroups.  The requestor
     * can then request the members of a specific group using the GetMeterGroupMembers
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
     * Returns all schedules that have been established on the server.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Schedule[] getSchedules(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a schedule that previously has been established on
     * the server, selected by the scheduleID.
     */
    public com.cannontech.multispeak.deploy.service.Schedule getScheduleByID(java.lang.String scheduleID) throws java.rmi.RemoteException;

    /**
     * Returns all readingSchedules that have been established on
     * the server. The calling parameter lastReceived is included so that
     * large sets of data can be returned in manageable blocks.  lastReceived
     * should carry an empty string the first time in a session that this
     * method is invoked.  When multiple calls to this method are required
     * to obtain all of the data, the lastReceived should carry in subsequent
     * calls the objectID of the data instance noted by the server as being
     * the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule[] getReadingSchedules(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns a readingSchedule that previously has been established
     * on the server, selected by the scheduleID.
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule getReadingScheduleByID(java.lang.String readingScheduleID) throws java.rmi.RemoteException;

    /**
     * Returns the latest readings for a list of meters for a specific
     * date range and reading type desired.Reading types may be specified
     * using the fieldName parameter.  Valid values for fieldName are those
     * that are specified in the most current formattedBlock Implementation
     * Guidelines Document, as issued by the MultiSpeak Initiative. The requestor
     * may specify a preferred format for the returned formattedBlock using
     * the formattedBlockTemplateName parameter.  If the publisher supports
     * this template, the data should be returned in that format; if not
     * the publisher should still return the data, but in its perferred formattedBlockTemplate
     * format.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getLatestReadingsByMeterNoList(java.lang.String[] meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived, com.cannontech.multispeak.deploy.service.ServiceType serviceType, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

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
     * Returns the requested meter base data given the objectID of
     * the meterBase.
     */
    public com.cannontech.multispeak.deploy.service.MeterBase getMeterBaseByObjectId(java.lang.String meterBaseID) throws java.rmi.RemoteException;

    /**
     * Returns the templates for formattedBlocks that the server supports.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlockTemplate[] getFormattedBlockTemplates(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the latest readings for a list of meters for a specific
     * date range and specific types of meter data, specified by fieldName.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.   The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getLatestReadingsByMeterNoListFormattedBlock(java.lang.String[] meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName, java.lang.String lastReceived, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

    /**
     * Publisher requests subscriber to add in home display(s) to
     * an existing group of in home displays to address as a group.  Subscriber
     * returns information about failed transaction using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] insertInHomeDisplayInIHDGroup(java.lang.String[] inHomeDisplayIDs, java.lang.String IHDGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests subscriber to remove in home display(s)
     * from an existing group of in home displays to address as a group.
     * Subscriber returns information about failed transaction using an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] removeInHomeDisplayFromIHDGroup(java.lang.String[] inHomeDisplays, java.lang.String IHDGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate a load management
     * event via the loadManagementEvent object.  If substation and feeder
     * information is included in the loadManagementEvent, then the LM should
     * attempt to control the requested amount of load in that substation/feeder
     * area.  Otherwise, the controlled load should be spread over the entire
     * system. If this transaction should fail, LM returns information about
     * the failure using a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject initiateLoadManagementEvent(com.cannontech.multispeak.deploy.service.LoadManagementEvent theLMEvent) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate an array of load
     * management events via multiple loadManagementEvent objects.  If substation
     * and feeder information is included in the loadManagementEvent, then
     * the LM should attempt to control the requested amount of load in that
     * substation/feeder area.  Otherwise, the controlled load should be
     * spread over the entire system. If this transaction should fail, LM
     * returns information about the failure using a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateLoadManagementEvents(com.cannontech.multispeak.deploy.service.LoadManagementEvent[] theLMEvents) throws java.rmi.RemoteException;

    /**
     * Publisher calls this LM service to initiate a power factor
     * management event via the powerFactorManagementEvent object.  If this
     * transaction should fail, LM returns information about the failure
     * using a SOAPFault.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject initiatePowerFactorManagementEvent(com.cannontech.multispeak.deploy.service.PowerFactorManagementEvent thePFMEvent) throws java.rmi.RemoteException;

    /**
     * Notify MDM of planned outage meters given a List of meterNumbers
     * and start and end dates of the outage. MR returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiatePlannedOutage(java.lang.String[] meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Notify MDM of cancellation of planned outage given a list of
     * meterNumbers.  MR returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelPlannedOutage(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MDM of meters where zero usage is expected.(ie move
     * outs).  MDM returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MDM of cancellation Of zero usage monitoring.(ie move
     * Ins).  MDM returns information about failed transactions using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of meters that have been disconnected
     * and no AMR reading is expected.  MDM returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MDM of meters that should be removed from
     * disconnected status.(i.e. made active).  MDM returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Client requests a new meter reading from MDM, on meters selected
     * by meter number.  MDM returns information about failed transactions
     * using an array of errorObjects. The meter read is returned to the
     * CB in the form of a meterRead or formattedBlock, sent to the URL specified
     * in the responseURL.  The transactionID calling parameter links this
     * Initiate request with the published data method call.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNumber(java.lang.String[] meterNos, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to establish a new group of meters to
     * address as a meter group.  MDM returns information about failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishMeterGroup(com.cannontech.multispeak.deploy.service.MeterGroup meterGroup) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to eliminate a previously defined group
     * of meters to address as a meter group.  MDM returns information about
     * failed transactions using an errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject deleteMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to add meter(s) to an existing group
     * of meters to address as a meter group.  MDM returns information about
     * failed transaction using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] insertMeterInMeterGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to remove meter(s) from an existing
     * group of meters to address as a meterroup.  MDM returns information
     * about failed transaction using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] removeMetersFromMeterGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * CB requests MR to schedule a meter reading on a group of meters,
     * referred to by meter reading group name.  MR returns an array of errorObjects
     * to signal failed transaction(s). Meter readings are subsequently published
     * to the CB using the FormattedBlockNotification method and sent to
     * the URL specified in the responseURL parameter.The transactionID calling
     * parameter links this Initiate request with the published data method
     * call.The expiration time parameter indicates the amount of time for
     * which the publisher should try to obtain and publish the data; if
     * the publisher has been unsuccessful in publishing the data after the
     * expiration time (specified in seconds), then the publisher will discard
     * the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateGroupMeterRead(java.lang.String meterGroupName, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB requests MDM to schedule meter readings for a group of meters.
     * MR returns information about failed transactions using an array of
     * errorObjects.  MDM returns meter readings when they have been collected
     * using the FormattedBlockNotification method sent to the URL specified
     * in the responseURL parameter.The transactionID calling parameter links
     * this Initiate request with the published data method call.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] scheduleGroupMeterRead(java.lang.String meterGroupName, java.util.Calendar timeToRead, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Request MDM to perform a meter reading for specific meter numbers
     * and reading type. MDM returns information about failed transactions
     * using an array of errorObjects.  The MR subsequently returns the data
     * collected by publishing formattedBlocks to the EA at the URL specified
     * in the responseURL parameter.The transactionID parameter is supplied
     * to link the returned formattedBlock(s) with this Initiate request.The
     * expiration time parameter indicates the amount of time for which the
     * publisher should try to obtain and publish the data; if the publisher
     * has been unsuccessful in publishing the data after the expiration
     * time (specified in seconds), then the publisher will discard the request
     * and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNoAndType(java.lang.String meterNo, java.lang.String responseURL, java.lang.String readingType, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * EA requests MDM to to perform a meter reading for all meters
     * down line of the circuit element supplied using the calling parameters
     * objectName and nounType and containing the phasing supplied in the
     * calling parameter phaseCode. The MR subsequently returns the data
     * collected by publishing formattedBlocks to the EA at the URL specified
     * in the responseURL parameter.  The transactionID parameter is supplied
     * to link the returned formattedBlock(s) with this Initiate request.The
     * expiration time parameter indicates the amount of time for which the
     * publisher should try to obtain and publish the data; if the publisher
     * has been unsuccessful in publishing the data after the expiration
     * time (specified in seconds), then the publisher will discard the request
     * and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * OA requests OD to update the status of an outageDetectionDevice.
     * OD responds by publishing a revised outageDetectionEvent (using the
     * ODEventNotification method on OA-OD) to the URL specified in the responseURL
     * parameter.  OD returns information about failed transactions using
     * an array of errorObjects. The transactionID calling parameter is included
     * to link a returned ODEventNotification with this request.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateOutageDetectionEventRequest(java.lang.String[] meterNos, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * OA requests OD to return only outage detection events that
     * are known to be of type Outage or Inferred on service locations downline
     * from a circuit element supplied using the calling parameters objectName
     * and nounType and containing the phasing supplied in the calling parameter
     * phaseCode. OD responds by publishing a revised outageDetectionEvent
     * (using the ODEventNotification method on OA-OD)to the URL specified
     * in the responseURL parameter.  OD returns information about failed
     * transactions using an array of errorObjects.The transactionID calling
     * parameter is included to link a returned ODEventNotification with
     * this request.The expiration time parameter indicates the amount of
     * time for which the publisher should try to obtain and publish the
     * data; if the publisher has been unsuccessful in publishing the data
     * after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODEventRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * OA requests OD to return only outage detection events that
     * are known to be of type Outage or Inferred on service locations downline
     * from a circuit element supplied using the calling parameters objectName
     * and nounType. OD creates a monitoring event for the specified circuit
     * element.  Monitoring shall be performed at the time interval given
     * in the periodicity parameter (expressed in minutes).  OD responds
     * by publishing a revised outageDetectionEvent (using the ODEventNotification
     * method on OA-OD)to the URL specified in the responseURL parameter.
     * OD returns information about failed transactions using an array of
     * errorObjects.The transactionID calling parameter is included to link
     * a returned ODEventNotification with this request.The expiration time
     * parameter indicates the amount of time for which the publisher should
     * try to obtain and publish the data; if the publisher has been unsuccessful
     * in publishing the data after the expiration time (specified in seconds),
     * then the publisher will discard the request and the requestor should
     * not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateODMonitoringRequestByObject(java.lang.String objectName, java.lang.String nounType, com.cannontech.multispeak.deploy.service.PhaseCd phaseCode, int periodicity, java.util.Calendar requestDate, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Requester requests OD to return a list of circuit elements
     * (in the form of objectRefs) that are currently being monitored.  OD
     * returns an array of objectRefs.
     */
    public com.cannontech.multispeak.deploy.service.ObjectRef[] displayODMonitoringRequests() throws java.rmi.RemoteException;

    /**
     * Requester requests OD to cancel outage detection monitoring
     * on the list of supplied circuit elements (called out by objectRef).
     * OD returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelODMonitoringRequestByObject(com.cannontech.multispeak.deploy.service.ObjectRef[] objectRef, java.util.Calendar requestDate) throws java.rmi.RemoteException;

    /**
     * CB initiates a connect or disconnect action by issuing one
     * or more connectDisconnectEvent objects to the CD.  CD returns information
     * about failed transactions by returning an array of errorObjects. 
     * The connect/disconnect function returns infromation about this action
     * using the CDStateChangedNotification to the URL specified in the responseURL
     * calling parameter and references the transactionID specified to link
     * the transaction to this Initiate request.The expiration time parameter
     * indicates the amount of time for which the publisher should try to
     * obtain and publish the data; if the publisher has been unsuccessful
     * in publishing the data after the expiration time (specified in seconds),
     * then the publisher will discard the request and the requestor should
     * not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateConnectDisconnect(com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] cdEvents, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of state change for a connect/disconnect
     * device By meterNumber and loadActionCode.  The transactionID calling
     * parameter can be used to link this action with an InitiateConectDisconnect
     * call.  If this transaction fails, CB returns information about the
     * failure in a SOAPFault.The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public void CDStateChangedNotification(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.LoadActionCode stateChange, java.lang.String transactionID, java.lang.String errorString) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of state change(s) for connect/disconnect
     * device(s).  The transactionID calling parameter can be used to link
     * this action with an InitiateConectDisconnect call.  If this transaction
     * fails, CB returns information about the failure in an array of errorObject(s).The
     * message header attribute 'registrationID' should be added to all publish
     * messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] CDStatesChangedNotification(com.cannontech.multispeak.deploy.service.CDStateChange[] stateChanges, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Publisher requests subscriber to to update the in-home display
     * associated with a specific service location.  Subscriber returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] updateServiceLocationDisplays(java.lang.String servLocID) throws java.rmi.RemoteException;

    /**
     * Publisher notifies CB that history comment(s) should be written
     * to a customer account. CB returns failed transactions using an errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] writeAccountHistoryComments(com.cannontech.multispeak.deploy.service.HistoryComment[] comments) throws java.rmi.RemoteException;

    /**
     * CB initiates a switch status check directly from one or more
     * Connect/Disconnect devices. CD returns information about failed transactions
     * by returning an array of errorObjects.  The CD switch state check
     * function returns information asynchronously about this switch state
     * using either the CDStateNotification (for only one device) or the
     * CDStatesNotification (for one or more devices) to the URL specified
     * in the responseURL calling parameter and references the transactionID
     * specified to link the transaction to this Initiate request.The expiration
     * time parameter indicates the amount of time for which the publisher
     * should try to obtain and publish the data; if the publisher has been
     * unsuccessful in publishing the data after the expiration time (specified
     * in seconds), then the publisher will discard the request and the requestor
     * should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateCDStateRequest(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher notifies subscriber of a change in OutageEvent by
     * sending an array of changed OutageEvent objects.  DGV returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] outageEventChangedNotification(com.cannontech.multispeak.deploy.service.OutageEvent[] oEvents) throws java.rmi.RemoteException;

    /**
     * CB initiates arming of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateArmCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates enabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateEnableCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * CB initiates disabling of one or more Connect/Disconnect devices.
     * CD returns information about failed transactions by returning an array
     * of errorObjects.  The CD function returns information asynchronously
     * about this switch action using either the CDStateChangedNotification
     * (for only one device) or the CDStatesChangedNotification (for one
     * or more devices) to the URL specified in the responseURL calling parameter
     * and references the transactionID specified to link the transaction
     * to this Initiate request.The expiration time parameter indicates the
     * amount of time for which the publisher should try to obtain and publish
     * the data; if the publisher has been unsuccessful in publishing the
     * data after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDisableCDDevice(com.cannontech.multispeak.deploy.service.CDState[] states, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Requester initiates a demand reset on one or more meters specified
     * by meter identifer.  MR returns information about failed transactions
     * using an array of errorObjects. The MR server confirms the action
     * has been taken by publishing a MeterEventNotification to the Requester
     * sent to the URL specified in the responseURL.  The transactionID calling
     * parameter links this Initiate request with the MeterEventNotification
     * method call.The expiration time parameter indicates the amount of
     * time for which the publisher should try to obtain and publish the
     * data; if the publisher has been unsuccessful in publishing the data
     * after the expiration time (specified in seconds), then the publisher
     * will discard the request and the requestor should not expect a response.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDemandReset(com.cannontech.multispeak.deploy.service.MeterIdentifier[] meterIDs, java.lang.String responseURL, java.lang.String transactionID, float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to add meter(s) to an existing configuration
     * group.  MDM returns information about failed transaction using an
     * array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] insertMetersInConfigurationGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

    /**
     * Publisher requests MDM to remove meter(s) from an existing
     * configuration group.  MDM returns information about failed transaction
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] removeMetersFromConfigurationGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID, com.cannontech.multispeak.deploy.service.ServiceType serviceType) throws java.rmi.RemoteException;

    /**
     * Requester establishes a new schedule on the server.  The server
     * returns information about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishSchedules(com.cannontech.multispeak.deploy.service.Schedule[] schedules) throws java.rmi.RemoteException;

    /**
     * Requester deletes a previously established schedule on the
     * server, specified by sending the scheduleID.  The server returns information
     * about failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] deleteSchedule(java.lang.String scheduleID) throws java.rmi.RemoteException;

    /**
     * Requester pings URL of MDM to see if it is alive.   Returns
     * errorObject(s) as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests a list of methods supported by MDM.
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
     * calling RequestRegistrationID, responseURL xx the URL to which information
     * should subsequently be published on this subscription, msFunction
     * xx the abbreviated string name of the MultiSpeak method making the
     * subscription request (for instance, if an application that exposes
     * the Meter Reading function has made the request, then the msFunction
     * variable should include xxxxxx), methodsList xx An array of strings that
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
     * Returns all connectDisconnectEvents in the specified date range
     * for the specified reasonCode.  See the connectDisconnectCode.reasonCode
     * enumeration for acceptable values.
     */
    public com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent[] getAllConnectDisconnectEventsByReasonCode(java.lang.String reasonCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns all in home display objects. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.ReadingSchedule getAllInHomeDisplays(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Requests that PPM return status information about a set of
     * PPMLocations. PPM returns information in the form of an array of ppmStatus
     * objects.
     */
    public com.cannontech.multispeak.deploy.service.PpmStatus[] getPrePayStatus(java.lang.String[] ppmLocations) throws java.rmi.RemoteException;

    /**
     * Returns the chargeable devices associated with an account number
     * given a customer's accountNumber.
     */
    public com.cannontech.multispeak.deploy.service.ChargeableDevice[] getChargeableDevicesByAccountNumber(java.lang.String accountNumber) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have AMR.  The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
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
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedAMRMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Return true if given meterNo has AMR. Otherwise return false.
     */
    public boolean isAMRMeter(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the theoretical total amount of load that can be controlled,
     * expressed in kW.
     */
    public float getAmountOfControllableLoad() throws java.rmi.RemoteException;

    /**
     * Returns all required data for all loadManagementDevices.  The
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
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getAllLoadManagementDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested loadManagementDevice(s), chosen by meter
     * number.
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByMeterNumber(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the requested loadManagementDevice(s), chosen by the
     * objectID of the service location (servLoc).
     */
    public com.cannontech.multispeak.deploy.service.LoadManagementDevice[] getLoadManagementDeviceByServLoc(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns true if load management is in effect for a given serviceLocation
     * (chosen by objectID of serviceLocation (servLoc), otherwise false.
     */
    public boolean isLoadManagementActive(java.lang.String servLoc) throws java.rmi.RemoteException;

    /**
     * Returns the amount of load that is currently under control,
     * expressed in kW.
     */
    public float getAmountOfControlledLoad() throws java.rmi.RemoteException;

    /**
     * Returns all of the substation load control statuses
     */
    public com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus[] getAllSubstationLoadControlStatuses() throws java.rmi.RemoteException;

    /**
     * Returns reading data for all meters given a date range. The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns meter reading data for a given MeterNo and date range.
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns the most recent meter reading data for a given MeterNo.
     */
    public com.cannontech.multispeak.deploy.service.MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all required reading data for a given billingCycle
     * and date range in the form of an array of formattedBlocks.  The calling
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
     * calls.Reading types may be specified using the fieldName parameter.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByBillingCycle(java.lang.String billingCycle, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

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
     * calls.Reading types may be specified using the fieldName parameter.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingByMeterNumberFormattedBlock(java.lang.String meterNumber, java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

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
     * calls. Reading types may be specified using the fieldName parameter.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByDateFormattedBlock(java.util.Calendar billingDate, int kWhLookBack, int kWLookBack, int kWLookForward, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns history log data for a given meterNo and date range.
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogByMeterNo(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns history log data for a all meters given a date range.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDate(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns history log data for a given meterNo, eventCode and
     * date range.
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByMeterNoAndEventCode(java.lang.String meterNo, com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Returns history log data for a all meters given the eventCode
     * and a date range.  The calling parameter lastReceived is included
     * so that large sets of data can be returned in manageable blocks. 
     * lastReceived should carry an empty string the first time in a session
     * that this method is invoked.  When multiple calls to this method are
     * required to obtain all of the data, the lastReceived should carry
     * in subsequent calls the objectID of the data instance noted by the
     * server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.HistoryLog[] getHistoryLogsByDateAndEventCode(com.cannontech.multispeak.deploy.service.EventCode eventCode, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns most recent meter reading data for all meters in a
     * given meterGroup, requested by meter group name. Reading types may
     * be specified using the fieldName parameter.  Valid values for fieldName
     * are those that are specified in the most current formattedBlock Implementation
     * Guidelines Document, as issued by the MultiSpeak Initiative. The requestor
     * may specify a preferred format for the returned formattedBlock using
     * the formattedBlockTemplateName parameter.  If the publisher supports
     * this template, the data should be returned in that format; if not
     * the publisher should still return the data, but in its perferred formattedBlockTemplate
     * format. Meter readings are returned in the form of a formattedBlock.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock getLatestMeterReadingsByMeterGroup(java.lang.String meterGroupID, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns the most recent reading data for a given meterNo and
     * reading type. Reading types may be specified using the fieldName parameter.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock getLatestReadingByMeterNoAndType(java.lang.String meterNo, java.lang.String readingType, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns the most recent reading data for a given reading type.
     * Reading types may be specified using the fieldName parameter.  Valid
     * values for fieldName are those that are specified in the most current
     * formattedBlock Implementation Guidelines Document, as issued by the
     * MultiSpeak Initiative. The requestor may specify a preferred format
     * for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.   The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getLatestReadingByType(java.lang.String readingType, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns readings all meters given the date range and reading
     * type desired. Reading types may be specified using the fieldName parameter.
     * Valid values for fieldName are those that are specified in the most
     * current formattedBlock Implementation Guidelines Document, as issued
     * by the MultiSpeak Initiative. The requestor may specify a preferred
     * format for the returned formattedBlock using the formattedBlockTemplateName
     * parameter.  If the publisher supports this template, the data should
     * be returned in that format; if not the publisher should still return
     * the data, but in its perferred formattedBlockTemplate format.  The
     * calling parameter lastReceived is included so that large sets of data
     * can be returned in manageable blocks.  lastReceived should carry an
     * empty string the first time in a session that this method is invoked.
     * When multiple calls to this method are required to obtain all of the
     * data, the lastReceived should carry in subsequent calls the objectID
     * of the data instance noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByDateAndType(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns all reading types supported by the AMR system.
     */
    public java.lang.String[] getSupportedReadingTypes() throws java.rmi.RemoteException;

    /**
     * Returns readings for a given meter for a specific date range
     * and reading type desired. Reading types may be specified using the
     * fieldName parameter.  Valid values for fieldName are those that are
     * specified in the most current formattedBlock Implementation Guidelines
     * Document, as issued by the MultiSpeak Initiative. The requestor may
     * specify a preferred format for the returned formattedBlock using the
     * formattedBlockTemplateName parameter.  If the publisher supports this
     * template, the data should be returned in that format; if not the publisher
     * should still return the data, but in its perferred formattedBlockTemplate
     * format.   The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByMeterNoAndType(java.lang.String meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns the most recent meter reading data for all AMR capable
     * meters.  The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getLatestReadings(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the requested reading data for all meters given unit
     * of measure(UOM) and date range. The calling parameter lastReceived
     * is included so that large sets of data can be returned in manageable
     * blocks.  lastReceived should carry an empty string the first time
     * in a session that this method is invoked.  When multiple calls to
     * this method are required to obtain all of the data, the lastReceived
     * should carry in subsequent calls the objectID of the data instance
     * noted by the server as being the lastSent.
     */
    public com.cannontech.multispeak.deploy.service.MeterRead[] getReadingsByUOMAndDate(java.lang.String uomData, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all outage detection devices. The calling parameter
     * lastReceived is included so that large sets of data can be returned
     * in manageable blocks.  lastReceived should carry an empty string the
     * first time in a session that this method is invoked.  When multiple
     * calls to this method are required to obtain all of the data, the lastReceived
     * should carry the objectID of the last data instance received in subsequent
     * calls.If the sessionID parameter is set in the message header, then
     * the server shall respond as if it were being asked for a GetModifiedXXX
     * since that sessionID; if the sessionID is not included in the method
     * call, then all instances of the object shall be returned in response
     * to the call.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getAllOutageDetectionDevices(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns outage detection devices associated with the given
     * meter number.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByMeterNo(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns all outage detection devices with a given OutageDetectionDeviceStatus.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByStatus(com.cannontech.multispeak.deploy.service.OutageDetectDeviceStatus oDDStatus, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all outage detection devices of a given OutageDetectionDeviceType.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry the objectID of the
     * last data instance received in subsequent calls.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutageDetectionDevicesByType(com.cannontech.multispeak.deploy.service.OutageDetectDeviceType oDDType, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns the outageDetectionDevices that are currently experiencing
     * outage conditions.
     */
    public com.cannontech.multispeak.deploy.service.OutageDetectionDevice[] getOutagedODDevices() throws java.rmi.RemoteException;

    /**
     * Returns all meters that have Connect/Disconnect Capability.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getCDSupportedMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all meters that have Connect/Disconnect Capability
     * and that have been modified since the last identified session.  The
     * calling parameter previousSessionID should carry the session identifier
     * for the last session of data that the client has successfully received.
     * The calling parameter lastReceived is included so that large sets
     * of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.Meter[] getModifiedCDMeters(java.lang.String previousSessionID, java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns Current State of a Connect/Disconnect Device for a
     * given the Meter Number.  The default condition is Closed.
     */
    public com.cannontech.multispeak.deploy.service.LoadActionCode getCDMeterState(java.lang.String meterNo) throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * event ID.  The outageEventID is the objectID of an outageEvent obtained
     * earlier by calling GetActiveOutages.
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatus(java.lang.String outageEventID) throws java.rmi.RemoteException;

    /**
     * Returns the outageEventIDs for all active outage events.  The
     * outageEventID is the objectID of an outageEvent.
     */
    public java.lang.String[] getActiveOutages() throws java.rmi.RemoteException;

    /**
     * Returns the current status of an outage event, given the outage
     * location.  The outageLocation object includes the telephone number,
     * service locationID, account number and/or meter number at the location
     * of the outage.
     */
    public com.cannontech.multispeak.deploy.service.OutageEventStatus getOutageEventStatusByOutageLocation(com.cannontech.multispeak.deploy.service.OutageLocation location) throws java.rmi.RemoteException;

    /**
     * Returns all electric meters.  The calling parameter lastReceived
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
    public com.cannontech.multispeak.deploy.service.ElectricMeter[] getAllElectricMeters(java.lang.String lastReceived) throws java.rmi.RemoteException;

    /**
     * Returns all gas meters.  The calling parameter lastReceived
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
     * noted by the server as being the lastSent. If the sessionID parameter
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
     * noted by the server as being the lastSent. If the sessionID parameter
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
     * noted by the server as being the lastSent. If the sessionID parameter
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
     * noted by the server as being the lastSent. If the sessionID parameter
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
     * lastSent.  If the sessionID parameter is set in the message header,
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
     * the lastSent.  If the sessionID parameter is set in the message header,
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
     * noted by the server as being the lastSent.  If the sessionID parameter
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
}
