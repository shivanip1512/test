package com.cannontech.common.smartNotification.service.impl;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.loggers.ArgEnum;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface SmartNotificationEventLogService {
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "smart.notifications")
    public void subscribe(@Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "smart.notifications")
    public void update(@Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "smart.notifications")
    public void unsubscribe(@Arg(ArgEnum.username) LiteYukonUser user);
}
