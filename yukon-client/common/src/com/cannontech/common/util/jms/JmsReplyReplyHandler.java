package com.cannontech.common.util.jms;

import java.io.Serializable;

public interface JmsReplyReplyHandler<T1 extends Serializable, T2 extends Serializable> {
    
    /**
     * Will be called if the first response is not received before timing out. If this is called,
     * {@link #handleReply1(Serializable)}, {@link #handleTimeout2()}, and {@link #handleReply2(Serializable)}
     * will not be called.
     */
    public void handleTimeout1();
    
    /**
     * Called when the first reply is received before timing out. If this is called, {@link #handleTimeout1()}
     * could not have been called and will not be called.
     * @param t
     * @return
     */
    public boolean handleReply1(T1 t);
    
    /**
     * Used to retrieve the Class of the type expected for the first reply. May be called multiple times.
     * @return
     */
    public Class<T1> getExpectedType1();
    
    /**
     * Will be called if the second response is not received before timing out. If this is called,
     * {@link #handleReply2(Serializable)}
     * will not be called.
     */
    public void handleTimeout2();
    
    /**
     * Called when the second reply is received before timing out. If this is called, {@link #handleTimeout2()}
     * could not have been called and will not be called.
     * @param t
     * @return
     */
    public void handleReply2(T2 t);
    
    /**
     * Used to retrieve the Class of teh type expected for the second reply. May be called multiple times.
     * @return
     */
    public Class<T2> getExpectedType2();
    
    /**
     * Called when an exception is thrown during normal execution. It is 
     * guaranteed that {@link #complete()} will be called subsequently. Other methods
     * may or may not be called as expected.
     * @param e
     */
    public void handleException(Exception e);
    
    /**
     * Guaranteed to be called last whether or not there were errors.
     */
    public void complete();
}
