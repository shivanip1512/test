package com.cannontech.common.config;

public enum MasterConfigLicenseKey {
    
    /*
     * This class exist to check the value of a MasterConfigString with a gUID like key.
     * We can then use the string to lock access to certain links/menu items/classes.
     * Please look at the annotation CheckCparmString if you would like Lock out access to a method or class.
     * 
     * If a menu item needs to be locked out please look at menu_config.xml, specifically look for CAP_CONTROL_ENABLE_DMV_TEST
     * 
     */
    
    CAP_CONTROL_ENABLE_DMV_TEST("somethingTBDmarketingInTheFuturePossibly"),
    ;
    
    
    private String key;
    
    MasterConfigLicenseKey(String key) {
        this.setKey(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
