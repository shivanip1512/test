package com.cannontech.jobs.model;

public class ScheduledRepeatingJob extends YukonJob {
    
    //Quartz will never execute a cron expression with a year above 2300
    public static final String NEVER_RUN_CRON_STRING = "0 0 0 1 1 ? 3099";
    
    private String cronString;

    public String getCronString() {
        return cronString;
    }
    
    public void setCronString(String cronString) {
        this.cronString = cronString;
    }

    @Override
    public String toString() {
        return super.toString() + "cronString=" + cronString;     
    }
    
    public boolean isManualScheduleWithoutRunDate(){
        if(cronString != null && NEVER_RUN_CRON_STRING.equals(cronString)){
            return true;
        }
        return false;
    }
    
}
