package com.cannontech.common.dr.setup;

import java.util.List;
import javax.validation.Valid;

public class ProgramDetails {

    private Integer programId;
    private Integer startOffset;
    private Integer stopOffset;
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

    public Integer getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(Integer startOffset) {
        this.startOffset = startOffset;
    }

    public Integer getStopOffset() {
        return stopOffset;
    }

    public void setStopOffset(Integer stopOffset) {
        this.stopOffset = stopOffset;
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
