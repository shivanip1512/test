package com.cannontech.web.api.point;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.web.tools.points.model.AccumulatorPointModel;
import com.cannontech.web.tools.points.model.PointAccumulatorModel;
import com.cannontech.web.tools.points.model.ScalarPointModel;

public class AccumulatorPointApiValidator extends ScalarPointApiValidator<AccumulatorPointModel> {

    public AccumulatorPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return AccumulatorPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(ScalarPointModel<?> target, Errors errors) {
        super.doValidation(target, errors);

        AccumulatorPointModel accumulatorPointModel = (AccumulatorPointModel) target;

        if (accumulatorPointModel != null) {
            PointAccumulatorModel pointAccumulator = accumulatorPointModel.getAccumulatorPoint();

            if (pointAccumulator != null) {
                if (pointAccumulator.getMultiplier() != null) {
                    YukonValidationUtils.checkRange(errors, "accumulatorPoint.multiplier", pointAccumulator.getMultiplier(), -99999999.0, 99999999.0, false);
                }
                if (pointAccumulator.getDataOffset() != null) {
                    YukonValidationUtils.checkRange(errors, "accumulatorPoint.dataOffset", pointAccumulator.getDataOffset(), -99999999.0, 99999999.0, false);
                }
            }
        }

    }

}
