package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResetEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetAttempted(Integer toResetCount,
                                            Integer unsupportedCount,
                                            Integer toVerifyCount,
                                            Integer unsupportedVerifyCount,
                                            @Arg(ArgEnum.resultKey) String resultKey,
                                            @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetToDeviceInitiated(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.deviceName) String deviceName,
                                          @Arg(ArgEnum.deviceRequestType) String deviceRequestType);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void cancelInitiated(@Arg(ArgEnum.username) LiteYukonUser user,
                                @Arg(ArgEnum.resultKey) String resultKey);

    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetCompletedResults(@Arg(ArgEnum.resultKey) String resultKey,
                                            @Arg(ArgEnum.totalCount) Integer total,
                                            @Arg(ArgEnum.successCount) Integer success,
                                            @Arg(ArgEnum.failureCount) Integer failure,
                                            @Arg(ArgEnum.notAttemptedCount) Integer notAttempted);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetInitiated(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.totalCount)Integer numDevices,
                                     @Arg(ArgEnum.username)LiteYukonUser username,
                                     @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetCompleted(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.statistics)String statistics,
                                     @Arg(ArgEnum.status)String creStatus,
                                     @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetCancelled(@Arg(ArgEnum.action)String action,
                                     @Arg(ArgEnum.input)String input,
                                     @Arg(ArgEnum.statistics)String statistics,
                                     @Arg(ArgEnum.username) LiteYukonUser user,
                                     @Arg(ArgEnum.resultKey) String resultKey);
    
    /**
     * Do not use this method, only here for legacy demand reset functionality (multispeak and EIM callbacks)
     */
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetByApiCompleted(@Arg(ArgEnum.username)LiteYukonUser user);
}
