package com.cannontech.web.stars.dr.operator.inventory.model;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.cannontech.stars.dr.hardware.model.CommandSchedule;

public class CommandScheduleWrapper {
    
    private static final PeriodType HM_PERIOD_TYPE = PeriodType.time().withSecondsRemoved().withMillisRemoved();
    private CommandSchedule commandSchedule = new CommandSchedule();
    private int runPeriodHours;
    private int runPeriodMinutes;
    private int delayPeriodSeconds;
    
    public CommandSchedule getCommandSchedule() {
        return commandSchedule;
    }
    
    public void setCommandSchedule(CommandSchedule commandSchedule) {
        this.commandSchedule = commandSchedule;
        Period usefulRunPeriod = commandSchedule.getRunPeriod().normalizedStandard(HM_PERIOD_TYPE);
        setRunPeriodHours(usefulRunPeriod.getHours());
        setRunPeriodMinutes(usefulRunPeriod.getMinutes());
        setDelayPeriodSeconds(commandSchedule.getDelayPeriod().toStandardSeconds().getSeconds());
    }
    
    public int getRunPeriodHours() {
        return runPeriodHours;
    }
    
    public void setRunPeriodHours(int runPeriodHours) {
        this.runPeriodHours = runPeriodHours;
    }
    
    public int getRunPeriodMinutes() {
        return runPeriodMinutes;
    }
    
    public void setRunPeriodMinutes(int runPeriodMinutes) {
        this.runPeriodMinutes = runPeriodMinutes;
    }
    
    public int getDelayPeriodSeconds() {
        return delayPeriodSeconds;
    }
    
    public void setDelayPeriodSeconds(int delayPeriodSeconds) {
        this.delayPeriodSeconds = delayPeriodSeconds;
    }
    
}