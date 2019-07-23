package com.cannontech.web.dr.setup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.dr.setup.HolidayUsage;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.dr.setup.ProgramConstraint;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class ProgramConstraintControllerHelper {
    private static final String selectKey = "yukon.web.modules.dr.setup.select";
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public void setDefaultValues(ProgramConstraint programConstraint) {
        if (programConstraint.getMaxActivateSeconds() == null) {
            programConstraint.setMaxActivateSeconds(0);
        }
        if (programConstraint.getMaxDailyOpsSeconds() == null) {
            programConstraint.setMaxDailyOpsSeconds(0);
        }
        if (programConstraint.getMinActivateSeconds() == null) {
            programConstraint.setMinActivateSeconds(0);
        }
        if (programConstraint.getMinRestartSeconds() == null) {
            programConstraint.setMinRestartSeconds(0);
        }
        if (programConstraint.getMaxHoursDaily() == null) {
            programConstraint.setMaxHoursDaily(0);
        }
        if (programConstraint.getMaxHoursMonthly() == null) {
            programConstraint.setMaxHoursMonthly(0);
        }
        if (programConstraint.getMaxHoursAnnually() == null) {
            programConstraint.setMaxHoursAnnually(0);
        }
        if (programConstraint.getMaxHoursSeasonal() == null) {
            programConstraint.setMaxHoursSeasonal(0);
        }
        if (programConstraint.getHolidaySchedule() == null || programConstraint.getHolidaySchedule().getId() == 0) {
            programConstraint.setHolidayUsage(HolidayUsage.NONE);
        }
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
