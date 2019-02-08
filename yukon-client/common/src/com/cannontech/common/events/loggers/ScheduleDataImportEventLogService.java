package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ScheduleDataImportEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "scheduledDataImportDetail")
    public void scheduleCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.scheduleName) String ScheduleName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "scheduledDataImportDetail")
    public void scheduleUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.scheduleName) String ScheduleName);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "scheduledDataImportDetail")
    public void scheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.scheduleName) String ScheduleName);

}
