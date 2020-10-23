package com.cannontech.common.dr.setup;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControlAreaProgramAssignment implements DBPersistentConverter<LMControlAreaProgram> {

    private Integer programId;
    private String programName;
    private Integer startPriority;
    private Integer stopPriority;

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public Integer getStartPriority() {
        return startPriority;
    }

    public void setStartPriority(Integer startPriority) {
        this.startPriority = startPriority;
    }

    public Integer getStopPriority() {
        return stopPriority;
    }

    public void setStopPriority(Integer stopPriority) {
        this.stopPriority = stopPriority;
    }

    /**
     * Build program assignment model.
     */
    @Override
    public void buildModel(LMControlAreaProgram lmControlAreaProgram) {
        setProgramId(lmControlAreaProgram.getLmProgramDeviceID());
        setStartPriority(lmControlAreaProgram.getStartPriority());
        setStopPriority(lmControlAreaProgram.getStopPriority());
    }

    /**
     * Build DBPersistent for program assignment.
     */
    @Override
    public void buildDBPersistent(LMControlAreaProgram lmControlAreaProgram) {
        lmControlAreaProgram.setLmProgramDeviceID(getProgramId());
        if (getStartPriority() != null) {
            lmControlAreaProgram.setStartPriority(getStartPriority());
        }
        if (getStopPriority() != null) {
            lmControlAreaProgram.setStopPriority(getStopPriority());
        }
    }

}
