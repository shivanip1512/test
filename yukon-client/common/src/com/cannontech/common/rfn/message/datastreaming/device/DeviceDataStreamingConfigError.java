package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming configuration error for a device.
 */
public enum DeviceDataStreamingConfigError {
    GATEWAY_OVERLOADED,

    NO_SENSOR_IDENTIFIER, // i.e., RfnIdentifier is null
    NO_NODE_FOR_SENSOR,
    NO_GATEWAY_FOR_NODE,
    NODE_NOT_READY,

    NO_DATA_STREAMING_METRIC, // the device doesn’t support any data streaming metric yet.

    INVALID_CONFIG_INDEX, // i.e., null value or out of bound indexes

    UNSUPPORTED_METRIC_ID, // for ASSESS or UPDATE only; null value is treated as unsupported. 
    UNSUPPORTED_REPORT_INTERVAL,
    
    TOO_MANY_REPORT_INTERVALS,
    TOO_MANY_ENABLED_METRICS,

    NULL_METRIC_ID,
    NULL_METRIC_CONFIG,
    
    // the following are for ASSESS/UPDATE request type.
    // ASSESS/UPDATE should have at least one non-null value.
    ALL_NULL_VALUES,
    
    // REQUESTID_ALREADY_USED
    
    // the following are for CONFIRM request type only.
    // CONFIRM should not allow any null value.
    // It should provide a full set of data streaming values for the device.
    NULL_DATA_STREAMING_ON,
    NULL_ENABLED,
    NULL_INTERVAL,
    NULL_SEQUENCE_NUMBER,
    
    // the following is reserved for future use
    OTHER_ERROR,
}