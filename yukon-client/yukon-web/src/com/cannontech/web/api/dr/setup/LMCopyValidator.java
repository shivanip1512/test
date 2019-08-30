package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;

public class LMCopyValidator extends SimpleValidator<LMCopy>{
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LMCopyValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy lmCopy, Errors errors) {

        // Group Name
        lmValidatorHelper.validateCopyPaoName(lmCopy.getName(), errors, "Name");

    }
}
