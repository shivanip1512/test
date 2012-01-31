package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface StarsEventLogService {

/* Energy Company */
    // Delete Energy Company
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.energyCompany")
    public void deleteEnergyCompanyAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                       @Arg(ArgEnum.energyCompanyName) String yukonEnergyCompany);
    
    // Delete Energy Company service level
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.energyCompany")
    public void deleteEnergyCompany(@Arg(ArgEnum.username) LiteYukonUser user,
                                     @Arg(ArgEnum.energyCompanyName) String yukonEnergyCompany);
    
/* Energy Company Settings */
    // Default Route Changed service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.energyCompanySettings")
    public void energyCompanyDefaultRouteChanged(@Arg(ArgEnum.username) LiteYukonUser user,
                                                 @Arg(ArgEnum.energyCompanyName) String energyCompanyName,
                                                 @Arg(ArgEnum.routeId) int oldRouteId,
                                                 @Arg(ArgEnum.routeId) int newRouteId);

    // Warehouse
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void addWarehouseAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                @Arg(ArgEnum.warehouseName) String warehouseName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void updateWarehouseAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                   @Arg(ArgEnum.warehouseName) String warehouseName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void deleteWarehouseAttemptedByOperator(@Arg(ArgEnum.username) LiteYukonUser user,
                                                   @Arg(ArgEnum.warehouseName) String warehouseName);
    
    // Warehouse service level
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void addWarehouse(@Arg(ArgEnum.warehouseName) String warehouseName);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void updateWarehouse(@Arg(ArgEnum.warehouseName) String warehouseName);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void deleteWarehouse(@Arg(ArgEnum.warehouseName) String warehouseName);
    
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
                                        boolean outOutsEnabled,
                                        boolean communicationsEnabled);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void optOutUsageEnabledTodayForProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                                  @Arg(ArgEnum.programName) String programName,
                                                  boolean optOutsEnabled,
                                                  boolean communicationsEnabled);

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
