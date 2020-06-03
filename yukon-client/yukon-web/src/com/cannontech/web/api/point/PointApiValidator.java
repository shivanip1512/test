package com.cannontech.web.api.point;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.PointAnalog;
import com.cannontech.web.tools.points.model.PointAnalogControl;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.model.PointLimitModel;
import com.cannontech.web.tools.points.model.PointUnit;
import com.cannontech.web.tools.points.model.ScalarPointModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class PointApiValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private StateGroupDao stateGroupDao;
    private static final String baseKey = "yukon.web.api.error";

    @SuppressWarnings("unchecked")
    public PointApiValidator() {
        super((Class<T>) PointBaseModel.class);
    }

    public PointApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T target, Errors errors) {

        if (target.getPointName() != null) {
            pointValidationUtil.validateName("pointName", errors, target.getPointName());
        }

        if (target.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(target.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", "yukon.web.api.error.doesNotExist", new Object[] { target.getPaoId() }, "");
            }

            if (!errors.hasFieldErrors("paoId")) {

                boolean isCreationOperation = target.getPointId() == null ? true : false;

                if (ServletUtils.getPathVariable("id") != null) {
                    isCreationOperation = false;
                }

                if (!errors.hasFieldErrors("pointName") && target.getPointName() != null) {
                    pointValidationUtil.validatePointName(target, "pointName", errors, isCreationOperation);
                }

                if (target.getPointOffset() != null) {
                    pointValidationUtil.validatePointOffset(target, "pointOffset", errors, isCreationOperation);
                }
            }
        }

        if (target instanceof ScalarPointModel) {
            validateScalarPointModel(target, errors);
        }

        validateArchiveSettings(target, errors);
        validateStateGroupId(target, errors);
        validateStaleDataSettings(target, errors);

        if (target instanceof AnalogPointModel) {
            validateAnalogPointModel(target, errors);
        }

    }

    /**
     * Validate ScalarPoint Fields.
     */

    private void validateScalarPointModel(T target, Errors errors) {

        ScalarPointModel<?> scalarPointModel = (ScalarPointModel<?>) target;
        validatePointLimit(scalarPointModel, errors);
        validatePointUnit(scalarPointModel.getPointUnit(), errors);
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
     * Validate ArchiveSettings Fields.
     */

    private void validateArchiveSettings(T target, Errors errors) {
        if (target.getArchiveType() != null && (target.getArchiveType() == PointArchiveType.ON_TIMER || target.getArchiveType() == PointArchiveType.ON_TIMER_OR_UPDATE)) {
            if (target.getArchiveInterval() != null) {
                TimeIntervals archiveInterval = TimeIntervals.fromSeconds(target.getArchiveInterval());
                if (!TimeIntervals.getArchiveIntervals().contains(archiveInterval)) {
                    errors.rejectValue("archiveInterval", baseKey + ".invalid", new Object[] { "Archive Interval" }, "");
                }
            } else {
                errors.rejectValue("archiveInterval", baseKey + ".invalid.archiveTimeInterval", new Object[] { "Archive Interval" }, "");
            }
        }

        if (target.getArchiveType() != null && (target.getArchiveType() == PointArchiveType.NONE || target.getArchiveType() == PointArchiveType.ON_CHANGE || target.getArchiveType() == PointArchiveType.ON_UPDATE)) {
            if (target.getArchiveInterval() != null && target.getArchiveInterval() != 0) {
                errors.rejectValue("archiveInterval", baseKey + ".invalid.archiveInterval");
            }
        }
    }

    /**
     * Validate State GroupId Fields.
     */

    private void validateStateGroupId(T target, Errors errors) {
        if (target.getStateGroupId() != null) {
            LiteStateGroup liteStateGroup = stateGroupDao.findStateGroup(target.getStateGroupId());
            if (liteStateGroup == null) {
                errors.rejectValue("stateGroupId", baseKey + ".invalid.stateGroupId", new Object[] { target.getStateGroupId() }, "");
            }
        }

    }

    /**
     * Validate Stale Data Fields.
     */

    private void validateStaleDataSettings(T target, Errors errors) {

        StaleData staleData = target.getStaleData();
        if (staleData != null) {
            if (staleData.getTime() != null) {
                YukonValidationUtils.checkRange(errors, "staleData.time", staleData.getTime(), 0, 99999999, false);
            }
            if (staleData.getUpdateStyle() != null) {
                if (!(staleData.getUpdateStyle() == 1 || staleData.getUpdateStyle() == 0)) {
                    YukonValidationUtils.rejectValues(errors, baseKey + ".staleData.updateStyle", "staleData.updateStyle");
                }
            }
        }
    }

    /**
     * Validate Analog Point Fields.
     */

    private void validateAnalogPointModel(T target, Errors errors) {
        AnalogPointModel analogPointModel = (AnalogPointModel) target;

        validatePointAnalog(analogPointModel.getPointAnalog(), errors);
        validatePointAnalogControl(analogPointModel.getPointAnalogControl(), errors);
    }

    /**
     * Validate Point Analog Control Fields.
     */
    private void validatePointAnalogControl(PointAnalogControl pointAnalogControl, Errors errors) {
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

                if (pointAnalogControl.getControlType() == AnalogControlType.NONE) {
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
}
