package com.cannontech.core.dynamic;

import java.util.List;

import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;

public interface DynamicDataSource {
    
    public void putValue(PointData pointData);
    public void putValue(int pointId, double value);
    
    public PointData getPointData(int pointId);
    public List<PointData> getPointData(List<Integer> pointIds);
    
    public List<Signal> getSignals(int pointId);
    public List<Signal> getSignalsByCategory(int pointId, int alarmCategoryId);
    
    public Integer getTags(int pointId);
    public List<Integer> getTags(List<Integer> pointIds);
}
