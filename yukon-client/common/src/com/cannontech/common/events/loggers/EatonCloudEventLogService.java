package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface EatonCloudEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShed(int deviceId,
                         String deviceGuid);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendRestore(int deviceId,
                            String deviceGuid);

}
