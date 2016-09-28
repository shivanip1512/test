package com.cannontech.dr.honeywellWifi;

public class HoneywellDemandResponseEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {

    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.DEMAND_RESPONSE_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        //TODO
    }

}
