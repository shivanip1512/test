package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface CommandScheduleEventLogService {

    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void scheduleCreated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.commandScheduleId) int commandScheduleId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void scheduleUpdated(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.commandScheduleId) int commandScheduleId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void scheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.commandScheduleId) int commandScheduleId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void scheduleEnabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.commandScheduleId) int commandScheduleId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void scheduleDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser, @Arg(ArgEnum.commandScheduleId) int commandScheduleId);
    
    @YukonEventLog(transactionality=ExecutorTransactionality.TRANSACTIONAL, category="system.commandSchedule")
    public void allSchedulesDisabled(@Arg(ArgEnum.username) LiteYukonUser yukonUser);
    
}