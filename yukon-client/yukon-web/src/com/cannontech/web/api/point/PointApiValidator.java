package com.cannontech.web.api.point;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.AlarmState;
import com.cannontech.web.tools.points.model.AnalogPointModel;
import com.cannontech.web.tools.points.model.PointAlarming;
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
    @Autowired private AlarmCatDao alarmCatDao;
    private static final String baseKey = "yukon.web.api.error";
    public static final int maxFdrInterfaceTranslations = 5;

    @SuppressWarnings("unchecked")
    public PointApiValidator() {
        super((Class<T>) PointBaseModel.class);
    }

    public PointApiValidator(Class<T> objectType) {
        super(objectType);
    }

    @Override
    protected void doValidation(T target, Errors errors) {
        PointType pointType = target.getPointType();
        boolean isCreationOperation = target.getPointId() == null ? true : false;
        
        if (target.getPointName() != null) {
            pointValidationUtil.validateName("pointName", errors, target.getPointName());
        }
        if (target.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(target.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", "yukon.web.api.error.doesNotExist", new Object[] { target.getPaoId() }, "");
            }

            if (!errors.hasFieldErrors("paoId")) {
                pointValidationUtil.checkIfPaoIdChanged(errors,target, isCreationOperation);
            }
            
            if (!errors.hasFieldErrors("paoId")) {

                if (!errors.hasFieldErrors("pointName") && target.getPointName() != null) {
                    pointValidationUtil.validatePointName(target, "pointName", errors, isCreationOperation);
                }

                if (target.getPointOffset() != null) {
                    pointValidationUtil.validatePointOffset(target, "pointOffset", errors, isCreationOperation);
                }
            }
        }

        if(!errors.hasFieldErrors("pointType") && target.getPointType() != null) {
            pointValidationUtil.checkIfPointTypeChanged(errors, target, isCreationOperation);
        }
        
        if (target instanceof ScalarPointModel) {
            validateScalarPointModel(target, errors);
        }

        validateArchiveSettings(target, errors);
        validateStateGroupId(target, errors);
        validateStaleDataSettings(target, errors);
        validateAlarming(target.getAlarming(), pointType, errors);
        if (target instanceof AnalogPointModel) {
            validateAnalogPointModel(target, errors);
        }

        validateFdrTranslation(target.getFdrList(), errors);
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

    private void validateAlarming(PointAlarming pointAlarming, PointType pointType, Errors errors) {
        if (pointAlarming != null) {
            // Validate notificationGroupId.
            Integer notificationGroupId = pointAlarming.getNotificationGroupId();
            if (notificationGroupId != null) {
                Optional<LiteNotificationGroup> existingNotifGroup = serverDatabaseCache
                                                                        .getAllContactNotificationGroups()
                                                                        .stream()
                                                                        .filter(e -> e.getNotificationGroupID() == notificationGroupId)
                                                                        .findFirst();
                if (existingNotifGroup.isEmpty()) {
                    errors.rejectValue("alarming.notificationGroupId", "yukon.web.api.error.doesNotExist", new Object[] { "Notification GroupId" }, "");
                }
            }
            
            // Validate alarmTableList
            List<AlarmTableEntry> alarmList = pointAlarming.getAlarmTableList();
            if (alarmList != null && CollectionUtils.isNotEmpty(alarmList)) {
                List<AlarmState> alarmStates = AlarmState.getOtherAlarmStates();
                if (pointType == PointType.Status || pointType == PointType.CalcStatus) {
                    alarmStates = AlarmState.getStatusAlarmStates();
                }

                for(int i = 0; i < alarmList.size(); i++) {
                    AlarmTableEntry entry = alarmList.get(i);
                    if (entry.getCondition()!= null && !alarmStates.contains(entry.getCondition())) {
                        errors.rejectValue("alarming.alarmTableList[" + i + "].condition", "yukon.web.api.error.invalid", new Object[] { "Condition" }, "");
                    }

                    if (entry.getCondition() == null && entry.getCategory() != null && entry.getNotify() != null) {
                        errors.rejectValue("alarming.alarmTableList[" + i + "].condition", "yukon.web.error.fieldrequired", new Object[] { "Condition" }, "");
                    }

                    if(entry.getCategory() != null) {
                        Optional<LiteAlarmCategory> catagory = alarmCatDao.getAlarmCategories()
                                                                          .stream()
                                                                          .filter(e -> e.getCategoryName().equals(entry.getCategory()))
                                                                          .findFirst();
                       if (catagory.isEmpty()) {
                           errors.rejectValue("alarming.alarmTableList[" + i + "].category", "yukon.web.api.error.invalid", new Object[] { "Category" }, "");
                       }
                    }
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

    /**
     * Validate FdrTranslation Fields.
     */

    private void validateFdrTranslation(List<FdrTranslation> fdrList, Errors errors) {

        if (CollectionUtils.isNotEmpty(fdrList)) {

            for (int i = 0; i < fdrList.size(); i++) {
                errors.pushNestedPath("fdrList[" + i + "]");
                FdrTranslation fdrTranslation = fdrList.get(i);

                FdrInterfaceType fdrInterfaceType = fdrTranslation.getFdrInterfaceType();

                if (fdrInterfaceType == null) {
                    errors.rejectValue("fdrInterfaceType", "yukon.web.error.fieldrequired", new Object[] { "Interface" }, "");
                }

                if (!errors.hasFieldErrors("fdrInterfaceType")) {
                    FdrDirection fdrDirection = fdrTranslation.getDirection();
                    List<FdrDirection> supportedDirections = fdrInterfaceType.getSupportedDirectionsList();
                    String supportedDirectionsInString  = supportedDirections.stream().map(direction -> direction.name()).collect(Collectors.joining(", "));

                    if (fdrDirection == null || !supportedDirections.contains(fdrDirection)) {
                        errors.rejectValue("direction", baseKey + ".fdr.supportedDirection", new Object[] { supportedDirectionsInString, fdrInterfaceType }, "");
                    }

                    Map<FdrInterfaceOption, String> parameterMap = new HashMap<>();
                    List<FdrInterfaceOption> supportedOptions = fdrInterfaceType.getInterfaceOptionsList();
                    String translation = fdrTranslation.getTranslation();

                    if (translation != null) {
                        String[] parameters = translation.split(";");


                        if (parameterMap.size() > maxFdrInterfaceTranslations) {
                            errors.reject(baseKey + ".fdr.invalidTranslationPropertyCount", new Object[] { maxFdrInterfaceTranslations }, "");
                        }

                        for (String paramSet : parameters) {
                            int splitSpot = paramSet.indexOf(":");
                            if (splitSpot != -1) {
                                String option = paramSet.substring(0, splitSpot);

                                FdrInterfaceOption fdrInterfaceOption = supportedOptions.stream()
                                                                                        .filter(interfaceOption -> interfaceOption.getOptionLabel()
                                                                                                                                  .equals(option))
                                                                                        .findFirst()
                                                                                        .orElse(null);

                                if (fdrInterfaceOption != null) {

                                    String fieldValue = paramSet.substring(splitSpot + 1);
                                    if (!(parameterMap.containsKey(fdrInterfaceOption))) {
                                        parameterMap.put(fdrInterfaceOption, fieldValue);
                                    } else {
                                        errors.rejectValue("translation",
                                                           baseKey + ".fdr.duplicateTranslationProperty",
                                                           new Object[] { fdrInterfaceOption.getOptionLabel(), fdrInterfaceType },
                                                           "");
                                    }

                                    FdrOptionType optionType = fdrInterfaceOption.getOptionType();

                                    if (!(optionType == FdrOptionType.TEXT && fieldValue.equals(CtiUtilities.STRING_NONE)) && !(fdrInterfaceOption.isValid(fieldValue))) {
                                        errors.rejectValue("translation",
                                                           baseKey + ".fdr.invalidTranslationPropertyValue",
                                                           new Object[] { fieldValue, fdrInterfaceOption.getOptionLabel(), fdrInterfaceType },
                                                           "");
                                    }
                                } else {
                                    errors.rejectValue("translation",
                                                       baseKey + ".fdr.inValidTranslationPropertyInterface",
                                                       new Object[] { option, fdrInterfaceType },
                                                       "");
                                }

                            }
                        }
                    }
                    String missedFdrInterfaceOptions = supportedOptions.stream()
                                                                       .filter(option -> !(parameterMap.keySet().contains(option)))
                                                                       .map(option -> option.getOptionLabel())
                                                                       .collect(Collectors.joining(", "));

                    if (StringUtils.isNotBlank(missedFdrInterfaceOptions)) {
                        errors.rejectValue("translation",
                                           baseKey + ".fdr.missingTranslationProperty",
                                           new Object[] { missedFdrInterfaceOptions, fdrInterfaceType },
                                           "");
                    }

                }
            }

                errors.popNestedPath();
            }
    
        }
    

}
