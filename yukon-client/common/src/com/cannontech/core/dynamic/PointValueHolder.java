package com.cannontech.core.dynamic;

import java.util.Date;

public interface PointValueHolder {
    public int getId();
    public Date getPointDataTimeStamp();
    /**@see PointValueQualityHolder.getPointType for a method that returns the enum value*/
    public int getType();
    public double getValue();
    public default String getTrackingId() {
        return "";
    }
}
