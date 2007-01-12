package com.cannontech.sensus;

import java.util.Date;

public interface PointValueUpdater {

    public void writePointDataMessage(int repId, double value, Date time);

    public void writePointDataMessage(int repId, boolean value, Date time);

}