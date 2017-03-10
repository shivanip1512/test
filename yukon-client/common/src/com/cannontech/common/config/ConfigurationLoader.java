package com.cannontech.common.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class ConfigurationLoader {

    private Map<String, Map<String, String>> configSettings;
    private final static Logger log = YukonLogManager.getLogger(ConfigurationLoader.class);

    private final String SMTPS_HOST = "mail.smtps.host";
    private final String SMTPS_PORT = "mail.smtps.port";
    private final String SMTPS_TLS_ENABLED = "mail.smtps.starttls.enable";
    private final String SMTP_HOST = "mail.smtp.host";
    private final String SMTP_PORT = "mail.smtp.port";
    private final String SMTP_TLS_ENABLED = "mail.smtp.starttls.enable";

    private static final String SMTP_CONFIGURATION_KEY_ALIAS = "smtp";
    private String configSection = null;
    private static File configFileLocation;
    private boolean isSMTPSProtocolConfigured = false;
    private final String SMTPS_PROTOCOL = "smtps";

    @Autowired private GlobalSettingDao globalSettingDao;

    static {
        URL url = MasterConfigHelper.class.getClassLoader().getResource("configuration.properties");
        if (url != null) {
            try {
                configFileLocation = new File(url.toURI());
            } catch (URISyntaxException e) {
                configFileLocation = new File(url.getPath());
            }
            log.info("Local config found on classpath: " + url);
        } else {
            configFileLocation =
                new File(BootstrapUtils.getYukonBase(log.isDebugEnabled()), "Server/Config/configuration.properties");
        }
    }

    @PostConstruct
    public void init() throws MessagingException {
        loadConfigurationProperties();
        mergeSMTPGlobalSettings();
    }

    /**
     * Loads the configuration file from properties file
     */
    private void loadConfigurationProperties() {
        INIConfiguration iniConfiguration = new INIConfiguration();
        configSettings = new HashMap<String, Map<String, String>>();
        try {
            InputStream inputStream = new FileInputStream(configFileLocation.getAbsoluteFile());
            Reader inputStreamReader = new InputStreamReader(inputStream);
            iniConfiguration.read(inputStreamReader);

            // Get Section names in file
            Set<String> setOfSections = iniConfiguration.getSections();
            for (String sectionName : setOfSections) {
                Map<String, String> settings = new HashMap<>();
                SubnodeConfiguration sObj = iniConfiguration.getSection(sectionName);
                sObj.getKeys().forEachRemaining(propertyName -> {
                    settings.put(propertyName.replace("..", "."), sObj.getString(propertyName));
                    log.debug("property : " + propertyName.replace("..", ".") + " = " + sObj.getString(propertyName));
                });
                configSettings.put(sectionName.toLowerCase(), settings);
            }
        } catch (ConfigurationException | IOException e) {
            log.error("Problem in setting up the additional configuration -", e);
        }
    }

    /**
     * If smtps is found, even in one of the properties in smtp properties, all are considered as smtps
     * properties
     * This method merges the properties set globally with the properties set in the properties file.
     * The file properties set have the higher preference, in case the same property is set at both places.
     */
    private void mergeSMTPGlobalSettings() {
        // Get section name for SMTP settings in the file
        configSection = getConfigSectionName(SMTP_CONFIGURATION_KEY_ALIAS);

        // Replace smtp with smtps, if a trace of SMTPS protocol is found in any of the properties
        if (configSettings.containsKey(configSection)) {
            Map<String, String> settings = configSettings.get(configSection);
            List<String> smtpKeys = Lists.newArrayList(settings.keySet());
            if (smtpKeys.toString().contains(SMTPS_PROTOCOL)) {
                isSMTPSProtocolConfigured = true;
                log.info("SMTPS Protocol will be used for Email notifications, smtp will be replaced by smtps.");
            }
            for (String key : settings.keySet()) {
                if (isSMTPSProtocolConfigured && !key.contains("smtps")) {
                    String value = settings.get(key);
                    settings.remove(key);
                    settings.put(key.replaceFirst("smtp", "smtps"), value);
                }
            }
        }
        
        // SMTP global Host settings - merging if found
        String smtpHost = globalSettingDao.getString(GlobalSettingType.SMTP_HOST);
        if (!smtpHost.isEmpty()) {
            String hostPropertyName = isSMTPSProtocolConfigured ? SMTPS_HOST : SMTP_HOST;
            setConfiguration(configSection, hostPropertyName, smtpHost);
        }

        // SMTP global Port settings - merging if found
        Integer smtpPort = globalSettingDao.getNullableInteger(GlobalSettingType.SMTP_PORT);
        if (smtpPort != null && smtpPort != 0) {
            String portPropertyName = isSMTPSProtocolConfigured ? SMTPS_PORT : SMTP_PORT;
            setConfiguration(configSection, portPropertyName, smtpPort.toString());
        }

        // SMTP global TLS Enabled settings - merging if found
        boolean smtpTls = globalSettingDao.getBoolean(GlobalSettingType.SMTP_TLS_ENABLED);
        if (smtpTls) {
            String tlsEnabledPropertyName = isSMTPSProtocolConfigured ? SMTPS_TLS_ENABLED : SMTP_TLS_ENABLED;
            setConfiguration(configSection, tlsEnabledPropertyName, smtpPort.toString());
        }
    }

    /**
     * Global properties values are sent to this method in parameters.
     * If same property is found in the file, it is logged for troubleshooting purpose
     * If property is not found the global property is considered.
     * 
     * @param section contains the name of the section. e.g. smtp
     * @param propertyName contains the name of the property, e.g. mail.smtp.host
     * @param value, contains the value for a property
     */
    private void setConfiguration(String section, String propertyName, String value) {
        if (!configSettings.containsKey(section)) {
            Map<String, String> settings = new HashMap<>();
            configSettings.put(section, settings); // create new section
        }

        if (configSettings.get(section).containsKey(propertyName)) {
            // Log the occurrence of Global settings being overridden by the configuration file settings
            log.info("Overwriting Configuration Settings for " + section + ", " + 
                        "\n    Global Setting -> " + propertyName + " = " + value + 
                        "\n    Actual Setting which will be used -> " + propertyName + " = "
                                    + configSettings.get(section).get(propertyName));
        } else {
            // Write settings to the map only when the settings is not found in the file, but found in global
            // settings
            configSettings.get(section).put(propertyName, value);
        }
    }

    public String getConfigSectionName(String sectionAlias) {
        for (String section : configSettings.keySet()) {
            if (section.contains(sectionAlias)) {
                return section;
            }
        }
        return sectionAlias;
    }

    public Map<String, Map<String, String>> getConfigSettings() {
        return configSettings;
    }

    public void setConfigSettings(Map<String, Map<String, String>> configSettings) {
        this.configSettings = configSettings;
    }
}
