package com.cannontech.common.rfn.message.gateway;

/**
 * The result of a request to update rfn firmware. ACCEPTED indicates that the upgrade was successful. All other results
 * indicate that an error occurred.
 */
public enum GatewayFirmwareUpdateRequestResult {
    ACCEPTED,
    REJECTED_UPDATE_IN_PROGRESS,
    REJECTED_BATTERY_ISSUE,
    VERSION_MISMATCH,
    GENERAL_ERROR,
    CONFIGURATION_PARSE_ERROR,
    FILE_IO_ERROR,
    NETWORK_FAILURE,
    SSL_VERIFICATION_FAILURE,
    AUTHENTICATION_FAILURE,
    PROTOCOL_ERROR,
    SERVER_ERROR;
}