package com.cannontech.common.events.loggers;

import com.cannontech.common.device.commands.CommandRequestExecutionType;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandRequestExecutorEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="porterRequests")
    public void commandFailedToTransmit(@Arg(EventLogArgEnum.commandRequestExecutionId) int creId, 
                                        @Arg(EventLogArgEnum.commandRequestExecutionContextId) int contextId, 
                                        @Arg(EventLogArgEnum.commandRequestExecutionType) CommandRequestExecutionType creType,
                                        String currentCommandString,
                                        String error,
                                        @Arg(EventLogArgEnum.username) LiteYukonUser user);

	@YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="porterRequests")
	public void foundFailedCre(@Arg(EventLogArgEnum.commandRequestExecutionId) int creId);
}
