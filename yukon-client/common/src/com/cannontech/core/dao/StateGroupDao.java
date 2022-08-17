package com.cannontech.core.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;

public interface StateGroupDao {

    /**
     * Retrieves all {@link LiteStateGroup}s from cache. Loads from database if not found in cache.
     */
    List<LiteStateGroup> getAllStateGroups();

    /**
     * Retrieves a {@link LiteStateGroup} from cache. Loads from database if not found in cache
     */
    LiteStateGroup getStateGroup(int stateGroupId);

    /**
     * Retrieves a {@link LiteStateGroup} from cache by the state group's name. Loads from database if not found in cache
     */
     LiteStateGroup getStateGroup(String stateGroupName);

     /**
      * Retrieves all {@link LiteState}s for a stateGroup from cache. Loads from database if not found in cache
      */

    List<LiteState> getLiteStates(int stateGroupId);

    /**
     * Returns the state for the given values. We could index the array with the value or rawstate for an instant
     * lookup. However, if the ordering ever changes this would then not work. Returns null when liteState is not found
     * for rawState
     */
    LiteState findLiteState(int stateGroupId, int rawState);

    /**
     * Retrieves the list of Raw State for the specified pointId.
     */
    List<LiteState> getStateList(Integer pointId);

    /**
     * Retrieves the name of Raw State for the specified pointId.
     */
    String getRawStateName(Integer pointId, Integer rawState);

}