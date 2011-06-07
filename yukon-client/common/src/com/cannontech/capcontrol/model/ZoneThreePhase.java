package com.cannontech.capcontrol.model;

import com.cannontech.database.data.pao.ZoneType;

public class ZoneThreePhase extends AbstractZoneThreePhase {

    public ZoneThreePhase() {
        super();
    }

    public ZoneThreePhase(Zone zone) {
        super(zone);
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.THREE_PHASE;
    }
}
