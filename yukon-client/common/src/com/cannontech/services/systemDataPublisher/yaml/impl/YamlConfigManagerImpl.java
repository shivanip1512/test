package com.cannontech.services.systemDataPublisher.yaml.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.encryption.SystemPublisherMetadataEncryption;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.DictionariesField;
import com.cannontech.services.systemDataPublisher.yaml.model.ScalarField;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YamlConfigManagerImpl implements YamlConfigManager {

    private final String SYSTEM_PUBLISHER_METADATA = "systemPublisherMetadata.yaml";
    private final String AUTO_ENCRYPTED_TEXT = "\\(AUTO_ENCRYPTED\\)";
    private static final Logger log = YukonLogManager.getLogger(YamlConfigManagerImpl.class);
    private final Map<SystemDataPublisher, List<DictionariesField>> mapOfPublisherToDictionaries = new ConcurrentHashMap<>();

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
        // when file is changed. For reload we can write a watcher which will watch for any change and when something
        // gets changed reload the configuration.
        ScalarField scalars = null;
        try {
            ClassPathResource systemPublisherYamlMetadata = new ClassPathResource(SYSTEM_PUBLISHER_METADATA);
            Yaml yaml = new Yaml();
            Object yamlObject = yaml.load(systemPublisherYamlMetadata.getInputStream());

            ObjectMapper objectMapper = new ObjectMapper();

            byte[] jsonBytes = objectMapper.writeValueAsBytes(yamlObject);
            log.debug("YAML configuration " + yamlObject);
            scalars = objectMapper.readValue(jsonBytes, ScalarField.class);
            if (scalars.getYukonDictionaries() != null) {
                mapOfPublisherToDictionaries.put(SystemDataPublisher.YUKON, getDecryptedDictionaries(scalars.getYukonDictionaries()));
            }
            if (scalars.getNmDictionaries() != null) {
                mapOfPublisherToDictionaries.put(SystemDataPublisher.NETWORK_MANAGER, getDecryptedDictionaries(scalars.getNmDictionaries()));
            }
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Error while parsing the YAML file fields.", e);
        } catch (IOException e) {
            log.error("Error while reading the YAML config file.", e);
        }

    }

    /**
     * Create and return List of DictionariesField object after decrypting source field.
     * 
     */
    private List<DictionariesField> getDecryptedDictionaries(List<DictionariesField> fields) {
        return fields.stream()
                     .map(dictionariesField -> {
                         return new DictionariesField(dictionariesField.getField(), dictionariesField.getDescription(),
                                    dictionariesField.getDetails(), getDecryptedSource(dictionariesField.getSource()),
                                    dictionariesField.getIotType(), dictionariesField.getFrequency());})
                     .collect(Collectors.toList());
    }

    /**
     * Return whole SQL script after decrypting the multi / Single line encrypted query.
     * 
     */
    private String getDecryptedSource(String multiLineSource) {
        StringBuilder encryptedSource = new StringBuilder();
        Arrays.stream(multiLineSource.split(AUTO_ENCRYPTED_TEXT))
              .forEach(source -> {
                  if (StringUtils.isNotEmpty(source)) {
                        String encryptedSubSource = StringUtils.EMPTY;
                        try {
                            encryptedSubSource = SystemPublisherMetadataEncryption.decrypt(source.trim());
                        } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                            log.error("Error while decrypting ", e);
                        }
                        encryptedSource.append(encryptedSubSource.trim()).append(StringUtils.SPACE);
                    }
              });
        return encryptedSource.toString();
    }

    @Override
    public Map<SystemDataPublisher, List<DictionariesField>> getMapOfPublisherToDictionaries() {
        return mapOfPublisherToDictionaries;
    }

}
