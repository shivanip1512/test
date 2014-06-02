package com.cannontech.web.deviceConfiguration.validation;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigTypes;

public class ConfigurationDeviceTypesValidator extends SimpleValidator<DeviceConfigTypes> {
    public ConfigurationDeviceTypesValidator() {
        super(DeviceConfigTypes.class);
    }
    
    @Override
    public void doValidation(DeviceConfigTypes target, Errors errors) {
        boolean noneSelected = true;
        for (Boolean value : target.getSupportedTypes().values()) {
            if (value != null && value == true) {
                noneSelected = false;
                break;
            }
        }
        
        if (noneSelected) {
            errors.rejectValue("supportedTypes", "yukon.web.modules.tools.configs.noSelectedTypes");
        }
    };
}
