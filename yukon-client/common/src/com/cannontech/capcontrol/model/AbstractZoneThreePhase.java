package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.enums.Phase;
import com.google.common.collect.Lists;

public abstract class AbstractZoneThreePhase extends AbstractZone {
    private ZoneRegulator regulatorA;
    private ZoneRegulator regulatorB;
    private ZoneRegulator regulatorC;
    
    public AbstractZoneThreePhase() {
        super();
    }

    public AbstractZoneThreePhase(Zone zone) {
        super(zone);
        for (ZoneRegulator zoneRegulator : zone.getRegulators()) {
            if (zoneRegulator.getPhase() == Phase.A) {
                this.regulatorA = zoneRegulator;
            } else if (zoneRegulator.getPhase() == Phase.B) {
                this.regulatorB = zoneRegulator;
            } else if (zoneRegulator.getPhase() == Phase.C) {
                regulatorC = zoneRegulator;
            }
        }
    }

    public ZoneRegulator getRegulatorA() {
        return regulatorA;
    }

    public void setRegulatorA(ZoneRegulator regulatorA) {
        this.regulatorA = regulatorA;
    }

    public ZoneRegulator getRegulatorB() {
        return regulatorB;
    }

    public void setRegulatorB(ZoneRegulator regulatorB) {
        this.regulatorB = regulatorB;
    }

    public ZoneRegulator getRegulatorC() {
        return regulatorC;
    }

    public void setRegulatorC(ZoneRegulator regulatorC) {
        this.regulatorC = regulatorC;
    }

    public List<ZoneRegulator> getRegulators() {
        return Lists.newArrayList(regulatorA, regulatorB, regulatorC);
    }
}
