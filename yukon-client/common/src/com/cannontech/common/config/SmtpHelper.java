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
import com.cannontech.encryption.SystemPublisherMetadataCryptoUtils;
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
    private Map<String, String> smtpConfigSettings = new ConcurrentHashMap<>();
    private Map<SystemEmailSettingsType, String> systemEmailSettingsCache = new ConcurrentHashMap<>(5);
    // local helper copies of common properties
    private volatile String cachedHost;
    private volatile String cachedPort;
    private volatile String cachedTls;

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
                        reloadSettings();
                    }
                });
        // update the cache with latest settings from configuration files.
        reloadSettings();
        // update the cache with latest setting. Configuration properties takes higher priority over global settings.
        reloadCommonProperties();

        update(SystemEmailSettingsType.MAIL_FROM_ADDRESS, globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        update(SystemEmailSettingsType.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
        update(SystemEmailSettingsType.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
        update(SystemEmailSettingsType.SUBSCRIBER_EMAIL_IDS,
                StringUtils.join(subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG), ","));
        update(SystemEmailSettingsType.SMTP_HOST, cachedHost);
        update(SystemEmailSettingsType.SMTP_PORT, cachedPort);
        update(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE, cachedTls);
    }

    /**
     * Updates the common smtp settings into the cache for email notifications to the cache either from file or global setting.
     */
    private void reloadCommonProperties() {
        cachedHost = loadCommonProperty(SmtpPropertyType.HOST);
        cachedPort = loadCommonProperty(SmtpPropertyType.PORT);
        cachedTls = loadCommonProperty(SmtpPropertyType.START_TLS_ENABLED);
    }

    /**
     * Updates the smtp settings into the cache for email notifications
     * Specifically updates the common properties settings to the cache from File.
     */
    public void reloadSettings() {
        smtpConfigSettings.clear();
        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Map<String, String> smtpConfig = configurationLoader.getConfigSettings().get(SMTP_CONFIGURATION_KEY_ALIAS);
        if (!CollectionUtils.isEmpty(smtpConfig)) {
            smtpConfigSettings.putAll(smtpConfig);
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
        Set<String> keys = smtpConfigSettings.keySet();
        for (String key : keys) {
            if (Pattern.matches(propertyType.getRegEx(), key.toLowerCase())) {
                // key already exists for smtp or smtps
                smtpPropertyValue = smtpConfigSettings.get(key);
                log.info(propertyType.getPropertyName()
                        + " key found in configuration settings, overriding the global setting with value : "
                        + smtpPropertyValue);
                break;
            }
        }
        // not in configSettings, load from global settings
        if (smtpPropertyValue == null) {
            smtpPropertyValue = globalSettingDao.getString(propertyType.getGlobalSettingType());
        }
        return smtpPropertyValue;
    }

    /**
     * Retrieve configurations in key:value pair form configuration.properties. Re
     */
    public Map<String, String> getSmtpConfigSettings() {
        if (CollectionUtils.isEmpty(smtpConfigSettings)) {
            reloadSettings();
        }
        return smtpConfigSettings;
    }

    /**
     * Retrieve value for the specified key from cache.
     */
    public String getValue(SystemEmailSettingsType key) {
        if (CollectionUtils.isEmpty(systemEmailSettingsCache)) {
            readSettingsFromFile();
        }
        return systemEmailSettingsCache.get(key);
    }

    /**
     * Update cache with specified key value pair.
     */
    public void update(SystemEmailSettingsType key, String value) {
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
        encryptSetting(settings, SystemEmailSettingsType.SMTP_HOST);
        encryptSetting(settings, SystemEmailSettingsType.SMTP_PORT);
        encryptSetting(settings, SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE);
        encryptSetting(settings, SystemEmailSettingsType.SMTP_USERNAME);
        encryptSetting(settings, SystemEmailSettingsType.SMTP_PASSWORD);
        encryptSetting(settings, SystemEmailSettingsType.MAIL_FROM_ADDRESS);
        encryptSetting(settings, SystemEmailSettingsType.SUBSCRIBER_EMAIL_IDS);
        return settings;
    }

    /**
     * Return an encrypted key value pair.
     * List entry formatted as: [encryptedKey:encryptedValue]
     */
    private void encryptSetting(List<String> settings, SystemEmailSettingsType key)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        settings.add(SystemPublisherMetadataCryptoUtils.encrypt(key.toString())
                .concat(separator)
                .concat(SystemPublisherMetadataCryptoUtils.encrypt(getValue(key))));
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
                    systemEmailSettingsCache.put(
                            SystemEmailSettingsType.valueOf(SystemPublisherMetadataCryptoUtils.decrypt(tokens[0])),
                            SystemPublisherMetadataCryptoUtils.decrypt(tokens[1]));
                } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                    log.error("Error decrypting data from emailSettings.txt file", e);
                }
            });
        } catch (IOException exception) {
            log.error("Error reading emailSettings.txt file", exception);
        }
        reloadSettings();
    }
}
