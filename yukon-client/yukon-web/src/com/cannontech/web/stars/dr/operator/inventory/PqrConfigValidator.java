package com.cannontech.web.stars.dr.operator.inventory;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.dr.rfn.model.PqrConfig;

@Service
public class PqrConfigValidator extends SimpleValidator<PqrConfig> {
    private static final String keyBase = "yukon.web.modules.operator.pqrConfig.validation.";
    
    public PqrConfigValidator() {
        super(PqrConfig.class);
    }
    
    @Override
    public void doValidation(PqrConfig config, Errors errors) {
        validateLovParameters(config, errors);
        validateLovEventDuration(config, errors);
        validateLovDelayDuration(config, errors);
        validateLofParameters(config, errors);
        validateLofEventDuration(config, errors);
        validateLofDelayDuration(config, errors);
        validateMinimumEventSeparation(config, errors);
    }
    
    private void validateLovParameters(PqrConfig config, Errors errors) {
        
        Object[] lovParameterFields = {config.getLovTrigger(), config.getLovRestore(), 
                                       config.getLovTriggerTime(), config.getLovRestoreTime()};
        
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(lovParameterFields)) {
            if (config.getLovTrigger() == null) {
                errors.rejectValue("lovTrigger", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveDouble(errors, "lovTrigger", config.getLovTrigger());
            }
            if (config.getLovRestore() == null) {
                errors.rejectValue("lovRestore", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveDouble(errors, "lovRestore", config.getLovRestore());
            }
            if (config.getLovTriggerTime() == null) {
                errors.rejectValue("lovTriggerTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovTriggerTime", config.getLovTriggerTime());
            }
            if (config.getLovRestoreTime() == null) {
                errors.rejectValue("lovRestoreTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovRestoreTime", config.getLovRestoreTime());
            }
        }
        
        // Trigger must be > Restore
        if (config.getLovTrigger() != null && config.getLovRestore() != null 
                && config.getLovTrigger() <= config.getLovRestore()) {
            errors.rejectValue("lovTrigger", keyBase + "triggerGtRestore");
        }
    }
    
    private void validateLovEventDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLovMinEventDuration(), config.getLovMaxEventDuration())) {
            if (config.getLovMinEventDuration() == null) {
                errors.rejectValue("lovMinEventDuration", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovMinEventDuration", config.getLovMinEventDuration());
            }
            if (config.getLovMaxEventDuration() == null) {
                errors.rejectValue("lovMaxEventDuration", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovMaxEventDuration", config.getLovMaxEventDuration());
            }
        }
    }
    
    private void validateLovDelayDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLovStartRandomTime(), config.getLovEndRandomTime())) {
            if (config.getLovStartRandomTime() == null) {
                errors.rejectValue("lovStartRandomTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovStartRandomTime", config.getLovStartRandomTime());
            }
            if (config.getLovEndRandomTime() == null) {
                errors.rejectValue("lovEndRandomTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovEndRandomTime", config.getLovEndRandomTime());
            }
        }
    }
    
    private void validateLofParameters(PqrConfig config, Errors errors) {
        Object[] lofParameterFields = {config.getLofTrigger(), config.getLofRestore(), 
                                       config.getLofTriggerTime(), config.getLofRestoreTime()};
        
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(lofParameterFields)) {
            if (config.getLofTrigger() == null) {
                errors.rejectValue("lofTrigger", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofTrigger", config.getLofTrigger());
            }
            if (config.getLofRestore() == null) {
                errors.rejectValue("lofRestore", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofRestore", config.getLofRestore());
            }
            if (config.getLofTriggerTime() == null) {
                errors.rejectValue("lofTriggerTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofTriggerTime", config.getLofTriggerTime());
            }
            if (config.getLofRestoreTime() == null) {
                errors.rejectValue("lofRestoreTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofRestoreTime", config.getLofRestoreTime());
            }
        }
        
        //Trigger must be < Restore
        if (config.getLofTrigger() != null && config.getLofRestore() != null 
                && config.getLofTrigger() >= config.getLofRestore()) {
            
            errors.rejectValue("lofTrigger", keyBase + "triggerLtRestore");
        }
    }
    
    private void validateLofEventDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLofMinEventDuration(), config.getLofMaxEventDuration())) {
            if (config.getLofMinEventDuration() == null) {
                errors.rejectValue("lofMinEventDuration", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofMinEventDuration", config.getLofMinEventDuration());
            }
            if (config.getLofMaxEventDuration() == null) {
                errors.rejectValue("lofMaxEventDuration", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofMaxEventDuration", config.getLofMaxEventDuration());
            }
        }
    }
    
    private void validateLofDelayDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLofStartRandomTime(), config.getLofEndRandomTime())) {
            if (config.getLofStartRandomTime() == null) {
                errors.rejectValue("lofStartRandomTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofStartRandomTime", config.getLofStartRandomTime());
            }
            if (config.getLofEndRandomTime() == null) {
                errors.rejectValue("lofEndRandomTime", keyBase + "valueEmpty");
            } else {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofEndRandomTime", config.getLofEndRandomTime());
            }
        }
    }
    
    private void validateMinimumEventSeparation(PqrConfig config, Errors errors) {
        // Value must be positive
        if (config.getMinimumEventSeparation() != null) {
            YukonValidationUtils.checkIsPositiveShort(errors, "minimumEventSeparation", config.getMinimumEventSeparation());
        }
    }
    
    private boolean anyFieldNotNull(Object... fields) {
        for (Object field : fields) {
            if (field != null) {
                return true;
            }
        }
        return false;
    }
}
