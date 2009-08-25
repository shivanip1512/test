package com.cannontech.dr.loadgroup.dao;

import java.util.List;

import com.cannontech.common.device.model.DisplayableDevice;

public interface LoadGroupDao {
    public List<DisplayableDevice> getLoadGroupsForProgram(int programId);
}
