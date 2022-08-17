package com.cannontech.common.events.loggers;

import org.joda.time.LocalDate;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EcobeeEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="ecobee")
    public void syncIssueFixed(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.syncIssueType) String type,
                               @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="ecobee")
    public void allSyncIssuesFixed(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                                   @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="ecobee")
    public void dataDownloaded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.startDate) LocalDate startReportDate,
                               @Arg(ArgEnum.endDate) LocalDate endReportDate,
                               @Arg(ArgEnum.loadGroupIds) String loadGroupIds,
                               @Arg(ArgEnum.eventSource) EventSource source);
}
