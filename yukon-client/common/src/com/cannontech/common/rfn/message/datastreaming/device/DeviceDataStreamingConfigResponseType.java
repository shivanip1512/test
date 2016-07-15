package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming response type to a request.
 * The entire request is treated as atomic. Any error will make the whole request rejected.
 * NETWORK_MANAGER_FAILURE indicates NM can't process the request due to exceptions (e.x., database exception).
 */
public enum DeviceDataStreamingConfigResponseType {
    ACCEPTED,
    REJECTED,
    NETWORK_MANAGER_FAILURE,
    OTHER_ERROR, // reserved for future use
}