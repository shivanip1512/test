package com.cannontech.core.roleproperties;

public enum MultispeakMeterLookupFieldEnum {

    AUTO_METER_NUMBER_FIRST("Auto (Meter Number First)", "autoMeterNumber"),
    AUTO_DEVICE_NAME_FIRST("Auto (Device Name First)", "autoDeviceName"),
    DEVICE_NAME("Device Name", "deviceName"),
    METER_NUMBER("Meter Number", "meterNumber"),
    ADDRESS("Address", "address"); 

    private String label;
    private String id;
    
    MultispeakMeterLookupFieldEnum(String label, String id) {
        this.label = label;
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }

}
