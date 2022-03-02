package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;

public class LMCopyValidator extends SimpleValidator<LMCopy> {
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;

    public LMCopyValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy lmCopy, Errors errors) {

        // Group Name
        YukonApiValidationUtils.validateCopyPaoName(lmCopy.getName(), errors, "Name");

        // Validate routeId if present.
        lmApiValidatorHelper.validateRouteId(lmCopy, errors, "RouteId");
    }
}
