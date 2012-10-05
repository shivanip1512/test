package com.cannontech.database.db.capcontrol;

import java.util.List;

import com.google.common.collect.Lists;

public class VoltageViolationSettingsHelper {
    public static List<VoltageViolationSetting> getVoltageViolationDefaults() {
        List<VoltageViolationSetting> settings = Lists.newArrayListWithCapacity(2);
        settings.add(new VoltageViolationSetting(VoltageViolationSettingNameType.LOW_VOLTAGE_VIOLATION,
                                                 VoltageViolationSettingType.BANDWIDTH.getDefaultLowValue(),
                                                 VoltageViolationSettingType.COST.getDefaultLowValue(),
                                                 VoltageViolationSettingType.EMERGENCY_COST.getDefaultLowValue()));
        settings.add(new VoltageViolationSetting(VoltageViolationSettingNameType.HIGH_VOLTAGE_VIOLATION,
                                                 VoltageViolationSettingType.BANDWIDTH.getDefaultHighValue(),
                                                 VoltageViolationSettingType.COST.getDefaultHighValue(),
                                                 VoltageViolationSettingType.EMERGENCY_COST.getDefaultHighValue()));
        return settings;
    }    
}