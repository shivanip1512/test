package com.cannontech.messaging.message.loadcontrol;

import java.util.GregorianCalendar;

import org.springframework.core.style.ToStringCreator;

import com.cannontech.common.util.CtiUtilities;

/**
 * ScheduleCommand objects are sent to the CBC server to request that an operation
 * be done on the given strategy.  Clients only send CBCCommands
 * and the server only receives them.
 */

public class ManualControlRequestMessage extends LmMessage {

    private int command;
    private int yukonId;
    private GregorianCalendar notifyTime = CtiUtilities.get1990GregCalendar();
    private GregorianCalendar startTime = CtiUtilities.get1990GregCalendar();
    private GregorianCalendar stopTime = CtiUtilities.get1990GregCalendar();
    private int startGear = 0;
    private int startPriority = 0;
    private String addditionalInfo = new String();
    private int constraintFlag = CONSTRAINTS_FLAG_USE;

    // The following are the different commands that
    // can be applied to control area, trigger, or program and map into the C++ side
    public static final int SCHEDULED_START = 0;
    public static final int SCHEDULED_STOP = 1;
    public static final int START_NOW = 2;
    public static final int STOP_NOW = 3;
    public static final int CHANGE_GEAR = 4;

    public static final String[] COMMAND_STRINGS = { "SCHEDULED START", "SCHEDULED STOP", "START NOW", "STOP NOW",
        "CHANGE GEAR" };

    // LMProgram constraint flags
    public static final int CONSTRAINTS_FLAG_USE = 0;
    public static final int CONSTRAINTS_FLAG_OVERRIDE = 1;
    public static final int CONSTRAINTS_FLAG_CHECK = 2;

    public static final String[] CONSTRAINT_FLAG_STRS = { "Observe", "Override", "Check" };

    public java.lang.String getAddditionalInfo() {
        return addditionalInfo;
    }

    public int getCommand() {
        return command;
    }

    public static String getCommandString(int command) {
        return COMMAND_STRINGS[command];
    }

    /**
     * Returns the Constraint Flag Id given the String representation
     */
    public static int getConstraintId(String constStr) {
        for (int i = 0; i < CONSTRAINT_FLAG_STRS.length; i++) {
            if (constStr.equalsIgnoreCase(CONSTRAINT_FLAG_STRS[i]))
                return i;
        }

        // some reasonable default value
        return CONSTRAINTS_FLAG_USE;
    }

    public GregorianCalendar getNotifyTime() {
        return notifyTime;
    }

    public int getStartGear() {
        return startGear;
    }

    public int getStartPriority() {
        return startPriority;
    }

    public GregorianCalendar getStartTime() {
        return startTime;
    }

    public GregorianCalendar getStopTime() {
        return stopTime;
    }

    public int getYukonId() {
        return yukonId;
    }

    public void setAddditionalInfo(java.lang.String newAddditionalInfo) {
        addditionalInfo = newAddditionalInfo;
    }

    public void setCommand(int newValue) {
        this.command = newValue;
    }

    public void setNotifyTime(GregorianCalendar newNotifyTime) {
        notifyTime = newNotifyTime;
    }

    public void setStartGear(int newStartGear) {
        startGear = newStartGear;
    }

    public void setStartPriority(int newStartPriority) {
        startPriority = newStartPriority;
    }

    public void setStartTime(GregorianCalendar newStartTime) {
        startTime = newStartTime;
    }

    public void setStopTime(GregorianCalendar newStopTime) {
        stopTime = newStopTime;
    }

    public void setYukonId(int newYukonId) {
        yukonId = newYukonId;
    }

    public int getConstraintFlag() {
        return constraintFlag;
    }

    public void setConstraintFlag(int i) {
        constraintFlag = i;
    }

    @Override
    public String toString() {
        ToStringCreator tsc = new ToStringCreator(this);
        tsc.append("command", getCommandString(getCommand()));
        tsc.append("yukonId", getYukonId());
        tsc.append("notifyTime", getNotifyTime().getTime());
        tsc.append("startTime", getStartTime().getTime());
        tsc.append("stopTime", getStopTime().getTime());
        tsc.append("startGear", getStartGear());
        tsc.append("startPriority", getStartPriority());
        tsc.append("addditionalInfo", getAddditionalInfo());
        tsc.append("constraintFlag", CONSTRAINT_FLAG_STRS[getConstraintFlag()]);
        return tsc.toString();
    }
}
