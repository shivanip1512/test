/**
 * 
 */
package com.cannontech.common.config;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * SmtpHelper sets up the SMTP configuration as and when changes are found
 * in the Global settings
 *
 */
public class SmtpHelper {

    private static final Logger log = YukonLogManager.getLogger(SmtpHelper.class);

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationLoader configurationLoader;

    private Cache<String, String> smtpConfigSettings = CacheBuilder.newBuilder().maximumSize(100).build();

    private static final String SMTP_CONFIGURATION_KEY_ALIAS = "smtp";
    private String configSection = null;

    public enum SmtpPropertyType {
        HOST("host", GlobalSettingType.SMTP_HOST),
        PORT("port", GlobalSettingType.SMTP_PORT),
        START_TLS_ENABLED("starttls.enable", GlobalSettingType.SMTP_TLS_ENABLED);

        private String propertyName;
        private GlobalSettingType globalSettingType;

        public GlobalSettingType getGlobalSettingType() {
            return globalSettingType;
        }

        private SmtpPropertyType(String type, GlobalSettingType globalSettingType) {
            propertyName = type;
            this.globalSettingType = globalSettingType;
        }

        public String generateKey(SmtpProtocolType smtp) {
            return "mail." + smtp.protocolName + "." + propertyName;
        }
    }

    public enum SmtpProtocolType {
        SMTP("smtp"), SMTPS("smtps");
        private String protocolName;

        private SmtpProtocolType(String protocol) {
            setProtocolName(protocol);
        }

        public void setProtocolName(String protocolName) {
            this.protocolName = protocolName;
        }
    }

    @PostConstruct
    public void setup() {
        // Get section name for SMTP settings in the file
        configSection = getConfigSectionName(SMTP_CONFIGURATION_KEY_ALIAS);
        
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING,
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                    if (log.isDebugEnabled()) {
                        log.debug("Reloading cache for the smtp Settings");
                    }
                    reloadSettings();
                }
            });
        
        smtpConfigSettings.putAll(loadConfigs());
        // update the cache with latest settings
        reloadSettings();
    }
    
    private Map<String, String> loadConfigs() {
        Map<String, String> configs = configurationLoader.getConfigSettings().get(configSection);
        if (configs == null) {
            configs = new HashMap<>();
        }
        return configs;
    }
    
    /**
     * Updates the smtp settings into the cache for email notifications
     * Specifically updates the common properties settings to the cache either from File or global settings
     */
    private void reloadSettings() {
        smtpConfigSettings.invalidateAll();
        smtpConfigSettings.putAll(loadConfigs());
        loadCommonProperty(SmtpPropertyType.HOST);
        loadCommonProperty(SmtpPropertyType.PORT);
        loadCommonProperty(SmtpPropertyType.START_TLS_ENABLED);
    }

    /**
     * Merges the properties from File and Global settings
     * If File contains HOST/PORT/START_TLS_ENABLED , then these settings are taken from file
     * If not, then global settings current value is set on the settings for HOST/PORT/START_TLS_ENABLED
     * In case global settings are considered, SMTP/SMTPS protocol specific properties are set
     * 
     * @param propertyType contains HOST/PORT/START_TLS_ENABLED values
     * @returns the final value of HOST/PORT/START_TLS_ENABLED properties set
     */
    public void loadCommonProperty(SmtpPropertyType propertyType) {

        // Check in global settings
        String smtpPropertyValue = globalSettingDao.getString(propertyType.getGlobalSettingType());
        boolean isKeyFoundInConfigFile = false;
        String smtpKey = propertyType.generateKey(SmtpProtocolType.SMTP);
        String smtpsKey = propertyType.generateKey(SmtpProtocolType.SMTPS);

        // Check host name in file
        Map<String, String> configFileSmtpSettings = configurationLoader.getConfigSettings().get(configSection);
        if (configFileSmtpSettings != null) {
            if (!smtpPropertyValue.isEmpty()
                && (configFileSmtpSettings.containsKey(smtpKey) || configFileSmtpSettings.containsKey(smtpKey))) {
                // Log the occurrence of Global settings being overridden by the configuration file settings
                log.info("Overwriting Global settings for SMTP " + propertyType.propertyName
                    + " with the configuration file settings.");

                // Add the setting to the settings map
                if (configFileSmtpSettings.containsKey(smtpsKey)) {
                    smtpPropertyValue = configFileSmtpSettings.get(smtpsKey);
                    isKeyFoundInConfigFile = true;
                } else if (configFileSmtpSettings.containsKey(smtpKey)) {
                    smtpPropertyValue = configFileSmtpSettings.get(smtpKey);
                    isKeyFoundInConfigFile = true;
                }
            }
        }
        if (!isKeyFoundInConfigFile) {
            if (smtpConfigSettings == null) {
                configFileSmtpSettings = new HashMap<>();
            }
            // Set the Global Config setting in the smtp settings
            if (isSmtpsProtocolEnabled()) {
                smtpConfigSettings.put(smtpsKey, smtpPropertyValue);
            } else {
                smtpConfigSettings.put(smtpKey, smtpPropertyValue);
            }
        }
    }

    /**
     * Gets the property value from the settings cache for HOST/PORT/START_TLS_ENABLED properties
     * If the properties are not set, logs an error message in the webserver logs
     * 
     * @param propertyType contains HOST/PORT/START_TLS_ENABLED values
     * @returns the final value of HOST/PORT/START_TLS_ENABLED properties set
     */
    public String getCommonProperty(SmtpPropertyType propertyType) {
        String smtpValue = smtpConfigSettings.getIfPresent(propertyType.generateKey(SmtpProtocolType.SMTP));
        String smtpsValue = smtpConfigSettings.getIfPresent(propertyType.generateKey(SmtpProtocolType.SMTPS));
        if (smtpValue == null && smtpsValue == null) {
            log.error(" No SMTP property " + propertyType.propertyName
                + "configuration found in configuration.properties file and global settings");
            // TODO : Event logging might be needed here
        }
        if (smtpValue != null) {
            return smtpValue;
        } else if (smtpsValue != null) {
            return smtpsValue;
        }
        return null;
    }

    public String getConfigSectionName(String sectionAlias) {
        for (String section : configurationLoader.getConfigSettings().keySet()) {
            if (section.contains(sectionAlias)) {
                return section;
            }
        }
        return sectionAlias;
    }

    /**
     * Returns if SMTPS is to be used for setting up global setting properties
     * @returns true if smtps protocol is to be used
     */
    public boolean isSmtpsProtocolEnabled() {
        String username = globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME);
        String password = globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD);

        if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
            log.info("SMTP username and password are set, use SMTPS protocol for Emails");
            return true;
        }
        return false;
    }

    public Map<String, String> getSmtpConfigSettings() {
        return smtpConfigSettings.asMap();
    }
}
