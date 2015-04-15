package com.cannontech.multispeak.service;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.CustomersAffectedByOutage;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.DomainNameChange;
import com.cannontech.msp.beans.v3.EndDeviceShipment;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.EventCode;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.FormattedBlockTemplate;
import com.cannontech.msp.beans.v3.HistoryLog;
import com.cannontech.msp.beans.v3.InHomeDisplay;
import com.cannontech.msp.beans.v3.InHomeDisplayExchange;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterBase;
import com.cannontech.msp.beans.v3.MeterBaseExchange;
import com.cannontech.msp.beans.v3.MeterConnectivity;
import com.cannontech.msp.beans.v3.MeterExchange;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterIdentifier;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.PhaseCd;
import com.cannontech.msp.beans.v3.ReadingSchedule;
import com.cannontech.msp.beans.v3.RegistrationInfo;
import com.cannontech.msp.beans.v3.Schedule;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceType;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;

public interface MR_Server {

    /**
     * ping URL.
     * 
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public void pingURL() throws MultispeakWebServiceException;

    /**
     * get Methods.
     * 
     * @return the methods
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<String> getMethods() throws MultispeakWebServiceException;;

    /**
     * get AMR SupportedMeters.
     * 
     * @param lastReceived the last received
     * @return the AMR supported meters
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<Meter> getAMRSupportedMeters(java.lang.String lastReceived) throws MultispeakWebServiceException;

    /**
     * is AMR Meter.
     * 
     * @param meterNo the meter no
     * @return true, if is AMR meter
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public boolean isAMRMeter(java.lang.String meterNo) throws MultispeakWebServiceException;

    /**
     * get Readings By Date.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param lastReceived the last received
     * @return the readings by date
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getReadingsByDate(java.util.Calendar startDate, java.util.Calendar endDate,
            java.lang.String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Readings By Meter No.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @return the readings by meter no
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getReadingsByMeterNo(java.lang.String meterNo, java.util.Calendar startDate,
            java.util.Calendar endDate) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Meter No.
     * 
     * @param meterNo the meter no
     * @return the latest reading by meter no
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public MeterRead getLatestReadingByMeterNo(java.lang.String meterNo) throws MultispeakWebServiceException;

    /**
     * get Readings By Billing Cycle.
     * 
     * @param billingCycle the billing cycle
     * @param billingDate the billing date
     * @param kWhLookBack the k wh look back
     * @param kWLookBack the k w look back
     * @param kWLookForward the k w look forward
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the readings by billing cycle
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByBillingCycle(String billingCycle, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * initiate Usage Monitoring.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * cancel Usage Monitoring.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelUsageMonitoring(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * initiate Disconnected Status.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * cancel Disconnected Status.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelDisconnectedStatus(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * initiate Meter Read By Meter Number.
     * 
     * @param meterNos the meter nos
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadByMeterNumber(List<String> meterNos, String responseURL, String transactionID,
            Float expirationTime) throws MultispeakWebServiceException;

    /**
     * service Location Changed Notification.
     * 
     * @param changedServiceLocations the changed service locations
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> serviceLocationChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException;

    /**
     * meter Changed Notification.
     * 
     * @param changedMeters the changed meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterChangedNotification(List<Meter> changedMeters) throws MultispeakWebServiceException;

    /**
     * meter Remove Notification.
     * 
     * @param removedMeters the removed meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterRemoveNotification(List<Meter> removedMeters) throws MultispeakWebServiceException;

    /**
     * meter Add Notification.
     * 
     * @param addedMeters the added meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterAddNotification(List<Meter> addedMeters) throws MultispeakWebServiceException;

    /**
     * delete Meter Group.
     * 
     * @param meterGroupID the meter group id
     * @return the error object
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ErrorObject deleteMeterGroup(String meterGroupID) throws MultispeakWebServiceException;

    /**
     * establish Meter Group.
     * 
     * @param meterGroup the meter group
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> establishMeterGroup(MeterGroup meterGroup) throws MultispeakWebServiceException;

    /**
     * insert Meter In Meter Group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> insertMeterInMeterGroup(List<String> meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * remove Meters From Meter Group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> removeMetersFromMeterGroup(List<String> meterNumbers, String meterGroupID)
            throws MultispeakWebServiceException;

    /**
     * get Latest Readings.
     * 
     * @param lastReceived the last received
     * @return the latest readings
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getLatestReadings(String lastReceived) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param readingType the reading type
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by meter no and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo, String readingType,
            String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * get Latest Reading By Type.
     * 
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the latest reading by type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getLatestReadingByType(String readingType, String lastReceived,
            String formattedBlockTemplateName) throws MultispeakWebServiceException;

    /**
     * get Readings By Date And Type.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @return the readings by date and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByDateAndType(Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Readings By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the readings by meter no and type
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByMeterNoAndType(String meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * get Supported Reading Types.
     * 
     * @return the supported reading types
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public Set<String> getSupportedReadingTypes() throws MultispeakWebServiceException;

    /**
     * initiate Meter Read By Meter No And Type.
     * 
     * @param meterNo the meter no
     * @param responseURL the response url
     * @param readingType the reading type
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, String readingType,
            String transactionID, Float expirationTime) throws MultispeakWebServiceException;

    /**
     * initiate Demand Reset.
     * 
     * @param meterIDs the meter i ds
     * @param responseURL the response url
     * @param transactionId the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateDemandReset(List<MeterIdentifier> meterIDs, String responseURL, String transactionId,
            Float expirationTime) throws MultispeakWebServiceException;

    /**
     * cancel planned outage.
     * 
     * @param meterNos the meter nos
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> cancelPlannedOutage(List<String> meterNos) throws MultispeakWebServiceException;

    /**
     * customer change notification.
     * 
     * @param changedCustomers the changed customers
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> customerChangedNotification(List<Customer> changedCustomers) throws MultispeakWebServiceException;

    /**
     * customers affected by Outage notification.
     * 
     * @param newOutages the new outages
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> customersAffectedByOutageNotification(List<CustomersAffectedByOutage> newOutages)
            throws MultispeakWebServiceException;

    /**
     * delete Reading Schedule.
     * 
     * @param readingScheduleID the reading schedule id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> deleteReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException;

    /**
     * Delete schedule.
     * 
     * @param scheduleID the schedule id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> deleteSchedule(String scheduleID) throws MultispeakWebServiceException;

    /**
     * Disable reading schedule.
     * 
     * @param readingScheduleID the reading schedule id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> disableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException;

    /**
     * Domain members changed notification.
     * 
     * @param changedDomainMembers the changed domain members
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> domainMembersChangedNotification(List<DomainMember> changedDomainMembers)
            throws MultispeakWebServiceException;

    /**
     * Domain names changed notification.
     * 
     * @param changedDomainNames the changed domain names
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> domainNamesChangedNotification(List<DomainNameChange> changedDomainNames)
            throws MultispeakWebServiceException;

    /**
     * Enable reading schedule.
     * 
     * @param readingScheduleID the reading schedule id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> enableReadingSchedule(String readingScheduleID) throws MultispeakWebServiceException;

    /**
     * End device shipment notification.
     * 
     * @param shipment the shipment
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> endDeviceShipmentNotification(EndDeviceShipment shipment) throws MultispeakWebServiceException;

    /**
     * Establish reading schedules.
     * 
     * @param readingSchedules the reading schedules
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> establishReadingSchedules(List<ReadingSchedule> readingSchedules)
            throws MultispeakWebServiceException;

    /**
     * Establish schedules.
     * 
     * @param schedules the schedules
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> establishSchedules(List<Schedule> schedules)
            throws MultispeakWebServiceException;

    /**
     * Gets the domain names.
     * 
     * @return the domain names
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<String> getDomainNames() throws MultispeakWebServiceException;

    /**
     * Gets the domain members.
     * 
     * @param domainNaString the domain na string
     * @return the domain members
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<DomainMember> getDomainMembers(String domainNaString) throws MultispeakWebServiceException;

    /**
     * Gets the formatted block templates.
     * 
     * @param lastReceived the last received
     * @return the formatted block templates
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlockTemplate> getFormattedBlockTemplates(String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * Gets the history log by meter no.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @return the history log by meter no
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<HistoryLog> getHistoryLogByMeterNo(String meterNo, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException;

    /**
     * Gets the history logs by date.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param lastReceived the last received
     * @return the history logs by date
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<HistoryLog> getHistoryLogsByDate(Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * Gets the history logs by meter no and event code.
     * 
     * @param meterNo the meter no
     * @param eventCode the event code
     * @param startDate the start date
     * @param endDate the end date
     * @return the history logs by meter no and event code
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<HistoryLog> getHistoryLogsByMeterNoAndEventCode(String meterNo, EventCode eventCode, Calendar startDate,
            Calendar endDate) throws MultispeakWebServiceException;

    /**
     * Gets the history logs by date and event code.
     * 
     * @param eventCode the event code
     * @param startDate the start date
     * @param endDate the end date
     * @param lastReceived the last received
     * @return the history logs by date and event code
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<HistoryLog> getHistoryLogsByDateAndEventCode(EventCode eventCode, Calendar startDate, Calendar endDate,
            String lastReceived) throws MultispeakWebServiceException;

    /**
     * Gets the latest meter readings by meter group.
     * 
     * @param meterGroupID the meter group id
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the latest meter readings by meter group
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public FormattedBlock getLatestMeterReadingsByMeterGroup(String meterGroupID, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException;

    /**
     * Gets the latest readings by meter no list.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @param readingType the reading type
     * @param lastReceived the last received
     * @param serviceType the service type
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the latest readings by meter no list
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getLatestReadingsByMeterNoList(List<String> meterNo, Calendar startDate, Calendar endDate,
            String readingType, String lastReceived, ServiceType serviceType, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException;

    /**
     * Gets the latest readings by meter no list formatted block.
     * 
     * @param meterNo the meter no
     * @param startDate the start date
     * @param endDate the end date
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @param lastReceived the last received
     * @param serviceType the service type
     * @return the latest readings by meter no list formatted block
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getLatestReadingsByMeterNoListFormattedBlock(List<String> meterNo, Calendar startDate,
            Calendar endDate, String formattedBlockTemplateName, List<String> fieldName, String lastReceived,
            ServiceType serviceType) throws MultispeakWebServiceException;

    /**
     * Gets the modified amr meters.
     * 
     * @param previousSessionID the previous session id
     * @param lastReceived the last received
     * @return the modified amr meters
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<Meter> getModifiedAMRMeters(String previousSessionID, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * Gets the publish methods.
     * 
     * @return the publish methods
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<String> getPublishMethods() throws MultispeakWebServiceException;

    /**
     * Gets the reading by meter number formatted block.
     * 
     * @param meterNumber the meter number
     * @param billingDate the billing date
     * @param kWhLookBack the k wh look back
     * @param kWLookBack the k w look back
     * @param kWLookForward the k w look forward
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the reading by meter number formatted block
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingByMeterNumberFormattedBlock(String meterNumber, Calendar billingDate,
            int kWhLookBack, int kWLookBack, int kWLookForward, String lastReceived, String formattedBlockTemplateName,
            List<String> fieldName) throws MultispeakWebServiceException;

    /**
     * Gets the readings by date formatted block.
     * 
     * @param billingDate the billing date
     * @param kWhLookBack the k wh look back
     * @param kWLookBack the k w look back
     * @param kWLookForward the k w look forward
     * @param lastReceived the last received
     * @param formattedBlockTemplateName the formatted block template name
     * @param fieldName the field name
     * @return the readings by date formatted block
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<FormattedBlock> getReadingsByDateFormattedBlock(Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived, String formattedBlockTemplateName, List<String> fieldName)
            throws MultispeakWebServiceException;

    /**
     * Gets the readings by uom and date.
     * 
     * @param uomData the uom data
     * @param startDate the start date
     * @param endDate the end date
     * @param lastReceived the last received
     * @return the readings by uom and date
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<MeterRead> getReadingsByUOMAndDate(String uomData, Calendar startDate, Calendar endDate, String lastReceived)
            throws MultispeakWebServiceException;

    /**
     * Gets the reading schedule by id.
     * 
     * @param readingScheduleID the reading schedule id
     * @return the reading schedule by id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public ReadingSchedule getReadingScheduleByID(String readingScheduleID) throws MultispeakWebServiceException;

    /**
     * Gets the reading schedules.
     * 
     * @param lastReceived the last received
     * @return the reading schedules
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ReadingSchedule> getReadingSchedules(String lastReceived) throws MultispeakWebServiceException;

    /**
     * Gets the registration info by id.
     * 
     * @param registrationID the registration id
     * @return the registration info by id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public RegistrationInfo getRegistrationInfoByID(String registrationID) throws MultispeakWebServiceException;

    /**
     * Gets the schedule by id.
     * 
     * @param scheduleID the schedule id
     * @return the schedule by id
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public Schedule getScheduleByID(String scheduleID) throws MultispeakWebServiceException;

    /**
     * Gets the schedules.
     * 
     * @param lastReceived the last received
     * @return the schedules
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<Schedule> getSchedules(String lastReceived) throws MultispeakWebServiceException;

    /**
     * In home display add notification.
     * 
     * @param addedIHDs the added ih ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> inHomeDisplayAddNotification(List<InHomeDisplay> addedIHDs) throws MultispeakWebServiceException;

    /**
     * In home display changed notification.
     * 
     * @param changedIHDs the changed ih ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> inHomeDisplayChangedNotification(List<InHomeDisplay> changedIHDs)
            throws MultispeakWebServiceException;

    /**
     * In home display exchange notification.
     * 
     * @param IHDChangeout the IHD changeout
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> inHomeDisplayExchangeNotification(List<InHomeDisplayExchange> IHDChangeout)
            throws MultispeakWebServiceException;

    /**
     * In home display remove notification.
     * 
     * @param removedIHDs the removed ih ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> inHomeDisplayRemoveNotification(List<InHomeDisplay> removedIHDs)
            throws MultispeakWebServiceException;

    /**
     * In home display retire notification.
     * 
     * @param retiredIHDs the retired ih ds
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> inHomeDisplayRetireNotification(List<InHomeDisplay> retiredIHDs)
            throws MultispeakWebServiceException;

    /**
     * Initiate group meter read.
     * 
     * @param meterGroupName the meter group name
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateGroupMeterRead(String meterGroupName, String responseURL, String transactionID,
            float expirationTime) throws MultispeakWebServiceException;

    /**
     * Initiate meter read by object.
     * 
     * @param objectName the object name
     * @param nounType the noun type
     * @param phaseCode the phase code
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadByObject(String objectName, String nounType, PhaseCd phaseCode,
            String responseURL, String transactionID, float expirationTime) throws MultispeakWebServiceException;

    /**
     * Initiate meter reads by field name.
     * 
     * @param meterNumbers the meter numbers
     * @param fieldNames the field names
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @param expirationTime the expiration time
     * @param formattedBlockTemplateName the formatted block template name
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiateMeterReadsByFieldName(List<String> meterNumbers, List<String> fieldNames, String responseURL,
            String transactionID, float expirationTime, String formattedBlockTemplateName)
            throws MultispeakWebServiceException;

    /**
     * Initiate planned outage.
     * 
     * @param meterNos the meter nos
     * @param startDate the start date
     * @param endDate the end date
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> initiatePlannedOutage(List<String> meterNos, Calendar startDate, Calendar endDate)
            throws MultispeakWebServiceException;

    /**
     * Insert meters in configuration group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @param serviceType the service type
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> insertMetersInConfigurationGroup(List<String> meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException;

    /**
     * Meter base add notification.
     * 
     * @param addedMBs the added m bs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterBaseAddNotification(List<MeterBase> addedMBs) throws MultispeakWebServiceException;

    /**
     * Meter base changed notification.
     * 
     * @param changedMBs the changed m bs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterBaseChangedNotification(List<MeterBase> changedMBs) throws MultispeakWebServiceException;

    /**
     * Meter base exchange notification.
     * 
     * @param MBChangeout the MB changeout
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterBaseExchangeNotification(List<MeterBaseExchange> MBChangeout)
            throws MultispeakWebServiceException;

    /**
     * Meter base remove notification.
     * 
     * @param removedMBs the removed m bs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterBaseRemoveNotification(List<MeterBase> removedMBs) throws MultispeakWebServiceException;

    /**
     * Meter base retire notification.
     * 
     * @param retiredMBs the retired m bs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterBaseRetireNotification(List<MeterBase> retiredMBs) throws MultispeakWebServiceException;

    /**
     * Meter connectivity notification.
     * 
     * @param newConnectivity the new connectivity
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterConnectivityNotification(List<MeterConnectivity> newConnectivity)
            throws MultispeakWebServiceException;

    /**
     * Meter exchange notification.
     * 
     * @param meterChangeout the meter changeout
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterExchangeNotification(List<MeterExchange> meterChangeout) throws MultispeakWebServiceException;

    /**
     * Meter retire notification.
     * 
     * @param retiredMeters the retired meters
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> meterRetireNotification(List<Meter> retiredMeters) throws MultispeakWebServiceException;

    /**
     * Register for service.
     * 
     * @param registrationDetails the registration details
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> registerForService(RegistrationInfo registrationDetails) throws MultispeakWebServiceException;

    /**
     * Removes the meters from configuration group.
     * 
     * @param meterNumbers the meter numbers
     * @param meterGroupID the meter group id
     * @param serviceType the service type
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> removeMetersFromConfigurationGroup(List<String> meterNumbers, String meterGroupID,
            ServiceType serviceType) throws MultispeakWebServiceException;

    /**
     * Request registration id.
     * 
     * @return the string
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public String requestRegistrationID() throws MultispeakWebServiceException;

    /**
     * Schedule group meter read.
     * 
     * @param meterGroupName the meter group name
     * @param timeToRead the time to read
     * @param responseURL the response url
     * @param transactionID the transaction id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> scheduleGroupMeterRead(String meterGroupName, Calendar timeToRead, String responseURL,
            String transactionID) throws MultispeakWebServiceException;

    /**
     * Unregister for service.
     * 
     * @param registrationID the registration id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> unregisterForService(String registrationID) throws MultispeakWebServiceException;

    /**
     * Update service location displays.
     * 
     * @param servLocID the serv loc id
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> updateServiceLocationDisplays(String servLocID) throws MultispeakWebServiceException;

}
