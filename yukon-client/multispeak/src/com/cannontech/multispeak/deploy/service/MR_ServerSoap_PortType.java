/**
 * MR_ServerSoap_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

public interface MR_ServerSoap_PortType extends java.rmi.Remote {

    /**
     * Publisher notifies MR that meterBase(es) have been deployed
     * or exchanged.  MR returns information about failed transactions in
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterBaseExchangeNotification(com.cannontech.multispeak.deploy.service.MeterBaseExchange[] MBChangeout) throws java.rmi.RemoteException;

    /**
     * Requester pings URL of MR to see if it is alive.   Returns
     * errorObject(s) as necessary to communicate application status.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] pingURL() throws java.rmi.RemoteException;

    /**
     * Requester requests a list of methods supported by MR.
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
     * calling RequestRegistrationID, responseURL - the URL to which information
     * should subsequently be published on this subscription, msFunction
     * - the abbreviated string name of the MultiSpeak method making the
     * subscription request (for instance, if an application that exposes
     * the Meter Reading function has made the request, then the msFunction
     * variable should include "MR"?), methodsList - An array of strings that
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
     * calls.Reading types may be specified using the fieldName parameter.
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
     * given meterGroup, requested by meter group name.  Meter readings are
     * returned in the form of a formattedBlock.Reading types may be specified
     * using the fieldName parameter.  Valid values for fieldName are those
     * that are specified in the most current formattedBlock Implementation
     * Guidelines Document, as issued by the MultiSpeak Initiative. The requestor
     * may specify a preferred format for the returned formattedBlock using
     * the formattedBlockTemplateName parameter.  If the publisher supports
     * this template, the data should be returned in that format; if not
     * the publisher should still return the data, but in its perferred formattedBlockTemplate
     * format.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock getLatestMeterReadingsByMeterGroup(java.lang.String meterGroupID, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns the most recent reading data for a given meterNo and
     * reading type.Reading types may be specified using the fieldName parameter.
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
     * the data, but in its perferred formattedBlockTemplate format.  The
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
     * type desired.  Reading types may be specified using the fieldName
     * parameter.  Valid values for fieldName are those that are specified
     * in the most current formattedBlock Implementation Guidelines Document,
     * as issued by the MultiSpeak Initiative. The requestor may specify
     * a preferred format for the returned formattedBlock using the formattedBlockTemplateName
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
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getReadingsByDateAndType(java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

    /**
     * Returns all reading types supported by the AMR system.
     */
    public java.lang.String[] getSupportedReadingTypes() throws java.rmi.RemoteException;

    /**
     * Returns readings for a given meter for a specific date range
     * and reading type desired.  Reading types may be specified using the
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
     * date range and reading type desired.  Reading types may be specified
     * using the fieldName parameter.  Valid values for fieldName are those
     * that are specified in the most current formattedBlock Implementation
     * Guidelines Document, as issued by the MultiSpeak Initiative. The requestor
     * may specify a preferred format for the returned formattedBlock using
     * the formattedBlockTemplateName parameter.  If the publisher supports
     * this template, the data should be returned in that format; if not
     * the publisher should still return the data, but in its perferred formattedBlockTemplate
     * format.   The calling parameter lastReceived is included so that large
     * sets of data can be returned in manageable blocks.  lastReceived should
     * carry an empty string the first time in a session that this method
     * is invoked.  When multiple calls to this method are required to obtain
     * all of the data, the lastReceived should carry in subsequent calls
     * the objectID of the data instance noted by the server as being the
     * lastSent.
     */
    public com.cannontech.multispeak.deploy.service.FormattedBlock[] getLatestReadingsByMeterNoList(java.lang.String[] meterNo, java.util.Calendar startDate, java.util.Calendar endDate, java.lang.String readingType, java.lang.String lastReceived, com.cannontech.multispeak.deploy.service.ServiceType serviceType, java.lang.String formattedBlockTemplateName, java.lang.String[] fieldName) throws java.rmi.RemoteException;

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
     * Notify MR of planned outage meters given a List of meterNumbers
     * and start and end dates of the outage. MR returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiatePlannedOutage(java.lang.String[] meterNos, java.util.Calendar startDate, java.util.Calendar endDate) throws java.rmi.RemoteException;

    /**
     * Notify MR of cancellation of planned outage given a list of
     * meterNumbers.  MR returns information about failed transactions using
     * an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelPlannedOutage(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of meters where zero usage is expected.(ie move outs).
     * MR returns information about failed transactions using an array of
     * errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Notify MR of cancellation Of zero usage monitoring.(ie move
     * Ins).  MR returns information about failed transactions using an array
     * of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelUsageMonitoring(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of meters that have been disconnected
     * and no AMR reading is expected.  MR returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of meters that should be removed from
     * disconnected status.(i.e. made active).  MR returns information about
     * failed transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] cancelDisconnectedStatus(java.lang.String[] meterNos) throws java.rmi.RemoteException;

    /**
     * CB requests a new meter reading from MR, on meters selected
     * by meter number.  MR returns information about failed transactions
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
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNumber(java.lang.String[] meterNos, java.lang.String responseURL, java.lang.String transactionID, java.lang.Float expirationTime) throws java.rmi.RemoteException;

    /**
     * Publisher requests MR to establish a new group of meters to
     * address as a meter group.  MR returns information about failed transactions
     * using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] establishMeterGroup(com.cannontech.multispeak.deploy.service.MeterGroup meterGroup) throws java.rmi.RemoteException;

    /**
     * Publisher requests MR to eliminate a previously defined group
     * of meters to address as a meter group.  MR returns information about
     * failed transactions using an errorObject.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject deleteMeterGroup(java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests MR to add meter(s) to an existing group
     * of meters to address as a meter group.  MR returns information about
     * failed transaction using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] insertMeterInMeterGroup(java.lang.String[] meterNumbers, java.lang.String meterGroupID) throws java.rmi.RemoteException;

    /**
     * Publisher requests MR to remove meter(s) from an existing group
     * of meters to address as a meterroup.  MR returns information about
     * failed transaction using an array of errorObjects.
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
     * CB requests MR to schedule meter readings for a group of meters.
     * MR returns information about failed transactions using an array of
     * errorObjects.  MR returns meter readings when they have been collected
     * using the FormattedBlockNotification method sent to the URL specified
     * in the responseURL parameter.The transactionID calling parameter links
     * this Initiate request with the published data method call.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] scheduleGroupMeterRead(java.lang.String meterGroupName, java.util.Calendar timeToRead, java.lang.String responseURL, java.lang.String transactionID) throws java.rmi.RemoteException;

    /**
     * Request MR to perform a meter reading for specific meter numbers
     * and reading type. MR returns information about failed transactions
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
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateMeterReadByMeterNoAndType(java.lang.String meterNo, java.lang.String responseURL, java.lang.String readingType, java.lang.String transactionID, java.lang.Float expirationTime) throws java.rmi.RemoteException;

    /**
     * EA requests MR to to perform a meter reading for all meters
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
     * CB requests MR to to update the in-home display associated
     * with a specific service location.  MR returns information about failed
     * transactions using an array of errorObjects.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] updateServiceLocationDisplays(java.lang.String servLocID) throws java.rmi.RemoteException;

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
    public com.cannontech.multispeak.deploy.service.ErrorObject[] initiateDemandReset(com.cannontech.multispeak.deploy.service.MeterIdentifier[] meterIDs, java.lang.String responseURL, java.lang.String transactionID, java.lang.Float expirationTime) throws java.rmi.RemoteException;

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
     * Publisher notifies MR of a change in the customer object by
     * sending the changed customer object.MR returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customerChangedNotification(com.cannontech.multispeak.deploy.service.Customer[] changedCustomers) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of a change in the serviceLocation object
     * by sending the changed serviceLocation object.MR returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] serviceLocationChangedNotification(com.cannontech.multispeak.deploy.service.ServiceLocation[] changedServiceLocations) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of a change in the meter object by sending
     * the changed meter object.  MR returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterChangedNotification(com.cannontech.multispeak.deploy.service.Meter[] changedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to remove the associated meter(s).  MR
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRemoveNotification(com.cannontech.multispeak.deploy.service.Meter[] removedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated meter(s)have been
     * retired from the system.  MR returns information about failed transactions
     * using an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterRetireNotification(com.cannontech.multispeak.deploy.service.Meter[] retiredMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to Add the associated meter(s).MR returns
     * information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterAddNotification(com.cannontech.multispeak.deploy.service.Meter[] addedMeters) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that meter(s) have been deployed or exchanged.
     * A meterExchange shall be a paired transaction of a meter being removed
     * and a meter being installed in the same meter base. MR returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] meterExchangeNotification(com.cannontech.multispeak.deploy.service.MeterExchange[] meterChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of new outages by sending the new lists
     * of CustomersAffectedByOutage.  MR returns status of failed tranactions
     * in an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] customersAffectedByOutageNotification(com.cannontech.multispeak.deploy.service.CustomersAffectedByOutage[] newOutages) throws java.rmi.RemoteException;

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
     * EDR Notifies MR of a received end device (meter) shipment by
     * sending the changed endDeviceShipment object.  MR returns information
     * about failed transactions in an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] endDeviceShipmentNotification(com.cannontech.multispeak.deploy.service.EndDeviceShipment shipment) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to add the associated in-home display(s).MR
     * returns information about failed transactions using an array of errorObjects.
     * The message header attribute 'registrationID' should be added to all
     * publish messages to indicate to the subscriber under which registrationID
     * they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayAddNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] addedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR of a change in the in-home display(s)by
     * sending the changed inHomeDisplay object.  MR returns information
     * about failed transactions using an array of errorObjects. The message
     * header attribute 'registrationID' should be added to all publish messages
     * to indicate to the subscriber under which registrationID they received
     * this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayChangedNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] changedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that in-home displays(s) have been deployed
     * or exchanged.  MR returns information about failed transactions in
     * an array of errorObjects. The message header attribute 'registrationID'
     * should be added to all publish messages to indicate to the subscriber
     * under which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayExchangeNotification(com.cannontech.multispeak.deploy.service.InHomeDisplayExchange[] IHDChangeout) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR to remove the associated in-home displays(s).
     * MR returns information about failed transactions using an array of
     * errorObjects. The message header attribute 'registrationID' should
     * be added to all publish messages to indicate to the subscriber under
     * which registrationID they received this notification data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRemoveNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] removedIHDs) throws java.rmi.RemoteException;

    /**
     * Publisher notifies MR that the associated in-home display(s)have
     * been retired from the system.  MR returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate
     * to the subscriber under which registrationID they received this notification
     * data.
     */
    public com.cannontech.multispeak.deploy.service.ErrorObject[] inHomeDisplayRetireNotification(com.cannontech.multispeak.deploy.service.InHomeDisplay[] retiredIHDs) throws java.rmi.RemoteException;

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
}
