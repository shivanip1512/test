package com.cannontech.services.systemDataPublisher.service;

import java.util.List;

import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

public interface CloudDataConfigurationsHandlerService {

    /**
     * Passes CloudDataConfiguration list to handler to handle cloudConfiguration.
     */
    void handleCloudConfiguration(List<CloudDataConfiguration> configurations);

}
