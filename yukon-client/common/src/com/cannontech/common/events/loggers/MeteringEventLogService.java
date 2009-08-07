package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MeteringEventLogService {
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="amr.meterReading")
    public void readNowPushedForReadingsWidget(LiteYukonUser user, long deviceId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.FORCED, category="amr.schedules")
    public void scheduleDeleted(LiteYukonUser user, String scheduleName);

}
