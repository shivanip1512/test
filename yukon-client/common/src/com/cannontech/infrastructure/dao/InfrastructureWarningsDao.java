package com.cannontech.infrastructure.dao;

import java.util.Collection;
import java.util.List;

import com.cannontech.infrastructure.model.InfrastructureWarning;

/**
 * Dao for saving and retrieving infrastructure warnings.
 */
public interface InfrastructureWarningsDao {
    
    /**
     * Replace the previously saved infrastructure warnings with new warnings.
     */
    public void insert(Collection<InfrastructureWarning> warnings);
    
    /**
     * Retrieve the most recently calculated set of infrastructure warnings.
     */
    public List<InfrastructureWarning> getWarnings();
    
}
