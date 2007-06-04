package com.cannontech.foreign.itron.client;

public interface ItronClient {
    
    public void invokeOneWay(Object...objects) throws Exception;
    
    public Object invoke(Object... objects) throws Exception;
    
}
