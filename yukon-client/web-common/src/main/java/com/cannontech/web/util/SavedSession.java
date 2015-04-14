package com.cannontech.web.util;

import java.util.Properties;

public final class SavedSession {
    
    private Properties properties;
    private String referer;
    
    private SavedSession(Properties properties, String referer) {
        this.properties = properties;
        this.referer = referer;
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public String getReferer() {
        return referer;
    }
    
    public static SavedSession of(Properties properties, String referer) {
        return new SavedSession(properties, referer);
    }
    
    @Override
    public String toString() {
        return String.format("SavedSession [properties=%s, referer=%s]", properties, referer);
    }
    
}