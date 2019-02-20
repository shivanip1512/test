package com.cannontech.dr.itron.service.impl;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.ScheduledExecutor;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.dr.itron.service.ItronPeriodicDataCollectionService;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class ItronPeriodicDataCollectionServiceImpl implements ItronPeriodicDataCollectionService {

    private static final Logger log = YukonLogManager.getLogger(ItronPeriodicDataCollectionServiceImpl.class);

    @Autowired @Qualifier("main") private ScheduledExecutor scheduledExecutor;
    @Autowired GlobalSettingDao settingsDao;
    @Autowired AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired PaoDao paoDao;

    @PostConstruct
    public void init() {
        paoDao.getExistingPaoTypes().stream()
            .filter(PaoType::isItron)
            .findAny()
            .ifPresentOrElse(type -> {
                int hours = settingsDao.getInteger(GlobalSettingType.ITRON_HCM_DATA_COLLECTION_HOURS);
                // Schedule the first run of collectData() 1 minute after the Itron services init.
                scheduledExecutor.scheduleAtFixedRate(this::collectData, 1, hours*60, TimeUnit.MINUTES);
                log.info("Scheduled data collection");
            },
            () -> log.info("No Itron devices, not scheduling data collection"));
        
        log.info("Initialized ItronPeriodicDataCollectionService");
    }
    
    @Override
    public void collectData() {
        log.info("Collecting data");
        
        var csv = requestRawDataCsv();
        
        var pointData = parseCsvToPointData(csv);
        
        sendPointData(pointData);
        
        log.info("Point data sent");
    }

    private Object requestRawDataCsv() {
        log.debug("Requesting raw data CSV from Itron");
        
        return null;
    }

    private Iterable<PointData> parseCsvToPointData(Object csv) {
        log.debug("Parsing raw data CSV into point data");
        
        return Collections.emptyList();
    }

    private void sendPointData(Iterable<PointData> pointDatas) {
        log.debug("Sending Itron point data to Dispatch");
        
        asyncDynamicDataSource.putValues(pointDatas);
    }
}
