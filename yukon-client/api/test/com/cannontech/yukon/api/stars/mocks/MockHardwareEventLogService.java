package com.cannontech.yukon.api.stars.mocks;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.loggers.ArgEnum;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public class MockHardwareEventLogService implements HardwareEventLogService {

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareMeterCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                               @Arg(ArgEnum.meterName) String meterName,
                                               @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareChangeOutForMeterAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                   @Arg(ArgEnum.meterName) String oldMeterName,
                                                   @Arg(ArgEnum.meterName) String newMeterName,
                                                   @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareChangeOutAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                           @Arg(ArgEnum.serialNumber) String oldSerialNumber,
                                           @Arg(ArgEnum.serialNumber) String newSerialNumber,
                                           @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.serialNumber) String serialNumber,
                                          @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void twoWayHardwareCreationAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                                @Arg(ArgEnum.deviceName) String deviceName,
                                                @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void assigningHardwareAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                           @Arg(ArgEnum.accountNumber) String accountNumber,
                                           @Arg(ArgEnum.serialNumber) String serialNumber,
                                           @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareAdditionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                          @Arg(ArgEnum.accountNumber) String accountNumber,
                                          @Arg(ArgEnum.serialNumber) String serialNumber,
                                          @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareUpdateAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                        @Arg(ArgEnum.serialNumber) String serialNumber,
                                        @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareDeletionAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                          @Arg(ArgEnum.deviceLabel) String deviceLabel,
                                          @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware")
    public void hardwareRemovalAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                         @Arg(ArgEnum.serialNumber) String serialNumber,
                                         @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void hardwareCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void hardwareAdded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                              @Arg(ArgEnum.deviceLabel) String deviceLabel,
                              @Arg(ArgEnum.accountNumber) String accountNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void hardwareUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void hardwareRemoved(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel,
                                @Arg(ArgEnum.accountNumber) String accountNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void hardwareDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.deviceLabel) String deviceLabel) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware")
    public void serialNumberChanged(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                    @Arg(ArgEnum.serialNumber) String oldSerialNumber,
                                    @Arg(ArgEnum.serialNumber) String newSerialNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware.config")
    public void hardwareConfigAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(ArgEnum.serialNumber) String serialNumber,
                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                        @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware.config")
    public void hardwareDisableAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                         @Arg(ArgEnum.serialNumber) String serialNumber,
                                         @Arg(ArgEnum.accountNumber) String accountNumber,
                                         @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "hardware.config")
    public void hardwareEnableAttempted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                        @Arg(ArgEnum.serialNumber) String serialNumber,
                                        @Arg(ArgEnum.accountNumber) String accountNumber,
                                        @Arg(ArgEnum.eventSource) EventSource source) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware.config")
    public void hardwareConfigUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                      @Arg(ArgEnum.serialNumber) String serialNumber,
                                      @Arg(ArgEnum.accountNumber) String accountNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware.config")
    public void hardwareDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                 @Arg(ArgEnum.serialNumber) String serialNumber,
                                 @Arg(ArgEnum.accountNumber) String accountNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware.config")
    public void hardwareEnabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                @Arg(ArgEnum.serialNumber) String serialNumber,
                                @Arg(ArgEnum.accountNumber) String accountNumber) {
    }

    @Override
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "hardware.config")
    public void hardwareConfigUnsupported(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.serialNumber) String serialNumber) {

    }   
}