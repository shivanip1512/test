package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface HardwareEventLogService {

/* HARDWARE */    
    // Hardware
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareMeterCreationAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                         @Arg(ArgEnum.meterName) String meterName);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareChangeOutForMeterAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                             @Arg(ArgEnum.serialNumber) String oldSerialNumber,
                                                             @Arg(ArgEnum.meterName) String meterName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareChangeOutAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                     @Arg(ArgEnum.serialNumber) String oldSerialNumber,
                                                     @Arg(ArgEnum.serialNumber) String newSerialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareCreationAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(ArgEnum.accountNumber) String accountNumber,
                                                     @Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareCreationAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                                @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void twoWayHardwareCreationAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                          @Arg(ArgEnum.deviceName) String deviceName);
    

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void assigningHardwareAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                     @Arg(ArgEnum.accountNumber) String accountNumber,
                                                     @Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareAdditionAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(ArgEnum.accountNumber) String accountNumber,
                                                    @Arg(ArgEnum.serialNumber) String serialNumber);
    
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                              @Arg(ArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(ArgEnum.accountNumber) String accountNumber,
                                                  @Arg(ArgEnum.serialNumber) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareDeletionAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(ArgEnum.serialNumber) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareRemovalAttemptedThroughAccountImporter(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(ArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareRemovalAttemptedThroughApi(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(ArgEnum.accountNumber) String accountNumber,
                                                   @Arg(ArgEnum.serialNumber) String serialNumber);

    // Hardware Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                @Arg(ArgEnum.deviceLabel) String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                              @Arg(ArgEnum.deviceLabel) String deviceLabel,
                              @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareRemoved(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel,
                                @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                @Arg(ArgEnum.deviceLabel) String deviceLabel);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void serialNumberChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                    @Arg(ArgEnum.serialNumber) String oldSerialNumber, 
                                    @Arg(ArgEnum.serialNumber) String newSerialNumber);

    
/* HARDWARE CONFIGURATION */
    // hardware configuration
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void hardwareConfigAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.serialNumber) String serialNumber, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void hardwareDisableAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                     @Arg(ArgEnum.serialNumber) String serialNumber, 
                                                     @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware.config")
    public void hardwareEnableAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                  @Arg(ArgEnum.serialNumber) String serialNumber, 
                                                  @Arg(ArgEnum.accountNumber) String accountNumber);

    
    // hardware configuration Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void hardwareConfigUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                                  @Arg(ArgEnum.serialNumber) String serialNumber, 
                                                  @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void hardwareDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                 @Arg(ArgEnum.serialNumber) String serialNumber, 
                                 @Arg(ArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware.config")
    public void hardwareEnabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                @Arg(ArgEnum.serialNumber) String serialNumber, 
                                @Arg(ArgEnum.accountNumber) String accountNumber);

}
