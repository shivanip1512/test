package com.cannontech.common.dr.setup;

import java.util.List;
import javax.validation.Valid;

public class ProgramDetails {

    private Integer programId;
    private Integer startOffsetInMinutes;
    private Integer stopOffsetInMinutes;
    private String category;
    @Valid private List<LMDto> gears;

    public ProgramDetails() {
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getStartOffsetInMinutes() {
        return startOffsetInMinutes;
    }

    public void setStartOffsetInMinutes(Integer startOffsetInMinutes) {
        this.startOffsetInMinutes = startOffsetInMinutes;
    }

    public Integer getStopOffsetInMinutes() {
        return stopOffsetInMinutes;
    }

    public void setStopOffsetInMinutes(Integer stopOffsetInMinutes) {
        this.stopOffsetInMinutes = stopOffsetInMinutes;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<LMDto> getGears() {
        return gears;
    }

    public void setGears(List<LMDto> gears) {
        this.gears = gears;
    }

}
