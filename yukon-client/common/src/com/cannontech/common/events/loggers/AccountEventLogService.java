package com.cannontech.common.events.loggers;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.stereotype.Service;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public interface AccountEventLogService {

/* ACCOUNTS */
    // Account
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountCreationAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountCreationAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(EventLogArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountDeletionAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                             @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account")
    public void accountUpdateAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber);
    
    // Account Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountAdded(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                             @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountUpdated(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountDeleted(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account")
    public void accountNumberChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                     @Arg(EventLogArgEnum.accountNumber) String oldAccountNumber,
                                     @Arg(EventLogArgEnum.accountNumber) String newAccountNumber);
    
/* CONTACT INFO */
    // Contact Info
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.contactInfo")
    public void contact(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                              @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                              @Arg(EventLogArgEnum.deviceName) String deviceName, 
                                              @Arg(EventLogArgEnum.programName) String programName, 
                                              @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    // Contact Info Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactAdded(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                              @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                              @Arg(EventLogArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactUpdated(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                               @Arg(EventLogArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactRemoved(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                               @Arg(EventLogArgEnum.contactName) String contactName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void contactNameChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                   @Arg(EventLogArgEnum.contactName) String oldContactName,
                                   @Arg(EventLogArgEnum.contactName) String newContactName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void customerTypeChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                    @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                    String oldCustomerType,
                                    String newcustomerType);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.contactInfo")
    public void companyNameChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                   String oldCompanyName,
                                   String newCompanyName);

    
/* ENROLLMENTS */
    // Enrollment
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                              @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                              @Arg(EventLogArgEnum.deviceName) String deviceName, 
                                              @Arg(EventLogArgEnum.programName) String programName, 
                                              @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                              @Arg(EventLogArgEnum.deviceName) String deviceName, 
                                              @Arg(EventLogArgEnum.programName) String programName,
                                              @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                              @Arg(EventLogArgEnum.deviceName) String deviceName,
                                              @Arg(EventLogArgEnum.programName) String programName,
                                              @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void enrollmentEditAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                  @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                  @Arg(EventLogArgEnum.programName) String programName,
                                                  @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                @Arg(EventLogArgEnum.programName) String programName,
                                                @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                @Arg(EventLogArgEnum.programName) String programName,
                                                @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.enrollment")
    public void unenrollmentAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                @Arg(EventLogArgEnum.programName) String programName,
                                                @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
    // Enrollment Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.enrollment")
    public void deviceEnrolled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                               @Arg(EventLogArgEnum.deviceName) String deviceName,
                               @Arg(EventLogArgEnum.programName) String programName,
                               @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.enrollment")
    public void deviceUnenrolled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                 @Arg(EventLogArgEnum.deviceName) String deviceName,
                                 @Arg(EventLogArgEnum.programName) String programName,
                                 @Arg(EventLogArgEnum.loadGroupName) String loadGroupName);
    
