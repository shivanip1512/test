package com.cannontech.core.dao;

import com.cannontech.common.model.PaoProperty;

public interface PaoPropertyDao {
    public boolean add( PaoProperty property );
    
    public boolean remove( PaoProperty property );
    
    public boolean update( PaoProperty property );
    
    public PaoProperty getById( int id );
    
    public PaoProperty getByIdAndName(int id, String name);
}
