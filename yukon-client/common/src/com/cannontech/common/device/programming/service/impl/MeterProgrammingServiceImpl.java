package com.cannontech.common.device.programming.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.jms.ConnectionFactory;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
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
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest.Source;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramCommandResult;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.device.programming.service.MeterProgrammingService;
import com.cannontech.common.events.loggers.MeterProgrammingEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.util.jms.ThriftRequestTemplate;
import com.cannontech.common.util.jms.api.JmsApiDirectory;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.messaging.serialization.thrift.serializer.MeterProgramStatusArchiveRequestSerializer;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

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
    @Autowired private RfnDeviceDao rfnDeviceDao;

    private final static String baseKey = "yukon.web.modules.amr.meterProgramming.";
    private ThriftRequestTemplate<MeterProgramStatusArchiveRequest> thriftMessenger;
    private final Map<DeviceRequestType, String> commands = new HashMap<>();
    {
        commands.put(DeviceRequestType.METER_PROGRAM_STATUS_READ, "");
        commands.put(DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, "");
        commands.put(DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE, ""); 
    }
    private static final Set<ProgrammingStatus> failures = Sets.newHashSet(ProgrammingStatus.CANCELED,
                                                                          ProgrammingStatus.FAILED,
                                                                          ProgrammingStatus.MISMATCHED);
    private static final Set<ProgrammingStatus> inProgress = Sets.newHashSet(ProgrammingStatus.CONFIRMING,
                                                                          ProgrammingStatus.INITIATING,
                                                                          ProgrammingStatus.UPLOADING);

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.METER_PROGRAM_UPLOAD_INITIATE || action == CollectionAction.METER_PROGRAM_STATUS_READ;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        commandRequestService.cancel(key, user);
    }

    @Override
    public MeterProgramCommandResult retrieveMeterProgrammingStatus(SimpleDevice device, YukonUserContext context) {
        return sendCommandToPorter(device, context, DeviceRequestType.METER_PROGRAM_STATUS_READ);
    }
    
    @Override
    public MeterProgramCommandResult cancelMeterProgramUpload(SimpleDevice device, YukonUserContext context, UUID assignedGuid) {

        MeterProgramStatus status = meterProgrammingDao.getMeterProgramStatus(device.getDeviceId());
        MeterProgram program = meterProgrammingDao.getProgramByDeviceId(device.getDeviceId());

        if (program != null && program.getGuid().equals(assignedGuid) && status != null && inProgress.contains(status.getStatus())) {
            return sendCommandToPorter(device, context, DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL);
        }
        return createFailureResult(DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL, device, context);
    }

    @Override
    public MeterProgramCommandResult reinitiateMeterProgramUpload(SimpleDevice device, YukonUserContext context, UUID assignedGuid) {
                
        MeterProgramStatus status = meterProgrammingDao.getMeterProgramStatus(device.getDeviceId());
        MeterProgram program = meterProgrammingDao.getProgramByDeviceId(device.getDeviceId());

        if (program != null && program.getGuid().equals(assignedGuid) && status != null && failures.contains(status.getStatus())) {
            return sendCommandToPorter(device, context, DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE);
        }
        return createFailureResult(DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE, device, context);
    }
    
    private MeterProgramCommandResult createFailureResult(DeviceRequestType type, SimpleDevice device, YukonUserContext context) {
        logCompleted(device, type, false, context.getYukonUser());
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        return new MeterProgramCommandResult(accessor.getMessage(baseKey + "summary.programDoesNotMatch"));
    }
      
    @Override
    public MeterProgramCommandResult acceptMeterProgrammingStatus(SimpleDevice device, YukonUserContext context, UUID reportedGuid) {
        // unknown program
        if (!meterProgrammingDao.hasMeterProgram(reportedGuid)) {
            meterProgrammingDao.unassignDeviceFromProgram(device.getDeviceId());
            logCompleted(device, null, true, context.getYukonUser());
            return new MeterProgramCommandResult(true);
        }

        MeterProgramStatus status = meterProgrammingDao.getMeterProgramStatus(device.getDeviceId());

        if (status != null && failures.contains(status.getStatus())) {
            // Yukon program
            meterProgrammingDao.assignDevicesToProgram(reportedGuid, Lists.newArrayList(device));
            status.setLastUpdate(new Instant());
            status.setStatus(ProgrammingStatus.IDLE);
            meterProgrammingDao.updateMeterProgramStatus(status);
            logCompleted(device, null, true, context.getYukonUser());
            return new MeterProgramCommandResult(true);
        }
        return createFailureResult(null, device, context);
    }

    private MeterProgramCommandResult sendCommandToPorter(SimpleDevice device, YukonUserContext context, DeviceRequestType deviceRequestType) {
        MeterProgramCommandResult result = new MeterProgramCommandResult();
        var execCallback = new CommandCompletionCallback<CommandRequestDevice>() {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                logCompleted(command.getDevice(), deviceRequestType, true, context.getYukonUser());
                result.setSuccess(true);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                logCompleted(command.getDevice(), deviceRequestType, false, context.getYukonUser());
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

        WaitableCommandCompletionCallback<CommandRequestDevice> waitableCallback = waitableFactory.createWaitable(execCallback);

        logInitiated(Lists.newArrayList(device), deviceRequestType, context.getYukonUser());

        String command = commands.get(deviceRequestType);
        commandRequestService.execute(Lists.newArrayList(new CommandRequestDevice(command, device)),
                                      waitableCallback,
                                      deviceRequestType,
                                      context.getYukonUser());
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException | TimeoutException e) {
            log.error(e);
        }
        return result;
    }

    @Override
    public int initiateMeterProgramUpload(DeviceCollection deviceCollection, UUID guid, YukonUserContext context) {

        String command = commands.get(DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE);

        List<SimpleDevice> unsupportedDevices = new ArrayList<>(deviceCollection.getDeviceList());
        LinkedHashMap<String, String> input = null;
        try {
            MeterProgram program = meterProgrammingDao.getMeterProgram(guid);
            unsupportedDevices.removeIf(device -> device.getDeviceType() == program.getPaoType());
            input = getInput(program);
        } catch (NotFoundException e) {
            log.error(e);
        }

        unsupportedDevices.addAll(meterProgrammingDao.getMetersWithOldFirmware(deviceCollection.getDeviceList()));
        unsupportedDevices.addAll(meterProgrammingDao.getMetersWithoutProgramStatus(deviceCollection.getDeviceList()));
        List<SimpleDevice> alreadyConfigured = meterProgrammingDao.getAlreadyProgrammedMeters(deviceCollection.getDeviceList(), guid);
        
        CollectionActionResult result = collectionActionService.createResult(CollectionAction.METER_PROGRAM_UPLOAD_INITIATE,
                                                                             input,
                                                                             deviceCollection,
                                                                             CommandRequestType.DEVICE,
                                                                             DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE,
                                                                             context);

        collectionActionService.addUnsupportedToResult(CollectionActionDetail.UNSUPPORTED, result, unsupportedDevices);
        collectionActionService.addUnsupportedToResult(CollectionActionDetail.ALREADY_CONFIGURED, result, alreadyConfigured);
        
        List<SimpleDevice> supportedDevices = new ArrayList<>(deviceCollection.getDeviceList());
        supportedDevices.removeAll(unsupportedDevices);
        supportedDevices.removeAll(alreadyConfigured);

        CommandCompletionCallback<CommandRequestDevice> execCallback = getExecutionCallback(context, result);
        meterProgrammingDao.assignDevicesToProgram(guid, supportedDevices);
        archiveProgramStatus(supportedDevices, guid);
        execute(context, command, result, supportedDevices, execCallback);
        return result.getCacheKey();
    }

    /**
     * Sends status update message to SM to update MeterProgramStatus table
     */
    private void archiveProgramStatus(List<SimpleDevice> supportedDevices, UUID guid) {
        Map<? extends YukonPao, RfnIdentifier> meterIdentifiersByPao = rfnDeviceDao.getRfnIdentifiersByPao(supportedDevices);
        supportedDevices.forEach(device -> {
            MeterProgramStatusArchiveRequest request = new MeterProgramStatusArchiveRequest();
            request.setError(DeviceError.SUCCESS);
            request.setSource(Source.WS_COLLECTION_ACTION);
            request.setRfnIdentifier(meterIdentifiersByPao.get(device));
            request.setStatus(ProgrammingStatus.INITIATING);
            request.setTimeStamp(System.currentTimeMillis());
            log.debug("Sending {} on queue {}", request, thriftMessenger.getRequestQueueName());
         
            thriftMessenger.send(request);
        });
    }

    @Override
    public int retrieveMeterProgrammingStatus(DeviceCollection deviceCollection, YukonUserContext context) {

        String command = commands.get(DeviceRequestType.METER_PROGRAM_STATUS_READ);

        CollectionActionResult result = collectionActionService.createResult(CollectionAction.METER_PROGRAM_STATUS_READ,
                                                                             null,
                                                                             deviceCollection,
                                                                             CommandRequestType.DEVICE,
                                                                             DeviceRequestType.METER_PROGRAM_STATUS_READ,
                                                                             context);

        CommandCompletionCallback<CommandRequestDevice> execCallback = getExecutionCallback(context, result);
        execute(context, command, result, deviceCollection.getDeviceList(), execCallback);
        return result.getCacheKey();
    }

    private void execute(YukonUserContext context, String command, CollectionActionResult result, List<SimpleDevice> supportedDevices,
            CommandCompletionCallback<CommandRequestDevice> execCallback) {
        if (supportedDevices.isEmpty()) {
            execCallback.complete();
        } else {
            List<CommandRequestDevice> requests = supportedDevices.stream()
                                                                  .map(device -> new CommandRequestDevice(command,
                                                                                                          new SimpleDevice(device.getPaoIdentifier())))
                                                                  .collect(Collectors.toList());
            result.addCancellationCallback(new CollectionActionCancellationCallback(StrategyType.PORTER, null, execCallback));
            result.getExecution().setRequestCount(requests.size());
            log.debug("updating request count =" + requests.size());
            commandRequestExecutionDao.saveOrUpdate(result.getExecution());
            logInitiated(supportedDevices, result.getExecution().getCommandRequestExecutionType(), context.getYukonUser());
            commandExecutionService.execute(requests, execCallback, result.getExecution(), false, context.getYukonUser());
        }
    }

    /**
     * Returns configuration name to be displayed on collection actions page
     */
    private LinkedHashMap<String, String> getInput(MeterProgram program) {
        LinkedHashMap<String, String> inputs = new LinkedHashMap<>();
        inputs.put(CollectionActionInput.METER_PROGRAM.name(), program.getName());
        return inputs;
    }

    private CommandCompletionCallback<CommandRequestDevice> getExecutionCallback(YukonUserContext context, CollectionActionResult result) {
        var execCallback = new CommandCompletionCallback<CommandRequestDevice>() {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);

            @Override
            public void receivedLastResultString(CommandRequestDevice command, String value) {
                logCompleted(command.getDevice(), result.getExecution().getCommandRequestExecutionType(), true, context.getYukonUser());
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.SUCCESS);
                detail.setLastValue(value);
                result.addDeviceToGroup(CollectionActionDetail.SUCCESS, command.getDevice(), detail);
            }

            @Override
            public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                logCompleted(command.getDevice(), result.getExecution().getCommandRequestExecutionType(), false, context.getYukonUser());
                CollectionActionLogDetail detail = new CollectionActionLogDetail(command.getDevice(), CollectionActionDetail.FAILURE);
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
            }
        };
        return execCallback;
    }

    /**
     * Adds entry to event log
     */
    private void logCompleted(SimpleDevice device, DeviceRequestType type, boolean isSuccessful, LiteYukonUser yukonUser) {
        int successOrFail = BooleanUtils.toInteger(isSuccessful);
        String deviceName = dbCache.getAllPaosMap().get(device.getDeviceId()).getPaoName();
        if(type == null) {
            eventLogService.acceptMeterProgramCompleted(deviceName, successOrFail, yukonUser);
        } else {
            if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_INITIATE) {
                eventLogService.initiateMeterProgramUploadCompleted(deviceName, successOrFail, yukonUser);
            } else if (type == DeviceRequestType.METER_PROGRAM_STATUS_READ) { 
                eventLogService.retrieveMeterProgrammingStatusCompleted(deviceName, successOrFail, yukonUser);
            } else if (type == DeviceRequestType.METER_PROGRAM_UPLOAD_CANCEL) {
                eventLogService.cancelMeterProgramUploadCompleted(deviceName, successOrFail, yukonUser);
            }
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

    @Autowired
    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        thriftMessenger = new ThriftRequestTemplate<>(connectionFactory, JmsApiDirectory.METER_PROGRAM_STATUS_ARCHIVE.getQueue().getName(),
                new MeterProgramStatusArchiveRequestSerializer());
    }
}
