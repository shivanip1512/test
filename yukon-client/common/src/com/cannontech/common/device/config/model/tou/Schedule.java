package com.cannontech.common.device.config.model.tou;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Schedule {
    List<TimeRatePair> timeRateList = new ArrayList<TimeRatePair>();

    public Schedule(final int numberOftimeRate){
        // Add midnight time/rate
        TimeRatePair midnightPair = new TimeRatePair();
        
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        midnightPair.setTime(calendar.getTime());
        timeRateList.add(midnightPair);
        
        // Add other time/rates
        for(int i = 0; i < numberOftimeRate; i++){
            timeRateList.add(new TimeRatePair());
        }
    }
    
    public List<TimeRatePair> getTimeRateList() {
        return timeRateList;
    }

    public void setTimeRateList(List<TimeRatePair> timeRateList) {
        this.timeRateList = timeRateList;
    }

}
