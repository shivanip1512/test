package com.cannontech.amr.disconnect.service.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CANCELED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.NOT_CONFIGURED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNSUPPORTED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.getDisconnectDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.amr.disconnect.model.DisconnectMeterResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectService;
import com.cannontech.amr.disconnect.service.DisconnectStrategyService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.Strategy;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DisconnectEventLogService;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DisconnectServiceImpl implements DisconnectService, CollectionActionCancellationService {

    private final Logger log = YukonLogManager.getLogger(DisconnectServiceImpl.class);

    @Autowired private List<DisconnectStrategyService> strategies;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DisconnectEventLogService disconnectEventLogService;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CollectionActionLogDetailService logService;

    @Override
    public CollectionActionResult execute(DisconnectCommand command, DeviceCollection deviceCollection,
            SimpleCallback<CollectionActionResult> alertCallback, YukonUserContext context) {

        disconnectEventLogService.groupDisconnectAttempted(context.getYukonUser(), command);
        CollectionActionResult result = collectionActionService.createResult(command.getCollectionAction(), null,
            deviceCollection, CommandRequestType.DEVICE, DeviceRequestType.GROUP_CONNECT_DISCONNECT, context);
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
        Map<DisconnectStrategyService, Set<SimpleDevice>> validMetersByStrategy = new HashMap<>();
        for (DisconnectStrategyService strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(allDevices);
            notConfiguredDevices.addAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getNotConfigured());
            unsupportedDevices.removeAll(filteredDevices.getValid());
            validMetersByStrategy.put(strategy, filteredDevices.getValid());
        }
        
        log.debug("validMetersByStrategy=" + validMetersByStrategy);

        addUnsupportedToResult(UNSUPPORTED, result, unsupportedDevices);
        addUnsupportedToResult(NOT_CONFIGURED, result, notConfiguredDevices);

        if (!unsupportedDevices.isEmpty() || !notConfiguredDevices.isEmpty()) {
            log.debug("Updated result with unsupported and not configured devices:");
            result.log();
        }
        
        DisconnectCallback callback = new DisconnectCallback() {
            List<Strategy> pendingStrategies = Collections.synchronizedList(strategies
                .stream().map(strategy -> strategy.getStrategy())
                .collect(Collectors.toList()));

            @Override
            public void success(DisconnectCommand command, SimpleDevice device, Instant timestamp) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(device, getDisconnectDetail(command));
                detail.setTime(timestamp);
                result.addDeviceToGroup(getDisconnectDetail(command), device, detail);
            }

            @Override
            public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
                result.addDeviceToGroup(FAILURE, device, new CollectionActionLogDetail(device, FAILURE));
            }

            @Override
            public void canceled(SimpleDevice device) {
                result.addDeviceToGroup(CANCELED, device, new CollectionActionLogDetail(device, CANCELED));
            }

            @Override
            public void complete(Strategy strategy) {
                pendingStrategies.remove(strategy);
                log.debug("Completing " + strategy + " strategy. Remaining strategies:" + pendingStrategies);
                if (pendingStrategies.isEmpty()) {
                    collectionActionService.updateResult(result, !result.isCanceled()
                        ? CommandRequestExecutionStatus.COMPLETE : CommandRequestExecutionStatus.CANCELLED);
                    disconnectEventLogService.groupActionCompleted(context.getYukonUser(), command,
                        result.getCounts().getTotalCount(), result.getCounts().getSuccessCount(),
                        result.getCounts().getFailedCount(), result.getCounts().getNotAttemptedCount());
                    result.log();
                    try {
                        alertCallback.handle(result);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }

            @Override
            public void processingExceptionOccured(String reason) {
                CollectionActionLogDetail log = new CollectionActionLogDetail(null, null);
                log.setExecutionExceptionText(reason);
                result.setExecutionExceptionText(reason, log);
                collectionActionService.updateResult(result, CommandRequestExecutionStatus.FAILED);
            }

            @Override
            public CollectionActionResult getResult() {
                return result;
            }
        };

        int requestCount = 0;
        for (DisconnectStrategyService strategy : strategies) {
            Set<SimpleDevice> meters = validMetersByStrategy.get(strategy);
            if (meters.isEmpty()) {
                callback.complete(strategy.getStrategy());
            } else {
                requestCount += meters.size();
                meters.forEach(meter -> disconnectEventLogService.disconnectInitiated(context.getYukonUser(), command,
                    dbCache.getAllPaosMap().get(meter.getPaoIdentifier().getPaoId()).getPaoName()));
                strategy.execute(command, meters, callback, result.getExecution(), context.getYukonUser());
            }
        }

        // update request count
        result.getExecution().setRequestCount(requestCount);
        log.debug("updating request count =" + requestCount);

        commandRequestExecutionDao.saveOrUpdate(result.getExecution());

        return result;
    }

    private void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result,
            List<SimpleDevice> unsupportedDevices) {
        result.addDevicesToGroup(detail, unsupportedDevices, logService.buildLogDetails(unsupportedDevices, detail));
        commandRequestExecutionResultDao.saveUnsupported(Sets.newHashSet(unsupportedDevices),
            result.getExecution().getId(), detail.getCreUnsupportedType());
    }
    @Override
    public DisconnectMeterResult execute(DisconnectCommand command, final DeviceRequestType type, YukonMeter meter,
            final LiteYukonUser user) {

        disconnectEventLogService.disconnectAttempted(user, command, meter.getName());

        List<SimpleDevice> allDevices = Lists.newArrayList(new SimpleDevice(meter));
        if (!supportsDisconnect(allDevices)) {
            throw new UnsupportedOperationException("Meter:" + meter + " doesn't support connect/disconnect");
        }

        CommandRequestExecution execution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE, type, 0, user);
        DisconnectMeterResult result = new DisconnectMeterResult(meter, command);

        if (log.isDebugEnabled()) {
            log.debug("Device: " + meter);
            log.debug("Execute: " + command);
            log.debug("CommandRequestExecutionId=" + execution.getId());
            log.debug("-----");
        }

        SingleMeterDisconnectCallback callback = new SingleMeterDisconnectCallback(result, execution, type, user);
        for (DisconnectStrategyService strategy : strategies) {
            FilteredDevices filteredDevices = strategy.filter(allDevices);
            if (!filteredDevices.getValid().isEmpty()) {

                log.debug("validMeters =" + filteredDevices.getValid());
                // since there is only 1 object in this method, if we made it here we can assume
                // filteredDevices == meter
                disconnectEventLogService.disconnectInitiated(user, command, meter.getName());

                strategy.execute(command, filteredDevices.getValid(), callback, execution, user);
            }
        }

        try {
            callback.waitForCompletion();
        } catch (InterruptedException e) { 
            log.error(e);
        }

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
        public void success(DisconnectCommand state, SimpleDevice device, Instant timestamp) {
            switch (state) {
            case ARM:
                result.setState(DisconnectDeviceState.ARMED);
                break;
            case CONNECT:
                result.setState(DisconnectDeviceState.CONNECTED);
                break;
            case DISCONNECT:
                result.setState(DisconnectDeviceState.DISCONNECTED);
                break;
            }
            result.setDisconnectTime(timestamp);
        }

        @Override
        public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
            result.setState(DisconnectDeviceState.FAILURE);
            result.setError(error);
        }

        @Override
        public void complete(Strategy strategy) {
            isComplete = true;
            log.debug("Command Completed by" + strategy+ " strategy");
            execution.setRequestCount(1);
            completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.COMPLETE);
            disconnectEventLogService.actionCompleted(user, result.getCommand(), result.getMeter().getName(),
                result.getState(), result.isSuccess() ? 1 : 0);
            completeLatch.countDown();
        }

        @Override
        public void processingExceptionOccured(String reason) {
            if (!isComplete) {
                completeCommandRequestExecutionRecord(execution, CommandRequestExecutionStatus.FAILED);
                result.setProcessingException(reason);
                complete(Strategy.PLC);
            }
        }

        public void waitForCompletion() throws InterruptedException {
            log.debug("Starting await completion");
            completeLatch.await();
            log.debug("Finished await completion");
        }
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = collectionActionService.getResult(key);
        disconnectEventLogService.groupCancelAttempted(user, DisconnectCommand.getDisconnectCommand(result.getAction()));
        collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELING);
        result.setCanceled(true);
        for (DisconnectStrategyService strategy : strategies) {
            strategy.cancel(result, user);
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
    public boolean supportsArm(List<SimpleDevice> meters) {
        return strategies.stream()
                .anyMatch(strategy -> strategy.supportsArm(meters));
    }

    @Override
    public boolean supportsDisconnect(List<SimpleDevice> meters) {
        return supportsDisconnect(meters, false);
    }

    @Override
    public boolean supportsDisconnect(List<SimpleDevice> meters, boolean includeNotConfigured) {
        return strategies.stream().anyMatch(strategy -> {
            FilteredDevices filteredDevices = strategy.filter(meters);
            if (!filteredDevices.getValid().isEmpty()) {
                return true;
            } else if (includeNotConfigured && !filteredDevices.getNotConfigured().isEmpty()) {
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.CONNECT || action == CollectionAction.ARM
            || action == CollectionAction.DISCONNECT;
    }
}