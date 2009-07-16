package com.cannontech.loadcontrol.loadgroup.dao;

import com.cannontech.loadcontrol.loadgroup.model.LoadGroup;


public interface LoadGroupDao {

    LoadGroup getById(int loadGroupId);

    LoadGroup getByLoadGroupName(String loadGroupName);
    
    boolean isLoadGroupInUse(int loadGroupId);

}