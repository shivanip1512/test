package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.hardware.model.SerialNumber;

public class SerialNumberValidator extends SimpleValidator<SerialNumber> {
    
    public SerialNumberValidator() {
        super(SerialNumber.class);
    }

    @Override
    public void doValidation(SerialNumber serialNumber, Errors errors) {
        
        /* Serial Number */
        if (StringUtils.isBlank(serialNumber.getSerialNumber())) {
            errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.serialNumber.required");
        } else if(serialNumber.getSerialNumber().length() > 30) {
            errors.rejectValue("serialNumber", "yukon.web.modules.operator.hardware.error.serialNumber.toolong");
        }
        
    }
}