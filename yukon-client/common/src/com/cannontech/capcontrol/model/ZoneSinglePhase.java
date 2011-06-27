package com.cannontech.capcontrol.model;

import java.util.Map;

import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.google.common.collect.ImmutableMap;

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
        Map<Phase, RegulatorToZoneMapping> regulatorMap = ImmutableMap.of(regulator.getPhase(), regulator);
        return regulatorMap;
    }
}
