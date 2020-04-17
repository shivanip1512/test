package com.cannontech.services.systemDataPublisher.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.context.NetworkManagerDBConfig;
import com.cannontech.services.systemDataPublisher.listener.CloudDataConfigurationsAdvisoryListener;
import com.cannontech.services.systemDataPublisher.processor.SystemDataHandler;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationsPublisherService;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;

@Service
public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private CloudDataConfigurationsPublisherService cloudDataConfigurationsPublisherService;
    @Autowired private SystemDataHandler systemDataHandler;
    @Autowired private NetworkManagerDBConfig networkManagerDBConfig;
    @Autowired private CloudDataConfigurationsAdvisoryListener advisoryListener;

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
        List<CloudDataConfiguration> cloudConfigurationToProcess = filterRelevantConfigurations(
                readYamlConfiguration().getConfigurations());
        handleCloudConfiguration(cloudConfigurationToProcess);
    }

    /**
     * Method to publish CloudDataConfigurations data to the topic.
     */
    public void publishCloudDataConfigurations() {
        cloudDataConfigurationsPublisherService.publish(readYamlConfiguration());
    }

    /**
     * This method filters the fields which Yukon will process.
     * Yukon will process only Yukon and Others section fields.
     */
    private List<CloudDataConfiguration> filterRelevantConfigurations(
            List<CloudDataConfiguration> cloudDataConfigurations) {

        boolean networkManagerDBConfigured = networkManagerDBConfig.isNetworkManagerDBConnectionConfigured();

        List<CloudDataConfiguration> releventConfigurations = cloudDataConfigurations.stream()
                .filter(e -> (e.getDataPublisher() == SystemDataPublisher.YUKON
                        || e.getDataPublisher() == SystemDataPublisher.OTHER
                        || (networkManagerDBConfigured && e.getDataPublisher() == SystemDataPublisher.NETWORK_MANAGER
                                && StringUtils.isNotEmpty(e.getSource()))))
                .collect(Collectors.toList());
        return releventConfigurations;
    }

    /**
     * Passes it to the handler to handle cloudConfiguration.
     */
    private void handleCloudConfiguration(List<CloudDataConfiguration> cloudDataConfiguration) {
        systemDataHandler.handle(cloudDataConfiguration);
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
