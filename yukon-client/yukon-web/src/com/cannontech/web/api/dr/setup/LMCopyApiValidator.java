package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationHelper;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.yukon.IDatabaseCache;

public class LMCopyApiValidator extends SimpleValidator<LMCopy> {
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private YukonApiValidationHelper yukonApiValidationHelper;
    @Autowired private IDatabaseCache serverDatabaseCache;

    public LMCopyApiValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy lmCopy, Errors errors) {

        // Group Name
        String paoId = ServletUtils.getPathVariable("id");
        yukonApiValidationHelper.validatePaoName(lmCopy.getName(),
                serverDatabaseCache.getAllPaosMap().get(Integer.valueOf(paoId)).getPaoType(), errors, "Name", paoId);

        // Validate routeId if present.
        lmApiValidatorHelper.validateRouteId(lmCopy, errors, "RouteId");
    }
}
