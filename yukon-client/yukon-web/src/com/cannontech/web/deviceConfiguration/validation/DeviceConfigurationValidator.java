package com.cannontech.web.deviceConfiguration.validation;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.deviceConfiguration.model.DeviceConfigurationBackingBean;

public class DeviceConfigurationValidator extends SimpleValidator<DeviceConfigurationBackingBean> {
    private static final String baseKey = "yukon.web.modules.tools.configs.config";
    
    public DeviceConfigurationValidator() {
        super(DeviceConfigurationBackingBean.class);
    }
    
    @Override
    protected void doValidation(DeviceConfigurationBackingBean target, Errors errors) {
        YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "configName", baseKey + ".emptyName");
    }
}
