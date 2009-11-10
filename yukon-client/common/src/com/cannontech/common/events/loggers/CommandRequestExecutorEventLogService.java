package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface CommandRequestExecutorEventLogService {

	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="porterRequests")
    public void connectionException(String error);
	
	@YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="porterRequests")
    public void exception(String error);
}
