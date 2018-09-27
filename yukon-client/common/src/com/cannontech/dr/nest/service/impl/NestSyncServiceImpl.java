package com.cannontech.dr.nest.service.impl;

import static org.joda.time.Instant.now;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.dr.nest.dao.NestDao;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;
import com.cannontech.dr.nest.model.NestSyncI18nKey;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.model.NestSyncType;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;
import com.cannontech.yukon.IDatabaseCache;

public class NestSyncServiceImpl implements NestSyncService{

    public static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_SYNC = Duration.standardMinutes(15);
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    
    @Autowired private IDatabaseCache dbCache;
    private boolean syncInProgress = false;
    public static final String EMPTY_ROW = "***";
    private static final Logger log = YukonLogManager.getLogger(NestSyncServiceImpl.class);
    @Autowired private NestDao nestDao;
        
    @Override
    public void sync(boolean forceSync) {
        
        if(!runSync(forceSync)) {
            return;
        }
        
        syncInProgress = true;

        log.info("Nest sync started");
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        NestSync sync = new NestSync();
        nestDao.saveSyncInfo(sync);
        List<NestExisting> existing = nestCommunicationService.downloadExisting();
        if (!existing.isEmpty()) {
            processGroups(existing, sync.getId());
                //processAccounts
                //processGroupChanges
        }
        sync.setStopTime(new Instant());
        nestDao.saveSyncInfo(sync);
        
        syncInProgress = false;
        log.info("Nest sync finished");
    }
    
    private boolean runSync(boolean forceSync) {
        if(forceSync) {
            return true;
        }
        if(syncInProgress) {
            return false;
        }
        Instant lastSyncTime = persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME);
        if (lastSyncTime == null || now().isAfter(lastSyncTime.plus(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC))) {
            return true;
        }
        return false;
    }

    private void processGroups(List<NestExisting> existing, int syncId) {
        List<String> groupsInNest = parseGroupsFromTheNestFile(existing);
        
        List<String> groupsInYukon = dbCache.getAllLMGroups().stream()
                    .filter(group -> group.getPaoType() == PaoType.LM_GROUP_NEST)
                    .map(group -> group.getPaoName())
                    .collect(Collectors.toList());
        
        List<String> groupsToCreate = new ArrayList<>(groupsInNest);
        groupsToCreate.removeAll(groupsInYukon);
        createGroups(groupsToCreate, syncId);
        
        List<String> groupsOnlyInYukon = new ArrayList<>(groupsInYukon);
        groupsInNest.removeAll(groupsInNest);
        logGroupDiscrepancies(groupsOnlyInYukon, syncId);  
    }
    
    private List<String> parseGroupsFromTheNestFile(List<NestExisting> existing) {
        List<String> groupsInNest = existing.stream()
                .filter(row -> Strings.isNotEmpty(row.getGroup()) && !row.getGroup().equals(EMPTY_ROW))
                .map(row -> row.getGroup())
                .distinct()
                .collect(Collectors.toList());
        return groupsInNest;
    }
    
    private void createGroups(List<String> groups, int syncId) {
        if(!groups.isEmpty()) {
            log.debug("Group found in Nest by not in Yukon: " + groups);
        }
        List<NestSyncDetail> details = groups.stream().map(group -> {
            NestSyncDetail detail = new NestSyncDetail();
            detail.setType(NestSyncType.AUTO);
            detail.setSyncId(syncId);
            detail.setReasonKey(NestSyncI18nKey.FOUND_GROUP_ONLY_IN_NEST);
            detail.setReasonValue(group);
            detail.setActionKey(NestSyncI18nKey.AUTO_CREATED_GROUP_IN_YUKON);
            detail.setActionValue(group);
            return detail;
            }).collect(Collectors.toList());
        nestDao.saveSyncDetails(details);
    }
    
    private void logGroupDiscrepancies(List<String> groups, int syncId) {
        if(!groups.isEmpty()) {
            log.debug("Group found in Yukon by not in Nest: " + groups);
        }
        
        //we do not have Nest groups in Yukon yet. Hardcoded for test.
        groups.add("D");
        
        List<NestSyncDetail> details = groups.stream().map(group -> {
            NestSyncDetail detail = new NestSyncDetail();
            detail.setType(NestSyncType.MANUAL);
            detail.setSyncId(syncId);
            detail.setReasonKey(NestSyncI18nKey.FOUND_GROUP_ONLY_IN_YUKON);
            detail.setReasonValue(group);
            detail.setActionKey(NestSyncI18nKey.MANUALLY_DELETE_GROUP_FROM_YUKON);
            detail.setActionValue(group);
            return detail;
            }).collect(Collectors.toList());
        nestDao.saveSyncDetails(details);
    }
    
    @Override
    public NestSyncTimeInfo getSyncTimeInfo() {
        NestSyncTimeInfo info = new NestSyncTimeInfo();
        info.setSyncTime(persistedSystemValueDao.getInstantValue(PersistedSystemValueKey.NEST_SYNC_TIME));
        info.setSyncInProgress(syncInProgress);
        if(info.getNextSyncTime() != null) {
            info.setNextSyncTime(info.getNextSyncTime().plus(MINUTES_TO_WAIT_BEFORE_NEXT_SYNC));
        }
        return info;
    }
}
