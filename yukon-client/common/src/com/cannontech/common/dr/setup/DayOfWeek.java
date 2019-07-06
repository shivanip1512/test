package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableMap;

public enum DayOfWeek {
    SUNDAY, 
    MONDAY, 
    TUESDAY, 
    WEDNESDAY, 
    THURSDAY, 
    FRIDAY, 
    SATURDAY;

    private final static ImmutableMap<Integer, DayOfWeek> dayOfWeeksMap = ImmutableMap.<Integer, DayOfWeek> builder()
            .put(0, SUNDAY)
            .put(1, MONDAY)
            .put(2, TUESDAY)
            .put(3, WEDNESDAY)
            .put(4, THURSDAY)
            .put(5, FRIDAY)
            .put(6, SATURDAY)
            .build();

    public static String buildDBPersistent(List<DayOfWeek> daySelections) {
        if (daySelections != null && daySelections.size() > 0) {
            String selection = StringUtils.EMPTY;
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                selection = (daySelections.contains(dayOfWeek)) ? selection.concat("Y") : selection.concat("N");
            }
            return selection;
        }
        return "NNNNNNN";
    }

    public static List<DayOfWeek> buildModelRepresentation(String daySelection) {
        List<DayOfWeek> dayList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            char ch = daySelection.charAt(i);
            if (ch == 'Y') {
                dayList.add(dayOfWeeksMap.get(i));
            }
        }
        return dayList;
    }
}
