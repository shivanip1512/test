package com.cannontech.amr.rfn.message.disconnect;

public enum RfnMeterDisconnectConfirmationReplyType {
    SUCCESS,
    //  Catch-all error
    FAILURE,
    //  Protocol errors
    FAILURE_REQUEST_REJECTED_REASON_UNKNOWN,
    FAILURE_SERVICE_NOT_SUPPORTED,
    FAILURE_INSUFFICIENT_SECURITY_CLEARANCE,
    FAILURE_OPERATION_NOT_POSSIBLE,
    FAILURE_INAPPROPRIATE_ACTION_REQUESTED,
    FAILURE_DEVICE_BUSY,
    FAILURE_DATA_NOT_READY,
    FAILURE_DATA_LOCKED,
    FAILURE_RENEGOTIATE_REQUEST,
    FAILURE_INVALID_STATE,
    //  Meter errors
    FAILURE_REJECTED_COMMAND_LOAD_SIDE_VOLTAGE_HIGHER_THAN_THRESHOLD,
    FAILURE_ARM_REJECTED_SWITCH_NOT_OPEN,
    FAILURE_METER_IN_TEST_MODE,
    FAILURE_CLOSE_PRESSED_BUT_METER_NOT_ARMED,
    FAILURE_METER_NOT_CAPABLE_OF_SERVICE_DISCONNECT,
    FAILURE_SERVICE_DISCONNECT_NOT_ENABLED,
    FAILURE_SERVICE_DISCONNECT_IS_CHARGING,
    FAILURE_SERVICE_DISCONNECT_ALREADY_OPERATING,
    FAILURE_CAPACITOR_DISCHARGE_NOT_DETECTED,
    FAILURE_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_DISCONNECT,
    FAILURE_NO_LOAD_SIDE_VOLTAGE_DETECTED_AFTER_CONNECT,
    //  Other errors
    FAILED_UNEXPECTED_STATUS,
    NOT_SUPPORTED,
    NETWORK_TIMEOUT,
    TIMEOUT, // Yukon specific timeout
}