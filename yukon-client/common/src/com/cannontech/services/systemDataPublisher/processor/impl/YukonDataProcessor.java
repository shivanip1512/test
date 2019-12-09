package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class YukonDataProcessor implements SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    private static final Logger log = YukonLogManager.getLogger(YukonDataProcessor.class);
    
    @Override
    public void process(List<DictionariesField> dictionaries) {
        Map<Integer, List<DictionariesField>> dictionariesByFrequency = groupDictionariesByFrequency(dictionaries);
        runScheduler(dictionariesByFrequency);
        
    }

    /** 
     * Group dictionaries by frequency values. This will give us the time period to create and run scheduler.
     * So if we have number of field having same frequency, then these fields will be processed collectively
     * by one scheduler.
     */
    private Map<Integer, List<DictionariesField>> groupDictionariesByFrequency(List<DictionariesField> dictionaries) {
        return dictionaries.stream()
                            .collect(Collectors.groupingBy(dict -> dict.getFrequency(),
                                    LinkedHashMap::new, 
                                    Collectors.toCollection(ArrayList::new)));
    }


    /**
     * Create and run scheduler based on dictionariesByFrequency map values. Based on each map entity,
     * create a scheduler. The scheduler task will consist of fetching the data from database and building
     * the JSON which will further be published on the topic.
     */
    public void runScheduler(Map<Integer, List<DictionariesField>> dictionariesByFrequency) {
        
        dictionariesByFrequency.entrySet()
                               .forEach(entity -> {
                                   executor.scheduleAtFixedRate(() -> 
                                   {
                                       // TODO - Changes for YUK-21022 to fetch data from database and prepare JSON.
                                       log.info("Running Executors in every " + entity.getKey() + " minutes."); // Can be removed once DAO layer is implemented.
                                       }, 0, entity.getKey(), TimeUnit.MINUTES); 
                               });
    }
}
