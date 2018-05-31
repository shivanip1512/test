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
                                          @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void disconnectInitiated(@Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                    @Arg(ArgEnum.deviceName) String deviceName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void actionCompleted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.commandRequestString) DisconnectCommand command,
                                @Arg(ArgEnum.deviceName) String deviceName,
                                DisconnectDeviceState disconnectDeviceState,
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
    public void disconnectInitiated(String action,
                                    String detail,
                                       @Arg(ArgEnum.totalCount)Integer numDevices,
                                       @Arg(ArgEnum.username)LiteYukonUser username,
                                       @Arg(ArgEnum.resultKey)String key);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.group.disconnect")
    public void disconnectCompleted(String action,
                                    String detail,
                                    String creStatus,
                                    @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.group.disconnect")
    public void disconnectCancelled(String action,
                                      String detail,
                                      @Arg(ArgEnum.username) LiteYukonUser user,
                                      @Arg(ArgEnum.resultKey) String resultKey);

}
