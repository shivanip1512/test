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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.api.token.ApiRequestContext;
import com.cannontech.common.dr.gear.setup.OperationalState;
import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindow;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.dr.loadprogram.service.LoadProgramSetupService;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.UserUtils;
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

    private Map <GearControlMethod, ProgramGearFieldsValidator<? extends ProgramGearFields>> gearFieldsValidatorMap = new HashMap<>();

    public LMProgramValidator() {
        super(LoadProgram.class);
    }

    @Override
    protected void doValidation(LoadProgram loadProgram, Errors errors) {
        lmValidatorHelper.checkIfFieldRequired("type", errors, loadProgram.getType(), "Type");

        if (!errors.hasFieldErrors("type")) {
            lmValidatorHelper.validateNewPaoName(loadProgram.getName(), loadProgram.getType(), errors, "Name");
            if (!loadProgram.getType().isDirectProgram()) {
                errors.reject(key + "notSupportedProgramType", new Object[] { loadProgram.getType() }, "");
            }
        }
        lmValidatorHelper.checkIfFieldRequired("operationalState", errors, loadProgram.getOperationalState(), "Operational State");
        if (!errors.hasFieldErrors("operationalState")) {
            if (loadProgram.getType() == PaoType.LM_NEST_PROGRAM && loadProgram.getOperationalState() != OperationalState.ManualOnly) {
                errors.reject(key + "notSupportedOperationalState", new Object[] { PaoType.LM_NEST_PROGRAM, loadProgram.getOperationalState() }, "");
            }
        }

        lmValidatorHelper.checkIfFieldRequired("constraint", errors, loadProgram.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgram.getConstraint().getConstraintId();
            errors.pushNestedPath("constraint");
            lmValidatorHelper.checkIfFieldRequired("constraintId", errors, constraintId, "Constraint");
            if (!errors.hasFieldErrors("constraintId")) {
                Set<Integer> constraintIds = cache.getAllLMProgramConstraints().stream()
                                                                               .map(lmConstraint -> lmConstraint.getConstraintID())
                                                                               .collect(Collectors.toSet());
                if (!constraintIds.contains(constraintId)) {
                    errors.rejectValue("constraintId", key + "constraintId.doesNotExist");
                }
            }
            errors.popNestedPath();
        }

        YukonValidationUtils.checkRange(errors, "triggerOffset", loadProgram.getTriggerOffset(), 0.0, 99999.9999, false);
        YukonValidationUtils.checkRange(errors, "restoreOffset", loadProgram.getRestoreOffset(), -9999.9999, 99999.9999, false);

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
                    if (loadGroupDao.isLoadGroupInUse(groupId)) {
                        errors.reject(key + "groupEnrollmentConflict", new Object[] { cache.getAllPaosMap().get(groupId).getPaoName() }, "");
                    }
                }

            } catch (SQLException e) {}

            if (CollectionUtils.isEmpty(loadProgram.getAssignedGroups())) {
                errors.reject(key + "noGroup");
            } else {

                for (int i = 0; i < loadProgram.getAssignedGroups().size(); i++) {
                    errors.pushNestedPath("assignedGroups[" + i + "]");
                    ProgramGroup group = loadProgram.getAssignedGroups().get(i);

                    lmValidatorHelper.checkIfFieldRequired("groupId", errors, group.getGroupId(), "Group Id");

                    if (!errors.hasFieldErrors("groupId")) {
                        Optional<ProgramGroup> programGroup = getProgramGroup(group, loadProgram.getType());
                        if (programGroup.isEmpty()) {
                            errors.rejectValue("groupId", key + "groupId.doesNotExist");
                        } else {



                            if (PaoType.LM_GROUP_NEST == programGroup.get().getType() && i > 0) {
                                errors.reject(key + "nestGroup", new Object[] { programGroup.get().getGroupName() }, "");
                            }
                            if (CollectionUtils.isNotEmpty(loadProgram.getGears())) {
                                Boolean isLatchGear = loadProgram.getGears().stream()
                                                                            .allMatch(gear -> gear.getControlMethod() == GearControlMethod.Latching);
                                if (PaoType.LM_GROUP_POINT == programGroup.get().getType() && !isLatchGear) {
                                    errors.reject(key + "notAllowedGroupPoint");
                                }
                            }
                        }
                    }
                    if (i > 0) {
                        lmValidatorHelper.checkIfFieldRequired("groupOrder", errors, group.getGroupOrder(),
                            "Group Order");
                    }
                    errors.popNestedPath();
                }
            }
        }
        if (!errors.hasFieldErrors("type")) {
            if (CollectionUtils.isEmpty(loadProgram.getGears())) {
                errors.reject(key + "noGear");
            } else {

                if (loadProgram.getGears().size() >= IlmDefines.MAX_GEAR_COUNT) {
                    errors.reject(key + "maxGearCount", new Object[] { loadProgram.getGears().size()}, "");
                }

                Long latchCount = loadProgram.getGears().stream()
                                                        .filter(gear -> gear.getControlMethod() == GearControlMethod.Latching)
                                                        .count();
                if (latchCount > 1) {
                    errors.reject(key + "oneLatchAllowed");
                }

                if (latchCount == 1 && loadProgram.getGears().size() > 1) {
                    errors.reject(key + "latchNotAllowedWithOtherGears");
                }

                for (int i = 0; i < loadProgram.getGears().size(); i++) {

                    ProgramGear gear = loadProgram.getGears().get(i);
                    lmValidatorHelper.checkIfFieldRequired("gears[" + i + "].controlMethod", errors,
                        gear.getControlMethod(), "Control Method");
                    if (!errors.hasFieldErrors("gears[" + i + "].controlMethod")) {
                        ImmutableSet<PaoType> supportedProgramTypesForGearType =
                            gear.getControlMethod().getProgramTypes();

                        if (supportedProgramTypesForGearType.contains(loadProgram.getType())) {
                            errors.pushNestedPath("gears[" + i + "]");
                            if (gear.getGearName() == null || !StringUtils.hasText(gear.getGearName().toString())) {
                                errors.rejectValue("gearName", "yukon.web.modules.dr.setup.error.required",
                                    new Object[] { "Gear Name" }, "");
                            }

                            if (!errors.hasFieldErrors("gearName")) {
                                YukonValidationUtils.checkExceedsMaxLength(errors, "gearName", gear.getGearName(), 30);
                                if (!PaoUtils.isValidPaoName(gear.getGearName())
                                    && !errors.hasFieldErrors("gearName")) {
                                    errors.rejectValue("gearName", "yukon.web.error.paoName.containsIllegalChars");
                                }
                            }

                            lmValidatorHelper.checkIfFieldRequired("gearNumber", errors, gear.getGearNumber(),
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
                                    errors.reject("yukon.web.modules.dr.setup.error.required",
                                        new Object[] { "Gear fields" }, "");
                                }
                            }
                        } else {
                            errors.reject(key + "notSupportedControlMethod",
                                new Object[] { gear.getControlMethod().name(), loadProgram.getType() }, "");
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
                        errors.reject("yukon.web.modules.dr.setup.gear.error.uniqueGearNumber",
                            new Object[] { duplicatedGearNumbers }, "");
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

            errors.reject(key + "timedSupportedControlWindow");
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
            if (CollectionUtils.isNotEmpty(notificationGroups)) {
                for (int i = 0; i < notificationGroups.size(); i++) {
                    errors.pushNestedPath("notification.assignedNotificationGroups[" + i + "]");
                    NotificationGroup notificationGroup = notificationGroups.get(i);
                   
                    lmValidatorHelper.checkIfFieldRequired("notificationGrpID", errors,
                        notificationGroup.getNotificationGrpID(), "Notification GroupId");
                    List<Integer> notifIds = cache.getAllContactNotificationGroups().stream()
                                                                                    .map(group -> group.getNotificationGroupID())
                                                                                    .collect(Collectors.toList());
                    if (!errors.hasFieldErrors("notificationGrpID")) {
                        if (!notifIds.contains(notificationGroup.getNotificationGrpID())) {
                            errors.rejectValue("notificationGrpID", key + "notificationGrpID.doesNotExist");
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
                    lmValidatorHelper.checkIfFieldRequired("subordinateProgId", errors,
                        memberControl.getSubordinateProgId(), "Subordinate ProgId");

                    if (!errors.hasFieldErrors("subordinateProgId")) {
                        if (!cache.getAllPaosMap().containsKey(memberControl.getSubordinateProgId())) {
                            errors.rejectValue("subordinateProgId", key + "subordinateProgId.doesNotExist");
                        }
                    }
                    errors.popNestedPath();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(loadProgram.getAssignedGroups())) {
            Set<Integer> duplicateLoadGroupsIds = getDuplicateLoadGroupsIds(loadProgram.getAssignedGroups());
            if (CollectionUtils.isNotEmpty(duplicateLoadGroupsIds)) {
                errors.reject("yukon.web.modules.dr.setup.loadGroup.error.assignedLoadGroup.duplicate.notAllowed",
                    new Object[] { duplicateLoadGroupsIds }, "");
            }
        }
    }
    
    private void validateControlWindowTime(String nestedPath, Errors errors, Integer availableStartTimeInMinutes,
            Integer availableStopTimeInMinutes) {
        if (availableStartTimeInMinutes != null && availableStopTimeInMinutes != null) {
            errors.pushNestedPath(nestedPath);
            YukonValidationUtils.checkRange(errors, "availableStartTimeInMinutes", availableStartTimeInMinutes, 0, 1439, false);
            YukonValidationUtils.checkRange(errors, "availableStopTimeInMinutes", availableStopTimeInMinutes, 0, 1440, false);
            errors.popNestedPath();
        }

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
