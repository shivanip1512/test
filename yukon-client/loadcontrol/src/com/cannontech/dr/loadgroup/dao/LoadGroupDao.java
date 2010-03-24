package com.cannontech.dr.loadgroup.dao;

import java.util.List;

import com.cannontech.common.pao.DisplayablePao;

public interface LoadGroupDao {
    public DisplayablePao getLoadGroup(int loadGroupId);

    public List<DisplayablePao> getForProgram(int programId);
}
