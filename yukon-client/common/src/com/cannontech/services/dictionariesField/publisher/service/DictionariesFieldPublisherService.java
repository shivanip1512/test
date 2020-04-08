package com.cannontech.services.dictionariesField.publisher.service;

import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

public interface DictionariesFieldPublisherService {

    /**
     * Publish the DictionariesField data to Message Broker with a specified Topic.
     */
    public void publish(DictionariesField dictionariesField);

}
