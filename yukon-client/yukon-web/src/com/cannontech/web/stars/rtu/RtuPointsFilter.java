package com.cannontech.web.stars.rtu;

import java.util.List;

import com.cannontech.database.data.point.PointType;

public class RtuPointsFilter {
    
    private List<PointType> types;
    private List<Integer> deviceIds;
    private List<String> pointNames;
    
    public List<PointType> getTypes() {
        return types;
    }
    public void setTypes(List<PointType> types) {
        this.types = types;
    }
    public List<Integer> getDeviceIds() {
        return deviceIds;
    }
    public void setDeviceIds(List<Integer> deviceIds) {
        this.deviceIds = deviceIds;
    }
    public List<String> getPointNames() {
        return pointNames;
    }
    public void setPointNames(List<String> pointNames) {
        this.pointNames = pointNames;
    }
    

}
