package com.cannontech.common.util.jms;

import java.io.Serializable;

public interface JmsReplyReplyHandler<T1 extends Serializable, T2 extends Serializable> {
    public boolean handleReply1(T1 t);
    public void handleTimeout1();
    public Class<T1> getExpectedType1();
    
    public void handleReply2(T2 t);
    public void handleTimeout2();
    public Class<T2> getExpectedType2();
    
    public void handleException(Exception e);
    
    public void complete();
}
