package com.cannontech.web.api.picker;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;

public class PickerIdSearchApiValidator extends SimpleValidator<PickerIdSearchCriteria> {

    public PickerIdSearchApiValidator() {
        super(PickerIdSearchCriteria.class);
    }

    @Override
    protected void doValidation(PickerIdSearchCriteria idSearchCriteria, Errors errors) {
        // Check if type is NULL
        YukonValidationUtils.checkIfFieldRequired("type", errors, idSearchCriteria.getType(), "type");

        // Check if getInitialIds is NULL
        YukonValidationUtils.checkIfFieldRequired("initialIds", errors, idSearchCriteria.getInitialIds(), "initialIds");
    }
}