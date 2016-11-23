package com.cannontech.dr.honeywellWifi.azure.event.processing;

import com.cannontech.dr.honeywellWifi.HoneywellWifiDataType;
import com.cannontech.dr.honeywellWifi.azure.event.HoneywellWifiData;

/**
 * Interface for strategies that can process a specific type of Honeywell Azure service bus messages.
 */
public interface HoneywellWifiDataProcessor {
    
    /**
     * @return The HoneywellWifiDataType of messages that can be processed by this strategy.
     */
    HoneywellWifiDataType getSupportedType();
    
    /**
     * Process a HoneywellWifiData message of the supported type.
     */
    void processData(HoneywellWifiData data);
}
