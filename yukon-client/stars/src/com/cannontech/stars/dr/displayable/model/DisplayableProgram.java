package com.cannontech.stars.dr.displayable.model;

import java.util.List;

import com.cannontech.stars.dr.program.model.Program;

public class DisplayableProgram {
    private Program program;
    private List<DisplayableControlHistory> displayableControlHistoryList;

    public DisplayableProgram(Program program, List<DisplayableControlHistory> controlHistoryDisplayList) {
        this.program = program;
        this.displayableControlHistoryList = controlHistoryDisplayList;
    }

    public Program getProgram() {
        return program;
    }
    public void setProgram(Program program) {
        this.program = program;
    }

    public List<DisplayableControlHistory> getDisplayableControlHistoryList() {
        return displayableControlHistoryList;
    }
    public void setDisplayableControlHistoryList(
                     List<DisplayableControlHistory> displayableControlHistoryList) {
        this.displayableControlHistoryList = displayableControlHistoryList;
    }
}
