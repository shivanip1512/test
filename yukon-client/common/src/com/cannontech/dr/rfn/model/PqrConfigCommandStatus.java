package com.cannontech.dr.rfn.model;

/**
 * Describes the possible states of a PQR configuration command for each device.
 */
public enum PqrConfigCommandStatus {
    IN_PROGRESS, // The configuration operation started, but messaging for the device is not yet complete.
    UNSUPPORTED, // The device does not support PQR.
    SUCCESS, // All configuration messages for the device were sent to Network Manager.
    FAILED, // A problem prevented the configuration messages from being sent to Network Manager.
    ;
}
