package com.cannontech.common.rfn.dao.impl;

/**
 * Thrown when an error occurs related to RFN Gateway data.
 */
public class GatewayDataException extends Exception {
    public GatewayDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
