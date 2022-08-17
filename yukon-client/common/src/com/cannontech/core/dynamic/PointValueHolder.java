package com.cannontech.core.dynamic;

import java.util.Date;

public interface PointValueHolder {
    public int getId();
    public Date getPointDataTimeStamp();
    public int getType();
    public double getValue();
    public default String getTrackingId() {
        return "";
    }
}
