package com.cannontech.core.dynamic.exception;

public class DispatchNotConnectedException extends DynamicDataAccessException {
    public DispatchNotConnectedException() {
        super("dispatch connection is invalid");
    }
}
