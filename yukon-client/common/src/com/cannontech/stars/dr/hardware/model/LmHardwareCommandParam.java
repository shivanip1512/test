package com.cannontech.stars.dr.hardware.model;

public enum LmHardwareCommandParam {
    
    OPTIONAL_ROUTE_ID(Integer.class), // XCEL BATCH COMMANDS ONLY
    OPTIONAL_GROUP_ID(Integer.class), // XCEL BATCH COMMANDS ONLY
    GROUP_ID(String.class), //NM ONLY
    EXPIRATION_DURATION(Long.class), // NM ONLY
    EXPECT_RESPONSE(Boolean.class), // NM ONLY
    BULK(Boolean.class), // NM ONLY
    PRIORITY(Integer.class),
    FORCE_IN_SERVICE(Boolean.class);
    
    private Class<?> clazz;
    private LmHardwareCommandParam(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Class<?> getClazz() {
        return clazz;
    };
    
    //TODO is this stupid?
    public Object getObjectValue(Object value) {
        return clazz.cast(value);
    }

}