package com.cannontech.web.capcontrol.ivvc.models;

import java.util.List;

import com.google.common.collect.Lists;

public class ZoneDto {
    
    private String name;
    private int zoneId = -1;
    private int parentZoneId = -1;
    private int substationBusId = -1;
    private int regulatorId = -1;
    private List<Integer> bankIds = Lists.newArrayList();
    private List<Integer> pointIds = Lists.newArrayList();

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParentZoneId() {
        return parentZoneId;
    }

    public void setParentZoneId(int parentZoneId) {
        this.parentZoneId = parentZoneId;
    }

    public int getSubstationBusId() {
        return substationBusId;
    }

    public void setSubstationBusId(int substationBusId) {
        this.substationBusId = substationBusId;
    }

    public int getRegulatorId() {
        return regulatorId;
    }

    public void setRegulatorId(int regulatorId) {
        this.regulatorId = regulatorId;
    }

    public List<Integer> getBankIds() {
        return bankIds;
    }

    public void setBankIds(List<Integer> bankIds) {
        this.bankIds = bankIds;
    }

    public List<Integer> getPointIds() {
        return pointIds;
    }

    public void setPointIds(List<Integer> pointIds) {
        this.pointIds = pointIds;
    }
}
