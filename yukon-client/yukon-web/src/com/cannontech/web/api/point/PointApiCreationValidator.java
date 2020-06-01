package com.cannontech.web.api.point;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;

public class PointApiCreationValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {

    @SuppressWarnings("unchecked")
    public PointApiCreationValidator() {
        super((Class<T>) PointBaseModel.class);
    }

    public PointApiCreationValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T pointBase, Errors errors) {
        // Check if point Type is NULL
        YukonValidationUtils.checkIfFieldRequired("pointType", errors, pointBase.getPointType(), "pointType");
        // Check if point Name is NULL
        YukonValidationUtils.checkIfFieldRequired("pointName", errors, pointBase.getPointName(), "pointName");
        // Check if pointOffset is NULL
        YukonValidationUtils.checkIfFieldRequired("pointOffset", errors, pointBase.getPointOffset(), "pointOffset");
        // Check if paoId is NULL
        YukonValidationUtils.checkIfFieldRequired("paoId", errors, pointBase.getPaoId(), "paoId");

        if (pointBase instanceof ScalarPointModel) {
            ScalarPointModel<?> scalarPointModel = (ScalarPointModel<?>) pointBase;
            YukonValidationUtils.checkIfFieldRequired("pointUnit", errors, scalarPointModel.getPointUnit(), "pointUnit");
            if (!errors.hasFieldErrors("pointUnit")) {
                YukonValidationUtils.checkIfFieldRequired("pointUnit.uomID", errors, scalarPointModel.getPointUnit().getUomId(), "pointUnit.uomId");
            }
        }

    }

}
