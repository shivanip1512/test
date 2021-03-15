package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationHelper;
import com.cannontech.stars.util.ServletUtils;

public class LMCopyApiValidator extends SimpleValidator<LMCopy> {
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private YukonApiValidationHelper yukonApiValidationHelper;

    public LMCopyApiValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy lmCopy, Errors errors) {

        // Group Name
        String paoId = ServletUtils.getPathVariable("id");
        yukonApiValidationHelper.validatePaoName(lmCopy.getName(), null, errors, "Name", paoId);
        
        //lmApiValidatorHelper.validateCopyPaoName(lmCopy.getName(), errors, "Name");

        // Validate routeId if present.
        lmApiValidatorHelper.validateRouteId(lmCopy, errors, "RouteId");
    }
}
