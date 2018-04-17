package com.cannontech.stars.dr.hardware.model;

import org.joda.time.Duration;

public enum LmHardwareCommandParam {
    
    OPTIONAL_ROUTE_ID(Integer.class), // XCEL BATCH COMMANDS ONLY
    OPTIONAL_GROUP_ID(Integer.class), // XCEL BATCH COMMANDS ONLY
    GROUP_ID(String.class), //NM ONLY
    EXPIRATION_DURATION(Long.class), // NM ONLY
    EXPECT_RESPONSE(Boolean.class), // NM ONLY
    BULK(Boolean.class), // NM ONLY
    PRIORITY(Integer.class),
    DURATION(Duration.class), // Currently only used for opt out duration in hours
    FORCE_IN_SERVICE(Boolean.class),
    SPID(Integer.class),
    UNIQUE_MESSAGE_ID(Long.class),
    RELAY(Integer.class),
    WAITABLE(Boolean.class),
    PQR_ENABLE(Boolean.class),
    PQR_LOV_TRIGGER(Double.class),
    PQR_LOV_RESTORE(Double.class),
    PQR_LOV_TRIGGER_TIME(Short.class),
    PQR_LOV_RESTORE_TIME(Short.class),
    PQR_LOV_MIN_EVENT_DURATION(Short.class),
    PQR_LOV_MAX_EVENT_DURATION(Short.class),
    PQR_LOV_START_RANDOM_TIME(Short.class),
    PQR_LOV_END_RANDOM_TIME(Short.class),
    PQR_LOF_TRIGGER(Short.class),
    PQR_LOF_RESTORE(Short.class),
    PQR_LOF_TRIGGER_TIME(Short.class),
    PQR_LOF_RESTORE_TIME(Short.class),
    PQR_LOF_MIN_EVENT_DURATION(Short.class),
    PQR_LOF_MAX_EVENT_DURATION(Short.class),
    PQR_LOF_START_RANDOM_TIME(Short.class),
    PQR_LOF_END_RANDOM_TIME(Short.class),
    PQR_EVENT_SEPARATION(Short.class),
    ;
    
    private Class<?> clazz;
    private LmHardwareCommandParam(Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public Class<?> getClazz() {
        return clazz;
    };

}