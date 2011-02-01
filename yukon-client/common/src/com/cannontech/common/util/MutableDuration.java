package com.cannontech.common.util;

import org.joda.time.Duration;
import org.joda.time.base.BaseDuration;

public class MutableDuration extends BaseDuration{

    public MutableDuration(long duration) {
        super(duration);
    }

    public void plus(Duration duration) {
        setMillis(getMillis()+duration.getMillis());
    }
    
}