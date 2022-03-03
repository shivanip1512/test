package com.cannontech.web.api.picker;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class PickerSearchApiValidator extends SimpleValidator<PickerSearchCriteria> {

    public PickerSearchApiValidator() {
        super(PickerSearchCriteria.class);
    }

    @Override
    protected void doValidation(PickerSearchCriteria searchCriteria, Errors errors) {

        // Check if type is NULL
        YukonValidationUtils.checkIfFieldRequired("type", errors, searchCriteria.getType(), "type");
    }
}