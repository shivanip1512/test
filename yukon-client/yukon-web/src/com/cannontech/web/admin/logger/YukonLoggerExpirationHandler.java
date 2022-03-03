package com.cannontech.web.admin.logger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.logger.dao.YukonLoggerDao;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;

public class YukonLoggerExpirationHandler {
    private static final Logger log = YukonLogManager.getLogger(YukonLoggerExpirationHandler.class);

    @Autowired private YukonLoggerDao yukonLoggerDao;
    @Autowired private DbChangeManager dbChangeManager;
    private ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void scheduleExpiration() {
        Long midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
        // Scheduling to run at everyday 00:05 AM i.e after log file rolling activity.
        scheduledExecutor.scheduleAtFixedRate(() -> {
            // If any loggers deleted, publish the DB change events.
            if (yukonLoggerDao.deleteExpiredLoggers()) {
                log.debug("Deleted Expired loggers and publishing DB change message for other services.");
                dbChangeManager.processDbChange(DbChangeType.DELETE, DbChangeCategory.LOGGER, -1);
            }
        }, midnight + 5, 1440, TimeUnit.MINUTES);
    }

}
