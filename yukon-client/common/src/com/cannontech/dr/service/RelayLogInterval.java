package com.cannontech.dr.service;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public enum RelayLogInterval {
    LOG_5_MINUTE(5),
    LOG_15_MINUTE(15),
    LOG_30_MINUTE(30),
    LOG_60_MINUTE(60);
    
    private Duration duration;
    
    private RelayLogInterval(int minutes) {
        duration = Duration.standardMinutes(minutes);
    }
    
    public Duration getDuration() {
        return duration;
    }

    public DateTime start(DateTime date) {
        //  RelayLogInterval is 60 minutes or less, or 3,600,000 millis.  
        //    The result of this modulus operation cannot be larger than 3,600,000, and will fit in an int. 
        int difference = (int)(date.getMillis() % duration.getMillis());
        
        return date.minusMillis(difference);
    }

    public DateTime end(DateTime date) {
        return start(date).plus(duration); 
    }
}
