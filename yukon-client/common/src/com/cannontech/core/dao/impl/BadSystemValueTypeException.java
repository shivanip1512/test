package com.cannontech.core.dao.impl;

import com.cannontech.core.dao.PersistedSystemValueKey;

public class BadSystemValueTypeException extends RuntimeException {
    public BadSystemValueTypeException(PersistedSystemValueKey property, String value, Throwable cause) {
        super("Unable to convert value of \"" + value + "\" for " + property, cause);
    }
}
