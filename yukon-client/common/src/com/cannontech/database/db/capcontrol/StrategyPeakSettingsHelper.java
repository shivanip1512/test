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
                
                settings.add(TargetSettingType.KVAR_LEADING.getPeakTargetSetting());
                settings.add(TargetSettingType.KVAR_LAGGING.getPeakTargetSetting());
                break;
                
            case PFACTORKWKVAR :
                settings.add(TargetSettingType.TARGET_PF.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_CLOSE.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_OPEN.getPeakTargetSetting());
                break;
                
            case INTEGRATED_VOLT_VAR :
                settings.add(TargetSettingType.UPPER_VOLT_LIMIT.getPeakTargetSetting());
                settings.add(TargetSettingType.LOWER_VOLT_LIMIT.getPeakTargetSetting());
                settings.add(TargetSettingType.VOLT_WEIGHT.getPeakTargetSetting());
                
                settings.add(TargetSettingType.TARGET_PF.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_CLOSE.getPeakTargetSetting());
                settings.add(TargetSettingType.MIN_BANK_OPEN.getPeakTargetSetting());
                settings.add(TargetSettingType.PF_WEIGHT.getPeakTargetSetting());
                
                settings.add(TargetSettingType.DECISION_WEIGHT.getPeakTargetSetting());
                settings.add(TargetSettingType.VOLTAGE_REGULATION_MARGIN.getPeakTargetSetting());
                break;
                
            case TIME_OF_DAY :
                settings.add(TargetSettingType.HOUR_ZERO.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_ONE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWO.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_THREE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_FOUR.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_FIVE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_SIX.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_SEVEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_EIGHT.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_NINE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_ELEVEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWELVE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_THIRTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_FOURTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_FIFTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_SIXTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_SEVENTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_EIGHTEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_NINETEEN.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWENTY.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWENTYONE.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWENTYTWO.getPeakTargetSetting());
                settings.add(TargetSettingType.HOUR_TWENTYTHREE.getPeakTargetSetting());
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
                
                settingString = lowerVoltLimit.getPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getPeakValue();
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
                settingString = minBankClose.getPeakValue() + "%C | " + targetPF.getPeakValue() + " | " + minBankOpen.getPeakValue() + "%O";
                break;
                
            case TIME_OF_DAY :
                settingString = "---";
                break;
                
            case INTEGRATED_VOLT_VAR :
                targetPF = getPeakTargetSetting(TargetSettingType.TARGET_PF, strategy.getTargetSettings());
                minBankOpen = getPeakTargetSetting(TargetSettingType.MIN_BANK_OPEN, strategy.getTargetSettings());
                minBankClose = getPeakTargetSetting(TargetSettingType.MIN_BANK_CLOSE, strategy.getTargetSettings());
                
                lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getPeakValue() + " : ";
                settingString += minBankClose.getPeakValue() + "%C | " + targetPF.getPeakValue() + " | " + minBankOpen.getPeakValue() + "%O";
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
                
                settingString = lowerVoltLimit.getOffPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getOffPeakValue();
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
                settingString = minBankClose.getOffPeakValue() + "%C | " + targetPF.getOffPeakValue() + " | " + minBankOpen.getOffPeakValue() + "%O";
                break;
                
            case TIME_OF_DAY :
                settingString = "---";
                break;
                
            case INTEGRATED_VOLT_VAR :
                targetPF = getPeakTargetSetting(TargetSettingType.TARGET_PF, strategy.getTargetSettings());
                minBankOpen = getPeakTargetSetting(TargetSettingType.MIN_BANK_OPEN, strategy.getTargetSettings());
                minBankClose = getPeakTargetSetting(TargetSettingType.MIN_BANK_CLOSE, strategy.getTargetSettings());
                
                lowerVoltLimit = getPeakTargetSetting(TargetSettingType.LOWER_VOLT_LIMIT, strategy.getTargetSettings());
                upperVoltLimit = getPeakTargetSetting(TargetSettingType.UPPER_VOLT_LIMIT, strategy.getTargetSettings());
                
                settingString = lowerVoltLimit.getOffPeakValue() + "<" + "Volt" + "<" + upperVoltLimit.getOffPeakValue() + " : ";
                settingString += minBankClose.getOffPeakValue() + "%C | " + targetPF.getOffPeakValue() + " | " + minBankOpen.getOffPeakValue() + "%O";
                break;
                
            default:
                settingString = "Unknown Algorithm";
                break;
        }
        
        return settingString;
    }
    
    private static PeakTargetSetting getPeakTargetSetting(TargetSettingType settingType, List<PeakTargetSetting> settings) {
        for(PeakTargetSetting setting : settings) {
            if(setting.getName().equalsIgnoreCase(settingType.getDisplayName())){
                return setting;
            }
        }
        return null;
    }
    
}