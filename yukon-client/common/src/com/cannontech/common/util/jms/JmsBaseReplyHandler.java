package com.cannontech.common.util.jms;

public interface JmsBaseReplyHandler {
    /**
     * Called when an exception is thrown during normal execution. It is
     * guaranteed that {@link #complete()} will be called subsequently. Other methods
     * may or may not be called as expected.
     */
    public void handleException(Exception e);

    /**
     * Guaranteed to be called last whether or not there were errors.
     */
    public void complete();
}
