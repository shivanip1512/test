package com.cannontech.capcontrol.model;

import java.util.Map;

import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.google.common.collect.ImmutableMap;

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
    
    @Override
    public Map<Phase, RegulatorToZoneMapping> getRegulators() {
        Map<Phase, RegulatorToZoneMapping> regulatorMap = ImmutableMap.of(Phase.ALL, regulator);
        return regulatorMap;
    }
}
