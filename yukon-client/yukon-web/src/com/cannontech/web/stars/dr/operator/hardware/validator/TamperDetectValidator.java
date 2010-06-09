package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfigurationDto;

public class TamperDetectValidator extends
        SimpleValidator<HardwareConfigurationDto> {

    @Override
    protected void doValidation(HardwareConfigurationDto target, Errors errors) {
        String[] tamperDetect = target.getTamperDetect();
        for (int index = 0; index < tamperDetect.length; index++) {
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "tamperDetect[" + index + "]",
                                                       tamperDetect[index], 10);
        }
    }

    public TamperDetectValidator() {
        super(HardwareConfigurationDto.class);
    }
}
