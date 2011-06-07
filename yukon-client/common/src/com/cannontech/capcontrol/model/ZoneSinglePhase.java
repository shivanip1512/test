package com.cannontech.capcontrol.model;

import com.cannontech.database.data.pao.ZoneType;

public class ZoneSinglePhase extends AbstractZoneNotThreePhase {

    public ZoneSinglePhase() {
        super();
    }

    public ZoneSinglePhase(Zone zone) {
        super(zone);
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.SINGLE_PHASE;
    }
}
