package com.cannontech.web.tools.points.validators;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.web.tools.points.model.PointModel;

@Service
public class CopyPointValidator extends PointValidator {

    @Override
    public void doValidation(PointModel pointModel, Errors errors) {
        super.validatePointName(pointModel, errors, true);
        super.validatePointOffset(pointModel, errors, true);
    }

}
