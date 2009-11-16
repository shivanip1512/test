package com.cannontech.common.events.loggers;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandRequestExecutorEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="porterRequests")
    public void commandFailedToTransmit(int creId, int contextId, CommandRequestExecutionType creType, String currentCommandString, String error, LiteYukonUser user);
}
