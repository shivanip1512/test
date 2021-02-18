package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
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
                    errors.rejectValue("pointUnit.uomId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { pointUnit.getUomId() }, "");
                }
            }

            Double highReasonabilityLimit = pointUnit.getHighReasonabilityLimit();
            Double lowReasonabilityLimit = pointUnit.getLowReasonabilityLimit();

            if (highReasonabilityLimit != null && lowReasonabilityLimit != null) {
                if (highReasonabilityLimit < lowReasonabilityLimit) {
                    errors.rejectValue("lowReasonability", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { "less than High Reasonability" }, "");
                }
            }

            if (highReasonabilityLimit != null && highReasonabilityLimit != CtiUtilities.INVALID_MAX_DOUBLE) {
                YukonApiValidationUtils.checkRange(errors, "pointUnit.highReasonabilityLimit", highReasonabilityLimit, -999999.999999, 999999.999999, true);
            }

            if (lowReasonabilityLimit != null && lowReasonabilityLimit != CtiUtilities.INVALID_MIN_DOUBLE) {
                YukonApiValidationUtils.checkRange(errors, "pointUnit.lowReasonabilityLimit", lowReasonabilityLimit, -999999.999999, 999999.999999, true);

            }

            if (pointUnit.getDecimalPlaces() != null) {
                YukonApiValidationUtils.checkRange(errors, "pointUnit.decimalPlaces", pointUnit.getDecimalPlaces(), 0, 10, true);
            }

            if (pointUnit.getMeterDials() != null) {
                YukonApiValidationUtils.checkRange(errors, "pointUnit.meterDials", pointUnit.getMeterDials(), 0, 10, true);
            }
        }

    }

    /**
     * Validate PointLimit Fields.
     */

    private void validatePointLimit(ScalarPointModel<?> scalarPointModel, Errors errors) {
        List<PointLimitModel> pointLimits = scalarPointModel.getLimits();
        if (pointLimits.size() > 2) {
            errors.rejectValue("limits", ApiErrorDetails.VALUE_OUTSIDE_VALID_RANGE.getCodeString(), new Object[] { "Limits", 0, 2 }, "");
        } else {
            if (CollectionUtils.isNotEmpty(pointLimits)) {
                int limitNumber = 0;
                for (int i = 0; i < scalarPointModel.getLimits().size(); i++) {
                    errors.pushNestedPath("limits[" + i + "]");
                    PointLimitModel pointLimit = pointLimits.get(i);
                    if (pointLimit != null) {
                        if ((pointLimit.getHighLimit() != null && pointLimit.getLowLimit() != null) && pointLimit.getHighLimit() < pointLimit.getLowLimit()) {
                            errors.rejectValue("lowLimit", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { "less than Upper Limit" }, "");
                        }

                        YukonApiValidationUtils.checkIfFieldRequired("limitNumber", errors, pointLimit.getLimitNumber(), "limitNumber");
                        if (!errors.hasFieldErrors("limitNumber")) {

                            if (!(pointLimit.getLimitNumber() == 1 || pointLimit.getLimitNumber() == 2)) {
                                errors.rejectValue("limitNumber", ApiErrorDetails.INVALID_VALUE.getCodeString(),  new Object[] { "1 - 2" }, "");
                            }

                            if (!errors.hasFieldErrors("limitNumber")) {
                                if (limitNumber == pointLimit.getLimitNumber()) {
                                    errors.rejectValue("limitNumber", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                            new Object[] { pointLimit.getLimitNumber() }, "");
                                }
                            }
                            limitNumber = pointLimit.getLimitNumber();
                        }

                        if (pointLimit.getHighLimit() != null) {
                            YukonApiValidationUtils.checkRange(errors, "highLimit", pointLimit.getHighLimit(), -99999999.0, 99999999.0, false);
                        }

                        if (pointLimit.getLowLimit() != null) {
                            YukonApiValidationUtils.checkRange(errors, "lowLimit", pointLimit.getLowLimit(), -99999999.0, 99999999.0, false);
                        }
                        if (pointLimit.getLimitDuration() != null) {
                            YukonApiValidationUtils.checkRange(errors, "limitDuration", pointLimit.getLimitDuration(), 0, 99999999, false);
                        }
                    }
                    errors.popNestedPath();
                }
            }
        }

    }

    @Override
    protected void validateArchiveSettings(T target, PointType pointType, Errors errors) {
        super.validateArchiveSettings(target, pointType, errors);

        if (target.getArchiveType() != null && (target.getArchiveType() == PointArchiveType.ON_TIMER || target.getArchiveType() == PointArchiveType.ON_TIMER_OR_UPDATE)) {
            if (target.getArchiveInterval() != null) {
                TimeIntervals archiveInterval = TimeIntervals.fromSeconds(target.getArchiveInterval());
                if (!TimeIntervals.getArchiveIntervals().contains(archiveInterval)) {
                    errors.rejectValue("archiveInterval", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                            new Object[] { StringUtils.join(TimeIntervals.getArchiveIntervals(), ", ") }, "");
                }
            } else {
                errors.rejectValue("archiveInterval", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                        new Object[] { "greater than 0 when Archive Data type is On Timer, or (On Timer Or Update)." }, "");
            }
        }
    }
}
