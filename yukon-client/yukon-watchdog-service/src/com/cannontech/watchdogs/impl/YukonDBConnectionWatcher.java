package com.cannontech.watchdogs.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.SmtpHelper;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.tools.email.SystemEmailSettingsType;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.model.Watchdogs;
import com.cannontech.watchdogs.util.DBConnectionUtil;

@Service
public class YukonDBConnectionWatcher extends ServiceStatusWatchdogImpl {

    public YukonDBConnectionWatcher(ConfigurationSource configSource) {
        super(configSource);
    }

    Logger log = YukonLogManager.getLogger(YukonDBConnectionWatcher.class);

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;
    @Autowired private SmtpHelper SmtpHelper;

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus serviceStatus = DBConnectionUtil
                .isDBConnected(DBName.YUKON) ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
        // If Database is running verify it by using SQL query. Save the results in file system so that it can be reused.
        if (serviceStatus == ServiceStatus.RUNNING) {
            try {
                List<String> subscriberEmailIds = subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG);
                SmtpHelper.updateCachedValue(SystemEmailSettingsType.WATCHDOG_SUBSCRIBER_EMAILS.getKey(),
                        StringUtils.join(subscriberEmailIds, ","));
                SmtpHelper.writeToFile();
            } catch (RuntimeException e) {
                serviceStatus = ServiceStatus.STOPPED;
            }
        }
        log.info("Status of Database " + serviceStatus);
        return generateWarning(WatchdogWarningType.YUKON_DATABASE, serviceStatus);
    }

    @Override
    public YukonServices getServiceName() {
        return YukonServices.DATABASE;
    }

    @Override
    public Watchdogs getName() {
        return Watchdogs.DB_CONNECTION;
    }

    @Override
    public boolean isServiceRunning() {
        return DBConnectionUtil.isDBConnected(DBName.YUKON);
    }
}
