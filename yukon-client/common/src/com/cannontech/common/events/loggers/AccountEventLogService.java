package com.cannontech.common.events.loggers;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface AccountEventLogService {

/* ACCOUNTS */
    // Account
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                       @Arg(ArgEnum.accountNumber) String accountNumber,
                                       @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                                         @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                           @Arg(ArgEnum.accountNumber) String accountNumber,
                                           @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                         @Arg(ArgEnum.eventSource) EventSource source);

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
    public void enrollmentAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.accountNumber) String accountNumber, 
                                              @Arg(ArgEnum.deviceName) String deviceName, 
                                              @Arg(ArgEnum.programName) String programName,
                                              @Arg(ArgEnum.loadGroupName) String loadGroupName,
                                              @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentModificationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(ArgEnum.accountNumber) String accountNumber,
                                                  @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                        @Arg(ArgEnum.deviceName) String deviceName,
                                        @Arg(ArgEnum.programName) String programName,
                                        @Arg(ArgEnum.loadGroupName) String loadGroupName,
                                        @Arg(ArgEnum.eventSource) EventSource source);
    
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
    public void optOutLimitReductionAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                                        @Arg(ArgEnum.deviceName) String serialNumber,
                                                        @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitIncreaseAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                       @Arg(ArgEnum.accountNumber) String accountNumber,
                                                       @Arg(ArgEnum.deviceName) String serialNumber,
                                                       @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitResetAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                    @Arg(ArgEnum.accountNumber) String accountNumber,
                                                    @Arg(ArgEnum.deviceName) String serialNumber,
                                                    @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutResendAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                @Arg(ArgEnum.accountNumber) String accountNumber,
                                                @Arg(ArgEnum.deviceName) String serialNumber,
                                                @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate,
                                          @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutCancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                               @Arg(ArgEnum.accountNumber) String accountNumber,
                                               @Arg(ArgEnum.deviceName) String serialNumber,
                                               ReadableInstant optOutStartDate,
                                               ReadableInstant optOutStopDate,
                                               @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void scheduledOptOutCancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                 @Arg(ArgEnum.accountNumber) String accountNumber,
                                                 @Arg(ArgEnum.deviceName) String serialNumber,
                                                 @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void activeOptOutCancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                              @Arg(ArgEnum.accountNumber) String accountNumber,
                                              @Arg(ArgEnum.deviceName) String serialNumber,
                                              @Arg(ArgEnum.eventSource) EventSource source);
    
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
    public void applianceAdditionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.applianceType) String applianceType,
                                                     @Arg(ArgEnum.deviceName) String deviceName,
                                                     @Arg(ArgEnum.programName) String programName,
                                                     @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceUpdateAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                   @Arg(ArgEnum.applianceType) String applianceType,
                                                   @Arg(ArgEnum.deviceName) String deviceName,
                                                   @Arg(ArgEnum.programName) String programName,
                                                   @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceDeletionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                      @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                      @Arg(ArgEnum.applianceType) String applianceType,
                                                      @Arg(ArgEnum.deviceName) String deviceName,
                                                      @Arg(ArgEnum.programName) String programName,
                                                      @Arg(ArgEnum.eventSource) EventSource source);

    
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
    public void workOrderCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.workOrderNumber) String workOrderNumber,
                                                     @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderUpdateAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                   @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                   @Arg(ArgEnum.workOrderNumber) String workOrderNumber,
                                                   @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.workOrder")
    public void workOrderDeletionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(ArgEnum.workOrderNumber) String workOrderNumber,
                                                     @Arg(ArgEnum.eventSource) EventSource source);
    
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
    public void thermostatScheduleSavingAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName,
                                                            @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSendDefaultAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSendAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.serialNumber) String serialNumber,
                                                            @Arg(ArgEnum.scheduleName) String scheduleName,
                                                            @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleDeleteAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(ArgEnum.scheduleName) String scheduleName,
                                                            @Arg(ArgEnum.eventSource) EventSource source);


    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatRunProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(ArgEnum.serialNumber) String serialNumber,
                                                       @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatRunProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(ArgEnum.accountNumber) String accountNumber, 
                                                       @Arg(ArgEnum.serialNumber) String serialNumber,
                                                       @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatManualSetAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(ArgEnum.accountNumber) String accountNumber,
                                               @Arg(ArgEnum.serialNumber) String serialNumber,
                                               Double heatTemperature,
                                               Double coolTemperature,
                                               String mode,
                                               boolean holdTemperature,
                                               @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatLabelChangeAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(ArgEnum.serialNumber) String serialNumber,
                                                         @Arg(ArgEnum.thermostatLabel) String oldThermostatLabel,
                                                         @Arg(ArgEnum.thermostatLabel) String newThermostatLabel,
                                                         @Arg(ArgEnum.eventSource) EventSource source);
    
    // Thermostat Schedule Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleSaved(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                        @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleDeleted(@Arg(ArgEnum.accountNumber) String accountNumber, 
                                          @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatManuallySet(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
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
