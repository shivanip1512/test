package com.cannontech.capcontrol.model;

import java.util.Map;

import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.google.common.collect.Maps;

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
        Map<Phase, RegulatorToZoneMapping> regulatorMap = Maps.newHashMapWithExpectedSize(1);
        regulatorMap.put(Phase.ALL, regulator);
        return regulatorMap;
    }
}
