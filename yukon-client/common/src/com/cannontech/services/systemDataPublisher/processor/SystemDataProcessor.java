package com.cannontech.services.systemDataPublisher.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisherService;
import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

public abstract class SystemDataProcessor {

    @Autowired private SystemDataPublisherService systemDataPublisherService;

    private static final Logger log = YukonLogManager.getLogger(SystemDataProcessor.class);
    /**
     * Create scheduler based on different frequency values. Will find out distinct frequency values 
     * and based on that will create the scheduler which will further query the database and form the 
     * JSON to be published on topic.
     */
    public void process(List<DictionariesField> dictionaries) {
        Map<SystemDataPublisherFrequency, List<DictionariesField>> dictionariesByFrequency = groupDictionariesByFrequency(dictionaries);
        if (!dictionariesByFrequency.isEmpty()) {
            processDictionariesFields(dictionariesByFrequency);
        }
    }

    /**
     * Build SystemData by executing the queries from database. Based on field name we are building the arguments
     * needed for the query. Process the query result to get the field value.
     */
    public abstract SystemData buildSystemData(DictionariesField dictionariesField);

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
     * Build SystemData by querying the database. Once the data is build we will publish the data to 
     * message broker one by one.
     */
    public void buildAndPublishSystemData(List<DictionariesField> dictionariesByFrequency) {
        for (DictionariesField dict : dictionariesByFrequency) {
            if (dict.getSource() != null) {
                SystemData systemData = buildSystemData(dict);
                if (systemData != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Publishing system data to topic " + systemData);
                    }
                    publishSystemData(systemData);
                }
            }
        }
    }

    /**
     * Create and run scheduler based on dictionariesByFrequency map values. Based on each map entity,
     * create a scheduler. The scheduler task will consist of fetching the data from database and building
     * the SystemData Object which will further be published on the topic.
     */
    public abstract void runScheduler(Entry<SystemDataPublisherFrequency, List<DictionariesField>> entry);

    /**
     * Publish the System data to topic, the interested service will listen to this topic and further push 
     * the data to other system.
     */
    private void publishSystemData(SystemData systemData) {
        systemDataPublisherService.publish(systemData);
    }

    /** 
     * Group dictionaries by frequency values. This will give us the time period to create and run scheduler.
     * So if we have number of field having same frequency, then these fields will be processed collectively
     * by one scheduler.
     */
    private Map<SystemDataPublisherFrequency, List<DictionariesField>> groupDictionariesByFrequency(List<DictionariesField> dictionaries) {
        return dictionaries.stream()
                            .filter(dict -> dict.getFrequency() != null)
                            .collect(Collectors.groupingBy(dict -> dict.getFrequency(),
                                    LinkedHashMap::new, 
                                    Collectors.toCollection(ArrayList::new)));
    }
}
