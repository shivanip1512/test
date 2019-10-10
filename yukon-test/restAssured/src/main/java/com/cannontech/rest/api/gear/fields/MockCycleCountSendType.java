package com.cannontech.rest.api.gear.fields;

/**
 * This enum represent database store values and a part of request.
 */
public enum MockCycleCountSendType {

    FixedCount, 
    CountDown,
    LimitedCountDown, 
    FixedShedTime, 
    DynamicShedTime;

}
