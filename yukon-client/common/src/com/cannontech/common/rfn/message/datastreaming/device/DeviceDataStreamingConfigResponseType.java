package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming response type to a request.
 * The entire request is treated as atomic. Any error will make the whole request rejected.
 * NETWORK_MANAGER_SERVER_FAILURE indicates NM can't process the request due to exceptions other than database exceptions.
 * NETWORK_MANAGER_DATABASE_FAILURE indicates NM can't process the request due to exceptions database exceptions.
 */
public enum DeviceDataStreamingConfigResponseType {
    ACCEPTED,
    CONFIG_ERROR,
    
    INVALID_REQUEST_TYPE, // i.e., the requestType field is null.
    INVALID_REQUEST_ID, // i.e., the requestId field is null or more than 255 characters.   
    NO_CONFIGS, // i.e., the configs field is null or empty.
    NO_DEVICES, // i.e., the devices field is null or empty.
    
    NETWORK_MANAGER_SERVER_FAILURE,
    NETWORK_MANAGER_DATABASE_FAILURE,
    
    OTHER_ERROR, // reserved for future use
}