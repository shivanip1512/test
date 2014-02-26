package com.cannontech.dr.rfn.service.impl;

/**
 * This exception should be used any time the RFN LCR simulator reaches an error condition but should
 * still be allowed to continue functioning.  For example, EXI encoding could fail from one malformed message
 * but work correctly for other messages that contain a different data set.  In this case the failed encoding
 * should be logged but the simulator should continue.
 * 
 * @author E9815731 - Garrett DeZeeuw
 */
public final class RfnLcrSimulatorException extends Exception {

    public RfnLcrSimulatorException(String message) {
        super(message);
    }

    public RfnLcrSimulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
