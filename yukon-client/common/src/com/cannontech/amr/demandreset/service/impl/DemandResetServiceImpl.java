package com.cannontech.amr.demandreset.service.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CONFIRMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.FAILURE;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNCONFIRMED;
import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.UNSUPPORTED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.CANCELLED;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.COMPLETE;
import static com.cannontech.common.device.commands.CommandRequestExecutionStatus.FAILED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.demandreset.service.DemandResetStrategyService;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionLogDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DemandResetEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Sets;

public class DemandResetServiceImpl implements DemandResetService, CollectionActionCancellationService {
    private static final Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

    @Autowired private DemandResetEventLogService demandResetEventLogService;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private List<DemandResetStrategyService> strategies;
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    /**
     * This class represents devices split into "collections" of devices ready to send, unsupported,
     * verifiable, unverifiable.
     */
    private class Summary {
        Map<DemandResetStrategyService, Set<SimpleDevice>> validDevices;
        Map<DemandResetStrategyService, Set<SimpleDevice>> verifiableDevices;
        Set<SimpleDevice> devicesToSend = new HashSet<>();
        Set<SimpleDevice> unsupportedDevices = new HashSet<>();
        Set<SimpleDevice> devicesToVerify = new HashSet<>();
        Set<SimpleDevice> unverifiableDevices = new HashSet<>();

        Summary(List<SimpleDevice> devices) {
            Set<SimpleDevice> allDevices = new HashSet<>(devices);
            validDevices = getValidDeviceMap(allDevices);
            verifiableDevices = getVerifiableDeviceMap(allDevices);
            validDevices.values().forEach(v -> devicesToSend.addAll(v));
            unsupportedDevices.addAll(Sets.difference(allDevices, devicesToSend));
            verifiableDevices.values().forEach(v -> devicesToVerify.addAll(v));
            unverifiableDevices.addAll(Sets.difference(devicesToSend, devicesToVerify));
            log.debug("All Devices:" + allDevices);
            log.debug("Support Demand Reset:" + devicesToSend);
            log.debug("Unsupported:" + unsupportedDevices);
            log.debug("Verifiable:" + devicesToVerify);
            log.debug("Unverifiable:" + unverifiableDevices);
        }

        public Set<SimpleDevice> getValidDevices(StrategyType strategyType) {
            DemandResetStrategyService service = validDevices.keySet().stream().filter(
                strategy -> strategy.getStrategy() == strategyType).findFirst().get();
            return validDevices.get(service);
        }
    }
    
    @Override
    public int sendDemandResetAndVerify(DeviceCollection deviceCollection,
            SimpleCallback<CollectionActionResult> alertCallback, YukonUserContext context) {
       
        LiteYukonUser user = context.getYukonUser();
        Summary summary = new Summary(deviceCollection.getDeviceList());

        CollectionActionResult result = collectionActionService.createResult(CollectionAction.DEMAND_RESET, null,
            deviceCollection, CommandRequestType.DEVICE, DeviceRequestType.DEMAND_RESET_COMMAND, context);
        demandResetEventLogService.demandResetAttempted(summary.devicesToSend.size(), summary.unsupportedDevices.size(),
            summary.devicesToVerify.size(), summary.unverifiableDevices.size(), String.valueOf(result.getCacheKey()), user);
        collectionActionService.addUnsupportedToResult(UNSUPPORTED, result, new ArrayList<>(summary.unsupportedDevices));
        
        if(summary.devicesToSend.isEmpty()) {
            collectionActionService.updateResult(result, COMPLETE);
            return result.getCacheKey();
        }

        CommandRequestExecution verifExecution =
            createVerificationExecution(result.getExecution().getContextId(), summary, user);
        result.setVerificationExecution(verifExecution);

        DemandResetCallback callback = new DemandResetCallback() {
            MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
            List<StrategyType> pendingInitStrategies = Collections.synchronizedList(summary.validDevices.keySet()
                .stream().filter( s -> !summary.getValidDevices(s.getStrategy()).isEmpty())
                .map(strategy -> strategy.getStrategy())
                .collect(Collectors.toList()));
            List<StrategyType> pendingVerifStrategies = Collections.synchronizedList(summary.verifiableDevices.keySet()
                .stream().map(strategy -> strategy.getStrategy())
                .collect(Collectors.toList()));
            @Override
            public void initiated(Results results, StrategyType strategyType) {
                pendingInitStrategies.remove(strategyType);
                log.debug("Completing " + strategyType + " initiaion strategy. Remaining initiation strategies:"
                    + pendingInitStrategies);
                Set<SimpleDevice> validDevices = summary.getValidDevices(strategyType);
                validDevices.forEach(device -> {
                    SpecificDeviceErrorDescription error = results.getErrors().get(device);
                    if (error != null) {
                        CollectionActionLogDetail detail = new CollectionActionLogDetail(device, FAILURE);
                        detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                        detail.setLastValue(DeviceRequestType.DEMAND_RESET_COMMAND.getShortName());
                        result.addDeviceToGroup(FAILURE, device, detail);
                    }
                });
                completeExecutions(strategyType);
            }

            @Override
            public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(device, FAILURE);
                detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                detail.setLastValue(DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY.getShortName());
                result.addDeviceToGroup(FAILURE, device, detail);
            }
            
            @Override
            public void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(device, UNCONFIRMED);
                detail.setLastValue(DeviceRequestType.DEMAND_RESET_COMMAND.getShortName());
                detail.setDeviceErrorText(accessor.getMessage(error.getDetail()));
                result.addDeviceToGroup(UNCONFIRMED, device, detail);
            }

