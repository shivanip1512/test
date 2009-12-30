package com.cannontech.database.db.capcontrol;

import java.util.List;

import com.cannontech.database.db.point.calculation.ControlAlgorithm;
import com.google.common.collect.Lists;

public class StrategyPeakSettingsHelper {

    public static List<PeakTargetSetting> getSettingDefaults(ControlAlgorithm algorithm) {
        List<PeakTargetSetting> settings = Lists.newArrayList();
        
        switch(algorithm) {
        
            case KVAR :
                settings.add(TargetSettingType.KVAR_LEADING.getPeakTargetSetting());
                settings.add(TargetSettingType.KVAR_LAGGING.getPeakTargetSetting());
                break;
                
            case VOLTS :
            case MULTIVOLT :
                settings.add(TargetSettingType.UPPER_VOLT_LIMIT.getPeakTargetSetting());
                settings.add(TargetSettingType.LOWER_VOLT_LIMIT.getPeakTargetSetting());
                break;
            
            case MULTIVOLTVAR:
                settings.add(TargetSettingType.UPPER_VOLT_LIMIT.getPeakTargetSetting());
                settings.add(TargetSettingType.LOWER_VOLT_LIMIT.getPeakTargetSetting());
                
                settings.add(TargetSettingType.KVAR_LAGGING.getPeakTargetSetting());
                settings.add(TargetSettingType.KVAR_LEADING.getPeakTargetSetting());
                break;
                
            case PFACTORKWKVAR :
                settings.add(TargetSettingType.TARGET_PF.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_OPEN.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_CLOSE.getPeakTargetSetting());
                break;
                
            case INTEGRATED_VOLT_VAR :
                settings.add(TargetSettingType.UPPER_VOLT_LIMIT.getPeakTargetSetting());
                settings.add(TargetSettingType.LOWER_VOLT_LIMIT.getPeakTargetSetting());
                
                settings.add(TargetSettingType.TARGET_PF.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_OPEN.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_CLOSE.getPeakTargetSetting());
                
                settings.add(TargetSettingType.KVOLT_WEIGHT.getPeakTargetSetting());
                settings.add(TargetSettingType.PF_WEIGHT.getPeakTargetSetting());
                settings.add(TargetSettingType.DECISION_WEIGHT.getPeakTargetSetting());
                break;
        }
        
        return settings;
    }

    public static String getPeakSettingsString(CapControlStrategy strategy) {
        ControlAlgorithm algorithm = ControlAlgorithm.getControlAlgorithm(strategy.getControlUnits());
        String settingString = null;
        switch(algorithm) {
        
            case KVAR:
                PeakTargetSetting kvarLag = getPeakTargetSetting(TargetSettingType.KVAR_LAGGING, strategy.getTargetSettings());
                PeakTargetSetting kvarLead = getPeakTargetSetting(TargetSettingType.KVAR_LEADING, strategy.getTargetSettings());
                
                settingString = kvarLag.getPeakValue() + "<" + "kVar" + "<" + kvarLead.getPeakValue();
                break;
            case VOLTS:
            case MULTIVOLT:
                PeakTargetSetting lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                PeakTargetSetting upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getPeakValue() + "<" + "V" + "<" + upperVoltLimit.getPeakValue();
                break;
            case MULTIVOLTVAR:
                kvarLag = getPeakTargetSetting(TargetSettingType.KVAR_LAGGING, strategy.getTargetSettings());
                kvarLead = getPeakTargetSetting(TargetSettingType.KVAR_LEADING, strategy.getTargetSettings());
                lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getPeakValue();
                settingString += " : " + kvarLag.getPeakValue() + "<" + "kVar" + "<" + kvarLead.getPeakValue();
                break;
            case PFACTORKWKVAR:
                PeakTargetSetting targetPF = getPeakTargetSetting(TargetSettingType.TARGET_PF, strategy.getTargetSettings());
                PeakTargetSetting minBankOpen = getPeakTargetSetting(TargetSettingType.MIN_BANK_OPEN, strategy.getTargetSettings());
                PeakTargetSetting minBankClose = getPeakTargetSetting(TargetSettingType.MIN_BANK_CLOSE, strategy.getTargetSettings());
                settingString =  minBankClose.getPeakValue() + "<" + targetPF.getPeakValue() + "<" + minBankOpen.getPeakValue();
                break;              
            default:
                settingString = "Unknown Algorithm";
                break;
        }
        
        return settingString;
    }
    
    public static String getOffPeakSettingsString(CapControlStrategy strategy) {
        ControlAlgorithm algorithm = ControlAlgorithm.getControlAlgorithm(strategy.getControlUnits());
        String settingString = null;
        switch(algorithm) {
        
            case KVAR:
                PeakTargetSetting kvarLag = getPeakTargetSetting(TargetSettingType.KVAR_LAGGING, strategy.getTargetSettings());
                PeakTargetSetting kvarLead = getPeakTargetSetting(TargetSettingType.KVAR_LEADING, strategy.getTargetSettings());
                
                settingString = kvarLag.getOffPeakValue() + "<" + "kVar" + "<" + kvarLead.getOffPeakValue();
                break;
            case VOLTS:
            case MULTIVOLT:
                PeakTargetSetting lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                PeakTargetSetting upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getOffPeakValue() + "<" + "V" + "<" + upperVoltLimit.getOffPeakValue();
                break;
            case MULTIVOLTVAR:
                kvarLag = getPeakTargetSetting(TargetSettingType.KVAR_LAGGING, strategy.getTargetSettings());
                kvarLead = getPeakTargetSetting(TargetSettingType.KVAR_LEADING, strategy.getTargetSettings());
                lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getOffPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getOffPeakValue();
                settingString += " : " + kvarLag.getOffPeakValue() + "<" + "kVar" + "<" + kvarLead.getOffPeakValue();
                break;
            case PFACTORKWKVAR:
                PeakTargetSetting targetPF = getPeakTargetSetting(TargetSettingType.TARGET_PF, strategy.getTargetSettings());
                PeakTargetSetting minBankOpen = getPeakTargetSetting(TargetSettingType.MIN_BANK_OPEN, strategy.getTargetSettings());
                PeakTargetSetting minBankClose = getPeakTargetSetting(TargetSettingType.MIN_BANK_CLOSE, strategy.getTargetSettings());
                settingString =  minBankClose.getOffPeakValue() + "<" + targetPF.getOffPeakValue() + "<" + minBankOpen.getOffPeakValue();
                break;              
            default:
                settingString = "Unknown Algorithm";
                break;
        }
        
        return settingString;
    }
    
    private static PeakTargetSetting getPeakTargetSetting(TargetSettingType settingType, List<PeakTargetSetting> settings) {
        for(PeakTargetSetting setting : settings) {
            if(setting.getName().equalsIgnoreCase(settingType.getName())){
                return setting;
            }
        }
        return null;
    }
    
}