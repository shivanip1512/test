package com.cannontech.common.bulk.collection.device.service.impl;

import static com.cannontech.common.bulk.collection.device.model.CollectionActionDetail.CANCELED;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceMemoryCollectionProducer;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.CollectionActionTerminate;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLogDetailService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionLoggingHelperService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Sets;

public class CollectionActionServiceImpl implements CollectionActionService {
    
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private CommandRequestExecutionDao executionDao;
    @Autowired private CollectionActionLogDetailService logService;
    @Autowired private List<CollectionActionCancellationService> cancellationService;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceMemoryCollectionProducer deviceMemoryCollectionProducer;
    
    @Autowired private CollectionActionLoggingHelperService eventLogHelper;
    
    private final Logger log = YukonLogManager.getLogger(CollectionActionServiceImpl.class);
    
    private Cache<Integer, CollectionActionResult> cache =
        CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).build();
    
    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, CommandRequestType commandRequestType, DeviceRequestType deviceRequestType,
            YukonUserContext context) {
        CommandRequestExecution execution =
            executionDao.createStartedExecution(commandRequestType, deviceRequestType, 0, context.getYukonUser());
        CollectionActionResult result = new CollectionActionResult(action, collection.getDeviceList(), inputs,
            execution, editorDao, tempGroupService, groupHelper, deviceMemoryCollectionProducer, logService, context);
        result.setExecution(execution);
        saveAndLogResult(result, context.getYukonUser());
        return result;
    }

    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, YukonUserContext context) {
        CollectionActionResult result = new CollectionActionResult(action, collection.getDeviceList(), inputs,
            editorDao, tempGroupService, groupHelper, deviceMemoryCollectionProducer, logService, context);
        saveAndLogResult(result, context.getYukonUser());
        return result;
    }

    @Transactional
    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult result = cache.getIfPresent(key);
        cancel(result, user);
    }
    
    @Override
    public void removeResultFromCache(int key) {
        cache.invalidate(key);
    }
    
    @Transactional
    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        if (cancelAction(result, user)) {
            log.info("Cancelled collection action {}.", result.getCacheKey());
            return;
        }
        if (terminate(result)) {
            log.info("Terminated collection action {}", result.getCacheKey());
        } else {
            log.info("Attempt to cancel collection action {} failed.", result.getCacheKey());
        }
    }

    /**
     * Attempts to terminate collection action
     */
    private boolean terminate(CollectionActionResult result) {
        if (result.isTerminatable()) {
            result.setCanceled(true);
            updateResult(result, CommandRequestExecutionStatus.CANCELLED);
            return true;
        }
        return false;
    }

    /**
     * Attempts to cancel collection action, returns true if cancellation attempt is successful
     */
    private boolean cancelAction(CollectionActionResult result, LiteYukonUser user) {
        Optional<CollectionActionCancellationService> service = cancellationService.stream()
                .filter(s -> s.isCancellable(result.getAction()))
                .findFirst();
        if (service.isPresent() && result.isCancelable()) {
            log.debug("Using " + service.get().getClass() + " to cancel result for " + result.getCacheKey());
            service.get().cancel(result.getCacheKey(), user);
            return true;
        }
        return false;
    }
        
    @Transactional
    @Override
    public void updateResult(CollectionActionResult result, CommandRequestExecutionStatus status) {
        CommandRequestExecutionStatus statusBeforeChange = result.getStatus();
        log.info("Updating collection action {} changing status from {} to {}.", result.getCacheKey(),
                result.getStatus(), status);
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        CommandRequestExecution execution = result.getExecution();
        Date stopTime = status == CommandRequestExecutionStatus.CANCELING ? null : new Date();
        if (execution != null && execution.getCommandRequestExecutionStatus() != CommandRequestExecutionStatus.FAILED) {
            log.debug("Cache key:" + result.getCacheKey() + " Completing execution:" + execution.getId() + " status="
                    + status + " for " + execution.getCommandRequestExecutionType());
            execution.setStopTime(stopTime);
            execution.setCommandRequestExecutionStatus(status);
            executionDao.saveOrUpdate(execution);
        }
        if (execution != null) {
            collectionActionDao.updateCollectionActionStatus(result.getCacheKey(),
                    execution.getCommandRequestExecutionStatus(), stopTime);
            result.setStatus(execution.getCommandRequestExecutionStatus());
        } else {
            collectionActionDao.updateCollectionActionStatus(result.getCacheKey(), status, stopTime);
            result.setStatus(status);
        }

        if (result.getStatus() == CommandRequestExecutionStatus.CANCELLED) {
            addUnsupportedToResult(CANCELED, result, result.getCancelableDevices());
        }
        result.setStopTime(new Instant(stopTime));
        log.info("Collection action {} status changed from {} to {}.", result.getCacheKey(),
                statusBeforeChange, result.getStatus());
        log.info(result);
        eventLogHelper.log(result);
    }

    @Override
    public CollectionActionResult getResult(int key) {
        if(cache.getIfPresent(key) == null) {
            CollectionActionResult result = collectionActionDao.loadResultFromDb(key);
            cache.put(key, result);
            log.info(result);
        }
        return cache.getIfPresent(key);
    }
    
    @Override
    public void clearCache() {
        cache.invalidateAll();
    }
    
    @Override
    public int terminate() {
        // Cancels started collection actions with command request execution entry on start-up
        List<CollectionActionTerminate> results = collectionActionDao.loadIncompeteResultsFromDb();
        log.info("Attempting to terminate {} Collection Actions", results.size());
        results.forEach(result -> {
            log.debug("{}", result);
            Date stopTime = new Date();
            terminateExecution(result, result.getExecution(), stopTime);
            terminateExecution(result, result.getVerificationExecution(), stopTime);
            collectionActionDao.updateCollectionActionStatus(result.getCacheKey(), CommandRequestExecutionStatus.CANCELLED,
                    stopTime);
            eventLogHelper.log(result.toCollectionActionResult());
        });
        log.info("Terminated {} Collection Actions", results.size());
        return results.size();
    }

    private void terminateExecution(CollectionActionTerminate result,
            CommandRequestExecution execution, Date stopTime) {
        if (execution != null && (execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.STARTED
                || execution.getCommandRequestExecutionStatus() == CommandRequestExecutionStatus.CANCELING)) {
            execution.setStopTime(stopTime);
            execution.setCommandRequestExecutionStatus(CommandRequestExecutionStatus.CANCELLED);
            result.addDevices(CollectionActionDetail.CANCELED, result.getCancelableDevices());
            commandRequestExecutionResultDao.saveUnsupported(Sets.newHashSet(result.getCancelableDevices()),
                    result.getExecution().getId(),
                    CANCELED.getCreUnsupportedType());
            executionDao.saveOrUpdate(execution);
        }
    }
   
    @Override
    public List<CollectionActionResult> getCachedResults(List<Integer> cacheKeys) {
        return cache.getAllPresent(cacheKeys).values().asList();
    }
    
    @Override
    public void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result,
            List<? extends YukonPao> devices) {
        addUnsupportedToResult(detail, result,  devices, null);
    }
    
    @Override
    public void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result,
            List<? extends YukonPao> devices, String deviceErrorText) {
        if(result.getExecution() != null) {
            addUnsupportedToResult(detail, result, result.getExecution().getId(), devices, deviceErrorText);
        }
    }

    @Override
    public void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result, int execId,
            List<? extends YukonPao> devices, String deviceErrorText) {
        if (!devices.isEmpty()) {
            log.debug("Adding unsupported devices:{} detail:{} cacheKey:{}", devices.size(), detail, result.getCacheKey());
            result.addDevicesToGroup(detail, devices, logService.buildLogDetails(devices, detail, deviceErrorText));
            commandRequestExecutionResultDao.saveUnsupported(Sets.newHashSet(devices), execId,
                    detail.getCreUnsupportedType());
        }
    }

    /**
     * 1. Saves result to the database
     * 2. Logs the result info
     * 3. Caches the result for 7 days
     */
    private void saveAndLogResult(CollectionActionResult result, LiteYukonUser user) {
        collectionActionDao.createCollectionAction(result, user);
        eventLogHelper.log(result);
        log.info("Created new collection action {}", result);
        cache.put(result.getCacheKey(), result);
    }
}