            @Override
            public void complete(StrategyType strategyType) {
                if (!result.isComplete()) {
                    pendingVerifStrategies.remove(strategyType);
                    if(result.getStatus() ==  CommandRequestExecutionStatus.CANCELING) {
                        pendingInitStrategies.remove(strategyType);
                    }
                    completeExecutions(strategyType);
                }
            }
            
            private void completeExecutions(StrategyType strategyType) {
                if (pendingInitStrategies.isEmpty() && pendingVerifStrategies.isEmpty()) {
                    log.debug("Completing " + strategyType + " verification strategy. Remaining verification strategies:" + pendingVerifStrategies);
                    completeExecution(verifExecution, !result.isCanceled() ? COMPLETE : CANCELLED);
                    collectionActionService.updateResult(result, !result.isCanceled() ? COMPLETE : CANCELLED);
                    demandResetEventLogService.demandResetCompletedResults(String.valueOf(result.getCacheKey()),
                        result.getCounts().getTotalCount(), result.getCounts().getSuccessCount(),
                        result.getCounts().getFailedCount(), result.getCounts().getNotAttemptedCount());
                    try {
                        alertCallback.handle(result);
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                completeExecution(verifExecution, FAILED);
                result.setExecutionExceptionText(reason);
                collectionActionService.updateResult(result, FAILED);
            }

            @Override
            public void verified(SimpleDevice device, PointValueHolder value) {
                CollectionActionLogDetail detail = new CollectionActionLogDetail(device, CONFIRMED);
                detail.setValue(value);
                detail.setLastValue(DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY.getShortName());
                result.addDeviceToGroup(CONFIRMED, device, detail);  
            }
            
            @Override
            public CollectionActionResult getResult() {
                return result;
            }
        };

        sendDemandReset(summary.validDevices, callback, result.getExecution(), verifExecution, user);
        return result.getCacheKey();
    }
    
    private void completeExecution(CommandRequestExecution execution, CommandRequestExecutionStatus status) {
        if (execution != null && execution.getCommandRequestExecutionStatus() != COMPLETE
            && execution.getCommandRequestExecutionStatus() != FAILED) {
            log.debug("Completing execution:" + execution.getId() + " status=" + status + " for "
                + execution.getCommandRequestExecutionType());
            completeCommandRequestExecution(execution, status);
        }
    }
    
