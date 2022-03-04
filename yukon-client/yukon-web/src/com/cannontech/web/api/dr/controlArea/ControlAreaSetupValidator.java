package com.cannontech.web.api.dr.controlArea;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.api.error.model.ApiErrorDetails;
import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.ControlAreaProjectionType;
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonApiValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.dr.setup.LMApiValidatorHelper;
import com.cannontech.web.tools.points.validators.PointApiValidationUtil;

public class ControlAreaSetupValidator extends SimpleValidator<ControlArea> {

    public static final int MAX_TRIGGER_COUNT = 2;
    @Autowired private LMApiValidatorHelper lmApiValidatorHelper;
    @Autowired private PointDao pointdao;
    @Autowired private PointApiValidationUtil pointApiValidationUtil;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private ControlAreaDao controlAreaDao;

    public ControlAreaSetupValidator() {
        super(ControlArea.class);
    }

    @Override
    protected void doValidation(ControlArea controlArea, Errors errors) {

        YukonApiValidationUtils.validateNewPaoName(controlArea.getName(), PaoType.LM_CONTROL_AREA, errors, "Name");
        YukonApiValidationUtils.checkIfFieldRequired("allTriggersActiveFlag", errors, controlArea.getAllTriggersActiveFlag(), "All Triggers Active Flag");
        YukonApiValidationUtils.checkIfFieldRequired("controlInterval", errors, controlArea.getControlInterval(), "Control Interval");
        if (!errors.hasFieldErrors("controlInterval")) {
            TimeIntervals controlInterval = TimeIntervals.fromSeconds(controlArea.getControlInterval());
            if (!TimeIntervals.getControlAreaInterval().contains(controlInterval)) {
                errors.rejectValue("controlInterval", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Control Interval" }, "");
            }
        }
        YukonApiValidationUtils.checkIfFieldRequired("minResponseTime", errors, controlArea.getMinResponseTime(), "Min Response Time");
        if (!errors.hasFieldErrors("minResponseTime")) {
            TimeIntervals minResponse = TimeIntervals.fromSeconds(controlArea.getMinResponseTime());
            if (!TimeIntervals.getControlAreaInterval().contains(minResponse)) {
                errors.rejectValue("minResponseTime", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Min Response Time" }, "");
            }
        }
        YukonApiValidationUtils.checkIfFieldRequired("dailyDefaultState", errors, controlArea.getDailyDefaultState(), "Daily Default State");

        YukonApiValidationUtils.checkRange(errors, "dailyStartTimeInMinutes", controlArea.getDailyStartTimeInMinutes(), 0, 1439, false);
        YukonApiValidationUtils.checkRange(errors, "dailyStopTimeInMinutes", controlArea.getDailyStopTimeInMinutes(), 0, 1439, false);

        if (!errors.hasFieldErrors("dailyStartTimeInMinutes") && !errors.hasFieldErrors("dailyStopTimeInMinutes")) {
            if (controlArea.getDailyStartTimeInMinutes() != null && controlArea.getDailyStopTimeInMinutes() != null) {
                if (controlArea.getDailyStartTimeInMinutes() > controlArea.getDailyStopTimeInMinutes()) {
                    errors.reject(ApiErrorDetails.INVALID_VALUE.getCodeString());
                }
            } else if (controlArea.getDailyStartTimeInMinutes() == null
                && controlArea.getDailyStopTimeInMinutes() != null) {
                YukonApiValidationUtils.checkIfFieldRequired("dailyStartTimeInMinutes", errors,
                    controlArea.getDailyStartTimeInMinutes(), "Daily Start Time");
            } else if (controlArea.getDailyStopTimeInMinutes() == null
                && controlArea.getDailyStartTimeInMinutes() != null) {
                YukonApiValidationUtils.checkIfFieldRequired("dailyStopTimeInMinutes", errors,
                    controlArea.getDailyStopTimeInMinutes(), "Daily Stop Time");
            }
        }

        if (CollectionUtils.isNotEmpty(controlArea.getTriggers())) {
            if (controlArea.getTriggers().size() > MAX_TRIGGER_COUNT) {
                errors.reject(ApiErrorDetails.CONSTRAINT_VIOLATED.getCodeString(), new Object[] { "Trigger Count", "Maximum two valid triggers are allowed"}, "");
            } else {
                for (int i = 0; i < controlArea.getTriggers().size(); i++) {
                    errors.pushNestedPath("triggers[" + i + "]");
                    ControlAreaTrigger trigger = controlArea.getTriggers().get(i);
                    YukonApiValidationUtils.checkIfFieldRequired("triggerType", errors, trigger.getTriggerType(), "Trigger Type");
                    YukonApiValidationUtils.checkIfFieldRequired("triggerPointId", errors, trigger.getTriggerPointId(), "Trigger Point Id");
                    if (!errors.hasFieldErrors("triggerPointId")) {
                        pointApiValidationUtil.validatePointId(errors, "triggerPointId", trigger.getTriggerPointId(),
                                "triggerPointId");
                    }

                    if (!errors.hasFieldErrors("triggerType") && !errors.hasFieldErrors("triggerPointId")) {
                        
                        if (trigger.getTriggerType().getTriggerTypeValue().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
                            
                            YukonApiValidationUtils.checkIfFieldRequired("normalState", errors, trigger.getNormalState(), "Normal State");
                            if (!errors.hasFieldErrors("normalState")) {

                                LitePoint litePoint = pointdao.getLitePoint(trigger.getTriggerPointId());
                                LiteStateGroup stateGroup = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
                                Optional<LiteState> liteState = stateGroup.getStatesList().stream().filter(
                                    state -> state.getLiteID() == trigger.getNormalState()).findFirst();
                                if (liteState.isEmpty()) {
                                    errors.rejectValue("normalState", ApiErrorDetails.INVALID_VALUE.getCodeString());
                                }
                            }
                        } else {
                            YukonApiValidationUtils.checkRange(errors, "minRestoreOffset", trigger.getMinRestoreOffset(), -99999.9999, 99999.9999, false);

                            if (trigger.getPeakPointId() != null) {
                                pointApiValidationUtil.validatePointId(errors, "peakPointId", trigger.getPeakPointId(),
                                        "peakPointId");
                            }

                            if ((trigger.getTriggerType().getTriggerTypeValue()).equalsIgnoreCase( IlmDefines.TYPE_THRESHOLD_POINT)) {
                                YukonApiValidationUtils.checkIfFieldRequired("thresholdPointId", errors, trigger.getThresholdPointId(), "Threshold Point Id");
                                if (!errors.hasFieldErrors("thresholdPointId")) { 
                                    pointApiValidationUtil.validatePointId(errors, "thresholdPointId", trigger.getThresholdPointId(),
                                            "peakPointId");
                                }
                            } else {
                                YukonApiValidationUtils.checkIfFieldRequired("threshold", errors, trigger.getThreshold(), "Threshold");
                                if (!errors.hasFieldErrors("threshold")) {
                                    YukonApiValidationUtils.checkRange(errors, "threshold", trigger.getThreshold(), -999999.99999999, 999999.99999999, false);
                                }

                                if (trigger.getAtku() != null) {
                                    YukonApiValidationUtils.checkRange(errors, "atku", trigger.getAtku(), -2147483648,
                                        2147483647, false);
                                }

                                YukonApiValidationUtils.checkIfFieldRequired("controlAreaProjection", errors, trigger.getControlAreaProjection(), "Control Area Projection");
                                if (!errors.hasFieldErrors("controlAreaProjection")) {
                                    YukonApiValidationUtils.checkIfFieldRequired("controlAreaProjection.projectionType", errors, trigger.getControlAreaProjection().getProjectionType(), "Projection Type");

                                    if(trigger.getControlAreaProjection().getProjectionType() != ControlAreaProjectionType.NONE ) {
                                        YukonApiValidationUtils.checkIfFieldRequired("controlAreaProjection.projectionPoint", errors, trigger.getControlAreaProjection().getProjectionPoint(), "Projection Point");
                                    if (!errors.hasFieldErrors("controlAreaProjection.projectionPoint")) {
                                        YukonApiValidationUtils.checkRange(errors, "controlAreaProjection.projectionPoint", trigger.getControlAreaProjection().getProjectionPoint(), 2, 12, false);
                                    }
                                    YukonApiValidationUtils.checkIfFieldRequired("controlAreaProjection.projectAheadDuration", errors, trigger.getControlAreaProjection().getProjectAheadDuration(), "Projection Ahead Duration");
                                    if (!errors.hasFieldErrors("controlAreaProjection.projectAheadDuration")) { 
                                        if(!TimeIntervals.getProjectionAheadDuration().contains(TimeIntervals.fromSeconds(trigger.getControlAreaProjection().getProjectAheadDuration()))) {
                                            errors.rejectValue("controlAreaProjection.projectAheadDuration", ApiErrorDetails.INVALID_VALUE.getCodeString(), new Object[] { "Project Ahead Duration" }, "");
                                        }
                                    }
                                  }
                               }
                            }
                        }
                    }
                    errors.popNestedPath();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(controlArea.getProgramAssignment())) {
            for (int i = 0; i < controlArea.getProgramAssignment().size(); i++) {
                errors.pushNestedPath("programAssignment[" + i + "]");
                ControlAreaProgramAssignment programAssignment = controlArea.getProgramAssignment().get(i);
                YukonApiValidationUtils.checkIfFieldRequired("programId", errors, programAssignment.getProgramId(), "Program Id");
                Integer areaId = null;
                if (!errors.hasFieldErrors("programId")) {
                    Set<Integer> unassignedPrograms = new LinkedHashSet<>(LMProgram.getUnassignedPrograms());
                    if (ServletUtils.getPathVariable("id") != null) {
                        areaId = Integer.valueOf(ServletUtils.getPathVariable("id"));
                        Set<Integer> assignedProgramIds = controlAreaDao.getProgramIdsForControlArea(areaId);
                        if (!assignedProgramIds.contains(programAssignment.getProgramId())) {
                            if (!unassignedPrograms.contains(programAssignment.getProgramId())) {
                                errors.rejectValue("programId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString());
                            }
                        }
                    } else {
                        if (!unassignedPrograms.contains(programAssignment.getProgramId())) {
                            errors.rejectValue("programId", ApiErrorDetails.DOES_NOT_EXISTS.getCodeString());
                        }
                    }
                }

                YukonApiValidationUtils.checkIfFieldRequired("startPriority", errors, programAssignment.getStartPriority(), "Start Priority");
                if (!errors.hasFieldErrors("startPriority")) {
                    YukonApiValidationUtils.checkRange(errors, "startPriority", programAssignment.getStartPriority(), 1, 1024, false);
                }
                YukonApiValidationUtils.checkIfFieldRequired("stopPriority", errors, programAssignment.getStopPriority(), "Stop Priority");
                if (!errors.hasFieldErrors("stopPriority")) {
                    YukonApiValidationUtils.checkRange(errors, "stopPriority", programAssignment.getStopPriority(), 1,1024, false);
                }
                errors.popNestedPath();
            }
        }

        if (CollectionUtils.isNotEmpty(controlArea.getProgramAssignment())) {
            Set<Integer> duplicatesLoadProgramsIds = getDuplicateLoadProgramsIds(controlArea.getProgramAssignment());
            if (CollectionUtils.isNotEmpty(duplicatesLoadProgramsIds)) {
                errors.reject(ApiErrorDetails.DUPLICATE_VALUE.getCodeString(),
                    new Object[] { "Load Program", "Load Program ID", duplicatesLoadProgramsIds }, "");
            }
        }
    }

    /**
     * Returns set of duplicate load programs ids.
     */
    private Set<Integer> getDuplicateLoadProgramsIds(List<ControlAreaProgramAssignment> programAssignment) {
        List<Integer> programsIds = programAssignment.stream()
                                                     .map(ControlAreaProgramAssignment::getProgramId)
                                                     .collect(Collectors.toList());
        return lmApiValidatorHelper.findDuplicates(programsIds);

    }
}