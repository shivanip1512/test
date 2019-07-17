package com.cannontech.web.support.waterNode.batteryLevel;

import com.cannontech.common.i18n.DisplayableEnum;

public enum WaterNodeBatteryLevel implements DisplayableEnum {
    NORMAL(3.5),
    LOW(3.45),
    CRITICALLY_LOW(3.3),
    UNREPORTED(-1.0);
    
    private final double upperThreshold;
    private final static String baseKey = "yukon.web.modules.support.waterNode.";
    
    private WaterNodeBatteryLevel(double upperThreshold) {
        this.upperThreshold = upperThreshold;
    }
    
    public Double getThreshold(){
        return upperThreshold;
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
