package com.cannontech.common.program.widget.model;

import java.util.List;
import java.util.Map;

/**
 * This class will hold program data and programDataCount for todays and future scheduled program.
 * data : This holds maximum 10 program data records for today, scheduled and past date.
 * programDataCount : This holds exact count for todays and future scheduled program data records.
 *                    This will be used to display a message on program widget in case of todays and future 
 *                    scheduled program count is greater than 10 (Max limit of showing program data in Program Widget).
 * */
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
