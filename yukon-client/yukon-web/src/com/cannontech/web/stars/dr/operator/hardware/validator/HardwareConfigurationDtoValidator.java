package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.stars.dr.operator.hardware.model.AddressingInfo;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfigurationDto;

public class HardwareConfigurationDtoValidator extends
        SimpleValidator<HardwareConfigurationDto> {

    public HardwareConfigurationDtoValidator() {
        super(HardwareConfigurationDto.class);
    }

    @Override
    protected void doValidation(HardwareConfigurationDto target, Errors errors) {
        AddressingInfo addressingInfo = target.getAddressingInfo();
        if (addressingInfo != null) {
            addressingInfo.validate(errors);
        }
    }
}
