package com.cannontech.common.dr.setup;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableMap;

public enum HolidayUsage {
    EXCLUDE('E'),
    FORCE('F'),
    NONE('N');
    
    private final static ImmutableMap<Character, HolidayUsage> lookupByHoliday =
        ImmutableMap.<Character, HolidayUsage> builder().put('E', EXCLUDE)
                                                        .put('F', FORCE)
                                                        .put('N', NONE)
                                                        .build();

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
