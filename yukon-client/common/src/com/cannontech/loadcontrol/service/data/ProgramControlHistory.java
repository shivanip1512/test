package com.cannontech.loadcontrol.service.data;

import java.util.Date;

public class ProgramControlHistory {
    private int programHistoryId;
    private int programId;
    private String programName;
    private Date startDateTime;
    private Date stopDateTime;
    private String gearName;
    private boolean knownGoodStopDateTime;
    private String originSource;

    public ProgramControlHistory(int programHistoryId, int programId) {
        this.programHistoryId = programHistoryId;
        this.programId = programId;
    }

    public int getProgramHistoryId() {
        return programHistoryId;
    }

    public int getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getStopDateTime() {
        return stopDateTime;
    }

    public void setStopDateTime(Date stopDateTime) {
        this.stopDateTime = stopDateTime;
    }

    public String getGearName() {
        return gearName;
    }

    public void setGearName(String gearName) {
        this.gearName = gearName;
    }

    /**
     * This boolean represents when it is known that the "Start" and "Stop" LMProgramGearHistory records that were found to create this ProgramControlHistory
     * share the same LMProgramGearHistory.LMProgramHistoryId.
     * If false, there is a good chance it is still valid, but if the duration between startDateTime and stopDateTime is suspiciously
     * long it may be that the LMProgramGearHistory identified as the "Stop" is not actually related.
     * Note: All BGE-Style gear changes will be false. This alone is NOT an indication that the stopDateTime is wrong.
     * @see com.cannontech.loadcontrol.dao.impl.LoadControlProgramDaoImpl#baseProgramControlHistory(Integer programId, Date startDateTime, Date stopDateTime)
     * @return
     */
    public boolean isKnownGoodStopDateTime() {
        return knownGoodStopDateTime;
    }

    /**
     * This boolean should be set when it is known that the "Start" and "Stop" LMProgramGearHistory records that were found to create this ProgramControlHistory
     * share the same LMProgramGearHistory.LMProgramHistoryId.
     * If set to false, there is a good chance it is still valid, but if the duration between startDateTime and stopDateTime is suspiciously
     * long it may be that the LMProgramGearHistory identified as the "Stop" is not actually related.
     * @see com.cannontech.loadcontrol.dao.impl.LoadControlProgramDaoImpl#baseProgramControlHistory(Integer programId, Date startDateTime, Date stopDateTime)
     * @param knownGoodStopDateTime
     */
    public void setKnownGoodStopDateTime(boolean knownGoodStopDateTime) {
        this.knownGoodStopDateTime = knownGoodStopDateTime;
    }

    public String getOriginSource() {
        return originSource;
    }

    public void setOriginSource(String originSource) {
        this.originSource = originSource;
    }
}
