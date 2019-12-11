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
import com.cannontech.services.systemDataPublisher.dao.SystemDataPublisherDao;
import com.cannontech.services.systemDataPublisher.dao.impl.QueryProcessorHelper;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class YukonDataProcessor implements SystemDataProcessor {

    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;
    @Autowired private SystemDataPublisherDao systemDataPublisherDao;
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
                            .filter(dict -> dict.getFrequency().getHours() != null)
                            .collect(Collectors.groupingBy(dict -> dict.getFrequency().getHours(),
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
                                      buildAndPublishSystemData(entity.getValue());
                                   }, 0, entity.getKey(), TimeUnit.HOURS); 
                               });
    }

    /**
     * Build SystemData by querying the database. Once the data is build we will publish the data to 
     * message broker one by one.
     */
    private void buildAndPublishSystemData(List<DictionariesField> dictionariesByFrequency) {
                                    dictionariesByFrequency.stream()
                                                           .filter(dict -> dict.getSource() != null)
                                                           .map(dict -> buildSystemData(dict))
                                                           .filter(systemData -> systemData != null)
                                                           .forEach(systemData -> {
                                                               // Publish Data to Broker.
                                                           });
    }

    /**
     * Build SystemData by executing the queries from database. Based on field name we are building the arguments
     * needed for the query. Process the query result to get the field value.
     */
    private SystemData buildSystemData(DictionariesField dictionariesField) {
        List<Map<String, Object>> queryResult = null;
        SystemData systemData = null;
        try {
            List<Object> queryArgs = QueryProcessorHelper.getQueryArguments(dictionariesField);
            if (queryArgs.size() == 0) {
                queryResult = systemDataPublisherDao.executeQuery(dictionariesField);
            } else {
                queryResult = systemDataPublisherDao.executeParameterizedQuery(dictionariesField, queryArgs);
            }
            systemData = QueryProcessorHelper.processQueryResult(dictionariesField, queryResult);
            
        } catch (Exception e) {
            log.debug("Error while executing query." + e);
        }
        return systemData;
    }
}
