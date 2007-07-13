package com.cannontech.sensus;

import java.util.Date;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class NullPointValueUpdater implements PointValueUpdater {
    private Logger log = YukonLogManager.getLogger(NullPointValueUpdater.class);

    public void writePointDataMessage(LiteYukonPAObject lpao, double value, Date time) {
        log.debug("Null write with pao =" + lpao.getPaoName() + ", value=" + value + ", time=" + time);
    }

    public void writePointDataMessage(LiteYukonPAObject lpao, boolean value, Date time) {
        writePointDataMessage(lpao, value ? 1 : 0, time);
    }

}
