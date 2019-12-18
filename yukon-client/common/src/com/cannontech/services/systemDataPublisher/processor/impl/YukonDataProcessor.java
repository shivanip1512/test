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
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

@Service
public class YukonDataProcessor implements SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    private static final Logger log = YukonLogManager.getLogger(YukonDataProcessor.class);
    
    @Override
    public void process(List<DictionariesField> dictionaries) {
        Map<SystemDataPublisherFrequency, List<DictionariesField>> dictionariesByFrequency = groupDictionariesByFrequency(dictionaries);
        if (!dictionariesByFrequency.isEmpty()) {
            processDictionariesFields(dictionariesByFrequency);
        }
    }

    /**
     * Process Dictionaries field based on Frequency. For Frequency as OnStartupOnly we don't need the 
     * scheduler and publish only during service startup while for other frequency scheduler will be
     * created to publish data
     */
    private void processDictionariesFields(Map<SystemDataPublisherFrequency, List<DictionariesField>> dictionariesByFrequency) {
        for (Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry : dictionariesByFrequency.entrySet()) {
            if (entry.getKey() == SystemDataPublisherFrequency.ON_STARTUP_ONLY) {
                buildAndPublishSystemData(entry.getValue());
            } else {
                runScheduler(entry);
            }
        }
    }

    /**
     * Create and run scheduler based on dictionariesByFrequency map values. Based on each map entity,
     * create a scheduler. The scheduler task will consist of fetching the data from database and building
     * the SystemData Object which will further be published on the topic.
     */
    private void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry) {
        executor.scheduleAtFixedRate(() -> {
            buildAndPublishSystemData(entry.getValue());
        }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
    }

    /**
     * Build SystemData by querying the database. Once the data is build we will publish the data to 
     * message broker one by one.
     */
    private void buildAndPublishSystemData(List<DictionariesField> dictionariesByFrequency) {
        for (DictionariesField dict : dictionariesByFrequency) {
            if (dict.getSource() != null) {
                SystemData systemData = buildSystemData(dict);
                if (systemData != null) {
                    publishSystemData(systemData);
                }
            }
        }
    }

    @Override
    public SystemData buildSystemData(DictionariesField dictionariesField) {
        List<Map<String, Object>> queryResult = null;
        SystemData systemData = null;
        try {
            queryResult = systemDataPublisherDao.getSystemData(dictionariesField);
            systemData = SystemDataProcessorHelper.processQueryResult(dictionariesField, queryResult);

        } catch (Exception e) {
            log.debug("Error while executing query." + e);
        }
        return systemData;
    }

    @Override
    public void publishSystemData(SystemData systemData) {
        if (log.isDebugEnabled()) {
            log.debug("Publishing system data to topic " + systemData);
        }
        // TODO Publish to topic changes will be done in YUK-21098
    }
}
