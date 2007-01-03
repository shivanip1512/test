package com.cannontech.core.dao.impl;

import java.util.Arrays;
import java.util.Collection;
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

public final class StateDaoImpl implements StateDao 
{
    private IDatabaseCache databaseCache;
    
	/**
	 * StateFuncs constructor comment.
	 */
	public StateDaoImpl() 
	{
		super();
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.StateDao#getLiteState(int, int)
     */
	public LiteState getLiteState(int stateGroupID, int rawState) 
	{
		LiteStateGroup stateGroup = getLiteStateGroup( stateGroupID );
		for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
		{
			if( rawState == ((LiteState)stateGroup.getStatesList().get(j)).getStateRawState() )
				return (LiteState)stateGroup.getStatesList().get(j);
		}
	
		//this is a internal error
		CTILogger.error("Unable to find the state for StateGroupID=" + stateGroupID +
			" and rawState=" + rawState );

		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.StateDao#getLiteStateGroup(int)
     */
	public LiteStateGroup getLiteStateGroup(int stateGroupID) 
	{
		return (LiteStateGroup)
			databaseCache.getAllStateGroupMap().get( new Integer(stateGroupID) );
	}

    @SuppressWarnings("unchecked")
    public LiteStateGroup getLiteStateGroup(String stateGroupName) 
	{
	    Map allStateGroupMap = databaseCache.getAllStateGroupMap();
        Collection<LiteStateGroup> stateGroups = allStateGroupMap.values();
        for(LiteStateGroup group : stateGroups){
            if(stateGroupName.equals(group.getStateGroupName())){
                return group;
            }
        }
        
        throw new NotFoundException("State group '" + stateGroupName + "' doesn't exist");
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.StateDao#getLiteStates(int)
     */
	public LiteState[] getLiteStates(int stateGroupID)
	{
		LiteStateGroup lsg = null;
		lsg = getLiteStateGroup(stateGroupID);
		
		LiteState[] ls = new LiteState[lsg.getStatesList().size()];
		lsg.getStatesList().toArray(ls);
		return ls;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.StateDao#getAllStateGroups()
     */
	public LiteStateGroup[] getAllStateGroups()
	{
		LiteStateGroup[] stateGroups = null;

		synchronized(databaseCache)
		{			
			stateGroups =
				(LiteStateGroup[])databaseCache.getAllStateGroupMap().values().toArray(
					new LiteStateGroup[databaseCache.getAllStateGroupMap().values().size()] );

			Arrays.sort( stateGroups, LiteComparators.liteStringComparator );
		}
		
		return stateGroups;
	}

    public void setDatabaseCache(IDatabaseCache databaseCache) {
        this.databaseCache = databaseCache;
    }
}
