package com.cannontech.web.api.dr.controlArea;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProgramAssignment;
import com.cannontech.common.dr.setup.ControlAreaProjectionType;
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.common.validator.SimpleValidator;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.dr.controlarea.dao.ControlAreaDao;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.web.api.dr.setup.LMValidatorHelper;

public class ControlAreaSetupValidator extends SimpleValidator<ControlArea> {

    private final static String key = "yukon.web.modules.dr.setup.controlArea.error.";
    public static final int MAX_TRIGGER_COUNT = 2;
    @Autowired private LMValidatorHelper lmValidatorHelper;
    @Autowired private PointDao pointdao;
    @Autowired private StateGroupDao stateGroupDao;
    @Autowired private ControlAreaDao controlAreaDao;

    public ControlAreaSetupValidator() {
        super(ControlArea.class);
    }

    @Override
    protected void doValidation(ControlArea controlArea, Errors errors) {

        lmValidatorHelper.validateNewPaoName(controlArea.getName(), PaoType.LM_CONTROL_AREA, errors, "Control Area Name");
        lmValidatorHelper.checkIfFieldRequired("allTriggersActiveFlag", errors, controlArea.getAllTriggersActiveFlag(), "All Triggers Active Flag");
        lmValidatorHelper.checkIfFieldRequired("controlInterval", errors, controlArea.getControlInterval(), "Control Interval");
        if (!errors.hasFieldErrors("controlInterval")) {
            TimeIntervals controlInterval = TimeIntervals.fromSeconds(controlArea.getControlInterval());
            if (!TimeIntervals.getControlAreaInterval().contains(controlInterval)) {
                errors.rejectValue("controlInterval", key + "invalid.intervalValue", new Object[] { "Control Interval" }, "");
            }
        }
        lmValidatorHelper.checkIfFieldRequired("minResponseTime", errors, controlArea.getMinResponseTime(), "Min Response Time");
        if (!errors.hasFieldErrors("minResponseTime")) {
            TimeIntervals minResponse = TimeIntervals.fromSeconds(controlArea.getMinResponseTime());
            if (!TimeIntervals.getControlAreaInterval().contains(minResponse)) {
                errors.rejectValue("minResponseTime", key + "invalid.intervalValue", new Object[] { "Min Response Time" }, "");
            }
        }
        lmValidatorHelper.checkIfFieldRequired("dailyDefaultState", errors, controlArea.getDailyDefaultState(), "Daily Default State");

        YukonValidationUtils.checkRange(errors, "dailyStartTimeInMinutes", controlArea.getDailyStartTimeInMinutes(), 0, 1439, false);
        YukonValidationUtils.checkRange(errors, "dailyStopTimeInMinutes", controlArea.getDailyStopTimeInMinutes(), 0, 1439, false);

        if (!errors.hasFieldErrors("dailyStartTimeInMinutes") && !errors.hasFieldErrors("dailyStopTimeInMinutes")) {
            if (controlArea.getDailyStartTimeInMinutes() != null && controlArea.getDailyStopTimeInMinutes() != null) {
                if (controlArea.getDailyStartTimeInMinutes() > controlArea.getDailyStopTimeInMinutes()) {
                    errors.reject(key + "invalid.startStopTimeRange");
                }
            } else if ((controlArea.getDailyStartTimeInMinutes() == null
                && controlArea.getDailyStopTimeInMinutes() != null)
                || (controlArea.getDailyStartTimeInMinutes() != null
                    && controlArea.getDailyStopTimeInMinutes() == null)) {
                if (controlArea.getDailyStartTimeInMinutes() == null) {
                    lmValidatorHelper.checkIfFieldRequired("dailyStartTimeInMinutes", errors,
                        controlArea.getDailyStartTimeInMinutes(), "Daily Start Time");
                } else {
                    lmValidatorHelper.checkIfFieldRequired("dailyStopTimeInMinutes", errors,
                        controlArea.getDailyStopTimeInMinutes(), "Daily Stop Time");
                }

            }
        }

        if (CollectionUtils.isNotEmpty(controlArea.getTriggers())) {
            if (controlArea.getTriggers().size() > MAX_TRIGGER_COUNT) {
                errors.reject(key + "maxTwoTriggers");
            } else {
                for (int i = 0; i < controlArea.getTriggers().size(); i++) {
                    errors.pushNestedPath("triggers[" + i + "]");
                    ControlAreaTrigger trigger = controlArea.getTriggers().get(i);
                    lmValidatorHelper.checkIfFieldRequired("triggerType", errors, trigger.getTriggerType(), "Trigger Type");
                    lmValidatorHelper.checkIfFieldRequired("triggerPointId", errors, trigger.getTriggerPointId(), "Trigger Point Id");
                    if (!errors.hasFieldErrors("triggerPointId")) {
                        lmValidatorHelper.validatePointId(errors, "triggerPointId", trigger.getTriggerPointId());
                    }

                    if (!errors.hasFieldErrors("triggerType") && !errors.hasFieldErrors("triggerPointId")) {
                        
                        if (trigger.getTriggerType().getTriggerTypeValue().equalsIgnoreCase(IlmDefines.TYPE_STATUS)) {
                            
                            lmValidatorHelper.checkIfFieldRequired("normalState", errors, trigger.getNormalState(), "Normal State");
                            if (!errors.hasFieldErrors("normalState")) {

                                LitePoint litePoint = pointdao.getLitePoint(trigger.getTriggerPointId());
                                LiteStateGroup stateGroup = stateGroupDao.getStateGroup(litePoint.getStateGroupID());
                                Optional<LiteState> liteState = stateGroup.getStatesList().stream().filter(
                                    state -> state.getLiteID() == trigger.getNormalState()).findFirst();
                                if (liteState.isEmpty()) {
                                    errors.rejectValue("normalState", key + "invalid.normalState");
                                }
                            }
                        } else {
                            YukonValidationUtils.checkRange(errors, "minRestoreOffset", trigger.getMinRestoreOffset(), -99999.9999, 99999.9999, false);

                            if (trigger.getPeakPointId() != null) {
                                lmValidatorHelper.validatePointId(errors, "peakPointId", trigger.getPeakPointId());
                            }

                            if ((trigger.getTriggerType().getTriggerTypeValue()).equalsIgnoreCase( IlmDefines.TYPE_THRESHOLD_POINT)) {
                                lmValidatorHelper.checkIfFieldRequired("thresholdPointId", errors, trigger.getThresholdPointId(), "Threshold Point Id");
                                if (!errors.hasFieldErrors("thresholdPointId")) { 
                                    lmValidatorHelper.validatePointId(errors, "thresholdPointId", trigger.getThresholdPointId());
                                }
                            } else {
                                lmValidatorHelper.checkIfFieldRequired("threshold", errors, trigger.getThreshold(), "Threshold");
                                if (!errors.hasFieldErrors("threshold")) {
                                    YukonValidationUtils.checkRange(errors, "threshold", trigger.getThreshold(), -999999.99999999, 999999.99999999, false);
                                }

                                if (trigger.getAtku() != null) {
                                    YukonValidationUtils.checkRange(errors, "atku", trigger.getAtku(), -2147483648,
                                        2147483647, false);
                                }

                                lmValidatorHelper.checkIfFieldRequired("controlAreaProjection", errors, trigger.getControlAreaProjection(), "Control Area Projection");
                                if (!errors.hasFieldErrors("controlAreaProjection")) {
                                    lmValidatorHelper.checkIfFieldRequired("controlAreaProjection.projectionType", errors, trigger.getControlAreaProjection().getProjectionType(), "Projection Type");

                                    if(trigger.getControlAreaProjection().getProjectionType() != ControlAreaProjectionType.NONE ) {
                                    lmValidatorHelper.checkIfFieldRequired("controlAreaProjection.projectionPoint", errors, trigger.getControlAreaProjection().getProjectionPoint(), "Projection Point");
                                    if (!errors.hasFieldErrors("controlAreaProjection.projectionPoint")) {
                                        YukonValidationUtils.checkRange(errors, "controlAreaProjection.projectionPoint", trigger.getControlAreaProjection().getProjectionPoint(), 2, 12, false);
                                    }
                                    lmValidatorHelper.checkIfFieldRequired("controlAreaProjection.projectAheadDuration", errors, trigger.getControlAreaProjection().getProjectAheadDuration(), "Projection Ahead Duration");
                                    if (!errors.hasFieldErrors("controlAreaProjection.projectAheadDuration")) { 
                                        if(!TimeIntervals.getProjectionAheadDuration().contains(TimeIntervals.fromSeconds(trigger.getControlAreaProjection().getProjectAheadDuration()))) {
                                            errors.rejectValue("controlAreaProjection.projectAheadDuration", key + "invalid.intervalValue", new Object[] { "Project Ahead Duration" }, "");
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
                lmValidatorHelper.checkIfFieldRequired("programId", errors, programAssignment.getProgramId(), "Program Id");
                Integer areaId = null;
                if (!errors.hasFieldErrors("programId")) {
                    Set<Integer> unassignedPrograms = new LinkedHashSet<>(LMProgram.getUnassignedPrograms());
                    if (ServletUtils.getPathVariable("id") != null) {
                        areaId = Integer.valueOf(ServletUtils.getPathVariable("id"));
                        Set<Integer> assignedProgramIds = controlAreaDao.getProgramIdsForControlArea(areaId);
                        if (!assignedProgramIds.contains(programAssignment.getProgramId())) {
                            if (!unassignedPrograms.contains(programAssignment.getProgramId())) {
                                errors.rejectValue("programId", key + "programId.doesNotExist");
                            }
                        }
                    } else {
                        if (!unassignedPrograms.contains(programAssignment.getProgramId())) {
                            errors.rejectValue("programId", key + "programId.doesNotExist");
                        }
                    }
                }

                lmValidatorHelper.checkIfFieldRequired("startPriority", errors, programAssignment.getStartPriority(), "Start Priority");
                if (!errors.hasFieldErrors("startPriority")) {
                    YukonValidationUtils.checkRange(errors, "startPriority", programAssignment.getStartPriority(), 1, 2147483647, false);
                }
                lmValidatorHelper.checkIfFieldRequired("stopPriority", errors, programAssignment.getStopPriority(), "Stop Priority");
                if (!errors.hasFieldErrors("stopPriority")) {
                    YukonValidationUtils.checkRange(errors, "stopPriority", programAssignment.getStopPriority(), 1,2147483647, false);
                }
                errors.popNestedPath();
            }
        }
    }
}
