package com.cannontech.watchdogs.message;

import com.cannontech.message.util.Message;

public interface WatchdogMessageListener {
    /**
     * Listens messages for Porter, Dispatch,MACS, CapControl, LoadManagement.
     */
    public void handleMessage(Message e);
}
