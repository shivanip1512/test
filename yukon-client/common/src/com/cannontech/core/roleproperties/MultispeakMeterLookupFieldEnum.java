package com.cannontech.core.roleproperties;

public enum MultispeakMeterLookupFieldEnum {

    AUTO_METER_NUMBER_FIRST("Auto (Meter Number First)"),
    AUTO_DEVICE_NAME_FIRST("Auto (Device Name First)"),
    DEVICE_NAME("Device Name"),
    METER_NUMBER("Meter Number"),
    ADDRESS("Address"); 

    private String label;
    
    MultispeakMeterLookupFieldEnum(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

}
