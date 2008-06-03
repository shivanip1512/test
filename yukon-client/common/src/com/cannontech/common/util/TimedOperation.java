package com.cannontech.common.util;

import java.util.Date;

public interface TimedOperation {

    public Date getStartTime();
    public void setStartTime(Date startTime);
    public Date getStopTime();
    public void setStopTime(Date stopTime);
}
