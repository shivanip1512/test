package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DataStreamingEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void unassignAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey,
            @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void assignAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey,
            @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void resendAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey,
            @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void completedResults(@Arg(ArgEnum.resultKey) String resultKey, @Arg(ArgEnum.totalCount) Integer total,
            @Arg(ArgEnum.successCount) Integer success, @Arg(ArgEnum.failureCount) Integer failure,
            @Arg(ArgEnum.notAttemptedCount) Integer notAttempted);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void cancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void acceptAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey,
            @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void acceptCompleted(@Arg(ArgEnum.resultKey) String resultKey, @Arg(ArgEnum.totalCount) Integer total,
            Integer assignedCount, Integer unassignedCount);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void removeAttempted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.resultKey) String resultKey,
            @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void removeCompleted(@Arg(ArgEnum.resultKey) String resultKey, @Arg(ArgEnum.totalCount) Integer total);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void cancelCompleted(@Arg(ArgEnum.resultKey) String resultKey, @Arg(ArgEnum.totalCount) Integer total,
            @Arg(ArgEnum.successCount) Integer success, @Arg(ArgEnum.failureCount) Integer failure,
            @Arg(ArgEnum.notAttemptedCount) Integer notAttempted, String canceledCount);
}