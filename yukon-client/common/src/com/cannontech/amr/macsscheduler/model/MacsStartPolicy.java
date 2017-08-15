package com.cannontech.amr.macsscheduler.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.amr.macsscheduler.model.MacsTimeField.AmPmOptionEnum;
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
    
    private StartPolicy policy;
    private int holidayScheduleId;
    private MacsTimeField time;
    private Instant startDateTime = new Instant();
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
            return new DateTime().getMonthOfYear();
        }
        return 0;
    }

    public int getStartYear() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().getYear();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            return new DateTime().getYear();
        }
        return 0;
    }

    public String getStartTime() {
        if (policy == StartPolicy.DATETIME) {
            return startDateTime.toDateTime().toString("HH:mm:ss");
        } else if (policy == StartPolicy.DAYOFMONTH || policy == StartPolicy.WEEKDAY) {
            NumberFormat formatter = new DecimalFormat("00");  
            //00:00:00 AM
            String timeString = formatter.format(time.getHours()) + ":" + formatter.format(time.getMinutes()) + ":00 "+ time.getAmPm();
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss a");
            String dateTimeString = DateTime.now().toString("MM/dd/yyyy") + " " + timeString;
            DateTime date = dateFormatter.parseDateTime(dateTimeString);
            return date.toString("HH:mm:ss");
        }
        return "";
    }

    // "YYYYYYYN"
    public String getValidWeekDays() {
        if (policy == StartPolicy.WEEKDAY) {
            StringBuilder daysString = new StringBuilder();
            daysString.append(defaultDays.get(DayOfWeek.MONDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.TUESDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.WEDNESDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.THURSDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.FRIDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.SATURDAY) == false ? "N" : "Y");
            daysString.append(defaultDays.get(DayOfWeek.SUNDAY) == false ? "N" : "Y");
            daysString.append("N");
            return daysString.toString();
        }
        return "";
    }
    
    public void setWeekDays(String weekDays) {
        if (policy == StartPolicy.WEEKDAY) {
            int i = 0;
            defaultDays.put(DayOfWeek.SUNDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.MONDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.TUESDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.WEDNESDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.THURSDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.FRIDAY, weekDays.charAt(i++) == 'N' ? false : true);
            defaultDays.put(DayOfWeek.SATURDAY, weekDays.charAt(i++) == 'N' ? false : true);
        }
    }
    
    public void buildStartTimeDate(int month, int day, int year, String timeString) {
        if (policy == StartPolicy.DATETIME) {
            if (year == 0) {
                year = 1970;
                everyYear = true;
            }
            startDateTime = parseDate(year, month, day, timeString).toInstant();
        } else if (policy == StartPolicy.DAYOFMONTH) {
            dayOfMonth = day;
            DateTime parsedDate = parseDate(year, month, day, timeString);
            time = getTimeField(parsedDate);
        } else if (policy == StartPolicy.WEEKDAY) {
            DateTime now = DateTime.now();
            DateTime parsedDate = parseDate(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), timeString);
            time = getTimeField(parsedDate);
        }
    }
    
    private MacsTimeField getTimeField(DateTime parsedDate){
        MacsTimeField timeField = new MacsTimeField();
        timeField.setAmPm(AmPmOptionEnum.valueOf(parsedDate.toString("a")));
        int hours = parsedDate.getHourOfDay();
        timeField.setHours(hours > 12 ? hours - 12 : hours);
        timeField.setMinutes(parsedDate.getMinuteOfHour());
        return timeField;
    }
    
    private DateTime parseDate(int year, int month, int day, String time) {
        String date = new DateTime(year, month, day, 00, 00, 00).toString("MM/dd/yyyy") + " " + time;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
        return formatter.parseDateTime(date);
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
