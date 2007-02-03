package com.cannontech.cc.dao;

import java.util.Set;

import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteNotificationGroup;

public interface ProgramNotificationGroupDao {
    public Set<LiteNotificationGroup> getNotificationGroupsForProgram(final Program program);
    public void setNotificationGroupsForProgram(Program program, Set<LiteNotificationGroup> notificationGroups);
    public void deleteForProgram(Program object);
}
