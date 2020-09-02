package com.cannontech.common.events.loggers;

import org.joda.time.LocalDate;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EcobeeEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "ecobee")
    public void reconciliationCompleted(int status, String syncObject,
                                        @Arg(ArgEnum.syncIssueType) String type,
                                        @Arg(ArgEnum.username) LiteYukonUser yukonUser, int statusValue);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "ecobee")
    public void dataDownloaded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.startDate) LocalDate startReportDate,
                               @Arg(ArgEnum.endDate) LocalDate endReportDate,
                               @Arg(ArgEnum.loadGroupIds) String loadGroupIds,
                               @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "ecobee")
    public void reconciliationStarted(@Arg(ArgEnum.totalCount) Integer total, @Arg(ArgEnum.username) LiteYukonUser yukonUser);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "ecobee")
    public void reconciliationResults(@Arg(ArgEnum.totalCount) Integer total, @Arg(ArgEnum.successCount) Integer success,
                                      @Arg(ArgEnum.failureCount) Integer failure, @Arg(ArgEnum.unsupportedCount) Integer unModified);
}
