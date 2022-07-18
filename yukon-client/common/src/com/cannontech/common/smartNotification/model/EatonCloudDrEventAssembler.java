package com.cannontech.common.smartNotification.model;

import java.util.Map;
import static java.util.Map.entry;

import org.joda.time.Instant;

import com.cannontech.dr.eatonCloud.job.service.EatonCloudJobControlType;


public class EatonCloudDrEventAssembler extends SmartNotificationAssembler {
    public static final String LM_GROUP = "lmGroup";
    public static final String LM_PROGRAM = "lmProgram";
    public static final String TOTAL_DEVICES = "totalDevices";
    public static final String TOTAL_FAILED = "totalFailed";
    public static final String CONTROL_TYPE = "controlType";

    public static SmartNotificationEvent assemble(String group, String program, int totalDevices,
            int totalFailed, EatonCloudJobControlType controlType ) {
        var event = new SmartNotificationEvent(new Instant());
        event.setParameters(Map.ofEntries(
                entry(LM_GROUP, group),
                entry(LM_PROGRAM, program),
                entry(TOTAL_DEVICES, totalDevices),
                entry(TOTAL_FAILED, totalFailed),
                entry(CONTROL_TYPE, controlType == EatonCloudJobControlType.SHED ? 1 : 0)));
        return event;
    }
}
