package com.cannontech.core.roleproperties;

import com.cannontech.common.i18n.DisplayableEnum;

public enum MultispeakMeterLookupFieldEnum implements DisplayableEnum{

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
    
    @Override
    public String getFormatKey() {
        return "yukon.common.multispeakMeterLookupField." + name();
    }

}
