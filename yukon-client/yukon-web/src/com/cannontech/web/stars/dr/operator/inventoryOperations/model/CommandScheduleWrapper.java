package com.cannontech.web.stars.dr.operator.inventoryOperations.model;

import org.joda.time.DurationFieldType;

import com.cannontech.stars.dr.hardware.model.CommandSchedule;

public class CommandScheduleWrapper {
    
    private CommandSchedule commandSchedule = new CommandSchedule();
    private String hours;
    private String minutes;
    private String seconds;
    private String startTime;
    
    public CommandSchedule getCommandSchedule() {
        return commandSchedule;
    }
    
    public void setCommandSchedule(CommandSchedule commandSchedule) {
        this.commandSchedule = commandSchedule;
        setHours(Integer.toString(commandSchedule.getRunPeriod().get(DurationFieldType.hours())));
        setMinutes(Integer.toString(commandSchedule.getRunPeriod().get(DurationFieldType.minutes())));
        setSeconds(Integer.toString(commandSchedule.getDelayPeriod().get(DurationFieldType.seconds())));
    }
    
    public String getHours() {
        return hours;
    }
    
    public void setHours(String hours) {
        this.hours = hours;
    }
    
    public String getMinutes() {
        return minutes;
    }
    
    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }
    
    public String getSeconds() {
        return seconds;
    }
    
    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

}