package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface InventoryConfigEventLogService {
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void taskCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void taskDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void itemConfigSucceeded(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.inventoryId) int inventoryId,
            @Arg(ArgEnum.commandRequestExecutionIdentifier) int commandRequestExecutionIdentifier);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void itemConfigFailed(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
            @Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.inventoryId) int inventoryId,
            @Arg(ArgEnum.commandRequestExecutionIdentifier) int commandRequestExecutionIdentifier);
}
