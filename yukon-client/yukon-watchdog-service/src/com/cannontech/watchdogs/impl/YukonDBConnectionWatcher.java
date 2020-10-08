package com.cannontech.watchdogs.impl;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.smartNotification.dao.SmartNotificationSubscriptionDao;
import com.cannontech.common.smartNotification.model.SmartNotificationEventType;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdogs.impl.ServiceStatusWatchdogImpl.ServiceStatus;

@Service
public class YukonDBConnectionWatcher extends DBConnectionWatchdogImpl {

    Logger log = YukonLogManager.getLogger(YukonDBConnectionWatcher.class);

    @Autowired private SmartNotificationSubscriptionDao subscriptionDao;

    @Override
    public List<WatchdogWarnings> watch() {
        ServiceStatus serviceStatus = DBConnectionUtil
                .isDBConnected(DBName.YUKON) ? ServiceStatus.RUNNING : ServiceStatus.STOPPED;
        // If Database is running verify it by using SQL query. Save the results in file system so that it can be reused.
        if (serviceStatus == ServiceStatus.RUNNING) {
            try {
            List<String> subscriberEmailIds = subscriptionDao.getSubscribedEmails(SmartNotificationEventType.YUKON_WATCHDOG);
            //In YUK-22935, we will keep these IDs in file system.
            }catch (RuntimeException e) {
                serviceStatus = ServiceStatus.STOPPED;
            }
        }
        log.info("Status of Database " + serviceStatus);
        return generateWarning(WatchdogWarningType.YUKON_DATABASE, serviceStatus);
    }
}
