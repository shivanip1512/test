package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

/*
16. success and failure of specific device serial numbers and timestamps
17. use of the tool by Yukon username and timestamp, as well as the device serial number.
 */
public interface InventoryConfigEventLogService {
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void taskCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void taskDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.taskName) String taskName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void itemConfigSucceeded(@Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.inventoryId) int inventoryId);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void itemConfigError(@Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.inventoryId) int inventoryId,
            @Arg(ArgEnum.porterUserErrorDescription) String errorDescription,
            @Arg(ArgEnum.porterTechnicalErrorDescription) String porter,
            @Arg(ArgEnum.porterErrorCode) Integer errorCode,
            @Arg(ArgEnum.porterErrorCategory) String category);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="dr.inventoryConfig")
    public void itemConfigFailed(@Arg(ArgEnum.serialNumber) String serialNumber,
            @Arg(ArgEnum.inventoryId) int inventoryId);
}
