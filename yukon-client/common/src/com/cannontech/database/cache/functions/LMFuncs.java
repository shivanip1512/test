package com.cannontech.database.cache.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Insert the type's description here.
 * Creation date: (3/26/2001 9:40:33 AM)
 * @author: 
 */
public final class LMFuncs
{
	/**
	 * LMFuncs constructor comment.
	 */
	private LMFuncs() {
		super();
	}
	
	public static LiteYukonPAObject[] getAllLMScenarios()
	{
		//Get an instance of the cache.
		DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
		ArrayList scenarioList = new ArrayList(32);
		synchronized(cache)
		{
			List lmScenarios = cache.getAllLMScenarios();
			Collections.sort( lmScenarios, LiteComparators.liteStringComparator );
		
			for (int i = 0; i < lmScenarios.size(); i++)
			{
				LiteYukonPAObject litePao = (LiteYukonPAObject)lmScenarios.get(i);
				scenarioList.add(litePao);	
			}
		}
		
		LiteYukonPAObject retVal[] = new LiteYukonPAObject[scenarioList.size()];
		scenarioList.toArray( retVal );
		return retVal;
	}

}
