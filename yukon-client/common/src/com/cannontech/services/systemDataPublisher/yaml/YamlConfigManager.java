package com.cannontech.services.systemDataPublisher.yaml;

import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

public interface YamlConfigManager {

    /**
     * This method will return CloudDataConfigurations value. On initialize of YamlConfigManagerImpl class the yaml
     * configuration is read and will be returned.
     */
    CloudDataConfigurations getCloudDataConfigurations();

}
