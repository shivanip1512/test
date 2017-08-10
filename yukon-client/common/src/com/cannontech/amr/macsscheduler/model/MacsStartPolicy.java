package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.Instant;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.message.macs.message.Schedule;

public class MacsStartPolicy {

    public enum StartPolicy {
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
    private Date manualStartTime;
    private Instant startDateTime;
    private boolean everyYear;
    private Map<DayOfWeek, Boolean> days = new HashMap<>(defaultDays);
    private int dayOfMonth;
    
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
       return 0;
   }


   public int getStartMonth() {
       return 0;
   }


   public int getStartYear() {
       return 0;
   }

    /**
     * @return the startTime (must be 8 chars)
     */
    public String getStartTime() {
        return "00:00:00";
    }

    public String getValidWeekDays() {
        return "YYYYYYYN";
    }

    public Date getManualStartTime() {
        return manualStartTime;
    }

    public void setManualStartTime(Date manualStartTime) {
        this.manualStartTime = manualStartTime;
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
}
