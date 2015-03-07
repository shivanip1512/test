package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.hardware.model.AddressingInfo;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;

public class HardwareConfigurationDtoValidator extends
        SimpleValidator<HardwareConfig> {

    public HardwareConfigurationDtoValidator() {
        super(HardwareConfig.class);
    }

    @Override
    protected void doValidation(HardwareConfig target, Errors errors) {
        AddressingInfo addressingInfo = target.getAddressingInfo();
        if (addressingInfo != null) {
            addressingInfo.validate(errors);
        }
    }
}
