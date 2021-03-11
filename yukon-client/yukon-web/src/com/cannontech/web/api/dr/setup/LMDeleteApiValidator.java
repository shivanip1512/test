package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.validator.SimpleValidator;

public class LMDeleteApiValidator extends SimpleValidator<LMDelete> {
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;

    public LMDeleteApiValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete lmDelete, Errors errors) {
        // Name
        lmApiValidatorHelper.checkIfFieldRequired("name", errors, lmDelete.getName(), "Name");
    }
}
