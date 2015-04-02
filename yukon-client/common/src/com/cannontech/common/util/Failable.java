package com.cannontech.common.util;

public interface Failable {
    
    void errorOccurred(Throwable t);
    
}