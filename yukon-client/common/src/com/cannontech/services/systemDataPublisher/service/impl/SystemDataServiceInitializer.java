package com.cannontech.services.systemDataPublisher.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private SystemDataProcessorFactory systemDataProcessorFactory;
    private static final Logger log = YukonLogManager.getLogger(SystemDataServiceInitializer.class);

    /**
     * The init method will read the systemDataMetadata.yaml file. Once the configuration is read 
     * we will create processor based on different scalars defined in YAML file. Also using 
     * the frequency field we will create & execute the scheduler to publish data on topic. 
     */
    @PostConstruct
    private void init() {
        Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDict = readYamlConfiguration();
        createAndExecuteProcessor(mapOfPublisherToDict);
    }

    /**
     * Create a processor based on YAML defined scalar fields. The processor will create
     * the scheduler based on defined frequency in YAML file. Once the scheduler is created
     * it will run the query and form the JSON object to be publisher on topic.
     * 
     */
    
    private void createAndExecuteProcessor(Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDict) {

         mapOfPublisherToDict.entrySet().stream()
                                        .forEach(publisher -> {
                                            SystemDataProcessor processor = systemDataProcessorFactory.createProcessor(publisher.getKey());
                                            processor.process(publisher.getValue());
                                        });
    }

    /**
     * This method will read the yaml configuration file.
     */
    private Map<SystemDataPublisher, List<DictionariesField>> readYamlConfiguration() {
        Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDict = yamlConfigManager.getMapOfPublisherToDictionaries();
        if (log.isDebugEnabled()) {
            mapOfPublisherToDict.entrySet().stream()
                                .forEach(entity -> {
                                    log.debug("System Data Publisher = " + entity.getKey() + ", with dictionaries values = " + entity.getValue());
                                });
        }
        return mapOfPublisherToDict;
    }
}
