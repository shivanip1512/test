package com.cannontech.sensus;

import java.util.Date;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public interface PointValueUpdater {

    public void writePointDataMessage(LiteYukonPAObject lpao, double value, Date time);
    public void writePointDataMessage(LiteYukonPAObject lpao, boolean value, Date time);
}