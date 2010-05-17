package com.cannontech.dr.loadgroup.dao;

import java.util.List;

import com.cannontech.dr.model.ControllablePao;

public interface LoadGroupDao {
    public ControllablePao getLoadGroup(int loadGroupId);

    public List<ControllablePao> getForProgram(int programId);
}
