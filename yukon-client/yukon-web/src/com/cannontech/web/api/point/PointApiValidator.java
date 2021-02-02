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

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
import com.cannontech.common.fdr.FdrTranslation;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.notification.NotificationGroup;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.editor.point.AlarmTableEntry;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.model.PointAlarming;
import com.cannontech.web.tools.points.model.PointBaseModel;
import com.cannontech.web.tools.points.validators.PointValidationUtil;
import com.cannontech.yukon.IDatabaseCache;

public class PointApiValidator<T extends PointBaseModel<?>> extends SimpleValidator<T> {
    @Autowired private PointValidationUtil pointValidationUtil;
    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired protected StateGroupDao stateGroupDao;
    @Autowired private AlarmCatDao alarmCatDao;
    @Autowired private PointDao pointDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    protected static final String baseKey = "yukon.web.api.error";
    private final static String pointBaseKey = "yukon.web.modules.tools.point.";
    public static final int maxFdrInterfaceTranslations = 5;
    private MessageSourceAccessor accessor;

    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

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
            YukonApiValidationUtils.checkIsBlank(errors, "pointName", target.getPointName(), "Name", false);
        }
        if (target.getPaoId() != null) {
            LiteYukonPAObject liteYukonPAObject = serverDatabaseCache.getAllPaosMap().get(target.getPaoId());
            if (liteYukonPAObject == null) {
                errors.rejectValue("paoId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { target.getPaoId() }, "");
            }

            if (!errors.hasFieldErrors("paoId")) {
                pointValidationUtil.checkIfPaoIdChanged(errors,target, isCreationOperation);
            }
            
            if (!errors.hasFieldErrors("paoId")) {

                if (!errors.hasFieldErrors("pointName") && target.getPointName() != null) {
                    pointValidationUtil.validatePointName(target, "pointName", errors, isCreationOperation);
                }

                if (target.getPointOffset() != null) {
                    YukonApiValidationUtils.checkRange(errors, "pointOffset", target.getPointOffset(), 0, 99999999, true);
                    if (!errors.hasFieldErrors("pointOffset")) {
                        pointValidationUtil.validatePointOffset(target, "pointOffset", errors, isCreationOperation);
                    }
                }

            }
        }

        if(!errors.hasFieldErrors("pointType") && target.getPointType() != null) {
            pointValidationUtil.checkIfPointTypeChanged(errors, target, isCreationOperation);
        }

        validateArchiveSettings(target, pointType, errors);
        validateStateGroupId(target, errors);
        validateStaleDataSettings(target, errors);
        validateAlarming(target.getAlarming(), pointType, errors, target.getStateGroupId(), target.getPointId());
        validateFdrTranslation(target.getFdrList(), errors);
    }

    /**
     * validate Alarming fields.
     */
    private void validateAlarming(PointAlarming pointAlarming, PointType pointType, Errors errors, Integer stateGroupID, Integer pointId) {
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
                    errors.rejectValue("alarming.notificationGroupId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(), new Object[] { "Notification GroupId" }, "");
                }
            }
            
            // Validate alarmTableList
            List<AlarmTableEntry> alarmList = pointAlarming.getAlarmTableList();
            if (alarmList != null && CollectionUtils.isNotEmpty(alarmList)) {
                List<String> alarmStates = new ArrayList<String>(Arrays.asList(IAlarmDefs.OTHER_ALARM_STATES));
                if (pointType == PointType.Status || pointType == PointType.CalcStatus) {
                    alarmStates = new ArrayList<String>(Arrays.asList(IAlarmDefs.STATUS_ALARM_STATES));
                    // Add all state present in the State Group

                    if (pointId != null && stateGroupID == null) {
                        stateGroupID = pointDao.getLitePoint(pointId).getStateGroupID();
                    } else if (pointId == null && stateGroupID == null) {
                        stateGroupID = -1;
                    }

                    if (!errors.hasFieldErrors("stateGroupId")) {
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

                    if (entry == null || entry.getCondition() == null) {
                        errors.rejectValue("condition", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                                new Object[] { "Condition" }, "");
                    }

                    if (!errors.hasFieldErrors("condition")) {
                        if (!alarmStates.contains(entry.getCondition())) {
                            String conditionI18nText = accessor.getMessage(pointBaseKey + "alarm.condition");
                            errors.rejectValue("condition", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                    new Object[] { conditionI18nText }, "");
                        }
                        if (!errors.hasFieldErrors("condition") && alarmStateEntries.contains(entry.getCondition())) {
                            errors.rejectValue("condition", ApiErrorDetails.ALREADY_EXISTS.getCodeString(),
                                    new Object[] { "Condition" }, "");
                        }

                        if (entry.getCategory() != null) {
                            Optional<LiteAlarmCategory> catagory = alarmCatDao.getAlarmCategories()
                                                                              .stream()
                                                                              .filter(e -> e.getCategoryName().equals(entry.getCategory()))
                                                                              .findFirst();
                            if (catagory.isEmpty()) {
                                String categoryI18nText = accessor.getMessage(pointBaseKey + "alarm.category");
                                errors.rejectValue("category", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                        new Object[] { categoryI18nText }, "");
                            }
                        }
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

    protected void validateArchiveSettings(T target, PointType pointType, Errors errors) {

        if (target.getArchiveType() != null && (target.getArchiveType() == PointArchiveType.NONE || target.getArchiveType() == PointArchiveType.ON_CHANGE || target.getArchiveType() == PointArchiveType.ON_UPDATE)) {
            if (target.getArchiveInterval() != null && target.getArchiveInterval() != 0) {
                errors.rejectValue("archiveInterval", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Archive interval" }, "");
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
                errors.rejectValue("stateGroupId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                        new Object[] { "StateGroupId()" }, "");
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
                YukonApiValidationUtils.checkRange(errors, "staleData.time", staleData.getTime(), 0, 99999999, false);
            }
            if (staleData.getUpdateStyle() != null) {
                if (!(staleData.getUpdateStyle() == 1 || staleData.getUpdateStyle() == 0)) {
                    errors.rejectValue( "staleData.updateStyle", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Update Style" }, "");
                }
            }
        }
    }

    /**
     * Validate FdrTranslation Fields.
     */

    private void validateFdrTranslation(List<FdrTranslation> fdrList, Errors errors) {
        String translationI18nText = accessor.getMessage(pointBaseKey + "fdr.translation");
        if (CollectionUtils.isNotEmpty(fdrList)) {
            Set<FdrTranslation> usedTypes = new HashSet<>();
            for (int i = 0; i < fdrList.size(); i++) {
                errors.pushNestedPath("fdrList[" + i + "]");
                FdrTranslation fdrTranslation = fdrList.get(i);

                if (fdrTranslation == null || fdrTranslation.getFdrInterfaceType() == null) {
                    errors.rejectValue("fdrInterfaceType", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                            new Object[] { "Interface" }, "");
                }

                if (!errors.hasFieldErrors("fdrInterfaceType")) {
                    FdrInterfaceType fdrInterfaceType = fdrTranslation.getFdrInterfaceType();
                    FdrDirection fdrDirection = fdrTranslation.getDirection();
                    List<FdrDirection> supportedDirections = fdrInterfaceType.getSupportedDirectionsList();
                    if (fdrDirection == null || !supportedDirections.contains(fdrDirection)) {
                        String directionI18nText = accessor.getMessage(pointBaseKey + "fdr.direction");
                        errors.rejectValue("direction", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { directionI18nText }, "");
                    }

                    Map<FdrInterfaceOption, String> parameterMap = new HashMap<>();
                    List<FdrInterfaceOption> supportedOptions = fdrInterfaceType.getInterfaceOptionsList();
                    String translation = fdrTranslation.getTranslation();

                    if (translation != null) {
                        String[] parameters = translation.split(";");


                        if (parameterMap.size() > maxFdrInterfaceTranslations) {
                            errors.reject(ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "FDR translation" }, "");
                        }
                        
                        if (usedTypes.contains(fdrTranslation)) {
                            errors.rejectValue("interfaceType", ApiErrorDetails.ALREADY_EXISTS.getCodeString(), new Object[] { "FDR translation entry" }, "");
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
                                        errors.rejectValue("translation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                                new Object[] { translationI18nText }, "");
                                    }

                                    FdrOptionType optionType = fdrInterfaceOption.getOptionType();

                                    if (!(optionType == FdrOptionType.TEXT && fieldValue.equals(CtiUtilities.STRING_NONE))
                                            && !(fdrInterfaceOption.isValid(fieldValue))) {
                                        errors.rejectValue("translation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                                new Object[] { translationI18nText }, "");
                                    }
                                } else {
                                    errors.rejectValue("translation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                                       new Object[] { translationI18nText }, "");
                                }

                            }
                        }
                    }
                    String missedFdrInterfaceOptions = supportedOptions.stream()
                                                                       .filter(option -> !(parameterMap.keySet().contains(option)))
                                                                       .map(option -> option.getOptionLabel())
                                                                       .collect(Collectors.joining(", "));

                    if (StringUtils.isNotBlank(missedFdrInterfaceOptions)) {
                        errors.rejectValue("translation", ApiErrorDetails.INVALID_VALUE.getCodeString(),
                                           new Object[] { translationI18nText }, "");
                    }

                }
                errors.popNestedPath();
            }

        }

    }

}