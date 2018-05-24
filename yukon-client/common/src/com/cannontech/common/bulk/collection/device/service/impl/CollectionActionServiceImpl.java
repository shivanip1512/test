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
    @Autowired private CollectionActionLoggingHelperService logHelper;
    
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
    
    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult cachedResult = cache.getIfPresent(key);
        //make sure result didn't complete before canceling
        if (cachedResult.isCancelable()) {
            log.debug("Attemting to cancel result for " + key);
            if (cachedResult != null) {
                Optional<CollectionActionCancellationService> service =
                    cancellationService.stream().filter(s -> s.isCancellable(cachedResult.getAction())).findFirst();
                if (service.isPresent()) {
                    log.debug("Using " + service.get().getClass() + " to cancel result for " + key);
                    log.debug("Result to be canceled:");
                    cachedResult.log();
                    service.get().cancel(cachedResult.getCacheKey(), user);
                }
            }
        } else {
            log.debug("Attemting to cancel result for " + key
                + " failed. The results was already completed with the status " + cachedResult.getStatus());
            cachedResult.log();
        }
    }
        
    @Transactional
    @Override
    public void updateResult(CollectionActionResult result, CommandRequestExecutionStatus status) {
        // If one execution failed and one succeeded (PLC or RFN), consider the execution failed.
        log.debug("Cache key:" + result.getCacheKey() + " updating result status to " + status);
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
        
        if(result.getStatus() == CommandRequestExecutionStatus.CANCELLED) {
            addUnsupportedToResult(CANCELED, result, result.getCancelableDevices());
        }
        result.setStopTime(new Instant(stopTime));
        try {
            logHelper.logActionCompletedCanceled(result);
        } catch (ClassNotFoundException e) {
            log.warn("CollectionAction needs to be added to CollectionActionLoggingHelperServiceImpl to be logged", e);
        }
    }

    @Override
    public CollectionActionResult getResult(int key) {
        return cache.getIfPresent(key) != null ? getCachedResult(key) : getDbResult(key);
    }
    
    @Override
    public CollectionActionResult getCachedResult(int key) {
        return cache.getIfPresent(key);
    }
    
    @Override
    public void clearCache() {
        cache.invalidateAll();
    }
    
    @Override
    public CollectionActionResult getDbResult(int key) {
        CollectionActionResult result = collectionActionDao.loadResultFromDb(key);
        result.setLogger(log);
        return result;
    }
    
    @Override
    public List<CollectionActionResult> getCachedResults(List<Integer> cacheKeys) {
        return cache.getAllPresent(cacheKeys).values().asList();
    }
    
    @Override
    public void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result,
            List<? extends YukonPao> devices) {
        addUnsupportedToResult(detail, result, result.getExecution().getId(), devices);
    }

    @Override
    public void addUnsupportedToResult(CollectionActionDetail detail, CollectionActionResult result, int execId,
            List<? extends YukonPao> devices) {
        if (!devices.isEmpty()) {
            log.debug("Adding unsupported devices:" + devices.size() + " detail:" + detail + " cacheKey:"
                + result.getCacheKey());
            result.addDevicesToGroup(detail, devices, logService.buildLogDetails(devices, detail));
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
        log.debug("Created new collecton action result:");
        try {
            logHelper.logActionInitiated(result);
        } catch (ClassNotFoundException e) {
            log.warn("CollectionAction needs to be added to CollectionActionLoggingHelperServiceImpl to be logged", e);
        }
        result.setLogger(log);
        result.log();
        cache.put(result.getCacheKey(), result);
        
    }
}
