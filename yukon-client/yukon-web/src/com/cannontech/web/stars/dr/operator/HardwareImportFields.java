package com.cannontech.web.stars.dr.operator;

public enum HardwareImportFields {
    ACCOUNT_NO("a"),
    HW_ACTION("b"),
    DEVICE_TYPE("a"),
    DEVICE_LABEL(null),
    SERIAL_NO("a"),
    INSTALL_DATE("c"),
    REMOVE_DATE(null),
    SERVICE_COMPANY(null),
    PROGRAM_NAME("d"),
    ADDR_GROUP("e"),
    APP_TYPE("d"),
    APP_KW(null),
    APP_RELAY_NUM("d"),
    MAC_ADDRESS(null),
    DEVICE_VENDOR_USER_ID(null),
    OPTION_PARAMS(null),
    LATITUDE(null),
    LONGITUDE(null);
    
    private String legendKey;
    
    private HardwareImportFields(String legendKey) {
        this.legendKey = legendKey;
    }

    public String getLegendKey() {
        return legendKey;
    }
    
    public String getName() {
        return this.name();
    }
}