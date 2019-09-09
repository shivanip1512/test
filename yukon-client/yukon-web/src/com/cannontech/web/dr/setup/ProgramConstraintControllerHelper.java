package com.cannontech.web.dr.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ProgramConstraintControllerHelper {
    private static final String selectKey = "yukon.web.modules.dr.setup.select";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public void setDefaultValues(ProgramConstraint programConstraint) {
            programConstraint.setMaxActivateSeconds(0);
            programConstraint.setMaxDailyOps(0);
            programConstraint.setMinActivateSeconds(0);
            programConstraint.setMinRestartSeconds(0);
            programConstraint.setMaxHoursDaily(0);
            programConstraint.setMaxHoursMonthly(0);
            programConstraint.setMaxHoursAnnually(0);
            programConstraint.setMaxHoursSeasonal(0);
    }

    public void setDefaultSeasonSchedule(List<LMDto> seasonSchedules, YukonUserContext userContext) {
        LMDto defaultSchedule = new LMDto(0, getSelectText(userContext));
        seasonSchedules.add(0, defaultSchedule);
    }

    public void setDefaultHolidaySchedule(List<LMDto> holidaySchedules, YukonUserContext userContext) {
        LMDto defaultSchedule = new LMDto(0, getSelectText(userContext));
        holidaySchedules.remove(0);
        holidaySchedules.add(0, defaultSchedule);
    }

    private String getSelectText(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        return accessor.getMessage(selectKey);
    }

}
