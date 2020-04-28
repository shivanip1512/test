package com.cannontech.services.systemDataPublisher.service;

import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

public interface CloudDataConfigurationsPublisherService {

    /**
     * Publish the CloudDataConfigurations data to Message Broker with a specified Topic.
     */
    void publish(CloudDataConfigurations cloudDataConfiguration);

}
