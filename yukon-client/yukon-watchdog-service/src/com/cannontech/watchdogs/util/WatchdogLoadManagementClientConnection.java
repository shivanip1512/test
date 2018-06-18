package com.cannontech.watchdogs.util;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.dynamic.receive.LMControlAreaChanged;
import com.cannontech.loadcontrol.messages.LMControlAreaMsg;
import com.cannontech.message.util.Message;
import com.cannontech.watchdogs.impl.LoadManagementServiceWatcher;

public class WatchdogLoadManagementClientConnection extends LoadControlClientConnection {
    private static final Logger log = YukonLogManager.getLogger(WatchdogLoadManagementClientConnection.class);
    private LoadManagementServiceWatcher watcher;

    public void addWatchdogMessageListener(LoadManagementServiceWatcher watcher) {
        this.watcher = watcher;
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof LMControlAreaMsg || msg instanceof LMControlAreaChanged) {
            watcher.handleMessage(msg);
        } else {
            log.debug("Other recieved message:" + msg.toString());
        }
    }

}
