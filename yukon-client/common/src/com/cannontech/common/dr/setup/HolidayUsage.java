package com.cannontech.common.dr.setup;

public enum HolidayUsage {
    EXCLUDE("E"), 
    FORCE("F");

    private final String holidayUsage;

    private HolidayUsage(String holidayUsage) {
        this.holidayUsage = holidayUsage;
    }

    public String getHolidayUsage() {
        return holidayUsage;
    }
}
