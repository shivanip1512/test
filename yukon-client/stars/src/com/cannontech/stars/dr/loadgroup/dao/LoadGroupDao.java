package com.cannontech.stars.dr.loadgroup.dao;

import com.cannontech.stars.dr.loadgroup.model.LoadGroup;

public interface LoadGroupDao {

    LoadGroup getById(int loadGroupId);

    LoadGroup getByLoadGroupName(String loadGroupName);

}