package com.cannontech.tools.smtp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class SmtpMetadataCacheUtil {
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;

    private final Map<String, String> smtpMetadataCache = new ConcurrentHashMap<>(7);

    @PostConstruct
    public void init() {
        smtpMetadataCache.put(SmtpMetadataConstants.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
        smtpMetadataCache.put(SmtpMetadataConstants.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
        smtpMetadataCache.put(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE, globalSettingDao
                .getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
        smtpMetadataCache.put(SmtpMetadataConstants.SMTP_USERNAME, globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
        smtpMetadataCache.put(SmtpMetadataConstants.SMTP_PASSWORD, globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
        smtpMetadataCache.put(SmtpMetadataConstants.MAIL_FROM_ADDRESS,
                globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS));
        smtpMetadataCache.put(SmtpMetadataConstants.SUBSCRIBER_EMAIL_IDS,
                StringUtils.join(subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG), ","));

        asyncDynamicDataSource.addDatabaseChangeEventListener(event -> {
            if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_HOST)) {
                smtpMetadataCache.put(SmtpMetadataConstants.SMTP_HOST, globalSettingDao.getString(GlobalSettingType.SMTP_HOST));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PORT)) {
                smtpMetadataCache.put(SmtpMetadataConstants.SMTP_PORT, globalSettingDao.getString(GlobalSettingType.SMTP_PORT));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_ENCRYPTION_TYPE)) {
                smtpMetadataCache.put(SmtpMetadataConstants.SMTP_ENCRYPTION_TYPE, globalSettingDao
                        .getString(GlobalSettingType.SMTP_ENCRYPTION_TYPE));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_USERNAME)) {
                smtpMetadataCache.put(SmtpMetadataConstants.SMTP_USERNAME,
                        globalSettingDao.getString(GlobalSettingType.SMTP_USERNAME));
            } else if (globalSettingDao.isDbChangeForSetting(event, GlobalSettingType.SMTP_PASSWORD)) {
                smtpMetadataCache.put(SmtpMetadataConstants.SMTP_PASSWORD,
                        globalSettingDao.getString(GlobalSettingType.SMTP_PASSWORD));
            }
        });
    }

    public String getValue(String key) {
        return smtpMetadataCache.get(key);
    }

    public void update(String key, String value) {
        smtpMetadataCache.put(key, value);
    }
}
