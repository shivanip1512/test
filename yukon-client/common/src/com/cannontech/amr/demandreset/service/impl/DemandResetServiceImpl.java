package com.cannontech.amr.demandreset.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.demandreset.model.DemandResetResult;
import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.amr.demandreset.service.DemandResetVerificationCallback;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
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
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MutableDuration;
import com.cannontech.common.util.RecentResultsCache;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Sets;

public class DemandResetServiceImpl implements DemandResetService {
    private final static Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private TemporaryDeviceGroupService tempDeviceGroupService;
    @Autowired private DeviceGroupCollectionHelper deviceGroupCollectionHelper;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CommandRequestExecutionDao commandRequestExecutionDao;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired @Qualifier("main") private ScheduledExecutor refreshTimer;
    private final RecentResultsCache<DemandResetResult> resultsCache = new RecentResultsCache<>();

    private final static class Callback extends DemandResetVerificationCallback {
        DemandResetCallback callerCallback;
        AtomicBoolean finished = new AtomicBoolean(false);
        Results results;

        @Override
        public void initiated(Results results) {
            // These get aggregated (from all strategies) so we only make a single call back to
            // the the caller of sendDemandReset.
            this.results = results;
            finished.set(true);
        }

        @Override
        public void failed(SimpleDevice device, String reason) {
            callerCallback.failed(device, reason);
        }

        @Override
        public void cannotVerify(SimpleDevice device, String reason) {
            callerCallback.cannotVerify(device, reason);
        }

        // These go out one at a time to the caller so we can just proxy.
        @Override
        public void verified(SimpleDevice device, Instant pointDataTimeStamp) {   
            callerCallback.verified(device, pointDataTimeStamp);
        }
    }

    @Autowired private List<DemandResetStrategy> strategies;
    private final Duration replyTimeout;

    @Autowired
    public DemandResetServiceImpl(ConfigurationSource configurationSource) {
        replyTimeout = configurationSource.getDuration("DEMAND_RESET_REPLY_TIMEOUT", Duration.standardMinutes(1));
    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Set<T> allValidDevices = Sets.newHashSet();
        for (DemandResetStrategy strategy : strategies) {
            allValidDevices.addAll(strategy.filterDevices(devices));
        }
        return allValidDevices;
    }