    private void sendDemandReset(Map<DemandResetStrategyService, Set<SimpleDevice>> validDevices,
            DemandResetCallback callback, CommandRequestExecution initExecution, CommandRequestExecution verifExecution,
            LiteYukonUser user) {

        for (DemandResetStrategyService strategy : strategies) {
            Set<SimpleDevice> meters = validDevices.get(strategy);
            if (meters.isEmpty()) {
                callback.complete(strategy.getStrategy());
            } else {
                meters.forEach(meter -> demandResetEventLogService.demandResetToDeviceInitiated(user,
                    dbCache.getAllPaosMap().get(meter.getPaoIdentifier().getPaoId()).getPaoName(),
                    DeviceRequestType.DEMAND_RESET_COMMAND.getShortName()));
                if (verifExecution == null) {
                    log.debug("StrategyType=" + strategy.getStrategy() + " Demand reset is initiated for:" + meters.size());
                    strategy.sendDemandReset(initExecution, meters, callback, user);
                } else {
                    log.debug("StrategyType=" + strategy.getStrategy() + " Demand reset and verify is initiated for:"
                        + meters.size());
                    strategy.sendDemandResetAndVerify(initExecution, verifExecution, meters, callback, user);
                }
            }
        }
    }
        
    private Map<DemandResetStrategyService, Set<SimpleDevice>> getValidDeviceMap(Set<SimpleDevice> allDevices) {
        return strategies.stream().collect(Collectors.toMap(s -> s, s -> s.filterDevices(allDevices)));
    }
    
