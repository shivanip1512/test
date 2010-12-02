package com.cannontech.stars.dr.hardware.model;

import org.joda.time.Period;
import org.joda.time.ReadablePeriod;

public class CommandSchedule {
    private int commandScheduleId;
    private String startTimeCronString;
    private ReadablePeriod runPeriod = new Period();
    private ReadablePeriod delayPeriod = new Period(2000);
    private boolean enabled = true;
    private int energyCompanyId;

    public int getCommandScheduleId() {
        return commandScheduleId;
    }

    public void setCommandScheduleId(int commandScheduleId) {
        this.commandScheduleId = commandScheduleId;
    }

    public String getStartTimeCronString() {
        return startTimeCronString;
    }

    public void setStartTimeCronString(String startTimeCronString) {
        this.startTimeCronString = startTimeCronString;
    }

    public ReadablePeriod getRunPeriod() {
        return runPeriod;
    }

    public void setRunPeriod(ReadablePeriod runPeriod) {
        this.runPeriod = runPeriod;
    }

    public ReadablePeriod getDelayPeriod() {
        return delayPeriod;
    }

    public void setDelayPeriod(ReadablePeriod delayPeriod) {
        this.delayPeriod = delayPeriod;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    @Override
    public String toString() {
        return "CommandSchedule [commandScheduleId=" + commandScheduleId +
            ", delayPeriod=" + delayPeriod + ", enabled=" + enabled + ", runPeriod=" + runPeriod +
            ", startTimeCronString=" + startTimeCronString +
            ", energyCompanyId=" + energyCompanyId + "]";
    }
}
