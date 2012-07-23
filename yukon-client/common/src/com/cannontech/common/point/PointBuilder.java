package com.cannontech.common.point;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.point.alarm.dao.PointPropertyValueDao;
import com.cannontech.common.point.alarm.model.PointPropertyValue;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;

/**
 * Abstract base class containing functionality shared by all point builders.
 */
public abstract class PointBuilder {
    private PointPropertyValueDao pointPropertyValueDao;
    protected String pointName;
    protected int paoId;
    protected int pointId;
    protected boolean isDisabled;
    
    protected Integer staleDataTime = null;
    protected Integer staleDataUpdate = null;
    
    protected PointBuilder(int paoId, int pointId, String pointName, boolean isDisabled, PointPropertyValueDao pointPropertyValueDao) {
        if(paoId < 1) throw new IllegalArgumentException("Pao Id must be greater than 0.");
        if(pointId < 1) throw new IllegalArgumentException("Point Id must be greater than 0.");
        if(StringUtils.isEmpty(pointName)) throw new IllegalArgumentException("Point name cannot be empty.");
        this.paoId = paoId;
        this.pointId = pointId;
        this.pointName = pointName;
        this.isDisabled = isDisabled;
        this.pointPropertyValueDao = pointPropertyValueDao;
    }
    
    public abstract PointBase build();
    
    protected PointBase insert() {
        //build and insert point
        PointBase point = this.build();
        PointFactory.addPoint(point);
        
        //insert stale data properties
        if(staleDataTime != null && staleDataUpdate != null) {
            PointPropertyValue timeValue = new PointPropertyValue();
            PointPropertyValue updateValue = new PointPropertyValue();
            timeValue.setPointId(point.getPoint().getPointID());
            timeValue.setPointPropertyCode(1);
            timeValue.setFloatValue(staleDataTime.floatValue());
            updateValue.setPointId(point.getPoint().getPointID());
            updateValue.setPointPropertyCode(2);
            updateValue.setFloatValue(staleDataUpdate);
            
            //remove old values
            pointPropertyValueDao.remove(timeValue);
            pointPropertyValueDao.remove(updateValue);
            
            //add new values
            pointPropertyValueDao.add(timeValue);
            pointPropertyValueDao.add(updateValue);
        }
        
        return point;
    }
    
    public void setStaleDataTime(int staleDataTime) {
        this.staleDataTime = staleDataTime;
    }
    
    public void setStaleDataUpdate(int staleDataUpdate) {
        this.staleDataUpdate = staleDataUpdate;
    }
}
