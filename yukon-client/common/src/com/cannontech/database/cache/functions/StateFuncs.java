package com.cannontech.database.cache.functions;

import java.util.Arrays;
import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:46:50 AM)
 * @author: 
 */

public final class StateFuncs 
{
	/**
	 * StateFuncs constructor comment.
	 */
	private StateFuncs() 
	{
		super();
	}

	/**
	 * Returns the state for the given values. We could index the array with the value
	 * or rawstate for an instant lookup. However, if the ordering ever changes this would
	 * then not work.
	 * 
	 * @return com.cannontech.database.data.lite.LiteState
	 * @param stateGroupID int
	 * @param rawState int
	 */
	public static LiteState getLiteState(int stateGroupID, int rawState) 
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
	
	public static LiteStateGroup getLiteStateGroup(int stateGroupID) 
	{
		return (LiteStateGroup)
			DefaultDatabaseCache.getInstance().getAllStateGroupMap().get( new Integer(stateGroupID) );
	}
	
	public static LiteState[] getLiteStates(int stateGroupID)
	{
		LiteStateGroup lsg = null;
		lsg = getLiteStateGroup(stateGroupID);
		
		LiteState[] ls = new LiteState[lsg.getStatesList().size()];
		lsg.getStatesList().toArray(ls);
		return ls;
	}

	/**
	 * Retrieves all the StateGroups in the system. Allocates a new array for storage.
	 * @return
	 */
	public static LiteStateGroup[] getAllStateGroups()
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		LiteStateGroup[] stateGroups = null;

		synchronized(cache)
		{			
			stateGroups =
				(LiteStateGroup[])cache.getAllStateGroupMap().values().toArray(
					new LiteStateGroup[cache.getAllStateGroupMap().values().size()] );

			Arrays.sort( stateGroups, LiteComparators.liteStringComparator );
		}
		
		return stateGroups;
	}

}
