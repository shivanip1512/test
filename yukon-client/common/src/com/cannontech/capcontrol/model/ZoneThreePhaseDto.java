package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.enums.Phase;
import com.google.common.collect.Lists;

public class ZoneThreePhaseDto extends ZoneDto {

    private ZoneRegulator regulatorA;
    private ZoneRegulator regulatorB;
    private ZoneRegulator regulatorC;

    public ZoneThreePhaseDto() {
    }

    public ZoneThreePhaseDto(Zone zone) {
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

    @Override
    public ZoneRegulator getRegulatorA() {
        return regulatorA;
    }

    @Override
    public void setRegulatorA(ZoneRegulator regulatorA) {
        this.regulatorA = regulatorA;
    }

    @Override
    public ZoneRegulator getRegulatorB() {
        return regulatorB;
    }

    @Override
    public void setRegulatorB(ZoneRegulator regulatorB) {
        this.regulatorB = regulatorB;
    }

    @Override
    public ZoneRegulator getRegulatorC() {
        return regulatorC;
    }

    @Override
    public void setRegulatorC(ZoneRegulator regulatorC) {
        this.regulatorC = regulatorC;
    }

    @Override
    public List<ZoneRegulator> getRegulators() {
        return Lists.newArrayList(regulatorA, regulatorB, regulatorC);
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.THREE_PHASE;
    }

    @Override
    public ZoneRegulator getRegulator() {
        return null;
    }

    @Override
    public void setRegulator(ZoneRegulator regulator) {
    }

    @Override
    public void setRegulators(List<ZoneRegulator> regulators) {
    }
}
