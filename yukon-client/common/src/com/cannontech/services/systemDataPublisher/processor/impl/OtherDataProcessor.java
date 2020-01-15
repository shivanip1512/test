package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.dao.impl.SystemDataProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

@Service
public class OtherDataProcessor extends SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    @Override
    public SystemData buildSystemData(DictionariesField dictionariesField) {
        return SystemDataProcessorHelper.processOtherData(dictionariesField);
    }

    @Override
    public void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry) {
        executor.scheduleAtFixedRate(() -> {
            buildAndPublishSystemData(entry.getValue());
        }, 0, entry.getKey().getHours(), TimeUnit.HOURS);
    }

}
