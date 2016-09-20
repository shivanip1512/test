package com.cannontech.common.rfn.dataStreaming;

enum DataStreamingMetricStatus {
    INVALID_STATUS,
    OK,
    METER_ACCESS_ERROR,
    METER_OR_NODE_BUSY,
    METER_READ_TIMEOUT,
    METER_PROTOCOL_ERROR,
    CHANNEL_NOT_SUPPORTED,
    UNKNOWN_ERROR,
    CHANNEL_NOT_ENABLED
}
