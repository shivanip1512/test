package com.cannontech.web.amr.util.cronExpressionTag;

public enum CronDay {

    SUN("Sun", "Sundays", 1),
    MON("Mon", "Mondays", 2),
    TUES("Tues", "Tuesdays", 3),
    WED("Wed", "Wednesdays", 4),
    THURS("Thurs", "Thursdays", 5),
    FRI("Fri", "Fridays", 6),
    SAT("Sat", "Saturdays", 7);
    
    String abbreviatedName;
    String fullName;
    int number;
    
    CronDay(String abbreviatedName, String fullName, int number) {
        
        this.abbreviatedName = abbreviatedName;
        this.fullName = fullName;
        this.number = number;
    }
    
    public String getAbbreviatedName() {
        return abbreviatedName;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public int getNumber() {
        return number;
    }
    public String getRequestName() {
        return "CRONEXP_WEEKLY_OPTION_EVERY_X_WEEKS_ON_" + this.name();
    }
}
