package com.cannontech.web.bulk;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class MeterProgrammingModelValidator extends SimpleValidator<MeterProgrammingModel> {

    private final static int maxPasswordLength = 30;

    public MeterProgrammingModelValidator() {
        super(MeterProgrammingModel.class);
    }

    @Override
    public void doValidation(MeterProgrammingModel config, Errors errors) {
        
        if (config.isNewProgram()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "paoType", "yukon.web.error.isBlank");
            YukonValidationUtils.checkExceedsMaxLength(errors,
                                                       "password",
                                                       config.getPassword(),
                                                       maxPasswordLength);
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "yukon.web.error.isBlank");
        } else {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "existingProgramGuid", "yukon.web.error.isBlank");
        }

    }

}
