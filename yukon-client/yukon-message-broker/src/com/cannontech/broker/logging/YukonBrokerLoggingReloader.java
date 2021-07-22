package com.cannontech.broker.logging;

import javax.annotation.PostConstruct;

import com.cannontech.clientutils.YukonLoggingReloaderHelper;
import com.cannontech.message.dispatch.message.DbChangeType;

public class YukonBrokerLoggingReloader extends YukonLoggingReloaderHelper {

    @PostConstruct
    public void initialize() {
        reloadAppenderForMaxFileSize(false);
        reloadAppenderForLogRetentionDays();
        reloadYukonLoggers(DbChangeType.NONE, -1);
    }

}
