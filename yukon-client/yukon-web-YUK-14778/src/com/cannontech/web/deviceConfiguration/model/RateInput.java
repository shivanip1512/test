package com.cannontech.web.deviceConfiguration.model;

public class RateInput {
    String time = "00:00";
    String rate = "A";

    public String getTime() {
        return time;
    }
    
    public void setTime(String time) {
        this.time = time;
    }
    
    public String getRate() {
        return rate;
    }
    
    public void setRate(String rate) {
        this.rate = rate;
    }
}
