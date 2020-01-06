package com.cannontech.common.events.loggers;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DisconnectEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void disconnectAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                          @Arg(ArgEnum.deviceName) String deviceName,
                                          @Arg(ArgEnum.meterNumber) String meterNumber);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void disconnectInitiated(@Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                    @Arg(ArgEnum.deviceName) String deviceName,
                                    @Arg(ArgEnum.meterNumber) String meterNumber);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void actionCompleted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                @Arg(ArgEnum.deviceName) String deviceName,
                                DisconnectDeviceState disconnectDeviceState,
                                @Arg(ArgEnum.meterNumber) String meterNumber,
                                Integer successOrFail);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupDisconnectAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.commandRequestString) DisconnectCommand command);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupCancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.commandRequestString) DisconnectCommand command);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupActionCompleted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                @Arg(ArgEnum.totalCount) Integer total,
                                @Arg(ArgEnum.successCount) Integer success,
                                @Arg(ArgEnum.failureCount) Integer failure,
                                @Arg(ArgEnum.notAttemptedCount) Integer notAttempted
            );
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void loadSideVoltageDetectedWhileDisconnected(@Arg(ArgEnum.username) LiteYukonUser user,
                                        @Arg(ArgEnum.deviceName) String deviceName
                                        );
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.group.disconnect")
    public void disconnectInitiated(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.totalCount)Integer numDevices,
                                    @Arg(ArgEnum.username)LiteYukonUser username,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.group.disconnect")
    public void disconnectCompleted(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.status)String creStatus,
                                    @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.group.disconnect")
    public void disconnectCancelled(@Arg(ArgEnum.action)String action,
                                    @Arg(ArgEnum.input)String input,
                                    @Arg(ArgEnum.statistics)String statistics,
                                    @Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.resultKey) String resultKey);

}
