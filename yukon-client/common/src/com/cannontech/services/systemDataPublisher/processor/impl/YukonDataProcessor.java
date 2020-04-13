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
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

@Service
public class YukonDataProcessor extends SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
    private static final Logger log = YukonLogManager.getLogger(YukonDataProcessor.class);

    @Override
    public void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry) {
        executor.scheduleAtFixedRate(() -> {
            buildAndPublishSystemData(entry.getValue());
        }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
    }

    @Override
    public SystemData buildSystemData(DictionariesField dictionariesField) {
        SystemData systemData = null;
        if (StringUtils.isNotEmpty(dictionariesField.getSource())) {
            List<Map<String, Object>> queryResult = null;
            try {
                queryResult = systemDataPublisherDao.getSystemData(dictionariesField);
                systemData = SystemDataProcessorHelper.processQueryResult(dictionariesField, queryResult);
            } catch (Exception e) {
                log.debug("Error while executing query." + e);
            }
        } else {
            // TODO : Here we need to call corresponding DAO for Yukon field against YUK-21731
            systemData = new SystemData();
            systemData.setFieldName(dictionariesField.getField());
            systemData.setFieldValue(dictionariesField.getSource());
            systemData.setIotDataType(dictionariesField.getIotType());
            systemData.setTimestamp(new DateTime());
        }
        return systemData;
    }
}
