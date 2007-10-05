package com.cannontech.common.device.config.model.tou;

import java.util.Date;

public class TimeRatePair {
    private Date time = null;
    private String rate = null;

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
