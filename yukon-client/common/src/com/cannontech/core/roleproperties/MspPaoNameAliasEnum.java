package com.cannontech.core.roleproperties;


public enum MspPaoNameAliasEnum {

    METER_NUMBER ("Meter Number (Default)") ,
    ACCOUNT_NUMBER ("Account Number"),
    SERVICE_LOCATION ("Service Location"),
    CUSTOMER_ID ("Customer"),
    EA_LOCATION ("EA Location"),
    GRID_LOCATION ("Grid Location"),
    POLE_NUMBER ("Pole Number"),
    ;
   
    private String displayName;
    
    private MspPaoNameAliasEnum(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
