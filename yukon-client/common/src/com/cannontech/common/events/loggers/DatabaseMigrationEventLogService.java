package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DatabaseMigrationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingExport(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(EventLogArgEnum.fileName) String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingValidation(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(EventLogArgEnum.fileName) String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingImport(@Arg(EventLogArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(EventLogArgEnum.fileName) String fileName);

}
