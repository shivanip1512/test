package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.ControlAreaProjectionType;
import com.cannontech.common.dr.setup.ControlAreaTrigger;
import com.cannontech.common.dr.setup.ControlAreaTriggerType;
import com.cannontech.common.dr.setup.DailyDefaultState;
import com.cannontech.common.util.TimeIntervals;
import com.cannontech.web.PageEditMode;

@Service
public class ControlAreaSetupControllerHelper {

    /**
     * Set model attributes for control areas
     */
    public void buildModelMap(ModelMap model, ControlArea controlArea) {
        model.addAttribute("controlIntervals", TimeIntervals.getControlAreaInterval());
        model.addAttribute("defaultStates", DailyDefaultState.values());
        List<Integer> assignedProgramIds = new ArrayList<>();
        if (controlArea.getProgramAssignment() != null) {
            controlArea.getProgramAssignment().forEach(p -> assignedProgramIds.add(p.getProgramId()));
        }
        model.addAttribute("assignedProgramIds", assignedProgramIds);

        Integer startTimeMinutes = controlArea.getDailyStartTimeInMinutes();
        if (startTimeMinutes != null) {
            LocalTime dailyStart = LocalTime.fromMillisOfDay(startTimeMinutes * 60000);
            model.addAttribute("dailyStartTime", dailyStart);
        }
        Integer stopTimeMinutes = controlArea.getDailyStopTimeInMinutes();
        if (stopTimeMinutes != null) {
            LocalTime dailyStop = LocalTime.fromMillisOfDay(stopTimeMinutes * 60000);
            model.addAttribute("dailyStopTime", dailyStop);
        }
        
        if (model.containsAttribute("mode")) {
            PageEditMode mode = (PageEditMode) model.get("mode");
            model.addAttribute("isViewMode", mode == PageEditMode.VIEW);
        }
    }

    public void buildTriggerModelMap(ModelMap model, ControlAreaTrigger controlAreaTrigger) {
        model.addAttribute("triggerTypes", ControlAreaTriggerType.values());
        model.addAttribute("projectionTypes", ControlAreaProjectionType.values());
        model.addAttribute("projectAheadDurations", TimeIntervals.getProjectionAheadDuration());
        if (controlAreaTrigger.getTriggerType() != null) {
            if (controlAreaTrigger.getTriggerType() == ControlAreaTriggerType.THRESHOLD_POINT) {
                model.addAttribute("thresholdPointTriggerId", controlAreaTrigger.getTriggerPointId());
                model.addAttribute("thresholdPointPeakPointId", controlAreaTrigger.getPeakPointId());
                model.addAttribute("thresholdPointThresholdId", controlAreaTrigger.getThresholdPointId());
            } else if (controlAreaTrigger.getTriggerType() == ControlAreaTriggerType.THRESHOLD) {
                model.addAttribute("thresholdTriggerId", controlAreaTrigger.getTriggerPointId());
                model.addAttribute("thresholdPeakPointId", controlAreaTrigger.getPeakPointId());
            } else {
                model.addAttribute("statusTriggerId", controlAreaTrigger.getTriggerPointId());
            }
        }
        model.addAttribute("controlAreaTrigger", controlAreaTrigger);
    }
}
