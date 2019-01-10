package com.cannontech.common.events.loggers;

import java.util.Date;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface ToolsEventLogService {
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportFormatCreated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportFormatUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportFormatDeleted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportFormatCopyAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportScheduleCreated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.name) String formatName, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportScheduleUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.name) String formatName, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.dataExport")
    public void dataExportScheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptStarted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.startDate) Date startDate, @Arg(ArgEnum.endDate) Date endDate);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptStopped(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.endDate) Date endDate);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptEnabled(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.state) String state);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptDeleted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.macsScripts")
    public void macsScriptCreated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.groupRead")
    public void groupRequestByAttributeScheduleCreated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.name) String name, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.groupRead")
    public void groupRequestByCommandScheduleCreated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.name) String name, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.groupRead")
    public void groupRequestByAttributeScheduleUpdated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.name) String name, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.groupRead")
    public void groupRequestByCommandScheduleUpdated(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.name) String name, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.groupRead")
    public void groupRequestScheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.billing")
    public void billingFormatCreated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.name) String formatName, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.billing")
    public void billingFormatUpdated(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name,
            @Arg(ArgEnum.name) String formatName, @Arg(ArgEnum.cron) String cron);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.tools.billing")
    public void billingFormatDeleted(@Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.name) String name);
    

}