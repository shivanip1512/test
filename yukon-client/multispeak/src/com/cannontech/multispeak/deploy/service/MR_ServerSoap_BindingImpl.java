/**
 * MR_ServerSoap_BindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Dec 28, 2007 (09:53:13 CST) WSDL2Java emitter.
 */

package com.cannontech.multispeak.deploy.service;

import java.rmi.RemoteException;
import java.util.Calendar;

import com.cannontech.spring.YukonSpringHook;

public class MR_ServerSoap_BindingImpl implements com.cannontech.multispeak.deploy.service.MR_ServerSoap_PortType{

    private MR_ServerSoap_PortType mr_server = YukonSpringHook.getBean("mr_server", MR_ServerSoap_PortType.class);

    public ErrorObject[] meterBaseExchangeNotification(
            MeterBaseExchange[] MBChangeout) throws RemoteException {
        return mr_server.meterBaseExchangeNotification(MBChangeout);
    }

    public ErrorObject[] pingURL() throws RemoteException {
        return mr_server.pingURL();
    }

    public String[] getMethods() throws RemoteException {
        return mr_server.getMethods();
    }

    public String[] getDomainNames() throws RemoteException {
        return mr_server.getDomainNames();
    }

    public DomainMember[] getDomainMembers(String domainName)
            throws RemoteException {
        return mr_server.getDomainMembers(domainName);
    }

    public String requestRegistrationID() throws RemoteException {
        return mr_server.requestRegistrationID();
    }

    public ErrorObject[] registerForService(RegistrationInfo registrationDetails)
            throws RemoteException {
        return mr_server.registerForService(registrationDetails);
    }

    public ErrorObject[] unregisterForService(String registrationID)
            throws RemoteException {
        return mr_server.unregisterForService(registrationID);
    }

    public RegistrationInfo getRegistrationInfoByID(String registrationID)
            throws RemoteException {
        return mr_server.getRegistrationInfoByID(registrationID);
    }

    public String[] getPublishMethods() throws RemoteException {
        return mr_server.getPublishMethods();
    }

    public ErrorObject[] domainMembersChangedNotification(
            DomainMember[] changedDomainMembers) throws RemoteException {
        return mr_server.domainMembersChangedNotification(changedDomainMembers);
    }

    public ErrorObject[] domainNamesChangedNotification(
            DomainNameChange[] changedDomainNames) throws RemoteException {
        return mr_server.domainNamesChangedNotification(changedDomainNames);
    }

    public Meter[] getAMRSupportedMeters(String lastReceived)
            throws RemoteException {
        return mr_server.getAMRSupportedMeters(lastReceived);
    }

    public Meter[] getModifiedAMRMeters(String previousSessionID,
            String lastReceived) throws RemoteException {
        return mr_server.getModifiedAMRMeters(previousSessionID, lastReceived);
    }

    public boolean isAMRMeter(String meterNo) throws RemoteException {
        return mr_server.isAMRMeter(meterNo);
    }

    public MeterRead[] getReadingsByDate(Calendar startDate, Calendar endDate,
            String lastReceived) throws RemoteException {
        return mr_server.getReadingsByDate(startDate, endDate, lastReceived);
    }

    public MeterRead[] getReadingsByMeterNo(String meterNo, Calendar startDate,
            Calendar endDate) throws RemoteException {
        return mr_server.getReadingsByMeterNo(meterNo, startDate, endDate);
    }

    public MeterRead getLatestReadingByMeterNo(String meterNo)
            throws RemoteException {
        return mr_server.getLatestReadingByMeterNo(meterNo);
    }

    public FormattedBlock[] getReadingsByBillingCycle(String billingCycle,
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        return mr_server.getReadingsByBillingCycle(billingCycle,
                                                   billingDate,
                                                   kWhLookBack,
                                                   kWLookBack,
                                                   kWLookForward,
                                                   lastReceived,
                                                   formattedBlockTemplateName,
                                                   fieldName);
    }

    public FormattedBlock[] getReadingByMeterNumberFormattedBlock(
            String meterNumber, Calendar billingDate, int kWhLookBack,
            int kWLookBack, int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        return mr_server.getReadingByMeterNumberFormattedBlock(meterNumber,
                                                               billingDate,
                                                               kWhLookBack,
                                                               kWLookBack,
                                                               kWLookForward,
                                                               lastReceived,
                                                               formattedBlockTemplateName,
                                                               fieldName);
    }

    public FormattedBlock[] getReadingsByDateFormattedBlock(
            Calendar billingDate, int kWhLookBack, int kWLookBack,
            int kWLookForward, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        return mr_server.getReadingsByDateFormattedBlock(billingDate,
                                                         kWhLookBack,
                                                         kWLookBack,
                                                         kWLookForward,
                                                         lastReceived,
                                                         formattedBlockTemplateName,
                                                         fieldName);
    }

