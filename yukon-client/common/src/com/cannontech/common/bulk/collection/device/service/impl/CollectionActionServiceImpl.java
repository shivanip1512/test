package com.cannontech.common.bulk.collection.device.service.impl;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.bulk.collection.device.DeviceGroupCollectionHelper;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionAction;
import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.bulk.collection.device.model.CollectionActionInputs;
import com.cannontech.common.bulk.collection.device.model.CollectionActionProcess;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.service.TemporaryDeviceGroupService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

public class CollectionActionServiceImpl implements CollectionActionService{
    
    @Autowired private TemporaryDeviceGroupService tempGroupService;
    @Autowired private DeviceGroupMemberEditorDao editorDao;
    @Autowired private DeviceGroupCollectionHelper groupHelper;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private CollectionActionDao collectionActionDao;
    
    private Cache<Integer, CollectionActionResult> cache =
        CacheBuilder.newBuilder().expireAfterAccess(7, TimeUnit.DAYS).build();
    
    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, CollectionActionInputs inputs,
            CommandRequestExecution execution, LiteYukonUser user) {
        CollectionActionResult result =
            new CollectionActionResult(action, inputs, execution, editorDao, tempGroupService, groupHelper);
        collectionActionDao.createCollectionAction(result, user);
        cache.put(result.getCacheKey(), result);
        return result;
    }

    @Transactional
    @Override
    public CollectionActionResult createResult(CollectionAction action, CollectionActionInputs inputs,
            LiteYukonUser user) {
        return createResult(action, inputs, null, user);
    }

    @Override
    public CollectionActionResult getResult(int key) {
        CollectionActionResult cachedResult = cache.getIfPresent(key);
        return cachedResult == null ? collectionActionDao.loadResultFromDb(key) : cachedResult;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Override
    public void printResult(CollectionActionResult result) {  
        System.out.println("Key="+result.getCacheKey());
        System.out.println("Inputs");
        System.out.println("Action:" + result.getAction());
        result.getInputs().getInputs().forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("Devices:" + result.getInputs().getCollection().getDeviceCount());

        System.out.println("Results");
        System.out.println("-Display cancel button:" + result.getAction().isCancelable());
        System.out.println("-Display link to cre results:" + (result.getExecution() != null));
        System.out.println("Progress bar=" + result.getCounts().getPercentProgress() + "%");

        result.getAction().getDetails().forEach(detail -> {
            System.out.println(
                "------" + detail + "    device count=" + result.getDeviceCollection(detail).getDeviceCount() + "   "
                    + result.getCounts().getPercentage(detail) + "%");
        });
        System.out.println("status="+result.getStatus());
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

        editorDao.addDevices(tempGroup, subset);

        CollectionActionInputs inputs = new CollectionActionInputs(devices, userInputs);

        CollectionActionResult result = createResult(action, inputs, new LiteYukonUser(1, String.valueOf((char)(rand.nextInt(26)+'a'))));

        if (status == CommandRequestExecutionStatus.STARTED || status == CommandRequestExecutionStatus.CANCELING) {
            stopTime = null;
        } else {
            subset.forEach(meter -> {
                int randomIndex = rand.nextInt(action.getDetails().size());
                CollectionActionDetail bucket = Lists.newArrayList(action.getDetails()).get(randomIndex);
                result.addDevicesToGroup(bucket, meter);
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
        collectionActionDao.updateCollectionActionStatus(result.getCacheKey(), status, stopTime);
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
