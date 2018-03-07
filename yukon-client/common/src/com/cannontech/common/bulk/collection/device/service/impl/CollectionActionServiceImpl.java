package com.cannontech.common.bulk.collection.device.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionProcess;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionCancellationService;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class CollectionActionServiceImpl implements CollectionActionService {
    
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private CommandRequestExecutionDao executionDao;
    @Autowired private List<CollectionActionCancellationService> cancellationService;
    
    private final Logger log = YukonLogManager.getLogger(CollectionActionServiceImpl.class);
    
    private Cache<Integer, CollectionActionResult> cache =
        CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).build();
    
    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, CommandRequestType commandRequestType, DeviceRequestType deviceRequestType,
            LiteYukonUser user) {
        CommandRequestExecution execution =
            executionDao.createStartedExecution(commandRequestType, deviceRequestType, 0, user);
        CollectionActionResult result = new CollectionActionResult(action, collection.getDeviceList(), inputs,
            execution, editorDao, tempGroupService, groupHelper);
        result.setExecution(execution);
        saveAndLogResult(result, user);
        return result;
    }

    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, LinkedHashMap<String, String> inputs,
            DeviceCollection collection, LiteYukonUser user) {
        CollectionActionResult result = new CollectionActionResult(action, collection.getDeviceList(), inputs,
            editorDao, tempGroupService, groupHelper);
        saveAndLogResult(result, user);
        return result;
    }
    
    @Override
    public void cancel(int key, LiteYukonUser user) {
        CollectionActionResult cachedResult = cache.getIfPresent(key);
        if(cachedResult != null) {
            Optional<CollectionActionCancellationService> service =
                cancellationService.stream().filter(s -> s.isCancellable(cachedResult.getAction())).findFirst();
            if(service.isPresent()) {
                service.get().cancel(cachedResult.getCacheKey(), user);
            }
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
        result.setStopTime(new Instant(stopTime));
    }

    @Override
    public CollectionActionResult getResult(int key) {
        CollectionActionResult cachedResult = cache.getIfPresent(key);
        return cachedResult == null ? collectionActionDao.loadResultFromDb(key) : cachedResult;
    }
    
    private void saveAndLogResult(CollectionActionResult result, LiteYukonUser user) {
        collectionActionDao.createCollectionAction(result, user);
        log.debug("Created new collecton action result:");
        result.log(log);
        cache.put(result.getCacheKey(), result);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public void printResult(CollectionActionResult result) {  
        result.log(log);
    }

    @Override
    public CollectionActionResult getRandomResult(int numberOfDevices, LinkedHashMap<String, String> userInputs,
            Instant stopTime, CommandRequestExecutionStatus status, CollectionAction action) {

        if (numberOfDevices < 1 || action == null || userInputs == null) {
            new Exception();
        }

        StoredDeviceGroup tempGroup = tempGroupService.createTempGroup();
        DeviceCollection devices = groupHelper.buildDeviceCollection(tempGroup);

        List<SimpleMeter> meters = Lists.newArrayList(dbCache.getAllMeters().values());
        Set<SimpleMeter> subset = new HashSet<>();

        Random rand = new Random();
        while (true) {
            int randomIndex = rand.nextInt(meters.size());
            SimpleMeter randomMeter = meters.get(randomIndex);
            subset.add(randomMeter);
            if (subset.size() == numberOfDevices) {
                break;
            }
        }



        CollectionActionResult result = createResult(action, userInputs, devices,
            new LiteYukonUser(1, String.valueOf((char) (rand.nextInt(26) + 'a'))));

        if (status == CommandRequestExecutionStatus.STARTED || status == CommandRequestExecutionStatus.CANCELING) {
            stopTime = null;
        } else {
            subset.forEach(meter -> {
                int randomIndex = rand.nextInt(action.getDetails().size());
                CollectionActionDetail bucket = Lists.newArrayList(action.getDetails()).get(randomIndex);
                result.addDeviceToGroup(bucket, meter);
                if (result.getAction().getProcess() == CollectionActionProcess.DB) {
                    if (bucket == CollectionActionDetail.SUCCESS) {
                        collectionActionDao.updateDbRequestStatus(result.getCacheKey(),
                            meter.getPaoIdentifier().getPaoId(), CommandRequestExecutionStatus.COMPLETE);
                    } else {
                        collectionActionDao.updateDbRequestStatus(result.getCacheKey(),
                            meter.getPaoIdentifier().getPaoId(), CommandRequestExecutionStatus.FAILED);
                    }
                }
            });
        }
        collectionActionDao.updateCollectionActionStatus(result.getCacheKey(), status, stopTime.toDate());
        result.setStatus(status);
        result.setStopTime(stopTime);
        printResult(result);
        return result;
    }
   
    @Transactional
    @Override
    public void loadLotsOfDataForNonCreCollectionActions(LinkedHashMap<String, String> userInputs) {
        List<CommandRequestExecutionStatus> statuses = Lists.newArrayList(CommandRequestExecutionStatus.values());
        statuses.remove(CommandRequestExecutionStatus.IN_PROGRESS);
        List<CollectionAction> actions = Lists.newArrayList(CollectionAction.values()).stream().filter(
            a -> a.getProcess() == CollectionActionProcess.DB).collect(Collectors.toList());
        for (int i = 0; i < 60; i++) {
            Random rand = new Random();
            DateTime stopTime = new DateTime().plusDays(rand.nextInt(6));
            System.out.println("---------------------------" + (i + 1) + " Creating new result");
            getRandomResult(60, userInputs, stopTime.toInstant(), statuses.get(rand.nextInt(statuses.size())),
                actions.get(rand.nextInt(actions.size())));
        }
    }
    

    @Override
    public void compareCacheAndGB(int key) {
        CollectionActionResult cachedResult = cache.getIfPresent(key);
        
        if(cachedResult == null) {
            System.out.println(key + " not in cache");
        }else {
            System.out.println("---------CACHE----------");
            printResult(cachedResult);
        }
        System.out.println("---------DB----------");
        printResult(collectionActionDao.loadResultFromDb(key));
    }
}
