package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DisconnectEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void disconnectAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.command) String command,
                                          @Arg(ArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupDisconnectAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                    @Arg(ArgEnum.command) String command);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupCancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.command) String command);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.disconnect")
    public void groupActionCompleted(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.command) String command,
                                @Arg(ArgEnum.total) Integer total,
                                @Arg(ArgEnum.success) Integer success,
                                @Arg(ArgEnum.failure) Integer failure,
                                @Arg(ArgEnum.notAttempted) Integer notAttempted
            );
}