    public HistoryLog[] getHistoryLogByMeterNo(String meterNo,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_server.getHistoryLogByMeterNo(meterNo, startDate, endDate);
    }

    public HistoryLog[] getHistoryLogsByDate(Calendar startDate,
            Calendar endDate, String lastReceived) throws RemoteException {
        return mr_server.getHistoryLogsByDate(startDate, endDate, lastReceived);
    }

    public HistoryLog[] getHistoryLogsByMeterNoAndEventCode(String meterNo,
            EventCode eventCode, Calendar startDate, Calendar endDate)
            throws RemoteException {
        return mr_server.getHistoryLogsByMeterNoAndEventCode(meterNo,
                                                             eventCode,
                                                             startDate,
                                                             endDate);
    }

    public HistoryLog[] getHistoryLogsByDateAndEventCode(EventCode eventCode,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getHistoryLogsByDateAndEventCode(eventCode,
                                                          startDate,
                                                          endDate,
                                                          lastReceived);
    }

    public FormattedBlock getLatestMeterReadingsByMeterGroup(
            String meterGroupID, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        return mr_server.getLatestMeterReadingsByMeterGroup(meterGroupID,
                                                            formattedBlockTemplateName,
                                                            fieldName);
    }

    public FormattedBlock getLatestReadingByMeterNoAndType(String meterNo,
            String readingType, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        return mr_server.getLatestReadingByMeterNoAndType(meterNo,
                                                          readingType,
                                                          formattedBlockTemplateName,
                                                          fieldName);
    }

    public FormattedBlock[] getLatestReadingByType(String readingType,
            String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        return mr_server.getLatestReadingByType(readingType,
                                                lastReceived,
                                                formattedBlockTemplateName,
                                                fieldName);
    }

    public FormattedBlock[] getReadingsByDateAndType(Calendar startDate,
            Calendar endDate, String readingType, String lastReceived,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        return mr_server.getReadingsByDateAndType(startDate,
                                                  endDate,
                                                  readingType,
                                                  lastReceived,
                                                  formattedBlockTemplateName,
                                                  fieldName);
    }

    public String[] getSupportedReadingTypes() throws RemoteException {
        return mr_server.getSupportedReadingTypes();
    }

    public FormattedBlock[] getReadingsByMeterNoAndType(String meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, String formattedBlockTemplateName,
            String[] fieldName) throws RemoteException {
        return mr_server.getReadingsByMeterNoAndType(meterNo,
                                                     startDate,
                                                     endDate,
                                                     readingType,
                                                     lastReceived,
                                                     formattedBlockTemplateName,
                                                     fieldName);
    }

    public MeterRead[] getLatestReadings(String lastReceived)
            throws RemoteException {
        return mr_server.getLatestReadings(lastReceived);
    }

    public MeterRead[] getReadingsByUOMAndDate(String uomData,
            Calendar startDate, Calendar endDate, String lastReceived)
            throws RemoteException {
        return mr_server.getReadingsByUOMAndDate(uomData,
                                                 startDate,
                                                 endDate,
                                                 lastReceived);
    }

    public Schedule[] getSchedules(String lastReceived) throws RemoteException {
        return mr_server.getSchedules(lastReceived);
    }

    public Schedule getScheduleByID(String scheduleID) throws RemoteException {
        return mr_server.getScheduleByID(scheduleID);
    }

    public ReadingSchedule[] getReadingSchedules(String lastReceived)
            throws RemoteException {
        return mr_server.getReadingSchedules(lastReceived);
    }

    public ReadingSchedule getReadingScheduleByID(String readingScheduleID)
            throws RemoteException {
        return mr_server.getReadingScheduleByID(readingScheduleID);
    }

    public FormattedBlock[] getLatestReadingsByMeterNoList(String[] meterNo,
            Calendar startDate, Calendar endDate, String readingType,
            String lastReceived, ServiceType serviceType,
            String formattedBlockTemplateName, String[] fieldName)
            throws RemoteException {
        return mr_server.getLatestReadingsByMeterNoList(meterNo,
                                                        startDate,
                                                        endDate,
                                                        readingType,
                                                        lastReceived,
                                                        serviceType,
                                                        formattedBlockTemplateName,
                                                        fieldName);
    }

    public FormattedBlockTemplate[] getFormattedBlockTemplates(
            String lastReceived) throws RemoteException {
        return mr_server.getFormattedBlockTemplates(lastReceived);
    }

    public FormattedBlock[] getLatestReadingsByMeterNoListFormattedBlock(
            String[] meterNo, Calendar startDate, Calendar endDate,
            String formattedBlockTemplateName, String[] fieldName,
            String lastReceived, ServiceType serviceType)
            throws RemoteException {
        return mr_server.getLatestReadingsByMeterNoListFormattedBlock(meterNo,
                                                                      startDate,
                                                                      endDate,
                                                                      formattedBlockTemplateName,
                                                                      fieldName,
                                                                      lastReceived,
                                                                      serviceType);
    }

