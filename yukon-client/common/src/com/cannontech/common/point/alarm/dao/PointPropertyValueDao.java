package com.cannontech.common.point.alarm.dao;

import java.util.List;

import com.cannontech.common.point.alarm.model.PointPropertyValue;



public interface PointPropertyValueDao {
    public boolean add( PointPropertyValue attrib );
    
    public boolean removeByPointId( int id );
    
    public boolean remove( PointPropertyValue attrib );
    
    public boolean update( PointPropertyValue attrib );
    
    public List<PointPropertyValue> getById( int id );
    
    public PointPropertyValue getByIdAndPropertyId(int id, int attributeId);
}
