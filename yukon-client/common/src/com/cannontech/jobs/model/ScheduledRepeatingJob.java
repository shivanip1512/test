package com.cannontech.jobs.model;

import org.springframework.core.style.ToStringCreator;

public class ScheduledRepeatingJob extends YukonJob {
    private String cronString;

    public String getCronString() {
        return cronString;
    }

    public void setCronString(String cronString) {
        this.cronString = cronString;
    }
    
    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append(super.toString());
        tsc.append("cronString", cronString);
        return tsc.toString();    
    }
}
