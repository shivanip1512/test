package com.cannontech.web.api.point;

import org.springframework.validation.Errors;

import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.PointAnalog;
import com.cannontech.web.tools.points.model.PointAnalogControlModel;

public class AnalogPointApiValidator extends ScalarPointApiValidator<AnalogPointModel> {

    public AnalogPointApiValidator() {
        super();
    }

    @Override
    public boolean supports(Class clazz) {
        return AnalogPointModel.class.isAssignableFrom(clazz);
    }

    @Override
    protected void doValidation(AnalogPointModel analogPointModel, Errors errors) {
        super.doValidation(analogPointModel, errors);

        validatePointAnalog(analogPointModel.getPointAnalog(), errors);
        validatePointAnalogControl(analogPointModel.getPointAnalogControl(), errors);

    }

    /**
     * Validate Point Analog Fields.
     */
    private void validatePointAnalog(PointAnalog pointAnalog, Errors errors) {

        if (pointAnalog != null) {

            if (pointAnalog.getDeadband() != null) {
                YukonValidationUtils.checkRange(errors, "pointAnalog.deadband", pointAnalog.getDeadband(), -1.0, 99999999.0, false);
            }

            if (pointAnalog.getMultiplier() != null) {
                YukonValidationUtils.checkRange(errors, "pointAnalog.multiplier", pointAnalog.getMultiplier(), -99999999.0, 99999999.0, false);
            }

            if (pointAnalog.getDataOffset() != null) {
                YukonValidationUtils.checkRange(errors, "pointAnalog.dataOffset", pointAnalog.getDataOffset(), -99999999.0, 99999999.0, false);
            }
        }
    }

    /**
     * Validate Point Analog Control Fields.
     */
    private void validatePointAnalogControl(PointAnalogControlModel pointAnalogControl, Errors errors) {
        if (pointAnalogControl != null) {
            if (pointAnalogControl.getControlType() != null) {

                if (pointAnalogControl.getControlType() == AnalogControlType.NORMAL && pointAnalogControl.getControlOffset() != null) {
                    YukonValidationUtils.checkRange(errors,
                                                    "pointAnalogControl.controlOffset",
                                                    pointAnalogControl.getControlOffset(),
                                                    -99999999,
                                                    99999999,
                                                    false);
                }
            }
            // if for accepting non-default values, need to specify control type in request otherwise it would accept only default values.
            if (pointAnalogControl.getControlType() == null || pointAnalogControl.getControlType() == AnalogControlType.NONE) {
                if (pointAnalogControl.getControlOffset() != null && pointAnalogControl.getControlOffset() != 0) {
                    errors.rejectValue("pointAnalogControl.controlOffset", baseKey + ".invalid.controlOffset");
                }

                if (pointAnalogControl.getControlInhibited() != null && pointAnalogControl.getControlInhibited().equals(true)) {
                    errors.rejectValue("pointAnalogControl.controlInhibited", baseKey + ".invalid.controlInhibited");
                }
            }
        }

    }

}
