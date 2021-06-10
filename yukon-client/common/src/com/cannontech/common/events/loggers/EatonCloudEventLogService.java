package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface EatonCloudEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShed(String deviceLabel,
                         String deviceGuid,
                         int dutyCyclePercent,
                         int dutyCyclePeriod,
                         int criticality);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendRestore(@Arg(ArgEnum.deviceLabel) String deviceLabel,
                            String deviceGuid);

}
