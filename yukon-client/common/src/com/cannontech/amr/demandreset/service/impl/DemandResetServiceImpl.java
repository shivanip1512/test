package com.cannontech.amr.demandreset.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.demandreset.service.DemandResetVerificationCallback;
import com.cannontech.amr.device.StrategyType;
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
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.DemandResetEventLogService;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Sets;

public class DemandResetServiceImpl implements DemandResetService {
    private static final Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

    @Autowired private DemandResetEventLogService demandResetEventLogService;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private MeterDao meterDao;
    @Autowired private List<DemandResetStrategy> strategies;
   
    private final RecentResultsCache<DemandResetResult> resultsCache = new RecentResultsCache<>();
    
    @Override
    public DemandResetResult sendDemandResetAndVerify(DeviceCollection deviceCollection,
                                    SimpleCallback<DemandResetResult> alertCallback,
                                    YukonUserContext userContext) {  
                
        log.debug("sendDemandReset (Collection Action)");
        LiteYukonUser user = userContext.getYukonUser();
        DemandResetResult result = new DemandResetResult();
        result.setKey(resultsCache.addResult(result));
        
        //create temporary device groups
        TempGroups tempGroups = new TempGroups(result);

        result.setAllDevicesCollection(deviceCollection);
        
        Set<SimpleDevice> allDevices = new HashSet<>(deviceCollection.getDeviceList());
        
        HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy = devicesForStrategy(allDevices);
        
        Set<SimpleDevice> devicesToSend = new HashSet<>(devicesForStrategy.values());
        Set<SimpleDevice> unsupportedDevices = Sets.difference(allDevices, devicesToSend);           
        Set<SimpleDevice> devicesToVerify = getVerifiableDevices(devicesToSend);
        Set<SimpleDevice> unverifiableDevices = Sets.difference(devicesToSend, devicesToVerify);

        demandResetEventLogService.demandResetAttempted(devicesToSend.size(), 
                                                                  unsupportedDevices.size(),
                                                                  devicesToVerify.size(),
                                                                  unverifiableDevices.size(),
                                                                  result.getKey(), 
                                                                  user);
        CommandRequestExecution initiatedExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.DEMAND_RESET_COMMAND,
                                                              devicesToSend.size(), user);
        result.setInitiatedExecution(initiatedExecution);
        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices, initiatedExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        deviceGroupMemberEditorDao.addDevices(tempGroups.unsupportedGroup, unsupportedDevices);


        log.debug("All Devices:" + allDevices);
        log.debug("Support Demand Reset:" + devicesToSend);
        log.debug("Unsupported:" + unsupportedDevices);
        log.debug("Verifiable:" + devicesToVerify);
        log.debug("Unverifiable:" + unverifiableDevices);
        log.debug("initiatedExecution creId:" + initiatedExecution.getId());
 
        
        if (!devicesToSend.isEmpty()) {
            CommandRequestExecution verificationExecution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY,
                                                                  devicesToVerify.size(), user);
            log.debug("verificationExecution creId:" + verificationExecution.getId());

            result.setVerificationExecution(verificationExecution);
            commandRequestExecutionResultDao.saveUnsupported(unverifiableDevices, verificationExecution.getId(),
                CommandRequestUnsupportedType.UNSUPPORTED);
            CollectionCallback callback = new CollectionCallback(devicesForStrategy, devicesToVerify, tempGroups, result, alertCallback);
            result.setDemandResetCallback(callback);
        
