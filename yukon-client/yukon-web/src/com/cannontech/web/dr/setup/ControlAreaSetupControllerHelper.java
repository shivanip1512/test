package com.cannontech.web.dr.setup;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import com.cannontech.common.dr.setup.ControlArea;
import com.cannontech.common.dr.setup.DailyDefaultState;
import com.cannontech.common.util.TimeIntervals;

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
    }

}
