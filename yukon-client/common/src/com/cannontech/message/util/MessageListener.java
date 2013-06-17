/*
 * Created on Jul 30, 2003
 */
package com.cannontech.message.util;

/**
 * The listener interface for receiving message events.
 * @author aaron
 * @see com.cannontech.message.util.MessageEvent
 */
public interface MessageListener {
	/**
	 * Invoked when a message is receieved.
	 * @param e
	 */
	public void messageReceived(MessageEvent e);
}
