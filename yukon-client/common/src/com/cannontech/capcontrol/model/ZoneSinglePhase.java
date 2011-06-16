package com.cannontech.capcontrol.model;

import java.util.Map;

import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.google.common.collect.Maps;

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
    
    @Override
    public Map<Phase, RegulatorToZoneMapping> getRegulators() {
        Map<Phase, RegulatorToZoneMapping> regulatorMap = Maps.newHashMapWithExpectedSize(1);
        regulatorMap.put(regulator.getPhase(), regulator);
        return regulatorMap;
    }
}
