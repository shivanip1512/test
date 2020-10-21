package com.cannontech.tools.email.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.encryption.SystemPublisherMetadataCryptoUtils;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailSettingsCacheService;
import com.cannontech.tools.email.SystemEmailSettingsType;

public class EmailSettingsCacheServiceImpl implements EmailSettingsCacheService {

    private static Logger log = YukonLogManager.getLogger(EmailSettingsCacheServiceImpl.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;

    private final Map<SystemEmailSettingsType, String> systemEmailSettingsCache = new ConcurrentHashMap<>(7);
    private static final String separator = ":";

    private static final String fineName = "/Server/Config/System/emailSettings.txt";
    private static File emailSettingsFile = null;

    static {
        try {
            String yukonBase = CtiUtilities.getYukonBase();
            emailSettingsFile = new File(yukonBase, fineName);
            if (!emailSettingsFile.exists()) {
                emailSettingsFile.getParentFile().mkdir();
                emailSettingsFile.createNewFile();
            }

        } catch (IOException e) {
            log.error("Error creating emailSettings.txt file", e);
        }
    }

    @PostConstruct
    public void init() {
        update(SystemEmailSettingsType.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        update(SystemEmailSettingsType.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
        update(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE, globalSettingDao.getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
        update(SystemEmailSettingsType.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
        update(SystemEmailSettingsType.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
        update(SystemEmailSettingsType.MAIL_FROM_ADDRESS, globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        update(SystemEmailSettingsType.SUBSCRIBER_EMAIL_IDS,
                StringUtils.join(subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG), ","));

        // SUBSCIBER_EMAIL_IDS is not updated through dbChange
        // when new emails are loaded, cached should be called and updated at that time. See YukonDBCOnnectionWatcher for example.
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_HOST)) {
                update(SystemEmailSettingsType.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PORT)) {
                update(SystemEmailSettingsType.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_ENCRYPTION_TYPE)) {
                update(SystemEmailSettingsType.SMTP_ENCRYPTION_TYPE,
                        globalSettingDao.getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_USERNAME)) {
                update(SystemEmailSettingsType.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PASSWORD)) {
                update(SystemEmailSettingsType.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
            }
        });
    }

    @Override
    public String getValue(SystemEmailSettingsType key) {
        return systemEmailSettingsCache.get(key);
    }

    @Override
    public void update(SystemEmailSettingsType key, String value) {
        systemEmailSettingsCache.put(key, value);
    }

    @Override
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
     * Method to return a list of encrypted key and value pairs.
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

    public static Map<SystemEmailSettingsType, String> readFromFile() {
        Map<SystemEmailSettingsType, String> systemEmailSettingsMap = new HashMap<SystemEmailSettingsType, String>();
        try (Stream<String> lines = Files.lines(Paths.get(emailSettingsFile.toURI()), Charset.defaultCharset())) {
            lines.forEach(line -> {
                try {
                    String tokens[] = line.split(separator);
                    systemEmailSettingsMap.put(SystemEmailSettingsType.valueOf(SystemPublisherMetadataCryptoUtils.decrypt(tokens[0])),
                            SystemPublisherMetadataCryptoUtils.decrypt(tokens[1]));
                } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                    log.error("Error decrypting data from emailSettings.txt file", e);
                }
            });
        } catch (IOException exception) {
            log.error("Error reading emailSettings.txt file", exception);
        }
        return systemEmailSettingsMap;
    }
}