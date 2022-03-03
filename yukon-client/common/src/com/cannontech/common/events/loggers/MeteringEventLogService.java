package com.cannontech.common.events.loggers;

import org.joda.time.Instant;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MeteringEventLogService {

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.meterReading")
    public void readNowPushedForReadingsWidget(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.meterNumber) String meterNumber);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void scheduleDeleted(@Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.scheduleName) String scheduleName);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void jobStarted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, @Arg(ArgEnum.deviceGroup) String deviceGroup, int retry,
            @Arg(ArgEnum.username) LiteYukonUser user, @Arg(ArgEnum.jobId) int jobId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void jobCompleted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, @Arg(ArgEnum.jobId) int jobId,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readAttempted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, Instant start,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readStarted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, 
            @Arg(ArgEnum.startDate) Instant start,
            Instant timeout, int commands,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readCompleted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readCancelled(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, int tryNumber, @Arg(ArgEnum.username) LiteYukonUser user,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId,
            @Arg(ArgEnum.commandRequestExecutionId) int executionId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readFailed(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            String reason,
            @Arg(ArgEnum.scheduleName) String scheduleName, int tryNumber,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId,
            @Arg(ArgEnum.commandRequestExecutionId) int executionId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void readTimeout(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, int tryNumber,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId,
            @Arg(ArgEnum.commandRequestExecutionId) int executionId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void tryStarted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, int tryNumber, int commands,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId,
            @Arg(ArgEnum.commandRequestExecutionId) int executionId);

    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category = "amr.schedules")
    public void tryCompleted(@Arg(ArgEnum.deviceRequestType) String deviceRequestType,
            @Arg(ArgEnum.scheduleName) String scheduleName, int tryNumber, int commands,
            @Arg(ArgEnum.commandRequestExecutionContextId) int contextId,
            @Arg(ArgEnum.commandRequestExecutionId) int executionId);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category="amr.meters")
    public void meterCreated(@Arg(ArgEnum.deviceName) String deviceName,
                             @Arg(ArgEnum.meterNumber) String meterNumber,
                             @Arg(ArgEnum.serialNumber) String serialNumberOrAddress,
                             @Arg(ArgEnum.paoType) PaoType paoType,
                             @Arg(ArgEnum.username) String username);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category="amr.meters")
    public void meterEdited(@Arg(ArgEnum.deviceName) String deviceName,
                            @Arg(ArgEnum.meterNumber) String meterNumber,
                            @Arg(ArgEnum.username) String username, 
                            @Arg(ArgEnum.meterDescription) YukonMeter originalMeter, 
                            @Arg(ArgEnum.meterDescription) YukonMeter updatedMeter);
    
    @YukonEventLog(transactionality = ExecutorTransactionality.FORCED, category="amr.meters")
    public void meterDeleted(@Arg(ArgEnum.deviceName) String deviceName,
                             @Arg(ArgEnum.meterNumber) String meterNumber,
                             @Arg(ArgEnum.username) String username);
}
