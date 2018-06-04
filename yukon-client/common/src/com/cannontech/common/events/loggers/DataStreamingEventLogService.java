package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface DataStreamingEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void configDataStreamingCancelled(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.statistics)String statistics,
                                             @Arg(ArgEnum.username) LiteYukonUser user,
                                             @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void configDataStreamingInitiated(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.totalCount)Integer numDevices,
                                             @Arg(ArgEnum.username)LiteYukonUser username,
                                             @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void configDataStreamingCompleted(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.statistics)String statistics,
                                             @Arg(ArgEnum.status)String creStatus,
                                             @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void removeDataStreamingCancelled(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.statistics)String statistics,
                                             @Arg(ArgEnum.username) LiteYukonUser user,
                                             @Arg(ArgEnum.resultKey) String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void removeDataStreamingInitiated(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.totalCount)Integer numDevices,
                                             @Arg(ArgEnum.username)LiteYukonUser username,
                                             @Arg(ArgEnum.resultKey)String resultKey);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "system.rfn.dataStreaming")
    public void removeDataStreamingCompleted(@Arg(ArgEnum.action)String action,
                                             @Arg(ArgEnum.input)String input,
                                             @Arg(ArgEnum.statistics)String statistics,
                                             @Arg(ArgEnum.status)String creStatus,
                                             @Arg(ArgEnum.resultKey)String resultKey);
}