package com.cannontech.core.dynamic;

import com.cannontech.messaging.message.dispatch.SignalMessage;

/**
 * Implement this interface to receive Signals from Yukon.
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource
 * @author alauinger
 */
public interface SignalListener {
    public void signalReceived(SignalMessage signal);
}
