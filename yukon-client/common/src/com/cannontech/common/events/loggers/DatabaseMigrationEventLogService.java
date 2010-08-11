package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DatabaseMigrationEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingExport(@Arg(ArgEnum.username) LiteYukonUser yukonUser,
                               @Arg(ArgEnum.fileName) String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingValidation(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                                   @Arg(ArgEnum.fileName) String fileName);

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.databaseMigration")
    public void startingImport(@Arg(ArgEnum.username) LiteYukonUser yukonUser, 
                               @Arg(ArgEnum.fileName) String fileName);

}
