package com.cannontech.stars.util.filter.filterBy;

import java.util.Collection;
import java.util.List;

import org.springframework.jdbc.core.SqlProvider;

import com.cannontech.stars.util.filter.JoinTable;

public interface FilterBy extends SqlProvider {
    
    public Collection<JoinTable> getJoinTables();
    
    public List<Object> getParameterValues();
    
}
