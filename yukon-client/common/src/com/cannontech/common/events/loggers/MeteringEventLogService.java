package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MeteringEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="amr.meterReading")
    public void readNowPushedForReadingsWidget(@Arg(ArgEnum.username) LiteYukonUser user, 
                                               @Arg(ArgEnum.paoId) long paoId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="amr.schedules")
    public void scheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser user, 
                                @Arg(ArgEnum.scheduleName) String scheduleName);

}
