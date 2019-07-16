package com.cannontech.web.api.dr.program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.gear.setup.fields.ProgramGearFields;
import com.cannontech.web.api.dr.gear.setup.fields.validator.ProgramGearFieldsValidator;
import com.cannontech.common.dr.gear.setup.model.ProgramGear;
import com.cannontech.common.dr.program.setup.model.LoadProgram;
import com.cannontech.common.dr.program.setup.model.NotificationGroup;
import com.cannontech.common.dr.program.setup.model.ProgramControlWindow;
import com.cannontech.common.dr.program.setup.model.ProgramDirectMemberControl;
import com.cannontech.common.dr.program.setup.model.ProgramGroup;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class LMProgramValidator extends SimpleValidator<LoadProgram> {

    private final static String key = "yukon.web.modules.dr.setup.loadProgram.error.";

    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private IDatabaseCache cache;
    @Autowired LoadGroupDao loadGroupDao;
    private Map <GearControlMethod, ProgramGearFieldsValidator<? extends ProgramGearFields>> gearFieldsValidatorMap = new HashMap<>();

    public LMProgramValidator() {
        super(LoadProgram.class);
    }

    @Override
    protected void doValidation(LoadProgram loadProgram, Errors errors) {
        lmValidatorHelper.validateNewPaoName(loadProgram.getName(), loadProgram.getType(), errors, "Program Name");
        //Type
        lmValidatorHelper.checkIfFieldRequired("type", errors, loadProgram.getType(), "Type");
        if (!errors.hasFieldErrors("type")) {
            if (!loadProgram.getType().isDirectProgram()) {
                errors.reject(key + "notSupportedProgramType", new Object[] { loadProgram.getType() }, "");
            }
        }
        lmValidatorHelper.checkIfFieldRequired("operationalState", errors, loadProgram.getOperationalState(), "Operational State");
        
        lmValidatorHelper.checkIfFieldRequired("constraint", errors, loadProgram.getConstraint(), "Program Constraint");

        if (!errors.hasFieldErrors("constraint")) {
            Integer constraintId = loadProgram.getConstraint().getConstraintId();
            errors.pushNestedPath("constraint");
            lmValidatorHelper.checkIfFieldRequired("constraintId", errors, constraintId, "Constraint Id");
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

        YukonValidationUtils.checkRange(errors, "restoreOffset", loadProgram.getRestoreOffset(), 0.0, 99999.9999,
            false);
        YukonValidationUtils.checkRange(errors, "triggerOffset", loadProgram.getTriggerOffset(), -9999.9999, 99999.9999,
            false);

        if (CollectionUtils.isEmpty(loadProgram.getAssignedGroups())) {
            errors.reject(key + "noGroup");
        } else {
            for (int i = 0; i < loadProgram.getAssignedGroups().size(); i++) {
                errors.pushNestedPath("assignedGroups[" + i + "]");
                ProgramGroup group = loadProgram.getAssignedGroups().get(i);
                
                lmValidatorHelper.checkIfFieldRequired("groupId", errors, group.getGroupId(), "Group Id");

                if (!errors.hasFieldErrors("groupId")) {
                    if (!cache.getAllPaosMap().containsKey(group.getGroupId())) {
                        errors.rejectValue("groupId", key + "groupId.doesNotExist");
                    }
                }
                
                if (!errors.hasFieldErrors("groupId")) {
                    LiteYukonPAObject pao = getGroupFromCache(group);
                    if (loadGroupDao.isLoadGroupInUse(group.getGroupId())) {
                        errors.reject(key + "groupEnrollmentConflict", new Object[] { pao.getPaoName() }, "");
                    }
                }

                if (!errors.hasFieldErrors("groupId")) {
                    LiteYukonPAObject pao = getGroupFromCache(group);
                    if (PaoType.NEST == pao.getPaoType() && i > 0) {
                        errors.reject(key + "nestGroup", new Object[] { pao.getPaoName() }, "");
                    }
                }
                if (!errors.hasFieldErrors("groupId")) {
                    LiteYukonPAObject pao = getGroupFromCache(group);
                    Boolean isLatchGear = loadProgram.getGears().stream()
                                                                .allMatch(gear -> gear.getControlMethod() == GearControlMethod.Latching);
                    if (PaoType.LM_GROUP_POINT == pao.getPaoType() && !isLatchGear) {
                        errors.reject(key + "notAllowedGroupPoint");
                    }
                }
                if (i > 0) {
                    lmValidatorHelper.checkIfFieldRequired("groupOrder", errors,
                        group.getGroupOrder(), "Group Order");
                }
                
                errors.popNestedPath();
            }
        }

        if (CollectionUtils.isEmpty(loadProgram.getGears())) {
            errors.reject(key + "noGear");
        } else {

            if (loadProgram.getGears().size() >= IlmDefines.MAX_GEAR_COUNT) {
                errors.reject(key + "maxGearCount");
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
                ImmutableSet<PaoType> supportedProgramTypesForGearType = gear.getControlMethod().getProgramTypes();

                if (supportedProgramTypesForGearType.contains(loadProgram.getType())) {
                    errors.pushNestedPath("gears[" + i + "]");
                    if (gear.getGearName() == null || !StringUtils.hasText(gear.getGearName().toString())) {
                        errors.rejectValue("gearName", "yukon.web.modules.dr.setup.error.required", new Object[] { "Gear Name" }, "");
                    }

                    lmValidatorHelper.checkIfFieldRequired("gearNumber", errors, gear.getGearNumber(), "Gear Number");
                    errors.popNestedPath();

                    // Validate Gear Fields
                    ProgramGearFields fields = gear.getFields();
                    if (fields != null) {
                        errors.pushNestedPath("gears[" + i + "].fields");
                        gearFieldsValidatorMap.get(gear.getControlMethod()).validate(fields, errors);
                        errors.popNestedPath();
                    }
                } else {
                    errors.reject(key + "notSupportedControlMethod", new Object[] { gear.getControlMethod(), loadProgram.getType() }, "");
                }

            }
            
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
        
        if (CollectionUtils.isNotEmpty(loadProgram.getMemberControl())) {
            for (int i = 0; i < loadProgram.getMemberControl().size(); i++) {
                errors.pushNestedPath("memberControl[" + i + "]");
                ProgramDirectMemberControl memberControl = loadProgram.getMemberControl().get(i);
                lmValidatorHelper.checkIfFieldRequired("subordinateProgId", errors, memberControl.getSubordinateProgId(), "Subordinate ProgId");

                if (!errors.hasFieldErrors("subordinateProgId")) {
                    if (!cache.getAllPaosMap().containsKey(memberControl.getSubordinateProgId())) {
                        errors.rejectValue("subordinateProgId", key + "subordinateProgId.doesNotExist");
                    }
                }
                errors.popNestedPath();
            }
        }

    }
    
    private void validateControlWindowTime(String nestedPath, Errors errors, Integer availableStartTimeInMinutes,
            Integer availableStopTimeInMinutes) {
        if (availableStartTimeInMinutes != null && availableStopTimeInMinutes != null) {
            errors.pushNestedPath(nestedPath);
            YukonValidationUtils.checkRange(errors, "availableStartTimeInMinutes", availableStartTimeInMinutes, 0, 1439, false);
            YukonValidationUtils.checkRange(errors, "availableStopTimeInMinutes", availableStopTimeInMinutes, 0, 1439, false);
            errors.popNestedPath();
        }

    }

    private LiteYukonPAObject getGroupFromCache(ProgramGroup group) {
        return cache.getAllPaosMap().get(group.getGroupId());
    }

    @Autowired
    public void setProgramGearFieldsValidators(List<ProgramGearFieldsValidator<? extends ProgramGearFields>> gearFieldsValidatorsList) {

        gearFieldsValidatorsList.stream()
                                .forEach(gearFields -> gearFieldsValidatorMap.put(gearFields.getControlMethod(), gearFields));

    }

}
