package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * Describes data streaming configuration error for a device.
 */
public enum DeviceDataStreamingConfigError {
    GATEWAY_OVERLOADED,
    
    INVALID_RFN_IDENTIFIER,
    NO_ASSOCIATED_NODE,
    NO_GATEWAY,
    
    UNSUPPORTED_DEVICE,
    UNSUPPORTED_METRIC_ID,
    UNSUPPORTED_REPORT_INTERVAL,
    
    TOO_MUCH_REPORT_INTERVALS,
    TOO_MUCH_CONFIGED_METRICS,
    
    SEQUENCE_NUMBER_MISSED, // only for SYNC request type
    
    OTHER_ERROR,  // reserved for future use
}