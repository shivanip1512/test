/*
 * Created on Jul 30, 2003
 */
package com.cannontech.messaging.util;

/**
 * The listener interface for receiving message events.
 * @author aaron
 * @see com.cannontech.messaging.util.MessageEvent
 */
public interface MessageListener {

    /**
     * Invoked when a message is receieved.
     * @param e
     */
    public void messageReceived(MessageEvent e);
}
