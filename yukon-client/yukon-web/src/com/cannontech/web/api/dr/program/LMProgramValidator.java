package com.cannontech.web.api.dr.program;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindow;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.api.dr.gear.setup.fields.validator.ProgramGearFieldsValidator;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class LMProgramValidator extends SimpleValidator<LoadProgram> {
    
    private final static String key = "yukon.web.modules.dr.setup.loadProgram.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private IDatabaseCache cache;
    @Autowired LoadGroupDao loadGroupDao;
    @Autowired LoadProgramSetupService programSetupService;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    private MessageSourceAccessor accessor;

    private Map <GearControlMethod, ProgramGearFieldsValidator<? extends ProgramGearFields>> gearFieldsValidatorMap = new HashMap<>();

    public LMProgramValidator() {
        super(LoadProgram.class);
    }
    
    @PostConstruct
    public void init() {
        accessor = messageResolver.getMessageSourceAccessor(YukonUserContext.system);
    }

    @Override
    protected void doValidation(LoadProgram loadProgram, Errors errors) {
        YukonApiValidationUtils.checkIfFieldRequired("type", errors, loadProgram.getType(), "Type");

        if (!errors.hasFieldErrors("type")) {
            YukonApiValidationUtils.validateNewPaoName(loadProgram.getName(), loadProgram.getType(), errors, "Name");
            if (!loadProgram.getType().isDirectProgram()) {
                errors.reject(ApiErrorDetails.TYPE_MISMATCH.getCodeString(), new Object[] { loadProgram.getType() }, "");
            }
        }
        YukonApiValidationUtils.checkIfFieldRequired("operationalState", errors, loadProgram.getOperationalState(), "Operational State");
        if (!errors.hasFieldErrors("operationalState")) {
            if (loadProgram.getType() == PaoType.LM_NEST_PROGRAM && loadProgram.getOperationalState() != OperationalState.ManualOnly) {
                errors.reject(ApiErrorDetails.TYPE_MISMATCH.getCodeString(), new Object[] { loadProgram.getOperationalState() }, "");
            }
        }

        YukonApiValidationUtils.checkIfFieldRequired("constraint", errors, loadProgram.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgram.getConstraint().getConstraintId();
            errors.pushNestedPath("constraint");
            YukonApiValidationUtils.checkIfFieldRequired("constraintId", errors, constraintId, "Constraint");
            if (!errors.hasFieldErrors("constraintId")) {
                Set<Integer> constraintIds = cache.getAllLMProgramConstraints().stream()
                                                                               .map(lmConstraint -> lmConstraint.getConstraintID())
                                                                               .collect(Collectors.toSet());
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraintId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                            new Object[] { constraintId }, "");
                }
            }
            errors.popNestedPath();
        }

        YukonApiValidationUtils.checkRange(errors, "triggerOffset", loadProgram.getTriggerOffset(), 0.0, 99999.9999, false);
        YukonApiValidationUtils.checkRange(errors, "restoreOffset", loadProgram.getRestoreOffset(), -9999.9999, 99999.9999, false);

        if (!errors.hasFieldErrors("type")) {
            Integer programId = null;
            if (ServletUtils.getPathVariable("id") != null) {
                programId = Integer.valueOf(ServletUtils.getPathVariable("id"));
            }
            List<Integer> currentAssignedGroups = new ArrayList<>();
            try {
                if (programId != null) {
                    LMProgramDirectGroup[] groups = LMProgramDirectGroup.getAllDirectGroups(programId);
                    currentAssignedGroups = Arrays.stream(groups).map(p -> p.getLmGroupDeviceID()).collect(Collectors.toList());
                }
                List<Integer> assignedGroups = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(loadProgram.getAssignedGroups())) {
                    assignedGroups = loadProgram.getAssignedGroups().stream().map(p -> p.getGroupId()).collect(Collectors.toList());
                }

                List<Integer> assignedGroupsDiff = (List<Integer>) CollectionUtils.disjunction(currentAssignedGroups, assignedGroups);

                for (Integer groupId : assignedGroupsDiff) {
                    if (groupId != null && loadGroupDao.isLoadGroupInUse(groupId)) {
                        String loadGroupI18nText = accessor.getMessage(key + "groupEnrollmentConflict",
                                String.valueOf(cache.getAllPaosMap().get(groupId).getPaoName()));
                        errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                                new Object[] { "load group", loadGroupI18nText }, "");
                    }
                }

            } catch (SQLException e) {}

            if (CollectionUtils.isEmpty(loadProgram.getAssignedGroups())) {
                errors.reject(ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Load Group" }, "");
            } else {

                for (int i = 0; i < loadProgram.getAssignedGroups().size(); i++) {
                    errors.pushNestedPath("assignedGroups[" + i + "]");
                    ProgramGroup group = loadProgram.getAssignedGroups().get(i);

                    YukonApiValidationUtils.checkIfFieldRequired("groupId", errors, group.getGroupId(), "Group Id");

                    if (!errors.hasFieldErrors("groupId")) {
                        Optional<ProgramGroup> programGroup = getProgramGroup(group, loadProgram.getType());
                        if (programGroup.isEmpty()) {
                            errors.rejectValue("groupId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                    new Object[] {loadProgram.getAssignedGroups().get(i).getGroupId()}, "");
                        } else {



                            if (PaoType.LM_GROUP_NEST == programGroup.get().getType() && i > 0) {
                                errors.reject(ApiErrorDetails.ONLY_ONE_ALLOWED.getCodeString(), new Object[] { "One Load Group", "Nest Program" }, "");
                            }
                            if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
                                Boolean isLatchGear = loadProgram.getGears().stream()
                                                                            .allMatch(gear -> gear.getControlMethod() == GearControlMethod.Latching);
                                if (PaoType.LM_GROUP_POINT == programGroup.get().getType() && !isLatchGear) {
                                    String groupPointI18nText = accessor.getMessage(key + "notAllowedGroupPoint");
                                    errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                                            new Object[] { "LMGroup Point groups", groupPointI18nText }, "");
                                }
                            }
                        }
                    }
                    if (i > 0) {
                        YukonApiValidationUtils.checkIfFieldRequired("groupOrder", errors, group.getGroupOrder(),
                            "Group Order");
                    }
                    errors.popNestedPath();
                }
            }
        }
        if (!errors.hasFieldErrors("type")) {
            if (CollectionUtils.isEmpty(loadProgram.getGears())) {
                errors.reject(ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Gears" }, "");
            } else {

                if (loadProgram.getGears().size() >= IlmDefines.MAX_GEAR_COUNT) {
                    String maxGearCountI18nText = accessor.getMessage(key + "maxGearCount",
                            String.valueOf(loadProgram.getGears().size()));
                    errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                            new Object[] { "Gear Count", maxGearCountI18nText }, "");
                }

                Long latchCount = loadProgram.getGears().stream()
                                                        .filter(gear -> gear.getControlMethod() == GearControlMethod.Latching)
                                                        .count();
                if (latchCount > 1) {
                    errors.reject(ApiErrorDetails.ONLY_ONE_ALLOWED.getCodeString(), new Object[] { 1, "Latching gear" }, "");
                }

                if (latchCount == 1 && loadProgram.getGears().size() > 1) {
                    String latchGearsI18nText = accessor.getMessage(key + "latchNotAllowedWithOtherGears");
                    errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                            new Object[] { "Latch Gears", latchGearsI18nText }, "");
                }

                for (int i = 0; i < loadProgram.getGears().size(); i++) {

                    ProgramGear gear = loadProgram.getGears().get(i);
                    YukonApiValidationUtils.checkIfFieldRequired("gears[" + i + "].controlMethod", errors,
                        gear.getControlMethod(), "Control Method");
                    if (!errors.hasFieldErrors("gears[" + i + "].controlMethod")) {
                        ImmutableSet<PaoType> supportedProgramTypesForGearType =
                            gear.getControlMethod().getProgramTypes();

                        if (supportedProgramTypesForGearType.contains(loadProgram.getType())) {
                            errors.pushNestedPath("gears[" + i + "]");
                            if (gear.getGearName() == null || !StringUtils.hasText(gear.getGearName().toString())) {
                                errors.rejectValue("gearName", ApiErrorDetails.FIELD_REQUIRED.getCodeString(),
                                        new Object[] { "Gear Name" }, "");
                            }

                            if (!errors.hasFieldErrors("gearName")) {
                                YukonApiValidationUtils.checkExceedsMaxLength(errors, "gearName", gear.getGearName(), 30);
                                if (!PaoUtils.isValidPaoName(gear.getGearName())
                                    && !errors.hasFieldErrors("gearName")) {
                                    errors.rejectValue("gearName", ApiErrorDetails.ILLEGAL_CHARACTERS.getCodeString(), new Object[] { "gearName" }, "");
                                }
                            }

                            YukonApiValidationUtils.checkIfFieldRequired("gearNumber", errors, gear.getGearNumber(),
                                "Gear Number");
                            errors.popNestedPath();

                            // Validate Gear Fields
                            ProgramGearFields fields = gear.getFields();
                            if (fields != null) {
                                errors.pushNestedPath("gears[" + i + "].fields");
                                gearFieldsValidatorMap.get(gear.getControlMethod()).validate(fields, errors);
                                errors.popNestedPath();
                            } else {
                                if (gear.getControlMethod() == GearControlMethod.MeterDisconnect
                                    || gear.getControlMethod() == GearControlMethod.NestCriticalCycle) {
                                    // Do not display error as these gears types do not have any validation
                                } else {
                                    errors.reject(ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Gear fields" },
                                            "");
                                }
                            }
                        } else {
                            String controlMethodI18nText = accessor.getMessage(key + "notSupportedControlMethod",
                                    String.valueOf(gear.getControlMethod().name()), String.valueOf(loadProgram.getType()));
                            errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                                    new Object[] { "Control Method", controlMethodI18nText }, "");
                        }
                    }
                }

                if (!errors.hasFieldErrors()) {
                    Set<Integer> duplicatedGearNumbers =
                        loadProgram.getGears().stream()
                                              .filter(gear -> loadProgram.getGears().stream()
                                                                                    .filter(x -> x.getGearNumber() == gear.getGearNumber())
                                                                                    .count() > 1)
                                              .map(gear -> gear.getGearNumber())
                                              .collect(Collectors.toSet());

                    if (!duplicatedGearNumbers.isEmpty()) {
                        errors.reject(ApiErrorDetails.DUPLICATE_VALUE.getCodeString(),
                                new Object[] { "Gear Number", "Gear Number ID", duplicatedGearNumbers }, "");
                    }
                }
            }

        }

        if (loadProgram.getOperationalState() == OperationalState.Timed && ((loadProgram.getControlWindow() == null
                || loadProgram.getControlWindow().getControlWindowOne() == null
                || loadProgram.getControlWindow().getControlWindowOne().getAvailableStartTimeInMinutes() == null
                || loadProgram.getControlWindow().getControlWindowOne().getAvailableStopTimeInMinutes() == null)
                || (loadProgram.getControlWindow().getControlWindowOne().getAvailableStopTimeInMinutes() % 1440 == 0 &&
                   loadProgram.getControlWindow().getControlWindowOne().getAvailableStartTimeInMinutes()  == 0))) {

            String timedSupportedControlWindowI18nText = accessor.getMessage(key + "timedSupportedControlWindow");
            errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(),
                    new Object[] { "Timed Supported Control Window", timedSupportedControlWindowI18nText }, "");
        }

        if (loadProgram.getControlWindow() != null) {
            ProgramControlWindow window = loadProgram.getControlWindow();
            if (window.getControlWindowOne() != null) {
                Integer availableStartTimeInMinutes = window.getControlWindowOne().getAvailableStartTimeInMinutes();
                Integer availableStopTimeInMinutes = window.getControlWindowOne().getAvailableStopTimeInMinutes();
                validateControlWindowTime("controlWindow.controlWindowOne", errors, availableStartTimeInMinutes, availableStopTimeInMinutes);
            }

            if (window.getControlWindowTwo() != null) {
                Integer availableStartTimeInMinutes = window.getControlWindowTwo().getAvailableStartTimeInMinutes();
                Integer availableStopTimeInMinutes = window.getControlWindowTwo().getAvailableStopTimeInMinutes();
                validateControlWindowTime("controlWindow.controlWindowTwo", errors, availableStartTimeInMinutes, availableStopTimeInMinutes);
            }
        }

        if (loadProgram.getNotification() != null) {
            List<NotificationGroup> notificationGroups = loadProgram.getNotification().getAssignedNotificationGroups();
            if ((loadProgram.getNotification().getProgramStartInMinutes() != null
                    || loadProgram.getNotification().getProgramStopInMinutes() != null
                    || loadProgram.getNotification().getNotifyOnAdjust() || loadProgram.getNotification().getEnableOnSchedule())
                    && CollectionUtils.isEmpty(notificationGroups)) {
                errors.reject(ApiErrorDetails.FIELD_REQUIRED.getCodeString(), new Object[] { "Notification Group" }, "");
            }
            if (CollectionUtils.isNotEmpty(notificationGroups)) {
                for (int i = 0; i < notificationGroups.size(); i++) {
                    errors.pushNestedPath("notification.assignedNotificationGroups[" + i + "]");
                    NotificationGroup notificationGroup = notificationGroups.get(i);
                   
                    YukonApiValidationUtils.checkIfFieldRequired("notificationGrpID", errors,
                        notificationGroup.getNotificationGrpID(), "Notification GroupId");
                    List<Integer> notifIds = cache.getAllContactNotificationGroups().stream()
                                                                                    .map(group -> group.getNotificationGroupID())
                                                                                    .collect(Collectors.toList());
                    if (!errors.hasFieldErrors("notificationGrpID")) {
                        if (!notifIds.contains(notificationGroup.getNotificationGrpID())) {
                            errors.rejectValue("notificationGrpID", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                    new Object[] {notificationGroup.getNotificationGrpID()}, "");
                        }
                    }
                    errors.popNestedPath(); 
                }
            }
        }

        LiteYukonUser user = ApiRequestContext.getContext().getLiteYukonUser();
        if (rolePropertyDao.checkProperty(YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, user)
            || user.getUserID() == UserUtils.USER_ADMIN_ID) {
            if (CollectionUtils.isNotEmpty(loadProgram.getMemberControl())) {
                for (int i = 0; i < loadProgram.getMemberControl().size(); i++) {
                    errors.pushNestedPath("memberControl[" + i + "]");
                    ProgramDirectMemberControl memberControl = loadProgram.getMemberControl().get(i);
                    YukonApiValidationUtils.checkIfFieldRequired("subordinateProgId", errors,
                        memberControl.getSubordinateProgId(), "Subordinate ProgId");

                    if (!errors.hasFieldErrors("subordinateProgId")) {
                        if (!cache.getAllPaosMap().containsKey(memberControl.getSubordinateProgId())) {
                            errors.rejectValue("subordinateProgId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString(),
                                    new Object[] { memberControl.getSubordinateProgId() }, "");
                        }
                    }
                    errors.popNestedPath();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(loadProgram.getAssignedGroups())) {
            Set<Integer> duplicateLoadGroupsIds = getDuplicateLoadGroupsIds(loadProgram.getAssignedGroups());
            if (CollectionUtils.isNotEmpty(duplicateLoadGroupsIds)) {
                errors.reject(ApiErrorDetails.DUPLICATE_VALUE.getCodeString(),
                        new Object[] { "Load Group", "Load Group ID", duplicateLoadGroupsIds }, "");
            }
        }
    }
    
    private void validateControlWindowTime(String nestedPath, Errors errors, Integer availableStartTimeInMinutes,
            Integer availableStopTimeInMinutes) {
        errors.pushNestedPath(nestedPath);
        if (availableStartTimeInMinutes != null && availableStopTimeInMinutes != null) {
            YukonApiValidationUtils.checkRange(errors, "availableStartTimeInMinutes", availableStartTimeInMinutes, 0, 1439, false);
            YukonApiValidationUtils.checkRange(errors, "availableStopTimeInMinutes", availableStopTimeInMinutes, 0, 1440, false);
        } else if (availableStartTimeInMinutes == null && availableStopTimeInMinutes != null) {
            YukonApiValidationUtils.checkIfFieldRequired("availableStartTimeInMinutes", errors, availableStartTimeInMinutes,
                "Start Time");
        } else if (availableStopTimeInMinutes == null && availableStartTimeInMinutes != null) {
            YukonApiValidationUtils.checkIfFieldRequired("availableStopTimeInMinutes", errors, availableStopTimeInMinutes,
                "Stop Time");
        }
        errors.popNestedPath();
    }

    private Optional<ProgramGroup> getProgramGroup(ProgramGroup group, PaoType programType) {
        Optional<ProgramGroup> lmGroup = programSetupService.getAllProgramLoadGroups(programType).stream()
                                                                                                 .filter(programGroup -> programGroup.getGroupId().intValue() == group.getGroupId())
                                                                                                 .findFirst();
        return lmGroup;
    }

    @Autowired
    public void setProgramGearFieldsValidators(List<ProgramGearFieldsValidator<? extends ProgramGearFields>> gearFieldsValidatorsList) {

        gearFieldsValidatorsList.stream()
                                .forEach(gearFields -> gearFieldsValidatorMap.put(gearFields.getControlMethod(), gearFields));

    }

    /**
     * Returns set of duplicate load group ids.
     */
    private Set<Integer> getDuplicateLoadGroupsIds(List<ProgramGroup> assignedLoadGroups) {
        List<Integer> groupIds = assignedLoadGroups.stream()
                                                   .map(ProgramGroup::getGroupId)
                                                   .collect(Collectors.toList());
        return lmValidatorHelper.findDuplicates(groupIds);

    }

}
