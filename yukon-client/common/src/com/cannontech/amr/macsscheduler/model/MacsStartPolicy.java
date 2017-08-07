package com.cannontech.amr.macsscheduler.model;

import java.util.Arrays;
import java.util.Date;

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
    
    private StartPolicy policy;
    private int holidayScheduleId;
    private Date manualStartTime;
  
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
}
