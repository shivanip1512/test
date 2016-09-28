package com.cannontech.dr.honeywellWifi;

public class HoneywellEquipmentStatusEventProcessingStrategy implements HoneywellWifiDataProcessingStrategy {

    @Override
    public HoneywellWifiDataType getSupportedType() {
        return HoneywellWifiDataType.EQUIPMENT_STATUS_EVENT;
    }

    @Override
    public void processData(HoneywellWifiData data) {
        //TODO
    }

}
