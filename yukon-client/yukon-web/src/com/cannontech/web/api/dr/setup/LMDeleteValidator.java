package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMDelete;
import com.cannontech.common.validator.SimpleValidator;

public class LMDeleteValidator extends SimpleValidator<LMDelete> {
    @Autowired private LoadGroupValidatorHelper loadGroupValidatorHelper;

    public LMDeleteValidator() {
        super(LMDelete.class);
    }

    @Override
    protected void doValidation(LMDelete loadGroup, Errors errors) {
        // Group Name
        loadGroupValidatorHelper.checkIfEmptyGroupName(loadGroup.getName(), errors);
    }
}
