package com.cannontech.broker.logging;

import javax.annotation.PostConstruct;

import com.cannontech.clientutils.YukonLoggingReloaderHelper;

public class YukonBrokerLoggingReloader extends YukonLoggingReloaderHelper {

    @PostConstruct
    public void initialize() {
        reloadAppenderForMaxFileSize(false);
        reloadAppenderForLogRetentionDays();
    }

}
