package com.cannontech.database;

import org.springframework.util.StringUtils;

public enum CompatibleBoolean {
    FALSE(false),
    TRUE(true),
    NULL(null)
    ;
    
    private final Boolean value;
    
    CompatibleBoolean(Boolean value) {
    	this.value = value;
    }
    
    public Boolean getBoolean() {
    	return value;
    }
    
    public static CompatibleBoolean valueOfStr(String value) {
    	String input = (value != null ? value.trim().toLowerCase() : null);
		if (!StringUtils.hasLength(input)) {
			return NULL;
		} else if (input.equals("1") ||input.equals("y") || input.equals("true")) {
			return TRUE;
		} else if (input.equals("0") || input.equals("n") || input.equals("false")) {
			return FALSE;
		} else {
			throw new IllegalArgumentException("Invalid boolean value [" + value + "]");
		}
    }
}
