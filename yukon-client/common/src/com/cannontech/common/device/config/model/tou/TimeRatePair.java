package com.cannontech.common.device.config.model.tou;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimeRatePair {
    private Date time = null;
    private String rate = "A";

    public TimeRatePair(){
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        this.time = calendar.getTime();
    }
    
    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

}
