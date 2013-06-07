/*
 * Created on Jul 30, 2003
 */
package com.cannontech.messaging.util;

import java.util.EventObject;

import com.cannontech.messaging.message.BaseMessage;

/**
 * A Message event for cti messages.
 * @author aaron
 * @see com.cannontech.messaging.util.MessageListener
 */
public class MessageEvent extends EventObject {

    private BaseMessage message;

    public MessageEvent(Object src, BaseMessage msg) {
        super(src);
        setMessage(msg);
    }

    public BaseMessage getMessage() {
        return message;
    }

    public void setMessage(BaseMessage message) {
        this.message = message;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ": " + message.getClass().getName();
    }
}
