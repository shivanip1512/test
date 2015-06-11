package com.cannontech.database.db.capcontrol;

import java.util.HashMap;
import java.util.Map;

public class VoltageViolationSettingsHelper {
    public static Map<VoltViolationType, VoltageViolationSetting> getVoltageViolationDefaults() {
        
        Map<VoltViolationType, VoltageViolationSetting> settings = new HashMap<>();
        
        settings.put(VoltViolationType.LOW_VOLTAGE_VIOLATION,
            new VoltageViolationSetting(
                VoltageViolationSettingType.BANDWIDTH.getDefaultLowValue(),
                VoltageViolationSettingType.COST.getDefaultLowValue(),
                VoltageViolationSettingType.EMERGENCY_COST.getDefaultLowValue()));
        settings.put(VoltViolationType.HIGH_VOLTAGE_VIOLATION,
            new VoltageViolationSetting(
                VoltageViolationSettingType.BANDWIDTH.getDefaultHighValue(),
                VoltageViolationSettingType.COST.getDefaultHighValue(),
                VoltageViolationSettingType.EMERGENCY_COST.getDefaultHighValue()));
        return settings;
    }    
}