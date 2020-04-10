package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

@Service
public class YukonDataProcessor extends SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    private static final Logger log = YukonLogManager.getLogger(YukonDataProcessor.class);

    @Override
    public void runScheduler(Entry<SystemDataPublisherFrequency, List<CloudDataConfiguration>> entry) {
        executor.scheduleAtFixedRate(() -> {
            buildAndPublishSystemData(entry.getValue());
        }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
    }

    @Override
    public SystemData buildSystemData(CloudDataConfiguration cloudDataConfiguration) {
        List<Map<String, Object>> queryResult = null;
        SystemData systemData = null;
        try {
            queryResult = systemDataPublisherDao.getSystemData(cloudDataConfiguration);
            systemData = SystemDataProcessorHelper.processQueryResult(cloudDataConfiguration, queryResult);

        } catch (Exception e) {
            log.debug("Error while executing query." + e);
        }
        return systemData;
    }
}
