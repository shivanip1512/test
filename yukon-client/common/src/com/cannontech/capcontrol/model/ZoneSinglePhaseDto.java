package com.cannontech.capcontrol.model;

import java.util.Collections;
import java.util.List;

import com.cannontech.database.data.pao.ZoneType;
import com.google.common.collect.Lists;

public class ZoneSinglePhaseDto extends ZoneDto {

    private ZoneRegulator regulator;

    public ZoneSinglePhaseDto() {
    }

    public ZoneSinglePhaseDto(Zone zone) {
        super(zone);
        this.regulator = zone.getRegulators().get(0);
    }

    @Override
    public ZoneRegulator getRegulator() {
        return regulator;
    }

    @Override
    public void setRegulator(ZoneRegulator regulator) {
        this.regulator = regulator;
    }

    @Override
    public List<ZoneRegulator> getRegulators() {
        return Lists.newArrayList(Collections.singleton(regulator));
    }

    @Override
    public ZoneType getZoneType() {
        return ZoneType.SINGLE_PHASE;
    }

    @Override
    public void setRegulators(List<ZoneRegulator> regulators) {
    }

    @Override
    public ZoneRegulator getRegulatorA() {
        return null;
    }

    @Override
    public void setRegulatorA(ZoneRegulator regulatorA) {
    }

    @Override
    public ZoneRegulator getRegulatorB() {
        return null;
    }

    @Override
    public void setRegulatorB(ZoneRegulator regulatorB) {
    }

    @Override
    public ZoneRegulator getRegulatorC() {
        return null;
    }

    @Override
    public void setRegulatorC(ZoneRegulator regulatorC) {
    }
}