    private Map<DemandResetStrategyService, Set<SimpleDevice>> getVerifiableDeviceMap(Set<SimpleDevice> allDevices) {
        return strategies.stream().collect(Collectors.toMap(s -> s, s -> s.getVerifiableDevices(allDevices)));
    }
    
      
    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user) {
        Summary summary = new Summary(devices.stream().map(d -> new SimpleDevice(d)).collect(Collectors.toList()));

        demandResetEventLogService.demandResetAttempted(summary.devicesToSend.size(), summary.unsupportedDevices.size(),
            0, 0, null, user);

        CommandRequestExecution execution = commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
            DeviceRequestType.DEMAND_RESET_COMMAND, summary.devicesToSend.size(), user);

        commandRequestExecutionResultDao.saveUnsupported(summary.unsupportedDevices, execution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);

        if (summary.devicesToSend.isEmpty()) {
            completeCommandRequestExecution(execution, COMPLETE);
            return;
        }

        DemandResetCallback initCallback = new DemandResetCallback() {
            Results initiationResults;
            List<StrategyType> pendingStrategies = Collections.synchronizedList(
                strategies.stream().map(strategy -> strategy.getStrategy()).collect(Collectors.toList()));

            @Override
            public void initiated(Results results, StrategyType strategyType) {
                log.debug("StrategyType=" + strategyType + " Demand reset is initiated for:"
                    + summary.getValidDevices(strategyType).size());
                if (initiationResults == null) {
                    initiationResults = results;
                } else {
                    initiationResults.append(results);
                }
                complete(strategyType);
            }

            @Override
            public void complete(StrategyType strategyType) {
                pendingStrategies.remove(strategyType);
                if (pendingStrategies.isEmpty()) {
                    completeCommandRequestExecution(execution, COMPLETE);
                    callback.initiated(initiationResults);
                    demandResetEventLogService.demandResetByApiCompleted(user);
                }
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                completeCommandRequestExecution(execution, FAILED);
                callback.processingExceptionOccurred(reason);
            }
        };

        sendDemandReset(summary.validDevices, initCallback, execution, null, user);
    }
    
    @Override
    public void sendDemandResetAndVerify(Set<? extends YukonPao> devices, DemandResetCallback callback,
            LiteYukonUser user) {
        Summary summary = new Summary(devices.stream().map(d -> new SimpleDevice(d)).collect(Collectors.toList()));

        demandResetEventLogService.demandResetAttempted(summary.devicesToSend.size(), summary.unsupportedDevices.size(),
            summary.devicesToVerify.size(), summary.unverifiableDevices.size(), null, user);

        CommandRequestExecution initExecution = commandRequestExecutionDao.createStartedExecution(
            CommandRequestType.DEVICE, DeviceRequestType.DEMAND_RESET_COMMAND, summary.devicesToSend.size(), user);

        log.debug("initiatedExecution creId:" + initExecution.getId());

        commandRequestExecutionResultDao.saveUnsupported(summary.unsupportedDevices, initExecution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);

        if (summary.devicesToSend.isEmpty()) {
            completeCommandRequestExecution(initExecution, COMPLETE);
            return;
        }

        CommandRequestExecution verifExecution =
            createVerificationExecution(initExecution.getContextId(), summary, user);

        DemandResetCallback verifCallback = new DemandResetCallback() {
            List<StrategyType> pendingInitStrategies = Collections.synchronizedList(summary.validDevices.keySet()
                .stream().filter( s -> !summary.getValidDevices(s.getStrategy()).isEmpty())
                .map(strategy -> strategy.getStrategy())
                .collect(Collectors.toList()));
            List<StrategyType> pendingVerifStrategies = Collections.synchronizedList(summary.verifiableDevices.keySet()
                .stream().map(strategy -> strategy.getStrategy())
                .collect(Collectors.toList()));
            
            Results initiationResults;

            @Override
            public void initiated(Results results, StrategyType strategyType) {
                pendingInitStrategies.remove(strategyType);
                log.debug("StrategyType=" + strategyType + " Demand reset is initiated for:"
                    + summary.getValidDevices(strategyType).size());
                if (initiationResults == null) {
                    initiationResults = results;
                } else {
                    initiationResults.append(results);
                }
                completeExecutions(strategyType);
            }

            @Override
            public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
                callback.failed(device, error);
            }

            @Override
            public void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
                callback.cannotVerify(device, error);
            }

            @Override
            public void complete(StrategyType strategyType) {
                pendingVerifStrategies.remove(strategyType);
                completeExecutions(strategyType);
            }

            @Override
            public void processingExceptionOccurred(String reason) {
                completeExecutions(FAILED);
                callback.processingExceptionOccurred(reason);
            }

            @Override
            public void verified(SimpleDevice device, PointValueHolder value) {
                callback.verified(device, value);
            }

            private void completeExecutions(StrategyType strategyType) {
                if (pendingInitStrategies.isEmpty() && pendingVerifStrategies.isEmpty()) {
                    log.debug("Completing " + strategyType + " verification strategy. Remaining verification strategies:" + pendingVerifStrategies);
                    log.debug("Demand Reset Verification Completed for all strategies.");
                    completeExecutions(COMPLETE);
                    demandResetEventLogService.demandResetByApiCompleted(user);
                }
            }
            
            private void completeExecutions(CommandRequestExecutionStatus newStatus) {
                if (initExecution.getCommandRequestExecutionStatus() != COMPLETE
                    && initExecution.getCommandRequestExecutionStatus() != FAILED) {
                    log.debug("Completing execution:" + initExecution.getId() + " status=" + newStatus + " for "
                        + initExecution.getCommandRequestExecutionType());
                    completeCommandRequestExecution(initExecution, newStatus);
                }
                completeExecution(verifExecution, newStatus);
            }
        };

        sendDemandReset(summary.validDevices, verifCallback, initExecution, verifExecution, user);
    }
    
    private void completeCommandRequestExecution(CommandRequestExecution commandRequestExecution,
                                                       CommandRequestExecutionStatus executionStatus) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        if (commandRequestExecution != null &&
            commandRequestExecution.getCommandRequestExecutionStatus() != FAILED) {
            commandRequestExecution.setStopTime(new Date());
            commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
            commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        }
    } 
    
    /**
     * Creates verification executions and marks devices as unverifiable and/or unsupported
     */
    private CommandRequestExecution createVerificationExecution(int contextId, Summary summary, LiteYukonUser user) {
        CommandRequestExecution verifExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY, contextId, summary.devicesToVerify.size(), user);
        log.debug("verificationExecution creId:" + verifExecution.getId());
        commandRequestExecutionResultDao.saveUnsupported(summary.unverifiableDevices, verifExecution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);
        commandRequestExecutionResultDao.saveUnsupported(summary.unsupportedDevices, verifExecution.getId(),
            CommandRequestUnsupportedType.UNSUPPORTED);
        return verifExecution;
    }
    
    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Set<T> allValidDevices = Sets.newHashSet();
        for (DemandResetStrategyService strategy : strategies) {
            allValidDevices.addAll(strategy.filterDevices(devices));
        }
        return allValidDevices;
    }
    
    @Override
    public boolean isCancellable(CollectionAction action) {
        return action == CollectionAction.DEMAND_RESET;
    }

    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = collectionActionService.getCachedResult(key);
        if (result != null) {
           //log cancellation attempt
            result.setCanceled(true);
            collectionActionService.updateResult(result, CommandRequestExecutionStatus.CANCELING);
            strategies.forEach(s -> s.cancel(result, user));
        }
    }
}
