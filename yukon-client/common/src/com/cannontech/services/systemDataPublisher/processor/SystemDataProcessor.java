package com.cannontech.services.systemDataPublisher.processor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.SystemDataPublisherFrequency;

public interface SystemDataProcessor {

    /**
     * Create scheduler based on different frequency values. Will find out distinct frequency values 
     * and based on that will create the scheduler which will further query the database and form the 
     * JSON to be published on topic.
     */
    void process(List<DictionariesField> dictionaries);

    /**
     * Build SystemData by executing the queries from database. Based on field name we are building the arguments
     * needed for the query. Process the query result to get the field value.
     */
    SystemData buildSystemData(DictionariesField dictionariesField);

    /**
     * Publish the System data to topic, the interested service will listen to this topic and further push 
     * the data to other system.
     */
    void publishSystemData(SystemData systemData);

    /** 
     * Group dictionaries by frequency values. This will give us the time period to create and run scheduler.
     * So if we have number of field having same frequency, then these fields will be processed collectively
     * by one scheduler.
     */
    default Map<SystemDataPublisherFrequency, List<DictionariesField>> groupDictionariesByFrequency(List<DictionariesField> dictionaries) {
        return dictionaries.stream()
                            .filter(dict -> dict.getFrequency() != null)
                            .collect(Collectors.groupingBy(dict -> dict.getFrequency(),
                                    LinkedHashMap::new, 
                                    Collectors.toCollection(ArrayList::new)));
    }
}
