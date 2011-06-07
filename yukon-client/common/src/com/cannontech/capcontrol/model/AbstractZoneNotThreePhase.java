package com.cannontech.capcontrol.model;

public abstract class AbstractZoneNotThreePhase extends AbstractZone {
    private ZoneRegulator regulator;

    public AbstractZoneNotThreePhase() {
        super();
    }

    public AbstractZoneNotThreePhase(Zone zone) {
        super(zone);
        this.regulator = zone.getRegulators().get(0);
    }

    public ZoneRegulator getRegulator() {
        return regulator;
    }

    public void setRegulator(ZoneRegulator regulator) {
        this.regulator = regulator;
    }
}
