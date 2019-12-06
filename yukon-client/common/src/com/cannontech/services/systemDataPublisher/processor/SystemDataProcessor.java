package com.cannontech.services.systemDataPublisher.processor;

import java.util.List;

import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public interface SystemDataProcessor {

    /**
     * Create scheduler based on different frequency values. Will find out distinct frequency values 
     * and based on that will create the scheduler which will further query the database and form the 
     * JSON to be published on topic.
     */
    void process(List<DictionariesField> dictionaries);

}
