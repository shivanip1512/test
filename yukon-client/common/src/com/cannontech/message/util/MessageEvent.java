/*
 * Created on Jul 30, 2003
 */
package com.cannontech.message.util;

import java.util.EventObject;

/**
 * A Message event for cti messages.
 * @author aaron
 * @see com.cannontech.message.util.MessageListener
 */
public class MessageEvent extends EventObject {
	private Message message;
	
	public MessageEvent(Object src, Message msg) {
		super(src);	
		setMessage(msg);
	}

	/**
	 * @return
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * @param message
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/* 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getClass().getName() + ": " + message.getClass().getName();
	}
}
