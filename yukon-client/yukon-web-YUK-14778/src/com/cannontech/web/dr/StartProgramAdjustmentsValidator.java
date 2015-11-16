package com.cannontech.web.dr;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.dr.program.model.GearAdjustment;

public class StartProgramAdjustmentsValidator extends SimpleValidator<StartProgramBackingBeanBase> {
    public StartProgramAdjustmentsValidator() {
        super(StartProgramBackingBeanBase.class);
    }

    @Override
    public void doValidation(StartProgramBackingBeanBase startProgram, Errors errors) {
        int index = 0;
        for (GearAdjustment gearAdjustment : startProgram.getGearAdjustments()) {
            int value = gearAdjustment.getAdjustmentValue();
            if (value < 0 || value > 250) {
                errors.rejectValue("gearAdjustments[" + index +
                                   "].adjustmentValue", "adjustmentOutOfRange");
            }
            index++;
        }
    }
}
