package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming configuration error for a device.
 */
public enum DeviceDataStreamingConfigError {
    GATEWAY_OVERLOADED,

    NO_SENSOR_IDENTIFIER, // i.e., RfnIdentifier is null
    NO_NODE_FOR_SENSOR,
    NO_GATEWAY_FOR_NODE,

    NO_DATA_STREAMING_METRIC, // the device doesn’t support any data streaming metric yet.

    INVALID_CONFIG_INDEX, // i.e., null value or out of bound indexes    
    
    UNSUPPORTED_METRIC_ID, // for ASSESS or UPDATE only; null value is treated as unsupported. 
    UNSUPPORTED_REPORT_INTERVAL,
    
    TOO_MANY_REPORT_INTERVALS,
    TOO_MANY_ENABLED_METRICS,
    
    // the following are for CONFIRM request type only.
    // CONFIRM should not allow any null value.
    // It should provide a full set of data streaming values for the device.
    NULL_DATA_STREAMING_ON,
    NULL_METRIC_ID,
    NULL_ENABLED,
    NULL_INTERVAL,
    NULL_SEQUENCE_NUMBER,
    
    // the following is reserved for future use
    OTHER_ERROR,
}