package com.cannontech.web.deviceConfiguration.validation;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.deviceConfiguration.model.DeviceConfig;

public class DeviceConfigurationValidator extends SimpleValidator<DeviceConfig> {
    private static final String baseKey = "yukon.web.modules.tools.configs.config";
    
    public DeviceConfigurationValidator() {
        super(DeviceConfig.class);
    }
    
    @Override
    protected void doValidation(DeviceConfig target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "configName", baseKey + ".emptyName");
    }
}
