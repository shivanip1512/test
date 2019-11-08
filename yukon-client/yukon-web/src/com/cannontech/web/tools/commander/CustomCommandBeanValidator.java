package com.cannontech.web.tools.commander;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class CustomCommandBeanValidator extends SimpleValidator<CustomCommandBean> {

    
    public CustomCommandBeanValidator() {
        super(CustomCommandBean.class);
    }
    
    @Override
    protected void doValidation(CustomCommandBean formBean, Errors errors) {
        int index = 0;

        for (DeviceCommandDetail detail : formBean.getDetail()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "detail[" + index + "].commandName", "yukon.web.error.isBlank");
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "detail[" + index + "].command", "yukon.web.error.isBlank");
            index ++;
        }

    }

}