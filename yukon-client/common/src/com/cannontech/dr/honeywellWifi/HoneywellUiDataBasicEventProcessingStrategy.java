package com.cannontech.dr.honeywellWifi;

public class HoneywellUiDataBasicEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {

    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.UI_DATA_BASIC_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        //TODO
    }

}
