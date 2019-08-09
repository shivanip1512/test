package com.cannontech.web.support.waterNode.batteryLevel;

import com.cannontech.common.i18n.DisplayableEnum;

public enum WaterNodeBatteryLevel implements DisplayableEnum {
    NORMAL(3.5, "Normal"),//The value 3.50 is a placeholder; it is not used in analysis currently. Eventually, data persistence will be used in node analysis
    //and nodes must clear this threshold to be placed back into the NORMAL category if they have dipped into the LOW category at any point.
    LOW(3.45, "Low"),
    CRITICALLY_LOW(3.3, "Critically Low"),
    UNREPORTED(-1.0, "Unreported");
    
    private final double upperThreshold;
    private final String outputName;//lowercase name for CSV output
    private final static String baseKey = "yukon.web.modules.support.waterNode.";
    
    private WaterNodeBatteryLevel(double upperThreshold, String outputName) {
        this.upperThreshold = upperThreshold;
        this.outputName = outputName;
    }
    
    public Double getThreshold(){
        return upperThreshold;
    }
    
    public String getOutputName() {
        return outputName;
    }
    
    @Override
    public String getFormatKey() {
        return baseKey + name();
    }

}
