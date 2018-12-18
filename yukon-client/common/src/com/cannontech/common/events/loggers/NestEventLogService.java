package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;

public interface NestEventLogService {
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void syncStart(@Arg(ArgEnum.syncId) int syncId, 
                          @Arg(ArgEnum.startTime) Instant startTime);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void syncResults(@Arg(ArgEnum.syncId) int syncId, 
                            @Arg(ArgEnum.startTime) Instant startTime, 
                            @Arg(ArgEnum.stopTime) Instant stopTime, 
                            int autoCount,
                            int manualCount);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void sendStartEvent(@Arg(ArgEnum.startTime) String string, 
                          @Arg(ArgEnum.duration) String duration, 
                          @Arg(ArgEnum.type) String type);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void responseStartEvent(@Arg(ArgEnum.startTime) Instant start, 
                                 @Arg(ArgEnum.stopTime) Instant stop,
                                 @Arg(ArgEnum.duration) String duration,
                                 @Arg(ArgEnum.type) String type,
                                 @Arg(ArgEnum.status) String response);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void sendCancelEvent(@Arg(ArgEnum.key) String key, 
                                @Arg(ArgEnum.group) String group, 
                                @Arg(ArgEnum.startTime) Instant startTime);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void responseCancelEvent(@Arg(ArgEnum.key) String key, 
                                    @Arg(ArgEnum.group) String group, 
                                    @Arg(ArgEnum.startTime) Instant startTime,
                                    @Arg(ArgEnum.cancelTime) Instant cancelRequestTime, 
                                    @Arg(ArgEnum.status) boolean success);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void sendStopEvent(@Arg(ArgEnum.key) String key, 
                              @Arg(ArgEnum.group) String group, 
                              @Arg(ArgEnum.startTime) Instant startTime);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void responseStopEvent(@Arg(ArgEnum.key) String key, 
                                  @Arg(ArgEnum.group) String group, 
                                  @Arg(ArgEnum.startTime) Instant startTime, 
                                  @Arg(ArgEnum.stopTime) Instant stopTime,
                                  @Arg(ArgEnum.status) boolean success);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void sendUpdateEnrollment(@Arg(ArgEnum.customerId) String customerId, 
                                     @Arg(ArgEnum.group) String groupId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void responseUpdateEnrollment(@Arg(ArgEnum.customerId) String customerId, 
                                         @Arg(ArgEnum.group) String groupId,
                                         @Arg(ArgEnum.status) String enrollmentState);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void sendRetrieveCustomers(@Arg(ArgEnum.state) String state);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "dr.nest")
    public void responseRetrieveCustomers(@Arg(ArgEnum.totalCount) int size);
    
}
