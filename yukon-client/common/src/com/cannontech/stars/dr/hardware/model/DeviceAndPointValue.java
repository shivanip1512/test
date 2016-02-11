package com.cannontech.stars.dr.hardware.model;

import com.cannontech.core.dynamic.impl.SimplePointValue;
import com.cannontech.stars.model.LiteLmHardware;

public class DeviceAndPointValue {
    
    private LiteLmHardware lmHardware;

    private SimplePointValue pointValue;
    
    private DeviceAndPointValue(LiteLmHardware lmHardware, SimplePointValue pointValue) {
        this.lmHardware = lmHardware;
        this.pointValue = pointValue;
    }
    
    public LiteLmHardware getLmHardware() {
        return lmHardware;
    }
    
    public SimplePointValue getPointValue() {
        return pointValue;
    }
    
    public static DeviceAndPointValue of(LiteLmHardware lmHardware, SimplePointValue pointValue) {
        return new DeviceAndPointValue(lmHardware, pointValue);
    }
    
    @Override
    public String toString() {
        return "DeviceAndPointValue [lmHardware=" + lmHardware + ", pointValue=" + pointValue + "]";
    }
    
}