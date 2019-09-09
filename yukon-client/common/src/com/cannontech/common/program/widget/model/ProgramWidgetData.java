package com.cannontech.common.program.widget.model;

import java.util.List;
import java.util.Map;

public class ProgramWidgetData {

    private Map<String, List<ProgramData>> data;
    private int programDataCount;

    public ProgramWidgetData(Map<String, List<ProgramData>> data, int programDataCount) {
        this.data = data;
        this.programDataCount = programDataCount;
    }

    public Map<String, List<ProgramData>> getData() {
        return data;
    }

    public int getProgramDataCount() {
        return programDataCount;
    }
}
