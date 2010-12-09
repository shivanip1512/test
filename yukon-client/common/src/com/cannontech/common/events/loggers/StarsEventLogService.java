package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface StarsEventLogService {

/* Energy Company Settings */
    // Default Route Changed service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.energyCompanySettings")
    public void energyCompanyDefaultRouteChanged(@Arg(ArgEnum.username) LiteYukonUser user,
                                                 @Arg(ArgEnum.energyCompanyName) String energyCompanyName,
                                                 @Arg(ArgEnum.routeId) int oldRouteId,
                                                 @Arg(ArgEnum.routeId) int newRouteId);
    
/* System Opt Outs */
    // Cancel All Opt Out
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsByProgramAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                 @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsByProgramAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                            @Arg(ArgEnum.programName) String programName);

    // Cancel All Opt Out service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void cancelCurrentOptOuts(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsByProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                              @Arg(ArgEnum.programName) String programName);

    // Enabling/Disabling the Ability for Customers To Opt Out
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayByProgramAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                        @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayByProgramAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                   @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayByProgramAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                         @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayByProgramAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                    @Arg(ArgEnum.programName) String programName);

    // Enabling/Disabling the Ability for Customers To Opt Out service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void optOutUsageEnabledToday(@Arg(ArgEnum.username) LiteYukonUser user,
                                        boolean outOutsEnabled);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void optOutUsageEnabledTodayForProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                                  @Arg(ArgEnum.programName) String programName,
                                                  boolean outOutsEnabled);

    // Counting/Not Counting Opt Outs
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayByProgramAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                        @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayByProgramAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                   @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayByProgramAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                             @Arg(ArgEnum.programName) String programName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayByProgramAttemptedByApi(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                        @Arg(ArgEnum.programName) String programName);

    // Counting/Not Counting Opt Outs service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitToday(@Arg(ArgEnum.username) LiteYukonUser user,
                                            boolean optOutsCount);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayForProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                                      @Arg(ArgEnum.programName) String programName,
                                                      boolean optOutsCount);

}
