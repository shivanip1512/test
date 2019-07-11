package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableMap;

public enum HolidayUsage {
    EXCLUDE('E'),
    FORCE('F'),
    NONE('N');
    
    private final static Logger log = YukonLogManager.getLogger(HolidayUsage.class);
    private final static ImmutableMap<Character, HolidayUsage> lookupByHoliday;

    static {
        try {
            ImmutableMap.Builder<Character, HolidayUsage> holidayBuilder = ImmutableMap.builder();
            for (HolidayUsage holidayUsage : values()) {
                holidayBuilder.put(holidayUsage.holidayUsage, holidayUsage);
            }
            lookupByHoliday = holidayBuilder.build();
        } catch (IllegalArgumentException e) {
            log.warn("Caught exception while building lookup maps, look for a duplicate holiday usage.", e);
            throw e;
        }
    }

    private final Character holidayUsage;

    private HolidayUsage(Character holidayUsage) {
        this.holidayUsage = holidayUsage;
    }

    public Character getHolidayUsage() {
        return holidayUsage;
    }

    public static HolidayUsage getForHoliday(Character holiday) {
        HolidayUsage holidayUsage = lookupByHoliday.get(holiday);
        checkArgument(holidayUsage != null, holidayUsage);
        return holidayUsage;
    }
}
