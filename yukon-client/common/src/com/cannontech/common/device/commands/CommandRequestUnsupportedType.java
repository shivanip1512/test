package com.cannontech.common.device.commands;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CommandRequestUnsupportedType implements DatabaseRepresentationSource {

    NOT_CONFIGURED,
    UNSUPPORTED,
    CANCELED,
    INVALID_STATE,
    ALREADY_CONFIGURED
    ;

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
}
