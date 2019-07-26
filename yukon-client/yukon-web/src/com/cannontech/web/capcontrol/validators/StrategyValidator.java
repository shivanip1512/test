package com.cannontech.web.capcontrol.validators;

import java.util.List;
import java.util.Map;

import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.ControlAlgorithm;
import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSetting;
import com.cannontech.database.db.capcontrol.PeakTargetSetting;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSetting;
import com.cannontech.database.db.capcontrol.StrategyPeakSettingsHelper;
import com.cannontech.database.db.capcontrol.TargetSettingType;
import com.cannontech.database.db.capcontrol.VoltViolationType;
import com.cannontech.database.db.capcontrol.VoltageViolationSetting;

@Service
public class StrategyValidator extends SimpleValidator<CapControlStrategy> {

    @Autowired private StrategyDao strategyDao;

    private static final String basekey = "yukon.web.modules.capcontrol.strategy.error";

    public StrategyValidator() {
        super(CapControlStrategy.class);
    }

    @Override
    public void doValidation(CapControlStrategy strategy, Errors errors) {
        
        validateName(strategy, errors);
        validateIntervalSettings(strategy, errors);
        validateTargetSettings(strategy, errors);
        YukonValidationUtils.checkRange(errors, "minConfirmPercent", 
            strategy.getMinConfirmPercent(), 0, 100, true);
        YukonValidationUtils.checkRange(errors, "failurePercent", 
            strategy.getMinConfirmPercent(), 0, 100, true);
        YukonValidationUtils.checkIsPositiveInt(errors,"controlSendRetries", strategy.getControlSendRetries());
        YukonValidationUtils.checkIsPositiveInt(errors,"maxDailyOperation", strategy.getMaxDailyOperation());
        validatePeakTimes(strategy, errors);
        
        if (strategy.isIvvc()) {

            YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.banksReportingRatio", 
                strategy.getMinCommunicationPercentageSetting().getBanksReportingRatio(), 0.0, 100.0, true);
            YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.regulatorReportingRatio", 
                strategy.getMinCommunicationPercentageSetting().getRegulatorReportingRatio(), 0.0, 100.0, true);
            YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.voltageMonitorReportingRatio", 
                strategy.getMinCommunicationPercentageSetting().getVoltageMonitorReportingRatio(), 0.0, 100.0, true);

            validateVoltageViolationSettings(strategy, errors);
            validatePowerFactorCorrectionSetting(strategy, errors);
            validateMinCommunicationPercentageSetting(strategy, errors);
            
        }
        
        if (strategy.isIvvc() || strategy.isMultiVolt() || strategy.isMultiVoltVar()) {
            //check max delta voltage is positive
            if (strategy.getMaxDeltaVoltage() < 0) {
                errors.rejectValue("maxDeltaVoltage", "yukon.web.error.isNotPositive");
            }
        }

    }

    private static void validatePeakTimes(CapControlStrategy strategy, Errors errors) {
        
        LocalTime peakStart = strategy.getPeakStartTime();
        LocalTime peakStop = strategy.getPeakStopTime();
        
        if(peakStart.isAfter(peakStop)){
            errors.rejectValue("peakStopTime", basekey + ".peakTimesViolation.startIsAfterStopTime");
        }
    }

    private void validateName(CapControlStrategy strategy, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
        if (!errors.hasFieldErrors("name")) {
            YukonValidationUtils.checkExceedsMaxLength(errors, "name", strategy.getName(), 32);
        }

        boolean idSpecified = strategy.getId() != null;

        boolean nameAvailable = !strategyDao.isUniqueName(strategy.getName());

        if (!nameAvailable) {
            if (!idSpecified) {
                //For create, we must have an available name
                errors.rejectValue("name", "yukon.web.error.nameConflict");
            } else {
                //For edit, we can use our own existing name
                CapControlStrategy existingStrategy = strategyDao.getForId(strategy.getId());
                if (!existingStrategy.getName().equals(strategy.getName())) {
                    errors.rejectValue("name", "yukon.web.error.nameConflict");
                }
            }
        }
    }
    
    private static void validateIntervalSettings(CapControlStrategy strategy, Errors errors) {
        int integrationSeconds = strategy.getIntegratePeriod();
        int analysisSeconds = strategy.getControlInterval();
        if(strategy.isIntegrateFlag() && integrationSeconds > analysisSeconds) {
            errors.rejectValue("integratePeriod", basekey + ".intervalViolation.integrationInterval");
        }
    }

    private static void validateVoltageViolationSettings(CapControlStrategy strategy, Errors errors) {
        
        Map<VoltViolationType, VoltageViolationSetting> targetSettings = strategy.getVoltageViolationSettings();
        
        VoltageViolationSetting lowSetting = targetSettings.get(VoltViolationType.LOW_VOLTAGE_VIOLATION);
        
        if (lowSetting.getCost() > 0) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].cost",
                         basekey +  ".voltageViolation.low.cost");
        }
        if (lowSetting.getEmergencyCost() > lowSetting.getCost()) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].emergencyCost",
                basekey +  ".voltageViolation.low.emergencyCost");
        }
        
        
        if (lowSetting.getBandwidth() <= 0) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].bandwidth",
                basekey +  ".voltageViolation.bandwidth");
        }
        
        VoltageViolationSetting highSetting = targetSettings.get(VoltViolationType.HIGH_VOLTAGE_VIOLATION);
        
        if (highSetting.getCost() < 0) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].cost",
                basekey +  ".voltageViolation.high.cost");
        }
        if (highSetting.getEmergencyCost() < highSetting.getCost()) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].emergencyCost",
                basekey +  ".voltageViolation.high.emergencyCost");
        }
        if (highSetting.getBandwidth() <= 0) {
            errors.rejectValue("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].bandwidth",
                basekey +  ".voltageViolation.bandwidth");
        }
    }
    
    private static void validatePowerFactorCorrectionSetting(CapControlStrategy strategy, Errors errors) {
        
        PowerFactorCorrectionSetting setting = strategy.getPowerFactorCorrectionSetting();
        
        YukonValidationUtils.checkRange(errors, "powerFactorCorrectionSetting.bandwidth", 
            setting.getBandwidth(), 0.0, 2.0, true);
        
        YukonValidationUtils.checkIsPositiveDouble(errors, "powerFactorCorrectionSetting.cost", setting.getCost());
        YukonValidationUtils.checkIsPositiveDouble(errors, "powerFactorCorrectionSetting.maxCost", setting.getMaxCost());
    }
    
    private static void validateMinCommunicationPercentageSetting(CapControlStrategy strategy, Errors errors) {
        
        CommReportingPercentageSetting setting = strategy.getMinCommunicationPercentageSetting();
        
        YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.banksReportingRatio", 
            setting.getBanksReportingRatio(), 0.0, 100.0, true);
        
        YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.regulatorReportingRatio", 
            setting.getRegulatorReportingRatio(), 0.0, 100.0, true);
        
        YukonValidationUtils.checkRange(errors, "minCommunicationPercentageSetting.voltageMonitorReportingRatio", 
            setting.getVoltageMonitorReportingRatio(), 0.0, 100.0, true);
    }
    
    private static void validateTargetSettings(CapControlStrategy strategy, Errors errors) {
        Map<TargetSettingType, PeakTargetSetting> targetSettings = strategy.getTargetSettings();
        List<TargetSettingType> timeOfDaySettings =
            StrategyPeakSettingsHelper.getSettingsForAlgorithm(ControlAlgorithm.TIME_OF_DAY);
        List<TargetSettingType> pFactorSettings =
            StrategyPeakSettingsHelper.getSettingsForAlgorithm(ControlAlgorithm.PFACTOR_KW_KVAR);
        for (Map.Entry<TargetSettingType, PeakTargetSetting> entry : targetSettings.entrySet()) {
            if (timeOfDaySettings.contains(entry.getKey()) || pFactorSettings.contains(entry.getKey())) {
                PeakTargetSetting peakTargetSetting = entry.getValue();
                if (entry.getKey().equals(TargetSettingType.TARGET_PF)) {
                    YukonValidationUtils.checkRange(errors, "targetSettings[" + entry.getKey() + "].peakValue",
                        peakTargetSetting.getPeakValue(), -99.9, 100.0, true);
                    YukonValidationUtils.checkRange(errors, "targetSettings[" + entry.getKey() + "].offPeakValue",
                        peakTargetSetting.getOffPeakValue(), -99.9, 100.0, true);
                } else {
                    YukonValidationUtils.checkRange(errors, "targetSettings[" + entry.getKey() + "].peakValue",
                        peakTargetSetting.getPeakValue(), 0.0, 100.0, true);
                    YukonValidationUtils.checkRange(errors, "targetSettings[" + entry.getKey() + "].offPeakValue",
                        peakTargetSetting.getOffPeakValue(), 0.0, 100.0, true);
                }
            }
            if (entry.getKey().equals(TargetSettingType.UPPER_VOLT_LIMIT)) {
                double upperVoltPeakValue = targetSettings.get(TargetSettingType.UPPER_VOLT_LIMIT).getPeakValue();
                double lowerVoltPeakValue = targetSettings.get(TargetSettingType.LOWER_VOLT_LIMIT).getPeakValue();
                if (upperVoltPeakValue < lowerVoltPeakValue) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].peakValue",
                        "yukon.web.error.themes.isNotValidVoltLimit");
                }
                double upperOffPeakValue = targetSettings.get(TargetSettingType.UPPER_VOLT_LIMIT).getOffPeakValue();
                double lowerOffPeakValue = targetSettings.get(TargetSettingType.LOWER_VOLT_LIMIT).getOffPeakValue();
                if (upperOffPeakValue < lowerOffPeakValue) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].offPeakValue",
                        "yukon.web.error.themes.isNotValidVoltLimit");
                }
            }
            
            if (entry.getKey().equals(TargetSettingType.KVAR_LEADING)) {
                double kVRLeadingPeakValue = targetSettings.get(TargetSettingType.KVAR_LEADING).getPeakValue();
                double kVRLaggingPeakValue = targetSettings.get(TargetSettingType.KVAR_LAGGING).getPeakValue();
                if (kVRLeadingPeakValue > kVRLaggingPeakValue) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].peakValue",
                        "yukon.web.error.themes.isNotValidKVRValue");
                }
                double kVRLeadingOffPeakValue = targetSettings.get(TargetSettingType.KVAR_LEADING).getOffPeakValue();
                double kVRLaggingOffPeakValue = targetSettings.get(TargetSettingType.KVAR_LAGGING).getOffPeakValue();
                if (kVRLeadingOffPeakValue > kVRLaggingOffPeakValue) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].offPeakValue",
                        "yukon.web.error.themes.isNotValidKVRValue");
                }
            }
            
            if (entry.getKey().equals(TargetSettingType.MAX_CONSECUTIVE_BANK_OPERATIONS)) {
                double bankOperationPeakValue =
                    targetSettings.get(TargetSettingType.MAX_CONSECUTIVE_BANK_OPERATIONS).getPeakValue();
                double bankOperationOffPeakValue =
                    targetSettings.get(TargetSettingType.MAX_CONSECUTIVE_BANK_OPERATIONS).getOffPeakValue();
                if (bankOperationPeakValue < 0 || bankOperationPeakValue % 1 != 0) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].peakValue",
                        "yukon.web.error.isNotPositiveInt");
                }
                if (bankOperationOffPeakValue < 0 || bankOperationOffPeakValue % 1 != 0) {
                    errors.rejectValue("targetSettings[" + entry.getKey() + "].offPeakValue",
                        "yukon.web.error.isNotPositiveInt");
                }
            }
        }
    }
    
}
