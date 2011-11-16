package com.cannontech.common.events.loggers;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AccountEventLogService {

/* ACCOUNTS */
    // Account
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountCreationAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountCreationAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateCreationAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(ArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(ArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(ArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                             @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                 @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                 @Arg(ArgEnum.accountNumber) String accountNumber);
    
    // Account Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                             @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountNumberChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                     @Arg(ArgEnum.accountNumber) String oldAccountNumber,
                                     @Arg(ArgEnum.accountNumber) String newAccountNumber);
    
/* CONTACT INFO */
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                              @Arg(ArgEnum.accountNumber) String accountNumber, 
                              @Arg(ArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.accountNumber) String accountNumber, 
                               @Arg(ArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactRemoved(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.accountNumber) String accountNumber, 
                               @Arg(ArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactNameChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                   @Arg(ArgEnum.contactName) String oldContactName,
                                   @Arg(ArgEnum.contactName) String newContactName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void customerTypeChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                    @Arg(ArgEnum.accountNumber) String accountNumber, 
                                    String oldCustomerType,
                                    String newcustomerType);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void companyNameChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                   String oldCompanyName,
                                   String newCompanyName);

    
/* ENROLLMENTS */
    // Enrollment
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.accountNumber) String accountNumber, 
                                              @Arg(ArgEnum.deviceName) String deviceName, 
                                              @Arg(ArgEnum.programName) String programName,
                                              @Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.accountNumber) String accountNumber,
                                              @Arg(ArgEnum.deviceName) String deviceName,
                                              @Arg(ArgEnum.programName) String programName,
                                              @Arg(ArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentModificationAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                          @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(ArgEnum.accountNumber) String accountNumber,
                                                @Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.programName) String programName,
                                                @Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(ArgEnum.accountNumber) String accountNumber,
                                                @Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.programName) String programName,
                                                @Arg(ArgEnum.loadGroupName) String loadGroupName);
    
    // Enrollment Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.enrollment")
    public void deviceEnrolled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.accountNumber) String accountNumber,
                               @Arg(ArgEnum.deviceName) String deviceName,
                               @Arg(ArgEnum.programName) String programName,
                               @Arg(ArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.enrollment")
    public void deviceUnenrolled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(ArgEnum.accountNumber) String accountNumber,
                                 @Arg(ArgEnum.deviceName) String deviceName,
                                 @Arg(ArgEnum.programName) String programName,
                                 @Arg(ArgEnum.loadGroupName) String loadGroupName);
    
