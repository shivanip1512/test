package com.cannontech.capcontrol.model;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public abstract class AbstractZoneNotThreePhase extends AbstractZone {
    protected RegulatorToZoneMapping regulator;

    public AbstractZoneNotThreePhase() {
        super();
    }

    public AbstractZoneNotThreePhase(Zone zone) {
        super(zone);
        this.regulator = Iterables.getOnlyElement(zone.getRegulators());
    }

    public RegulatorToZoneMapping getRegulator() {
        return regulator;
    }

    public void setRegulator(RegulatorToZoneMapping regulator) {
        this.regulator = regulator;
    }
    
    public List<RegulatorToZoneMapping> getRegulatorsList() {
        return Lists.newArrayList(Collections.singleton(regulator));
    }
}
