package com.cannontech.common.config;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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
 * SmtpHelper sets up the SMTP configuration on server startup
 * When changes are found in the Global settings, it reloads the settings
 */
public class SmtpHelper {

    private static final Logger log = YukonLogManager.getLogger(SmtpHelper.class);

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private ConfigurationLoader configurationLoader;

    // Assuming there are no more than 100 other settings
    // See https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
    private Cache<String, String> smtpConfigSettings = CacheBuilder.newBuilder().maximumSize(100).build();

    // local helper copies of common properties
    private volatile String cachedHost;
    private volatile String cachedPort;
    private volatile String cachedTls;

    private static final String SMTP_CONFIGURATION_KEY_ALIAS = "smtp";

    @PostConstruct
    public void setup() {
        // Get section name for SMTP settings in the file

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING,
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                    reloadSettings();
                }
            });

        // update the cache with latest settings
        reloadSettings();
    }

    /**
     * Updates the smtp settings into the cache for email notifications
     * Specifically updates the common properties settings to the cache either from File or global settings
     */
    public void reloadSettings() {
        smtpConfigSettings.invalidateAll();
        Map<String, String> smtpConfig = configurationLoader.getConfigSettings().get(SMTP_CONFIGURATION_KEY_ALIAS);
        if (!CollectionUtils.isEmpty(smtpConfig)) {
            smtpConfigSettings.putAll(smtpConfig);
        }
        cachedHost = loadCommonProperty(SmtpPropertyType.HOST);
        cachedPort = loadCommonProperty(SmtpPropertyType.PORT);
        SmtpEncryptionType encryptionType = globalSettingDao.getEnum(GlobalSettingType.SMTP_ENCRYPTION_TYPE, SmtpEncryptionType.class);
        if (encryptionType == SmtpEncryptionType.TLS) {
            smtpConfigSettings.put(SmtpPropertyType.START_TLS_ENABLED.getKey(false), "true");
        } else {
            smtpConfigSettings.asMap().remove(SmtpPropertyType.START_TLS_ENABLED.getKey(false));
        }
        log.info("Reloaded cache for the smtp Settings.");
    }

    /**
     * Merges the properties from File and Global settings
     * If File contains HOST/PORT/START_TLS_ENABLED , then these settings are taken from file
     * If not, then global settings current value is set on the settings for HOST/PORT/START_TLS_ENABLED
     * 
     * @param propertyType contains HOST/PORT/START_TLS_ENABLED values
     * @returns the final value of HOST/PORT/START_TLS_ENABLED properties set
     */
    public String loadCommonProperty(SmtpPropertyType propertyType) {
        String smtpPropertyValue = null;
        Set<String> keys = smtpConfigSettings.asMap().keySet();
        for (String key : keys) {
            if (Pattern.matches(propertyType.getRegEx(), key.toLowerCase())) {
                // key already exists for smtp or smtps
                smtpPropertyValue = smtpConfigSettings.getIfPresent(key);
                log.info(propertyType.getPropertyName()
                    + " key found in configuration settings, overriding the global setting with value : "
                    + smtpPropertyValue);
                break;
            }
        }

        if (smtpPropertyValue == null) { // not in configSettings, load from global settings
            smtpPropertyValue = globalSettingDao.getString(propertyType.getGlobalSettingType());
            // TODO : During testing with gmail smtp server this logic can be tested. For now we are setting
            // mail.smtp.*
            // TODO : YUK-16427 Should address this issue
            // smtpConfigSettings.put(propertyType.getKey(isSmtpsProtocolEnabled()), smtpPropertyValue);
            smtpConfigSettings.put(propertyType.getKey(false), smtpPropertyValue);
        }
        return smtpPropertyValue;
    }

    /**
     * Helper method to return specific SmptPropetyType values.
     * 
     * @param propertyType
     * @returns the property value
     * @throws MessagingException if no configuration for the host is found in file/global settings
     */
    public String getCommonProperty(SmtpPropertyType propertyType) throws MessagingException {
        String value = null;
        switch (propertyType) {
        case HOST:
            value = cachedHost;
            if (StringUtils.isEmpty(value)) {
                // The SMTP host name must be configured in configuration.properties file or in the
                // GlobalSettings.
                throw new MessagingException("No " + propertyType
                    + " defined in configuration.properties file or in the GlobalSettings table in the database.");
            }
            break;
        case PORT:
            value = cachedPort;
            break;
        case START_TLS_ENABLED:
            value = cachedTls;
            break;
        }

        return value;
    }

    public Map<String, String> getSmtpConfigSettings() {
        return smtpConfigSettings.asMap();
    }
}
