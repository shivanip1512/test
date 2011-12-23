package com.cannontech.core.dao;

import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;

public interface StateDao {

    /**
     * Returns the state for the given values. We could index the array with the value
     * or rawstate for an instant lookup. However, if the ordering ever changes this would
     * then not work.
     * Returns null when liteState is not found for rawState
     * @return com.cannontech.database.data.lite.LiteState
     * @param stateGroupID int
     * @param rawState int
     */
    public LiteState findLiteState(int stateGroupID, int rawState);

    public LiteStateGroup getLiteStateGroup(int stateGroupID);

    /**
     * Method to get a state group by name
     * @param stateGroupName - Name of state group
     * @return State group
     */
    public LiteStateGroup getLiteStateGroup(String stateGroupName);

    public LiteState[] getLiteStates(int stateGroupID);

    /**
     * Retrieves all the StateGroups in the system. Allocates a new array for storage.
     * @return
     */
    public LiteStateGroup[] getAllStateGroups();

}