            Set<CommandCompletionCallback<CommandRequestDevice>> completionCallbacks =
                sendDemandResetAndVerify(devicesForStrategy, callback, initiatedExecution, verificationExecution, user);
            result.addCommandCompletionCallbacks(completionCallbacks);
        } else {
            completeCommandRequestExecution(initiatedExecution, CommandRequestExecutionStatus.COMPLETE);
            result.setComplete(true);
        }

        return result;
    }
      
    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user) {
       
        log.debug("sendDemandReset");
        
        Set<SimpleDevice> allDevices = Sets.newHashSet(PaoUtils.asSimpleDeviceListFromPaos(devices));
        
        HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy = devicesForStrategy(allDevices);
        
        Set<SimpleDevice> devicesToSend = new HashSet<>(devicesForStrategy.values());
        Set<SimpleDevice> unsupportedDevices = Sets.difference(allDevices, devicesToSend);

        demandResetEventLogService.demandResetAttempted(devicesToSend.size(), 
                                                                  unsupportedDevices.size(),
                                                                  0, 0, null, user);
        
        CommandRequestExecution initiatedExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.DEMAND_RESET_COMMAND,
                                                              devicesToSend.size(),
                                                              user);

        log.debug("All Devices:" + allDevices);
        log.debug("Support Demand Reset:" + devicesToSend);
        log.debug("Unsupported:" + unsupportedDevices);
        log.debug("initiatedExecution creId:" + initiatedExecution.getId());

        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices, initiatedExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);

        if (!devicesToSend.isEmpty()) {
            InitiationCallback initiationCallback =
                new InitiationCallback(devicesForStrategy, callback, initiatedExecution, user);
            sendDemandReset(devicesForStrategy, initiationCallback, initiatedExecution, user);
        } else {
            completeCommandRequestExecution(initiatedExecution, CommandRequestExecutionStatus.COMPLETE);
        }
    }
    
    @Override
    public void sendDemandResetAndVerify(Set<? extends YukonPao> devices, DemandResetCallback callback,
                                         LiteYukonUser user) {

        log.debug("sendDemandResetAndVerify");
        
        Set<SimpleDevice> allDevices = Sets.newHashSet(PaoUtils.asSimpleDeviceListFromPaos(devices));
        
        HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy = devicesForStrategy(allDevices);
        
        Set<SimpleDevice> devicesToSend = new HashSet<>(devicesForStrategy.values());
        Set<SimpleDevice> unsupportedDevices = Sets.difference(allDevices, devicesToSend);
        Set<SimpleDevice> devicesToVerify = getVerifiableDevices(devicesToSend);
        Set<SimpleDevice> unverifiableDevices = Sets.difference(devicesToSend, devicesToVerify);

        demandResetEventLogService.demandResetAttempted(devicesToSend.size(), 
                                                                  unsupportedDevices.size(),
                                                                  devicesToVerify.size(),
                                                                  unverifiableDevices.size(),
                                                                  null, user);
        
        CommandRequestExecution initiationExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.DEMAND_RESET_COMMAND,
                                                              devicesToSend.size(),
                                                              user);
        

        log.debug("All Devices:" + allDevices);
        log.debug("Support Demand Reset:" + devicesToSend);
        log.debug("Unsupported:" + unsupportedDevices);
        log.debug("Verifiable:" + devicesToVerify);
        log.debug("Unverifiable:" + unverifiableDevices);
        log.debug("initiatedExecution creId:" + initiationExecution.getId());

        
        commandRequestExecutionResultDao.saveUnsupported(unsupportedDevices, initiationExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
           
        if (!devicesToSend.isEmpty()) {
            
            CommandRequestExecution verificationExecution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY,
                                                                  devicesToVerify.size(),
                                                                  user);
            log.debug("verificationExecution creId:" + verificationExecution.getId());
           
            commandRequestExecutionResultDao.saveUnsupported(unverifiableDevices,
                            verificationExecution.getId(),
                            CommandRequestUnsupportedType.UNSUPPORTED);
            VerificationCallback verificationCallback =
                new VerificationCallback(devicesForStrategy, callback, initiationExecution, verificationExecution, user);
            sendDemandResetAndVerify(devicesForStrategy, verificationCallback, initiationExecution,
                verificationExecution, user);
        } else {
            completeCommandRequestExecution(initiationExecution, CommandRequestExecutionStatus.COMPLETE);
        }
    }
    
    @Override
    public DemandResetResult getResult(String key) {
        return resultsCache.getResult(key);
    }

    @Override
    public Iterable<DemandResetResult> getResults() {
        
        List<DemandResetResult> results = new ArrayList<>();
        results.addAll(resultsCache.getCompleted());
        results.addAll(resultsCache.getPending());
        Collections.sort(results);
        
        return results;
    }
    
    @Override
    public void cancel(String key, LiteYukonUser user) {
        
        DemandResetResult result = getResult(key);
        CommandRequestExecution verificationExecution = result.getVerificationExecution();
        verificationExecution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELING);
        commandRequestExecutionDao.saveOrUpdate(verificationExecution);

        demandResetEventLogService.cancelInitiated(user, key);
        
        result.getDemandResetCallback().cancel();
        for (DemandResetStrategy strategy : strategies) {
            strategy.cancel(result, user);
        }
    } 
    
    private void log(Set<SimpleDevice> meters, LiteYukonUser user, DeviceRequestType deviceRequestType) {
        
        Iterable<YukonMeter> yukonMeters = meterDao.getMetersForYukonPaos(meters);
        for (YukonMeter meter : yukonMeters) {
            demandResetEventLogService.demandResetInitiated(user, meter.getName(), deviceRequestType.getShortName());
        }
        
    }
    
    private void sendDemandReset(HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy,
            DemandResetCallback callback, CommandRequestExecution initiatedExecution, LiteYukonUser user) {
        for (DemandResetStrategy strategy : devicesForStrategy.keySet()) {
            Set<SimpleDevice> meters = devicesForStrategy.get(strategy);
            log(meters, user, initiatedExecution.getCommandRequestExecutionType());
            strategy.sendDemandReset(initiatedExecution, meters, callback, user);
        }
    }

    private Set<CommandCompletionCallback<CommandRequestDevice>> sendDemandResetAndVerify(
            HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy, DemandResetCallback callback,
            CommandRequestExecution initiatedExecution, CommandRequestExecution verificationExecution,
            LiteYukonUser user) {
        Set<CommandCompletionCallback<CommandRequestDevice>> completionCallbacks = new HashSet<>();
        for (DemandResetStrategy strategy : devicesForStrategy.keySet()) {
            Set<SimpleDevice> meters = devicesForStrategy.get(strategy);

            log(meters, user, verificationExecution.getCommandRequestExecutionType());
            // send demand reset and collect callbacks needed for cancellation
            Set<CommandCompletionCallback<CommandRequestDevice>> callbacks =
                strategy.sendDemandResetAndVerify(initiatedExecution, verificationExecution, meters, callback, user);
            if (callbacks != null) {
                completionCallbacks.addAll(callbacks);
            }
        }
        return completionCallbacks.isEmpty() ? null : completionCallbacks;
    }
    
    private Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos) {

        Set<SimpleDevice> allValidDevices = new HashSet<>();
        for (DemandResetStrategy strategy : strategies) {
            allValidDevices.addAll(strategy.getVerifiableDevices(paos));
        }

        return allValidDevices;
    }

    private void completeCommandRequestExecution(CommandRequestExecution commandRequestExecution,
                                                       CommandRequestExecutionStatus executionStatus) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        if (commandRequestExecution != null &&
            commandRequestExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.FAILED) {
            commandRequestExecution.setStopTime(new Date());
            commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
            commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        }
    } 
    
    private class TempGroups {

        private final StoredDeviceGroup confirmedGroup;
        private final StoredDeviceGroup unconfirmedGroup;
        private final StoredDeviceGroup unsupportedGroup;
        private final StoredDeviceGroup canceledGroup;
        private final StoredDeviceGroup failedGroup;

        TempGroups(DemandResetResult result) {
            confirmedGroup = tempDeviceGroupService.createTempGroup();
            unconfirmedGroup = tempDeviceGroupService.createTempGroup();
            unsupportedGroup = tempDeviceGroupService.createTempGroup();
            canceledGroup = tempDeviceGroupService.createTempGroup();
            failedGroup = tempDeviceGroupService.createTempGroup();

            result.setConfirmedCollection(deviceGroupCollectionHelper.buildDeviceCollection(confirmedGroup));
            result.setUnconfirmedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unconfirmedGroup));
            result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
            result.setFailedCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
            result.setCanceledCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));
        }
    }
    
    private class CollectionCallback implements DemandResetCallback {
        
        TempGroups tempGroups;
        DemandResetResult result;
        SimpleCallback<DemandResetResult> alertCallback;

        Set<StrategyType> pendingStrategies;
        Set<SimpleDevice> devicesToSend = new HashSet<>();
        Set<SimpleDevice> devicesToVerify = new HashSet<>();
        boolean isCanceled = false;

        public CollectionCallback(HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy,
                                  Set<SimpleDevice> devicesToVerify,
                                  TempGroups tempGroups, DemandResetResult result,
                                  SimpleCallback<DemandResetResult> alertCallback) {
            this.devicesToSend.addAll(devicesForStrategy.values());
            this.devicesToVerify.addAll(devicesToVerify);
            pendingStrategies = Sets.newConcurrentHashSet(
                devicesForStrategy.keySet().stream().map(s -> s.getType()).collect(Collectors.toSet()));
            this.tempGroups = tempGroups;
            this.result = result;
            this.alertCallback = alertCallback;
        }
        
        //Initiated called once per strategy after the request was sent. 
        @Override
        public void initiated(Results results) { 
            log.debug("Demand reset is initiated for:" + results.getAllDevices());
            
            //add errors that happened during sending of demand reset command.
            result.addErrors(results.getErrors());
            
            for (SimpleDevice device : results.getErrors().keySet()) {
                if (devicesToSend.contains(device)) {
                    failed(device, results.getErrors().get(device));
                }
            }
            
            //remove all devices to which demand reset command was sent.
            devicesToSend.removeAll(results.getAllDevices());        
            
            //If there are no devices to verify the strategy is complete.
            if (!CollectionUtils.containsAny(devicesToVerify, results.getAllDevices())) {
                pendingStrategies.forEach(s -> complete(s));
            }
        }

        @Override
        public void verified(SimpleDevice device, Instant pointDataTimeStamp) {
            deviceGroupMemberEditorDao.addDevices(tempGroups.confirmedGroup, device);
            result.addTimestamp(device, pointDataTimeStamp);
            devicesToVerify.remove(device);
        }

        @Override
        public void failed(SimpleDevice device, SpecificDeviceErrorDescription error) {
            deviceGroupMemberEditorDao.addDevices(tempGroups.failedGroup, device);
            devicesToVerify.remove(device);
            result.addError(device, error);
        }

        @Override
        public void cannotVerify(SimpleDevice device, SpecificDeviceErrorDescription error) {
            deviceGroupMemberEditorDao.addDevices(tempGroups.unconfirmedGroup, device);
            devicesToVerify.remove(device);
            result.addError(device, error);
        }
        
        @Override
        public void complete(StrategyType strategy) {
            log.debug("Demand Reset Completed for "+strategy+" strategy.");
            pendingStrategies.remove(strategy);
            if (pendingStrategies.isEmpty()) {
                completeCommandRequestExecution(result.getInitiatedExecution(), CommandRequestExecutionStatus.COMPLETE);
                if (result.getCanceledCollection().getDeviceCount() > 0) {
                    log.debug("Demand Reset Canceled");
                    Set<SimpleDevice> canceled =
                        Sets.newHashSet(result.getCanceledCollection().getDeviceList());
                    commandRequestExecutionResultDao.saveUnsupported(canceled,
                                    result.getVerificationExecution().getId(),
                                    CommandRequestUnsupportedType.CANCELED);
                    List<SimpleDevice> completed = new ArrayList<>();
                    completed.addAll(result.getConfirmedCollection().getDeviceList());
                    completed.addAll(result.getUnconfirmedCollection().getDeviceList());
                    completed.addAll(result.getFailedCollection().getDeviceList());
                    //unconfirmed collection might contain devices that can't be verified (no point)
                    //exclude those devices when when updating execution count
                    Set<SimpleDevice> verify = getVerifiableDevices(Sets.newHashSet(completed));
                    result.getVerificationExecution().setRequestCount(verify.size());
                    completeCommandRequestExecution(result.getVerificationExecution(),
                                                          CommandRequestExecutionStatus.CANCELLED);
                } else {
                    log.debug("Demand Reset Completed");
                    completeCommandRequestExecution(result.getVerificationExecution(),
                                                          CommandRequestExecutionStatus.COMPLETE);
                }
                demandResetEventLogService.demandResetCompletedResults(result.getKey(),
                                                               result.getTotalCount(),
                                                               result.getSuccessCount(),
                                                               result.getFailedCount(),
                                                               result.getNotAttemptedCount());
                result.setComplete(true);
                try {
                    alertCallback.handle(result);
                } catch (Exception e) {
                    log.warn("caught exception in complete", e);
                }
            }
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }

        @Override
        public void cancel() {
            //Implemented for PLC only
            isCanceled = true;
        }

        @Override
        public void canceled(SimpleDevice device) {
            deviceGroupMemberEditorDao.addDevices(tempGroups.canceledGroup, device);
            devicesToVerify.remove(device);
        }

        @Override
        public void processingExceptionOccured(String reason) {
            if (result.getInitiatedExecution().getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                completeCommandRequestExecution(result.getInitiatedExecution(),
                                                      CommandRequestExecutionStatus.FAILED);
            }
            if (result.getVerificationExecution() != null
                && result.getVerificationExecution().getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                completeCommandRequestExecution(result.getVerificationExecution(),
                                                      CommandRequestExecutionStatus.FAILED);
            }
            result.setExceptionReason(reason);
        }
    }
   
    private class InitiationCallback extends DemandResetVerificationCallback {

        AtomicInteger pendingStrategies;
        Results initiationResults;
        DemandResetCallback callback;
        CommandRequestExecution initiationExecution;
        LiteYukonUser user;

        public InitiationCallback(HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy,
                                  DemandResetCallback callback,
                                  CommandRequestExecution initiatedExecution,
                                  LiteYukonUser user) {
            this.callback = callback;
            this.initiationExecution = initiatedExecution;
            this.user = user;
            initiationResults = new Results(Sets.newHashSet(devicesForStrategy.values()));
            pendingStrategies = new AtomicInteger(devicesForStrategy.keySet().size());
        }

        @Override
        public void initiated(Results results) {
            initiationResults.append(results);
            int remaining = pendingStrategies.decrementAndGet();
            if (remaining == 0) {
                log.debug("initiated:" + initiationResults.getAllDevices());
                completeCommandRequestExecution(initiationExecution, CommandRequestExecutionStatus.COMPLETE);
                callback.initiated(initiationResults);
                
                demandResetEventLogService.demandResetCompleted(user);
            }
        }

        @Override
        public void processingExceptionOccured(String reason) {
            completeCommandRequestExecution(initiationExecution, CommandRequestExecutionStatus.FAILED);
            callback.processingExceptionOccured(reason);
        }
    }
    
    private class VerificationCallback extends DemandResetVerificationCallback {
        
        AtomicInteger initiationStrategies;
        Set<StrategyType> verificationStrategies;
        Results initiationResults;
        DemandResetCallback callback;
        CommandRequestExecution verificationExecution;
        CommandRequestExecution initiationExecution;
        LiteYukonUser user;

        public VerificationCallback(HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy,
                                    DemandResetCallback callback,
                                    CommandRequestExecution initiationExecution,
                                    CommandRequestExecution verificationExecution, LiteYukonUser user) {
            this.verificationExecution = verificationExecution;
            initiationResults = new Results(Sets.newHashSet(devicesForStrategy.values()));
            this.initiationExecution = initiationExecution;
            this.user = user;
            this.callback = callback;
            initiationStrategies = new AtomicInteger(devicesForStrategy.keySet().size());
            verificationStrategies = Sets.newConcurrentHashSet(
                devicesForStrategy.keySet().stream().map(s -> s.getType()).collect(Collectors.toSet()));
        }
        
        @Override
        public void initiated(Results results) {
            initiationResults.append(results);
            int remaining = initiationStrategies.decrementAndGet();
            if (remaining == 0) {
                log.debug("initiated:" + results.getAllDevices());
                callback.initiated(initiationResults);
                completeCommandRequestExecution(initiationExecution, CommandRequestExecutionStatus.COMPLETE);
            }
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
        public void verified(SimpleDevice device, Instant resetTime) { 
            callback.verified(device, resetTime);
        }
        
        @Override
        public void processingExceptionOccured(String reason) {
            if (initiationExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                completeCommandRequestExecution(initiationExecution, CommandRequestExecutionStatus.FAILED);
            }
            if (verificationExecution != null && verificationExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                completeCommandRequestExecution(verificationExecution, CommandRequestExecutionStatus.FAILED);
            }
            callback.processingExceptionOccured(reason);
        }
        
        @Override
        public void complete(StrategyType type) {
            log.debug("Demand Reset Verefication Completed for "+type+" strategy.");
            verificationStrategies.remove(type);
            if (verificationStrategies.isEmpty()) {
                log.debug("Demand Reset Verefication Completed for all strategies.");
                completeCommandRequestExecution(verificationExecution, CommandRequestExecutionStatus.COMPLETE);
                demandResetEventLogService.demandResetCompleted(user);
            }
        }
    }
    
    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Set<T> allValidDevices = Sets.newHashSet();
        for (DemandResetStrategy strategy : strategies) {
            allValidDevices.addAll(strategy.filterDevices(devices));
        }
        return allValidDevices;
    }
    
    private HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy(Set<SimpleDevice> allDevices) {
        HashMultimap<DemandResetStrategy, SimpleDevice> devicesForStrategy = HashMultimap.create();
        for (DemandResetStrategy strategy : strategies) {
            Set<SimpleDevice> allValidDevices = strategy.filterDevices(allDevices);
            for(SimpleDevice device: allValidDevices){
                devicesForStrategy.put(strategy, device);
            }
         
        }
        return devicesForStrategy;
    }
}
