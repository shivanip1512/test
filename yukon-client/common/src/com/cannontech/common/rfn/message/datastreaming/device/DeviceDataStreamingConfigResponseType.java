package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming response type to a request.
 * The entire request is treated as atomic. Any error will make the whole request rejected.
 * NETWORK_MANAGER_SERVER_FAILURE indicates NM can't process the request due to exceptions other than database exceptions.
 * NETWORK_MANAGER_DATABASE_FAILURE indicates NM can't process the request due to exceptions database exceptions.
 */
public enum DeviceDataStreamingConfigResponseType {
    ACCEPTED,
    ACCEPTED_WITH_ERROR,
    CONFIG_ERROR,
    
    INVALID_REQUEST_TYPE, // e.x, the requestType field is null, OTHER, or unexpected enum value.
    INVALID_REQUEST_SEQUENCE_NUMBER, // used for UPDATE or UPDATE_WITH_FORCE: e.x.,
                                     // the current sequence number is less than the last sequence number received.
    NO_DEVICES, // i.e., the devices field is null or empty.
    NO_CONFIGS, // i.e., the configs field is null or empty.
    INVALID_CONFIG, // found invalid-configured (wrong input parameters) device.
    
    NETWORK_MANAGER_SERVER_FAILURE,
    NETWORK_MANAGER_DATABASE_FAILURE,
    OTHER_ERROR, // reserved for future use
}