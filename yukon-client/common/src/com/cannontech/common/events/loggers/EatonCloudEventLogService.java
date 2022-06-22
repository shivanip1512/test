package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EatonCloudEventLogService {
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShedJob(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid,
            String externalEventId,
            String tryNumber,
            int dutyCyclePercent,
            int dutyCyclePeriod,
            int criticality,
            int relay);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShedJobFailed(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid,
            String externalEventId,
            String tryNumber,
            int dutyCyclePercent,
            int dutyCyclePeriod,
            int criticality,
            int relay,
            String error);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShed(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid,
            String externalEventId,
            String tryNumber,
            int dutyCyclePercent,
            int dutyCyclePeriod,
            int criticality,
            int relay);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendShedFailed(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid,
            String externalEventId,
            String tryNumber,
            int dutyCyclePercent,
            int dutyCyclePeriod,
            int criticality,
            int relay,
            String error);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendRestore(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid, String externalEventId, int relay);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void sendRestoreFailed(@Arg(ArgEnum.deviceLabel) String deviceLabel,
            String deviceGuid, String error, String externalEventId, int relay);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void tokenRetrievalFailed(String secret, String error, int tryNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void secretRotationFailed(String secret, @Arg(ArgEnum.username) LiteYukonUser user, String error, int tryNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.eatoncloud")
    public void secretRotationSuccess(String secret, @Arg(ArgEnum.username) LiteYukonUser user, int tryNumber);
}
