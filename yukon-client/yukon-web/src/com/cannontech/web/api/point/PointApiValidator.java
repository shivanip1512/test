package com.cannontech.web.api.point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.tags.IAlarmDefs;
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
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.notification.NotificationGroup;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.PointAlarming;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class PointApiValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private AlarmCatDao alarmCatDao;
    protected static final String baseKey = "yukon.web.api.error";
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

        validateArchiveSettings(target, errors);
        validateStateGroupId(target, errors);
        validateStaleDataSettings(target, errors);
        validateAlarming(target.getAlarming(), pointType, errors, target.getStateGroupId());
        validateFdrTranslation(target.getFdrList(), errors);
    }

    /**
     * validate Alarming fields.
     */
    private void validateAlarming(PointAlarming pointAlarming, PointType pointType, Errors errors, Integer stateGroupID) {
        if (pointAlarming != null) {
            // Validate notificationGroupId.
            Integer notificationGroupId = pointAlarming.getNotificationGroupId();
            boolean isNullOrDefault = notificationGroupId == null || 
                                          notificationGroupId == NotificationGroup.NONE_NOTIFICATIONGROUP_ID;
            if (!isNullOrDefault) {
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
                List<String> alarmStates = Arrays.asList(IAlarmDefs.OTHER_ALARM_STATES);
                if (pointType == PointType.Status || pointType == PointType.CalcStatus) {
                    alarmStates = Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES);
                    // Add all state present in the State Group
                    // TODO : Case for stateGroupID = null need to handle for Status point type.
                    if (stateGroupID != null) {
                        List<String> rawStates = stateGroupDao.getStateGroup(stateGroupID).getStatesList()
                                                                                          .stream()
                                                                                          .map(e -> String.valueOf(e.getLiteID()))
                                                                                          .collect(Collectors.toList());
                        alarmStates.addAll(rawStates);
                    }
                }

                List<String> alarmStateEntries = new ArrayList<>();
                for (int i = 0; i < alarmList.size(); i++) {
                    errors.pushNestedPath("alarming.alarmTableList[" + i + "]");
                    AlarmTableEntry entry = alarmList.get(i);
                    if (entry.getCondition()!= null && !alarmStates.contains(entry.getCondition())) {
                        errors.rejectValue("condition", "yukon.web.api.error.invalid", new Object[] { "Condition" }, "");
                    }
                    if (!errors.hasFieldErrors("condition") && entry.getCondition()!= null 
                            && alarmStateEntries.contains(entry.getCondition())) {
                        errors.rejectValue("condition", "yukon.web.api.error.field.uniqueError", new Object[] { "Condition", entry.getCondition()}, "");
                    }
                    if (entry.getCondition() == null && entry.getCategory() != null && entry.getNotify() != null) {
                        errors.rejectValue("condition", "yukon.web.error.fieldrequired", new Object[] { "Condition" }, "");
                    }

                    if (entry.getCategory() != null) {
                        Optional<LiteAlarmCategory> catagory = alarmCatDao.getAlarmCategories()
                                                                          .stream()
                                                                          .filter(e -> e.getCategoryName().equals(entry.getCategory()))
                                                                          .findFirst();
                       if (catagory.isEmpty()) {
                           errors.rejectValue("category", "yukon.web.api.error.invalid", new Object[] { "Category" }, "");
                       }
                    }

                    if (entry.getCondition()!= null) {
                        alarmStateEntries.add(entry.getCondition());
                    }
                    errors.popNestedPath();
                }
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
     * Validate FdrTranslation Fields.
     */

    private void validateFdrTranslation(List<FdrTranslation> fdrList, Errors errors) {

        if (CollectionUtils.isNotEmpty(fdrList)) {
            Set<FdrTranslation> usedTypes = new HashSet<>();
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
                        
                        if (usedTypes.contains(fdrTranslation)) {
                            errors.reject("yukon.web.modules.tools.point.error.fdr.unique", new Object[] { fdrInterfaceType },
                                    "");
                        }
                        usedTypes.add(fdrTranslation);

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
                errors.popNestedPath();
            }

        }

    }

}
