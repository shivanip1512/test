package com.cannontech.database.db.pao;

import org.joda.time.Instant;

public class PaoScheduleAssignment implements Comparable<PaoScheduleAssignment> {

    private int eventId;
    private int scheduleId;
    private int paoId;
    private String deviceName;
    private String commandName;
    private String scheduleName;
    private int commandId;
    private Instant lastRunTime;
    private Instant nextRunTime;
    private String disableOvUv;

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getPaoId() {
        return paoId;
    }

    public void setPaoId(int paoId) {
        this.paoId = paoId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String command) {
        this.commandName = command;
    }

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String sched) {
        this.scheduleName = sched;
    }

    public int getCommandId() {
        return commandId;
    }

    public void setCommandId(int commandId) {
        this.commandId = commandId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Instant getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Instant lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Instant getNextRunTime() {
        return nextRunTime;
    }

    public String getDisableOvUv() {
        return disableOvUv;
    }

    public void setDisableOvUv(String disableOvUv) {
        this.disableOvUv = disableOvUv;
    }

    public void setNextRunTime(Instant nextRunTime) {
        this.nextRunTime = nextRunTime;
    }

    @Override
    public int compareTo(PaoScheduleAssignment o) {
        int ret = scheduleName.compareTo(o.getScheduleName());

        if (ret == 0) {
            return commandName.compareTo(o.getCommandName());
        } else {
            return ret;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + commandId;
        result = prime * result + ((commandName == null) ? 0 : commandName.hashCode());
        result = prime * result + ((deviceName == null) ? 0 : deviceName.hashCode());
        result = prime * result + ((disableOvUv == null) ? 0 : disableOvUv.hashCode());
        result = prime * result + eventId;
        result = prime * result + ((lastRunTime == null) ? 0 : lastRunTime.hashCode());
        result = prime * result + ((nextRunTime == null) ? 0 : nextRunTime.hashCode());
        result = prime * result + paoId;
        result = prime * result + scheduleId;
        result = prime * result + ((scheduleName == null) ? 0 : scheduleName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PaoScheduleAssignment other = (PaoScheduleAssignment) obj;
        if (commandId != other.commandId)
            return false;
        if (commandName == null) {
            if (other.commandName != null)
                return false;
        } else if (!commandName.equals(other.commandName))
            return false;
        if (deviceName == null) {
            if (other.deviceName != null)
                return false;
        } else if (!deviceName.equals(other.deviceName))
            return false;
        if (disableOvUv == null) {
            if (other.disableOvUv != null)
                return false;
        } else if (!disableOvUv.equals(other.disableOvUv))
            return false;
        if (eventId != other.eventId)
            return false;
        if (lastRunTime == null) {
            if (other.lastRunTime != null)
                return false;
        } else if (!lastRunTime.equals(other.lastRunTime))
            return false;
        if (nextRunTime == null) {
            if (other.nextRunTime != null)
                return false;
        } else if (!nextRunTime.equals(other.nextRunTime))
            return false;
        if (paoId != other.paoId)
            return false;
        if (scheduleId != other.scheduleId)
            return false;
        if (scheduleName == null) {
            if (other.scheduleName != null)
                return false;
        } else if (!scheduleName.equals(other.scheduleName))
            return false;
        return true;
    }

}
