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
            } else if (config.getLovTrigger() != null) {
                YukonValidationUtils.checkIsPositiveDouble(errors, "lovTrigger", config.getLovTrigger());
            }
            if (config.getLovRestore() == null) {
                errors.rejectValue("lovRestore", keyBase + "valueEmpty");
            } else if (config.getLovRestore() != null) {
                YukonValidationUtils.checkIsPositiveDouble(errors, "lovRestore", config.getLovRestore());
            }
            if (config.getLovTriggerTime() == null && !errors.hasFieldErrors("lovTriggerTime")) {
                errors.rejectValue("lovTriggerTime", keyBase + "valueEmpty");
            } else if (config.getLovTriggerTime() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovTriggerTime", config.getLovTriggerTime());
            }
            if (config.getLovRestoreTime() == null && !errors.hasFieldErrors("lovRestoreTime")) {
                errors.rejectValue("lovRestoreTime", keyBase + "valueEmpty");
            } else if (config.getLovRestoreTime() != null) {
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
            if (config.getLovMinEventDuration() == null && !errors.hasFieldErrors("lovMinEventDuration")) {
                errors.rejectValue("lovMinEventDuration", keyBase + "valueEmpty");
            } else if (config.getLovMinEventDuration() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovMinEventDuration", config.getLovMinEventDuration());
            }
            if (config.getLovMaxEventDuration() == null && !errors.hasFieldErrors("lovMaxEventDuration")) {
                errors.rejectValue("lovMaxEventDuration", keyBase + "valueEmpty");
            } else if (config.getLovMaxEventDuration() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovMaxEventDuration", config.getLovMaxEventDuration());
            }
        }
        
        //Min must be < Max
        if (config.getLovMinEventDuration() != null && config.getLovMaxEventDuration() != null
                && config.getLovMinEventDuration() >= config.getLovMaxEventDuration()) {
            
            errors.rejectValue("lovMinEventDuration", keyBase + "minLtMax");
        }
    }
    
    private void validateLovDelayDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLovStartRandomTime(), config.getLovEndRandomTime())) {
            if (config.getLovStartRandomTime() == null && !errors.hasFieldErrors("lovStartRandomTime")) {
                errors.rejectValue("lovStartRandomTime", keyBase + "valueEmpty");
            } else if (config.getLovStartRandomTime() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lovStartRandomTime", config.getLovStartRandomTime());
            }
            if (config.getLovEndRandomTime() == null && !errors.hasFieldErrors("lovEndRandomTime")) {
                errors.rejectValue("lovEndRandomTime", keyBase + "valueEmpty");
            } else if (config.getLovEndRandomTime() != null) {
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
            if (config.getLofTrigger() == null && !errors.hasFieldErrors("lofTrigger")) {
                errors.rejectValue("lofTrigger", keyBase + "valueEmpty");
            } else if (config.getLofTrigger() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofTrigger", config.getLofTrigger());
            }
            if (config.getLofRestore() == null && !errors.hasFieldErrors("lofRestore")) {
                errors.rejectValue("lofRestore", keyBase + "valueEmpty");
            } else if (config.getLofRestore() != null){
                YukonValidationUtils.checkIsPositiveShort(errors, "lofRestore", config.getLofRestore());
            }
            if (config.getLofTriggerTime() == null && !errors.hasFieldErrors("lofTriggerTime")) {
                errors.rejectValue("lofTriggerTime", keyBase + "valueEmpty");
            } else if (config.getLofTriggerTime() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofTriggerTime", config.getLofTriggerTime());
            }
            if (config.getLofRestoreTime() == null && !errors.hasFieldErrors("lofRestoreTime")) {
                errors.rejectValue("lofRestoreTime", keyBase + "valueEmpty");
            } else if (config.getLofRestoreTime() != null) {
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
            if (config.getLofMinEventDuration() == null && !errors.hasFieldErrors("lofMinEventDuration")) {
                errors.rejectValue("lofMinEventDuration", keyBase + "valueEmpty");
            } else if (config.getLofMinEventDuration() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofMinEventDuration", config.getLofMinEventDuration());
            }
            if (config.getLofMaxEventDuration() == null && !errors.hasFieldErrors("lofMaxEventDuration")) {
                errors.rejectValue("lofMaxEventDuration", keyBase + "valueEmpty");
            } else if (config.getLofMaxEventDuration() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofMaxEventDuration", config.getLofMaxEventDuration());
            }
        }
        
        //Min must be < Max
        if (config.getLofMinEventDuration() != null && config.getLofMaxEventDuration() != null
                && config.getLofMinEventDuration() >= config.getLofMaxEventDuration()) {
            
            errors.rejectValue("lofMinEventDuration", keyBase + "minLtMax");
        }
    }
    
    private void validateLofDelayDuration(PqrConfig config, Errors errors) {
        // All fields must be filled, or none
        // Values must be positive
        if (anyFieldNotNull(config.getLofStartRandomTime(), config.getLofEndRandomTime())) {
            if (config.getLofStartRandomTime() == null && !errors.hasFieldErrors("lofStartRandomTime")) {
                errors.rejectValue("lofStartRandomTime", keyBase + "valueEmpty");
            } else if (config.getLofStartRandomTime() != null) {
                YukonValidationUtils.checkIsPositiveShort(errors, "lofStartRandomTime", config.getLofStartRandomTime());
            }
            if (config.getLofEndRandomTime() == null && !errors.hasFieldErrors("lofEndRandomTime")) {
                errors.rejectValue("lofEndRandomTime", keyBase + "valueEmpty");
            } else if (config.getLofEndRandomTime() != null) {
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
