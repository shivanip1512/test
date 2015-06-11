package com.cannontech.database.db.capcontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class StrategyPeakSettingsHelper {
    
    private static final Map<ControlAlgorithm, List<TargetSettingType>> usedSettingsByAlgorithm;
    
    static {
        ImmutableMap.Builder<ControlAlgorithm, List<TargetSettingType>> usedSettings = ImmutableMap.builder();
        
        usedSettings.put(ControlAlgorithm.KVAR, ImmutableList.of(
            TargetSettingType.KVAR_LEADING,
            TargetSettingType.KVAR_LAGGING));
        
        usedSettings.put(ControlAlgorithm.VOLTS, ImmutableList.of(
            TargetSettingType.UPPER_VOLT_LIMIT,
            TargetSettingType.LOWER_VOLT_LIMIT));

        usedSettings.put(ControlAlgorithm.MULTI_VOLT, ImmutableList.of(
            TargetSettingType.UPPER_VOLT_LIMIT,
            TargetSettingType.LOWER_VOLT_LIMIT));
        
        usedSettings.put(ControlAlgorithm.MULTI_VOLT_VAR, ImmutableList.of(
            TargetSettingType.UPPER_VOLT_LIMIT,
            TargetSettingType.LOWER_VOLT_LIMIT,
            TargetSettingType.KVAR_LEADING,
            TargetSettingType.KVAR_LAGGING));
        
        usedSettings.put(ControlAlgorithm.PFACTOR_KW_KVAR, ImmutableList.of(
            TargetSettingType.TARGET_PF,
            TargetSettingType.MIN_BANK_CLOSE,
            TargetSettingType.MIN_BANK_OPEN));
        
        usedSettings.put(ControlAlgorithm.INTEGRATED_VOLT_VAR, ImmutableList.of(
            TargetSettingType.UPPER_VOLT_LIMIT,
            TargetSettingType.LOWER_VOLT_LIMIT,
            TargetSettingType.VOLT_WEIGHT,
            TargetSettingType.TARGET_PF,
            TargetSettingType.MIN_BANK_CLOSE,
            TargetSettingType.MIN_BANK_OPEN,
            TargetSettingType.PF_WEIGHT,
            TargetSettingType.DECISION_WEIGHT,
            TargetSettingType.VOLTAGE_REGULATION_MARGIN,
            TargetSettingType.MAX_CONSECUTIVE_BANK_OPERATIONS));
        
        usedSettings.put(ControlAlgorithm.TIME_OF_DAY, ImmutableList.of(
            TargetSettingType.HOUR_ZERO,
            TargetSettingType.HOUR_ONE,
            TargetSettingType.HOUR_TWO,
            TargetSettingType.HOUR_THREE,
            TargetSettingType.HOUR_FOUR,
            TargetSettingType.HOUR_FIVE,
            TargetSettingType.HOUR_SIX,
            TargetSettingType.HOUR_SEVEN,
            TargetSettingType.HOUR_EIGHT,
            TargetSettingType.HOUR_NINE,
            TargetSettingType.HOUR_TEN,
            TargetSettingType.HOUR_ELEVEN,
            TargetSettingType.HOUR_TWELVE,
            TargetSettingType.HOUR_THIRTEEN,
            TargetSettingType.HOUR_FOURTEEN,
            TargetSettingType.HOUR_FIFTEEN,
            TargetSettingType.HOUR_SIXTEEN,
            TargetSettingType.HOUR_SEVENTEEN,
            TargetSettingType.HOUR_EIGHTEEN,
            TargetSettingType.HOUR_NINETEEN,
            TargetSettingType.HOUR_TWENTY,
            TargetSettingType.HOUR_TWENTYONE,
            TargetSettingType.HOUR_TWENTYTWO,
            TargetSettingType.HOUR_TWENTYTHREE));
        
        usedSettingsByAlgorithm = usedSettings.build();
    }
    
    public static Map<ControlAlgorithm, List<TargetSettingType>> getAlgorithmToSettings() {
        return usedSettingsByAlgorithm;
    }
    public static List<TargetSettingType> getSettingsForAlgorithm(ControlAlgorithm algorithm) {
        return usedSettingsByAlgorithm.get(algorithm);
    }
    
    
    public static Map<TargetSettingType, PeakTargetSetting> getSettingDefaults(ControlAlgorithm algorithm) {
        
        Map<TargetSettingType, PeakTargetSetting> defaults = new HashMap<>();
        
        for (TargetSettingType settingType : getSettingsForAlgorithm(algorithm)) {
            defaults.put(settingType, settingType.getDefaultSetting());
        }
        
        return defaults;
    }
    
    public static Map<TargetSettingType, PeakTargetSetting> getAllSettingDefaults() {
        
        Map<TargetSettingType, PeakTargetSetting> defaults = new HashMap<>();
        
        for (TargetSettingType settingType : TargetSettingType.values()) {
            defaults.put(settingType, settingType.getDefaultSetting());
        }
        
        return defaults;
    }
    
    public static List<TargetSettingType> getDisplayArguments() {
        
        return ImmutableList.of(
            TargetSettingType.KVAR_LEADING,
            TargetSettingType.KVAR_LAGGING,
            TargetSettingType.LOWER_VOLT_LIMIT,
            TargetSettingType.UPPER_VOLT_LIMIT,
            TargetSettingType.MIN_BANK_CLOSE,
            TargetSettingType.MIN_BANK_OPEN,
            TargetSettingType.TARGET_PF);
    }

}