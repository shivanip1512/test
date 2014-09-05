package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DemandResetEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetAttempted(@Arg(ArgEnum.username) LiteYukonUser user,
                                          @Arg(ArgEnum.deviceName) String deviceName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void collectionDemandResetAttempted(String collection, @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void verifyDemandResetAttempted(@Arg(ArgEnum.totalRequests) Integer total,
                                          @Arg(ArgEnum.notAttemptedRequests) Integer notAttempted,
                                          @Arg(ArgEnum.username) LiteYukonUser user);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void initDemandResetAttempted(@Arg(ArgEnum.totalRequests) Integer total,
                                         @Arg(ArgEnum.notAttemptedRequests) Integer notAttempted,
                                         @Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void cancelAttempted(@Arg(ArgEnum.username) LiteYukonUser user);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetCompletedResults(@Arg(ArgEnum.username) LiteYukonUser user,
                                            @Arg(ArgEnum.totalRequests) Integer total,
                                            @Arg(ArgEnum.successRequests) Integer success,
                                            @Arg(ArgEnum.failureRequests) Integer failure,
                                            @Arg(ArgEnum.notAttemptedRequests) Integer notAttempted
            );

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.demand.reset")
    public void demandResetCompleted(@Arg(ArgEnum.username) LiteYukonUser user);
}
