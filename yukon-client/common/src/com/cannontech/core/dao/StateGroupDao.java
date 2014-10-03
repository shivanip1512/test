package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteStateGroup;

public interface StateGroupDao {

    /** Retrieves all {@link LiteStateGroup}s from the database. */
    List<LiteStateGroup> getAllStateGroups();

    /** Retrieves a {@link LiteStateGroup} from the database. */
    LiteStateGroup getStateGroup(int stateGroupId);
    
}