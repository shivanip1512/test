package com.cannontech.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;

/**
 * ConfigurationLoader class is meant to load the additional configuration.properties file into cache
 * This file should be used when the configuration properties are to be added
 * without making any changes to global settings or code changes.
 *
 */
public class ConfigurationLoader {

    private static Map<String, Map<String, String>> configSettings;
    private final static Logger log = YukonLogManager.getLogger(ConfigurationLoader.class);

    private static File configFileLocation;

    static {
        URL url = ConfigurationLoader.class.getClassLoader().getResource("configuration.properties");
        if (url != null) {
            try {
                configFileLocation = new File(url.toURI());
            } catch (URISyntaxException e) {
                configFileLocation = new File(url.getPath());
            }
            log.info("Local config found on classpath: " + url);
        } else {
            configFileLocation = new File(BootstrapUtils.getYukonBase(log.isDebugEnabled()),
                    "Server/Config/configuration.properties");
        }
        loadConfigurationProperties();
    }

    /**
     * Loads the configuration file from properties file
     */
    public static void loadConfigurationProperties() {
        INIConfiguration iniConfiguration = new INIConfiguration();
        configSettings = new HashMap<String, Map<String, String>>();
        try {
            InputStream inputStream = new FileInputStream(configFileLocation.getAbsoluteFile());
            Reader inputStreamReader = new InputStreamReader(inputStream);
            iniConfiguration.read(inputStreamReader);

            // Get Section names in file
            Set<String> setOfSections = iniConfiguration.getSections();
            if (setOfSections != null && !setOfSections.isEmpty()) {
                setOfSections.forEach(sectionName -> {
                    if (sectionName != null) {
                        Map<String, String> settings = new HashMap<>();
                        SubnodeConfiguration sObj = iniConfiguration.getSection(sectionName);
                        if (sObj.getKeys() != null) {
                            sObj.getKeys().forEachRemaining(propertyName -> {
                                // Replace .. with . as the INI config library adds extra period in the key string
                                String key = propertyName.replace("..", ".");
                                settings.put(key, sObj.getString(propertyName));
                                log.debug("property : " + key + " = " + sObj.getString(propertyName));
                            });
                            configSettings.put(sectionName.toLowerCase(), settings);
                        }
                    }
                });
            }
        } catch (FileNotFoundException e) {
            log.warn("No custom configuration.properties file.");
        } catch (ConfigurationException | IOException e) {
            log.warn("Problem in setting up the custom configuration.properties file");
        }
    }

    public static Map<String, Map<String, String>> getConfigSettings() {
        return configSettings;
    }
}
