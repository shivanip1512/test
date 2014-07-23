package com.cannontech.billing.format.itron;

public interface ItronClient {
    
    public void invokeOneWay(Object...objects) throws Exception;
    
    public Object invoke(Object... objects) throws Exception;
    
}
