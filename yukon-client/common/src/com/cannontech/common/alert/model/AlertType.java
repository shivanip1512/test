package com.cannontech.common.alert.model;

public enum AlertType {

    ALARM("Alarm"),
    CAPCONTROL_SERVER_RESPONSE("Server Response");
    
    private final String alertName;
    
    private AlertType ( String alertName ) {
        this.alertName = alertName;
    }
    
    public String getValue() {
        return alertName;
    }
    
    public static AlertType getEnum(String name) {
        AlertType ret = null;
        for( AlertType dir : AlertType.values() ) {
            if( dir.getValue().equalsIgnoreCase(name)) {
                ret = dir;
                break;
            }
        }
        return ret;
    }

}
