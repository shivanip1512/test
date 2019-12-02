package com.cannontech.services.iot.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.iot.processor.IOTProcessor;
import com.cannontech.services.iot.service.IOTPublisher;
import com.cannontech.services.iot.yaml.YamlConfigManager;
import com.cannontech.services.iot.yaml.model.DictionariesField;

@Service
public class IOTPublisherServiceImpl {

    @Autowired private YamlConfigManager yamlConfigManager;
    @Autowired private IOTProcessorFactory iotProcessorFactory;
    private static final Logger log = YukonLogManager.getLogger(IOTPublisherServiceImpl.class);

    /**
     * The init method will read the iotMetadata.yaml file. Once the configuration is read 
     * we will create processor based on different scalars defined in yaml file. Also using 
     * the frequency field we will create & execute the scheduler to publish data on topic. 
     */
    @PostConstruct
    private void init() {
        Map<IOTPublisher, List<DictionariesField>> mapOfPublisherToDict = readYamlConfiguration();
        createAndExecuteProcessor(mapOfPublisherToDict);
    }

    /**
     * Create a processor based on yaml defined scalar fields. The processor will create
     * the scheduler based on defined frequency in yaml file. Once the scheduler is created
     * it will run the query and form the json object to be publisher on topic.
     * 
     */
    
    private void createAndExecuteProcessor(Map<IOTPublisher, List<DictionariesField>> mapOfPublisherToDict) {

         mapOfPublisherToDict.entrySet().stream()
                                        .forEach(publisher -> {
                                            IOTProcessor processor = iotProcessorFactory.createProcessor(publisher.getKey());
                                            processor.execute(publisher.getValue());
                                        });
    }

    /**
     * This method will read the yaml configuration file.
     */
    private Map<IOTPublisher, List<DictionariesField>> readYamlConfiguration() {
        Map<IOTPublisher, List<DictionariesField>> mapOfPublisherToDict = yamlConfigManager.getMapOfPublisherToDictionaries();
        if (log.isDebugEnabled()) {
            mapOfPublisherToDict.entrySet().stream()
                                .forEach(entity -> {
                                    log.debug("IOT Publisher = " + entity.getKey() + ", with dictionaries values = " + entity.getValue());
                                });
        }
        return mapOfPublisherToDict;
    }
}
