package com.cannontech.common.smartNotification.model;

import java.io.Serializable;

public class DailyDigestTestParams implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int hour;
    
    public DailyDigestTestParams(int hour) {
        this.hour = hour;
    }
    
    public int getHour() {
        return hour;
    }
    
}
