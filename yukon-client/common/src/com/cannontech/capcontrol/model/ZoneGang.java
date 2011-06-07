package com.cannontech.capcontrol.model;

import com.cannontech.database.data.pao.ZoneType;

public class ZoneGang extends AbstractZoneNotThreePhase {

    public ZoneGang() {
        super();
    }

    public ZoneGang(Zone zone) {
        super(zone);
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.GANG_OPERATED;
    }
}
