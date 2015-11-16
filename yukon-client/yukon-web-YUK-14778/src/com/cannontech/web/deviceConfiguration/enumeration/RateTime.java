package com.cannontech.web.deviceConfiguration.enumeration;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.input.type.InputOption;
import com.google.common.collect.ImmutableList.Builder;

@Component
public class RateTime implements DeviceConfigurationInputEnumeration {

    @Autowired private DateFormattingService dateFormattingService;
    private static final List<LocalTime> validRateTimes;

    static {
        Builder<LocalTime> minutesBuilder = new Builder<>();

        /*
         * This loop builds up the String representation of the TOU Times as an offset into the day 
         * in 5-minute intervals starting with 00:00 and incrementing to 23:55.
         */

        for (int minute=0; minute < DateTimeConstants.MINUTES_PER_DAY; minute += 5) {
            minutesBuilder.add( LocalTime.fromMillisOfDay(minute * DateTimeConstants.MILLIS_PER_MINUTE));
        }

        validRateTimes = minutesBuilder.build();
    }

    @Override
    public List<InputOption> getDisplayableValues(YukonUserContext userContext) {

        DateTimeFormatter formatter = dateFormattingService.getDateTimeFormatter(DateFormatEnum.TIME24H, YukonUserContext.system);
        List<InputOption> rateTimes = new ArrayList<>();

        for (LocalTime time : validRateTimes) {
            String dbTime = formatter.print(time);
            rateTimes.add( new InputOption( dbTime, dbTime));
        }

        return rateTimes;
    }

    @Override
    public String getEnumOptionName() {
        return "RateTime";
    }
}