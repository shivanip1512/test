package com.cannontech.common.events.loggers;

import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface MeterProgrammingEventLogService {

	@YukonEventLog(category = "device.programming")
	public void initiateMeterProgramUploadInitiated(@Arg(ArgEnum.deviceName) String deviceName,
			@Arg(ArgEnum.username) LiteYukonUser yukonUser);
	
	@YukonEventLog(category = "device.programming")
	public void initiateMeterProgramUploadCompleted(@Arg(ArgEnum.deviceName) String deviceName, Integer successOrFail);

	@YukonEventLog(category = "device.programming")
	public void retrieveMeterProgrammingStatusInitiated(@Arg(ArgEnum.deviceName) String deviceName,
			@Arg(ArgEnum.username) LiteYukonUser yukonUser);
	
	@YukonEventLog(category = "device.programming")
	public void retrieveMeterProgrammingStatusCompleted(@Arg(ArgEnum.deviceName) String deviceName, Integer successOrFail);
	
	@YukonEventLog(category = "device.programming")
	public void cancelMeterProgramUploadInitiated(@Arg(ArgEnum.deviceName) String deviceName,
			@Arg(ArgEnum.username) LiteYukonUser yukonUser);
	
	@YukonEventLog(category = "device.programming")
	public void cancelMeterProgramUploadCompleted(@Arg(ArgEnum.deviceName) String deviceName, Integer successOrFail);
        
	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramUploadCancelled(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.statistics) String statistics, @Arg(ArgEnum.username) LiteYukonUser user,
			@Arg(ArgEnum.resultKey) String resultKey);

	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramUploadInitiated(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.totalCount) Integer numDevices, @Arg(ArgEnum.username) LiteYukonUser username,
			@Arg(ArgEnum.resultKey) String resultKey);

	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramUploadCompleted(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.statistics) String statistics, @Arg(ArgEnum.status) String creStatus,
			@Arg(ArgEnum.resultKey) String resultKey);

	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramStatusReadCancelled(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.statistics) String statistics, @Arg(ArgEnum.username) LiteYukonUser user,
			@Arg(ArgEnum.resultKey) String resultKey);

	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramStatusReadInitiated(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.totalCount) Integer numDevices, @Arg(ArgEnum.username) LiteYukonUser username,
			@Arg(ArgEnum.resultKey) String resultKey);

	@YukonEventLog(transactionality = ExecutorTransactionality.TRANSACTIONAL, category = "device.programming")
	public void meterProgramStatusReadCompleted(@Arg(ArgEnum.action) String action, @Arg(ArgEnum.input) String input,
			@Arg(ArgEnum.statistics) String statistics, @Arg(ArgEnum.status) String creStatus,
			@Arg(ArgEnum.resultKey) String resultKey);
}