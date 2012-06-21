package com.cannontech.dr.rfn.message;

import com.cannontech.dr.rfn.message.archive.RfnLcrReading;

/**
 * Use this interface on message classes that will contain an {@link RfnLcrReading} 
 */
public interface RfnLcrDataMessage {
    
    public RfnLcrReading getData();
    
}