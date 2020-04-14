package com.cannontech.services.systemDataPublisher.service;

import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

public interface CloudDataConfigurationPublisherService {

    /**
     * Publish the CloudDataConfiguration data to Message Broker with a specified Topic.
     */
    void publish(CloudDataConfiguration cloudDataConfiguration);

}
