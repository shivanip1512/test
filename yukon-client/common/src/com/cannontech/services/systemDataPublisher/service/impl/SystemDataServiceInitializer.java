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
import com.cannontech.services.dictionariesField.publisher.service.DictionariesFieldPublisherService;
import com.cannontech.services.systemDataPublisher.processor.SystemDataProcessor;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;

@Service
public class SystemDataServiceInitializer {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private SystemDataProcessorFactory systemDataProcessorFactory;
    @Autowired private DictionariesFieldPublisherService dictionariesFieldPublisherService;
    private static final Logger log = YukonLogManager.getLogger(SystemDataServiceInitializer.class);

    /**
     * The init method will read the systemDataMetadata.yaml file. Once the configuration is read 
     * we will create processor based on different scalars defined in YAML file. Also using 
     * the frequency field we will create & execute the scheduler to publish data on topic. 
     */
    @PostConstruct
    private void init() {
        List<DictionariesField> dictionariesFields = readYamlConfiguration();
        publishDictionariesFields(dictionariesFields);
        Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDict = filterRelevantDictionaries(dictionariesFields);
        createAndExecuteProcessor(mapOfPublisherToDict);
    }

    /**
     * Method to publish DictionariesField data to the topic on startup.
     */
    private void publishDictionariesFields(List<DictionariesField> dictionariesFields) {
        dictionariesFields.stream().forEach(
                dictionariesField -> {
                    dictionariesFieldPublisherService.publish(dictionariesField);
                });
    }

    // TODO: Created the the Map for supporting the Existing framework.We will update this method on once all the NM publishes its
    // own data.
    private Map<SystemDataPublisher, List<DictionariesField>> filterRelevantDictionaries(
            List<DictionariesField> dictionariesFields) {
        Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDict = new HashMap<>();
        mapOfPublisherToDict.put(SystemDataPublisher.YUKON,
                dictionariesFields.stream()
                                  .filter(dictionariesField -> dictionariesField.getDataPublisher() == SystemDataPublisher.YUKON)
                                  .collect(Collectors.toList()));
        mapOfPublisherToDict.put(SystemDataPublisher.OTHER,
                dictionariesFields.stream()
                                  .filter(dictionariesField -> dictionariesField.getDataPublisher() == SystemDataPublisher.OTHER)
                                  .collect(Collectors.toList()));
        mapOfPublisherToDict.put(SystemDataPublisher.NETWORK_MANAGER,
                dictionariesFields.stream()
                                  .filter(dictionariesField -> dictionariesField.getDataPublisher() == SystemDataPublisher.NETWORK_MANAGER)
                                  .collect(Collectors.toList()));
        return mapOfPublisherToDict;
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
    private List<DictionariesField> readYamlConfiguration() {
        List<DictionariesField> dictionariesFields = yamlConfigManager.getDictionariesFields();
        if (log.isDebugEnabled()) {
            dictionariesFields.stream()
                    .forEach(dictionariesField -> {
                        log.debug("Retrieved dictionaries values = " + dictionariesField.toString());
                    });
        }
        return dictionariesFields;
    }
}