    public ErrorObject[] initiatePlannedOutage(String[] meterNos,
            Calendar startDate, Calendar endDate) throws RemoteException {
        return mr_server.initiatePlannedOutage(meterNos, startDate, endDate);
    }

    public ErrorObject[] cancelPlannedOutage(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelPlannedOutage(meterNos);
    }

    public ErrorObject[] initiateUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_server.initiateUsageMonitoring(meterNos);
    }

    public ErrorObject[] cancelUsageMonitoring(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelUsageMonitoring(meterNos);
    }

    public ErrorObject[] initiateDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_server.initiateDisconnectedStatus(meterNos);
    }

    public ErrorObject[] cancelDisconnectedStatus(String[] meterNos)
            throws RemoteException {
        return mr_server.cancelDisconnectedStatus(meterNos);
    }

    public ErrorObject[] initiateMeterReadByMeterNumber(String[] meterNos,
            String responseURL, String transactionID, java.lang.Float expirationTime)
            throws RemoteException {
        return mr_server.initiateMeterReadByMeterNumber(meterNos,
                                                        responseURL,
                                                        transactionID,
                                                        expirationTime);
    }

    public ErrorObject[] establishMeterGroup(MeterGroup meterGroup)
            throws RemoteException {
        return mr_server.establishMeterGroup(meterGroup);
    }

    public ErrorObject deleteMeterGroup(String meterGroupID)
            throws RemoteException {
        return mr_server.deleteMeterGroup(meterGroupID);
    }

    public ErrorObject[] insertMeterInMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_server.insertMeterInMeterGroup(meterNumbers, meterGroupID);
    }

    public ErrorObject[] removeMetersFromMeterGroup(String[] meterNumbers,
            String meterGroupID) throws RemoteException {
        return mr_server.removeMetersFromMeterGroup(meterNumbers, meterGroupID);
    }

    public ErrorObject[] initiateGroupMeterRead(String meterGroupName,
            String responseURL, String transactionID, float expirationTime)
            throws RemoteException {
        return mr_server.initiateGroupMeterRead(meterGroupName,
                                                responseURL,
                                                transactionID,
                                                expirationTime);
    }

    public ErrorObject[] scheduleGroupMeterRead(String meterGroupName,
            Calendar timeToRead, String responseURL, String transactionID)
            throws RemoteException {
        return mr_server.scheduleGroupMeterRead(meterGroupName,
                                                timeToRead,
                                                responseURL,
                                                transactionID);
    }

    public ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNo,
            String responseURL, String readingType, String transactionID,
            java.lang.Float expirationTime) throws RemoteException {
        return mr_server.initiateMeterReadByMeterNoAndType(meterNo,
                                                           responseURL,
                                                           readingType,
                                                           transactionID,
                                                           expirationTime);
    }

    public ErrorObject[] initiateMeterReadByObject(String objectName,
            String nounType, PhaseCd phaseCode, String responseURL,
            String transactionID, float expirationTime) throws RemoteException {
        return mr_server.initiateMeterReadByObject(objectName,
                                                   nounType,
                                                   phaseCode,
                                                   responseURL,
                                                   transactionID,
                                                   expirationTime);
    }

    public ErrorObject[] updateServiceLocationDisplays(String servLocID)
            throws RemoteException {
        return mr_server.updateServiceLocationDisplays(servLocID);
    }

    public ErrorObject[] initiateDemandReset(MeterIdentifier[] meterIDs,
            String responseURL, String transactionID, java.lang.Float expirationTime)
            throws RemoteException {
        return mr_server.initiateDemandReset(meterIDs,
                                             responseURL,
                                             transactionID,
                                             expirationTime);
    }

    public ErrorObject[] insertMetersInConfigurationGroup(
            String[] meterNumbers, String meterGroupID, ServiceType serviceType)
            throws RemoteException {
        return mr_server.insertMetersInConfigurationGroup(meterNumbers,
                                                          meterGroupID,
                                                          serviceType);
    }

    public ErrorObject[] removeMetersFromConfigurationGroup(
            String[] meterNumbers, String meterGroupID, ServiceType serviceType)
            throws RemoteException {
        return mr_server.removeMetersFromConfigurationGroup(meterNumbers,
                                                            meterGroupID,
                                                            serviceType);
    }

    public ErrorObject[] establishSchedules(Schedule[] schedules)
            throws RemoteException {
        return mr_server.establishSchedules(schedules);
    }

    public ErrorObject[] deleteSchedule(String scheduleID)
            throws RemoteException {
        return mr_server.deleteSchedule(scheduleID);
    }

    public ErrorObject[] establishReadingSchedules(
            ReadingSchedule[] readingSchedules) throws RemoteException {
        return mr_server.establishReadingSchedules(readingSchedules);
    }

    public ErrorObject[] enableReadingSchedule(String readingScheduleID)
            throws RemoteException {
        return mr_server.enableReadingSchedule(readingScheduleID);
    }

    public ErrorObject[] disableReadingSchedule(String readingScheduleID)
            throws RemoteException {
        return mr_server.disableReadingSchedule(readingScheduleID);
    }

    public ErrorObject[] deleteReadingSchedule(String readingScheduleID)
            throws RemoteException {
        return mr_server.deleteReadingSchedule(readingScheduleID);
    }

    public ErrorObject[] initiateMeterReadsByFieldName(String[] meterNumbers,
            String[] fieldNames, String responseURL, String transactionID,
            float expirationTime, String formattedBlockTemplateName)
            throws RemoteException {
        return mr_server.initiateMeterReadsByFieldName(meterNumbers,
                                                       fieldNames,
                                                       responseURL,
                                                       transactionID,
                                                       expirationTime,
                                                       formattedBlockTemplateName);
    }

    public ErrorObject[] customerChangedNotification(Customer[] changedCustomers)
            throws RemoteException {
        return mr_server.customerChangedNotification(changedCustomers);
    }

    public ErrorObject[] serviceLocationChangedNotification(
            ServiceLocation[] changedServiceLocations) throws RemoteException {
        return mr_server.serviceLocationChangedNotification(changedServiceLocations);
    }

    public ErrorObject[] meterChangedNotification(Meter[] changedMeters)
            throws RemoteException {
        return mr_server.meterChangedNotification(changedMeters);
    }

    public ErrorObject[] meterRemoveNotification(Meter[] removedMeters)
            throws RemoteException {
        return mr_server.meterRemoveNotification(removedMeters);
    }

    public ErrorObject[] meterRetireNotification(Meter[] retiredMeters)
            throws RemoteException {
        return mr_server.meterRetireNotification(retiredMeters);
    }

    public ErrorObject[] meterAddNotification(Meter[] addedMeters)
            throws RemoteException {
        return mr_server.meterAddNotification(addedMeters);
    }

    public ErrorObject[] meterExchangeNotification(
            MeterExchange[] meterChangeout) throws RemoteException {
        return mr_server.meterExchangeNotification(meterChangeout);
    }

    public ErrorObject[] customersAffectedByOutageNotification(
            CustomersAffectedByOutage[] newOutages) throws RemoteException {
        return mr_server.customersAffectedByOutageNotification(newOutages);
    }

    public ErrorObject[] meterConnectivityNotification(
            MeterConnectivity[] newConnectivity) throws RemoteException {
        return mr_server.meterConnectivityNotification(newConnectivity);
    }

    public ErrorObject[] endDeviceShipmentNotification(
            EndDeviceShipment shipment) throws RemoteException {
        return mr_server.endDeviceShipmentNotification(shipment);
    }

    public ErrorObject[] inHomeDisplayAddNotification(InHomeDisplay[] addedIHDs)
            throws RemoteException {
        return mr_server.inHomeDisplayAddNotification(addedIHDs);
    }

    public ErrorObject[] inHomeDisplayChangedNotification(
            InHomeDisplay[] changedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayChangedNotification(changedIHDs);
    }

    public ErrorObject[] inHomeDisplayExchangeNotification(
            InHomeDisplayExchange[] IHDChangeout) throws RemoteException {
        return mr_server.inHomeDisplayExchangeNotification(IHDChangeout);
    }

    public ErrorObject[] inHomeDisplayRemoveNotification(
            InHomeDisplay[] removedIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRemoveNotification(removedIHDs);
    }

    public ErrorObject[] inHomeDisplayRetireNotification(
            InHomeDisplay[] retiredIHDs) throws RemoteException {
        return mr_server.inHomeDisplayRetireNotification(retiredIHDs);
    }

    public ErrorObject[] meterBaseChangedNotification(MeterBase[] changedMBs)
            throws RemoteException {
        return mr_server.meterBaseChangedNotification(changedMBs);
    }

    public ErrorObject[] meterBaseRemoveNotification(MeterBase[] removedMBs)
            throws RemoteException {
        return mr_server.meterBaseRemoveNotification(removedMBs);
    }

    public ErrorObject[] meterBaseRetireNotification(MeterBase[] retiredMBs)
            throws RemoteException {
        return mr_server.meterBaseRetireNotification(retiredMBs);
    }

    public ErrorObject[] meterBaseAddNotification(MeterBase[] addedMBs)
            throws RemoteException {
        return mr_server.meterBaseAddNotification(addedMBs);
    }

    
}
