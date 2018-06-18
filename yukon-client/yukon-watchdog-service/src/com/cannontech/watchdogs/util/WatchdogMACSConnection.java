package com.cannontech.watchdogs.util;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.util.Message;
import com.cannontech.watchdogs.impl.MACSServiceWatcher;
import com.cannontech.yukon.conns.ServerMACSConnection;

public class WatchdogMACSConnection extends ServerMACSConnection {
    private static final Logger log = YukonLogManager.getLogger(WatchdogMACSConnection.class);
    private MACSServiceWatcher watcher;

    public void addWatchdogMessageListener(MACSServiceWatcher watcher) {
        this.watcher = watcher;
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof Schedule) {
            watcher.handleMessage(msg);
        } else {
            log.debug("Other recieved message:" + msg.toString());
        }
    }
}
