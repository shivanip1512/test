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
import com.cannontech.web.tools.points.model.LitePointModel;
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

        if (target.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(target.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", "yukon.web.api.error.doesNotExist");
            }

            if (!errors.hasFieldErrors("paoId") && target.getPointName() != null && target.getPointOffset() != null) {
                boolean physicalOffset = target.getPointOffset() > 0 ? true : false;
                Integer pointId = null;
                if (ServletUtils.getPathVariable("id") != null) {
                    pointId = Integer.valueOf(ServletUtils.getPathVariable("id"));
                }

                LitePointModel litePointModel = new LitePointModel(target.getPointName(),
                                                                   pointId,
                                                                   physicalOffset,
                                                                   target.getPointOffset(),
                                                                   target.getPointType(),
                                                                   target.getPaoId());

                boolean isCreationOperation = pointId == null ? true : false;

                pointValidationUtil.validatePointName(litePointModel, "pointName", errors, isCreationOperation);
                pointValidationUtil.validatePointOffset(litePointModel, "pointOffset", errors, isCreationOperation);
            }
        }

        if (target instanceof ScalarPointModel) {
            ScalarPointModel<?> scalarPointModel = (ScalarPointModel<?>) target;

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

            PointUnit pointUnit = scalarPointModel.getPointUnit();
            if (pointUnit != null) {
                if (pointUnit.getUomId() != null) {
                    List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
                    List<Integer> uomIds = unitMeasures.stream().map(unit -> unit.getId()).collect(Collectors.toList());
                    if (!uomIds.contains(scalarPointModel.getPointUnit().getUomId())) {
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
        if (target.getArchiveType() != null && (target.getArchiveType() == PointArchiveType.ON_TIMER || target.getArchiveType() == PointArchiveType.ON_TIMER_OR_UPDATE)) {
            if (target.getArchiveInterval() != null) {
                TimeIntervals archiveInterval = TimeIntervals.fromSeconds(target.getArchiveInterval());
                if (!TimeIntervals.getArchiveIntervals().contains(archiveInterval)) {
                    errors.rejectValue("archiveInterval", baseKey + ".invalid", new Object[] { "Archive Interval" }, "");
                }
            }
        }

        if (target.getArchiveType() != null
                && (target.getArchiveType() == PointArchiveType.NONE || target.getArchiveType() == PointArchiveType.ON_CHANGE || target.getArchiveType() == PointArchiveType.ON_UPDATE)) {
            if (target.getArchiveInterval() != null && target.getArchiveInterval() != 0) {
                errors.rejectValue("archiveInterval", baseKey + ".invalid.archiveInterval");
            }
        }

        if (target.getStateGroupId() != null) {
            LiteStateGroup liteStateGroup = stateGroupDao.findStateGroup(target.getStateGroupId());
            if (liteStateGroup == null) {
                errors.rejectValue("stateGroupId", baseKey + ".invalid.stateGroupId", new Object[] { target.getStateGroupId() }, "");
            }
        }

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

        if (target instanceof AnalogPointModel) {
            AnalogPointModel analogPointModel = (AnalogPointModel) target;
            PointAnalog pointAnalog = analogPointModel.getPointAnalog();
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

            PointAnalogControl pointAnalogControl = analogPointModel.getPointAnalogControl();
            if (pointAnalogControl != null) {
                if (pointAnalogControl.getControlType() != null) {

                    if (pointAnalogControl.getControlType() == AnalogControlType.NORMAL && pointAnalogControl.getControlOffset() != null) {
                        YukonValidationUtils.checkRange(errors, "pointAnalogControl.controlOffset", pointAnalogControl.getControlOffset(), -99999999, 99999999, false);
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
    }
}
