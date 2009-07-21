package com.cannontech.loadcontrol.loadgroup.dao;

import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;


public interface LoadGroupDao {

    public LoadGroup getById(int loadGroupId);

    public LoadGroup getByLoadGroupName(String loadGroupName);
    
    /**
     * This method returns false when the supplied load group
     * is apart of an active enrollment.  
     * 
     * @param loadGroupId
     * @return
     */
    public boolean isLoadGroupInUse(int loadGroupId);

}