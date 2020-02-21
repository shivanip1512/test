package com.cannontech.web.api.dr.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.dr.setup.LoadGroupCopy;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.stars.util.ServletUtils;

public class LMCopyValidator extends SimpleValidator<LMCopy> {
    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private PaoDao paoDao;

    public LMCopyValidator() {
        super(LMCopy.class);
    }

    @Override
    protected void doValidation(LMCopy lmCopy, Errors errors) {

        // Group Name
        lmValidatorHelper.validateCopyPaoName(lmCopy.getName(), errors, "Name");
        // Validate routeId if present.
        lmValidatorHelper.validateRouteId(lmCopy, lmCopy.getName(), errors, "RouteId");
    }
}
