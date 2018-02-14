package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;

public interface PointEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataDeleted(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String value, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataUpdated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String oldValue, String newValue, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point.data")
    public void pointDataAdded(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            String value, @Arg(ArgEnum.pointDate) Date timestamp, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointCreated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointUpdated(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "endpoint.point")
    public void pointDeleted(@Arg(ArgEnum.deviceName) String deviceName, @Arg(ArgEnum.pointName) String pointName,
            @Arg(ArgEnum.pointType) PointType pointType, @Arg(ArgEnum.pointOffset) int pointOffset, @Arg(ArgEnum.username) LiteYukonUser user);

}
