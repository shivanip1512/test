package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
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
	 * Insert the method's description here.
	 * Creation date: (3/26/2001 9:47:28 AM)
	 * @return com.cannontech.database.data.lite.LiteState
	 * @param stateGroupID int
	 * @param rawState int
	 */
	public static LiteState getLiteState(int stateGroupID, int rawState) 
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			java.util.List allStateGroups = cache.getAllStateGroups();
			java.util.Collections.sort( allStateGroups, com.cannontech.database.data.lite.LiteComparators.liteBaseIDComparator);
			
			for(int i = 0; i < allStateGroups.size(); i++ )
			{
				LiteStateGroup stateGroup = (LiteStateGroup)allStateGroups.get(i);
	
				if( stateGroup.getStateGroupID() == stateGroupID )
				{
					for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
					{
						if( rawState == ((LiteState)stateGroup.getStatesList().get(j)).getStateRawState() )
							return (LiteState)stateGroup.getStatesList().get(j);
					}
				}
	
			}
		}
	
		//this is a internal error
		CTILogger.error("Unable to find the state for StateGroupID=" + stateGroupID +
			" and rawState=" + rawState );

		return null;
	}
	
	public static LiteStateGroup getLiteStateGroup(int stateGroupID) 
	{
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized(cache) 
		{
			for(Iterator i = cache.getAllStateGroups().iterator(); i.hasNext(); ) 
			{
				LiteStateGroup lsg = (LiteStateGroup) i.next();
				if(lsg.getStateGroupID() == stateGroupID)
				{
					return lsg;		
				}
			}
		}
		
		return null;
	}
	
	public static LiteState[] getLiteStates(int stateGroupID) {
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		LiteStateGroup lsg = null;
		synchronized(cache) 
		{
			lsg = getLiteStateGroup(stateGroupID);
		}
		
		LiteState[] ls = new LiteState[lsg.getStatesList().size()];
		lsg.getStatesList().toArray(ls);
		return ls;
	}
}
