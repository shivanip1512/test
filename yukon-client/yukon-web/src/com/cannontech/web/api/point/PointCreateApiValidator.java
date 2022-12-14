package com.cannontech.web.api.point;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;

public class PointCreateApiValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {

    @SuppressWarnings("unchecked")
    public PointCreateApiValidator() {
        super((Class<T>) PointBaseModel.class);
    }

    public PointCreateApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T pointBase, Errors errors) {
        // Check if point Type is NULL
        YukonApiValidationUtils.checkIfFieldRequired("pointType", errors, pointBase.getPointType(), "Point Type");
        // Check if point Name is NULL
        YukonApiValidationUtils.checkIfFieldRequired("pointName", errors, pointBase.getPointName(), "Name");
        // Check if pointOffset is NULL
        YukonApiValidationUtils.checkIfFieldRequired("pointOffset", errors, pointBase.getPointOffset(), "Point Offset");
        // Check if paoId is NULL
        YukonApiValidationUtils.checkIfFieldRequired("paoId", errors, pointBase.getPaoId(), "PaoId");

        if (pointBase instanceof ScalarPointModel) {
            ScalarPointModel<?> scalarPointModel = (ScalarPointModel<?>) pointBase;
            YukonApiValidationUtils.checkIfFieldRequired("pointUnit", errors, scalarPointModel.getPointUnit(), "Point Unit");
            if (!errors.hasFieldErrors("pointUnit")) {
                YukonApiValidationUtils.checkIfFieldRequired("pointUnit.uomId", errors, scalarPointModel.getPointUnit().getUomId(), "pointUnit.uomId");
            }
        }

    }

}