/* Opt Outs */
    // Opt Outs
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitReductionAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                        @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                        @Arg(EventLogArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitIncreaseAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                       @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                       @Arg(EventLogArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitReductionAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                        @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                        @Arg(EventLogArgEnum.deviceName) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitResetAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                    @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                    @Arg(EventLogArgEnum.deviceName) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutLimitResetAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                    @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                    @Arg(EventLogArgEnum.deviceName) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutResendAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                                @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                @Arg(EventLogArgEnum.deviceName) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                          @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                          @Arg(EventLogArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                          @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                          @Arg(EventLogArgEnum.deviceName) String serialNumber,
                                          ReadableInstant startDate);
    
    
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutCancelAtteptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                               @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                               @Arg(EventLogArgEnum.deviceName) String serialNumber,
                                               ReadableInstant optOutStartDate,
                                               ReadableInstant optOutStopDate);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.optOut")
    public void optOutCancelAtteptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser user,
                                               @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                               @Arg(EventLogArgEnum.deviceName) String serialNumber,
                                               ReadableInstant optOutStartDate,
                                               ReadableInstant optOutStopDate);
    
    
    // Opt Out Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutLimitIncreased(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                     @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                     @Arg(EventLogArgEnum.deviceName) String deviceName,
                                     int optOutsAdded);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutLimitReset(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                 @Arg(EventLogArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutResent(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                             @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                             @Arg(EventLogArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void deviceOptedOut(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                               @Arg(EventLogArgEnum.deviceName) String deviceName, 
                               Instant startDate,
                               Instant stopDate);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.optOut")
    public void optOutCanceled(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                               @Arg(EventLogArgEnum.deviceName) String deviceName);

    
    
/* Appliances */
    // Appliance
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceAdditionAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                     @Arg(EventLogArgEnum.applianceType) String applianceType,
                                                     @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                     @Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceUpdateAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                   @Arg(EventLogArgEnum.applianceType) String applianceType,
                                                   @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                   @Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceDelettionAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                                      @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                      @Arg(EventLogArgEnum.applianceType) String applianceType,
                                                      @Arg(EventLogArgEnum.deviceName) String deviceName,
                                                      @Arg(EventLogArgEnum.programName) String programName);

    
    // Appliance Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceAdded(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                               @Arg(EventLogArgEnum.applianceType) String applianceType,
                               @Arg(EventLogArgEnum.deviceName) String deviceName,
                               @Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceUpdated(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                 @Arg(EventLogArgEnum.applianceType) String applianceType,
                                 @Arg(EventLogArgEnum.deviceName) String deviceName,
                                 @Arg(EventLogArgEnum.programName) String programName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.appliance")
    public void applianceDeleted(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                 @Arg(EventLogArgEnum.applianceType) String applianceType,
                                 @Arg(EventLogArgEnum.deviceName) String deviceName,
                                 @Arg(EventLogArgEnum.programName) String programName);


/* THERMOSTAT SCHEDULES */
    // Thermostat Schedule
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSavingAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(EventLogArgEnum.serialNumber) String serialNumber,
                                                            @Arg(EventLogArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatScheduleSavingAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                            @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                            @Arg(EventLogArgEnum.serialNumber) String serialNumber,
                                                            @Arg(EventLogArgEnum.scheduleName) String scheduleName);
    
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatManualSetAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                       @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatManualSetAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                       @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                       @Arg(EventLogArgEnum.serialNumber) String serialNumber);
    

    /////////
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatLabelChangeAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                                         @Arg(EventLogArgEnum.serialNumber) String serialNumber,
                                                         @Arg(EventLogArgEnum.thermostatLabel) String oldThermostatLabel,
                                                         @Arg(EventLogArgEnum.thermostatLabel) String newThermostatLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="stars.account.thermostat")
    public void thermostatLabelChangeAttemptedByConsumer(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                         @Arg(EventLogArgEnum.serialNumber) String serialNumber,
                                                         @Arg(EventLogArgEnum.thermostatLabel) String oldThermostatLabel,
                                                         @Arg(EventLogArgEnum.thermostatLabel) String newThermostatLabel);

    
    // Thermostat Schedule Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleSaved(@Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                        @Arg(EventLogArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatManuallySet(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                      @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                      @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatLabelChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                       @Arg(EventLogArgEnum.accountNumber) String accountNumber, 
                                       @Arg(EventLogArgEnum.serialNumber) String serialNumber,
                                       @Arg(EventLogArgEnum.thermostatLabel) String oldThermostatLabel,
                                       @Arg(EventLogArgEnum.thermostatLabel) String newThermostatLabel);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="stars.account.thermostat")
    public void thermostatScheduleNameChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                              @Arg(EventLogArgEnum.scheduleName) String oldScheduleName,
                                              @Arg(EventLogArgEnum.scheduleName) String newScheduleName);

    ////////////////
}
