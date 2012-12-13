package com.cannontech.yukon.api.loadManagement.mocks;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MockAccountEventLogService implements AccountEventLogService {

    @Override
    public void accountCreationAttempted(LiteYukonUser yukonUser,
                                           String accountNumber,
                                           EventSource source) {
    }

    @Override
    public void accountUpdateCreationAttempted(LiteYukonUser yukonUser,
                                               String accountNumber,
                                               EventSource source) {
    }

    @Override
    public void accountDeletionAttempted(LiteYukonUser yukonUser, String accountNumber, EventSource source) {
    }

    @Override
    public void accountUpdateAttempted(LiteYukonUser yukonUser, String accountNumber, EventSource source) {
    }

    @Override
    public void accountAdded(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountUpdated(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountDeleted(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountNumberChanged(LiteYukonUser yukonUser, String oldAccountNumber,
                                     String newAccountNumber) {
    }

    @Override
    public void contactAdded(LiteYukonUser yukonUser, String accountNumber, String contactName) {
    }

    @Override
    public void contactUpdated(LiteYukonUser yukonUser, String accountNumber, String contactName) {
    }

    @Override
    public void contactRemoved(LiteYukonUser yukonUser, String accountNumber, String contactName) {
    }

    @Override
    public void contactNameChanged(LiteYukonUser yukonUser, String accountNumber,
                                   String oldContactName, String newContactName) {
    }

    @Override
    public void customerTypeChanged(LiteYukonUser yukonUser, String accountNumber,
                                    String oldCustomerType, String newcustomerType) {
    }

    @Override
    public void companyNameChanged(LiteYukonUser yukonUser, String accountNumber,
                                   String oldCompanyName, String newCompanyName) {
    }

    @Override
    public void enrollmentAttempted(LiteYukonUser yukonUser, String accountNumber,
                                              String deviceName, String programName,
                                              String loadGroupName, EventSource source) {
    }

    @Override
    public void enrollmentModificationAttempted(LiteYukonUser yukonUser,
                                                String accountNumber,
                                                EventSource source) {
    }

    @Override
    public void unenrollmentAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                String deviceName, String programName,
                                                String loadGroupName, EventSource source) {
    }

    @Override
    public void deviceEnrolled(LiteYukonUser yukonUser, String accountNumber, String deviceName,
                               String programName, String loadGroupName) {
    }

    @Override
    public void deviceUnenrolled(LiteYukonUser yukonUser, String accountNumber, String deviceName,
                                 String programName, String loadGroupName) {
    }

    @Override
    public void optOutLimitReductionAttempted(LiteYukonUser user, String accountNumber,
                                              String serialNumber, EventSource source) {
    }

    @Override
    public void optOutLimitResetAttempted(LiteYukonUser user, String accountNumber,
                                          String serialNumber, EventSource source) {
    }

    @Override
    public void optOutResendAttempted(LiteYukonUser user, String accountNumber,
                                                String serialNumber, EventSource source) {
    }

    @Override
    public void optOutAttempted(LiteYukonUser user, String accountNumber,
                                          String serialNumber, ReadableInstant startDate, EventSource source) {
    }

    @Override
    public void optOutCancelAttempted(LiteYukonUser user, String accountNumber,
                                                String serialNumber,
                                                ReadableInstant optOutStartDate,
                                                ReadableInstant optOutStopDate, EventSource source) {
    }

    @Override
    public void scheduledOptOutCancelAttempted(LiteYukonUser user, String accountNumber,
                                                         String serialNumber, EventSource source) {
    }

    @Override
    public void activeOptOutCancelAttempted(LiteYukonUser user, String accountNumber,
                                                      String serialNumber, EventSource source) {
    }

    @Override
    public void optOutLimitIncreased(LiteYukonUser yukonUser, String accountNumber,
                                     String deviceName, int optOutsAdded) {
    }

    @Override
    public void optOutLimitReset(LiteYukonUser yukonUser, String accountNumber, String deviceName) {
    }

    @Override
    public void optOutResent(LiteYukonUser yukonUser, String accountNumber, String deviceName) {
    }

    @Override
    public void deviceOptedOut(LiteYukonUser yukonUser, String accountNumber, String deviceName,
                               Instant startDate, Instant stopDate) {
    }

    @Override
    public void optOutCanceled(LiteYukonUser yukonUser, String accountNumber, String deviceName) {
    }

    @Override
    public void applianceAdditionAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                     String applianceType, String deviceName,
                                                     String programName, EventSource source) {
    }

    @Override
    public void applianceUpdateAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                   String applianceType, String deviceName,
                                                   String programName, EventSource source) {
    }

    @Override
    public void applianceDeletionAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                     String applianceType, String deviceName,
                                                     String programName, EventSource source) {
    }

    @Override
    public void applianceAdded(LiteYukonUser yukonUser, String accountNumber, String applianceType,
                               String deviceName, String programName) {
    }

    @Override
    public void applianceUpdated(LiteYukonUser yukonUser, String accountNumber,
                                 String applianceType, String deviceName, String programName) {
    }

    @Override
    public void applianceDeleted(LiteYukonUser yukonUser, String accountNumber,
                                 String applianceType, String deviceName, String programName) {
    }

    @Override
    public void workOrderCreationAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                     String workOrderNumber, EventSource source) {
    }

    @Override
    public void workOrderUpdateAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                   String workOrderNumber, EventSource source) {
    }

    @Override
    public void workOrderDeletionAttempted(LiteYukonUser yukonUser, String accountNumber,
                                                     String workOrderNumber, EventSource source) {
    }

    @Override
    public void workOrderCreated(LiteYukonUser yukonUser, String accountNumber,
                                 String workOrderNumber) {
    }

    @Override
    public void workOrderUpdated(LiteYukonUser yukonUser, String accountNumber,
                                 String workOrderNumber) {
    }

    @Override
    public void workOrderDeleted(LiteYukonUser yukonUser, String accountNumber,
                                 String workOrderNumber) {
    }

    @Override
    public void thermostatScheduleSavingAttempted(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String serialNumber, String scheduleName, EventSource source) {
    }

    @Override
    public void thermostatScheduleDeleteAttempted(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String scheduleName, EventSource source) {
    }

    @Override
    public void thermostatManualSetAttempted(LiteYukonUser yukonUser, String accountNumber, String serialNumber,
                                                  Double heatTemperature, Double coolTemperature,
                                                  String mode,
                                                  boolean holdTemperature, EventSource source) {
    }

    @Override
    public void thermostatLabelChangeAttempted(LiteYukonUser yukonUser,
                                                         String serialNumber,
                                                         String oldThermostatLabel,
                                                         String newThermostatLabel,
                                                         EventSource source) {
    }

    @Override
    public void thermostatScheduleSaved(String accountNumber, String scheduleName) {
    }

    @Override
    public void thermostatScheduleDeleted(String accountNumber, String scheduleName) {
    }

    @Override
    public void thermostatManuallySet(LiteYukonUser yukonUser, String serialNumber) {
    }

    @Override
    public void thermostatLabelChanged(LiteYukonUser yukonUser, String accountNumber,
                                       String serialNumber, String oldThermostatLabel,
                                       String newThermostatLabel) {
    }

    @Override
    public void thermostatScheduleNameChanged(LiteYukonUser yukonUser, String oldScheduleName,
                                              String newScheduleName) {
    }

    @Override
    public void thermostatRunProgramAttempted(LiteYukonUser yukonUser, String serialNumber, EventSource source) {
    }

    @Override
    public void thermostatRunProgramAttempted(LiteYukonUser yukonUser, String accountNumber, String serialNumber, EventSource source) {
    }

    @Override
    public void thermostatScheduleSendDefaultAttempted(LiteYukonUser yukonUser, String serialNumber, EventSource source) {
    }

    @Override
    public void thermostatScheduleSendAttempted(LiteYukonUser yukonUser, String serialNumber, String scheduleName, EventSource source) {
    }

    @Override
    public void optOutLimitIncreaseAttempted(LiteYukonUser yukonUser, String accountNumber, String serialNumber,  EventSource source) {
    }

}