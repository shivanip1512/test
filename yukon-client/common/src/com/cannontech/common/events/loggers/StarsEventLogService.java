package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface StarsEventLogService {

/* Energy Company */
    // Delete Energy Company
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.energyCompany")
    public void deleteEnergyCompanyAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                       @Arg(ArgEnum.energyCompanyName) String yukonEnergyCompany,
                                                       @Arg(ArgEnum.eventSource) EventSource source);
    
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
    public void addWarehouseAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                @Arg(ArgEnum.warehouseName) String warehouseName,
                                                @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void updateWarehouseAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                   @Arg(ArgEnum.warehouseName) String warehouseName,
                                                   @Arg(ArgEnum.eventSource) EventSource source);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.warehouse")
    public void deleteWarehouseAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                   @Arg(ArgEnum.warehouseName) String warehouseName,
                                                   @Arg(ArgEnum.eventSource) EventSource source);
    
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
    public void cancelCurrentOptOutsAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                              @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsByProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                            @Arg(ArgEnum.programName) String programName,
                                                            @Arg(ArgEnum.eventSource) EventSource source);

    // Cancel All Opt Out service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void cancelCurrentOptOuts(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void cancelCurrentOptOutsByProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                              @Arg(ArgEnum.programName) String programName);

    // Enabling/Disabling the Ability for Customers To Opt Out
    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                     @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void enablingOptOutUsageForTodayByProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                        @Arg(ArgEnum.programName) String programName,
                                                                        @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                      @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void disablingOptOutUsageForTodayByProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                         @Arg(ArgEnum.programName) String programName,
                                                                         @Arg(ArgEnum.eventSource) EventSource source);

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
    public void countTowardOptOutLimitTodayAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                     @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayByProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                   @Arg(ArgEnum.programName) String programName,
                                                                   @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                          @Arg(ArgEnum.eventSource) EventSource source);

    @YukonEventLog(transactionality = ExecutorTransactionality.ASYNCHRONOUS, category = "stars.optOutAdmin")
    public void doNotCountTowardOptOutLimitTodayByProgramAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                                                             @Arg(ArgEnum.programName) String programName,
                                                                             @Arg(ArgEnum.eventSource) EventSource source);

    // Counting/Not Counting Opt Outs service level
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitToday(@Arg(ArgEnum.username) LiteYukonUser user,
                                            boolean optOutsCount);

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "stars.optOutAdmin")
    public void countTowardOptOutLimitTodayForProgram(@Arg(ArgEnum.username) LiteYukonUser user,
                                                      @Arg(ArgEnum.programName) String programName,
                                                      boolean optOutsCount);

}
