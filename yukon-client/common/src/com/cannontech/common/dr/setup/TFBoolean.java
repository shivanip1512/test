package com.cannontech.common.dr.setup;

import com.cannontech.common.util.DatabaseRepresentationSource;

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

    public abstract boolean getBoolean();

    public static TFBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }
}
