package com.cannontech.common.device.model;

import java.util.List;

import com.cannontech.database.data.point.PointType;

public class DevicePointsFilter {

    private List<PointType> types;
    private List<String> pointNames;

    public DevicePointsFilter(List<PointType> types, List<String> pointNames) {
        this.types = types;
        this.pointNames = pointNames;
    }

    public List<PointType> getTypes() {
        return types;
    }

    public void setTypes(List<PointType> types) {
        this.types = types;
    }

    public List<String> getPointNames() {
        return pointNames;
    }

    public void setPointNames(List<String> pointNames) {
        this.pointNames = pointNames;
    }

}
