package com.cannontech.common.config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.encryption.YukonCryptoUtils;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.SystemEmailSettingsType;

/**
 * SmtpHelper sets up the SMTP configuration on server startup When changes are found in the Global settings, it reloads the
 * settings.
 */
public class SmtpHelper {

    private static final Logger log = YukonLogManager.getLogger(SmtpHelper.class);

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;

    // Assuming there are no more than 100 other settings
    // See https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
    private Map<String, String> systemEmailSettingsCache = new ConcurrentHashMap<>();
    // local helper copies of common properties
    private static final String separator = ":";
    private static final String fileName = "/Server/Config/System/emailSettings.txt";
    private static final String SMTP_CONFIGURATION_KEY_ALIAS = "smtp";
    private static File emailSettingsFile = null;

    static {
        try {
            String yukonBase = CtiUtilities.getYukonBase();
            emailSettingsFile = new File(yukonBase, fileName);
            if (!emailSettingsFile.exists()) {
                emailSettingsFile.getParentFile().mkdir();
                emailSettingsFile.createNewFile();
            }

        } catch (IOException e) {
            log.error("Error creating emailSettings.txt file", e);
        }
    }

    @PostConstruct
    public void setup() {
        // Get section name for SMTP settings in the file
        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.GLOBAL_SETTING,
                new DatabaseChangeEventListener() {
                    @Override
                    public void eventReceived(DatabaseChangeEvent event) {
                        reloadAllSettings();
                    }
                });
        // update the cache with latest settings from configuration file and global settings.
        reloadAllSettings();
    }

    /**
     * Updates the smtp settings into the cache for email notifications.
     */
    public void reloadAllSettings() {
        // Load smtp configuration from configuration.properties file.
        Map<String, String> smtpConfig = ConfigurationLoader.getConfigSettings().get(SMTP_CONFIGURATION_KEY_ALIAS);
        if (!CollectionUtils.isEmpty(smtpConfig)) {
            systemEmailSettingsCache.putAll(smtpConfig);
        }
        // Load smtp configurations, subscriber mail IDs from Database. For HOST, PORT and START_TLS_ENABLED,
        // configuration.properties will take higher priority.
        loadCommonProperty(SmtpPropertyType.HOST);
        loadCommonProperty(SmtpPropertyType.PORT);
        SmtpEncryptionType encryptionType = globalSettingDao.getEnum(GlobalSettingType.SMTP_ENCRYPTION_TYPE,
                SmtpEncryptionType.class);
        if (encryptionType == SmtpEncryptionType.TLS) {
            updateCachedValue(SmtpPropertyType.START_TLS_ENABLED.getKey(false), "true");
        } else {
            systemEmailSettingsCache.remove(SmtpPropertyType.START_TLS_ENABLED.getKey(false));
        }
        updateCachedValue(SystemEmailSettingsType.SMTP_PROTOCOL.getKey(), encryptionType.getProtocol());
        updateCachedValue(SystemEmailSettingsType.MAIL_FROM_ADDRESS.getKey(),
                globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        updateCachedValue(SystemEmailSettingsType.SMTP_PASSWORD.getKey(),
                globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
        updateCachedValue(SystemEmailSettingsType.SMTP_USERNAME.getKey(),
                globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
        updateCachedValue(SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey(),
                StringUtils.join(subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG), ","));
        log.info("Reloaded cache for the smtp Settings.");
        // Write configurations to emailSettings.txt file.
        writeToFile();
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
        Set<String> keys = systemEmailSettingsCache.keySet();
        for (String key : keys) {
            if (Pattern.matches(propertyType.getRegEx(), key.toLowerCase())) {
                // key already exists for smtp or smtps
                smtpPropertyValue = systemEmailSettingsCache.get(key);
                log.info(propertyType.getPropertyName()
                        + " key found in configuration settings, overriding the global setting with value : "
                        + smtpPropertyValue);
                break;
            }
        }
        // not in configSettings, load from global settings
        if (smtpPropertyValue == null) {
            smtpPropertyValue = globalSettingDao.getString(propertyType.getGlobalSettingType());
            updateCachedValue(propertyType.getKey(false), smtpPropertyValue);
        }
        return smtpPropertyValue;
    }

    /**
     * Retrieve value for the specified key from cache.
     */
    public String getCachedValue(String key) {
        if (CollectionUtils.isEmpty(systemEmailSettingsCache)) {
            readSettingsFromFile();
        }
        return systemEmailSettingsCache.get(key);
    }

    /**
     * Update cache with specified key value pair.
     */
    public void updateCachedValue(String key, String value) {
        systemEmailSettingsCache.put(key, value);
    }

    /**
     * Write Email configuration settings to emailSettings.txt file
     */
    public void writeToFile() {
        try {
            List<String> systemEmailSettings = new ArrayList<String>();
            systemEmailSettings.addAll(getEncryptedSettings());
            Files.write(emailSettingsFile.toPath(), systemEmailSettings);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Error writing encrypted settings to emailSettings.txt file", e);
        }
    }

    /**
     * Return a list of encrypted key and value pairs.
     */
    private List<String> getEncryptedSettings()
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        List<String> settings = new ArrayList<String>();
        encryptSetting(settings, SmtpPropertyType.HOST.getKey(false));
        encryptSetting(settings, SmtpPropertyType.PORT.getKey(false));
        if (systemEmailSettingsCache.containsKey(SmtpPropertyType.START_TLS_ENABLED.getKey(false))) {
            encryptSetting(settings, SmtpPropertyType.START_TLS_ENABLED.getKey(false));
        }
        encryptSetting(settings, SystemEmailSettingsType.SMTP_PROTOCOL.getKey());
        encryptSetting(settings, SystemEmailSettingsType.SMTP_USERNAME.getKey());
        encryptSetting(settings, SystemEmailSettingsType.SMTP_PASSWORD.getKey());
        encryptSetting(settings, SystemEmailSettingsType.MAIL_FROM_ADDRESS.getKey());
        encryptSetting(settings, SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey());
        return settings;
    }

    /**
     * Return an encrypted key value pair.
     * List entry formatted as: [encryptedKey:encryptedValue]
     */
    private void encryptSetting(List<String> settings, String key)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        settings.add(YukonCryptoUtils.encrypt(key.toString())
                .concat(separator)
                .concat(YukonCryptoUtils.encrypt(getCachedValue(key))));
    }

    /**
     * Update cache from file when DB is not reachable on startup.
     */
    private void readSettingsFromFile() {
        systemEmailSettingsCache.clear();
        try (Stream<String> lines = Files.lines(Paths.get(emailSettingsFile.toURI()), Charset.defaultCharset())) {
            lines.forEach(line -> {
                try {
                    String tokens[] = line.split(separator);
                    updateCachedValue(YukonCryptoUtils.decrypt(tokens[0]),
                            YukonCryptoUtils.decrypt(tokens[1]));
                } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                    log.error("Error decrypting data from emailSettings.txt file", e);
                }
            });
        } catch (IOException exception) {
            log.error("Error reading emailSettings.txt file", exception);
        }
    }

    /**
     * 
     * Returns SMTP configurations. 
     */
    public Map<String, String> getSmtpConfigSettings() {
        if (CollectionUtils.isEmpty(systemEmailSettingsCache)) {
            readSettingsFromFile();
        }
        return systemEmailSettingsCache;
    }
}
