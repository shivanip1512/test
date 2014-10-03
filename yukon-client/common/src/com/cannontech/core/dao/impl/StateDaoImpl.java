package com.cannontech.core.dao.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.yukon.IDatabaseCache;

public final class StateDaoImpl implements StateDao {
    
    private static final Logger log = YukonLogManager.getLogger(StateDaoImpl.class);
    
    @Autowired private IDatabaseCache databaseCache;
    
    @Override
    public LiteState findLiteState(int stateGroupId, int rawState) {
        
        LiteStateGroup stateGroup = getLiteStateGroup(stateGroupId);
        
        List<LiteState> stateList = stateGroup.getStatesList();
        for (final LiteState state : stateList) {
            if (rawState == state.getStateRawState()) return state;
        }
        
        //this is a internal error
        log.debug("Unable to find the state for StateGroupID = " + stateGroupId +
                        " and rawState = " + rawState );
        
        return null;
    }
    
    @Override
    public LiteStateGroup getLiteStateGroup(int stateGroupID) {
        synchronized (databaseCache) {
            LiteStateGroup liteStateGroup = databaseCache.getAllStateGroups().get(stateGroupID);
            return liteStateGroup;
        }
    }
    
    @Override
    public LiteStateGroup getLiteStateGroup(String stateGroupName) {
        
        synchronized (databaseCache) {
            Map<Integer, LiteStateGroup> allStateGroupMap = databaseCache.getAllStateGroups();
            Collection<LiteStateGroup> stateGroups = allStateGroupMap.values();
            for(LiteStateGroup group : stateGroups){
                if(stateGroupName.equals(group.getStateGroupName())){
                    return group;
                }
            }
        }
        
        throw new NotFoundException("State group '" + stateGroupName + "' doesn't exist");
    }
    
    @Override
    public LiteState[] getLiteStates(int stateGroupId) {
        
        LiteStateGroup lsg = null;
        lsg = getLiteStateGroup(stateGroupId);
        
        LiteState[] ls = new LiteState[lsg.getStatesList().size()];
        lsg.getStatesList().toArray(ls);
        
        return ls;
    }
    
    @Override
    public LiteStateGroup[] getAllStateGroups() {
        
        LiteStateGroup[] stateGroups = null;
        
        synchronized (databaseCache) {
            Collection<LiteStateGroup> values = databaseCache.getAllStateGroups().values();
            stateGroups = values.toArray(new LiteStateGroup[values.size()]);
            Arrays.sort(stateGroups, LiteComparators.liteStringComparator);
        }
        
        return stateGroups;
    }
    
}