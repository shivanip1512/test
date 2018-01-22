package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PointEventLogService {
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataDeleted(@Arg(ArgEnum.deviceName) String deviceName, String pointName, String value,
            Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataUpdated(@Arg(ArgEnum.deviceName) String deviceName, String pointName, String oldValue,
            String newValue, Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataAdded(@Arg(ArgEnum.deviceName) String deviceName, String pointName, String value,
            Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

}
