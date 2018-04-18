package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface SmartNotificationEventLogService {
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "system.notifications")
    public void subscribe(@Arg(ArgEnum.username) LiteYukonUser user,
                          @Arg(ArgEnum.frequency) String frequency,
                          @Arg(ArgEnum.media) String media,
                          @Arg(ArgEnum.eventType) String eventType);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "system.notifications")
    public void update(@Arg(ArgEnum.username) LiteYukonUser user,
                       @Arg(ArgEnum.frequency) String frequency,
                       @Arg(ArgEnum.media) String media,
                       @Arg(ArgEnum.eventType) String eventType);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "system.notifications")
    public void unsubscribe(@Arg(ArgEnum.username) LiteYukonUser user,
                            @Arg(ArgEnum.eventType) String eventType);
    
    @YukonEventLog(category = "system.notifications")
    public void subscriptionsRemoved(@Arg(ArgEnum.username) LiteYukonUser user,
                                     @Arg(ArgEnum.eventType) String eventType,
                                     String identifier);
}
