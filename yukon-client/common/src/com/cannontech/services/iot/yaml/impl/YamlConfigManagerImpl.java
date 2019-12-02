package com.cannontech.services.iot.yaml.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.services.iot.service.IOTPublisher;
import com.cannontech.services.iot.yaml.YamlConfigManager;
import com.cannontech.services.iot.yaml.model.DictionariesField;
import com.cannontech.services.iot.yaml.model.ScalarField;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YamlConfigManagerImpl implements YamlConfigManager {

    private final String IOT_YAML_METADATA = "iotMetadata.yaml";
    private static final Logger log = YukonLogManager.getLogger(YamlConfigManagerImpl.class);
    private final Map<IOTPublisher, List<DictionariesField>> mapOfPublisherToDictionaries = new ConcurrentHashMap<>();

    @PostConstruct
    private void init() {
        loadConfig();
    }

    /**
     * Load YAML config from classpath.
     * TODO - In Future we will be reading the file directly from file system and monitor that file
     * on change to reload the configuration with new changes.
     * 
     */
    private void loadConfig() {
        // Currently we are reading the Yaml file from shared resource classpath. In future this file can
        // be read from file system like we read logging configuration file. Doing so will help in reloading
        // when file is changed. For reload we can write a watcher will watch for any change and when something
        // gets changed reload the configuration.
        ScalarField scalars = null;
        try {
            ClassPathResource iotYamlMetadata = new ClassPathResource(IOT_YAML_METADATA);
            Yaml yaml = new Yaml();
            Object yamlObject = yaml.load(iotYamlMetadata.getInputStream());

            ObjectMapper objectMapper = new ObjectMapper();

            byte[] jsonBytes = objectMapper.writeValueAsBytes(yamlObject);
            log.debug("YAML configuration " + yamlObject);
            scalars = objectMapper.readValue(jsonBytes, ScalarField.class);
            if (scalars.getYukonDictionaries() != null) {
                mapOfPublisherToDictionaries.put(IOTPublisher.YUKON, scalars.getYukonDictionaries());
            }
            if (scalars.getNmDictionaries() != null) {
                mapOfPublisherToDictionaries.put(IOTPublisher.NETWORK_MANAGER, scalars.getNmDictionaries());
            }
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Error while parsing the YAML file fields.", e);
        } catch (IOException e) {
            log.error("Error while reading the YAML config file.", e);
        }

    }

    @Override
    public Map<IOTPublisher, List<DictionariesField>> getMapOfPublisherToDictionaries() {
        return mapOfPublisherToDictionaries;
    }

}
