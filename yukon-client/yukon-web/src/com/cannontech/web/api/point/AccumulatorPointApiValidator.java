package com.cannontech.web.api.point;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.web.tools.points.model.AccumulatorPointModel;
import com.cannontech.web.tools.points.model.PointAccumulatorModel;

public class AccumulatorPointApiValidator extends ScalarPointApiValidator<AccumulatorPointModel> {
    @Autowired private YukonApiValidationUtils yukonApiValidationUtils;

    public AccumulatorPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return AccumulatorPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(AccumulatorPointModel accumulatorPointModel, Errors errors) {
        super.doValidation(accumulatorPointModel, errors);

        if (accumulatorPointModel != null) {
            PointAccumulatorModel pointAccumulator = accumulatorPointModel.getAccumulatorPoint();

            if (pointAccumulator != null) {
                if (pointAccumulator.getMultiplier() != null) {
                    yukonApiValidationUtils.checkRange(errors, "accumulatorPoint.multiplier", pointAccumulator.getMultiplier(), -99999999.0, 99999999.0, false);
                }
                if (pointAccumulator.getDataOffset() != null) {
                    yukonApiValidationUtils.checkRange(errors, "accumulatorPoint.dataOffset", pointAccumulator.getDataOffset(), -99999999.0, 99999999.0, false);
                }
            }
        }

    }

}
