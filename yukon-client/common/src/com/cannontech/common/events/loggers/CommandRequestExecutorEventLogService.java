package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface CommandRequestExecutorEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="commandRequestExecutor")
    public void connectionException(int id, int contextId);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="commandRequestExecutor")
    public void exception(int id, int contextId, String error);
}
