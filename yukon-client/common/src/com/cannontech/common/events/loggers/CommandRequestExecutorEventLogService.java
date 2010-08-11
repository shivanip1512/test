package com.cannontech.common.events.loggers;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandRequestExecutorEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="porterRequests")
    public void commandFailedToTransmit(@Arg(ArgEnum.commandRequestExecutionId) int creId, 
                                        @Arg(ArgEnum.commandRequestExecutionContextId) int contextId, 
                                        @Arg(ArgEnum.commandRequestExecutionType) CommandRequestExecutionType creType,
                                        String currentCommandString,
                                        String error,
                                        @Arg(ArgEnum.username) LiteYukonUser user);

	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="porterRequests")
	public void foundFailedCre(@Arg(ArgEnum.commandRequestExecutionId) int creId);
}
