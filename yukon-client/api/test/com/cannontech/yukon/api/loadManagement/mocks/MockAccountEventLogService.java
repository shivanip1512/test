package com.cannontech.yukon.api.loadManagement.mocks;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MockAccountEventLogService implements AccountEventLogService{

    @Override
    public void accountCreationAttemptedThroughAccountImporter(LiteYukonUser yukonUser,
                                                               String accountNumber) {
    }

    @Override
    public void accountCreationAttemptedThroughApi(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountUpdateCreationAttemptedThroughApi(LiteYukonUser yukonUser,
                                                         String accountNumber) {
    }

    @Override
    public void accountDeletionAttemptedThroughAccountImporter(LiteYukonUser yukonUser,
                                                               String accountNumber) {
    }

    @Override
    public void accountDeletionAttemptedThroughApi(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountDeletionAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountUpdateAttemptedThroughAccountImporter(LiteYukonUser yukonUser,
                                                             String accountNumber) {
    }

    @Override
    public void accountUpdateAttemptedThroughApi(LiteYukonUser yukonUser, String accountNumber) {
    }

    @Override
    public void accountUpdateAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber) {
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
    public void enrollmentAttemptedByConsumer(LiteYukonUser yukonUser, String accountNumber,
                                              String deviceName, String programName,
                                              String loadGroupName) {
    }

    @Override
    public void enrollmentAttemptedThroughApi(LiteYukonUser yukonUser, String accountNumber,
                                              String deviceName, String programName,
                                              String loadGroupName) {
    }

    @Override
    public void enrollmentModificationAttemptedByOperator(LiteYukonUser yukonUser,
                                                          String accountNumber) {
    }

    @Override
    public void unenrollmentAttemptedByConsumer(LiteYukonUser yukonUser, String accountNumber,
                                                String deviceName, String programName,
                                                String loadGroupName) {
    }

    @Override
    public void unenrollmentAttemptedThroughApi(LiteYukonUser yukonUser, String accountNumber,
                                                String deviceName, String programName,
                                                String loadGroupName) {
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
    public void optOutLimitReductionAttemptedThroughApi(LiteYukonUser user, String accountNumber,
                                                        String serialNumber) {
    }

    @Override
    public void optOutLimitIncreaseAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                                       String serialNumber) {
    }

    @Override
    public void optOutLimitReductionAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                                        String serialNumber) {
    }

    @Override
    public void optOutLimitResetAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                                    String serialNumber) {
    }

    @Override
    public void optOutLimitResetAttemptedThroughApi(LiteYukonUser user, String accountNumber,
                                                    String serialNumber) {
    }

    @Override
    public void optOutResendAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                                String serialNumber) {
    }

    @Override
    public void optOutAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                          String serialNumber, ReadableInstant startDate) {
    }

    @Override
    public void optOutAttemptedByConsumer(LiteYukonUser user, String accountNumber,
                                          String serialNumber, ReadableInstant startDate) {
    }

    @Override
    public void optOutAttemptedThroughApi(LiteYukonUser user, String accountNumber,
                                          String serialNumber, ReadableInstant startDate) {
    }

    @Override
    public void optOutCancelAttemptedByConsumer(LiteYukonUser user, String accountNumber,
                                                String serialNumber,
                                                ReadableInstant optOutStartDate,
                                                ReadableInstant optOutStopDate) {
    }

    @Override
    public void optOutCancelAttemptedByOperator(LiteYukonUser user, String accountNumber,
                                                String serialNumber,
                                                ReadableInstant optOutStartDate,
                                                ReadableInstant optOutStopDate) {
    }

    @Override
    public void scheduledOptOutCancelAttemptedThroughApi(LiteYukonUser user, String accountNumber,
                                                         String serialNumber) {
    }

    @Override
    public void activeOptOutCancelAttemptedThroughApi(LiteYukonUser user, String accountNumber,
                                                      String serialNumber) {
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
    public void applianceAdditionAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                     String applianceType, String deviceName,
                                                     String programName) {
    }

    @Override
    public void applianceUpdateAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                   String applianceType, String deviceName,
                                                   String programName) {
    }

    @Override
    public void applianceDeletionAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                     String applianceType, String deviceName,
                                                     String programName) {
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
    public void workOrderCreationAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                     String workOrderNumber) {
    }

    @Override
    public void workOrderUpdateAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                   String workOrderNumber) {
    }

    @Override
    public void workOrderDeletionAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber,
                                                     String workOrderNumber) {
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
    public void thermostatScheduleSavingAttemptedByOperator(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String serialNumber, String scheduleName) {
    }

    @Override
    public void thermostatScheduleSavingAttemptedByConsumer(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String serialNumber, String scheduleName) {
    }

    @Override
    public void thermostatScheduleDeleteAttemptedByOperator(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String scheduleName) {
    }

    @Override
    public void thermostatScheduleDeleteAttemptedByConsumer(LiteYukonUser yukonUser,
                                                            String accountNumber,
                                                            String scheduleName) {
    }

    @Override
    public void thermostatManualSetAttemptedByApi(LiteYukonUser yukonUser, String serialNumber, int heatTemperature,
                                                  int coolTemperature, String mode, String fanSetting, boolean holdTemperature) {
    }

    @Override
    public void thermostatManualSetAttemptedByOperator(LiteYukonUser yukonUser,
                                                       String accountNumber, String serialNumber) {
    }

    @Override
    public void thermostatManualSetAttemptedByConsumer(LiteYukonUser yukonUser,
                                                       String accountNumber, String serialNumber) {
    }

    @Override
    public void thermostatLabelChangeAttemptedByConsumer(LiteYukonUser yukonUser,
                                                         String serialNumber,
                                                         String oldThermostatLabel,
                                                         String newThermostatLabel) {
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
    public void thermostatRunProgramAttemptedByApi(LiteYukonUser yukonUser, String serialNumber) {
    }

    @Override
    public void thermostatRunProgramAttemptedByOperator(LiteYukonUser yukonUser, String accountNumber, String serialNumber) {
    }

    @Override
    public void thermostatRunProgramAttemptedByConsumer(LiteYukonUser yukonUser, String accountNumber, String serialNumber) {
    }

    @Override
    public void thermostatScheduleSendDefaultAttemptedByApi(LiteYukonUser yukonUser, String serialNumber) {
    }

    @Override
    public void thermostatScheduleSendAttemptedByApi(LiteYukonUser yukonUser, String serialNumber, String scheduleName) {
    }
}