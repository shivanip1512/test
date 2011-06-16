package com.cannontech.capcontrol.model;

import java.util.List;
import java.util.Map;

import com.cannontech.common.util.LazyLinkedHashMap;
import com.cannontech.enums.Phase;
import com.google.common.collect.Lists;

public abstract class AbstractZoneThreePhase extends AbstractZone {
    private Map<Phase, RegulatorToZoneMapping> regulators = 
        new LazyLinkedHashMap<Phase, RegulatorToZoneMapping>(Phase.class, RegulatorToZoneMapping.class);
    
    public AbstractZoneThreePhase() {
        super();
    }

    public AbstractZoneThreePhase(Zone zone) {
        super(zone);
        for (RegulatorToZoneMapping regulator : zone.getRegulators()) {
            Phase phase = regulator.getPhase();
            regulators.put(phase, regulator);
        }
    }

    public Map<Phase, RegulatorToZoneMapping> getRegulators() {
        return regulators;
    }

    public void setRegulators(Map<Phase, RegulatorToZoneMapping> regulators) {
        this.regulators = regulators;
    }
    
    public List<RegulatorToZoneMapping> getRegulatorsList() {
        return Lists.newArrayList(regulators.values());
    }
}
