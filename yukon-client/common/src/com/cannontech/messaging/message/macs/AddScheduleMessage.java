package com.cannontech.messaging.message.macs;

import com.cannontech.messaging.message.BaseMessage;

public class AddScheduleMessage extends BaseMessage {

    // The schedule to add
    private ScheduleMessage schedule;

    // The script for the schedule if any
    private String script = "";

    public ScheduleMessage getSchedule() {
        return schedule;
    }

    public String getScript() {
        return script;
    }

    public void setSchedule(ScheduleMessage newSchedule) {
        schedule = newSchedule;
    }

    public void setScript(java.lang.String newScript) {
        script = newScript;
    }
}
