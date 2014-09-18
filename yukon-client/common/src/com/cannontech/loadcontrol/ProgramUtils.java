package com.cannontech.loadcontrol;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;

public class ProgramUtils {

    private static final Set<Integer> activeStatii = new HashSet<>();
    private static final Set<Integer> scheduledStatii = new HashSet<>();
    private static final Set<Integer> inactiveStatii = new HashSet<>();
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
            for(LMProgramDirectGear gear : gearProgram.getDirectGearVector()) {
                if (gearProgram.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue() ) {
                    return gear.getGearName();
                }
            }
        }
        return null;
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
