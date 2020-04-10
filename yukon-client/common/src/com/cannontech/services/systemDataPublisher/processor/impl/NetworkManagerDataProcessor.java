package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.context.NetworkManagerDBConfig;
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

@Service
public class NetworkManagerDataProcessor extends SystemDataProcessor {

    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    @Autowired private NetworkManagerDBConfig networkManagerDBConfig;
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    private static final Logger log = YukonLogManager.getLogger(NetworkManagerDataProcessor.class);

    @Override
    public void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry) {

        if (networkManagerDBConfig.isNetworkManagerDBConnectionConfigured()) {
            executor.scheduleAtFixedRate(() -> {
                buildAndPublishSystemData(entry.getValue());
            }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
        } else {
            log.info("Network Manager DB configurations not found. Not able to publish data to Cloud Service");
        }
    }

    @Override
    public SystemData buildSystemData(DictionariesField dictionariesField) {
        SystemData nmSystemData = null;
        if (StringUtils.isNotEmpty(dictionariesField.getSource())) {
            List<Map<String, Object>> queryResult = null;
            try {
                queryResult = systemDataPublisherDao.getNMSystemData(dictionariesField);
                nmSystemData = SystemDataProcessorHelper.processQueryResult(dictionariesField, queryResult);

            } catch (Exception e) {
                log.debug("Error while executing query." + e);
            }
        }
        return nmSystemData;
    }

}
