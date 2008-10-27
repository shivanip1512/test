package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;

public class ProgramUtils {

    private static final Set<Integer> activeStatii = new HashSet<Integer>();
    private static final long nullDateTime = CtiUtilities.get1990GregCalendar().getTime().getTime();
    
    static {
        activeStatii.add(LMProgramBase.STATUS_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_MANUAL_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_FULL_ACTIVE);
        activeStatii.add(LMProgramBase.STATUS_TIMED_ACTIVE);
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
    @SuppressWarnings("unchecked")
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
     * Creates either a startNow or scheduledStart request message depending on wheather the given startTime is
     * equal to or less than the current time, or is sometime in the future.
     * @param program
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param constraintFlag
     * @return
     */
    public static LMManualControlRequest createStartRequest(LMProgramBase program, Date startTime, Date stopTime, int gearNumber, boolean forceStart) {
        
        int constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        if (forceStart) {
            constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE;
        }
        
        LMManualControlRequest request = null;
        Date nowTime = new Date();
        
        if (nowTime.getTime() >= startTime.getTime()) {
            request = program.createStartStopNowMsg(stopTime, gearNumber, "", true, constraintFlag);
        } else {
            request = program.createScheduledStartMsg(startTime, stopTime, gearNumber, null, "", constraintFlag);
        }
        
        return request;
    }
    
    /**
     * Creates either a stopNow or scheduledStop request message depending on wheather the given stopTime is
     * equal to or less than the current time, or is sometime in the future.
     * @param program
     * @param startTime
     * @param stopTime
     * @param gearNumber
     * @param constraintFlag
     * @return
     */
    public static LMManualControlRequest createStopRequest(LMProgramBase program, Date stopTime, int gearNumber, boolean forceStop) {
        
        int constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_USE;
        if (forceStop) {
            constraintFlag = LMManualControlRequest.CONSTRAINTS_FLAG_OVERRIDE;
        }
        
        LMManualControlRequest request = null;
        Date nowTime = new Date();
        
        if (nowTime.getTime() >= stopTime.getTime()) {
            request = program.createStartStopNowMsg(nowTime, gearNumber, "", false, constraintFlag);
        } else {
            request = program.createScheduledStopMsg(program.getStartTime().getTime(), stopTime, gearNumber, "");
        }
        
        return request;
    }
    
    public static boolean isActive(LMProgramBase program) {
        return activeStatii.contains(program.getProgramStatus());
    }
    
}
