package com.cannontech.web.stars.dr.operator.hardware.validator;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.stars.dr.operator.hardware.model.HardwareConfigurationDto;

public class ProgramSplinterValidator extends
        SimpleValidator<HardwareConfigurationDto> {

    @Override
    protected void doValidation(HardwareConfigurationDto target, Errors errors) {
        Integer[] expressComProgram = target.getProgram();
        for (int index = 0; index < expressComProgram.length; index++) {
            YukonValidationUtils.checkRange(errors,
                                            "expressComProgram[" + index + "]",
                                            expressComProgram[index],
                                            0, 254, false);
        }
        Integer[] expressComSplinter = target.getSplinter();
        for (int index = 0; index < expressComSplinter.length; index++) {
            YukonValidationUtils.checkRange(errors,
                                            "expressComSplinter[" + index + "]",
                                            expressComSplinter[index],
                                            0, 254, false);
        }
    }

    public ProgramSplinterValidator() {
        super(HardwareConfigurationDto.class);
    }
}
