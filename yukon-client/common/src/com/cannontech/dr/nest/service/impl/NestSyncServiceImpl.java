package com.cannontech.dr.nest.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.PersistedSystemValueDao;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestSyncTimeInfo;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;

public class NestSyncServiceImpl implements NestSyncService{

    public static final Duration MINUTES_TO_WAIT_BEFORE_NEXT_SYNC = Duration.standardMinutes(15);
    @Autowired private NestCommunicationService nestCommunicationService;
    @Autowired private PersistedSystemValueDao persistedSystemValueDao;
    
    private boolean syncInProgress = false;
    
  
    @Override
    public void sync() {
        syncInProgress = true;
        persistedSystemValueDao.setValue(PersistedSystemValueKey.NEST_SYNC_TIME, new Instant());
        Date date = new Date();
        // List<NestExisting> existing = nestCommunicationService.downloadExisting(date);
        List<NestExisting> modified = new ArrayList<>();
        // not tested
        // nestCommunicationService.uploadExisting(existing, date);
        syncInProgress = false;
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
