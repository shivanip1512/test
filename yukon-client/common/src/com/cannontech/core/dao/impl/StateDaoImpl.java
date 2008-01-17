package com.cannontech.core.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:46:50 AM)
 * @author: 
 */

public final class StateDaoImpl implements StateDao {
    private IDatabaseCache databaseCache;

    public StateDaoImpl() {

    }

    public LiteState getLiteState(int stateGroupID, int rawState) {
        LiteStateGroup stateGroup = getLiteStateGroup( stateGroupID );

        List<LiteState> stateList = stateGroup.getStatesList();
        for (final LiteState state : stateList) {
            if (rawState == state.getStateRawState()) return state;
        }

        //this is a internal error
        CTILogger.error("Unable to find the state for StateGroupID=" + stateGroupID +
                        " and rawState=" + rawState );

        return null;
    }

    public LiteStateGroup getLiteStateGroup(int stateGroupID) {
        synchronized (databaseCache) {
            LiteStateGroup liteStateGroup = databaseCache.getAllStateGroupMap().get(stateGroupID);
            return liteStateGroup;
        }
    }

    public LiteStateGroup getLiteStateGroup(String stateGroupName) {
        synchronized (databaseCache) {
            Map<Integer,LiteStateGroup> allStateGroupMap = databaseCache.getAllStateGroupMap();
            Collection<LiteStateGroup> stateGroups = allStateGroupMap.values();
            for(LiteStateGroup group : stateGroups){
                if(stateGroupName.equals(group.getStateGroupName())){
                    return group;
                }
            }
        }
        throw new NotFoundException("State group '" + stateGroupName + "' doesn't exist");
    }

    public LiteState[] getLiteStates(int stateGroupID) {
        LiteStateGroup lsg = null;
        lsg = getLiteStateGroup(stateGroupID);

        LiteState[] ls = new LiteState[lsg.getStatesList().size()];
        lsg.getStatesList().toArray(ls);
        return ls;
    }

    public LiteStateGroup[] getAllStateGroups() {
        LiteStateGroup[] stateGroups = null;

        synchronized (databaseCache) {
            Collection<LiteStateGroup> values = databaseCache.getAllStateGroupMap().values();
            stateGroups = values.toArray(new LiteStateGroup[values.size()]);
            Arrays.sort(stateGroups, LiteComparators.liteStringComparator);
        }

        return stateGroups;
    }

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
