package com.cannontech.common.smartNotification.model;

import org.joda.time.Instant;

import com.google.common.collect.ImmutableMap;


public class EatonCloudDrEventAssembler extends SmartNotificationAssembler {
    public static final String LM_GROUP = "lmGroup";
    public static final String LM_PROGRAM = "lmProgram";
    public static final String TOTAL_DEVICES = "totalDevices";
    public static final String TOTAL_FAILED = "totalFailed";

    public static SmartNotificationEvent assemble(String group, String program, int totalDevices,
            int totalFailed) {
        SmartNotificationEvent event = new SmartNotificationEvent(new Instant());
        event.setParameters(ImmutableMap.of(
                LM_GROUP, group,
                LM_PROGRAM, program,
                TOTAL_DEVICES, totalDevices,
                TOTAL_FAILED, totalFailed));
        return event;
    }
}
