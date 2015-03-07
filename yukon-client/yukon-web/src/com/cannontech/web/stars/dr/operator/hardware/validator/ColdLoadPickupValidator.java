package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfig;

public class ColdLoadPickupValidator extends
        SimpleValidator<HardwareConfig> {

    @Override
    protected void doValidation(HardwareConfig target, Errors errors) {
        String[] coldLoadPickup = target.getColdLoadPickup();
        for (int index = 0; index < coldLoadPickup.length; index++) {
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "coldLoadPickup[" + index + "]",
                                                       coldLoadPickup[index], 10);
        }
    }

    public ColdLoadPickupValidator() {
        super(HardwareConfig.class);
    }
}
