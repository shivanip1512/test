package com.cannontech.stars.dr.displayable.model;

import java.util.List;

import com.cannontech.stars.dr.program.model.Program;

public class DisplayableProgram {
    private final Program program;
    private final List<DisplayableControlHistory> displayableControlHistoryList;

    public DisplayableProgram(Program program, List<DisplayableControlHistory> controlHistoryDisplayList) {
        this.program = program;
        this.displayableControlHistoryList = controlHistoryDisplayList;
    }

    public Program getProgram() {
        return program;
    }

    public List<DisplayableControlHistory> getDisplayableControlHistoryList() {
        return displayableControlHistoryList;
    }

}
