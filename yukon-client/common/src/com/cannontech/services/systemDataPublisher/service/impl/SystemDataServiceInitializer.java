package com.cannontech.services.systemDataPublisher.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.listener.CloudDataConfigurationsAdvisoryListener;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsPublisherService;
import com.cannontech.services.systemDataPublisher.watcher.SystemPublisherMetadataWatcher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private CloudDataConfigurationsPublisherService cloudDataConfigurationsPublisherService;
    @Autowired private CloudDataConfigurationsAdvisoryListener advisoryListener;
    @Autowired private SystemPublisherMetadataWatcher systemPublisherMetadataWatcher;

    private static final Logger log = YukonLogManager.getLogger(SystemDataServiceInitializer.class);

    /**
     * The init method will read the systemDataMetadata.yaml file. Once the configuration is read
     * we will create processor based on different scalars defined in YAML file. Also using
     * the frequency field we will create & execute the scheduler to publish data on topic.
     */
    @PostConstruct
    private void init() {
        publishCloudDataConfigurations();
        new Thread(advisoryListener.advisoryListener()).start();
        new Thread(systemPublisherMetadataWatcher.watch()).start();
    }

    /**
     * Method to publish CloudDataConfigurations data to the topic.
     */
    public void publishCloudDataConfigurations() {
            cloudDataConfigurationsPublisherService.publish(readYamlConfiguration());
    }

    /**
     * This method will read the yaml configuration file.
     */
    private CloudDataConfigurations readYamlConfiguration() {
        CloudDataConfigurations cloudDataConfigurations = yamlConfigManager.getCloudDataConfigurations();
        if (log.isDebugEnabled()) {
            cloudDataConfigurations.getConfigurations()
                    .stream()
                    .forEach(configuration -> {
                        log.debug("Retrieved CloudDataConfiguration values = " + configuration.toString());
                    });
        }
        return cloudDataConfigurations;
    }
}
