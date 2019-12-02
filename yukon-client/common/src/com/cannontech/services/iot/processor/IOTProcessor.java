package com.cannontech.services.iot.processor;

import java.util.List;

import com.cannontech.services.iot.yaml.model.DictionariesField;

public interface IOTProcessor {

    /**
     * Create scheduler based on different frequency values. Will find out distinct frequency values 
     * and based on that will create the scheduler which will further query the database and form the 
     * json to be published on topic.
     */
    void execute(List<DictionariesField> dictionaries);

}
