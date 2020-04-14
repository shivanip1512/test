package com.cannontech.services.systemDataPublisher.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.CloudDataConfigurationPublisherService;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;

@Service
public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private SystemDataProcessorFactory systemDataProcessorFactory;
    @Autowired private CloudDataConfigurationPublisherService cloudDataConfigurationPublisherService;
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
        Map<SystemDataPublisher, List<CloudDataConfiguration>> mapOfPublisherToConfig = filterRelevantConfigurations(cloudDataConfigurations);
        createAndExecuteProcessor(mapOfPublisherToConfig);
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

    // TODO: Created the the Map for supporting the Existing framework.We will update this method on once all the NM publishes its
    // own data.
    private Map<SystemDataPublisher, List<CloudDataConfiguration>> filterRelevantConfigurations(
            List<CloudDataConfiguration> cloudDataConfigurations) {
        Map<SystemDataPublisher, List<CloudDataConfiguration>> mapOfPublisherToConfig = new HashMap<>();
        mapOfPublisherToConfig.put(SystemDataPublisher.YUKON,
                cloudDataConfigurations.stream()
                                  .filter(configuration -> configuration.getDataPublisher() == SystemDataPublisher.YUKON)
                                  .collect(Collectors.toList()));
        mapOfPublisherToConfig.put(SystemDataPublisher.OTHER,
                cloudDataConfigurations.stream()
                                  .filter(configuration -> configuration.getDataPublisher() == SystemDataPublisher.OTHER)
                                  .collect(Collectors.toList()));
        mapOfPublisherToConfig.put(SystemDataPublisher.NETWORK_MANAGER,
                cloudDataConfigurations.stream()
                                  .filter(configuration -> configuration.getDataPublisher() == SystemDataPublisher.NETWORK_MANAGER)
                                  .collect(Collectors.toList()));
        return mapOfPublisherToConfig;
    }

    /**
     * Create a processor based on YAML defined scalar fields. The processor will create
     * the scheduler based on defined frequency in YAML file. Once the scheduler is created
     * it will run the query and form the JSON object to be publisher on topic.
     * 
     */
    
    private void createAndExecuteProcessor(Map<SystemDataPublisher, List<CloudDataConfiguration>> mapOfPublisherToConfig) {

        mapOfPublisherToConfig.entrySet().stream()
                                        .forEach(publisher -> {
                                            SystemDataProcessor processor = systemDataProcessorFactory.createProcessor(publisher.getKey());
                                            processor.process(publisher.getValue());
                                        });
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
