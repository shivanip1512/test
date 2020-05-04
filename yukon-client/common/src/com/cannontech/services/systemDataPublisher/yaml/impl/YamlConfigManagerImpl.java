package com.cannontech.services.systemDataPublisher.yaml.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.encryption.SystemPublisherMetadataCryptoUtils;
import com.cannontech.services.systemDataPublisher.service.SystemDataPublisher;
import com.cannontech.services.systemDataPublisher.yaml.YamlConfigManager;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfiguration;
import com.cannontech.services.systemDataPublisher.yaml.model.CloudDataConfigurations;
import com.cannontech.services.systemDataPublisher.yaml.model.ScalarField;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YamlConfigManagerImpl implements YamlConfigManager {

    private final String ENCRYPTED_SYSTEM_PUBLISHER_METADATA = "Server/Config/encryptedSystemPublisherMetadata.yaml";
    private final String AUTO_ENCRYPTED_TEXT = "(AUTO_ENCRYPTED)";
    private static final Logger log = YukonLogManager.getLogger(YamlConfigManagerImpl.class);
    private CloudDataConfigurations cloudDataConfigurations;

    /**
     * Load encryptedSystemPublisherMetadata.yaml config from Server/Config folder directly and monitor that file
     * on change to reload the configuration with new changes.
     * 
     */
    private void loadConfig() {
        // Currently we are reading the Yaml file from shared resource classpath. In future this file can
        // be read from file system like we read logging configuration file. Doing so will help in reloading
        // when file is changed. For reload we can write a watcher which will watch for any change and when something
        // gets changed reload the configuration.
        ScalarField scalars = null;
        cloudDataConfigurations = new CloudDataConfigurations();
        List<CloudDataConfiguration> configurations = new ArrayList<CloudDataConfiguration>();
        try {
            InputStream systemPublisherYamlMetadataStream = new FileInputStream(
                    new File(CtiUtilities.getYukonBase(), ENCRYPTED_SYSTEM_PUBLISHER_METADATA));
            Yaml yaml = new Yaml();
            Object yamlObject = yaml.load(systemPublisherYamlMetadataStream);

            ObjectMapper objectMapper = new ObjectMapper();

            byte[] jsonBytes = objectMapper.writeValueAsBytes(yamlObject);
            log.debug("YAML configuration " + yamlObject);
            scalars = objectMapper.readValue(jsonBytes, ScalarField.class);
            if (scalars.getYukonConfigurations() != null) {
                configurations.addAll(getDecryptedConfigurations(scalars.getYukonConfigurations(), SystemDataPublisher.YUKON));
            }
            if (scalars.getNmConfigurations() != null) {
                configurations.addAll(getDecryptedConfigurations(scalars.getNmConfigurations(),
                        SystemDataPublisher.NETWORK_MANAGER));
            }
            if (scalars.getOtherConfigurations() != null) {
                configurations.addAll(getDecryptedConfigurations(scalars.getOtherConfigurations(), SystemDataPublisher.OTHER));
            }
            cloudDataConfigurations.setConfigurations(configurations);
        } catch (JsonParseException | JsonMappingException e) {
            log.error("Error while parsing the YAML file fields.", e);
        } catch (IOException e) {
            log.error("Error while reading the YAML config file.", e);
        }

    }

    /**
     * Create and return List of CloudDataConfiguration object after decrypting source field.
     * @param fields : List of CloudDataConfiguration with encrypted source field.
     * 
     */
    private List<CloudDataConfiguration> getDecryptedConfigurations(List<CloudDataConfiguration> fields, SystemDataPublisher dataPublisher) {
        return fields.stream()
                .map(configuration -> {
                    return new CloudDataConfiguration(configuration.getField(), configuration.getDescription(),
                            configuration.getDetails(), getDecryptedSource(configuration.getSource()),
                            configuration.getIotType(), configuration.getFrequency(), dataPublisher);})
                     .collect(Collectors.toList());
    }

    /**
     * Return  SQL script after decrypting the source. If any error occurred while decryption returns empty string.
     * @param encryptedSource : Encrypted source field
     */
    private String getDecryptedSource(String encryptedSource) {
        if (encryptedSource != null) {
            try {
                return SystemPublisherMetadataCryptoUtils.decrypt(encryptedSource.substring(AUTO_ENCRYPTED_TEXT.length()).trim());
            } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                log.error("Error while decrypting source field.", e);
                return StringUtils.EMPTY;
            }
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public CloudDataConfigurations getCloudDataConfigurations() {
        loadConfig();
        return cloudDataConfigurations;
    }

}
