package com.cannontech.web.api.dr.setup;

import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

public class LMDeleteApiValidator extends SimpleValidator<LMDelete> {
    public LMDeleteApiValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete lmDelete, Errors errors) {
        // Name
        YukonApiValidationUtils.checkIfFieldRequired("name", errors, lmDelete.getName(), "Name");
    }
}
