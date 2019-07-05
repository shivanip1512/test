package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.program.setup.model.LoadProgramCopy;
import com.cannontech.common.validator.SimpleValidator;

public class LMProgramCopyValidator extends SimpleValidator<LoadProgramCopy> {
    @Autowired private LMValidatorHelper lmValidatorHelper;

    public LMProgramCopyValidator() {
        super(LoadProgramCopy.class);
    }

    @Override
    protected void doValidation(LoadProgramCopy loadProgramCopy, Errors errors) {
        lmValidatorHelper.validateCopyPaoName(loadProgramCopy.getName(), errors, "Program Name");
        lmValidatorHelper.checkIfEmptyPaoType(errors);
    }
}
