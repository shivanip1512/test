package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DeviceReconfigEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.deviceReconfig")
    public void taskCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.deviceReconfig")
    public void taskDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);
    
}