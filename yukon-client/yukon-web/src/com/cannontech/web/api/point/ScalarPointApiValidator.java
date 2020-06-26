package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.web.tools.points.model.PointLimitModel;
import com.cannontech.web.tools.points.model.PointUnit;
import com.cannontech.web.tools.points.model.ScalarPointModel;

public class ScalarPointApiValidator<T extends ScalarPointModel<?>> extends PointApiValidator<T> {

    public ScalarPointApiValidator() {
        super();
    }

    @Override
    protected void doValidation(T target, Errors errors) {
        super.doValidation(target, errors);

        validatePointLimit(target, errors);
        validatePointUnit(target.getPointUnit(), errors);

    }

    /**
     * Validate PointUnit Fields.
     */

    private void validatePointUnit(PointUnit pointUnit, Errors errors) {

        if (pointUnit != null) {
            if (pointUnit.getUomId() != null) {
                List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
                List<Integer> uomIds = unitMeasures.stream().map(unit -> unit.getId()).collect(Collectors.toList());
                if (!uomIds.contains(pointUnit.getUomId())) {
                    errors.rejectValue("pointUnit.uomId", "yukon.web.api.error.doesNotExist", new Object[] { "Uom Id" }, "");
                }
            }

            Double highReasonabilityLimit = pointUnit.getHighReasonabilityLimit();
            Double lowReasonabilityLimit = pointUnit.getLowReasonabilityLimit();

            if (highReasonabilityLimit != null && lowReasonabilityLimit != null) {
                if (highReasonabilityLimit < lowReasonabilityLimit) {
                    YukonValidationUtils.rejectValues(errors, "yukon.web.modules.tools.point.error.reasonability", "pointUnit.lowReasonabilityLimit");
                }
            }

            if (highReasonabilityLimit != null && highReasonabilityLimit != CtiUtilities.INVALID_MAX_DOUBLE) {
                YukonValidationUtils.checkRange(errors, "pointUnit.highReasonabilityLimit", highReasonabilityLimit, -999999.999999, 999999.999999, true);
            }

            if (lowReasonabilityLimit != null && lowReasonabilityLimit != CtiUtilities.INVALID_MIN_DOUBLE) {
                YukonValidationUtils.checkRange(errors, "pointUnit.lowReasonabilityLimit", lowReasonabilityLimit, -999999.999999, 999999.999999, true);

            }

            if (pointUnit.getDecimalPlaces() != null) {
                YukonValidationUtils.checkRange(errors, "pointUnit.decimalPlaces", pointUnit.getDecimalPlaces(), 0, 10, true);
            }

            if (pointUnit.getMeterDials() != null) {
                YukonValidationUtils.checkRange(errors, "pointUnit.meterDials", pointUnit.getMeterDials(), 0, 10, true);
            }
        }

    }

    /**
     * Validate PointLimit Fields.
     */

    private void validatePointLimit(ScalarPointModel<?> scalarPointModel, Errors errors) {
        List<PointLimitModel> pointLimits = scalarPointModel.getLimits();
        if (pointLimits.size() > 2) {
            errors.rejectValue("limits", baseKey + ".pointLimit.invalidSize");
        } else {
            if (CollectionUtils.isNotEmpty(pointLimits)) {
                int limitNumber = 0;
                for (int i = 0; i < scalarPointModel.getLimits().size(); i++) {
                    errors.pushNestedPath("limits[" + i + "]");
                    PointLimitModel pointLimit = pointLimits.get(i);
                    if (pointLimit != null) {
                        if ((pointLimit.getHighLimit() != null && pointLimit.getLowLimit() != null) && pointLimit.getHighLimit() < pointLimit.getLowLimit()) {
                            errors.rejectValue("lowLimit", "yukon.web.modules.tools.point.error.limits");
                        }

                        YukonValidationUtils.checkIfFieldRequired("limitNumber", errors, pointLimit.getLimitNumber(), "limitNumber");
                        if (!errors.hasFieldErrors("limitNumber")) {

                            if (!(pointLimit.getLimitNumber() == 1 || pointLimit.getLimitNumber() == 2)) {
                                errors.rejectValue("limitNumber", baseKey + ".invalid.limitNumber");
                            }

                            if (!errors.hasFieldErrors("limitNumber")) {
                                if (limitNumber == pointLimit.getLimitNumber()) {
                                    errors.rejectValue("limitNumber", baseKey + ".duplicateLimitNumbers");
                                }
                            }
                            limitNumber = pointLimit.getLimitNumber();
                        }

                        if (pointLimit.getHighLimit() != null) {
                            YukonValidationUtils.checkRange(errors, "highLimit", pointLimit.getHighLimit(), -99999999.0, 99999999.0, false);
                        }

                        if (pointLimit.getLowLimit() != null) {
                            YukonValidationUtils.checkRange(errors, "lowLimit", pointLimit.getLowLimit(), -99999999.0, 99999999.0, false);
                        }
                        if (pointLimit.getLimitDuration() != null) {
                            YukonValidationUtils.checkRange(errors, "limitDuration", pointLimit.getLimitDuration(), 0, 99999999, false);
                        }
                    }
                    errors.popNestedPath();
                }
            }
        }

    }
}
