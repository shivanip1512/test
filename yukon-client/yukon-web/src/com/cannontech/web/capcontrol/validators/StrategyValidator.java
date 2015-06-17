package com.cannontech.web.capcontrol.validators;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.capcontrol.dao.StrategyDao;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.db.capcontrol.CapControlStrategy;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSetting;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSetting;
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
        
        YukonValidationUtils.checkRange(errors, "minConfirmPercent", 
            strategy.getMinConfirmPercent(), 0, 100, true);
        YukonValidationUtils.checkRange(errors, "failurePercent", 
            strategy.getMinConfirmPercent(), 0, 100, true);
        
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

    }
    
    private void validateName(CapControlStrategy strategy, Errors errors) {
        
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");

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
    
    private static void validateVoltageViolationSettings(CapControlStrategy strategy, Errors errors) {
        
        Map<VoltViolationType, VoltageViolationSetting> targetSettings = strategy.getVoltageViolationSettings();
        
        VoltageViolationSetting lowSetting = targetSettings.get(VoltViolationType.LOW_VOLTAGE_VIOLATION);
        
        if (lowSetting.getCost() > 0) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].cost",
                         basekey +  ".voltageViolation.low.cost");
        }
        if (lowSetting.getEmergencyCost() > lowSetting.getCost()) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].emergencyCost",
                basekey +  ".voltageViolation.low.emergencyCost");
        }
        
        
        if (lowSetting.getBandwidth() <= 0) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.LOW_VOLTAGE_VIOLATION.name() + "].bandwidth",
                basekey +  ".voltageViolation.bandwidth");
        }
        
        VoltageViolationSetting highSetting = targetSettings.get(VoltViolationType.LOW_VOLTAGE_VIOLATION);
        
        if (highSetting.getCost() < 0) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].cost",
                basekey +  ".voltageViolation.high.cost");
        }
        if (highSetting.getEmergencyCost() < highSetting.getCost()) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].emergencyCost",
                basekey +  ".voltageViolation.high.emergencyCost");
        }
        if (highSetting.getBandwidth() <= 0) {
            errors.reject("voltageViolationSettings[" + VoltViolationType.HIGH_VOLTAGE_VIOLATION.name() + "].bandwidth",
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

}
