package com.cannontech.rest.api.point.request;

public enum MockCalcUpdateType {
    ON_FIRST_CHANGE,
    ON_ALL_CHANGE,
    ON_TIMER,
    ON_TIMER_AND_CHANGE,
    CONSTANT,
    HISTORICAL;
}
