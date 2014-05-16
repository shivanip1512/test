package com.cannontech.common.device.commands;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum CommandRequestUnsupportedType implements DatabaseRepresentationSource {

    NOT_CONFIGURED,
    UNSUPPORTED,
    CANCELED
    ;

    @Override
    public Object getDatabaseRepresentation() {
        return name();
    }
}
