package com.cannontech.database;

import com.cannontech.common.util.DatabaseRepresentationSource;

public enum YNBoolean implements DatabaseRepresentationSource {
    YES {
        @Override
        public Object getDatabaseRepresentation() {
            return "Y";
        }
        
        @Override
        public boolean getBoolean() {
            return true;
        }
    },
    NO{
        @Override
        public Object getDatabaseRepresentation() {
            return "N";
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
    
    public static YNBoolean valueOf(char value) {
        if(value == 'Y' || value == 'y') return YES;
        if(value == 'N' || value == 'n') return NO;
        return null;
    }
}
