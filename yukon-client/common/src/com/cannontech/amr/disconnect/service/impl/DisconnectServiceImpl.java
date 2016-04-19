package com.cannontech.amr.disconnect.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
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
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

public class DisconnectServiceImpl implements DisconnectService {

    private final Logger log = YukonLogManager.getLogger(DisconnectServiceImpl.class);
    
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
    
    private final RecentResultsCache<DisconnectResult> resultsCache = new RecentResultsCache<>();

    @Override
    public DisconnectResult execute(final DisconnectCommand command, DeviceCollection deviceCollection,
                                    final SimpleCallback<DisconnectResult> callback,
                                    final YukonUserContext userContext) {
        disconnectEventLogService.groupDisconnectAttempted(userContext.getYukonUser(), command);
        
        
        final DisconnectResult result = new DisconnectResult();
        result.setCommand(command);
        result.setKey(resultsCache.addResult(result));
        final CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.GROUP_CONNECT_DISCONNECT,
                                                              0,
                                                              userContext.getYukonUser());
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
                                                                       command,
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
        Map<DisconnectStrategy, Set<SimpleDevice>> validMetersByStrategy = new HashMap<>();

        for (DisconnectStrategy strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(allDevices);
            notConfiguredDevices.addAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getValid());
            validMetersByStrategy.put(strategy, filteredDevices.getValid());
        }
        if (log.isDebugEnabled()) {
            log.debug("execute: " + command);
            log.debug("CommandRequestExecutionId=" + execution.getId());
            log.debug("-----");
            log.debug("allDevices =" + allDevices);
            log.debug("notConfiguredDevices =" + notConfiguredDevices);
            log.debug("unsupportedDevices =" + unsupportedDevices);
            for (DisconnectStrategy strategy : strategies) {
                log.debug("validMeters =" + validMetersByStrategy.get(strategy));
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
            Set<SimpleDevice> meters = validMetersByStrategy.get(strategy);
            if (meters.isEmpty()) {
                disconnectCallback.complete();
            } else {
                requestCount += meters.size();

                Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
                for (YukonMeter meter : yukonMeters) {
                    disconnectEventLogService.disconnectInitiated(userContext.getYukonUser(), command, meter.getName());
                }

                // send command to the valid devices
                CommandCompletionCallback<CommandRequestDevice> completionCallback =
                    strategy.execute(command, meters, disconnectCallback, execution, userContext);
                // this callback is needed for cancellation
                result.setCommandCompletionCallback(completionCallback);
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
    
    @Override
    public DisconnectMeterResult execute(DisconnectCommand command, final DeviceRequestType type, YukonMeter meter,
                                         final YukonUserContext userContext) {

        disconnectEventLogService.disconnectAttempted(userContext.getYukonUser(), command, meter.getName());
        
        Set<SimpleDevice> allDevices = Sets.newHashSet(new SimpleDevice(meter));
        if (!supportsDisconnect(allDevices)) {
            throw new UnsupportedOperationException("Meter:" + meter + " doesn't support connect/disconnect");
        }
        
        final CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              type,
                                                              0,
                                                              userContext.getYukonUser());
        final DisconnectMeterResult result = new DisconnectMeterResult(meter, command);
        
        if (log.isDebugEnabled()) {
            log.debug("Device: " + meter);
            log.debug("Execute: " + command);
            log.debug("CommandRequestExecutionId=" + execution.getId());
            log.debug("-----");
        }
        
        SingleMeterDisconnectCallback callback =
            new SingleMeterDisconnectCallback(result, execution, type, userContext.getYukonUser());
        for (DisconnectStrategy strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(allDevices);
            if(!filteredDevices.getValid().isEmpty()){

                log.debug("validMeters =" + filteredDevices.getValid());
                disconnectEventLogService.disconnectInitiated(userContext.getYukonUser(),
                                                              command,
                                                              meter.getName()); // since there is only 1 object in this method, if we made it here we can assume filteredDevices == meter

                strategy.execute(command, filteredDevices.getValid(), callback, execution, userContext);
            }
        } 
        
        try {
            callback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */ }
        
        return result;
    }
    
    
    class SingleMeterDisconnectCallback implements DisconnectCallback {

        private final CountDownLatch completeLatch = new CountDownLatch(1);
        private boolean isComplete = false;
        DisconnectMeterResult result;
        CommandRequestExecution execution;
        DeviceRequestType type;
        LiteYukonUser user;

        SingleMeterDisconnectCallback(DisconnectMeterResult result, CommandRequestExecution execution,
                                      DeviceRequestType type, LiteYukonUser user) {
            this.result = result;
            this.execution = execution;
            this.type = type;
            this.user = user;
        }

        @Override
        public void connected(SimpleDevice device, Instant timestamp) {
            result.setState(DisconnectDeviceState.CONNECTED);
            result.setDisconnectTime(timestamp);
        }
        @Override
        public void armed(SimpleDevice device, Instant timestamp) {
            result.setState(DisconnectDeviceState.ARMED);
            result.setDisconnectTime(timestamp);
        }

        @Override
        public void disconnected(SimpleDevice device, Instant timestamp) {
            result.setState(DisconnectDeviceState.DISCONNECTED);
            result.setDisconnectTime(timestamp);
        }

        @Override
        public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
            result.setState(DisconnectDeviceState.FAILED);
            result.setError(error);
        }

        @Override
        public void canceled(SimpleDevice device) {
            // not implemented
        }

        @Override
        public void complete() {
            isComplete = true;
            log.debug("Command Complete");
            execution.setRequestCount(1);
            completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.COMPLETE);
            disconnectEventLogService.actionCompleted(user,
                                                      result.getCommand(),
                                                      result.getMeter().getName(),
                                                      result.getState(),
                                                      result.isSuccess() ? 1 : 0);
            completeLatch.countDown();
        }

        @Override
        public boolean isComplete() {
            return isComplete;
        }

        @Override
        public void processingExceptionOccured(String reason) {
            if (!isComplete) {
                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.FAILED);
                result.setProcessingException(reason);
                complete();
            }
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public void cancel() {
            // not implemented
        }

        public void waitForCompletion() throws InterruptedException {
            log.debug("Starting await completion");
            completeLatch.await();
            log.debug("Finished await completion");
        }
    } 
    
    private void saveUnsupported(List<SimpleDevice> devices, final int creId,
                                 final CommandRequestUnsupportedType type) {
        for (SimpleDevice device : devices) {
            CommandRequestUnsupported unsupported = new CommandRequestUnsupported();
            unsupported.setCommandRequestExecId(creId);
            unsupported.setDeviceId(device.getDeviceId());
            unsupported.setType(type);
            commandRequestExecutionResultDao.saveUnsupported(unsupported);
        }
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
        
        disconnectEventLogService.groupCancelAttempted(userContext.getYukonUser(), command);
        
        DisconnectResult result = getResult(key);
        CommandRequestExecution execution = result.getCommandRequestExecution();
        execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELING);
        commandRequestExecutionDao.saveOrUpdate(execution);
        result.getDisconnectCallback().cancel();
        for (DisconnectStrategy strategy : strategies) {
            strategy.cancel(result, userContext);
        }
    }

    private void completeCommandRequestExecutionRecord(CommandRequestExecution cre,
                                                       CommandRequestExecutionStatus status) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        if (cre.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.FAILED) {
            cre.setStopTime(new Date());
            cre.setCommandRequestExecutionStatus(status);
            commandRequestExecutionDao.saveOrUpdate(cre);
        }
    }
    
    @Override
    public boolean supportsArm(Iterable<SimpleDevice> meters) {
        
        for (DisconnectStrategy strategy : strategies) {
            if (strategy.supportsArm(meters)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean supportsDisconnect(Iterable<SimpleDevice> meters) {
        
        for (DisconnectStrategy strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(meters);
            if (!filteredDevices.getValid().isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean supportsDisconnectConfiguration(Iterable<SimpleDevice> meters) {
        
        for (DisconnectStrategy strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(meters);
            if (!filteredDevices.getValid().isEmpty() || !filteredDevices.getNotConfigured().isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
    
}