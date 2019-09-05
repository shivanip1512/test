package com.cannontech.dr.program.service;

import java.util.List;
import java.util.Map;

import com.cannontech.common.program.widget.model.ProgramData;
import com.cannontech.user.YukonUserContext;

public interface ProgramWidgetService {
    /**
     * Build data for Program widget.
     */
    public Map<String, List<ProgramData>> buildProgramWidgetData(YukonUserContext userContext);

    /**
     * Build Program Details data for today, next control day after today and
     * previous 7 days.
     */
    public Map<String, List<ProgramData>> buildProgramDetailsData(YukonUserContext userContext);

    /**
     * Return total number of Todays and Scheduled program data count.
     */
	public int getTodaysAndScheduledProgramDataCount();
}
