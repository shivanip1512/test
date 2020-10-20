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
import com.cannontech.tools.smtp.SmtpMetadataConstants;

public class EmailSettingsCacheServiceImpl implements EmailSettingsCacheService {

    private static Logger log = YukonLogManager.getLogger(EmailSettingsCacheServiceImpl.class);
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;

    private final Map<SmtpMetadataConstants, String> smtpMetadataCache = new ConcurrentHashMap<>(7);
    private static final String SEPARATOR = ":";

    private static final String FILE_NAME = "/Server/Config/System/emailSettings.txt";
    private static File SMTP_META_DATA_FILE = null;

    static {
        try {
            String yukonBase = CtiUtilities.getYukonBase();
            SMTP_META_DATA_FILE = new File(yukonBase, FILE_NAME);
            if (!SMTP_META_DATA_FILE.exists()) {
                SMTP_META_DATA_FILE.getParentFile().mkdir();
                SMTP_META_DATA_FILE.createNewFile();
            }

        } catch (IOException e) {
            log.error("Error creating emailSettings.txt file", e);
        }
    }

    @PostConstruct
    public void init() {
        update(SmtpMetadataConstants.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        update(SmtpMetadataConstants.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
        update(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE, globalSettingDao.getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
        update(SmtpMetadataConstants.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
        update(SmtpMetadataConstants.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
        update(SmtpMetadataConstants.MAIL_FROM_ADDRESS, globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        update(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS,
                StringUtils.join(subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG), ","));

        // SUBSCIBER_EMAIL_IDS is not updated through dbChange
        // when new emails are loaded, cached should be called and updated at that time. See YukonDBCOnnectionWatcher for example.
        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_HOST)) {
                update(SmtpMetadataConstants.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PORT)) {
                update(SmtpMetadataConstants.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_ENCRYPTION_TYPE)) {
                update(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE,
                        globalSettingDao.getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_USERNAME)) {
                update(SmtpMetadataConstants.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PASSWORD)) {
                update(SmtpMetadataConstants.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
            }
        });
    }

    @Override
    public String getValue(SmtpMetadataConstants key) {
        return smtpMetadataCache.get(key);
    }

    @Override
    public void update(SmtpMetadataConstants key, String value) {
        smtpMetadataCache.put(key, value);
    }

    @Override
    public void writeToFile() {
        try {
            List<String> smtpMetadata = new ArrayList<String>();
            smtpMetadata.addAll(getEncryptedSettings());
            Files.write(SMTP_META_DATA_FILE.toPath(), smtpMetadata);
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
        encryptSetting(settings, SmtpMetadataConstants.SMTP_HOST);
        encryptSetting(settings, SmtpMetadataConstants.SMTP_PORT);
        encryptSetting(settings, SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE);
        encryptSetting(settings, SmtpMetadataConstants.SMTP_USERNAME);
        encryptSetting(settings, SmtpMetadataConstants.SMTP_PASSWORD);
        encryptSetting(settings, SmtpMetadataConstants.MAIL_FROM_ADDRESS);
        encryptSetting(settings, SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS);
        return settings;
    }

    /**
     * Return an encrypted key value pair.
     * List entry formatted as: [encryptedKey:encryptedValue]
     */
    private void encryptSetting(List<String> settings, SmtpMetadataConstants key)
            throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        settings.add(SystemPublisherMetadataCryptoUtils.encrypt(key.toString())
                .concat(SEPARATOR)
                .concat(SystemPublisherMetadataCryptoUtils.encrypt(getValue(key))));
    }

    @Override
    public Map<SmtpMetadataConstants, String> readFromFile() {
        Map<SmtpMetadataConstants, String> metadataMap = new HashMap<SmtpMetadataConstants, String>();
        try (Stream<String> lines = Files.lines(Paths.get(SMTP_META_DATA_FILE.toURI()), Charset.defaultCharset())) {
            lines.forEach(line -> {
                try {
                    String tokens[] = line.split(SEPARATOR);
                    metadataMap.put(SmtpMetadataConstants.valueOf(SystemPublisherMetadataCryptoUtils.decrypt(tokens[0])),
                            SystemPublisherMetadataCryptoUtils.decrypt(tokens[1]));
                } catch (IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
                    log.error("Error decrypting data from emailSettings.txt file", e);
                }
            });
        } catch (IOException exception) {
            log.error("Error reading emailSettings.txt file", exception);
        }
        return metadataMap;
    }
}