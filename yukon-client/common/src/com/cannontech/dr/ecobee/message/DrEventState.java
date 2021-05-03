package com.cannontech.dr.ecobee.message;

public enum DrEventState {
    REQUESTED,
    SUBMITTED_DIRECTLY,
    SUBMITTED_FOR_OPTIMIZATION,
    ACTIVE,
    CANCELLED,
    ERROR,
    COMPLETED;
}