package com.cannontech.dr.loadgroup.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;

public interface LoadGroupDao {
    public List<DisplayablePao> getLoadGroupsForProgram(int programId);
    public List<DisplayablePao> getLoadGroups();
    public DisplayablePao getLoadGroup(int loadGroupId);
}
