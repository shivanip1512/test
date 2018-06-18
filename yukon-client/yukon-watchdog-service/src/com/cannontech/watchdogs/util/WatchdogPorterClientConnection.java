package com.cannontech.watchdogs.util;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.porter.PorterClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.watchdogs.impl.PorterServiceWatcher;

public class WatchdogPorterClientConnection extends PorterClientConnection {
    private static final Logger log = YukonLogManager.getLogger(WatchdogPorterClientConnection.class);
    private PorterServiceWatcher watcher;

    public void addWatchdogMessageListener(PorterServiceWatcher watcher) {
        this.watcher = watcher;
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof Command) {
            Command command = (Command) msg;
            if (command.getOperation() == Command.LOOP_CLIENT) {
                watcher.handleMessage(msg);
            }
        } else {
            log.debug("Other recieved message:" + msg.toString());
        }
    }

}
