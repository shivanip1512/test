package com.cannontech.dr.honeywellWifi;

/**
 * Interface for strategies that can process a specific type of Honeywell Azure service bus messages.
 */
public interface HoneywellWifiDataProcessingStrategy {
    
    /**
     * @return The HoneywellWifiDataType of messages that can be processed by this strategy.
     */
    HoneywellWifiDataType getSupportedType();
    
    /**
     * Process a HoneywellWifiData message of the supported type.
     */
    void processData(HoneywellWifiData data);
}
