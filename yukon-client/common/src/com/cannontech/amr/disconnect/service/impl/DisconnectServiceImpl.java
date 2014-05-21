package com.cannontech.amr.disconnect.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionContextId;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestUnsupported;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DisconnectServiceImpl implements DisconnectService {

    private Logger log = YukonLogManager.getLogger(DisconnectServiceImpl.class);
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private List<DisconnectStrategy> strategies;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DisconnectEventLogService disconnectEventLogService;
    @Autowired private MeterDao meterDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    

    private RecentResultsCache<DisconnectResult> resultsCache = new RecentResultsCache<>();

    @Override
    public DisconnectResult execute(DisconnectCommand command, DeviceCollection deviceCollection,
                                    final SimpleCallback<DisconnectResult> callback,
                                    final YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        final String resolvedCommand = accessor.getMessage(command);
        disconnectEventLogService.groupDisconnectAttempted(userContext.getYukonUser(), resolvedCommand);
        
        final DisconnectResult result = new DisconnectResult();
        result.setCommand(command);
        result.setKey(resultsCache.addResult(result));
        final CommandRequestExecution execution = createExecution(userContext.getYukonUser());
        result.setCommandRequestExecution(execution);

        final StoredDeviceGroup allDevicesGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup armedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup connectedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup disconnectedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup notConfiguredGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup canceledGroup = tempDeviceGroupService.createTempGroup();
        
        result.setAllDevicesCollection(deviceCollection);
        result.setArmedCollection(deviceGroupCollectionHelper.buildDeviceCollection(armedGroup));
        result.setConnectedCollection(deviceGroupCollectionHelper.buildDeviceCollection(connectedGroup));
        result.setDisconnectedCollection(deviceGroupCollectionHelper.buildDeviceCollection(disconnectedGroup));
        result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
        result.setNotConfiguredCollection(deviceGroupCollectionHelper.buildDeviceCollection(notConfiguredGroup));
        result.setFailedCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        result.setCanceledCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));

        DisconnectCallback disconnectCallback =
            new DisconnectCallback() {
                AtomicInteger pendingStrategies = new AtomicInteger(strategies.size());
                boolean canceled = false;

                @Override
                public void connected(SimpleDevice device, Instant timestamp) {
                    deviceGroupMemberEditorDao.addDevices(connectedGroup, device);
                    result.addTimestamp(device, timestamp);
                }

                @Override
                public void armed(SimpleDevice device, Instant timestamp) {
                    deviceGroupMemberEditorDao.addDevices(armedGroup, device);
                    result.addTimestamp(device, timestamp);
                }

                @Override
                public void disconnected(SimpleDevice device, Instant timestamp) {
                    deviceGroupMemberEditorDao.addDevices(disconnectedGroup, device);
                    result.addTimestamp(device, timestamp);
                }

                @Override
                public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
                    deviceGroupMemberEditorDao.addDevices(failedGroup, device);
                    result.addError(device, error);
                }

                @Override
                public void canceled(SimpleDevice device) {
                    deviceGroupMemberEditorDao.addDevices(canceledGroup, device);
                }

                @Override
                public void complete() {
                    if (log.isDebugEnabled()) {
                        log.debug("pendingStrategies=" + pendingStrategies.intValue());
                    }
                    int remaining = pendingStrategies.decrementAndGet();
                    if (remaining == 0) {
                        log.debug("All strategies complete");
                        if (canceled) {
                            log.debug("Command Canceled");
                            if (result.getCanceledCollection().getDeviceCount() > 0) {
                                saveUnsupported(result.getCanceledCollection().getDeviceList(), result
                                    .getCommandRequestExecution().getId(), CommandRequestUnsupportedType.CANCELED);
                                execution.setRequestCount(result.getCompletedCount());
                            }
                            completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.CANCELLED);
                           
                        } else {
                            log.debug("Command Complete");
                            completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.COMPLETE);
                        }
                        result.complete();
                        disconnectEventLogService.groupActionCompleted(userContext.getYukonUser(),
                                                                  resolvedCommand,
                                                                  result.getTotalCount(),
                                                                  result.getSuccessCount(),
                                                                  result.getFailedCount(),
                                                                  result.getNotAttemptedCount());
                        try {
                            callback.handle(result);
                        } catch (Exception e) {
                            log.warn("caught exception in complete", e);
                        }
                    }
                }

                @Override
                public boolean isComplete() {
                    return result.isComplete();
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.FAILED);
                    result.setExceptionReason(reason);
                }

                @Override
                public boolean isCanceled() {
                    return canceled;
                }

                @Override
                public void cancel() {
                    canceled = true;
                }

                @Override
                public void setCommandCompletionCallback(CommandCompletionCallback<CommandRequestDevice> commandCompletionCallback) {
                    result.setCommandCompletionCallback(commandCompletionCallback);
                }
            };

        result.setDisconnectCallback(disconnectCallback);
        List<SimpleDevice> allDevices = deviceCollection.getDeviceList();
        /*
         * Start with all the devices, for each strategy remove "not configured" and "valid"
         * devices. The remaining devices will be unsupported.
         */
        List<SimpleDevice> unsupportedDevices = deviceCollection.getDeviceList();
        List<SimpleDevice> notConfiguredDevices = new ArrayList<>();

        /*
         * Map holds valid devices for each strategy
         */
        Map<DisconnectStrategy, List<SimpleDevice>> validMeters = new HashMap<>();

        for (DisconnectStrategy strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(allDevices);
            notConfiguredDevices.addAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getValid());
            validMeters.put(strategy, filteredDevices.getValid());
        }
        if (log.isDebugEnabled()) {
            log.debug("execute: " + command);
            log.debug("CommandRequestExecutionId=" + execution.getId());
            log.debug("-----");
            log.debug("allDevices =" + allDevices);
            log.debug("notConfiguredDevices =" + notConfiguredDevices);
            log.debug("unsupportedDevices =" + unsupportedDevices);
            for (DisconnectStrategy strategy : strategies) {
                log.debug("validMeters =" + validMeters.get(strategy));
            }
            log.debug("-----");
        }
        
        deviceGroupMemberEditorDao.addDevices(allDevicesGroup, allDevices);
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);
        deviceGroupMemberEditorDao.addDevices(notConfiguredGroup, notConfiguredDevices);

        saveUnsupported(unsupportedDevices, result
            .getCommandRequestExecution().getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        saveUnsupported(notConfiguredDevices, result
            .getCommandRequestExecution().getId(), CommandRequestUnsupportedType.NOT_CONFIGURED);

        int requestCount = 0;
        for (DisconnectStrategy strategy : strategies) {
            List<SimpleDevice> meters = validMeters.get(strategy);
            if (meters.isEmpty()) {
                disconnectCallback.complete();
            } else {
                requestCount += meters.size();
                log(meters, resolvedCommand, userContext.getYukonUser());
                // send command to the valid devices
                strategy.execute(command, meters, disconnectCallback, execution, userContext);
            }
        }
        
        //update request count
        execution.setRequestCount(requestCount);
        if (log.isDebugEnabled()) {
            log.debug("updating request count =" + requestCount);
        }
        commandRequestExecutionDao.saveOrUpdate(execution);
        
        return result;
    }
    
    private void log(List<SimpleDevice> meters, String commandName, LiteYukonUser user) {
        Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
        for (YukonMeter meter : yukonMeters) {
            disconnectEventLogService.disconnectAttempted(user, commandName, meter.getName());
        }
    }

    private void saveUnsupported(List<SimpleDevice> devices, final int commandRequestExecutionId,
                                 final CommandRequestUnsupportedType type) {
        for (SimpleDevice device : devices) {
            CommandRequestUnsupported unsupported = new CommandRequestUnsupported();
            unsupported.setCommandRequestExecId(commandRequestExecutionId);
            unsupported.setDeviceId(device.getDeviceId());
            unsupported.setType(type);
            commandRequestExecutionResultDao.saveUnsupported(unsupported);
        }
    }

    private CommandRequestExecution createExecution(LiteYukonUser user) {
        CommandRequestExecutionContextId contextId =
            new CommandRequestExecutionContextId(nextValueHelper.getNextValue("CommandRequestExec"));
        CommandRequestExecution execution = new CommandRequestExecution();
        execution.setContextId(contextId.getId());
        execution.setStartTime(new Date());
        execution.setRequestCount(0);
        execution.setCommandRequestExecutionType(DeviceRequestType.GROUP_CONNECT_DISCONNECT);
        execution.setUserName(user.getUsername());
        execution.setCommandRequestType(CommandRequestType.DEVICE);
        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.STARTED);
        commandRequestExecutionDao.saveOrUpdate(execution);
        return execution;
    }

    @Override
    public DisconnectResult getResult(String key) {
        return resultsCache.getResult(key);
    }

    @Override
    public Iterable<DisconnectResult> getResults() {
        List<DisconnectResult> results = new ArrayList<>();
        results.addAll(resultsCache.getCompleted());
        results.addAll(resultsCache.getPending());
        return results;
    }

    @Override
    public void cancel(String key, YukonUserContext userContext, DisconnectCommand command) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        String commandName = accessor.getMessage(command).toLowerCase();
        disconnectEventLogService.groupCancelAttempted(userContext.getYukonUser(), commandName);
        
        DisconnectResult result = getResult(key);
        CommandRequestExecution execution = result.getCommandRequestExecution();
        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELING);
        commandRequestExecutionDao.saveOrUpdate(execution);
        result.getDisconnectCallback().cancel();
        for (DisconnectStrategy strategy : strategies) {
            strategy.cancel(result, userContext);
        }
    }

    private void completeCommandRequestExecutionRecord(CommandRequestExecution commandRequestExecution,
                                                       CommandRequestExecutionStatus executionStatus) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        if (commandRequestExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.FAILED) {
            commandRequestExecution.setStopTime(new Date());
            commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
            commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        }
    }
}
