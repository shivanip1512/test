package com.cannontech.sensus;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;

public class NullPointValueUpdater implements PointValueUpdater {
    private Logger log = YukonLogManager.getLogger(NullPointValueUpdater.class);

    public void writePointDataMessage(int repId, double value, Date time) {
        log.debug("Null write with repId=" + repId + ", value=" + value + ", time=" + time);
    }

    public void writePointDataMessage(int repId, boolean value, Date time) {
        writePointDataMessage(repId, value ? 1 : 0, time);
    }

}
