package com.cannontech.common.config;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

public enum MasterConfigLicenseKey {
    
    /*
     * This class exists to check the value of a MasterConfigString with a GUID key.
     * We can then use the string to lock access to certain links/menu items/classes.
     * Please look at the annotation CheckCparmString if you would like Lock out access to a method or class.
     * 
     * If a menu item needs to be locked out please look at menu_config.xml, specifically look for DEMAND_MEASUREMENT_VERIFICATION_ENABLED
     * 
     * The value of the key will be obfuscated by combining the key from five separate parts.
     */
    
    DEMAND_MEASUREMENT_VERIFICATION_ENABLED("452C3B88", 7122, 18058, 28002, "FDFB58B528B5"), //452C3B88-1BD2-468A-6D62-FDFB58B528B5
    METER_PROGRAMMING_ENABLED("130A06FD", 55770, 19918, 46271, "837A991F5383"), //130A06FD-D9DA-4DCE-B4BF-837A991F5383
    ;
    
    
    public static final Map<String, MasterConfigLicenseKey> configMap = 
            Maps.uniqueIndex(Arrays.asList(values()), MasterConfigLicenseKey::name);
    
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
        return key1 + "-" 
                + String.format("%04X", key2) + "-" 
                + String.format("%04X", key3) + "-" 
                + String.format("%04X", key4) + "-" 
                + key5;
    }

}
