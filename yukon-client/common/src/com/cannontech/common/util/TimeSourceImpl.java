package com.cannontech.common.util;

import java.util.Date;


public class TimeSourceImpl implements TimeSource {

    public long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public Date getCurrentTime() {
        return new Date();
    }

}
