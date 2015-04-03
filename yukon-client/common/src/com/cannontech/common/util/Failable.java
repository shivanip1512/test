package com.cannontech.common.util;

/**
 * Interface for any operation that can have an error occur, causing the operation to fail.
 */
public interface Failable {
    
    void errorOccurred(Throwable t);
    
    boolean isErrorOccurred();
    
    Throwable getError();
}