/* Opt Outs */
    // Opt Outs
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitReductionAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                                        @Arg(ArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitIncreaseAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                       @Arg(ArgEnum.accountNumber) String accountNumber,
                                                       @Arg(ArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitReductionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                                        @Arg(ArgEnum.deviceName) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitResetAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                    @Arg(ArgEnum.accountNumber) String accountNumber,
                                                    @Arg(ArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitResetAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                    @Arg(ArgEnum.accountNumber) String accountNumber,
                                                    @Arg(ArgEnum.deviceName) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutResendAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                @Arg(ArgEnum.accountNumber) String accountNumber,
                                                @Arg(ArgEnum.deviceName) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate);
    
    
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutCancelAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser user,
                                               @Arg(ArgEnum.accountNumber) String accountNumber,
                                               @Arg(ArgEnum.deviceName) String serialNumber,
                                               ReadableInstant optOutStartDate,
                                               ReadableInstant optOutStopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutCancelAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                               @Arg(ArgEnum.accountNumber) String accountNumber,
                                               @Arg(ArgEnum.deviceName) String serialNumber,
                                               ReadableInstant optOutStartDate,
                                               ReadableInstant optOutStopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void scheduledOptOutCancelAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                                         @Arg(ArgEnum.deviceName) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void activeOptOutCancelAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                      @Arg(ArgEnum.accountNumber) String accountNumber,
                                                      @Arg(ArgEnum.deviceName) String serialNumber);
    
    // Opt Out Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutLimitIncreased(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                     @Arg(ArgEnum.accountNumber) String accountNumber,
                                     @Arg(ArgEnum.deviceName) String deviceName,
                                     int optOutsAdded);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutLimitReset(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(ArgEnum.accountNumber) String accountNumber,
                                 @Arg(ArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutResent(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                             @Arg(ArgEnum.accountNumber) String accountNumber,
                             @Arg(ArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void deviceOptedOut(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.accountNumber) String accountNumber,
                               @Arg(ArgEnum.deviceName) String deviceName, 
                               Instant startDate,
                               Instant stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutCanceled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.accountNumber) String accountNumber,
                               @Arg(ArgEnum.deviceName) String deviceName);

    
    
/* Appliances */
    // Appliance
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceAdditionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.applianceType) String applianceType,
                                                     @Arg(ArgEnum.deviceName) String deviceName,
                                                     @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceUpdateAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                   @Arg(ArgEnum.applianceType) String applianceType,
                                                   @Arg(ArgEnum.deviceName) String deviceName,
                                                   @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceDeletionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                      @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                      @Arg(ArgEnum.applianceType) String applianceType,
                                                      @Arg(ArgEnum.deviceName) String deviceName,
                                                      @Arg(ArgEnum.programName) String programName);

    
    // Appliance Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.accountNumber) String accountNumber, 
                               @Arg(ArgEnum.applianceType) String applianceType,
                               @Arg(ArgEnum.deviceName) String deviceName,
                               @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(ArgEnum.accountNumber) String accountNumber, 
                                 @Arg(ArgEnum.applianceType) String applianceType,
                                 @Arg(ArgEnum.deviceName) String deviceName,
                                 @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(ArgEnum.accountNumber) String accountNumber, 
                                 @Arg(ArgEnum.applianceType) String applianceType,
                                 @Arg(ArgEnum.deviceName) String deviceName,
                                 @Arg(ArgEnum.programName) String programName);

/* Work Orders */
    // Work Order
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderCreationAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.workOrderNumber) String workOrderNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderUpdateAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                   @Arg(ArgEnum.workOrderNumber) String workOrderNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderDeletionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.workOrderNumber) String workOrderNumber);
    
    // Work Order Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(ArgEnum.accountNumber) String accountNumber, 
                                 @Arg(ArgEnum.workOrderNumber) String workOrderNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(ArgEnum.accountNumber) String accountNumber, 
                                 @Arg(ArgEnum.workOrderNumber) String workOrderNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(ArgEnum.accountNumber) String accountNumber, 
                                 @Arg(ArgEnum.workOrderNumber) String workOrderNumber);
    
/* THERMOSTAT SCHEDULES */
    // Thermostat Schedule
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSavingAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSavingAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleDeleteAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleDeleteAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName);


    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatManualSetAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                       @Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatManualSetAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                       @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatLabelChangeAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                         @Arg(ArgEnum.serialNumber) String serialNumber,
                                                         @Arg(ArgEnum.thermostatLabel) String oldThermostatLabel,
                                                         @Arg(ArgEnum.thermostatLabel) String newThermostatLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatLabelChangeAttemptedByConsumer(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(ArgEnum.serialNumber) String serialNumber,
                                                         @Arg(ArgEnum.thermostatLabel) String oldThermostatLabel,
                                                         @Arg(ArgEnum.thermostatLabel) String newThermostatLabel);

    
    // Thermostat Schedule Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleSaved(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                        @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleDeleted(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                          @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatManuallySet(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                      @Arg(ArgEnum.accountNumber) String accountNumber, 
                                      @Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatLabelChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                       @Arg(ArgEnum.accountNumber) String accountNumber, 
                                       @Arg(ArgEnum.serialNumber) String serialNumber,
                                       @Arg(ArgEnum.thermostatLabel) String oldThermostatLabel,
                                       @Arg(ArgEnum.thermostatLabel) String newThermostatLabel);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleNameChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.scheduleName) String oldScheduleName,
                                              @Arg(ArgEnum.scheduleName) String newScheduleName);

}