    @Override
    public DemandResetResult sendDemandReset(DeviceCollection deviceCollection,
                                    final SimpleCallback<DemandResetResult> alertCallback,
                                    final YukonUserContext userContext) {
        log.debug("sendDemandReset (Collection Action)");
        LiteYukonUser user = userContext.getYukonUser();
        final DemandResetResult result = new DemandResetResult();
        result.setKey(resultsCache.addResult(result));
        
        final StoredDeviceGroup confirmedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unconfirmedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup unsupportedGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup canceledGroup = tempDeviceGroupService.createTempGroup();
        final StoredDeviceGroup failedGroup = tempDeviceGroupService.createTempGroup();
        
        result.setAllDevicesCollection(deviceCollection);
        result.setConfirmedCollection(deviceGroupCollectionHelper.buildDeviceCollection(confirmedGroup));
        result.setUnconfirmedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unconfirmedGroup));
        result.setUnsupportedCollection(deviceGroupCollectionHelper.buildDeviceCollection(unsupportedGroup));
        result.setFailedCollection(deviceGroupCollectionHelper.buildDeviceCollection(failedGroup));
        result.setCanceledCollection(deviceGroupCollectionHelper.buildDeviceCollection(canceledGroup));

        Set<SimpleDevice> allDevices = new HashSet<>(deviceCollection.getDeviceList());
        final Set<SimpleDevice> devicesToSend = new HashSet<>(filterDevices(allDevices));
        Set<SimpleDevice> unsupportedDevices = Sets.difference(allDevices, devicesToSend);           
        final Set<SimpleDevice> devicesToVerify = getVerifiableDevices(devicesToSend);
        Set<SimpleDevice> unverifiableDevices = Sets.difference(devicesToSend, devicesToVerify);
        
        if (log.isDebugEnabled()) {
            log.debug("All Devices:" + allDevices);
            log.debug("Support Demand Reset:" + devicesToSend);
            log.debug("Unsupported:" + unsupportedDevices);
            log.debug("Verifiable:" + devicesToVerify);
            log.debug("Unverifiable:" + unverifiableDevices);
        }
        final CommandRequestExecution initiatedExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.DEMAND_RESET_COMMAND,
                                                              devicesToSend.size(),
                                                              userContext.getYukonUser());
        result.setInitiatedExecution(initiatedExecution);
        saveUnsupported(unsupportedDevices, initiatedExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        if(!devicesToSend.isEmpty()){
            CommandRequestExecution verificationExecution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY,
                                                                  devicesToVerify.size(),
                                                                  userContext.getYukonUser());
                result.setVerificationExecution(verificationExecution);
            saveUnsupported(unverifiableDevices, verificationExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        }
        deviceGroupMemberEditorDao.addDevices(unsupportedGroup, unsupportedDevices);      
        
        class CollectionCallback implements DemandResetCallback {
            AtomicInteger pendingStrategies;
            Set<SimpleDevice> devicesToSend = new HashSet<>();
            Set<SimpleDevice> devicesToVerify = new HashSet<>();
            boolean isCanceled = false;

            public CollectionCallback(Set<SimpleDevice> devicesToSend, Set<SimpleDevice> devicesToVerify) {
                this.devicesToSend.addAll(devicesToSend);
                this.devicesToVerify.addAll(devicesToVerify);
                pendingStrategies = new AtomicInteger(strategies.size());
                System.out.println("pendingStrategies="+pendingStrategies);
            }
            
            //Initiated called once per strategy after the request was sent. 
            @Override
            public void initiated(Results results) {
                if(log.isDebugEnabled()){
                    log.debug("Demand reset is initiated for:" + results.getAllDevices());
                }
                
                //add errors that happened during sending of demand reset command.
                result.addErrors(results.getErrors());
                
                for (SimpleDevice device : results.getErrors().keySet()) {
                    if (devicesToSend.contains(device)) {
                        failed(device, results.getErrors().get(device).getDescription());
                    }
                }
                
                //remove all devices to which demand reset command was sent.
                devicesToSend.removeAll(results.getAllDevices());        
                
                //If there are no devices to verify the strategy is complete.
                if (!CollectionUtils.containsAny(devicesToVerify, results.getAllDevices())) {
                    complete();
                }
            }

            @Override
            public void verified(SimpleDevice device, Instant pointDataTimeStamp) {
                deviceGroupMemberEditorDao.addDevices(confirmedGroup, device);
                result.addTimestamp(device, pointDataTimeStamp);
                devicesToVerify.remove(device);
            }

            @Override
            public void failed(SimpleDevice device, String reason) {
                deviceGroupMemberEditorDao.addDevices(failedGroup, device);
                devicesToVerify.remove(device);
                result.addCustomError(device, reason);
            }
            @Override
            public void cannotVerify(SimpleDevice device, String reason) {
                deviceGroupMemberEditorDao.addDevices(unconfirmedGroup, device);
                devicesToVerify.remove(device);
                result.addCustomError(device, reason);
            }
            
            public void timeout(){   
                log.debug("Callback Timeout");
                // complete all remaining strategies
               /*  int remaining = pendingStrategies.get();
                while(remaining > 0){
                    complete();
                    remaining = pendingStrategies.get();
                }*/
                
                // Process devices that timed out, not implemented
            }

            @Override
            public void complete() {
                log.debug("CollectionCallback:complete   pendingStrategies="+pendingStrategies.get());
                int remaining = pendingStrategies.decrementAndGet();
                if (remaining == 0) {
                    log.debug("All strategies complete");
                    completeCommandRequestExecutionRecord(initiatedExecution, CommandRequestExecutionStatus.COMPLETE);
                    if (result.getCanceledCollection().getDeviceCount() > 0) {
                        log.debug("Command Canceled");
                        Set<SimpleDevice> canceled =
                            Sets.newHashSet(result.getCanceledCollection().getDeviceList());
                        saveUnsupported(canceled,
                                        result.getVerificationExecution().getId(),
                                        CommandRequestUnsupportedType.CANCELED);
                        List<SimpleDevice> completed = new ArrayList<>();
                        completed.addAll(result.getConfirmedCollection().getDeviceList());
                        completed.addAll(result.getUnconfirmedCollection().getDeviceList());
                        completed.addAll(result.getFailedCollection().getDeviceList());
                        //unconfirmed collection might contain devices that can't be verified (no point)
                        //exclude those devices when when updating execution count
                        final Set<SimpleDevice> verify = getVerifiableDevices(Sets.newHashSet(completed));
                        result.getVerificationExecution().setRequestCount(verify.size());
                        completeCommandRequestExecutionRecord(result.getVerificationExecution(),
                                                              CommandRequestExecutionStatus.CANCELLED);
                    } else {
                        completeCommandRequestExecutionRecord(result.getVerificationExecution(),
                                                              CommandRequestExecutionStatus.COMPLETE);
                    }
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
                deviceGroupMemberEditorDao.addDevices(canceledGroup, device);
                devicesToVerify.remove(device);
            }

            @Override
            public void processingExceptionOccured(String reason) {
                if (initiatedExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                    completeCommandRequestExecutionRecord(initiatedExecution, CommandRequestExecutionStatus.FAILED);
                }
                if (result.getVerificationExecution().getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.COMPLETE) {
                    completeCommandRequestExecutionRecord(result.getVerificationExecution(),
                                                          CommandRequestExecutionStatus.FAILED);
                }
                result.setExceptionReason(reason);
            }
        }
                
        final CollectionCallback callback = new CollectionCallback(devicesToSend, devicesToVerify);
        result.setDemandResetCallback(callback);
        for (DemandResetStrategy strategy : strategies) {
            Set<SimpleDevice> meters = strategy.filterDevices(devicesToSend);
            if (meters.isEmpty()) {
                callback.complete();
            } else {
                //mark the result as "cancellable" if the demand reset is sent to the devices that support cancellation.
                //(UI will display the cancel button)
                if (strategy.cancellable()) {
                    result.setCancellable(true);
                }
                // send demand reset and collect callbacks needed for cancellation
                result.addCommandCompletionCallbacks(strategy.sendDemandReset(initiatedExecution,
                                                                              result.getVerificationExecution(),
                                                                              meters,
                                                                              callback,
                                                                              user));
            }
        }
     
        
        Runnable timeoutRunner = new Runnable() {
            @Override
            public void run() {
                if (log.isDebugEnabled()) {
                    log.debug("Demand reset timed out. Timeout was set to " + replyTimeout.getStandardMinutes() + " minutes");
                }
                callback.timeout();
            }
        };
        //Creates and executes a one-shot action that becomes enabled after the given delay.
        refreshTimer.schedule(timeoutRunner, replyTimeout.getStandardMinutes(), TimeUnit.MINUTES);
  
        return result;
    }
    

    
    public Set<SimpleDevice> getVerifiableDevices(Set<? extends YukonPao> paos) {
        Set<SimpleDevice> allValidDevices = new HashSet<>();
        for (DemandResetStrategy strategy : strategies) {
            allValidDevices.addAll(strategy.getVerifiableDevices(paos));
        }
        return allValidDevices;
    }

    
    private void saveUnsupported(Set<? extends YukonPao> devices, final int commandRequestExecutionId,
                                 final CommandRequestUnsupportedType type) {
        for (YukonPao device : devices) {
            CommandRequestUnsupported unsupported = new CommandRequestUnsupported();
            unsupported.setCommandRequestExecId(commandRequestExecutionId);
            unsupported.setDeviceId(device.getPaoIdentifier().getPaoId());
            unsupported.setType(type);
            commandRequestExecutionResultDao.saveUnsupported(unsupported);
        }
    }
    
    private void completeCommandRequestExecutionRecord(CommandRequestExecution commandRequestExecution,
                                                       CommandRequestExecutionStatus executionStatus) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        if (commandRequestExecution != null &&
                commandRequestExecution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.FAILED) {
            commandRequestExecution.setStopTime(new Date());
            commandRequestExecution.setCommandRequestExecutionStatus(executionStatus);
            commandRequestExecutionDao.saveOrUpdate(commandRequestExecution);
        }
    }
    
    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback, LiteYukonUser user) {
        log.debug("sendDemandReset");
        
        List<Callback> callbacks = new ArrayList<>();
        Set<SimpleDevice> allDevices = Sets.newHashSet(PaoUtils.asSimpleDeviceListFromPaos(devices));
        Set<SimpleDevice> devicesToSend = new HashSet<>(filterDevices(allDevices));
        Set<SimpleDevice> unsupportedDevices = Sets.difference(allDevices, devicesToSend);
        Set<SimpleDevice> devicesToVerify = getVerifiableDevices(devicesToSend);
        Set<SimpleDevice> unverifiableDevices = Sets.difference(devicesToSend, devicesToVerify);

        if (log.isDebugEnabled()) {
            log.debug("All Devices:" + allDevices);
            log.debug("Support Demand Reset:" + devicesToSend);
            log.debug("Unsupported:" + unsupportedDevices);
            log.debug("Verifiable:" + devicesToVerify);
            log.debug("Unverifiable:" + unverifiableDevices);
        }
        CommandRequestExecution initiatedExecution =
            commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                              DeviceRequestType.DEMAND_RESET_COMMAND,
                                                              devicesToSend.size(),
                                                              user);
        saveUnsupported(unsupportedDevices, initiatedExecution.getId(), CommandRequestUnsupportedType.UNSUPPORTED);
        CommandRequestExecution verificationExecution = null;
        if (!devicesToSend.isEmpty()) {
            verificationExecution =
                commandRequestExecutionDao.createStartedExecution(CommandRequestType.DEVICE,
                                                                  DeviceRequestType.DEMAND_RESET_COMMAND_VERIFY,
                                                                  devicesToVerify.size(),
                                                                  user);
            saveUnsupported(unverifiableDevices,
                            verificationExecution.getId(),
                            CommandRequestUnsupportedType.UNSUPPORTED);
        }

        for (DemandResetStrategy strategy : strategies) {
            Set<? extends YukonPao> strategyDevices = strategy.filterDevices(devicesToSend);
            if (strategyDevices.iterator().hasNext()) {
                Callback strategyCallback = new Callback();
                strategyCallback.callerCallback = callback;
                callbacks.add(strategyCallback);
                strategy.sendDemandReset(initiatedExecution, verificationExecution, strategyDevices, strategyCallback, user);
            }
        }

        MutableDuration millisWaited = new MutableDuration(0);
        Duration waitPeriod = new Duration(250);
        boolean finished = false;
        while (!finished && millisWaited.isShorterThan(replyTimeout)) {
            try {
                Thread.sleep(waitPeriod.getMillis());
                millisWaited.plus(waitPeriod);
            } catch (InterruptedException e) {
                log.warn("caught exception in sendDemandReset", e);
            }
            finished = true;
            for (Callback strategyCallback : callbacks) {
                if (!strategyCallback.finished.get()) {
                    finished = false;
                }
            }
        }

        Results finalResults = new Results(devices);
        for (Callback strategyCallback : callbacks) {
            if (strategyCallback.finished.get()) {
                finalResults.append(strategyCallback.results);
            } else {
                log.error("timed out waiting for demand reset");
                throw new RuntimeException("timed out waiting for demand reset");
            }
        }
        completeCommandRequestExecutionRecord(initiatedExecution, CommandRequestExecutionStatus.COMPLETE);
        completeCommandRequestExecutionRecord(verificationExecution, CommandRequestExecutionStatus.COMPLETE);
        callback.initiated(finalResults);
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

        result.getDemandResetCallback().cancel();
        for (DemandResetStrategy strategy : strategies) {
            strategy.cancel(result.getCommandCompletionCallbacks(), user);
        }
    } 
}
