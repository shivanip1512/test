package com.cannontech.web.common.service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.service.CollectionActionService;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;

public class StartupCancelationService {

    private static final Logger log = YukonLogManager.getLogger(StartupCancelationService.class);
    private static int MIN_TO_WAIT = 1;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    @Autowired private CollectionActionService collectionActionService;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    
    @PostConstruct
    public void init() {
        scheduledExecutorService.schedule(() -> {
            try {
                int total = collectionActionService.terminate();
                int inProgressCount = deviceConfigurationDao.getInProgressCount();
                //Change DeviceConfigState status from "In Progress" to "Failure"
                deviceConfigurationDao.failInProgressDevices();

                log.info("Terminated Collection Actions:{} DeviceConfigState in progress:{}", total, inProgressCount);
            } catch (Exception e) {
                log.error(e);
            }
        }, MIN_TO_WAIT, TimeUnit.MINUTES);
    }
}
