package com.cannontech.jobs.support;

import java.text.ParseException;

public class ScheduleException extends Exception {

    public ScheduleException(String string, ParseException e) {
        super(string,e);
    }
    
    public ScheduleException(String string, UnsupportedOperationException e) {
        super(string,e);
    }
    
    public ScheduleException(String string) {
        super(string);
    }

}
