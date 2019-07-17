package com.cannontech.common.dr.setup;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ControlAreaProgramAssignment {

    private Integer programId;
    private Integer startPriority;
    private Integer stopPriority;

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
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

}
