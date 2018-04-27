package com.cannontech.web.tools.points.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.web.tools.points.model.LitePointModel;

@Service
public class CopyPointValidator extends SimpleValidator<LitePointModel> {

    @Autowired private PointValidationUtil pointValidationUtil;

    public CopyPointValidator() {
        super(LitePointModel.class);
    }

    @Override
    public void doValidation(LitePointModel pointModel, Errors errors) {
        pointValidationUtil.validatePointName(pointModel, "pointName", errors, true);
        pointValidationUtil.validatePointOffset(pointModel, "pointOffset", errors, true);
    }

}
