package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface OutageEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="system.outageEvent")
    public void mspMessageSentToVendor(String source, 
                                       String eventType, 
                                       String objectId,
                                       String deviceType,
                                       String mspVendor);
}