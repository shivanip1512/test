package com.cannontech.database;

import com.cannontech.common.util.DatabaseRepresentationSource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TFBoolean implements DatabaseRepresentationSource {
    TRUE {
        @Override
        public Object getDatabaseRepresentation() {
            return 'T';
        }

        @Override
        public boolean getBoolean() {
            return true;
        }
    },
    FALSE {
        @Override
        public Object getDatabaseRepresentation() {
            return 'F';
        }

        @Override
        public boolean getBoolean() {
            return false;
        }
    };

    @JsonValue
    public abstract boolean getBoolean();

    @JsonCreator
    public static TFBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }
}
