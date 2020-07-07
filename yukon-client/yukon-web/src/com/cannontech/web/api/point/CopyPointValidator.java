package com.cannontech.web.api.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.LMCopy;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.web.tools.points.model.CopyPoint;

public class CopyPointValidator extends SimpleValidator<CopyPoint>{

    @Autowired private LMValidatorHelper lmValidatorHelper;

    public CopyPointValidator() {
        super(CopyPoint.class);
    }

    @Override
    protected void doValidation(CopyPoint copyPoint, Errors errors) {
        
        YukonValidationUtils.checkIfFieldRequired("pointName", errors, copyPoint.getPointName(), "pointName");
        // Check if pointOffset is NULL
        YukonValidationUtils.checkIfFieldRequired("pointOffset", errors, copyPoint.getPointOffset(), "pointOffset");
        
        // Group Name
      //  lmValidatorHelper.validateCopyPaoName(copyPoint.getName(), errors, "Name");

        // Validate routeId if present.
      //  lmValidatorHelper.validateRouteId(lmCopy, errors, "RouteId");
    }
}

