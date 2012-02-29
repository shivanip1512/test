package com.cannontech.capcontrol.model;

import java.util.List;

import com.cannontech.common.util.LazyList;

public class ZoneVoltagePointsHolder {

    private int zoneId;
    private String zoneName;
    private List<VoltageLimitedDeviceInfo> points = LazyList
        .ofInstance(VoltageLimitedDeviceInfo.class);

    public ZoneVoltagePointsHolder() {
        super();
    }

    public ZoneVoltagePointsHolder(int zoneId, List<VoltageLimitedDeviceInfo> points) {
        this.zoneId = zoneId;
        this.points = points;
    }

    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public List<VoltageLimitedDeviceInfo> getPoints() {
        return points;
    }

    public void setPoints(List<VoltageLimitedDeviceInfo> points) {
        this.points = points;
    }
}
