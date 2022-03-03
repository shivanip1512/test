package com.cannontech.common.events.helper;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.SystemEventLogService;

public class EventLogHelper {

    @Autowired private SystemEventLogService systemEventLogService;

    private static final Logger log = YukonLogManager.getLogger(EventLogHelper.class);

    public void decryptionFailedEventLog(String yukonService, String type) {
        try {
            systemEventLogService.decryptionFailed(yukonService, InetAddress.getLocalHost().getHostName(), type, Instant.now());
        } catch (UnknownHostException ex) {
            log.error("Unknown host exception : \"" + ex.getMessage() + "\".");
        }
    }
}
