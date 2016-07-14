package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.base.CaseFormat;

@Component
public final class ScheduleInput implements DeviceConfigurationInputEnumeration {

    private static final String baseKey = "yukon.web.modules.tools.configs.enum.schedules.";

    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    public enum Schedule implements DisplayableEnum {
        SCHEDULE_1,
        SCHEDULE_2,
        SCHEDULE_3,
        SCHEDULE_4;

        @Override
        public String getFormatKey() {
            return baseKey + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.name()) ;
        }
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {
        MessageSourceAccessor messageAccessor = messageResolver.getMessageSourceAccessor(userContext);

        List<InputOption> validSchedules = new ArrayList<>();

        for (Schedule schedule : Schedule.values()) {
            validSchedules.add( new InputOption( schedule.name(), messageAccessor.getMessage(schedule)));
        }
        return validSchedules;
    }

    @Override
    public String getEnumOptionName() {
        return "Schedule";
    }

    @Override
    public SelectionType getSelectionType() {
        return SelectionType.SWITCH;
    }
}