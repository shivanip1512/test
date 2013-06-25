package com.cannontech.web.deviceConfiguration.validation;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.deviceConfiguration.model.ConfigurationDeviceTypesBackingBean;

public class ConfigurationDeviceTypesValidator extends SimpleValidator<ConfigurationDeviceTypesBackingBean> {
    public ConfigurationDeviceTypesValidator() {
        super(ConfigurationDeviceTypesBackingBean.class);
    }
    
    @Override
    public void doValidation(ConfigurationDeviceTypesBackingBean target, Errors errors) {
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
