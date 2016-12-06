package com.cannontech.common.rfn.dataStreaming;

/**
 * This comes from the DS streaming spec, 10030-06_Hub_Meter_Application_ICD, section 2.2.50.
 * It must match the C++ metrics list in \yukon-server\RTDB\cmd_rfn_DataStreamingConfiguration.cpp
 */
public enum DataStreamingMetricStatus {
    OK(0),
    METER_ACCESS_ERROR(1),
    METER_OR_NODE_BUSY(2),
    METER_READ_TIMEOUT(3),
    METER_PROTOCOL_ERROR(4),
    CHANNEL_NOT_SUPPORTED(5),
    UNKNOWN_ERROR(6),
    CHANNEL_NOT_ENABLED(7);
    
    DataStreamingMetricStatus(int status) {
        this.status = (short)status;
    }
    
    public short asShort() {
        return status;
    }
    
    private short status;
}
