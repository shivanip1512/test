package com.cannontech.common.util.jms;

import java.io.Serializable;

public interface JmsReplyHandler<T extends Serializable> extends JmsBaseReplyHandler {
    /**
     * Will be called if the first response is not received before timing out. If this is called,
     * {@link #handleReply(Serializable)} and {@link #handleTimeout()} will not be called.
     */
    public void handleTimeout();

    /**
     * Called when the reply is received before timing out. If this is called,
     * {@link #handleTimeout()} could not have been called and will not be called.
     */
    public void handleReply(T t);

    /**
     * Used to retrieve the Class of the type expected for the first reply. May be called multiple
     * times.
     */
    public Class<T> getExpectedType();
}
