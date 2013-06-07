package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class UpdateScheduleMessage extends BaseMessage {

    // The schedule to update
    private ScheduleMessage schedule;

    // The schedules script
    private String script = "";

    public ScheduleMessage getSchedule() {
        return schedule;
    }

    public void setSchedule(ScheduleMessage newSchedule) {
        schedule = newSchedule;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String newScript) {
        script = newScript;
    }
}
