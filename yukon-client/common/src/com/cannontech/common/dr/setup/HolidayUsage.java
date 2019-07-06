package com.cannontech.common.dr.setup;

public enum HolidayUsage {
    Exclude("E"), 
    Force("F");

    private final String holidayUsage;

    private HolidayUsage(String holidayUsage) {
        this.holidayUsage = holidayUsage;
    }

    public String getHolidayUsage() {
        return holidayUsage;
    }
}
