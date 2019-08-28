package com.cannontech.common.device.programming.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInput;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.WaitableCommandCompletionCallbackFactory;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.impl.WaitableCommandCompletionCallback;
import com.cannontech.common.device.commands.service.CommandExecutionService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramUploadCancelResult;
import com.cannontech.common.device.programming.service.MeterProgrammingService;
import com.cannontech.common.events.loggers.MeterProgrammingEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class MeterProgrammingServiceImpl implements MeterProgrammingService, CollectionActionCancellationService {
    private final static Logger log = YukonLogManager.getLogger(MeterProgrammingServiceImpl.class);
    
	@Autowired private CommandExecutionService commandRequestService;
	@Autowired private CollectionActionService collectionActionService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandExecutionService commandExecutionService;
    @Autowired private MeterProgrammingEventLogService eventLogService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private WaitableCommandCompletionCallbackFactory waitableFactory;
    @Autowired private MeterProgrammingDao meterProgrammingDao;
	
	@Override
	public boolean isCancellable(CollectionAction action) {
		return action == CollectionAction.METER_PROGRAM_UPLOAD_INITIATE || action == CollectionAction.METER_PROGRAM_STATUS_READ;
	}

	@Override
	public void cancel(int key, LiteYukonUser user) {
		 commandRequestService.cancel(key, user);
	}
	
	@Override
	public MeterProgramUploadCancelResult cancelMeterProgramUpload(SimpleDevice device, YukonUserContext context) {
		
		String command = "";

		MeterProgramUploadCancelResult result = new MeterProgramUploadCancelResult();
		CommandCompletionCallback<CommandRequestDevice> execCallback = new CommandCompletionCallback<CommandRequestDevice>() {
			MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

			@Override
			public void receivedLastResultString(CommandRequestDevice command, String value) {
				logCompleted(command.getDevice(), DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, true);
				result.setSuccess(true);
				meterProgrammingDao.unassignDeviceFromProgram(device.getDeviceId());
			}

			@Override
			public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
				logCompleted(command.getDevice(), DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, false);
				String errorText = accessor.getMessage(error.getDetail());
				result.setErrorText(errorText);
				log.error("{} Error:{}", command, errorText);
			}

			@Override
			public void processingExceptionOccurred(String reason) {
				log.error(reason);
				result.setErrorText(reason);
			}
		};

		WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = waitableFactory
				.createWaitable(execCallback);

		logInitiated(Lists.newArrayList(device), DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, context.getYukonUser());

		commandRequestService.execute(Lists.newArrayList(new CommandRequestDevice(command, device)), waitableCallback,
				DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, context.getYukonUser());
		try {
			waitableCallback.waitForCompletion();
		} catch (InterruptedException | TimeoutException e) {
			log.error(e);
		}
		return result;
	}
	

	@Override
	public int initiateMeterProgramUpload(DeviceCollection deviceCollection,
			SimpleCallback<CollectionActionResult> callback, String guid, YukonUserContext context) {

		String command = "";
		
		List<SimpleDevice> unsupportedDevices = new ArrayList<>(deviceCollection.getDeviceList());
		LinkedHashMap<String, String> input = null;
		try {
			MeterProgram program = meterProgrammingDao.getMeterProgram(guid);
			unsupportedDevices.removeIf(device -> device.getDeviceType() == program.getPaoType());
			input = getInput(program);
		} catch (NotFoundException e) {
			log.error(e);
		}

		CollectionActionResult result = collectionActionService.createResult(
				CollectionAction.METER_PROGRAM_UPLOAD_INITIATE, input, deviceCollection, CommandRequestType.DEVICE,
				DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE, context);

		collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, unsupportedDevices);
		List<SimpleDevice> supportedDevices = new ArrayList<>(deviceCollection.getDeviceList());
		supportedDevices.removeAll(unsupportedDevices);

		CommandCompletionCallback<CommandRequestDevice> execCallback = getExecutionCallback(callback, context, result);
		meterProgrammingDao.assignDevicesToProgram(guid, supportedDevices);
		execute(context, command, result, supportedDevices, execCallback);
		return result.getCacheKey();
	}
	
	@Override
	public int retrieveMeterProgrammingStatus(DeviceCollection deviceCollection, SimpleCallback<CollectionActionResult> callback,
			YukonUserContext context) {

		String command = "";
		
		CollectionActionResult result = collectionActionService.createResult(CollectionAction.METER_PROGRAM_STATUS_READ,
				null, deviceCollection, CommandRequestType.DEVICE, DeviceRequestType.METER_PROGRAM_STATUS_READ, context);

		CommandCompletionCallback<CommandRequestDevice> execCallback = getExecutionCallback(callback, context, result);
		execute(context, command, result, deviceCollection.getDeviceList(), execCallback);
		return result.getCacheKey();
	}

	private void execute(YukonUserContext context, String command, CollectionActionResult result,
			List<SimpleDevice> supportedDevices, CommandCompletionCallback<CommandRequestDevice> execCallback) {
		if (supportedDevices.isEmpty()) {
			execCallback.complete();
		} else {
			List<CommandRequestDevice> requests = supportedDevices.stream()
					.map(device -> new CommandRequestDevice(command, new SimpleDevice(device.getPaoIdentifier())))
					.collect(Collectors.toList());
			result.addCancellationCallback(
					new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback));
			result.getExecution().setRequestCount(requests.size());
			log.debug("updating request count =" + requests.size());
			commandRequestExecutionDao.saveOrUpdate(result.getExecution());
			logInitiated(supportedDevices, result.getExecution().getCommandRequestExecutionType(), context.getYukonUser()) ;
			commandExecutionService.execute(requests, execCallback, result.getExecution(), false,
					context.getYukonUser());
		}
	}
	
	/**
	 * Returns configuration name to be displayed on collection actions page
	 */
	private LinkedHashMap<String, String> getInput(MeterProgram program) {
		LinkedHashMap<String, String> inputs = new LinkedHashMap<>();
		inputs.put(CollectionActionInput.CONFIGURATION.name(), program.getName());
		return inputs;
	}
	
	private CommandCompletionCallback<CommandRequestDevice> getExecutionCallback(
			SimpleCallback<CollectionActionResult> callback, YukonUserContext context, CollectionActionResult result) {
		CommandCompletionCallback<CommandRequestDevice> execCallback = new CommandCompletionCallback<CommandRequestDevice>() {
			MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

			@Override
			public void receivedLastResultString(CommandRequestDevice command, String value) {
				logCompleted(command.getDevice(), result.getExecution().getCommandRequestExecutionType(), true);
				CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
						CollectionActionDetail.SUCCESS);
				detail.setLastValue(value);
				result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
			}

			@Override
			public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
				logCompleted(command.getDevice(), result.getExecution().getCommandRequestExecutionType(), false);
				CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(),
						CollectionActionDetail.FAILURE);
				detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
				result.addDeviceToGroup(CollectionActionDetail.FAILURE, command.getDevice(), detail);
			}

			@Override
			public void processingExceptionOccurred(String reason) {
				result.setExecutionExceptionText(reason);
				collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
			}

			@Override
			public void complete() {
				collectionActionService.updateResult(result,
						!result.isCanceled() ? CommandRequestExecutionStatus.COMPLETE
								: CommandRequestExecutionStatus.CANCELLED);
				try {
					callback.handle(result);
				} catch (Exception e) {
					log.error(e);
				}
			}
		};
		return execCallback;
	}
	
	/**
	 * Adds entry to event log
	 */
	private void logCompleted(SimpleDevice device, DeviceRequestType type, boolean isSuccessful) {
		int successOrFail = BooleanUtils.toInteger(isSuccessful);
		String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
		if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE) {
			eventLogService.retrieveMeterProgrammingStatusCompleted(deviceName, successOrFail);
		} else if (type == DeviceRequestType.METER_PROGRAM_STATUS_READ) {
			eventLogService.initiateMeterProgramUploadCompleted(deviceName, successOrFail);
		} else if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL) {
			eventLogService.cancelMeterProgramUploadCompleted(deviceName, successOrFail);
		}
	}

	/**
	 * Adds entry to event log
	 */
	private void logInitiated(List<SimpleDevice> devices, DeviceRequestType type, LiteYukonUser yukonUser) {
		for (SimpleDevice device : devices) {
			String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
			if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE) {
				eventLogService.initiateMeterProgramUploadInitiated(deviceName, yukonUser);
			} else if (type == DeviceRequestType.METER_PROGRAM_STATUS_READ) {
				eventLogService.retrieveMeterProgrammingStatusInitiated(deviceName, yukonUser);
			} else if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL) {
				eventLogService.cancelMeterProgramUploadInitiated(deviceName, yukonUser);
			}
		}
	}
}
