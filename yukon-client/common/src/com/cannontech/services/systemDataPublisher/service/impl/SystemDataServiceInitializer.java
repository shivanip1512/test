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
import com.cannontech.services.systemDataPublisher.processor.SystemDataHandler;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationPublisherService;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private CloudDataConfigurationPublisherService cloudDataConfigurationPublisherService;
    @Autowired private SystemDataHandler systemDataHandler;
    @Autowired private NetworkManagerDBConfig networkManagerDBConfig;

    private static final Logger log = YukonLogManager.getLogger(SystemDataServiceInitializer.class);

    /**
     * The init method will read the systemDataMetadata.yaml file. Once the configuration is read
     * we will create processor based on different scalars defined in YAML file. Also using
     * the frequency field we will create & execute the scheduler to publish data on topic.
     */
    @PostConstruct
    private void init() {
        List<CloudDataConfiguration> cloudDataConfigurations = readYamlConfiguration();
        publishCloudDataConfigurations(cloudDataConfigurations);
        List<CloudDataConfiguration> cloudConfigurationToProcess = filterRelevantConfigurations(cloudDataConfigurations);
        handleCloudConfiguration(cloudConfigurationToProcess);
    }

    /**
     * Method to publish CloudDataConfiguration data to the topic on startup.
     */
    private void publishCloudDataConfigurations(List<CloudDataConfiguration> cloudDataConfigurations) {
        cloudDataConfigurations.stream().forEach(
                configuration -> {
                    cloudDataConfigurationPublisherService.publish(configuration);
                });
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
    private List<CloudDataConfiguration> readYamlConfiguration() {
        List<CloudDataConfiguration> cloudDataConfigurations = yamlConfigManager.getCloudDataConfigurations();
        if (log.isDebugEnabled()) {
            cloudDataConfigurations.stream()
                    .forEach(configuration -> {
                        log.debug("Retrieved CloudDataConfiguration values = " + configuration.toString());
                    });
        }
        return cloudDataConfigurations;
    }
}
