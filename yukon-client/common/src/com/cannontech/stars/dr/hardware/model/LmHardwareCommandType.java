package com.cannontech.stars.dr.hardware.model;

public enum LmHardwareCommandType {
    CONFIG,
    IN_SERVICE,
    OUT_OF_SERVICE,
    TEMP_OUT_OF_SERVICE,
    CANCEL_TEMP_OUT_OF_SERVICE,
    READ_NOW,
    PERFORMANCE_VERIFICATION,
    SHED,
    RESTORE,
    //Power Quality Response
    PQR_ENABLE,
    PQR_LOV_PARAMETERS,
    PQR_LOV_EVENT_DURATION,
    PQR_LOV_DELAY_DURATION,
    PQR_LOF_PARAMETERS,
    PQR_LOF_EVENT_DURATION,
    PQR_LOF_DELAY_DURATION,
    PQR_EVENT_SEPARATION,
    ;
}