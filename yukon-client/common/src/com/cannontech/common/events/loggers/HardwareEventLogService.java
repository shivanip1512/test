package com.cannontech.common.events.loggers;

import org.springframework.stereotype.Service;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

@Service
public interface HardwareEventLogService {

/* HARDWARE */    
    // Hardware
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareMeterCreationAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                                         @Arg(EventLogArgEnum.meterName) String meterName);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareChangeOutForMeterAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                             @Arg(EventLogArgEnum.serialNumber) String oldSerialNumber,
                                                             @Arg(EventLogArgEnum.meterName) String meterName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareChangeOutAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                     @Arg(EventLogArgEnum.serialNumber) String oldSerialNumber,
                                                     @Arg(EventLogArgEnum.serialNumber) String newSerialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareCreationAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                     @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareCreationAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                                @Arg(EventLogArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void twoWayHardwareCreationAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                          @Arg(EventLogArgEnum.deviceName) String deviceName);
    

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void assigningHardwareAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                     @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                     @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareAdditionAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                    @Arg(EventLogArgEnum.serialNumber) String serialNumber);
    
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(EventLogArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                              @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareUpdateAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                  @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                  @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareDeletionAttemptedByOperator(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                    @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareRemovalAttemptedThroughAccountImporter(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                               @Arg(EventLogArgEnum.serialNumber) String serialNumber);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="hardware")
    public void hardwareRemovalAttemptedThroughAPI(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(EventLogArgEnum.accountNumber) String accountNumber,
                                                   @Arg(EventLogArgEnum.serialNumber) String serialNumber);

    // Hardware Service Level
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareCreated(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                @Arg(EventLogArgEnum.deviceLabel) String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareAdded(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                              @Arg(EventLogArgEnum.deviceLabel) String deviceLabel,
                              @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareUpdated(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(EventLogArgEnum.deviceLabel) String deviceLabel);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareRemoved(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(EventLogArgEnum.deviceLabel) String deviceLabel,
                                @Arg(EventLogArgEnum.accountNumber) String accountNumber);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void hardwareDeleted(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                @Arg(EventLogArgEnum.deviceLabel) String deviceLabel);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="hardware")
    public void serialNumberChanged(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                    @Arg(EventLogArgEnum.serialNumber) String oldSerialNumber, 
                                    @Arg(EventLogArgEnum.serialNumber) String newSerialNumber);

    
    
}
