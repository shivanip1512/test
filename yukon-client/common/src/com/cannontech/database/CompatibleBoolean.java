package com.cannontech.database;

import org.springframework.util.StringUtils;

/** 
 * For reading boolean values from the database.
 * 
 * When writing a boolean to the database, the default JDBC way should be used, when reading values
 * from legacy DAO's which may have used strings or Y/N to indicate boolean values, use this
 * enum.
 * 
 * This should not be used outside of the database package.
 */
/*package*/ enum CompatibleBoolean {
    FALSE(false),
    TRUE(true),
    NULL(null),
    ;

    private final Boolean value;

    CompatibleBoolean(Boolean value) {
    	this.value = value;
    }

    public Boolean getBoolean() {
    	return value;
    }

    public static CompatibleBoolean valueOfStr(String value) {
    	String input = (value != null) ? value.trim().toLowerCase() : null;
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
