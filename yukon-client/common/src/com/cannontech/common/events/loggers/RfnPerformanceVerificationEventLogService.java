package com.cannontech.common.events.loggers;


import org.joda.time.DateTime;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface RfnPerformanceVerificationEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfnPerformanceVerificationEventStatus")
    public void archivedRfnBroadcastEventStatus(DateTime beforeDate);

}