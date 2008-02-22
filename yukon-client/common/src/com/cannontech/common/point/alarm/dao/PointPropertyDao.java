package com.cannontech.common.point.alarm.dao;

import java.util.List;

import com.cannontech.common.point.alarm.model.PointProperty;



public interface PointPropertyDao {
    public boolean add( PointProperty attrib );
    
    public boolean removeByPointId( int id );
    
    public boolean remove( PointProperty attrib );
    
    public boolean update( PointProperty attrib );
    
    public List<PointProperty> getById( int id );
    
    public PointProperty getByIdAndPropertyId(int id, int attributeId);
}
