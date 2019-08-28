package com.cannontech.web.bulk;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

@Service
public class MeterProgrammingConfigurationValidator extends SimpleValidator<MeterProgrammingConfiguration> {


    public MeterProgrammingConfigurationValidator() {
        super(MeterProgrammingConfiguration.class);
    }

    @Override
    public void doValidation(MeterProgrammingConfiguration config, Errors errors) {
        
        if (config.isNewConfiguration()) {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "yukon.web.error.isBlank");
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "paoType", "yukon.web.error.isBlank");
        } else {
            YukonValidationUtils.rejectIfEmptyOrWhitespace(errors, "existingConfigurationGuid", "yukon.web.error.isBlank");
        }

    }

}
