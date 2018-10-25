package com.cannontech.watchdogs.util;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.message.util.Message;
import com.cannontech.watchdogs.impl.DispatchServiceWatcher;

public class WatchdogDispatchClientConnection extends DispatchClientConnection {
    private static final Logger log = YukonLogManager.getLogger(WatchdogDispatchClientConnection.class);
    private DispatchServiceWatcher watcher;

    public void addWatchdogMessageListener(DispatchServiceWatcher watcher) {
        this.watcher = watcher;
    }

    protected void fireMessageEvent(Message msg) {
        if (msg instanceof Command) {
            Command command = (Command) msg;
            if (command.getOperation() == Command.LOOP_CLIENT) {
                watcher.handleMessage(msg);
            }
        } else {
            log.debug("Other received message:" + msg.toString());
        }
    }

}
