package com.cannontech.common.events.loggers;

import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DatabaseMigrationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingExport(LiteYukonUser yukonUser, String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingValidation(LiteYukonUser yukonUser, String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingImport(LiteYukonUser yukonUser, String fileName);

}
