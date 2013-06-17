package com.cannontech.core.dynamic;

import com.cannontech.message.dispatch.message.Signal;

/**
 * Implement this interface to receive Signals from Yukon.
 * @see com.cannontech.core.dynamic.AsyncDynamicDataSource
 * @author alauinger
 */
public interface SignalListener {
    public void signalReceived(Signal signal);
}
