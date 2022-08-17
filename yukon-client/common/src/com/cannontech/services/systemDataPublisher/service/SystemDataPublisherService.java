package com.cannontech.services.systemDataPublisher.service;

import com.cannontech.services.systemDataPublisher.service.model.SystemData;

public interface SystemDataPublisherService {

    /**
     * Publish the data to Message Broker with a specified Topic.
     */
    public void publish(SystemData systemData);
}
