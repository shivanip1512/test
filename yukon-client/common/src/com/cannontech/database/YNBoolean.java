package com.cannontech.database;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum YNBoolean implements DatabaseRepresentationSource {
    YES {
        @Override
        public Object getDatabaseRepresentation() {
            return "y";
        }
        
        @Override
        public boolean getBoolean() {
            return true;
        }
    },
    NO{
        @Override
        public Object getDatabaseRepresentation() {
            return "n";
        }
        
        @Override
        public boolean getBoolean() {
            return false;
        }
    },
    ;

    public abstract boolean getBoolean();
    
    public static YNBoolean valueOf(boolean value) {
        return value ? YES : NO;
    }
}
