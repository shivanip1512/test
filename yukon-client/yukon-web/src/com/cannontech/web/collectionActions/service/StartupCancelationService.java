package com.cannontech.web.collectionActions.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.dao.CollectionActionDao;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.user.YukonUserContext;

/**
 * Cancels started collection actions with command request execution entry on start-up
 */
public class StartupCancelationService {

    private static final Logger log = YukonLogManager.getLogger(StartupCancelationService.class);
    private static int MIN_TO_WAIT = 1;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    @Autowired private CollectionActionDao collectionActionDao;
    @Autowired private CollectionActionService collectionActionService;
    
    @PostConstruct
    public void init() {
        scheduledExecutorService.schedule(() -> {
            try {
                List<CollectionActionResult> results =  collectionActionDao.loadIncompeteResultsFromDb();
                log.info("Attempting to terminate {} Collection Actions", results.size());
                results.forEach(result -> {
                    collectionActionService.cancel(result, YukonUserContext.system.getYukonUser());
                    collectionActionService.removeResultFromCache(result.getCacheKey());
                });
                log.info("Terminated {} Collection Actions", results.size());
            } catch (Exception e) {
                log.error("Failed to cancel started collection actions", e);
            }
        }, MIN_TO_WAIT, TimeUnit.MINUTES);
    }
}
