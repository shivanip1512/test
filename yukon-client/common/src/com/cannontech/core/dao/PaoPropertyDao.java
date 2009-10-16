package com.cannontech.core.dao;

import com.cannontech.common.model.PaoProperty;
import com.cannontech.common.model.PaoPropertyName;

public interface PaoPropertyDao {
    public boolean add(PaoProperty property);
    
    public boolean remove(PaoProperty property);
    
    public boolean removeAll(int id);
    
    public boolean update(PaoProperty property);
    
    public PaoProperty getByIdAndName(int id, PaoPropertyName propertyName);
}
