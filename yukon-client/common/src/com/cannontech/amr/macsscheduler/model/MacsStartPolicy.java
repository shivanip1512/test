package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.Schedule;

public class MacsStartPolicy {

    public enum StartPolicy implements DisplayableEnum {
        DATETIME(Schedule.DATETIME_START),
        DAYOFMONTH(Schedule.DAYOFMONTH_START),
        WEEKDAY(Schedule.WEEKDAY_START),
        MANUAL(Schedule.MANUAL_START);

        private String policy;

        StartPolicy(String policy) {
            this.policy = policy;
        }

        public String getPolicyString() {
            return policy;
        }
        
        public static StartPolicy getPolicy(String value) {
            return Arrays.stream(StartPolicy.values())
                    .filter(s -> s.getPolicyString().equalsIgnoreCase(value))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Could not find policy=" + value));
        }

        @Override
        public String getFormatKey() {
            return "yukon.web.modules.tools.schedule.startPolicy.type." + name();
        }
    }
    
    public static enum DayOfWeek implements DisplayableEnum {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;

        @Override
        public String getFormatKey() {
            return "yukon.common.day." + name() + ".short";
        }
        
    }
    
    private StartPolicy policy = StartPolicy.MANUAL;
    private int holidayScheduleId;
    private MacsTimeField time;
    private Instant startDateTime = Instant.now();
    private boolean everyYear;
    private Map<DayOfWeek, Boolean> days = new HashMap<>(defaultDays);
    private int dayOfMonth = 1;
    
    private static final Map<DayOfWeek, Boolean> defaultDays = new HashMap<>();
    
    static {
        for (DayOfWeek  day : DayOfWeek.values()) {
            defaultDays.put(day, false);
        }
    }
  
    public StartPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(StartPolicy policy) {
        this.policy = policy;
    }

    public int getHolidayScheduleId() {
        return holidayScheduleId;
    }

    public void setHolidayScheduleId(int holidayScheduleId) {
        this.holidayScheduleId = holidayScheduleId;
    }
    
    public int getStartDay() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().getDayOfMonth();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            return dayOfMonth;
        }
        return 0;
    }

    public int getStartMonth() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().getMonthOfYear();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            return DateTime.now().getMonthOfYear();
        }
        return 0;
    }

    public int getStartYear() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().getYear();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            return DateTime.now().getYear();
        }
        return 0;
    }

    public String getStartTime() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().toString("HH:mm:ss");
        } else if (policy == StartPolicy.DAYOFMONTH || policy == StartPolicy.WEEKDAY) {
           return time.getTimeString();
        }
        return "";
    }

    // "YYYYYYYN"
    public String getValidWeekDays() {
        if (policy == StartPolicy.WEEKDAY) {
            StringBuilder daysString = new StringBuilder();
            daysString.append(days.get(DayOfWeek.MONDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.TUESDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.WEDNESDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.THURSDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.FRIDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.SATURDAY) == false ? "N" : "Y");
            daysString.append(days.get(DayOfWeek.SUNDAY) == false ? "N" : "Y");
            daysString.append("N");
            return daysString.toString();
        }
        return "";
    }
    
    public void setWeekDays(String weekDays) {
        if (policy == StartPolicy.WEEKDAY) {
            int i = 0;
            days.put(DayOfWeek.SUNDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.MONDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.TUESDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.WEDNESDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.THURSDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.FRIDAY, weekDays.charAt(i++) == 'N' ? false : true);
            days.put(DayOfWeek.SATURDAY, weekDays.charAt(i++) == 'N' ? false : true);
        }
    }
    
    public void buildStartTimeDate(int month, int day, int year, String timeString) {
        if (policy == StartPolicy.DATETIME) {
            if (year == 0) {
                year = 1970;
                everyYear = true;
            }
            //startDateTime = MacsTimeField.parseDate(year, month, day, timeString).toInstant();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            dayOfMonth = day;
            DateTime parsedDate = MacsTimeField.parseDate(year, month, day, timeString);
            time = MacsTimeField.getTimeField(parsedDate);
        } else if (policy == StartPolicy.WEEKDAY) {
            DateTime now = DateTime.now();
            DateTime parsedDate = MacsTimeField.parseDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), timeString);
            time = MacsTimeField.getTimeField(parsedDate);
        }
    }
    
    public Instant getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
    }

    public boolean isEveryYear() {
        return everyYear;
    }

    public void setEveryYear(boolean everyYear) {
        this.everyYear = everyYear;
    }    
    
    public Map<DayOfWeek, Boolean> getDays() {
        return days;
    }

    public void setDays(Map<DayOfWeek, Boolean> days) {
        this.days = days;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public MacsTimeField getTime() {
        return time;
    }

    public void setTime(MacsTimeField time) {
        this.time = time;
    }
}
