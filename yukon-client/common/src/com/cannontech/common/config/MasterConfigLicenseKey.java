package com.cannontech.common.config;

import java.util.HashMap;
import java.util.Map;

public enum MasterConfigLicenseKey {
    
    
    /*
     * This class exist to check the value of a MasterConfigString with a gUID like key.
     * We can then use the string to lock access to certain links/menu items/classes.
     * Please look at the annotation CheckCparmString if you would like Lock out access to a method or class.
     * 
     * If a menu item needs to be locked out please look at menu_config.xml, specifically look for DEMAND_MEASUREMENT_VERIFICATION_ENABLED
     * 
     * The value of the key will be obfuscated by combining the key from five separate parts.
     */
    
    DEMAND_MEASUREMENT_VERIFICATION_ENABLED("452C3B88", 7122, 18058, 28002, "FDFB58B528B5"), //452C3B88-1BD2/7122-468A/18058-6D62/28002-FDFB58B528B5
    ;
    
    
    public static final Map<String, MasterConfigLicenseKey> configMap = new HashMap<String, MasterConfigLicenseKey>() {{
        put("DEMAND_MEASUREMENT_VERIFICATION_ENABLED", DEMAND_MEASUREMENT_VERIFICATION_ENABLED);
    }};
    
    private String key1;
    private int key2;
    private int key3;
    private int key4;
    private String key5;
    
    MasterConfigLicenseKey(String key1, int key2, int key3, int key4, String key5) {
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
        this.key4 = key4;
        this.key5 = key5;
    }

    public String getKey() {
        StringBuilder retVal = new StringBuilder();
        retVal.append(key1+"-");
        retVal.append(String.format("%04X", key2)+"-");
        retVal.append(String.format("%04X", key3)+"-");
        retVal.append(String.format("%04X", key4)+"-");
        retVal.append(key5);
        return retVal.toString();
    }

}
