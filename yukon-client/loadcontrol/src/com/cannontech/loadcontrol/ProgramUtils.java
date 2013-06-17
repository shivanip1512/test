package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public class ProgramUtils {

    private static final Set<Integer> activeStatii = new HashSet<Integer>();
    private static final Set<Integer> scheduledStatii = new HashSet<Integer>();
    private static final Set<Integer> inactiveStatii = new HashSet<Integer>();
    private static final long nullDateTime = CtiUtilities.get1990GregCalendar().getTime().getTime();
    
    static {
        activeStatii.add(LMProgramBase.STATUS_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_MANUAL_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_FULL_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_TIMED_ACTIVE);
        
        scheduledStatii.add(LMProgramBase.STATUS_SCHEDULED);
        scheduledStatii.add(LMProgramBase.STATUS_NOTIFIED);
        
        inactiveStatii.add(LMProgramBase.STATUS_INACTIVE);
        inactiveStatii.add(LMProgramBase.STATUS_STOPPING);
        inactiveStatii.add(LMProgramBase.STATUS_CNTRL_ATTEMPT);
        inactiveStatii.add(LMProgramBase.STATUS_NON_CNTRL);
    }
    
    public static int getProgramId(LMProgramBase program) {
        return program.getYukonID();
    }
    
    public static String getProgramName(LMProgramBase program) {
        return program.getYukonName();
    }
    
    /**
     * Gets start time as Date.
     * @param program
     * @return start time Date or null if program has no start time set.
     */
    public static Date getStartTime(LMProgramBase program) {

        Date startTime = program.getStartTime().getTime();
        if (startTime.getTime() <= nullDateTime) {
            return null;
        }
        return startTime;
    }
    
    /**
     * Gets the stop time as Date.
     * @param program
     * @return stop time Date or null if program has no stop time set.
     */
    public static Date getStopTime(LMProgramBase program) {
        
        Date stopTime = program.getStopTime().getTime();
        if (stopTime.getTime() <= nullDateTime) {
            return null;
        }
        return stopTime;
    }
    
    /**
     * Returns name of current gear.
     * Returns null if program does not implement IGearProgram or program has no gears.
     * @param program
     * @return
     */
    public static String getCurrentGearName(LMProgramBase program) {
        
        if (program instanceof IGearProgram) {
            IGearProgram gearProgram = (IGearProgram)program;
            for(LMProgramDirectGear gear : (Vector<LMProgramDirectGear>)gearProgram.getDirectGearVector()) {
                if (gearProgram.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue() ) {
                    return gear.getGearName();
                }           
            }
        }
        return null;
    }
    
    /**
     * Creates either a startNow or scheduledStart request message depending on whether the given startTime is
     * equal to or less than the current time, or is sometime in the future.
     * @param program
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param constraintFlag
     * @return
     */
    public static LMManualControlRequest createStartRequest(LMProgramBase program, Date startTime, Duration startOffset, Date stopTime, Duration stopOffset, int gearNumber, boolean forceStart) {
        
        int constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        if (forceStart) {
            constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE;
        }
        
        Date nowTime = new Date();
        
        boolean applyStopOffset = true;
        if (stopTime == null) {
            stopTime = CtiUtilities.get2035GregCalendar().getTime();
            applyStopOffset = false;
        }
        if (startTime == null && startOffset != null) {
            startTime = nowTime;
        }

        Date startTimeWithOffset = new Instant(startTime).plus(startOffset).toDate();
        Date stopTimeWithOffset = stopTime;
        if (applyStopOffset) {
        	stopTimeWithOffset = new Instant(stopTime).plus(stopOffset).toDate();
        }
        LMManualControlRequest request = null;
        if (startTimeWithOffset == null || nowTime.getTime() >= startTimeWithOffset.getTime()) {
            request = program.createStartStopNowMsg(stopTimeWithOffset, gearNumber, "", true, constraintFlag);
        } else {
            request = program.createScheduledStartMsg(startTimeWithOffset, stopTimeWithOffset, gearNumber, null, "", constraintFlag);
        }
        
        return request;
    }
    
    /**
     * Creates either a stopNow or scheduledStop request message depending on whether the given stopTime is
     * equal to or less than the current time, or is sometime in the future.
     * @param program
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param constraintFlag
     * @return
     */
    public static LMManualControlRequest createStopRequest(LMProgramBase program, Date stopTime, Duration stopOffset, int gearNumber, boolean forceStop) {
        
        int constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        if (forceStop) {
            constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE;
        }
        
        LMManualControlRequest request = null;
        Date nowTime = new Date();
        Date stopTimeWithOffset = stopTime;
        if (stopOffset != null) {
            if (stopTime == null) {
                stopTime = nowTime;
            }
            stopTimeWithOffset = new Instant(stopTime).plus(stopOffset).toDate();
        }

        if (stopTimeWithOffset == null || nowTime.getTime() >= stopTimeWithOffset.getTime()) {
            request = program.createStartStopNowMsg(CtiUtilities.get1990GregCalendar().getTime(), gearNumber, "", false, constraintFlag);
        } else {
            request = program.createScheduledStopMsg(CtiUtilities.get1990GregCalendar().getTime(), stopTimeWithOffset, gearNumber, "");
        }
        
        return request;
    }
    
    public static boolean isActive(LMProgramBase program) {
        return activeStatii.contains(program.getProgramStatus());
    }
    
    public static boolean isScheduled(LMProgramBase program) {
        return scheduledStatii.contains(program.getProgramStatus());
    }
    
    public static boolean isInactive(LMProgramBase program) {
        return inactiveStatii.contains(program.getProgramStatus());
    }
}
